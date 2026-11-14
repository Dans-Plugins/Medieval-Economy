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

If the player does not rejoin, the record is lost from the index instead. `coinpurse-record-filenames.txt` is rewritten each time the server stops, from the coinpurses the server holds, which do not include the unreadable record, so its filename is dropped and the file in `Coinpurse-Records/` is not read on later starts. If you repair the record after the server has stopped once, add its filename (`<uuid>.txt`) back to `coinpurse-record-filenames.txt` as well, with the server stopped, so that the next start loads it.

A player who is already online when the plugin is enabled, for example when it is loaded or reloaded while the server is running, is not seen joining, so no starter kit is given and no coinpurse is assigned to a player who has none. Until one is created, `/balance` answers them with the `balanceNoCoinpurse` message. An empty coinpurse is created for them the first time they die, run `/withdraw` with a whole number above zero, or `/deposit` coins they actually hold; after that, their next join gives no starter kit. If their record could not be read, it is that coinpurse which is saved over it when the server stops, so for a player who is online, repair or restore the record before any of those happen.

## Death Penalty

When a player with coins in their coinpurse dies, 10% of their coinpurse, rounded down (or 1 coin if their balance is below 10), is removed and dropped on the ground as physical coins.

## Where Balances Are Stored

Each coinpurse is stored in `plugins/MedievalEconomy/Coinpurse-Records/<uuid>.txt`, with the player's UUID on the first line and their balance on the second. `plugins/MedievalEconomy/coinpurse-record-filenames.txt` lists the records to load at startup, one filename per line.

Balances are held in memory while the server runs and are written to these files only when the plugin is disabled, which normally happens when the server stops. A server that crashes or is killed without stopping cleanly loses every balance change made since the plugin was last enabled: deposits, withdrawals and death penalties, and the coinpurses of players who joined for the first time in that period. Player inventories are saved by the server on its own schedule, not with the coinpurses, so after such a crash a deposit may have taken coins from an inventory without them reaching the coinpurse, and a withdrawal may have put coins in an inventory without them leaving the coinpurse.

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

`/balance`, `/deposit` and `/withdraw` run for a player who holds either the command's own permission or `medievaleconomy.default`, and every player holds `medievaleconomy.default` unless it is set to false. Setting only `medievaleconomy.balance`, `medievaleconomy.deposit` or `medievaleconomy.withdraw` to false therefore does not take the command away: `medievaleconomy.default` has to be false as well. In the same way, `/econ createcurrency` and `/econ reload` also accept `medievaleconomy.admin`.

## Support

Open a [GitHub issue](https://github.com/Dans-Plugins/Medieval-Economy/issues) to report bugs or request features.
