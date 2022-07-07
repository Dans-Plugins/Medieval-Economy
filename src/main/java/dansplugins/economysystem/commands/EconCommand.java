package dansplugins.economysystem.commands;

import dansplugins.economysystem.MedievalEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconCommand {
    private final MedievalEconomy medievalEconomy;

    public EconCommand(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("help")) {
                if (sender instanceof Player) {
                    medievalEconomy.getUtilityService().sendHelpMessage((Player) sender);
                }
            }

            if (args[0].equalsIgnoreCase("createcurrency")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("medievaleconomy.createcurrency") || player.hasPermission("medievaleconomy.admin")) {

                        if (args.length == 1) {
                            medievalEconomy.getUtilityService().addCurrencyToInventory(player, 1);
                        }
                        else {
                            medievalEconomy.getUtilityService().addCurrencyToInventory(player, Integer.parseInt(args[1]));
                        }

                    }
                    else {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("createCurrencyNoPermission"));
                    }
                }
                else {
                    System.out.println(medievalEconomy.getConfig().getString("createCurrencyNoRunFromConsole"));
                }
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("medievaleconomy.reload") || player.hasPermission("medievaleconomy.admin")) {
                        medievalEconomy.reloadConfig();
                        player.sendMessage(ChatColor.GREEN + medievalEconomy.getConfig().getString("configReloadedText"));
                    }
                    else {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("reloadNoPermission"));
                    }
                }
                else {
                    medievalEconomy.reloadConfig();
                    System.out.println(medievalEconomy.getConfig().getString("configReloadedText"));
                }

            }

        }
        else {
            if (sender instanceof Player) {
                medievalEconomy.getUtilityService().sendHelpMessage((Player) sender);
            }
        }
    }

}
