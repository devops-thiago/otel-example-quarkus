## What

<!-- One sentence: what does this PR do? -->

## Why

<!-- Why is this change needed? Link issues if applicable. -->

## Changes

<!-- Bullet list of what changed. Be specific: file names, before/after values. -->

-

## Test Plan

<!-- How was this verified? Check all that apply. -->

- [ ] Unit tests pass (`make test` / `npm run test:unit` / `dotnet test` / `poetry run pytest`)
- [ ] Linter / formatter clean (`make fmt-check` / `npm run lint` / `dotnet format --verify-no-changes`)
- [ ] Manual smoke test against local `docker compose up`
- [ ] Integration tests pass (if DB available)
- [ ] No new high-severity vulnerabilities (`npm audit --audit-level=high` / `trivy`)

## Checklist

- [ ] Commit messages follow Conventional Commits (`type(scope): description`)
- [ ] No debug output, commented-out code, or `TODO`s left behind
- [ ] Equivalent change applied to all relevant repos (if cross-repo feature)
- [ ] Docs / README updated if behaviour or setup changed
