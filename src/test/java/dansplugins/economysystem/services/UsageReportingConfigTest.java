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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Pins the way the usage-reporting settings reach the plugin. Unlike every other key, the
 * three under {@code usage-reporting} are carried by the bundled {@code config.yml} as well
 * as being registered in ConfigService: a server upgraded from a version before usage
 * reporting keeps its existing {@code config.yml}, which has no such block, and the only
 * thing that makes those servers report is Bukkit falling through to the bundled file for
 * a key the file on disk lacks. That fall-through works for the one-argument getters and
 * not for the two-argument ones, so the getters are checked at the source level the same
 * way ConfigDefaultsTest checks the registrations.
 */
public class UsageReportingConfigTest {

    private static final File CONFIG_SERVICE =
            new File("src/main/java/dansplugins/economysystem/services/ConfigService.java");

    private static final Pattern REGISTRATION =
            Pattern.compile("addDefault\\(\"(usage-reporting\\.[^\"]+)\",\\s*\"?([^\"\\)]*)\"?\\);");
    private static final Pattern USAGE_REPORTING_READ =
            // the keys are read through the USAGE_REPORTING_* constants, or by literal
            Pattern.compile("get(?:String|Boolean|Int)\\((?:USAGE_REPORTING_[A-Z_]+|\"usage-reporting\\.[^\"]+\")([^)]*)\\)");

    @Test
    public void bundledConfig_carriesAnEnabledBlockWithEndpointAndKey() {
        YamlConfiguration bundled = bundledConfig();
        assertTrue(bundled.getBoolean("usage-reporting.enabled"));
        assertEquals("https://trace.danielstephenson.dev", bundled.getString("usage-reporting.endpoint"));
        String key = bundled.getString("usage-reporting.key");
        assertNotNull("no key is bundled, so no installation would ever report", key);
        assertFalse("the bundled key is blank, which the client treats as off", key.trim().isEmpty());
    }

    @Test
    public void bundledConfig_agreesWithTheConfigServiceRegistrations() {
        YamlConfiguration bundled = bundledConfig();
        int registrations = 0;
        for (String line : read(CONFIG_SERVICE)) {
            Matcher matcher = REGISTRATION.matcher(line);
            if (matcher.find()) {
                registrations++;
                assertEquals(matcher.group(1) + " is registered with a value the bundled config.yml does not carry",
                        matcher.group(2), String.valueOf(bundled.get(matcher.group(1))));
            }
        }
        assertEquals("usage-reporting keys registered in ConfigService", 3, registrations);
    }

    /**
     * Measured against YamlConfiguration rather than assumed: a file without the block reads
     * the bundled key through the one-argument getter, and the explicit fallback through the
     * two-argument one -- which is why ConfigService must never use the latter for these keys.
     */
    @Test
    public void aConfigWithoutTheBlock_readsTheBundledKeyOnlyThroughTheOneArgumentGetter() throws Exception {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.loadFromString("version: v1.2.0\nenablingText: Medieval Economy is enabling...\n");
        onDisk.setDefaults(bundledConfig());

        assertEquals(bundledConfig().getString("usage-reporting.key"), onDisk.getString("usage-reporting.key"));
        assertTrue(onDisk.getBoolean("usage-reporting.enabled"));
        assertEquals("", onDisk.getString("usage-reporting.key", ""));
        assertFalse(onDisk.getBoolean("usage-reporting.enabled", false));
    }

    /**
     * The mechanics ConfigService.ensureDefaultsOnDisk() relies on, measured against
     * YamlConfiguration: with the bundled file registered as defaults, isSet() is false for a
     * block that is only in the defaults, and copying the three values across makes the block
     * part of what a save writes -- with the bundled key, not a fresh literal.
     * ConfigDefaultsOnDiskTest covers the decision of when that write happens.
     */
    @Test
    public void aConfigWithoutTheBlock_isNotSetUntilTheDefaultsAreCopiedOntoIt() throws Exception {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.loadFromString("version: v1.2.0\nenablingText: Medieval Economy is enabling...\n");
        onDisk.setDefaults(bundledConfig());

        assertFalse("a block present only in the defaults must not count as set", onDisk.isSet("usage-reporting"));
        assertFalse(onDisk.saveToString().contains("usage-reporting"));

        for (String key : new String[] {"usage-reporting.enabled", "usage-reporting.endpoint", "usage-reporting.key"}) {
            onDisk.set(key, onDisk.getDefaults().get(key));
        }

        assertTrue(onDisk.isSet("usage-reporting"));
        String written = onDisk.saveToString();
        assertTrue(written.contains("usage-reporting:"));
        assertTrue(written.contains(bundledConfig().getString("usage-reporting.key")));
        assertTrue(written.contains("enabled: true"));
        assertTrue(written.contains("endpoint: https://trace.danielstephenson.dev"));
    }

    @Test
    public void configService_readsEveryUsageReportingKeyWithTheOneArgumentGetter() {
        int reads = 0;
        for (String line : read(CONFIG_SERVICE)) {
            Matcher matcher = USAGE_REPORTING_READ.matcher(line);
            while (matcher.find()) {
                reads++;
                assertEquals("a usage-reporting key is read with an explicit fallback, which hides the bundled default: "
                        + line.trim(), "", matcher.group(1).trim());
            }
        }
        // the switch itself is read through UsageReportingSetting (see the test below)
        assertEquals("usage-reporting keys read in ConfigService", 2, reads);
    }

    /**
     * getBoolean() reads a quoted "false" as the default, true; UsageReportingSettingTest covers
     * what isEnabled() does instead. This pins that ConfigService uses it.
     */
    @Test
    public void configService_readsTheSwitchThroughUsageReportingSetting() {
        String source = String.join("\n", read(CONFIG_SERVICE));
        assertTrue(source.contains("UsageReportingSetting.isEnabled(medievalEconomy.getConfig(),"));
        assertFalse(source.contains("getBoolean(USAGE_REPORTING_ENABLED_KEY)"));
    }

    /**
     * A config.yml Bukkit cannot parse is loaded as empty, so it has no version and the version
     * mismatch path would move it aside and write the defaults, usage reporting on, in its place.
     */
    @Test
    public void theVersionMismatchPath_leavesAFileItCannotParseAlone() {
        String source = String.join("\n", read(CONFIG_SERVICE));
        int method = source.indexOf("public void renameConfigToConfigDotOldAndSaveDefaults()");
        int guard = source.indexOf("UsageReportingSetting.isUnparseable(saveFile)", method);
        int keep = source.indexOf("UsageReportingSetting.keepOptOutFromDisk(medievalEconomy.getConfig(), saveFile)", method);
        int rename = source.indexOf("saveFile.renameTo(", method);
        assertTrue("the unparseable check must come before the file is renamed", guard > method && guard < rename);
        assertTrue("an opt-out must be carried over before the file is renamed", keep > method && keep < rename);
    }

    /** Turning reporting off and running /econ reload stops it at once. */
    @Test
    public void reload_stopsUsageReportingWhenItIsNowOff() {
        String source = String.join("\n", read(CONFIG_SERVICE));
        int method = source.indexOf("public boolean reload()");
        int stop = source.indexOf("medievalEconomy.stopUsageReporting();", method);
        int end = source.indexOf("return true;", method);
        assertTrue(stop > method && stop < end);
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
