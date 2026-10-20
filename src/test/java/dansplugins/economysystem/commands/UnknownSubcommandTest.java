package dansplugins.economysystem.commands;

import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;

/**
 * Tests what /econ answers with for a subcommand it does not recognize. It used to answer with
 * nothing, which looks the same as the command failing to register.
 *
 * No MedievalEconomy can be constructed here, so the command is given none and the sender is a
 * console-like stand-in that is not a Player, the same way CommandDispatchTest drives
 * CommandService. The console branches of help and of an unknown subcommand never touch the
 * plugin, so what they print to the log is the thing observed.
 */
public class UnknownSubcommandTest {

    private final EconCommand command = new EconCommand(null);

    private final CommandSender console = (CommandSender) Proxy.newProxyInstance(
            CommandSender.class.getClassLoader(),
            new Class<?>[] { CommandSender.class },
            (proxy, method, args) -> method.getReturnType() == boolean.class ? false : null);

    private final ByteArrayOutputStream log = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @Before
    public void captureLog() {
        originalOut = System.out;
        System.setOut(new PrintStream(log, true));
    }

    @After
    public void restoreLog() {
        System.setOut(originalOut);
    }

    @Test
    public void run_fromConsole_withAnUnknownSubcommand_logsThatItIsUnknown() {
        command.run(console, new String[] { "relaod" });

        assertEquals(EconCommand.composeUnknownSubcommandMessage("relaod") + System.lineSeparator(), log.toString());
    }

    /**
     * Recognized subcommands must not fall through to the unknown-subcommand branch as well.
     */
    @Test
    public void run_fromConsole_withHelp_logsNothing() {
        command.run(console, new String[] { "HELP" });

        assertEquals("", log.toString());
    }

    @Test
    public void composeUnknownSubcommandMessage_namesTheSubcommand() {
        assertEquals("Unknown /econ subcommand: 'creatcurrency'.",
                EconCommand.composeUnknownSubcommandMessage("creatcurrency"));
    }
}
