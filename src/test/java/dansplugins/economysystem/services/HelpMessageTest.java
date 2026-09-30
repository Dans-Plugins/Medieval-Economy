package dansplugins.economysystem.services;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests which commands /econ help lists for a given set of permissions. The menu used to list
 * only /econ help and createcurrency, leaving out every command a player holds by default.
 *
 * UtilityService itself cannot be exercised here — it takes a live MedievalEconomy — so the menu
 * is tested through the static helper sendHelpMessage delegates to, the same way
 * CoinpurseLookupTest reaches the lookup. The permission check is supplied as a set of held nodes.
 */
public class HelpMessageTest {

    private List<String> linesFor(String... heldNodes) {
        Set<String> held = new HashSet<>(Arrays.asList(heldNodes));
        return UtilityService.composeHelpLines(held::contains);
    }

    private boolean lists(List<String> lines, String command) {
        for (String line : lines) {
            if (line.contains(command + " ")) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void composeHelpLines_withNoPermissions_listsOnlyHelp() {
        List<String> lines = linesFor();

        assertEquals(1, lines.size());
        assertTrue(lists(lines, "/econ help"));
    }

    @Test
    public void composeHelpLines_withDefault_listsThePlayerCommands() {
        List<String> lines = linesFor("medievaleconomy.default");

        assertTrue(lists(lines, "/balance"));
        assertTrue(lists(lines, "/deposit"));
        assertTrue(lists(lines, "/withdraw"));
        assertFalse(lists(lines, "/econ createcurrency"));
        assertFalse(lists(lines, "/econ reload"));
    }

    @Test
    public void composeHelpLines_withASingleChildNode_listsOnlyThatCommand() {
        List<String> lines = linesFor("medievaleconomy.deposit");

        assertTrue(lists(lines, "/deposit"));
        assertFalse(lists(lines, "/balance"));
        assertFalse(lists(lines, "/withdraw"));
    }

    /**
     * EconCommand accepts .admin in place of the createcurrency and reload nodes, so an admin
     * whose child nodes are negated can still run both and should see both listed.
     */
    @Test
    public void composeHelpLines_withAdminAlone_listsTheAdminCommands() {
        List<String> lines = linesFor("medievaleconomy.admin");

        assertTrue(lists(lines, "/econ createcurrency"));
        assertTrue(lists(lines, "/econ reload"));
    }

    @Test
    public void composeHelpLines_withReload_listsReloadButNotCreateCurrency() {
        List<String> lines = linesFor("medievaleconomy.reload");

        assertTrue(lists(lines, "/econ reload"));
        assertFalse(lists(lines, "/econ createcurrency"));
    }
}
