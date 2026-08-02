package dansplugins.economysystem.objects;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Characterization tests for {@link Coinpurse}'s in-memory balance logic.
 * save()/load() are excluded here since they perform real file I/O and
 * config lookups against a Bukkit plugin instance.
 */
public class CoinpurseTest {

    private Coinpurse coinpurse;

    @Before
    public void setUp() {
        coinpurse = new Coinpurse(null);
    }

    @Test
    public void newCoinpurse_startsWithZeroCoinsAndNoUUID() {
        assertEquals(0, coinpurse.getCoins());
        assertNull(coinpurse.getPlayerUUID());
    }

    @Test
    public void setPlayerUUID_thenGetPlayerUUID_returnsSameValue() {
        UUID id = UUID.randomUUID();
        coinpurse.setPlayerUUID(id);
        assertEquals(id, coinpurse.getPlayerUUID());
    }

    @Test
    public void setCoins_thenGetCoins_returnsSameValue() {
        coinpurse.setCoins(42);
        assertEquals(42, coinpurse.getCoins());
    }

    @Test
    public void addCoins_increasesBalance() {
        coinpurse.setCoins(10);
        coinpurse.addCoins(5);
        assertEquals(15, coinpurse.getCoins());
    }

    @Test
    public void removeCoins_withSufficientBalance_decreasesBalanceAndReturnsTrue() {
        coinpurse.setCoins(10);
        boolean result = coinpurse.removeCoins(4);
        assertTrue(result);
        assertEquals(6, coinpurse.getCoins());
    }

    @Test
    public void removeCoins_withExactBalance_leavesZeroAndReturnsTrue() {
        coinpurse.setCoins(10);
        boolean result = coinpurse.removeCoins(10);
        assertTrue(result);
        assertEquals(0, coinpurse.getCoins());
    }

    @Test
    public void removeCoins_withInsufficientBalance_leavesBalanceUnchangedAndReturnsFalse() {
        coinpurse.setCoins(3);
        boolean result = coinpurse.removeCoins(4);
        assertFalse(result);
        assertEquals(3, coinpurse.getCoins());
    }

    @Test
    public void containsAtLeast_withEqualOrLowerBalance_returnsTrue() {
        coinpurse.setCoins(5);
        assertTrue(coinpurse.containsAtLeast(5));
        assertTrue(coinpurse.containsAtLeast(0));
    }

    @Test
    public void containsAtLeast_withInsufficientBalance_returnsFalse() {
        coinpurse.setCoins(5);
        assertFalse(coinpurse.containsAtLeast(6));
    }
}
