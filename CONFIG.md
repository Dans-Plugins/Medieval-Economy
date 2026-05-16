# Medieval Economy Configuration

The configuration file is generated at `plugins/MedievalEconomy/config.yml` on first run.

## Options

| Key | Default | Description |
|-----|---------|-------------|
| `version` | *(plugin version)* | Config file version. Do not edit manually. |
| `currencyItemName` | `Gold Coin` | Display name of the coin item. |
| `currencyItemLoreLineOne` | `The currency of the Continent.` | First lore line on the coin item. |
| `currencyItemLoreLineTwo` | `Best kept in a coinpurse.` | Second lore line on the coin item. |
| `currencyItemLoreLineThree` | `useful commands: /balance /deposit /withdraw` | Third lore line on the coin item. |
| `titleSeparator` | `true` | Whether to display a separator line in the help menu. |
| `balanceTextStart` | `You have ` | Text prefix for the balance message. |
| `balanceTextEnd` | ` coins in your coinpurse.` | Text suffix for the balance message. |
| `depositUsageText` | `Usage: /deposit (whole number)` | Usage hint shown on bad deposit input. |
| `depositPositiveText` | `Number must be positive!` | Error when a non-positive amount is provided. |
| `depositTextStart` | `You open your coinpurse and deposit ` | Prefix for successful deposit message. |
| `depositTextEnd` | ` coins.` | Suffix for successful deposit message. |
| `depositNotEnoughCoins` | `You don't have that many coins!` | Error when inventory lacks sufficient coins. |
| `withdrawUsageText` | `Usage: /withdraw (whole number)` | Usage hint shown on bad withdraw input. |
| `withdrawPositiveText` | `Number must be positive!` | Error when a non-positive amount is provided. |
| `withdrawTextStart` | `You open your coinpurse and take out ` | Prefix for successful withdraw message. |
| `withdrawTextEnd` | ` coins.` | Suffix for successful withdraw message. |
| `withdrawNotEnoughCoins` | `You don't have that many coins in your coinpurse!` | Error when coinpurse lacks sufficient coins. |
| `withdrawNotEnoughSpace` | `You don't have enough space in your inventory for that many coins!` | Error when inventory is full. |
| `deathMessage` | `Your coinpurse feels lighter than it was.` | Message shown to a player on death (coins may be lost). |
