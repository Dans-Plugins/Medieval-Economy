# Medieval Economy Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/econ` or `/econ help` | *(none)* | Show the help menu. |
| `/econ createcurrency [amount]` | `medievaleconomy.createcurrency` | Give yourself `amount` coins (default 1). Refused with "Inventory full." when your inventory has no empty slot. |
| `/econ reload` | `medievaleconomy.reload` | Reload the plugin configuration. |
| `/balance` | `medievaleconomy.balance` | Display how many coins are in your coinpurse. |
| `/deposit <amount>` | `medievaleconomy.deposit` | Move coins from your inventory into your coinpurse. |
| `/withdraw <amount>` | `medievaleconomy.withdraw` | Move coins from your coinpurse into your inventory. |

## From the console

Only `/econ reload` does anything when run from the server console; it needs no permission there
and prints the `configReloadedText` message to the log. `/econ createcurrency` is refused with the
`createCurrencyNoRunFromConsole` message. `/econ`, `/econ help`, `/balance`, `/deposit` and
`/withdraw` are player-only and produce no output from the console.
