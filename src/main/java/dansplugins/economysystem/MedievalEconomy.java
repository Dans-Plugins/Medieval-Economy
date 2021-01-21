package dansplugins.economysystem;

import dansplugins.economysystem.EventHandlers.PlayerDeathEventHandler;
import dansplugins.economysystem.EventHandlers.PlayerJoinEventHandler;
import dansplugins.economysystem.Objects.Coinpurse;
import dansplugins.economysystem.bStats.Metrics;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import static org.bukkit.Bukkit.getOfflinePlayers;
import static org.bukkit.Bukkit.getOnlinePlayers;

public final class MedievalEconomy extends JavaPlugin implements Listener {

    // version
    public String version = "v1.2-beta-2";

    // subsystems
    public StorageManager storage = new StorageManager(this);
    public CommandInterpreter commands = new CommandInterpreter(this);
    public Utilities utilities = new Utilities(this);
    public ConfigManager config = new ConfigManager(this);

    // saved lists
    public ArrayList<Coinpurse> coinpurses = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println(getConfig().getString("enablingText"));

        utilities.ensureSmoothTransitionBetweenVersions();

        // config creation/loading
        if (!(new File("./plugins/MedievalEconomy/config.yml").exists())) {
            config.saveConfigDefaults();
        }
        else {
            // check version
            if (!getConfig().getString("version").equalsIgnoreCase(version)) {
                config.handleVersionMismatch();
            }
            reloadConfig();
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        if (new File("./plugins/MedievalEconomy/config.yml").exists()) {
            storage.load();
        }
        else {
            storage.legacyLoadCoinpurses();
        }

        int pluginId = 8998;
        Metrics metrics = new Metrics(this, pluginId);

        System.out.println(getConfig().getString("enabledText"));
    }

    @Override
    public void onDisable() {
        System.out.println(getConfig().getString("disablingText"));
        storage.save();
        System.out.println(getConfig().getString("disabledText"));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return commands.interpretCommand(sender, label, args);
    }

    @EventHandler()
    public void onJoin(PlayerJoinEvent event) {
        PlayerJoinEventHandler handler = new PlayerJoinEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onDeath(PlayerDeathEvent event) {
        PlayerDeathEventHandler handler = new PlayerDeathEventHandler(this);
        handler.handle(event);
    }

    // Pasarus wrote this
    public static UUID findUUIDBasedOnPlayerName(String playerName){
        // Check online
        for (Player player : getOnlinePlayers()){
            if (player.getName().equals(playerName)){
                return player.getUniqueId();
            }
        }

        // Check offline
        for (OfflinePlayer player : getOfflinePlayers()){
            try {
                if (player.getName().equals(playerName)){
                    return player.getUniqueId();
                }
            } catch (NullPointerException e) {
                // Fail silently as quit possibly common.
            }

        }

        return null;
    }
}