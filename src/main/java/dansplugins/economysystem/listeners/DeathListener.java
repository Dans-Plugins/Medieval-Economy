package dansplugins.economysystem.listeners;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.objects.Coinpurse;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public class DeathListener {
    private final MedievalEconomy medievalEconomy;

    public DeathListener(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void handle(PlayerDeathEvent event) {
        Coinpurse purse = medievalEconomy.getUtilityService().getOrCreateCoinpurse(event.getEntity().getUniqueId());

        int amountToDrop = takeDeathPenalty(purse);

        if (amountToDrop != 0) {

            // drop coins on ground
            event.getDrops().add(medievalEconomy.getUtilityService().getCurrency(amountToDrop));

            // inform player
            event.getEntity().sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("deathMessage"));
        }
    }

    /**
     * Removes the death penalty from a coinpurse and returns how many coins it came to: 10% of
     * the balance, rounded down, or 1 coin for a balance below 10. An empty coinpurse loses
     * nothing and 0 is returned, in which case nothing is dropped and no message is sent.
     */
    static int takeDeathPenalty(Coinpurse purse) {
        if (purse.getCoins() == 0) {
            return 0;
        }

        int amountToDrop = 0;

        // check if purse has at least 10 coins
        if (purse.containsAtLeast(10)) {
            amountToDrop = (int) (purse.getCoins() * 0.10);
        }
        else {
            amountToDrop = 1;
        }

        // remove coins from purse
        purse.removeCoins(amountToDrop);

        return amountToDrop;
    }

}
