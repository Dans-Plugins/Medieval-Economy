# Medieval Economy User Guide

## What is Medieval Economy?

Medieval Economy is a Spigot plugin that adds a physical coin-based currency system to your server. Players carry coins in their inventory and store them in a virtual coinpurse. Coins are represented as named items that can be deposited and withdrawn at any time.

## Installation

1. Download the latest `Medieval-Economy-<version>.jar` from the [Releases](https://github.com/Dans-Plugins/Medieval-Economy/releases) page.
2. Place the JAR in your server's `plugins/` folder.
3. Restart the server.

## Getting Started

1. Admins create currency with `/econ createcurrency <amount>` (requires `medievaleconomy.createcurrency`).
2. Check your balance: `/balance`
3. Deposit coins from your inventory into your coinpurse: `/deposit <amount>`
4. Withdraw coins from your coinpurse into your inventory: `/withdraw <amount>`

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
