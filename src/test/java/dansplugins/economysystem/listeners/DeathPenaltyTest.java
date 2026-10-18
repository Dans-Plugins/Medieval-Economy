package dansplugins.economysystem.listeners;

import dansplugins.economysystem.objects.Coinpurse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Characterization tests for the coins a player loses on death, as described under
 * "Death Penalty" in USER_GUIDE.md: 10% of the coinpurse, rounded down, or 1 coin if the
 * balance is below 10.
 *
 * DeathListener itself cannot be constructed here — it takes MedievalEconomy, which extends
 * JavaPlugin and refuses to be instantiated outside a running server — so the penalty is taken
 * by a static helper the handler delegates to, the same way BalanceMessageTest reaches the
 * /balance message. Coinpurse takes the plugin only for its file and config work, so null
 * stands in for it.
 */
public class DeathPenaltyTest {

    private Coinpurse purseHolding(int coins) {
        Coinpurse purse = new Coinpurse(null);
        purse.setCoins(coins);
        return purse;
    }

    /**
     * The handler drops nothing and sends no death message when 0 is returned, so this is the
     * case that keeps a player with an empty coinpurse from being told it feels lighter.
     */
    @Test
    public void takeDeathPenalty_withAnEmptyCoinpurse_takesNothing() {
        Coinpurse purse = purseHolding(0);

        assertEquals(0, DeathListener.takeDeathPenalty(purse));
        assertEquals(0, purse.getCoins());
    }

    @Test
    public void takeDeathPenalty_withOneCoin_takesTheLastCoin() {
        Coinpurse purse = purseHolding(1);

        assertEquals(1, DeathListener.takeDeathPenalty(purse));
        assertEquals(0, purse.getCoins());
    }

    @Test
    public void takeDeathPenalty_belowTenCoins_takesOneCoin() {
        Coinpurse purse = purseHolding(9);

        assertEquals(1, DeathListener.takeDeathPenalty(purse));
        assertEquals(8, purse.getCoins());
    }

    @Test
    public void takeDeathPenalty_withExactlyTenCoins_takesTenPercent() {
        Coinpurse purse = purseHolding(10);

        assertEquals(1, DeathListener.takeDeathPenalty(purse));
        assertEquals(9, purse.getCoins());
    }

    @Test
    public void takeDeathPenalty_roundsTenPercentDown() {
        Coinpurse purse = purseHolding(19);

        assertEquals(1, DeathListener.takeDeathPenalty(purse));
        assertEquals(18, purse.getCoins());
    }

    @Test
    public void takeDeathPenalty_withALargeBalance_takesTenPercent() {
        Coinpurse purse = purseHolding(1234);

        assertEquals(123, DeathListener.takeDeathPenalty(purse));
        assertEquals(1111, purse.getCoins());
    }

    /**
     * The coins dropped on the ground are created from the returned amount, so what is removed
     * from the coinpurse and what is returned must agree — otherwise a death would create or
     * destroy currency.
     */
    @Test
    public void takeDeathPenalty_removesExactlyTheAmountItReturns() {
        for (int coins = 0; coins <= 1000; coins++) {
            Coinpurse purse = purseHolding(coins);

            int dropped = DeathListener.takeDeathPenalty(purse);

            assertEquals("balance " + coins, coins, purse.getCoins() + dropped);
        }
    }
}
