package dansplugins.economysystem;

import dansplugins.economysystem.bStats.Metrics;
import dansplugins.economysystem.listeners.DeathListener;
import dansplugins.economysystem.listeners.JoinListener;
import dansplugins.economysystem.objects.Coinpurse;
import dansplugins.economysystem.services.CommandService;
import dansplugins.economysystem.services.ConfigService;
import dansplugins.economysystem.services.StorageService;
import dansplugins.economysystem.services.UtilityService;
import dansplugins.economysystem.trace.TraceClient;
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
import java.util.Collections;
import java.util.UUID;

import static org.bukkit.Bukkit.getOfflinePlayers;
import static org.bukkit.Bukkit.getOnlinePlayers;

/**
 * @author Daniel McCoy Stephenson
 */
public final class MedievalEconomy extends JavaPlugin implements Listener {
    private final String pluginVersion = "v" + getDescription().getVersion();

    private final StorageService storageService = new StorageService(this);
    private final CommandService commandService = new CommandService(this);
    private final UtilityService utilityService = new UtilityService(this);
    private final ConfigService configService = new ConfigService(this);

    private final ArrayList<Coinpurse> coinpurses = new ArrayList<>();

    // A no-op until the config has been read, so a command arriving before
    // onEnable() finishes has something safe to report to.
    private TraceClient trace = TraceClient.disabled();

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
            configService.ensureUsageReportingBlockOnDisk();
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

        // usage reporting: one event now, one per command; see config.yml
        trace = TraceClient.builder(configService.getUsageReportingEndpoint(), getName())
                .key(configService.getUsageReportingKey())
                .enabled(configService.isUsageReportingEnabled())
                .serverWideConfig(getDataFolder().getParentFile())
                .logger(getLogger())
                .build();
        logUsageReportingState();
        trace.report("startup", null, Collections.singletonMap("version", getDescription().getVersion()));

        System.out.println(getConfig().getString("enabledText"));
    }

    // Said on every startup so an operator can see reporting is on, and why it is off, from
    // the console alone. The wording is shared by every plugin that reports to trace.
    private void logUsageReportingState() {
        if (trace.isEnabled()) {
            getLogger().info("Usage reporting is on: " + getName() + " sends its name, version and command names to "
                    + configService.getUsageReportingEndpoint()
                    + " - nothing about players or the server. Turn it off with usage-reporting.enabled: false"
                    + " in this plugin's config.yml, or for every plugin with enabled: false in"
                    + " plugins/trace/config.yml. Details: https://github.com/Stephenson-Software/trace#usage-reporting");
        } else {
            getLogger().info("Usage reporting is off (" + trace.disabledReason() + ").");
        }
    }

    @Override
    public void onDisable() {
        trace.close();

        System.out.println(getConfig().getString("disablingText"));
        storageService.save();
        System.out.println(getConfig().getString("disabledText"));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        trace.report("command", null, Collections.singletonMap("name", cmd.getName()));
        return commandService.interpretCommand(sender, label, args);
    }

    @EventHandler()
    public void onJoin(PlayerJoinEvent event) {
        JoinListener handler = new JoinListener(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onDeath(PlayerDeathEvent event) {
        DeathListener handler = new DeathListener(this);
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