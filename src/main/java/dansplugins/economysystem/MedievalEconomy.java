package dansplugins.economysystem;

import dansplugins.economysystem.bStats.Metrics;
import dansplugins.economysystem.eventhandlers.PlayerDeathEventHandler;
import dansplugins.economysystem.eventhandlers.PlayerJoinEventHandler;
import dansplugins.economysystem.objects.Coinpurse;
import dansplugins.economysystem.services.CommandService;
import dansplugins.economysystem.services.ConfigService;
import dansplugins.economysystem.services.StorageService;
import dansplugins.economysystem.services.UtilityService;
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
    private final String pluginVersion = "v" + getDescription().getVersion();

    private final StorageService storageService = new StorageService(this);
    private final CommandService commandService = new CommandService(this);
    private final UtilityService utilityService = new UtilityService(this);
    private final ConfigService configService = new ConfigService(this);

    private final ArrayList<Coinpurse> coinpurses = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println(getConfig().getString("enablingText"));

        utilityService.ensureSmoothTransitionBetweenVersions();

        // config creation/loading
        if (!(new File("./plugins/MedievalEconomy/config.yml").exists())) {
            configService.saveConfigDefaults();
        }
        else {
            // check version
            if (!getConfig().getString("version").equalsIgnoreCase(pluginVersion)) {
                configService.handleVersionMismatch();
            }
            reloadConfig();
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        if (new File("./plugins/MedievalEconomy/config.yml").exists()) {
            storageService.load();
        }
        else {
            storageService.legacyLoadCoinpurses();
        }

        int pluginId = 8998;
        Metrics metrics = new Metrics(this, pluginId);

        System.out.println(getConfig().getString("enabledText"));
    }

    @Override
    public void onDisable() {
        System.out.println(getConfig().getString("disablingText"));
        storageService.save();
        System.out.println(getConfig().getString("disabledText"));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return commandService.interpretCommand(sender, label, args);
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

    public String getVersion() {
        return pluginVersion;
    }

    public StorageService getStorageService() {
        return storageService;
    }

    public UtilityService getUtilityService() {
        return utilityService;
    }

    public ArrayList<Coinpurse> getCoinpurses() {
        return coinpurses;
    }
}