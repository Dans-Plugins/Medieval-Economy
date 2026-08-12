# Medieval Economy Configuration

The configuration file is generated at `plugins/MedievalEconomy/config.yml` on first run.

## Options

| Key | Default | Description |
|-----|---------|-------------|
| `version` | *(plugin version)* | Config file version. Do not edit manually. |
| `enablingText` | `Medieval Economy is enabling...` | Console message shown while the plugin is enabling. |
| `enabledText` | `Medieval Economy is enabled!` | Console message shown once the plugin has enabled. |
| `disablingText` | `Medieval Economy is disabling...` | Console message shown while the plugin is disabling. |
| `disabledText` | `Medieval Economy is disabled!` | Console message shown once the plugin has disabled. |
| `compatibilityText` | `[ALERT] Old save folder name (pre v3.2) detected. Updating for compatibility.` | Console message shown when migrating saves from the pre-v3.2 folder name. |
| `configReloadedText` | `Config reloaded!` | Message shown after `/econ reload` succeeds. |
| `reloadNoPermission` | `You need the following permission to use this command: 'medievaleconomy.reload'` | Error shown when `/econ reload` is used without permission. |
| `createCurrencyUsageText` | `Usage: /econ createcurrency (whole number)` | Usage hint shown when `/econ createcurrency` is given something other than a whole number. |
| `createCurrencyPositiveText` | `Number must be positive!` | Error when `/econ createcurrency` is given an amount below 1. |
| `createCurrencyNoPermission` | `You need the following permission to use this command: 'medievaleconomy.createcurrency'` | Error shown when `/econ createcurrency` is used without permission. |
| `createCurrencyNoRunFromConsole` | `You can't run this command from the console!` | Error shown when `/econ createcurrency` is run from the console. |
| `currencyItemName` | `Gold Coin` | Display name of the coin item. |
| `currencyItemLoreLineOne` | `The currency of the Continent.` | First lore line on the coin item. |
| `currencyItemLoreLineTwo` | `Best kept in a coinpurse.` | Second lore line on the coin item. |
| `currencyItemLoreLineThree` | `useful commands: /balance /deposit /withdraw` | Third lore line on the coin item. |
| `titleSeparator` | `true` | Whether to display a blank separator line before the lore text on the coin item. |
| `balanceTextStart` | `You have ` | Text prefix for the balance message. |
| `balanceTextEnd` | ` coins in your coinpurse.` | Text suffix for the balance message. |
| `balanceNoPermission` | `Sorry! In order to run this command, you need the following permission: 'medievaleconomy.balance'` | Error shown when `/balance` is used without permission. |
| `depositUsageText` | `Usage: /deposit (whole number)` | Usage hint shown on bad deposit input. |
| `depositPositiveText` | `Number must be positive!` | Error when a non-positive amount is provided. |
| `depositTextStart` | `You open your coinpurse and deposit ` | Prefix for successful deposit message. |
| `depositTextEnd` | ` coins.` | Suffix for successful deposit message. |
| `depositNotEnoughCoins` | `You don't have that many coins!` | Error when inventory lacks sufficient coins. |
| `depositNoPermission` | `Sorry! In order to use this command, you need the permission 'medievaleconomy.deposit'` | Error shown when `/deposit` is used without permission. |
| `withdrawUsageText` | `Usage: /withdraw (whole number)` | Usage hint shown on bad withdraw input. |
| `withdrawPositiveText` | `Number must be positive!` | Error when a non-positive amount is provided. |
| `withdrawTextStart` | `You open your coinpurse and take out ` | Prefix for successful withdraw message. |
| `withdrawTextEnd` | ` coins.` | Suffix for successful withdraw message. |
| `withdrawNotEnoughCoins` | `You don't have that many coins in your coinpurse!` | Error when coinpurse lacks sufficient coins. |
| `withdrawNotEnoughSpace` | `You don't have enough space in your inventory for that many coins!` | Error when inventory is full. |
| `withdrawNoPermission` | `Sorry! In order to use this command, you need the permission 'medievaleconomy.withdraw'` | Error shown when `/withdraw` is used without permission. |
| `deathMessage` | `Your coinpurse feels lighter than it was.` | Message shown to a player on death (coins may be lost). |
| `coinpurseSaveErrorText` | `An error occurred saving a Coinpurse Record.` | Console message shown when a single coinpurse fails to save. |
| `coinpurseLoadErrorText` | `An error occurred loading ` | Console message prefix shown when a single coinpurse fails to load. |
| `storageSaveError` | `An error occurred while saving coinpurse record filenames.` | Console message shown when the coinpurse filename index fails to save. |
| `storageLoadError` | `Error loading the coinpurse records!` | Console message shown when the coinpurse filename index fails to load. |
