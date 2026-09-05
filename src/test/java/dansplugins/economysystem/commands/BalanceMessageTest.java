package dansplugins.economysystem.commands;

import dansplugins.economysystem.objects.Coinpurse;
import org.bukkit.ChatColor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests what /balance answers with, including the case that used to produce no answer at all:
 * a player whose coinpurse lookup returns null. That state is reachable for a player whose
 * record went missing between joining and running the command.
 *
 * BalanceCommand itself cannot be constructed here — it takes MedievalEconomy, which extends
 * JavaPlugin and refuses to be instantiated outside a running server — so the message is built
 * by a static helper the handler delegates to, the same way CoinpurseLookupTest reaches the
 * lookup behind UtilityService. Coinpurse takes the plugin only for its file and config work,
 * so null stands in for it.
 */
public class BalanceMessageTest {

    private static final String START = "You have ";
    private static final String END = " coins in your coinpurse.";
    private static final String NO_COINPURSE = "No coinpurse could be found for you.";

    private Coinpurse purseHolding(int coins) {
        Coinpurse purse = new Coinpurse(null);
        purse.setCoins(coins);
        return purse;
    }

    @Test
    public void composeBalanceMessage_withCoins_reportsTheBalance() {
        assertEquals(ChatColor.GREEN + START + "42" + END,
                BalanceCommand.composeBalanceMessage(purseHolding(42), START, END, NO_COINPURSE));
    }

    @Test
    public void composeBalanceMessage_withAnEmptyCoinpurse_reportsZero() {
        assertEquals(ChatColor.GREEN + START + "0" + END,
                BalanceCommand.composeBalanceMessage(purseHolding(0), START, END, NO_COINPURSE));
    }

    /**
     * The handler used to fall out of the method here, sending nothing. Silence is
     * indistinguishable from the command having failed to register.
     */
    @Test
    public void composeBalanceMessage_withNoCoinpurse_reportsThatOneIsMissing() {
        assertEquals(ChatColor.RED + NO_COINPURSE,
                BalanceCommand.composeBalanceMessage(null, START, END, NO_COINPURSE));
    }

    /**
     * Holding nothing and holding no coinpurse are different situations, and the point of the
     * configurable message is that a player can tell which one they are in.
     */
    @Test
    public void composeBalanceMessage_tellsAnEmptyCoinpurseApartFromAMissingOne() {
        String empty = BalanceCommand.composeBalanceMessage(purseHolding(0), START, END, NO_COINPURSE);
        String missing = BalanceCommand.composeBalanceMessage(null, START, END, NO_COINPURSE);

        assertFalse("an empty coinpurse and a missing one produce the same message", empty.equals(missing));
    }
}
