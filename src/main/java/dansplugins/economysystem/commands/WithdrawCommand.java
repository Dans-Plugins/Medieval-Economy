package dansplugins.economysystem.commands;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.objects.Coinpurse;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 */
public class WithdrawCommand {
    private final MedievalEconomy medievalEconomy;

    public WithdrawCommand(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void withdrawCoins(CommandSender sender, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            // permission check
            if (player.hasPermission("medievaleconomy.withdraw") || player.hasPermission("medievaleconomy.default")) {

                // args check
                if (args.length > 0) {

                    int amount = 0;

                    // get args[0]
                    try {
                        amount = Integer.parseInt(args[0]);
                    } catch(Exception e) {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("withdrawUsageText"));
                        return;
                    }

                    if (amount < 0) {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("withdrawPositiveText"));
                        return;
                    }

                    Coinpurse purse = medievalEconomy.getUtilityService().getPlayersCoinPurse(player.getUniqueId());

                    // enough coins check
                    if (purse.containsAtLeast(amount)) {

                        // if no free slots then disallow
                        if (player.getInventory().firstEmpty() == -1) {
                            player.sendMessage(ChatColor.RED + "" + medievalEconomy.getConfig().getString("withdrawNotEnoughSpace"));
                            return;
                        }

                        // withdraw until inventory is full
                        int withdrawn = 0;
                        for (int i = 0; i < amount; i++) {
                            if (!(player.getInventory().firstEmpty() == -1)) {
                                purse.removeCoins(1);
                                player.getInventory().addItem(medievalEconomy.getUtilityService().getCurrency(1));
                                withdrawn++;
                            }
                            else {
                                int remainder = amount - withdrawn;
                                if (remainder < 64) {
                                    purse.removeCoins(remainder);
                                    player.getInventory().addItem(medievalEconomy.getUtilityService().getCurrency(remainder));
                                    withdrawn = withdrawn + remainder;
                                }
                                else {
                                    purse.removeCoins(63);
                                    player.getInventory().addItem(medievalEconomy.getUtilityService().getCurrency(63));
                                    withdrawn = withdrawn + 63;
                                    player.sendMessage(ChatColor.RED + "" + medievalEconomy.getConfig().getString("withdrawNotEnoughSpace"));
                                    return;
                                }

                            }
                        }
                        player.sendMessage(ChatColor.GREEN + medievalEconomy.getConfig().getString("withdrawTextStart") + withdrawn + medievalEconomy.getConfig().getString("withdrawTextEnd"));
                    }
                    else {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("withdrawNotEnoughCoins"));
                    }

                }
                else {
                    player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("withdrawUsageText"));
                }

            }
            else {
                player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("withdrawNoPermission"));
            }

        }

    }

}
