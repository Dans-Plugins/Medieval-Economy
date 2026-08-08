# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

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
