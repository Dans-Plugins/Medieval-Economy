package dansplugins.economysystem.services;

import dansplugins.economysystem.MedievalEconomy;
import dansplugins.economysystem.objects.Coinpurse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class UtilityService {
    private final MedievalEconomy medievalEconomy;

    public UtilityService(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void addCurrencyToInventory(Player player, int amount) {
        // if player's inventory has space
        if (!(player.getInventory().firstEmpty() == -1)) {
            player.getInventory().addItem(getCurrency(amount));
            player.sendMessage(ChatColor.GREEN + "" + amount + " currency created.");
        }
        else { // player's inventory is full
            player.sendMessage(ChatColor.RED + "Inventory full.");
        }
    }

    public void removeCurrencyFromInventory(Player player, int amount) {
        player.getInventory().removeItem(getCurrency(amount));
    }

    public ItemStack getCurrency(int amount) {
        ItemStack currencyItem = new ItemStack(Material.GOLD_NUGGET, amount);
        ItemMeta meta = currencyItem.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + medievalEconomy.getConfig().getString("currencyItemName"));
        List<String> lore = new ArrayList<String>();
        if (medievalEconomy.getConfig().getBoolean("titleSeparator")) {
            lore.add("");
        }
        lore.add(ChatColor.GOLD + "" + ChatColor.ITALIC + medievalEconomy.getConfig().getString("currencyItemLoreLineOne"));
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + medievalEconomy.getConfig().getString("currencyItemLoreLineTwo"));
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + medievalEconomy.getConfig().getString("currencyItemLoreLineThree"));

        meta.setLore(lore);
        currencyItem.setItemMeta(meta);

        return currencyItem;
    }

    public void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.AQUA + "/econ help - Show a helpful list of commands.");
        if (player.hasPermission("medievaleconomy.createcurrency")) {
            player.sendMessage(ChatColor.AQUA + "/econ createcurrency # - Bring more currency into the world.");
        }
    }

    public boolean hasCoinpurse(UUID uuid) {
        return findCoinpurse(medievalEconomy.getCoinpurses(), uuid) != null;
    }

    public Coinpurse getPlayersCoinPurse(UUID uuid) {
        return findCoinpurse(medievalEconomy.getCoinpurses(), uuid);
    }

    /**
     * Returns the coinpurse held for the given player, creating and registering an empty one
     * if none is held yet. A coinpurse is normally assigned when the player joins, but a record
     * that failed to load leaves the player without one, and the callers of this method would
     * otherwise dereference null.
     */
    public Coinpurse getOrCreateCoinpurse(UUID uuid) {
        return findOrCreateCoinpurse(medievalEconomy.getCoinpurses(), uuid, medievalEconomy);
    }

    /**
     * The comparison runs from the requested UUID rather than the stored one: a coinpurse whose
     * UUID is null is a state the plugin can reach, since legacyLoad assigns whatever
     * findUUIDBasedOnPlayerName returned and that is null for a name no longer known to the server.
     */
    static Coinpurse findCoinpurse(List<Coinpurse> coinpurses, UUID uuid) {
        if (uuid == null) {
            return null;
        }
        for (Coinpurse purse : coinpurses) {
            if (uuid.equals(purse.getPlayerUUID())) {
                return purse;
            }
        }
        return null;
    }

    static Coinpurse findOrCreateCoinpurse(List<Coinpurse> coinpurses, UUID uuid, MedievalEconomy plugin) {
        Coinpurse existing = findCoinpurse(coinpurses, uuid);
        if (existing != null) {
            return existing;
        }
        Coinpurse purse = new Coinpurse(plugin);
        purse.setPlayerUUID(uuid);
        if (uuid != null) {
            // a purse registered under a null UUID would be unreachable by lookup and would save
            // itself to a file named "null.txt", so it is handed back unregistered instead
            coinpurses.add(purse);
        }
        return purse;
    }

    public void ensureSmoothTransitionBetweenVersions() {
        // this piece of code is to ensure that saves don't become broken when updating to v0.7 from a previous version
        File saveFolder = new File("./plugins/Medieval-Economy/");
        if (saveFolder.exists()) {
            System.out.println(medievalEconomy.getConfig().getString("compatibilityText"));

            File newSaveFolder = new File("./plugins/MedievalEconomy/");
            if (!newSaveFolder.exists()) {
                // rename directory
                saveFolder.renameTo(newSaveFolder);

                // delete old folder
                File oldFolder = new File("./plugins/Medieval-Economy");
                deleteLegacyFiles(oldFolder);

                // load in old saves and save them with new format
                medievalEconomy.getStorageService().legacyLoadCoinpurses();
                medievalEconomy.getStorageService().save();
            }

        }
    }

    // Recursive file delete from https://www.baeldung.com/java-delete-directory
    boolean deleteLegacyFiles(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteLegacyFiles(file);
            }
        }
        if (directoryToBeDeleted.getAbsolutePath().contains("config.yml")) {
            return true;
        }
        return directoryToBeDeleted.delete();
    }
}
