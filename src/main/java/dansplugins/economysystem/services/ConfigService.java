package dansplugins.economysystem.services;

import dansplugins.economysystem.MedievalEconomy;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

/**
 * @author Daniel McCoy Stephenson
 */
public class ConfigService {
    private static final String USAGE_REPORTING_ENABLED_KEY = "usage-reporting.enabled";
    private static final String USAGE_REPORTING_ENDPOINT_KEY = "usage-reporting.endpoint";
    private static final String USAGE_REPORTING_KEY_KEY = "usage-reporting.key";
    private static final String DEFAULT_USAGE_REPORTING_ENDPOINT = "https://trace.danielstephenson.dev";

    private final MedievalEconomy medievalEconomy;

    public ConfigService(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void handleVersionMismatch() {

        if (!medievalEconomy.getConfig().getString("version").equalsIgnoreCase(medievalEconomy.getVersion())) {
            System.out.println("[ALERT] Verson mismatch! Saving old config as config.yml.old and loading in the default values.");
            renameConfigToConfigDotOldAndSaveDefaults();
        }

    }

    public void renameConfigToConfigDotOldAndSaveDefaults() {
        // save old config as config.yml.old
        File saveFile = new File("./plugins/MedievalEconomy/config.yml");
        if (saveFile.exists()) {

            // rename file
            File newSaveFile = new File("./plugins/MedievalEconomy/config.yml.old");
            saveFile.renameTo(newSaveFile);

            // save defaults
            saveConfigDefaults();
        }
    }

    public void saveConfigDefaults() {
        registerDefaults();
        medievalEconomy.getConfig().options().copyDefaults(true);
        medievalEconomy.saveConfig();
    }

    /**
     * Registers every key the plugin reads, so that a one-argument getter falls through to
     * the default for any key the file on disk lacks. Registering is idempotent and never
     * touches a value the file already carries, which is why it is safe to do on every
     * enable rather than only when the file is first generated.
     */
    public void registerDefaults() {
        medievalEconomy.getConfig().addDefault("version", medievalEconomy.getVersion());
        medievalEconomy.getConfig().addDefault("enablingText", "Medieval Economy is enabling...");
        medievalEconomy.getConfig().addDefault("enabledText", "Medieval Economy is enabled!");
        medievalEconomy.getConfig().addDefault("disablingText", "Medieval Economy is disabling...");
        medievalEconomy.getConfig().addDefault("disabledText", "Medieval Economy is disabled!");
        medievalEconomy.getConfig().addDefault("coinpurseSaveErrorText", "An error occurred saving a Coinpurse Record.");
        medievalEconomy.getConfig().addDefault("coinpurseLoadErrorText", "An error occurred loading ");
        medievalEconomy.getConfig().addDefault("balanceTextStart", "You have ");
        medievalEconomy.getConfig().addDefault("balanceTextEnd", " coins in your coinpurse.");
        medievalEconomy.getConfig().addDefault("balanceNoCoinpurse", "No coinpurse could be found for you. One will be created when you next deposit coins.");
        medievalEconomy.getConfig().addDefault("balanceNoPermission", "Sorry! In order to run this command, you need the following permission: 'medievaleconomy.balance'");
        medievalEconomy.getConfig().addDefault("depositUsageText", "Usage: /deposit (whole number)");
        medievalEconomy.getConfig().addDefault("depositPositiveText", "Number must be positive!");
        medievalEconomy.getConfig().addDefault("depositTextStart", "You open your coinpurse and deposit ");
        medievalEconomy.getConfig().addDefault("depositTextEnd", " coins.");
        medievalEconomy.getConfig().addDefault("depositNotEnoughCoins", "You don't have that many coins!");
        medievalEconomy.getConfig().addDefault("depositNoPermission", "Sorry! In order to use this command, you need the permission 'medievaleconomy.deposit'");
        medievalEconomy.getConfig().addDefault("createCurrencyUsageText", "Usage: /econ createcurrency (whole number)");
        medievalEconomy.getConfig().addDefault("createCurrencyPositiveText", "Number must be positive!");
        medievalEconomy.getConfig().addDefault("createCurrencyNoPermission", "You need the following permission to use this command: 'medievaleconomy.createcurrency'");
        medievalEconomy.getConfig().addDefault("createCurrencyNoRunFromConsole", "You can't run this command from the console!");
        medievalEconomy.getConfig().addDefault("configReloadedText", "Config reloaded!");
        medievalEconomy.getConfig().addDefault("reloadNoPermission", "You need the following permission to use this command: 'medievaleconomy.reload'");
        medievalEconomy.getConfig().addDefault("withdrawUsageText", "Usage: /withdraw (whole number)");
        medievalEconomy.getConfig().addDefault("withdrawPositiveText", "Number must be positive!");
        medievalEconomy.getConfig().addDefault("withdrawTextStart", "You open your coinpurse and take out ");
        medievalEconomy.getConfig().addDefault("withdrawTextEnd", " coins.");
        medievalEconomy.getConfig().addDefault("withdrawNotEnoughCoins", "You don't have that many coins in your coinpurse!");
        medievalEconomy.getConfig().addDefault("withdrawNotEnoughSpace", "You don't have enough space in your inventory for that many coins!");
        medievalEconomy.getConfig().addDefault("withdrawNoPermission", "Sorry! In order to use this command, you need the permission " + "'medievaleconomy.withdraw'");
        medievalEconomy.getConfig().addDefault("deathMessage", "Your coinpurse feels lighter than it was.");
        medievalEconomy.getConfig().addDefault("storageSaveError", "An error occurred while saving coinpurse record filenames.");
        medievalEconomy.getConfig().addDefault("storageLoadError", "Error loading the coinpurse records!");
        medievalEconomy.getConfig().addDefault("titleSeparator", true);
        medievalEconomy.getConfig().addDefault("currencyItemName", "Gold Coin");
        medievalEconomy.getConfig().addDefault("currencyItemLoreLineOne", "The currency of the Continent.");
        medievalEconomy.getConfig().addDefault("currencyItemLoreLineTwo", "Best kept in a coinpurse.");
        medievalEconomy.getConfig().addDefault("currencyItemLoreLineThree", "useful commands: /balance /deposit /withdraw");
        medievalEconomy.getConfig().addDefault("compatibilityText", "[ALERT] Old save folder name (pre v3.2) detected. Updating for compatibility.");
        // The same three values are written in src/main/resources/config.yml, which is what an
        // installation whose config.yml predates usage reporting falls back to (see the getters
        // below); UsageReportingConfigTest pins the two together.
        medievalEconomy.getConfig().addDefault("usage-reporting.enabled", true);
        medievalEconomy.getConfig().addDefault("usage-reporting.endpoint", "https://trace.danielstephenson.dev");
        medievalEconomy.getConfig().addDefault("usage-reporting.key", "v9jS7yhG5qIdX8rvSNGNTJvfIHQMe5jPy4Xt5J0UBpA");
    }

    /**
     * Registers every default on the loaded config and writes the file only when it lacks
     * one of them. {@link #saveConfigDefaults()} runs on a fresh install and on a version
     * change; this covers the remaining case, a config.yml generated by an earlier build
     * that carries the same version string (the rolling {@code dev} prerelease), which would
     * otherwise never learn a key added since and would read it back as null. A file that
     * already carries every key is left untouched, so a hand-edited config is not rewritten
     * on every startup; when a key is missing the write keeps every value already there and
     * appends the defaults, including the bundled usage-reporting block.
     */
    public void ensureDefaultsOnDisk() {
        registerDefaults();
        if (lacksARegisteredKey(medievalEconomy.getConfig())) {
            medievalEconomy.getConfig().options().copyDefaults(true);
            medievalEconomy.saveConfig();
        }
    }

    /**
     * Whether a key present in {@code config}'s defaults is absent from {@code config}
     * itself. The check reads through {@link ConfigurationSection#get(String, Object)},
     * which never falls through to the defaults, so it answers the same whether or not
     * {@code copyDefaults} has been switched on -- unlike {@code isSet} and {@code getKeys},
     * which report the defaults as present once it has.
     */
    static boolean lacksARegisteredKey(Configuration config) {
        Configuration defaults = config.getDefaults();
        if (defaults == null) {
            return false;
        }
        for (String key : defaults.getKeys(true)) {
            if (defaults.isConfigurationSection(key)) {
                continue;
            }
            if (config.get(key, null) == null) {
                return true;
            }
        }
        return false;
    }

    // The one-argument getters, deliberately. Bukkit registers the jar's config.yml as the
    // defaults for the server's config.yml, and the one-argument getters fall through to
    // them for any key the file on disk lacks -- but the two-argument getters return their
    // explicit fallback instead, which for the key would be "" and would turn reporting off
    // on a config.yml that has no usage-reporting block yet. Verified against
    // YamlConfiguration, not assumed. ensureDefaultsOnDisk() writes the block onto disk on
    // enable, so the fall-through is only needed until that has run once.

    public boolean isUsageReportingEnabled() {
        return medievalEconomy.getConfig().getBoolean(USAGE_REPORTING_ENABLED_KEY);
    }

    public String getUsageReportingEndpoint() {
        String endpoint = medievalEconomy.getConfig().getString(USAGE_REPORTING_ENDPOINT_KEY);
        return endpoint != null ? endpoint : DEFAULT_USAGE_REPORTING_ENDPOINT;
    }

    /** Empty when no key is configured or bundled, which the client treats as "off". */
    public String getUsageReportingKey() {
        String key = medievalEconomy.getConfig().getString(USAGE_REPORTING_KEY_KEY);
        return key != null ? key : "";
    }

}
