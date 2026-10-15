package dansplugins.economysystem.services;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests which handler CommandService picks for a command. It used to dispatch on the label the
 * command was typed with, so the namespaced form /medievaleconomy:balance, whose label carries
 * the plugin prefix, matched no handler and the server answered with the usage string instead.
 *
 * No MedievalEconomy can be constructed here, so the service is given none and the sender is a
 * console-like stand-in that is not a Player: every handler returns before touching the plugin
 * for such a sender, which leaves the dispatch decision as the only thing observed.
 */
public class CommandDispatchTest {

    private final CommandService service = new CommandService(null);

    private final CommandSender console = (CommandSender) Proxy.newProxyInstance(
            CommandSender.class.getClassLoader(),
            new Class<?>[] { CommandSender.class },
            (proxy, method, args) -> method.getReturnType() == boolean.class ? false : null);

    private Command command(String name) {
        return new Command(name) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return false;
            }
        };
    }

    @Test
    public void interpretCommand_dispatchesEveryDeclaredCommand() {
        for (String name : new String[] { "econ", "balance", "deposit", "withdraw" }) {
            assertTrue(name, service.interpretCommand(console, command(name), new String[0]));
        }
    }

    @Test
    public void interpretCommand_ignoresCaseOfTheName() {
        assertTrue(service.interpretCommand(console, command("BALANCE"), new String[0]));
    }

    @Test
    public void interpretCommand_withAnUnknownName_isNotHandled() {
        assertFalse(service.interpretCommand(console, command("pay"), new String[0]));
    }
}
