package dansplugins.economysystem.services;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.objects.Coinpurse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Daniel McCoy Stephenson
 */
public class StorageService {
    private final MedievalEconomy medievalEconomy;

    public StorageService(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void save() {
        saveCoinpurseFilenames();
        saveCoinpurses();
    }

    public void load() {
        loadCoinpurses();
    }

    public void saveCoinpurseFilenames() {
        try {
            File saveFolder = new File("./plugins/MedievalEconomy/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/MedievalEconomy/" + "coinpurse-record-filenames.txt");

            FileWriter saveWriter = new FileWriter(saveFile);

            // actual saving takes place here
            for (Coinpurse purse : medievalEconomy.getCoinpurses()) {
                saveWriter.write(purse.getPlayerUUID().toString() + ".txt" + "\n");
            }

            saveWriter.close();

        } catch (IOException e) {
            System.out.println(medievalEconomy.getConfig().getString("storageSaveError"));
        }
    }

    public void saveCoinpurses() {
        for (Coinpurse purse : medievalEconomy.getCoinpurses()) {
            purse.save();
        }
    }

    public void loadCoinpurses() {
        try {
            System.out.println("Attempting to load coinpurse records...");
            File loadFile = new File("./plugins/MedievalEconomy/" + "coinpurse-record-filenames.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextName = loadReader.nextLine();
                Coinpurse temp = new Coinpurse(medievalEconomy);
                temp.load(nextName);

                if (temp.getPlayerUUID() == null) {
                    // the record named in the index could not be read, so this coinpurse carries no
                    // identity. Keeping it would break every later lookup and, worse, abort the
                    // filename save on shutdown before any balance had been written.
                    continue;
                }

                // existence check
                Coinpurse existing = UtilityService.findCoinpurse(medievalEconomy.getCoinpurses(), temp.getPlayerUUID());
                if (existing != null) {
                    medievalEconomy.getCoinpurses().remove(existing);
                }

                medievalEconomy.getCoinpurses().add(temp);
            }

            loadReader.close();
            System.out.println("Coinpurse records successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println(medievalEconomy.getConfig().getString("storageLoadError"));
            // e.printStackTrace();
        }
    }

    public void legacyLoadCoinpurses() {
        try {
            File loadFile = new File("./plugins/MedievalEconomy/" + "coinpurse-record-filenames.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextName = loadReader.nextLine();
                Coinpurse temp = new Coinpurse(medievalEconomy);
                temp.legacyLoad(nextName);

                if (temp.getPlayerUUID() == null) {
                    // legacyLoad resolves a player name the server may no longer know, and returns
                    // null when it cannot. The coins are unattributable either way, so the record is
                    // dropped rather than left to break lookups and the shutdown save.
                    continue;
                }

                medievalEconomy.getCoinpurses().add(temp);
            }

            loadReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(medievalEconomy.getConfig().getString("storageLoadError"));
            // e.printStackTrace();
        }

        save();
    }

}
