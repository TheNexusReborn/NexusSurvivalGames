package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.maven.MavenLibrary;
import com.thenexusreborn.api.network.cmd.NetworkCommand;
import com.thenexusreborn.api.registry.*;
import com.thenexusreborn.api.server.Environment;
import com.thenexusreborn.api.stats.StatType;
import com.thenexusreborn.api.storage.objects.Database;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import com.thenexusreborn.survivalgames.cmd.*;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.tasks.*;
import com.thenexusreborn.survivalgames.listener.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.tasks.*;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.map.*;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.settings.*;
import com.thenexusreborn.survivalgames.tasks.ServerStatusTask;
import org.bukkit.*;
import org.bukkit.configuration.file.*;

import java.io.File;
import java.sql.*;
import java.util.*;

@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.30")
public class SurvivalGames extends NexusSpigotPlugin {
    
    public static final String MAP_URL = "https://starmediadev.com/files/nexusreborn/sgmaps/";
    public static final Queue<UUID> PLAYER_QUEUE = new LinkedList<>();
    
    private NexusCore nexusCore;
    
    private MapManager mapManager;
    private Lobby lobby;
    private LootManager lootManager;
    
    private Game game;
    
    private int gamesPlayed = 0;
    
    private SGChatHandler chatHandler;
    private boolean restart = false;
    
    private final Map<String, LobbySettings> lobbySettings = new HashMap<>();
    private final Map<String, GameSettings> gameSettings = new HashMap<>();
    private Database mapDatabase;
    
    private final Map<UUID, PlayerMutations> playerUnlockedMutations = new HashMap<>();
    
    private File deathMessagesFile;
    private FileConfiguration deathMessagesConfig;

    @Override
    public void onLoad() {
        nexusCore = (NexusCore) Bukkit.getPluginManager().getPlugin("NexusCore");
        nexusCore.addNexusPlugin(this);
        getLogger().info("Loaded NexusCore");
    }
    
    @Override
    public void onEnable() {
        getLogger().info("Loading NexusSurvivalGames v" + getDescription().getVersion());
        //LibraryLoader.loadAll(SurvivalGames.class, (URLClassLoader) getClassLoader());
        try {
            Driver mysqlDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(mysqlDriver);
        } catch (SQLException e) {
            getLogger().severe("Error while loading the MySQL driver, disabling plugin");
            e.printStackTrace();
            return;
        }
        
        saveDefaultConfig();
        
        deathMessagesFile = new File(getDataFolder(), "deathmessages.yml");
        if (!deathMessagesFile.exists()) {
            saveResource("deathmessages.yml", false);
        }
        
        deathMessagesConfig = YamlConfiguration.loadConfiguration(deathMessagesFile);
        
        getLogger().info("Loading Game and Lobby Settings");
        try {
            for (GameSettings gameSettings : NexusAPI.getApi().getPrimaryDatabase().get(GameSettings.class)) {
                addGameSettings(gameSettings);
            }
            
            for (LobbySettings lobbySettings : NexusAPI.getApi().getPrimaryDatabase().get(LobbySettings.class)) {
                addLobbySettings(lobbySettings);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        if (!this.lobbySettings.containsKey("default")) {
            LobbySettings lobbySettings = new LobbySettings("default");
            NexusAPI.getApi().getPrimaryDatabase().push(lobbySettings);
            addLobbySettings(lobbySettings);
        }
        if (!this.gameSettings.containsKey("default")) {
            GameSettings gameSettings = new GameSettings("default");
            NexusAPI.getApi().getPrimaryDatabase().push(gameSettings);
            addGameSettings(gameSettings);
        }
        
        if (NexusAPI.getApi().getEnvironment() == Environment.DEVELOPMENT) {
            LobbySettings devLobbySettings = getLobbySettings("dev");
            if (devLobbySettings == null) {
                devLobbySettings = new LobbySettings("dev");
                devLobbySettings.setTimerLength(10);
                NexusAPI.getApi().getPrimaryDatabase().push(devLobbySettings);
                addLobbySettings(devLobbySettings);
            }
            
            GameSettings devGameSettings = getGameSettings("dev");
            if (devGameSettings == null) {
                devGameSettings = new GameSettings("dev");
                devGameSettings.setWarmupLength(10);
                NexusAPI.getApi().getPrimaryDatabase().push(devGameSettings);
                addGameSettings(devGameSettings);
            }
        }
        
        getLogger().info("Settings Loaded");
        
        mapManager = new MapManager(this);
        getLogger().info("Loaded Maps");
        lobby = new Lobby(this);
        getLogger().info("Loaded Lobby Settings");
        
        lobby.setControlType(ControlType.AUTOMATIC);
        Game.setControlType(ControlType.AUTOMATIC);
        
        if (NexusAPI.getApi().getEnvironment() == Environment.DEVELOPMENT) {
            lobby.setLobbySettings(getLobbySettings("dev"));
            lobby.setGameSettings(getGameSettings("dev"));
        }
        
        if (this.getConfig().contains("spawnpoint")) {
            String worldName = this.getConfig().getString("spawnpoint.world");
            int x = Integer.parseInt(this.getConfig().getString("spawnpoint.x"));
            int y = Integer.parseInt(this.getConfig().getString("spawnpoint.y"));
            int z = Integer.parseInt(this.getConfig().getString("spawnpoint.z"));
            float yaw = Float.parseFloat(this.getConfig().getString("spawnpoint.yaw"));
            float pitch = Float.parseFloat(this.getConfig().getString("spawnpoint.pitch"));
            
            Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
            lobby.setSpawnpoint(location);
        } else {
            lobby.setSpawnpoint(Bukkit.getWorld(ServerProperties.getLevelName()).getSpawnLocation());
        }

        getLogger().info("Loading all unlocked mutations");
        try {
            List<UnlockedMutation> unlockedMutations = NexusAPI.getApi().getPrimaryDatabase().get(UnlockedMutation.class);
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

        this.chatHandler = new SGChatHandler(this);
        nexusCore.getChatManager().setHandler(chatHandler);
        
        getCommand("votestart").setExecutor(new VoteStartCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("survivalgames").setExecutor(new SGCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("map").setExecutor(new MapCommand(this));
        getCommand("mutate").setExecutor(new MutateCmd(this));
        
        getLogger().info("Registered commands");
        
        new GameSetupTask(this).runTaskTimer(this, 1L, 1L);
        new TimerCountdownCheck(this).runTaskTimer(this, 1L, 1L);
        new DeathmatchSetupTask(this).runTaskTimer(this, 1L, 1L);
        new GameWorldTask(this).runTaskTimer(this, 1L, 20L);
        new LobbyTask(this).runTaskTimer(this, 1L, 20L);
        new PlayerTrackerTask().start();
        new MapSignUpdateTask(this).start();
        new MapChatOptionsMsgTask(this).start();
        new VoteStartMsgTask(this).start();
        new StatSignUpdateTask(this).start();
        new TributeSignUpdateTask(this).start();
        new EndermanWaterDamageTask(this).start();
        new ChickenMutationTask(this).start();
        new SpectatorUpdateTask(this).start();
        new ServerStatusTask(this).start();
        
        getLogger().info("Registered Tasks");
        
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        
        if (getServer().getPluginManager().getPlugin("Spartan") != null) {
            getServer().getPluginManager().registerEvents(new AnticheatListener(this), this);
        }
        
        getLogger().info("Registered Listeners");
    }
    
    @Override
    public void registerStats(StatRegistry registry) {
        registry.register("sg_score", "Score", StatType.INTEGER, 100);
        registry.register("sg_kills", "Kills", StatType.INTEGER, 0);
        registry.register("sg_highest_kill_streak", "Highest Kill Streak", StatType.INTEGER, 0);
        registry.register("sg_games", "Total Games", StatType.INTEGER, 0);
        registry.register("sg_wins", "Total Wins", StatType.INTEGER, 0);
        registry.register("sg_win_streak", "Winstreak", StatType.INTEGER, 0);
        registry.register("sg_deaths", "Deaths", StatType.INTEGER, 0);
        registry.register("sg_deathmatches_reached", "Deathmatches Reached", StatType.INTEGER, 0);
        registry.register("sg_chests_looted", "Chests Looted", StatType.INTEGER, 0);
        registry.register("sg_assists", "Kill Assists", StatType.INTEGER, 0);
        registry.register("sg_times_mutated", "Times Mutated", StatType.INTEGER, 0);
        registry.register("sg_mutation_kills", "Kills as a Mutation", StatType.INTEGER, 0);
        registry.register("sg_mutation_deaths", "Deaths as a Mutation", StatType.INTEGER, 0);
        registry.register("sg_mutation_passes", "Mutation Passes", StatType.INTEGER, 0);
        registry.register("sg_sponsored_others", "Times Sponsored Others", StatType.INTEGER, 0);
        registry.register("sg_sponsors_received", "Times Sponsored By Others", StatType.INTEGER, 0);
    }
    
    public int getGamesPlayed() {
        return gamesPlayed;
    }
    
    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }
    
    @Override
    public void onDisable() {
        if (game != null) {
            game.handleShutdown();
        }
        
        if (lobby != null) {
            lobby.handleShutdown();
        }
        
        getConfig().set("spawnpoint.world", lobby.getSpawnpoint().getWorld().getName());
        getConfig().set("spawnpoint.x", lobby.getSpawnpoint().getBlockX() + "");
        getConfig().set("spawnpoint.y", lobby.getSpawnpoint().getBlockY() + "");
        getConfig().set("spawnpoint.z", lobby.getSpawnpoint().getBlockZ() + "");
        getConfig().set("spawnpoint.yaw", lobby.getSpawnpoint().getYaw() + "");
        getConfig().set("spawnpoint.pitch", lobby.getSpawnpoint().getPitch() + "");
        
        saveConfig();
    }
    
    public MapManager getMapManager() {
        return mapManager;
    }
    
    public Lobby getLobby() {
        return lobby;
    }
    
    public LootManager getLootManager() {
        return lootManager;
    }
    
    public NexusCore getNexusCore() {
        return nexusCore;
    }
    
    public Game getGame() {
        return game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    public SGChatHandler getChatHandler() {
        return chatHandler;
    }
    
    public boolean restart() {
        return restart;
    }
    
    public void setRestart(boolean restart) {
        this.restart = restart;
    }
    
    public Database getMapDatabase() {
        return this.mapDatabase;
    }
    
    public LobbySettings getLobbySettings(String type) {
        LobbySettings settings = this.lobbySettings.get(type.toLowerCase());
        if (settings != null) {
            return settings.clone();
        }
        return null;
    }
    
    public GameSettings getGameSettings(String type) {
        GameSettings settings = this.gameSettings.get(type.toLowerCase());
        if (settings != null) {
            return settings.clone();
        }
        return null;
    }
    
    public void addLobbySettings(LobbySettings settings) {
        this.lobbySettings.put(settings.getType().toLowerCase(), settings);
    }
    
    public void addGameSettings(GameSettings settings) {
        this.gameSettings.put(settings.getType().toLowerCase(), settings);
    }

    @Override
    public void registerNetworkCommands(NetworkCommandRegistry registry) {
        registry.register(new NetworkCommand("unlockmutation", (cmd, origin, args) -> {
            UUID uuid = UUID.fromString(args[0]);
            String type = args[1];
            long timestamp = Long.parseLong(args[2]);
            getUnlockedMutations(uuid).add(new UnlockedMutation(uuid, type, timestamp));
        }));
    }

    @Override
    public void registerDatabases(DatabaseRegistry registry) {
        for (Database database : registry.getObjects()) {
            if (database.isPrimary()) {
                database.registerClass(GameSettings.class);
                database.registerClass(LobbySettings.class);
                database.registerClass(UnlockedMutation.class);
            }
        }
        
        FileConfiguration config = getConfig();
        mapDatabase = new Database(config.getString("mapdatabase.database"), config.getString("mapdatabase.host"), config.getString("mapdatabase.user"), config.getString("mapdatabase.password"), false);
        mapDatabase.registerClass(GameMap.class);
        mapDatabase.registerClass(MapSpawn.class);
        registry.register(mapDatabase);
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
}
