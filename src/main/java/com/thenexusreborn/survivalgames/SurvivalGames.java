package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.data.objects.Database;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.registry.*;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.api.stats.StatType;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.api.util.Environment;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.cmd.*;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.tasks.*;
import com.thenexusreborn.survivalgames.listener.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.tasks.*;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.map.*;
import com.thenexusreborn.survivalgames.settings.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;

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
    
    @Override
    public void onLoad() {
        nexusCore = (NexusCore) Bukkit.getPluginManager().getPlugin("NexusCore");
        nexusCore.addNexusPlugin(this);
        getLogger().info("Loaded NexusCore");
    }
    
    @Override
    public void onEnable() {
        getLogger().info("Loading NexusSurvivalGames v" + getDescription().getVersion());
        try {
            Driver mysqlDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(mysqlDriver);
        } catch (SQLException e) {
            getLogger().severe("Error while loading the MySQL driver, disabling plugin");
            e.printStackTrace();
            return;
        }
        
        saveDefaultConfig();
        
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
        
        if (!(NexusAPI.getApi().getTournament() != null && NexusAPI.getApi().getTournament().isActive())) {
            if (NexusAPI.getApi().getEnvironment() == Environment.DEVELOPMENT) {
                lobby.setLobbySettings(getLobbySettings("dev"));
                lobby.setGameSettings(getGameSettings("dev"));
            }
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
        
        this.chatHandler = new SGChatHandler(this);
        nexusCore.getChatManager().setHandler(chatHandler);
        
        getCommand("votestart").setExecutor(new VoteStartCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("survivalgames").setExecutor(new SGCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("map").setExecutor(new MapCommand(this));
        
        getLogger().info("Registered commands");
        
        new GameSetupTask(this).runTaskTimer(this, 1L, 1L);
        new TimerCountdownCheck(this).runTaskTimer(this, 1L, 1L);
        new DeathmatchSetupTask(this).runTaskTimer(this, 1L, 1L);
        new GameWorldTask(this).runTaskTimer(this, 1L, 20L);
        new LobbyTask(this).runTaskTimer(this, 1L, 20L);
        new PlayerTrackerTask().start();
        
        getLogger().info("Registered Tasks");
        
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        
        if (getServer().getPluginManager().getPlugin("Spartan") != null) {
            getServer().getPluginManager().registerEvents(new AnticheatListener(this), this);
        }
        
        getLogger().info("Registered Listeners");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game != null) {
                    for (GamePlayer player : game.getPlayers().values()) {
                        if (player.getTeam() != GameTeam.TRIBUTES) {
                            updatePlayerHealthAndFood(Bukkit.getPlayer(player.getUniqueId()));
                        }
                    }
                } else {
                    for (NexusPlayer player : lobby.getPlayers()) {
                        updatePlayerHealthAndFood(Bukkit.getPlayer(player.getUniqueId()));
                    }
                }
            }
        }.runTaskTimer(this, 1L, 20L);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                ServerInfo serverInfo = NexusAPI.getApi().getServerManager().getCurrentServer();
                if (game != null) {
                    serverInfo.setState("game:" + game.getState().toString());
                } else {
                    serverInfo.setState("lobby:" + lobby.getState().toString());
                }
            }
        }.runTaskTimer(this, 20L, 20L);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                Tournament tournament = NexusAPI.getApi().getTournament();
                if (tournament != null && tournament.isActive()) {
                    Bukkit.broadcastMessage(MCUtils.color("&6&l>> &aThere is an active tournament going on right now."));
                    Bukkit.broadcastMessage(MCUtils.color("&6&l> &aYou will be seeing some additional messages for Points in chat."));
                    Bukkit.broadcastMessage(MCUtils.color("&6&l> &aYou can use &b/tournament (alias /tt) leaderboard &ato see the current leaderboards"));
                    Bukkit.broadcastMessage(MCUtils.color("&6&l> &aYou can use &b/tournament score &ato see your score specifically"));
                }
            }
        }.runTaskTimer(this, 20L, 2450L);
    }
    
    @Override
    public void registerStats(StatRegistry registry) {
        registry.register("sg_score", StatType.INTEGER, 100);
        registry.register("sg_kills", StatType.INTEGER, 0);
        registry.register("sg_highest_kill_streak", StatType.INTEGER, 0);
        registry.register("sg_games", StatType.INTEGER, 0);
        registry.register("sg_wins", StatType.INTEGER, 0);
        registry.register("sg_win_streak", StatType.INTEGER, 0);
        registry.register("sg_deaths", StatType.INTEGER, 0);
        registry.register("sg_deathmatches_reached", StatType.INTEGER, 0);
        registry.register("sg_chests_looted", StatType.INTEGER, 0);
        registry.register("sg_assists", StatType.INTEGER, 0);
        registry.register("sg_times_mutated", StatType.INTEGER, 0);
        registry.register("sg_mutation_kills", StatType.INTEGER, 0);
        registry.register("sg_mutation_deaths", StatType.INTEGER, 0);
        registry.register("sg_mutation_passes", StatType.INTEGER, 0);
        registry.register("sg_sponsored_others", StatType.INTEGER, 0);
        registry.register("sg_sponsors_received", StatType.INTEGER, 0);
        registry.register("sg_tournament_points", StatType.INTEGER, 0);
        registry.register("sg_tournament_kills", StatType.INTEGER, 0);
        registry.register("sg_tournament_wins", StatType.INTEGER, 0);
        registry.register("sg_tournament_survives", StatType.INTEGER, 0);
        registry.register("sg_tournament_chests_looted", StatType.INTEGER, 0);
        registry.register("sg_tournament_assists", StatType.INTEGER, 0);
    }
    
    private void updatePlayerHealthAndFood(Player player) {
        if (player == null) {
            return;
        }
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(2);
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
    public void registerDatabases(DatabaseRegistry registry) {
        for (Database database : registry.getObjects()) {
            if (database.isPrimary()) {
                database.registerClass(GameSettings.class);
                database.registerClass(LobbySettings.class);
            }
        }
        
        FileConfiguration config = getConfig();
        mapDatabase = new Database(config.getString("mapdatabase.database"), config.getString("mapdatabase.host"), config.getString("mapdatabase.user"), config.getString("mapdatabase.password"), false);
        mapDatabase.registerClass(GameMap.class);
        mapDatabase.registerClass(MapSpawn.class);
        registry.register(mapDatabase);
    }
}
