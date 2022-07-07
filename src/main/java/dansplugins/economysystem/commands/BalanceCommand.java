package dansplugins.economysystem.commands;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.objects.Coinpurse;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 */
public class BalanceCommand {
    private final MedievalEconomy medievalEconomy;

    public BalanceCommand(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void run(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // permission check
            if (player.hasPermission("medievaleconomy.balance") || player.hasPermission("medievaleconomy.default")) {

                Coinpurse purse = medievalEconomy.getUtilityService().getPlayersCoinPurse(player.getUniqueId());

                if (purse != null) {

                    int num = purse.getCoins();

                    player.sendMessage(ChatColor.GREEN + medievalEconomy.getConfig().getString("balanceTextStart") + num + medievalEconomy.getConfig().getString("balanceTextEnd"));

                }

            }
            else {
                player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("balanceNoPermission"));
            }

        }
    }

}
