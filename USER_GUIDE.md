# Medieval Economy User Guide

## What is Medieval Economy?

Medieval Economy is a Spigot plugin that adds a physical coin-based currency system to your server. Players carry coins in their inventory and store them in a virtual coinpurse. Coins are represented as named items that can be deposited and withdrawn at any time.

## Installation

1. Download the latest `Medieval-Economy-<version>.jar` from the [Releases](https://github.com/Dans-Plugins/Medieval-Economy/releases) page.
2. Place the JAR in your server's `plugins/` folder.
3. Restart the server.

## Getting Started

1. Admins create currency with `/econ createcurrency [amount]` (requires `medievaleconomy.createcurrency`; `amount` defaults to 1).
2. Check your balance: `/balance`
3. Deposit coins from your inventory into your coinpurse: `/deposit <amount>`
4. Withdraw coins from your coinpurse into your inventory: `/withdraw <amount>`

## Starter Kit

When a player joins without a coinpurse, they're given 50 gold coins, 10 bread, and a book and quill, and an empty coinpurse is created for them. Items that do not fit in their inventory are not given.

A player normally has no coinpurse only on their first join. A player whose coinpurse record could not be read at startup (the `coinpurseLoadErrorText` message in the console, see [CONFIG.md](CONFIG.md)) has none either, so on their next join they get the starter kit again and an empty coinpurse, and that empty coinpurse is saved over their unreadable record when the server stops. Repair or restore the record before the player rejoins to keep their balance.

The record also leaves the index whether or not the player rejoins. `coinpurse-record-filenames.txt` is rewritten from the coinpurses that loaded each time the server stops, so the unreadable record's filename is dropped from it and the file in `Coinpurse-Records/` is not read on later starts. If you repair the record after the server has stopped once, add its filename (`<uuid>.txt`) back to `coinpurse-record-filenames.txt` as well, with the server stopped, so that the next start loads it.

## Death Penalty

When a player with coins in their coinpurse dies, 10% of their coinpurse, rounded down (or 1 coin if their balance is below 10), is removed and dropped on the ground as physical coins.

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `medievaleconomy.default` | `true` | Grants balance, deposit, and withdraw access. |
| `medievaleconomy.balance` | `true` | Check coinpurse balance. |
| `medievaleconomy.deposit` | `true` | Deposit coins into your coinpurse. |
| `medievaleconomy.withdraw` | `true` | Withdraw coins from your coinpurse. |
| `medievaleconomy.createcurrency` | `op` | Create coin items. |
| `medievaleconomy.reload` | `op` | Reload the plugin config. |
| `medievaleconomy.admin` | `op` | Grants all permissions. |

## Support

Open a [GitHub issue](https://github.com/Dans-Plugins/Medieval-Economy/issues) to report bugs or request features.
