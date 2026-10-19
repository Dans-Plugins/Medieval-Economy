# Contributing to Medieval Economy

Thank you for your interest in contributing!

## Workflow

1. Fork the repository.
2. Create a feature branch from `main`: `git checkout -b feature/my-change`
3. Make your changes.
4. Open a pull request against `main`.
5. Reference the related GitHub issue in your pull request description.

## Code Style

- Language: Java
- Build tool: Maven (`mvn clean package`, which also runs the tests)
- Follow existing conventions in the codebase.

## Tests

The tests are JUnit 4 and live under `src/test/java`; run them with `mvn test`. `MedievalEconomy`
extends `JavaPlugin`, which cannot be constructed outside a running server, so a test does not
build the plugin or a command handler. Logic worth testing is instead moved into a package-private
`static` method that takes plain values, and the handler delegates to it — see
`DeathListener.takeDeathPenalty` and `BalanceCommand.composeBalanceMessage` for the pattern.

## Reporting Issues

Open a [GitHub issue](https://github.com/Dans-Plugins/Medieval-Economy/issues) with a clear description of the bug or feature request.
