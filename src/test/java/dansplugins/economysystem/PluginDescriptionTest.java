package dansplugins.economysystem;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Characterization tests for the command and permission surface declared in
 * plugin.yml, read with the same YAML loader Bukkit itself uses. The Maven build
 * never reads plugin.yml, so without these the manifest has no automated coverage.
 *
 * PluginDescriptionFile is deliberately not used here: constructing a Permission
 * calls through to Bukkit.getPluginManager(), which requires a running server.
 */
public class PluginDescriptionTest {

    private static final Set<String> EXPECTED_COMMANDS =
            new TreeSet<String>(Arrays.asList("econ", "balance", "deposit", "withdraw"));

    private YamlConfiguration description;

    @Before
    public void setUp() throws Exception {
        description = new YamlConfiguration();
        // permission nodes contain dots, which the default path separator would treat as nesting
        description.options().pathSeparator('/');
        try (InputStream stream = getClass().getResourceAsStream("/plugin.yml")) {
            assertNotNull("plugin.yml was not found on the test classpath", stream);
            description.load(new InputStreamReader(stream, "UTF-8"));
        }
    }

    @Test
    public void mainClass_pointsAtThePluginEntryPoint() {
        assertEquals(MedievalEconomy.class.getName(), description.getString("main"));
    }

    @Test
    public void exactlyTheDocumentedCommands_areDeclared() {
        assertEquals(EXPECTED_COMMANDS, new TreeSet<String>(commands().getKeys(false)));
    }

    @Test
    public void everyDeclaredCommand_carriesADescriptionAndUsage() {
        for (String name : commands().getKeys(false)) {
            ConfigurationSection command = command(name);
            assertNotNull("/" + name + " has no description", command.getString("description"));
            assertNotNull("/" + name + " has no usage", command.getString("usage"));
        }
    }

    /**
     * Declaring a permission on the command entry as well would let Bukkit refuse the
     * command before the handler runs, suppressing the configurable refusal messages
     * the handlers send themselves.
     */
    @Test
    public void noDeclaredCommand_isGatedAtTheManifestLevel() {
        for (String name : commands().getKeys(false)) {
            assertNull("/" + name + " declares a permission", command(name).getString("permission"));
        }
    }

    /**
     * Bukkit resolves an undeclared node against Permission.DEFAULT_PERMISSION, which is
     * OP — so a node missing from plugin.yml silently becomes op-only no matter what the
     * documentation promises. Every node a handler checks must therefore be declared.
     */
    @Test
    public void everyDocumentedNode_isDeclaredWithItsDocumentedDefault() {
        assertDeclaredDefault("medievaleconomy.default", "true");
        assertDeclaredDefault("medievaleconomy.balance", "true");
        assertDeclaredDefault("medievaleconomy.deposit", "true");
        assertDeclaredDefault("medievaleconomy.withdraw", "true");
        assertDeclaredDefault("medievaleconomy.createcurrency", "op");
        assertDeclaredDefault("medievaleconomy.reload", "op");
        assertDeclaredDefault("medievaleconomy.admin", "op");
    }

    /**
     * The effective grant, resolved the way Bukkit resolves it: a node is held either
     * because its own default says so or because some node that is held grants it as a
     * child. This is the property players actually experience.
     */
    @Test
    public void ordinaryPlayers_holdTheEverydayNodesAndNotTheAdministrativeOnes() {
        assertHeldByOrdinaryPlayers("medievaleconomy.default", true);
        assertHeldByOrdinaryPlayers("medievaleconomy.balance", true);
        assertHeldByOrdinaryPlayers("medievaleconomy.deposit", true);
        assertHeldByOrdinaryPlayers("medievaleconomy.withdraw", true);
        assertHeldByOrdinaryPlayers("medievaleconomy.createcurrency", false);
        assertHeldByOrdinaryPlayers("medievaleconomy.reload", false);
        assertHeldByOrdinaryPlayers("medievaleconomy.admin", false);
    }

    @Test
    public void defaultNode_grantsBalanceDepositAndWithdraw() {
        assertGrantedBy("medievaleconomy.default", "medievaleconomy.balance");
        assertGrantedBy("medievaleconomy.default", "medievaleconomy.deposit");
        assertGrantedBy("medievaleconomy.default", "medievaleconomy.withdraw");
    }

    @Test
    public void adminNode_transitivelyGrantsEveryOtherNode() {
        for (String name : permissions().getKeys(false)) {
            if (name.equals("medievaleconomy.admin")) {
                continue;
            }
            assertGrantedBy("medievaleconomy.admin", name);
        }
    }

    private ConfigurationSection commands() {
        ConfigurationSection commands = description.getConfigurationSection("commands");
        assertNotNull("plugin.yml declares no commands block", commands);
        return commands;
    }

    private ConfigurationSection command(String name) {
        ConfigurationSection command = commands().getConfigurationSection(name);
        assertNotNull("/" + name + " has no configuration block in plugin.yml", command);
        return command;
    }

    private ConfigurationSection permissions() {
        ConfigurationSection permissions = description.getConfigurationSection("permissions");
        assertNotNull("plugin.yml declares no permissions block", permissions);
        return permissions;
    }

    private ConfigurationSection permission(String name) {
        ConfigurationSection node = permissions().getConfigurationSection(name);
        assertNotNull(name + " is not declared in plugin.yml", node);
        return node;
    }

    private String declaredDefault(String name) {
        return String.valueOf(permission(name).get("default"));
    }

    private void assertDeclaredDefault(String name, String expected) {
        assertEquals(name + " has the wrong default", expected, declaredDefault(name));
    }

    private void assertHeldByOrdinaryPlayers(String name, boolean expected) {
        assertEquals(name + " is held by ordinary players", expected, heldByOrdinaryPlayers(name));
    }

    private boolean heldByOrdinaryPlayers(String name) {
        if ("true".equals(declaredDefault(name))) {
            return true;
        }
        for (String other : permissions().getKeys(false)) {
            if (!other.equals(name)
                    && "true".equals(declaredDefault(other))
                    && grantedBy(other, name)) {
                return true;
            }
        }
        return false;
    }

    private void assertGrantedBy(String parentName, String targetName) {
        assertTrue(targetName + " is not granted by " + parentName, grantedBy(parentName, targetName));
    }

    private boolean grantedBy(String parentName, String targetName) {
        return grantedBy(parentName, targetName, new HashSet<String>());
    }

    private boolean grantedBy(String parentName, String targetName, Set<String> visited) {
        // a malformed manifest could declare a cycle; without this the recursion would
        // blow the stack instead of reporting a missing grant
        if (!visited.add(parentName)) {
            return false;
        }
        ConfigurationSection parent = permissions().getConfigurationSection(parentName);
        if (parent == null) {
            return false;
        }
        ConfigurationSection children = parent.getConfigurationSection("children");
        if (children == null) {
            return false;
        }
        for (String child : children.getKeys(false)) {
            if (!children.getBoolean(child)) {
                continue;
            }
            if (child.equals(targetName) || grantedBy(child, targetName, visited)) {
                return true;
            }
        }
        return false;
    }
}
