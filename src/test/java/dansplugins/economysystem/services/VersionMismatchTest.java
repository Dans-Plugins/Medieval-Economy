package dansplugins.economysystem.services;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Pins the write ConfigService.renameConfigToConfigDotOldAndSaveDefaults() makes after a
 * plugin version change. That write used to carry the previous version string, because
 * addDefault never overrides a value the loaded file already holds, so the mismatch was
 * detected again -- and config.yml.old overwritten again -- on every subsequent enable.
 *
 * ConfigService cannot be executed against a live plugin here, so the write is measured
 * against YamlConfiguration and the explicit set() is checked at the source level, the way
 * ConfigDefaultsOnDiskTest checks the wiring in MedievalEconomy.onEnable().
 */
public class VersionMismatchTest {

    private static final File CONFIG_SERVICE =
            new File("src/main/java/dansplugins/economysystem/services/ConfigService.java");

    @Test
    public void aDefaultAlone_leavesTheOldVersionInTheWrittenFile() throws Exception {
        YamlConfiguration loaded = load("version: v1.2.0\nenablingText: custom\n");
        loaded.addDefault("version", "v2.0.0");
        loaded.options().copyDefaults(true);

        assertEquals("v1.2.0", load(loaded.saveToString()).getString("version"));
    }

    @Test
    public void settingTheVersion_writesTheNewVersionAndKeepsOperatorValues() throws Exception {
        YamlConfiguration loaded = load("version: v1.2.0\nenablingText: custom\n");
        loaded.set("version", "v2.0.0");
        loaded.addDefault("version", "v2.0.0");
        loaded.addDefault("enablingText", "Medieval Economy is enabling...");
        loaded.addDefault("balanceNoCoinpurse", "No coinpurse could be found for you.");
        loaded.options().copyDefaults(true);

        YamlConfiguration written = load(loaded.saveToString());
        assertEquals("v2.0.0", written.getString("version"));
        assertEquals("custom", written.getString("enablingText"));
        assertEquals("No coinpurse could be found for you.", written.getString("balanceNoCoinpurse"));
    }

    @Test
    public void theRenamePath_setsTheVersionBeforeSavingTheDefaults() {
        String method = methodBody("renameConfigToConfigDotOldAndSaveDefaults");
        int set = method.indexOf("getConfig().set(\"version\", medievalEconomy.getVersion())");
        int save = method.indexOf("saveConfigDefaults()");

        assertTrue("renameConfigToConfigDotOldAndSaveDefaults() no longer sets the version explicitly, so the"
                + " old version string is written back and the mismatch recurs on every enable", set >= 0);
        assertTrue("the version must be set before saveConfigDefaults() writes the file", save > set);
    }

    private String methodBody(String name) {
        StringBuilder body = new StringBuilder();
        boolean inside = false;
        for (String line : read(CONFIG_SERVICE)) {
            if (line.contains("public void " + name + "(")) {
                inside = true;
            } else if (inside && line.startsWith("    public ")) {
                break;
            }
            if (inside) {
                body.append(line).append('\n');
            }
        }
        assertTrue(name + " was not found in " + CONFIG_SERVICE, body.length() > 0);
        return body.toString();
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
