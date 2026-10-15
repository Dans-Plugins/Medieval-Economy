package dansplugins.economysystem.services;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.commands.BalanceCommand;
import dansplugins.economysystem.commands.DepositCommand;
import dansplugins.economysystem.commands.EconCommand;
import dansplugins.economysystem.commands.WithdrawCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel McCoy Stephenson
 */
public class CommandService {
    private final MedievalEconomy medievalEconomy;

    public CommandService(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    /**
     * Dispatches on the command's registered name rather than the label it was typed with: the
     * label of the namespaced form, e.g. /medievaleconomy:balance, carries the plugin prefix and
     * would match none of the commands below.
     */
    public boolean interpretCommand(CommandSender sender, Command cmd, String[] args) {
        String name = cmd.getName();
        if (name.equalsIgnoreCase("econ")) {
            EconCommand command = new EconCommand(medievalEconomy);
            command.run(sender, args);
            return true;
        }
        if (name.equalsIgnoreCase("balance")) {
            BalanceCommand command = new BalanceCommand(medievalEconomy);
            command.run(sender);
            return true;
        }
        if (name.equalsIgnoreCase("deposit")) {
            DepositCommand command = new DepositCommand(medievalEconomy);
            command.depositCoins(sender, args);
            return true;
        }
        if (name.equalsIgnoreCase("withdraw")) {
            WithdrawCommand command = new WithdrawCommand(medievalEconomy);
            command.withdrawCoins(sender, args);
            return true;
        }
        return false;
    }

}
