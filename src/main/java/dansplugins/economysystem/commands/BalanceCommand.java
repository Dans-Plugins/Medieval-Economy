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

                player.sendMessage(composeBalanceMessage(purse,
                        medievalEconomy.getConfig().getString("balanceTextStart"),
                        medievalEconomy.getConfig().getString("balanceTextEnd"),
                        medievalEconomy.getConfig().getString("balanceNoCoinpurse")));

            }
            else {
                player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("balanceNoPermission"));
            }

        }
    }

    /**
     * Builds what /balance answers with, for a coinpurse that may be absent. Every other outcome of
     * the command produces a message, so a missing coinpurse is told apart from an empty one rather
     * than being left unanswered — silence is indistinguishable from the command failing to register.
     *
     * @param purse the player's coinpurse, or null if none is held
     */
    static String composeBalanceMessage(Coinpurse purse, String start, String end, String noCoinpurse) {
        if (purse == null) {
            return ChatColor.RED + noCoinpurse;
        }
        return ChatColor.GREEN + start + purse.getCoins() + end;
    }

}
