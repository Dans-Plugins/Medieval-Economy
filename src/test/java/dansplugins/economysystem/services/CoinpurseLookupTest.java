package dansplugins.economysystem.services;

import dansplugins.economysystem.objects.Coinpurse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the coinpurse lookup shared by DeathListener, /balance, /deposit and /withdraw.
 *
 * UtilityService itself cannot be exercised here — its instance methods read the coinpurse list
 * through a live MedievalEconomy — so the lookup is tested through the static helpers those
 * methods delegate to, the same way AmountArgumentTest reaches the rule the command handlers
 * enforce. Coinpurse takes the plugin only for its file and config work, so null stands in for it.
 */
public class CoinpurseLookupTest {

    private Coinpurse purseFor(UUID uuid) {
        Coinpurse purse = new Coinpurse(null);
        purse.setPlayerUUID(uuid);
        return purse;
    }

    @Test
    public void findCoinpurse_withMatchingPurse_returnsThatPurse() {
        UUID wanted = UUID.randomUUID();
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();
        coinpurses.add(purseFor(UUID.randomUUID()));
        Coinpurse expected = purseFor(wanted);
        coinpurses.add(expected);

        assertSame(expected, UtilityService.findCoinpurse(coinpurses, wanted));
    }

    @Test
    public void findCoinpurse_withNoMatchingPurse_returnsNull() {
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();
        coinpurses.add(purseFor(UUID.randomUUID()));

        assertNull(UtilityService.findCoinpurse(coinpurses, UUID.randomUUID()));
    }

    /**
     * Coinpurse.legacyLoad takes its UUID from findUUIDBasedOnPlayerName, which returns null for a
     * name the server no longer knows. Such a purse in the list used to make every later lookup
     * throw, since the comparison started from the stored UUID.
     */
    @Test
    public void findCoinpurse_withPurseCarryingNullUUID_skipsItAndFindsTheMatch() {
        UUID wanted = UUID.randomUUID();
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();
        coinpurses.add(purseFor(null));
        Coinpurse expected = purseFor(wanted);
        coinpurses.add(expected);

        assertSame(expected, UtilityService.findCoinpurse(coinpurses, wanted));
    }

    @Test
    public void findCoinpurse_withPurseCarryingNullUUIDAndNoMatch_returnsNull() {
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();
        coinpurses.add(purseFor(null));

        assertNull(UtilityService.findCoinpurse(coinpurses, UUID.randomUUID()));
    }

    @Test
    public void findCoinpurse_withNullUUID_returnsNull() {
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();
        coinpurses.add(purseFor(null));

        assertNull(UtilityService.findCoinpurse(coinpurses, null));
    }

    @Test
    public void findOrCreateCoinpurse_withExistingPurse_returnsItAndAddsNothing() {
        UUID wanted = UUID.randomUUID();
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();
        Coinpurse expected = purseFor(wanted);
        expected.setCoins(25);
        coinpurses.add(expected);

        assertSame(expected, UtilityService.findOrCreateCoinpurse(coinpurses, wanted, null));
        assertEquals(1, coinpurses.size());
        assertEquals(25, expected.getCoins());
    }

    @Test
    public void findOrCreateCoinpurse_withNoPurse_registersAnEmptyOneForThatPlayer() {
        UUID wanted = UUID.randomUUID();
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();

        Coinpurse created = UtilityService.findOrCreateCoinpurse(coinpurses, wanted, null);

        assertNotNull(created);
        assertEquals(wanted, created.getPlayerUUID());
        assertEquals(0, created.getCoins());
        assertEquals(1, coinpurses.size());
        assertSame(created, coinpurses.get(0));
    }

    @Test
    public void findOrCreateCoinpurse_calledTwice_returnsTheSamePurse() {
        UUID wanted = UUID.randomUUID();
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();

        Coinpurse first = UtilityService.findOrCreateCoinpurse(coinpurses, wanted, null);
        first.addCoins(10);
        Coinpurse second = UtilityService.findOrCreateCoinpurse(coinpurses, wanted, null);

        assertSame(first, second);
        assertEquals(10, second.getCoins());
        assertEquals(1, coinpurses.size());
    }

    /**
     * A purse registered under a null UUID would be unreachable by lookup and would save itself to
     * a file named "null.txt", so it is handed back without being added to the list.
     */
    @Test
    public void findOrCreateCoinpurse_withNullUUID_returnsAnUnregisteredPurse() {
        List<Coinpurse> coinpurses = new ArrayList<Coinpurse>();

        Coinpurse created = UtilityService.findOrCreateCoinpurse(coinpurses, null, null);

        assertNotNull(created);
        assertEquals(0, created.getCoins());
        assertTrue(coinpurses.isEmpty());
    }
}
