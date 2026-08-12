package dansplugins.economysystem.objects;

/**
 * The result of parsing the whole-number amount argument that /deposit, /withdraw and
 * /econ createcurrency each accept.
 *
 * The parsing rules live here, away from Bukkit types, so that the amounts the commands
 * accept and reject can be tested without a running server.
 *
 * @author Daniel McCoy Stephenson
 */
public class AmountArgument {
    private final int value;
    private final boolean numeric;

    private AmountArgument(int value, boolean numeric) {
        this.value = value;
        this.numeric = numeric;
    }

    public static AmountArgument parse(String argument) {
        try {
            return new AmountArgument(Integer.parseInt(argument), true);
        } catch (NumberFormatException e) {
            return new AmountArgument(0, false);
        }
    }

    /**
     * @return whether the argument was a whole number at all, and so whether a usage hint is owed
     */
    public boolean isNumeric() {
        return numeric;
    }

    /**
     * @return whether the amount is one or greater; zero is not positive, and neither the coinpurse
     *         nor the inventory has anything to do with a movement of no coins
     */
    public boolean isPositive() {
        return numeric && value > 0;
    }

    public int getValue() {
        return value;
    }
}
