package dansplugins.economysystem.objects;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Characterization tests for the amount argument shared by /deposit, /withdraw and
 * /econ createcurrency. The command handlers themselves cannot be exercised here —
 * constructing MedievalEconomy requires Bukkit's PluginClassLoader — so the rules
 * they enforce are tested through this object instead.
 */
public class AmountArgumentTest {

    @Test
    public void parse_withWholeNumber_isNumericAndKeepsTheValue() {
        AmountArgument argument = AmountArgument.parse("42");
        assertTrue(argument.isNumeric());
        assertEquals(42, argument.getValue());
    }

    @Test
    public void parse_withWholeNumber_isPositive() {
        assertTrue(AmountArgument.parse("1").isPositive());
        assertTrue(AmountArgument.parse("64").isPositive());
    }

    /**
     * The refusal these commands send reads "Number must be positive!", and zero is not
     * positive. Accepting it let /deposit 0 and /withdraw 0 report a movement of no coins.
     */
    @Test
    public void parse_withZero_isNumericButNotPositive() {
        AmountArgument argument = AmountArgument.parse("0");
        assertTrue(argument.isNumeric());
        assertFalse(argument.isPositive());
        assertEquals(0, argument.getValue());
    }

    @Test
    public void parse_withNegativeNumber_isNumericButNotPositive() {
        AmountArgument argument = AmountArgument.parse("-5");
        assertTrue(argument.isNumeric());
        assertFalse(argument.isPositive());
        assertEquals(-5, argument.getValue());
    }

    @Test
    public void parse_withNonNumericText_isNeitherNumericNorPositive() {
        AmountArgument argument = AmountArgument.parse("abc");
        assertFalse(argument.isNumeric());
        assertFalse(argument.isPositive());
    }

    @Test
    public void parse_withDecimal_isNotNumeric() {
        assertFalse(AmountArgument.parse("1.5").isNumeric());
    }

    @Test
    public void parse_withEmptyArgument_isNotNumeric() {
        assertFalse(AmountArgument.parse("").isNumeric());
    }

    /**
     * An amount too large for an int would otherwise throw out of the command handler,
     * which is the failure this object exists to prevent.
     */
    @Test
    public void parse_withNumberTooLargeForAnInt_isNotNumeric() {
        assertFalse(AmountArgument.parse("99999999999999").isNumeric());
    }
}
