package dansplugins.economysystem.services;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Pins the decision ConfigService.ensureDefaultsOnDisk() makes on every enable: write the
 * config.yml only when a registered key is missing from it. The defaults used to be
 * registered only when the file was first generated or the plugin version changed, so a
 * key added between two builds sharing one version string (the rolling {@code dev}
 * prerelease) read back as null and the player was shown the text "null".
 *
 * ConfigService cannot be executed against a live plugin here, so the decision is measured
 * against YamlConfiguration through the static check the method delegates to, the way
 * UsageReportingConfigTest measures the getter fall-through; the wiring in
 * MedievalEconomy.onEnable() is checked at the source level, as ConfigDefaultsTest checks
 * the registrations. The same check covers EconCommand's /econ reload.
 */
public class ConfigDefaultsOnDiskTest {

    private static final File MEDIEVAL_ECONOMY =
            new File("src/main/java/dansplugins/economysystem/MedievalEconomy.java");
    private static final File ECON_COMMAND =
            new File("src/main/java/dansplugins/economysystem/commands/EconCommand.java");
    private static final File CONFIG_SERVICE =
            new File("src/main/java/dansplugins/economysystem/services/ConfigService.java");

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void aConfigCarryingEveryRegisteredKey_isNotWritten() throws Exception {
        YamlConfiguration onDisk = load("version: v1.2.0\nenablingText: custom\nusage-reporting:\n  enabled: false\n");
        onDisk.addDefault("version", "v1.2.0");
        onDisk.addDefault("enablingText", "Medieval Economy is enabling...");
        onDisk.addDefault("usage-reporting.enabled", true);

        assertFalse(ConfigService.lacksARegisteredKey(onDisk));
    }

    @Test
    public void aConfigMissingATopLevelKey_isWritten() throws Exception {
        YamlConfiguration onDisk = load("version: v1.2.0\nenablingText: custom\n");
        onDisk.addDefault("version", "v1.2.0");
        onDisk.addDefault("enablingText", "Medieval Economy is enabling...");
        onDisk.addDefault("balanceNoCoinpurse", "No coinpurse could be found for you.");

        assertTrue(ConfigService.lacksARegisteredKey(onDisk));
    }

    @Test
    public void aConfigMissingTheNestedUsageReportingBlock_isWritten() throws Exception {
        YamlConfiguration onDisk = load("version: v1.2.0\nenablingText: custom\n");
        onDisk.addDefault("version", "v1.2.0");
        onDisk.addDefault("enablingText", "Medieval Economy is enabling...");
        onDisk.addDefault("usage-reporting.enabled", true);

        assertTrue(ConfigService.lacksARegisteredKey(onDisk));
    }

    /**
     * isSet() and getKeys() start reporting the defaults as present once copyDefaults is on,
     * which is the state saveConfigDefaults() leaves a config in. onEnable() reloads the config
     * before the check today, so copyDefaults is off there; the check must still not be fooled
     * should it ever run on such a config, or a missing key would silently go unwritten.
     */
    @Test
    public void aMissingKey_isStillDetectedOnceCopyDefaultsIsOn() throws Exception {
        YamlConfiguration onDisk = load("version: v1.2.0\n");
        onDisk.addDefault("version", "v1.2.0");
        onDisk.addDefault("balanceNoCoinpurse", "No coinpurse could be found for you.");
        onDisk.options().copyDefaults(true);

        assertTrue("copyDefaults makes isSet report the default as present", onDisk.isSet("balanceNoCoinpurse"));
        assertTrue(ConfigService.lacksARegisteredKey(onDisk));
    }

    @Test
    public void aConfigWithNoDefaults_isNotWritten() throws Exception {
        YamlConfiguration onDisk = load("version: v1.2.0\n");

        assertFalse(ConfigService.lacksARegisteredKey(onDisk));
    }

    /**
     * What the write that follows a positive check produces, measured against
     * YamlConfiguration: a value the operator changed survives, and the missing keys -- the
     * top-level one and the whole nested block -- are appended with their defaults.
     */
    @Test
    public void theWrite_keepsExistingValuesAndAppendsTheMissingDefaults() throws Exception {
        YamlConfiguration onDisk = load("version: v1.2.0\nenablingText: custom\n");
        onDisk.addDefault("version", "v1.2.0");
        onDisk.addDefault("enablingText", "Medieval Economy is enabling...");
        onDisk.addDefault("balanceNoCoinpurse", "No coinpurse could be found for you.");
        onDisk.addDefault("usage-reporting.enabled", true);
        onDisk.addDefault("usage-reporting.endpoint", "https://trace.danielstephenson.dev");

        assertTrue(ConfigService.lacksARegisteredKey(onDisk));
        onDisk.options().copyDefaults(true);
        YamlConfiguration written = load(onDisk.saveToString());

        assertEquals("custom", written.getString("enablingText"));
        assertEquals("No coinpurse could be found for you.", written.getString("balanceNoCoinpurse"));
        assertTrue(written.getBoolean("usage-reporting.enabled"));
        assertEquals("https://trace.danielstephenson.dev", written.getString("usage-reporting.endpoint"));
    }

    @Test
    public void onEnable_ensuresTheDefaultsOnDiskForAnExistingConfig() {
        boolean wired = false;
        for (String line : read(MEDIEVAL_ECONOMY)) {
            if (line.contains("configService.ensureDefaultsOnDisk()")) {
                wired = true;
            }
        }
        assertTrue("MedievalEconomy.onEnable() no longer calls ensureDefaultsOnDisk(), so a key added without a"
                + " version bump reads back as null on an existing config.yml", wired);
    }

    /**
     * reloadConfig() drops every registered default, so a reload that stopped there would leave
     * a key removed from the file reading back as null until the next restart. EconCommand
     * cannot be constructed here (#119), so the wiring is checked at the source level: both the
     * player and the console path must go through ConfigService.reload().
     */
    @Test
    public void econReload_goesThroughConfigServiceOnBothPaths() {
        int reloads = 0;
        for (String line : read(ECON_COMMAND)) {
            assertFalse("EconCommand reloads the config directly, so a key removed from config.yml reads back as"
                    + " null until the next restart: " + line.trim(), line.contains("medievalEconomy.reloadConfig()"));
            if (line.contains("getConfigService().reload()")) {
                reloads++;
            }
        }
        assertEquals("the player path and the console path", 2, reloads);
    }

    /**
     * /econ reload used to answer "Config reloaded!" for a file it could not read, because
     * ConfigService.reload() reported nothing. Both paths must act on its answer. EconCommand
     * cannot be constructed here (#119), so this is checked at the source level as above.
     */
    @Test
    public void econReload_actsOnWhetherTheFileWasRead() {
        for (String line : read(ECON_COMMAND)) {
            if (line.contains("getConfigService().reload()")) {
                assertTrue("EconCommand discards the result of the reload, so a file that could not be read is"
                        + " reported as reloaded: " + line.trim(), line.contains("if ("));
            }
        }
    }

    /**
     * Why ConfigService.reload() must check the file first. Bukkit's reloadConfig() loads through
     * YamlConfiguration.loadConfiguration, which catches a YAML error, logs it and returns an
     * empty config; the logging needs a running server, so that call cannot be made here. An
     * empty config lacks every registered key, so without the check, one typo followed by
     * /econ reload would replace the operator's whole config.yml with the defaults, and the
     * values in memory with them.
     */
    @Test
    public void aFileThatCannotBeParsed_isNotWrittenThoughItsEmptyConfigLacksEveryKey() throws Exception {
        YamlConfiguration whatBukkitLoadsForIt = load("");
        whatBukkitLoadsForIt.addDefault("enablingText", "Medieval Economy is enabling...");
        assertTrue(ConfigService.lacksARegisteredKey(whatBukkitLoadsForIt));

        assertFalse(ConfigService.canBeParsed(write("enablingText: custom\n  badly: indented: value\n")));
    }

    @Test
    public void aFileThatParses_mayBeWritten() throws Exception {
        assertTrue(ConfigService.canBeParsed(write("enablingText: custom\n")));
    }

    @Test
    public void anEmptyFile_mayBeWritten() throws Exception {
        assertTrue(ConfigService.canBeParsed(write("")));
    }

    @Test
    public void aDeletedFile_mayBeWritten() {
        assertTrue(ConfigService.canBeParsed(new File(folder.getRoot(), "config.yml")));
    }

    @Test
    public void aFileThatCannotBeParsed_hasItsErrorReported() throws Exception {
        String error = ConfigService.parseError(write("enablingText: custom\n  badly: indented: value\n"));
        assertTrue("the error is logged in place of the one Bukkit would have logged", error != null && !error.isEmpty());
    }

    @Test
    public void aFileThatParses_hasNoError() throws Exception {
        assertEquals(null, ConfigService.parseError(write("enablingText: custom\n")));
    }

    /**
     * Bukkit loads a file it cannot parse as an empty config, so a reload that called
     * reloadConfig() before checking the file would put every key on its default in memory,
     * and a server that customizes currencyItemName or the lore would stop accepting its own
     * coins for /deposit (#209). reload() needs a running plugin, so the order is checked at the
     * source level: inside reload(), the check and its early return come before reloadConfig().
     */
    @Test
    public void reload_checksTheFileBeforeReplacingTheValuesInMemory() {
        boolean inReload = false;
        int checkLine = -1;
        int returnFalseLine = -1;
        int reloadConfigLine = -1;
        List<String> lines = read(CONFIG_SERVICE);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("public boolean reload()")) {
                inReload = true;
            }
            else if (inReload && line.startsWith("    }")) {
                break;
            }
            if (!inReload) {
                continue;
            }
            if (checkLine < 0 && line.contains("parseError(configFile)")) {
                checkLine = i;
            }
            if (returnFalseLine < 0 && line.contains("return false;")) {
                returnFalseLine = i;
            }
            if (reloadConfigLine < 0 && line.contains("medievalEconomy.reloadConfig()")) {
                reloadConfigLine = i;
            }
        }
        assertTrue("ConfigService.reload() no longer checks the file", checkLine >= 0);
        assertTrue("ConfigService.reload() no longer calls reloadConfig()", reloadConfigLine >= 0);
        assertTrue("ConfigService.reload() no longer returns early for a file it cannot read", returnFalseLine >= 0);
        assertTrue("ConfigService.reload() reloads before checking the file, so a YAML error followed by"
                + " /econ reload puts every setting on its default", checkLine < returnFalseLine && returnFalseLine < reloadConfigLine);
    }

    private File write(String yaml) throws IOException {
        File file = folder.newFile();
        Files.write(file.toPath(), yaml.getBytes(Charset.forName("UTF-8")));
        return file;
    }

    private YamlConfiguration load(String yaml) throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(yaml);
        return config;
    }

    private List<String> read(File file) {
        assertTrue(file + " was not found; tests are expected to run from the project root", file.isFile());
        try {
            return Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new AssertionError("could not read " + file, e);
        }
    }
}
