# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Changed

- Usage reporting is now disclosed on every startup: the plugin logs whether reporting is on — and what is sent, where, and how to turn it off — or why it is off. Two new opt-outs win over `usage-reporting.enabled`: `enabled: false` in `plugins/trace/config.yml`, a server-wide switch written by the first trace-reporting plugin to start, and the environment variables `TRACE_USAGE_REPORTING=off` / `DO_NOT_TRACK=1`. A `config.yml` that has no `usage-reporting` block gains one from the bundled defaults on enable, so the switch is visible on disk. `README.md` gained a "Usage reporting" section. Nothing about what is sent changed.

### Fixed

- `/balance` no longer answers a player who holds no coinpurse with silence. That was the one outcome of the command producing no message at all, which is indistinguishable from the command having failed to register; the player is now told that no coinpurse could be found, through the new configurable `balanceNoCoinpurse` key. An empty coinpurse still reports a balance of zero, so the two situations remain distinguishable.
- Death, `/deposit` and `/withdraw` no longer throw a `NullPointerException` for a player who holds no coinpurse. A coinpurse is normally assigned on join, but a record that failed to load leaves the player without one, and those three paths used the lookup result without checking it. An empty coinpurse is now created on demand instead, so the player is told what happened rather than the console receiving a stack trace.
- A coinpurse record that cannot be read is now dropped at load time rather than kept in memory without a player UUID. Such a record is produced when the file named in the record index is missing, and when an upgrade from a pre-v0.7 save resolves a player name the server no longer knows. It used to make every subsequent coinpurse lookup throw, and — because writing the record index dereferenced the same missing UUID — it aborted the shutdown save before a single balance had been written. The lookup itself is also null-safe now, so an unreadable record can no longer take the rest of the coinpurses down with it.
- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.

### Added

- The plugin now reports usage events — `startup` on enable, `command` on each of its commands — to the author's trace server so it is known which plugins are in use. Events carry the plugin name, the event name, and the plugin version or command name; nothing about players or the server. Reporting runs off the main thread, never delays a tick, drops silently when the server is unreachable, and is turned off with `usage-reporting.enabled: false` in `config.yml`. The default config carries the plugin's key, so reporting is active out of the box unless turned off — including on servers upgraded from a version before the `usage-reporting` block existed, whose `config.yml` is never rewritten: the plugin reads the bundled defaults for any key the file lacks.
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
