# Medieval Economy Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/econ` or `/econ help` | *(none)* | Show the help menu, which lists each command you have permission to run. |
| `/econ createcurrency [amount]` | `medievaleconomy.createcurrency` | Give yourself `amount` coins (default 1). Refused with "Inventory full." when your inventory has no empty slot. |
| `/econ reload` | `medievaleconomy.reload` | Reload the plugin configuration. |
| `/balance` | `medievaleconomy.balance` | Display how many coins are in your coinpurse. |
| `/deposit <amount>` | `medievaleconomy.deposit` | Move coins from your inventory into your coinpurse. Only coins that match the coin item as currently configured are counted (see `currencyItemName` in [CONFIG.md](CONFIG.md)). |
| `/withdraw <amount>` | `medievaleconomy.withdraw` | Move coins from your coinpurse into your inventory. Refused when your inventory has no empty slot, even if a stack of coins in it has room. If the coins do not all fit, as many as fit are withdrawn, the rest stay in your coinpurse, and the `withdrawNotEnoughSpace` message is shown in place of the usual count. |

## From the console

`/econ reload` is the only command that runs from the server console; it needs no permission there
and prints the `configReloadedText` message to the log. `/econ createcurrency` is refused, and the
`createCurrencyNoRunFromConsole` message is printed to the log. `/econ`, `/econ help`, `/balance`, `/deposit` and
`/withdraw` are player-only and produce no output from the console.
