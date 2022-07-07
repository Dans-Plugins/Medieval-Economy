package dansplugins.economysystem.listeners;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.objects.Coinpurse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel McCoy Stephenson
 */
public class JoinListener {
    private final MedievalEconomy medievalEconomy;

    public JoinListener(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void handle(PlayerJoinEvent event) {
        if (!medievalEconomy.getUtilityService().hasCoinpurse(event.getPlayer().getUniqueId())) {
            giveStarterKit(event.getPlayer());
            assignCoinpurse(event.getPlayer());
        }
    }

    private void giveStarterKit(Player player) {
        player.sendMessage(ChatColor.GREEN + "You wake up and find that you have some gold coins, some food and an empty book on your person.");
        player.getInventory().addItem(medievalEconomy.getUtilityService().getCurrency(50));
        player.getInventory().addItem(new ItemStack(Material.BREAD, 10));
        player.getInventory().addItem(new ItemStack(Material.WRITABLE_BOOK));
    }

    private void assignCoinpurse(Player player) {
        // assign coinpurse
        Coinpurse purse = new Coinpurse(medievalEconomy);
        purse.setPlayerUUID(player.getUniqueId());
        medievalEconomy.getCoinpurses().add(purse);
        player.sendMessage(ChatColor.GREEN + "You lay a hand at your side to reassure yourself your coinpurse is still there. (commands: /balance, /deposit, /withdraw)");
    }

}
