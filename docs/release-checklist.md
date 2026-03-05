# Release Checklist — Campus Study Hub

Use this checklist before cutting a release tag.

## Pre-Release

- [ ] **Build & Tests**: `./mvnw clean test` passes with 0 failures
- [ ] **E2E Tests**: Cypress smoke tests pass (`npx cypress run`)
- [ ] **Load Tests**: k6 smoke test passes locally (`k6 run scripts/load-tests/smoke-test.js`)
- [ ] **Seed Data**: Demo database is populated (`./scripts/seed-data.sh`)
- [ ] **Demo Deck**: `/docs/demo/demo-deck.md` is up to date
- [ ] **Recording**: Demo video recorded per `/docs/demo/recording-guidelines.md`

## Security & Compliance

- [ ] No secrets committed to repository
- [ ] `.env.example` updated with all required variables
- [ ] Privacy policy and cookie policy present in `/docs/`
- [ ] Data retention policy documented

## Infrastructure

- [ ] Docker image builds successfully (`docker build -t campus-hub .`)
- [ ] `docker-compose up` starts all services
- [ ] Health check passes: `curl http://localhost:8080/actuator/health`
- [ ] Backup script tested: `./ops/auto-backup.sh` (dry-run)

## Release

- [ ] `RELEASE_NOTES.md` created with feature summary
- [ ] Git tag created: `git tag v1.0.0 -m "Campus-Hub v1.0.0"`
- [ ] Tag pushed: `git push origin v1.0.0`
- [ ] GitHub Release created with JAR artifact attached
- [ ] Handoff documentation present in `/docs/handoff.md`

## Rollback Plan

If issues are found after release:

1. Revert to previous Docker image: `docker pull ghcr.io/<repo>:<previous-tag>`
2. Restore database from backup: `./ops/restore-example.sh`
3. Notify team via escalation matrix in `/docs/handoff.md`
