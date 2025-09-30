-- Sample data for users table
INSERT INTO users (id, name, email, bio, created_at, updated_at) VALUES (1, 'John Doe', 'john.doe@example.com', 'Software Engineer passionate about cloud computing and DevOps', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, name, email, bio, created_at, updated_at) VALUES (2, 'Jane Smith', 'jane.smith@example.com', 'Full-stack developer with expertise in Java and React', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, name, email, bio, created_at, updated_at) VALUES (3, 'Bob Johnson', 'bob.johnson@example.com', 'DevOps engineer specializing in Kubernetes and observability', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, name, email, bio, created_at, updated_at) VALUES (4, 'Alice Williams', 'alice.williams@example.com', 'Backend developer focused on microservices architecture', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, name, email, bio, created_at, updated_at) VALUES (5, 'Charlie Brown', 'charlie.brown@example.com', 'Site Reliability Engineer with a passion for monitoring and tracing', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Set sequence for next ID
ALTER SEQUENCE users_SEQ RESTART WITH 6;