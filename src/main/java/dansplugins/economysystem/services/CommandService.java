package dansplugins.economysystem.services;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.commands.BalanceCommand;
import dansplugins.economysystem.commands.DepositCommand;
import dansplugins.economysystem.commands.EconCommand;
import dansplugins.economysystem.commands.WithdrawCommand;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel McCoy Stephenson
 */
public class CommandService {
    private final MedievalEconomy medievalEconomy;

    public CommandService(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("econ")) {
            EconCommand command = new EconCommand(medievalEconomy);
            command.run(sender, args);
            return true;
        }
        if (label.equalsIgnoreCase("balance")) {
            BalanceCommand command = new BalanceCommand(medievalEconomy);
            command.run(sender);
            return true;
        }
        if (label.equalsIgnoreCase("deposit")) {
            DepositCommand command = new DepositCommand(medievalEconomy);
            command.depositCoins(sender, args);
            return true;
        }
        if (label.equalsIgnoreCase("withdraw")) {
            WithdrawCommand command = new WithdrawCommand(medievalEconomy);
            command.withdrawCoins(sender, args);
            return true;
        }
        return false;
    }

}
