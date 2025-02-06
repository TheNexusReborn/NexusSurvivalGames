package com.thenexusreborn.survivalgames;

import com.stardevllc.clock.ClockManager;
import com.stardevllc.registry.IntegerRegistry;
import com.stardevllc.registry.UUIDRegistry;
import com.stardevllc.starchat.StarChat;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.registry.ToggleRegistry;
import com.thenexusreborn.api.sql.DatabaseRegistry;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.gamemaps.MapManager;
import com.thenexusreborn.gamemaps.SGMapCommand;
import com.thenexusreborn.gamemaps.YamlMapManager;
import com.thenexusreborn.gamemaps.model.MapRating;
import com.thenexusreborn.gamemaps.model.MapSpawn;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.cmds.ToggleCmd;
import com.thenexusreborn.survivalgames.cmd.*;
import com.thenexusreborn.survivalgames.disguises.NexusDisguises;
import com.thenexusreborn.survivalgames.hooks.NexusHubHook;
import com.thenexusreborn.survivalgames.hooks.SGPAPIExpansion;
import com.thenexusreborn.survivalgames.listener.BlockListener;
import com.thenexusreborn.survivalgames.listener.EntityListener;
import com.thenexusreborn.survivalgames.listener.PlayerListener;
import com.thenexusreborn.survivalgames.listener.ServerListener;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.map.SQLMapManager;
import com.thenexusreborn.survivalgames.mutations.PlayerMutations;
import com.thenexusreborn.survivalgames.mutations.UnlockedMutation;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.LobbySettings;
import com.thenexusreborn.survivalgames.threads.ServerStatusThread;
import com.thenexusreborn.survivalgames.threads.game.*;
import com.thenexusreborn.survivalgames.threads.lobby.*;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SurvivalGames extends NexusSpigotPlugin {
    
    public static SurvivalGames INSTANCE;
    
    private NexusCore nexusCore;
    private StarChat starChat;
    private NexusHubHook nexusHubHook;

    private MapManager mapManager;

    private final Map<UUID, PlayerMutations> playerUnlockedMutations = new HashMap<>();

    private UUIDRegistry<SGPlayer> playerRegistry = new UUIDRegistry<>(null, null, SGPlayer::getUniqueId, null, null);

    private File deathMessagesFile;
    private FileConfiguration deathMessagesConfig;
    
    private IntegerRegistry<SGVirtualServer> servers = new IntegerRegistry<>();
    
    private int gamesPlayed;
    
    private ClockManager clockManager;
    private LootManager lootManager;
    
    public static final GameSettings globalGameSettings = new GameSettings();
    public static final LobbySettings globalLobbySettings = new LobbySettings();

    public static SurvivalGames getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        Plugin nexusCorePlugin = Bukkit.getPluginManager().getPlugin("NexusCore");
        if (nexusCorePlugin == null) {
            getLogger().severe("NexusCore not found, disabling SurvivalGames.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        nexusCore = (NexusCore) nexusCorePlugin;
        nexusCore.addNexusPlugin(this);
        getLogger().info("Loaded NexusCore");
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        getLogger().info("Loading NexusSurvivalGames v" + getDescription().getVersion());
        saveDefaultConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("PlaceholderAPI") == null) {
            getLogger().severe("PlaceholderAPI not found, disabling SurvivalGames.");
            pluginManager.disablePlugin(this);
            return;
        }

        if (pluginManager.getPlugin("ProtocolLib") == null) {
            getLogger().severe("ProtocolLib not found, disabling SurvivalGames.");
            pluginManager.disablePlugin(this);
            return;
        }

        if (pluginManager.getPlugin("NexusMaps") == null) {
            getLogger().severe("NexusMaps not found, disabling SurvivalGames.");
            pluginManager.disablePlugin(this);
            return;
        }
        
        this.clockManager = getServer().getServicesManager().getRegistration(ClockManager.class).getProvider();

        this.starChat = (StarChat) getServer().getPluginManager().getPlugin("StarChat");
        getLogger().info("Hooked into StarChat");

        new SGPAPIExpansion(this).register();
        getLogger().info("Hooked into PlaceholderAPI");

        Plugin nexusHubPlugin = getServer().getPluginManager().getPlugin("NexusHub");
        if (nexusHubPlugin != null) {
            this.nexusHubHook = new NexusHubHook(this, nexusHubPlugin);
            getServer().getPluginManager().registerEvents(this.nexusHubHook, this);
            getLogger().info("Applied Hooks and Usages for NexusHub.");
        }

        deathMessagesFile = new File(getDataFolder(), "deathmessages.yml");

        if (!deathMessagesFile.exists()) {
            saveResource("deathmessages.yml", false);
        }

        deathMessagesConfig = YamlConfiguration.loadConfiguration(deathMessagesFile);

        String mapSource = getConfig().getString("map-source");
        if (mapSource == null || mapSource.equalsIgnoreCase("sql")) {
            mapManager = new SQLMapManager(this);
        } else if (mapSource != null && mapSource.equalsIgnoreCase("yml")) {
            mapManager = new YamlMapManager(this);
        }

        mapManager.loadMaps();
        getCommand("sgmap").setExecutor(new SGMapCommand(this, mapManager));

        getLogger().info("Loaded " + mapManager.getMaps().size() + " Maps");

        getLogger().info("Loading all unlocked mutations");
        SQLDatabase database = NexusAPI.getApi().getPrimaryDatabase();
        try {
            List<UnlockedMutation> unlockedMutations = database.get(UnlockedMutation.class);
            for (UnlockedMutation unlockedMutation : unlockedMutations) {
                if (this.playerUnlockedMutations.containsKey(unlockedMutation.getUuid())) {
                    this.playerUnlockedMutations.get(unlockedMutation.getUuid()).add(unlockedMutation);
                } else {
                    PlayerMutations value = new PlayerMutations(unlockedMutation.getUuid());
                    value.add(unlockedMutation);
                    this.playerUnlockedMutations.put(unlockedMutation.getUuid(), value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getLogger().info("Unlocked mutations loaded.");
        
        this.lootManager = new LootManager(this);
        this.lootManager.loadData();

        getCommand("votestart").setExecutor(new VoteStartCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("survivalgames").setExecutor(new SGCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("mapvote").setExecutor(new MapVoteCommand(this));
        getCommand("bounty").setExecutor(new BountyCmd(this));
        new ToggleCmd(this, "spectatorchat", "specchat");
        new ToggleCmd(this, "allowsponsors", "sponsors");
        getCommand("ratemap").setExecutor(new RateMapCmd(this));

        getLogger().info("Registered commands");

        new GameStateThread(this).start();
        new TimerCountdownCheckThread(this).start();
        new DeathmatchSetupThread(this).start();
        new GameWorldThread(this).start();
        new LobbyThread(this).start();
        new PlayerTrackerThread(this).start();
        new MapSignUpdateThread(this).start();
        new MapChatOptionsMsgThread(this).start();
        new VoteStartMsgThread(this).start();
        new StatSignUpdateThread(this).start();
        new TributeSignUpdateThread(this).start();
        new EndermanWaterDamageThread(this).start();
        new ChickenMutationThread(this).start();
        new PlayerUpdateThread(this).start();
        new ServerStatusThread(this).start();
        new PlayerScoreboardThread(this).start();
        new WarmupSpawnThread(this).start();

        getLogger().info("Registered Tasks");

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);

        getLogger().info("Registered Listeners");

//        nexusCore.setMotdSupplier(() -> {
//            String line1 = "&5&lNEXUS REBORN &aSurvival Games", line2;
//            if (this.game == null) {
//                line1 += " &7- &bLobby";
//                line2 = "&3Status: &c" + this.lobby.getState() + "   " + (lobby.getState() == LobbyState.COUNTDOWN ? "&dTime Left: &e" + (int) Math.ceil(lobby.getTimer().getTime() / 1000.0) : "");
//            } else {
//                line1 += " &7- &bGame";
//                line2 = "&3Status: &c" + this.game.getState() + "   " + (game.getTimer() != null ? "&dTime Left: &e" + Game.LONG_TIME_FORMAT.format(game.getTimer().getTimeLeft()) : "");
//            }
//
//            return line1 + "\n" + line2;
//        });
        
        new NexusDisguises().init(this);
        getLogger().info("Loaded the disguises for mutations.");
    }

    @Override
    public void registerToggles(ToggleRegistry registry) {
        registry.register("spectatorchat", Rank.HELPER, "View Spectator Chat", "Allows you to see the spectator chat", false);
        registry.register("allowsponsors", Rank.MEMBER, "Allow Sponsors", "Allow other players to sponsor you", true);
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    @Override
    public void onDisable() {
        for (SGVirtualServer server : this.servers) {
            server.onStop();
        }
        
        if (mapManager instanceof SQLMapManager) {
            getConfig().set("map-source", "sql");
        } else if (mapManager instanceof YamlMapManager) {
            getConfig().set("map-source", "yml");
        }
        
        this.lootManager.saveData();

        saveConfig();
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public NexusCore getNexusCore() {
        return nexusCore;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    @Override
    public void registerDatabases(DatabaseRegistry registry) {
        for (SQLDatabase database : registry.getObjects().values()) {
            if (database.getName().toLowerCase().contains("nexus")) {
                database.registerClass(SGPlayerStats.class);
                database.registerClass(UnlockedMutation.class);
                database.registerClass(MapRating.class);
                database.registerClass(SGMap.class);
                database.registerClass(MapSpawn.class);
            }
        }
    }

    public PlayerMutations getUnlockedMutations(UUID uniqueId) {
        PlayerMutations playerMutations = this.playerUnlockedMutations.get(uniqueId);
        if (playerMutations == null) {
            playerMutations = new PlayerMutations(uniqueId);
            this.playerUnlockedMutations.put(uniqueId, playerMutations);
        }

        return playerMutations;
    }

    public FileConfiguration getDeathMessagesConfig() {
        return deathMessagesConfig;
    }

    public StarChat getStarChat() {
        return starChat;
    }

    public void setMapManager(MapManager mapManager) {
        this.mapManager = mapManager;
        getCommand("sgmap").setExecutor(new SGMapCommand(this, mapManager));
        this.mapManager.loadMaps();
    }

    public NexusHubHook getNexusHubHook() {
        return nexusHubHook;
    }

    public UUIDRegistry<SGPlayer> getPlayerRegistry() {
        return playerRegistry;
    }

    public IntegerRegistry<SGVirtualServer> getServers() {
        return servers;
    }

    public ClockManager getClockManager() {
        return clockManager;
    }
}