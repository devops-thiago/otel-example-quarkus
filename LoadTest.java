///usr/bin/env java "$0" "$@"; exit $?

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

void main(String... args) throws Exception {
    final String BASE = "http://localhost:8080/api/users";
    final var running = new AtomicBoolean(true);

    var client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    var firstNames = List.of("Alice","Bob","Carlos","Diana","Eve","Frank","Grace",
            "Hector","Iris","Jack","Karen","Leo","Mia","Nate","Olivia","Paul","Quinn","Rita","Sam","Tina");
    var lastNames = List.of("Silva","Santos","Oliveira","Souza","Lima","Pereira","Costa","Ferreira",
            "Rodrigues","Almeida","Nascimento","Carvalho");
    var bios = List.of("Backend engineer","Frontend dev","DevOps specialist","Data scientist",
            "QA engineer","Tech lead","Intern","Product manager","SRE","Architect");

    var rng = ThreadLocalRandom.current();
    var ids = new CopyOnWriteArrayList<Long>();
    var emails = new CopyOnWriteArrayList<String>();
    var seq = new AtomicInteger(0);

    // counters per operation
    var counters = new ConcurrentHashMap<String, AtomicInteger>();
    // counters per status class
    var statusCounters = new ConcurrentHashMap<String, AtomicInteger>();

    Runnable incStatus = () -> {};
    java.util.function.Consumer<Integer> trackStatus = code -> {
        String cls = switch (code / 100) {
            case 2 -> "2xx";
            case 4 -> "4xx";
            case 5 -> "5xx";
            default -> code + "";
        };
        statusCounters.computeIfAbsent(cls, _ -> new AtomicInteger()).incrementAndGet();
    };

    java.util.function.Supplier<String> nextEmail = () ->
            "u" + seq.incrementAndGet() + "r" + rng.nextInt(100000) + "@test.com";
    java.util.function.Supplier<String> randName = () ->
            firstNames.get(rng.nextInt(firstNames.size())) + " " + lastNames.get(rng.nextInt(lastNames.size()));
    java.util.function.Supplier<String> randBio = () ->
            bios.get(rng.nextInt(bios.size()));

    // helper to send request and get status code
    java.util.function.ToIntFunction<HttpRequest> send = req -> {
        try {
            var resp = client.send(req, BodyHandlers.ofString());
            trackStatus.accept(resp.statusCode());
            return resp.statusCode();
        } catch (Exception e) {
            return -1;
        }
    };

    // helper for POST that parses ID from response
    record PostResult(int status, String body) {}
    java.util.function.Function<HttpRequest, PostResult> sendPost = req -> {
        try {
            var resp = client.send(req, BodyHandlers.ofString());
            trackStatus.accept(resp.statusCode());
            return new PostResult(resp.statusCode(), resp.body());
        } catch (Exception e) {
            return new PostResult(-1, "");
        }
    };

    // operations
    Runnable create = () -> {
        var name = randName.get();
        var email = nextEmail.get();
        var bio = randBio.get();
        var json = "{\"name\":\"%s\",\"email\":\"%s\",\"bio\":\"%s\"}".formatted(name, email, bio);
        var req = HttpRequest.newBuilder(URI.create(BASE))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json").build();
        var r = sendPost.apply(req);
        if (r.status() == 201) {
            var m = java.util.regex.Pattern.compile("\"id\":(\\d+)").matcher(r.body());
            if (m.find()) {
                ids.add(Long.parseLong(m.group(1)));
                emails.add(email);
            }
        }
        counters.computeIfAbsent("create", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable update = () -> {
        if (ids.isEmpty()) { create.run(); return; }
        int idx = rng.nextInt(ids.size());
        try {
            var id = ids.get(idx);
            var em = emails.get(idx);
            var json = "{\"name\":\"%s\",\"email\":\"%s\",\"bio\":\"%s\"}".formatted(randName.get(), em, randBio.get());
            var req = HttpRequest.newBuilder(URI.create(BASE + "/" + id))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json").build();
            send.applyAsInt(req);
        } catch (IndexOutOfBoundsException _) {}
        counters.computeIfAbsent("update", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable delete = () -> {
        if (ids.isEmpty()) { create.run(); return; }
        try {
            int idx = rng.nextInt(ids.size());
            var id = ids.remove(idx);
            emails.remove(idx);
            var req = HttpRequest.newBuilder(URI.create(BASE + "/" + id)).DELETE().build();
            send.applyAsInt(req);
        } catch (IndexOutOfBoundsException _) {}
        counters.computeIfAbsent("delete", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable search = () -> {
        var q = firstNames.get(rng.nextInt(firstNames.size()));
        var req = HttpRequest.newBuilder(URI.create(BASE + "/search?name=" + q)).GET().build();
        send.applyAsInt(req);
        counters.computeIfAbsent("search", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable getAll = () -> {
        var req = HttpRequest.newBuilder(URI.create(BASE)).GET().build();
        send.applyAsInt(req);
        counters.computeIfAbsent("getAll", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable getById = () -> {
        if (ids.isEmpty()) { create.run(); return; }
        try {
            var id = ids.get(rng.nextInt(ids.size()));
            var req = HttpRequest.newBuilder(URI.create(BASE + "/" + id)).GET().build();
            send.applyAsInt(req);
        } catch (IndexOutOfBoundsException _) {}
        counters.computeIfAbsent("getById", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable dupCreate = () -> {
        if (emails.isEmpty()) { create.run(); return; }
        try {
            var em = emails.get(rng.nextInt(emails.size()));
            var json = "{\"name\":\"Dup User\",\"email\":\"%s\",\"bio\":\"dup\"}".formatted(em);
            var req = HttpRequest.newBuilder(URI.create(BASE))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json").build();
            send.applyAsInt(req);
        } catch (IndexOutOfBoundsException _) {}
        counters.computeIfAbsent("dupCreate", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable delNotFound = () -> {
        var fakeId = 90000 + rng.nextInt(10000);
        var req = HttpRequest.newBuilder(URI.create(BASE + "/" + fakeId)).DELETE().build();
        send.applyAsInt(req);
        counters.computeIfAbsent("delNotFound", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable getByEmail = () -> {
        if (emails.isEmpty()) { create.run(); return; }
        try {
            var em = emails.get(rng.nextInt(emails.size()));
            var req = HttpRequest.newBuilder(URI.create(BASE + "/email/" + em)).GET().build();
            send.applyAsInt(req);
        } catch (IndexOutOfBoundsException _) {}
        counters.computeIfAbsent("getByEmail", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable getRecent = () -> {
        var days = 1 + rng.nextInt(30);
        var req = HttpRequest.newBuilder(URI.create(BASE + "/recent?days=" + days)).GET().build();
        send.applyAsInt(req);
        counters.computeIfAbsent("getRecent", _ -> new AtomicInteger()).incrementAndGet();
    };

    Runnable getCount = () -> {
        var req = HttpRequest.newBuilder(URI.create(BASE + "/count")).GET().build();
        send.applyAsInt(req);
        counters.computeIfAbsent("getCount", _ -> new AtomicInteger()).incrementAndGet();
    };

    // weighted ops table
    var ops = List.of(
        create, create, create,
        update, update,
        delete,
        search, search,
        getAll, getAll,
        getById, getById,
        dupCreate,
        delNotFound,
        getByEmail,
        getRecent,
        getCount
    );

    // shutdown hook prints stats on Ctrl+C
    Runtime.getRuntime().addShutdownHook(Thread.ofVirtual().unstarted(() -> {
        running.set(false);
        System.out.println("\n\n=== Stopped ===");
        System.out.println("  Operations:");
        counters.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("    %-14s %d%n", e.getKey(), e.getValue().get()));
        System.out.println("\n  HTTP Status Classes:");
        statusCounters.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("    %-6s %d%n", e.getKey(), e.getValue().get()));
    }));

    System.out.println("=== Quarkus User API Load Test ===");
    System.out.println("  Pace: 10-100 req/s (random)");
    System.out.println("  Press Ctrl+C to stop\n");

    // seed
    for (int s = 0; s < 10; s++) create.run();
    System.out.printf("Seeded %d users%n%n", ids.size());

    var start = Instant.now();
    var total = new AtomicInteger(0);

    while (running.get()) {
        // pick random target rate between 10-100 req/s, hold for 5-15 seconds
        int targetRps = 10 + rng.nextInt(91);
        int burstDurationMs = 5000 + rng.nextInt(10001);
        long burstEnd = System.currentTimeMillis() + burstDurationMs;
        int delayMs = 1000 / targetRps;

        System.out.printf("  >>> target %d req/s for %.0fs%n", targetRps, burstDurationMs / 1000.0);

        while (running.get() && System.currentTimeMillis() < burstEnd) {
            var op = ops.get(rng.nextInt(ops.size()));
            op.run();
            int t = total.incrementAndGet();

            if (t % 50 == 0) {
                long secs = Duration.between(start, Instant.now()).toSeconds();
                double avgRps = secs > 0 ? (double) t / secs : t;
                int s2xx = statusCounters.getOrDefault("2xx", new AtomicInteger()).get();
                int s4xx = statusCounters.getOrDefault("4xx", new AtomicInteger()).get();
                int s5xx = statusCounters.getOrDefault("5xx", new AtomicInteger()).get();
                System.out.printf("  [%d ops | avg %.1f req/s | 2xx=%d 4xx=%d 5xx=%d]%n",
                        t, avgRps, s2xx, s4xx, s5xx);
            }

            // jitter: 50%-150% of base delay
            int jitter = Math.max(1, delayMs / 2 + rng.nextInt(Math.max(delayMs, 1)));
            Thread.sleep(jitter);
        }
    }
}
