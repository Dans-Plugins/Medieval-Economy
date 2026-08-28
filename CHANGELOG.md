# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Fixed

- Death, `/deposit` and `/withdraw` no longer throw a `NullPointerException` for a player who holds no coinpurse. A coinpurse is normally assigned on join, but a record that failed to load leaves the player without one, and those three paths used the lookup result without checking it. An empty coinpurse is now created on demand instead, so the player is told what happened rather than the console receiving a stack trace.
- A stored coinpurse whose player UUID is null no longer breaks every coinpurse lookup after it. Such a record is produced when an upgrade from a pre-v0.7 save resolves a player name the server no longer knows, and the lookup used to compare from the stored UUID rather than the requested one. That record is now skipped.
- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.

### Added

- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get medievaleconomy --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.
- Each command declared in `plugin.yml` now carries a description and a usage string, so the server's own `/help` output describes them.
- `/econ createcurrency` now answers a non-numeric amount with a usage hint and an amount below 1 with a refusal, instead of throwing out of the command handler. Both messages are configurable through the new `createCurrencyUsageText` and `createCurrencyPositiveText` keys.

### Fixed
- `/deposit 0` and `/withdraw 0` are now refused with "Number must be positive!", the message their guard was always meant to produce. Zero previously passed the guard and reported a movement of no coins.
- `depositUsageText` was registered twice, so the default a generated `config.yml` received was `Usage: /deposit (number)` rather than the `Usage: /deposit (whole number)` documented in `CONFIG.md` and matching `withdrawUsageText`. The stray second registration has been removed. Existing `config.yml` files are unaffected; a value already saved there stays as it is.
- Every permission node the plugin checks is now declared in `plugin.yml`. Undeclared nodes fall back to Bukkit's op-only default, so `/balance`, `/deposit` and `/withdraw` were unusable by ordinary players on servers without a permissions plugin. The declared defaults match the table in `USER_GUIDE.md`, and `medievaleconomy.admin` now genuinely grants every other node. Explicit grants made through a permissions plugin are unaffected.
- `COMMANDS.md` no longer lists `medievaleconomy.default` against `/econ help`; that subcommand has never been permission-gated.

## [2.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

### Changed
- Medieval-Economy is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The major version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `2.0.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.

## [1.2.0]

### Added
- Physical coin-based currency with inventory items.
- Coinpurse virtual wallet per player.
- `/econ`, `/balance`, `/deposit`, `/withdraw` commands.
- Death event integration (coinpurse message on death).
- bStats metrics.
