package dansplugins.economysystem.objects;

import dansplugins.economysystem.MedievalEconomy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class Coinpurse {
    private final MedievalEconomy medievalEconomy;

    public Coinpurse(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    private UUID uuid = null;
    private int numCoins = 0;

    public void setPlayerUUID(UUID id) {
        uuid = id;
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public void setCoins(int i) {
        numCoins = i;
    }

    public int getCoins() {
        return numCoins;
    }

    public void addCoins(int num) {
        numCoins = numCoins + num;
    }

    public boolean removeCoins(int num) {
        if ((numCoins - num) >= 0) {
            numCoins = numCoins - num;
            return true;
        }
        return false;
    }

    public boolean containsAtLeast(int num) {
        if (numCoins >= num) {
            return true;
        }
        return false;
    }

    public void save() {
        try {
            File saveFolder = new File("./plugins/MedievalEconomy/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFolder2 = new File("./plugins/MedievalEconomy/Coinpurse-Records/");
            if (!saveFolder2.exists()) {
                saveFolder2.mkdir();
            }
            File saveFile = new File("./plugins/MedievalEconomy/Coinpurse-Records/" + uuid + ".txt");
            saveFile.createNewFile();

            FileWriter saveWriter = new FileWriter("./plugins/MedievalEconomy/Coinpurse-Records/" + uuid + ".txt");

            // actual saving takes place here
            saveWriter.write(uuid.toString() + "\n");
            saveWriter.write(numCoins + "\n");

            saveWriter.close();

        } catch (IOException e) {
            System.out.println(medievalEconomy.getConfig().getString("coinpurseSaveErrorText"));
        }
    }

    public void load(String filename) {
        try {
            File loadFile = new File("./plugins/MedievalEconomy/Coinpurse-Records/" + filename);
            Scanner loadReader = new Scanner(loadFile);

            boolean read = readRecord(loadReader);

            loadReader.close();
            if (read) {
                System.out.println(filename + " successfully loaded.");
            }
            else {
                System.out.println(medievalEconomy.getConfig().getString("coinpurseLoadErrorText") + filename);
            }
        } catch (FileNotFoundException e) {
            System.out.println(medievalEconomy.getConfig().getString("coinpurseLoadErrorText") + filename);
        }
    }

    /**
     * Reads a record as save() writes it: the player's UUID on the first line, the balance on the
     * second. A record that cannot be parsed leaves this coinpurse with no UUID and no coins, the
     * same state as a record that is missing, so the loader skips it and goes on to the next one.
     * Before, the parse exception escaped onEnable, which left every later record unloaded.
     *
     * @return false if the record could not be parsed
     */
    boolean readRecord(Scanner reader) {
        try {
            if (reader.hasNextLine()) {
                uuid = UUID.fromString(reader.nextLine());
            }

            if (reader.hasNextLine()) {
                numCoins = Integer.parseInt(reader.nextLine());
            }
            return true;
        } catch (IllegalArgumentException e) {
            // NumberFormatException, from the balance, is an IllegalArgumentException too
            uuid = null;
            numCoins = 0;
            return false;
        }
    }

    public void legacyLoad(String filename) {
        try {
            File loadFile = new File("./plugins/MedievalEconomy/Coinpurse-Records/" + filename);
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            if (loadReader.hasNextLine()) {
                String playerName = loadReader.nextLine();

                uuid = medievalEconomy.findUUIDBasedOnPlayerName(playerName);
            }

            if (loadReader.hasNextLine()) {
                numCoins = Integer.parseInt(loadReader.nextLine());
            }

            loadReader.close();
            loadFile.delete();
        } catch (FileNotFoundException e) {
            System.out.println(medievalEconomy.getConfig().getString("coinpurseLoadErrorText") + filename);
        }
    }
}
