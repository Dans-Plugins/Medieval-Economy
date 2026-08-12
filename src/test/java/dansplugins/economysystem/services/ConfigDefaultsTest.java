package dansplugins.economysystem.services;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Guards the configuration surface against the two ways it has drifted before: a key
 * registered twice (where the later registration silently overwrites the earlier one, so
 * the value a generated config.yml receives is not the value anyone read in the source),
 * and CONFIG.md describing keys or defaults the plugin does not actually register.
 *
 * ConfigService cannot be executed here — saveConfigDefaults needs a live plugin instance
 * for getConfig() — so its registrations are read from the source file, the same way
 * PluginDescriptionTest reads plugin.yml rather than constructing a PluginDescriptionFile.
 * Defaults that are not written as string literals (the plugin version, the titleSeparator
 * boolean) are checked for presence only, not for value.
 */
public class ConfigDefaultsTest {

    private static final File CONFIG_SERVICE =
            new File("src/main/java/dansplugins/economysystem/services/ConfigService.java");
    private static final File CONFIG_DOCUMENTATION = new File("CONFIG.md");

    private static final Pattern REGISTRATION = Pattern.compile("addDefault\\(\"([^\"]+)\",(.*)\\);");
    private static final Pattern STRING_LITERAL = Pattern.compile("\"([^\"]*)\"");

    @Test
    public void noKeyIsRegisteredTwice() {
        Set<String> seen = new LinkedHashSet<String>();
        List<String> duplicates = new ArrayList<String>();
        for (String key : registrationOrder()) {
            if (!seen.add(key)) {
                duplicates.add(key);
            }
        }
        assertEquals("keys registered more than once in ConfigService", new ArrayList<String>(), duplicates);
    }

    @Test
    public void everyRegisteredKeyIsDocumented() {
        assertEquals("keys registered in ConfigService but missing from CONFIG.md",
                new TreeSet<String>(), missing(registeredDefaults().keySet(), documentedDefaults().keySet()));
    }

    @Test
    public void everyDocumentedKeyIsRegistered() {
        assertEquals("keys documented in CONFIG.md but never registered in ConfigService",
                new TreeSet<String>(), missing(documentedDefaults().keySet(), registeredDefaults().keySet()));
    }

    @Test
    public void everyDocumentedDefaultMatchesTheRegisteredValue() {
        Map<String, String> registered = registeredDefaults();
        for (Map.Entry<String, String> documented : documentedDefaults().entrySet()) {
            String value = registered.get(documented.getKey());
            if (value == null || documented.getValue() == null) {
                // absent keys are reported by the key-set tests; unquoted defaults are not compared
                continue;
            }
            assertEquals(documented.getKey() + " is documented with a default the plugin does not register",
                    documented.getValue(), value);
        }
    }

    private Set<String> missing(Set<String> expected, Set<String> actual) {
        Set<String> difference = new TreeSet<String>(expected);
        difference.removeAll(actual);
        return difference;
    }

    /**
     * @return every key passed to addDefault, in source order, duplicates included
     */
    private List<String> registrationOrder() {
        List<String> keys = new ArrayList<String>();
        for (String line : read(CONFIG_SERVICE)) {
            Matcher matcher = REGISTRATION.matcher(line);
            if (matcher.find()) {
                keys.add(matcher.group(1));
            }
        }
        assertTrue("no addDefault calls were found in " + CONFIG_SERVICE, keys.size() > 0);
        return keys;
    }

    /**
     * @return each registered key against its default, or null where the default is not written
     *         as string literals
     */
    private Map<String, String> registeredDefaults() {
        Map<String, String> defaults = new LinkedHashMap<String, String>();
        for (String line : read(CONFIG_SERVICE)) {
            Matcher matcher = REGISTRATION.matcher(line);
            if (matcher.find()) {
                defaults.put(matcher.group(1), literalValue(matcher.group(2)));
            }
        }
        return defaults;
    }

    /**
     * Joins the string literals a default is written as, so that a value split across a
     * concatenation reads the same as the single string it produces.
     */
    private String literalValue(String argument) {
        StringBuilder value = new StringBuilder();
        Matcher matcher = STRING_LITERAL.matcher(argument);
        boolean found = false;
        while (matcher.find()) {
            value.append(matcher.group(1));
            found = true;
        }
        return found ? value.toString() : null;
    }

    /**
     * @return each key documented in the CONFIG.md options table against its documented
     *         default, or null where that default is not written as a backticked literal
     */
    private Map<String, String> documentedDefaults() {
        Map<String, String> defaults = new LinkedHashMap<String, String>();
        for (String line : read(CONFIG_DOCUMENTATION)) {
            String[] cells = line.split("\\|", -1);
            if (cells.length < 4) {
                continue;
            }
            String key = backtickedValue(cells[1]);
            if (key == null) {
                // the header and separator rows carry no backticked key
                continue;
            }
            defaults.put(key, backtickedValue(cells[2]));
        }
        assertTrue("no documented options were found in " + CONFIG_DOCUMENTATION, defaults.size() > 0);
        return defaults;
    }

    /**
     * Reads between the outermost backticks so that a default's leading or trailing spaces,
     * which several message fragments depend on, survive the comparison.
     */
    private String backtickedValue(String cell) {
        int start = cell.indexOf('`');
        int end = cell.lastIndexOf('`');
        if (start == -1 || end == start) {
            return null;
        }
        return cell.substring(start + 1, end);
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
