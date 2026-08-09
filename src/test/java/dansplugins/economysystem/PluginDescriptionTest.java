package dansplugins.economysystem;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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

    private static final List<String> COMMAND_NAMES = Arrays.asList("econ", "balance", "deposit", "withdraw");

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
    public void everyCommand_isDeclaredWithADescriptionAndUsage() {
        for (String name : COMMAND_NAMES) {
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
    public void noCommand_isGatedAtTheManifestLevel() {
        for (String name : COMMAND_NAMES) {
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
        assertPermissionDefault("medievaleconomy.default", "true");
        assertPermissionDefault("medievaleconomy.balance", "true");
        assertPermissionDefault("medievaleconomy.deposit", "true");
        assertPermissionDefault("medievaleconomy.withdraw", "true");
        assertPermissionDefault("medievaleconomy.createcurrency", "op");
        assertPermissionDefault("medievaleconomy.reload", "op");
        assertPermissionDefault("medievaleconomy.admin", "op");
    }

    @Test
    public void ordinaryPlayers_holdTheEverydayNodesAndNotTheAdministrativeOnes() {
        assertGrantedToNonOp("medievaleconomy.default", true);
        assertGrantedToNonOp("medievaleconomy.balance", true);
        assertGrantedToNonOp("medievaleconomy.deposit", true);
        assertGrantedToNonOp("medievaleconomy.withdraw", true);
        assertGrantedToNonOp("medievaleconomy.createcurrency", false);
        assertGrantedToNonOp("medievaleconomy.reload", false);
        assertGrantedToNonOp("medievaleconomy.admin", false);
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
        assertNotNull("/" + name + " is not declared in plugin.yml", command);
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

    private void assertPermissionDefault(String name, String expected) {
        assertEquals(name + " has the wrong default", expected,
                String.valueOf(permission(name).get("default")));
    }

    private void assertGrantedToNonOp(String name, boolean expected) {
        boolean granted = "true".equals(String.valueOf(permission(name).get("default")));
        assertEquals(name + " is granted to ordinary players", expected, granted);
    }

    private void assertGrantedBy(String parentName, String targetName) {
        assertTrue(targetName + " is not granted by " + parentName, grantedBy(parentName, targetName));
    }

    private boolean grantedBy(String parentName, String targetName) {
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
            if (child.equals(targetName) || grantedBy(child, targetName)) {
                return true;
            }
        }
        return false;
    }
}
