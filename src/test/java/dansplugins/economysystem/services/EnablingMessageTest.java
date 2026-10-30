package dansplugins.economysystem.services;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Pins the order MedievalEconomy.onEnable() reads its first messages in. enablingText (and
 * compatibilityText, during a migration) used to be read before any default was registered,
 * and the bundled config.yml carries no message keys, so on a fresh install the console line
 * read "null".
 *
 * Registering the defaults first also registers the running version as the default for
 * "version", which would hide a file with no version line from the version check; so both
 * version checks are pinned to the two-argument getter, which does not fall through.
 *
 * MedievalEconomy cannot be constructed here, so the getter behavior is measured against
 * YamlConfiguration and the wiring is checked at the source level, as ConfigDefaultsOnDiskTest
 * and VersionMismatchTest do.
 */
public class EnablingMessageTest {

    private static final File CONFIG_SERVICE =
            new File("src/main/java/dansplugins/economysystem/services/ConfigService.java");
    private static final File MEDIEVAL_ECONOMY =
            new File("src/main/java/dansplugins/economysystem/MedievalEconomy.java");

    @Test
    public void withNoConfigOnDisk_theEnablingMessageReadsBackAsNullUntilTheDefaultsAreRegistered() {
        YamlConfiguration fresh = new YamlConfiguration();
        fresh.setDefaults(bundledConfig());

        assertNull(fresh.getString("enablingText"));
        fresh.addDefault("enablingText", "Medieval Economy is enabling...");
        assertEquals("Medieval Economy is enabling...", fresh.getString("enablingText"));
    }

    @Test
    public void onEnable_registersTheDefaultsBeforeTheFirstMessageIsRead() {
        String source = String.join("\n", read(MEDIEVAL_ECONOMY));
        int register = source.indexOf("configService.registerDefaults()");
        int enablingText = source.indexOf("getString(\"enablingText\")");
        int migration = source.indexOf("utilityService.ensureSmoothTransitionBetweenVersions()");

        assertTrue("MedievalEconomy.onEnable() no longer registers the defaults itself", register >= 0);
        assertTrue("enablingText is read before the defaults are registered, so it prints null on a fresh install",
                enablingText > register);
        assertTrue("the migration prints compatibilityText, so it must run after the defaults are registered",
                migration > register);
    }

    @Test
    public void aRegisteredVersionDefault_isHiddenOnlyFromTheTwoArgumentGetter() throws Exception {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.loadFromString("enablingText: custom\n");
        onDisk.addDefault("version", "v2.3.2");

        assertEquals("v2.3.2", onDisk.getString("version"));
        assertNull(onDisk.getString("version", null));
    }

    @Test
    public void bothVersionChecks_readTheVersionWithoutTheDefault() {
        for (File source : new File[] {CONFIG_SERVICE, MEDIEVAL_ECONOMY}) {
            for (String line : read(source)) {
                assertFalse(source + " reads the configured version through the one-argument getter, which returns"
                        + " the registered default for a config.yml without a version line: " + line.trim(),
                        line.contains("getString(\"version\")"));
            }
        }
    }

    private YamlConfiguration bundledConfig() {
        YamlConfiguration bundled = new YamlConfiguration();
        try (InputStream stream = getClass().getResourceAsStream("/config.yml")) {
            assertNotNull("config.yml was not found on the test classpath", stream);
            bundled.load(new InputStreamReader(stream, "UTF-8"));
        } catch (Exception e) {
            throw new AssertionError("could not read the bundled config.yml", e);
        }
        return bundled;
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
