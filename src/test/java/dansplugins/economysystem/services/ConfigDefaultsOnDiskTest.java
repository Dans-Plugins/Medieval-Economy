package dansplugins.economysystem.services;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

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
 * the registrations.
 */
public class ConfigDefaultsOnDiskTest {

    private static final File MEDIEVAL_ECONOMY =
            new File("src/main/java/dansplugins/economysystem/MedievalEconomy.java");

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
