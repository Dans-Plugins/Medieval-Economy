package dansplugins.economysystem.commands;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.objects.Coinpurse;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 */
public class DepositCommand {
    private final MedievalEconomy medievalEconomy;

    public DepositCommand(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void depositCoins(CommandSender sender, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            // permission check
            if (player.hasPermission("medievaleconomy.deposit") || player.hasPermission("medievaleconomy.default")) {

                // args check
                if (args.length > 0) {

                    int amount = 0;

                    // get args[0]
                    try {
                        amount = Integer.parseInt(args[0]);
                    } catch(Exception e) {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("depositUsageText"));
                        return;
                    }

                    if (amount < 0) {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("depositPositiveText"));
                        return;
                    }

                    // enough coins check
                    if (player.getInventory().containsAtLeast(medievalEconomy.getUtilityService().getCurrency(1), amount)) {

                        // add coins to coinpurse
                        Coinpurse purse = medievalEconomy.getUtilityService().getPlayersCoinPurse(player.getUniqueId());
                        purse.addCoins(amount);

                        // delete coins from inventory
                        player.getInventory().removeItem(medievalEconomy.getUtilityService().getCurrency(amount));

                        player.sendMessage(ChatColor.GREEN + medievalEconomy.getConfig().getString("depositTextStart") + amount + medievalEconomy.getConfig().getString("depositTextEnd"));
                    }
                    else {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("depositNotEnoughCoins"));
                    }

                }
                else {
                    player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("depositUsageText"));
                }

            }
            else {
                player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("depositNoPermission"));
            }

        }

    }
}
