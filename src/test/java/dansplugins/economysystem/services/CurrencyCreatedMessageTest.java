package dansplugins.economysystem.services;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Tests what /econ createcurrency answers with. It used to report the requested amount no matter
 * how many coins fit in the inventory: Inventory.addItem hands back what it could not store, and
 * that was ignored, so "1000 currency created." could follow 64 coins actually being created.
 *
 * UtilityService itself cannot be exercised here — it takes a live MedievalEconomy — so the message
 * is tested through the static helpers addCurrencyToInventory delegates to, the same way
 * HelpMessageTest reaches the help menu.
 */
public class CurrencyCreatedMessageTest {

    @Test
    public void composeCurrencyCreatedMessage_whenEverythingFits_reportsTheRequestedAmount() {
        assertEquals(ChatColor.GREEN + "1000 currency created.",
                UtilityService.composeCurrencyCreatedMessage(1000, 0));
    }

    @Test
    public void composeCurrencyCreatedMessage_whenSomeDoesNotFit_reportsOnlyWhatWasStored() {
        assertEquals(ChatColor.YELLOW + "64 currency created. 936 did not fit in your inventory and were not created.",
                UtilityService.composeCurrencyCreatedMessage(1000, 936));
    }

    @Test
    public void countItems_withNothingLeftOver_isZero() {
        assertEquals(0, UtilityService.countItems(Collections.<ItemStack>emptyList()));
    }

    /**
     * addItem returns one leftover stack per argument it was given, but an oversized stack is not
     * guaranteed to come back as a single stack, so every leftover stack is counted.
     */
    @Test
    public void countItems_sumsEveryLeftoverStack() {
        assertEquals(936, UtilityService.countItems(Arrays.asList(
                new ItemStack(Material.GOLD_NUGGET, 900),
                new ItemStack(Material.GOLD_NUGGET, 36))));
    }
}
