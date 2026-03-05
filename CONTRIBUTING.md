# Contributing to Campus Study Hub

Thank you for considering contributing! Here's how to get started.

## Code of Conduct

Be respectful, constructive, and inclusive. We're all here to learn.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/<your-username>/Campus-Hub.git`
3. Create a branch: `git checkout -b feature/your-feature-name`
4. Make your changes
5. Run tests: `./mvnw test`
6. Commit and push
7. Open a Pull Request

## Commit Message Format

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): description

Examples:
  feat(booking): add recurring bookings
  fix(auth): handle expired sessions
  docs(readme): update deployment instructions
  test(e2e): add search flow test
  ci(release): fix Docker build step
```

### Types

- `feat` — New feature
- `fix` — Bug fix
- `docs` — Documentation only
- `test` — Adding or updating tests
- `ci` — CI/CD changes
- `refactor` — Code change that neither fixes a bug nor adds a feature
- `style` — Code style (formatting, missing semicolons, etc.)
- `chore` — Maintenance tasks

## Pull Request Checklist

- [ ] Code compiles without warnings (`./mvnw clean compile`)
- [ ] All tests pass (`./mvnw test`)
- [ ] New features have tests
- [ ] Documentation updated if needed
- [ ] Commit messages follow conventional format
- [ ] No secrets or credentials in code
- [ ] Branch is up to date with `main`

## Code Style

- **Java**: 4-space indentation, follow Spring Boot conventions
- **HTML**: Proper indentation, `lang="en"`, meta charset/viewport
- **SQL**: Uppercase keywords, lowercase identifiers
- **Scripts**: POSIX-compliant shell (`#!/bin/sh`), `set -e` at top
