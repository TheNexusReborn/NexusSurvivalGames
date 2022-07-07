package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.registry.StatRegistry;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.api.stats.*;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.cmd.*;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.tasks.*;
import com.thenexusreborn.survivalgames.listener.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.tasks.*;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.map.MapManager;
import com.thenexusreborn.survivalgames.settings.Time;
import com.thenexusreborn.survivalgames.settings.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

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
    
    private Map<String, LobbySettings> lobbySettings = new HashMap<>();
    private Map<String, GameSettings> gameSettings = new HashMap<>();
    
    @Override
    public void onLoad() {
        nexusCore = (NexusCore) Bukkit.getPluginManager().getPlugin("NexusCore");
        nexusCore.addNexusPlugin(this);
        getLogger().info("Loaded NexusCore");
    }
    
    @Override
    public void onEnable() {
        try {
            Driver mysqlDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(mysqlDriver);
        } catch (SQLException e) {
            getLogger().severe("Error while loading the MySQL driver, disabling plugin");
            e.printStackTrace();
            return;
        }
        
        getLogger().info("Loading NexusSurvivalGames v" + getDescription().getVersion());
        saveDefaultConfig();
        
        getLogger().info("Creating Map Tables");
        try (Connection connection = getMapConnection(); Statement statement = connection.createStatement()) {
            statement.execute("create table if not exists sgmaps(id int primary key not null auto_increment, name varchar(32), url varchar(1000), centerX int, centerY int, centerZ int, borderRadius int, dmBorderRadius int, creators varchar(1000), active varchar(5));");
            statement.execute("create table if not exists sgmapspawns(id int, mapId int, x int, y int, z int);");
        } catch (SQLException e) {
            getLogger().severe("Error while creating map tables");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        getLogger().info("Map Tables successfully created");
        
        getLogger().info("Loading Game and Lobby Settings");
        try (Connection connection = NexusAPI.getApi().getConnection(); Statement statement = connection.createStatement()) {
            Map<String, String> lobbySettingsColumns = new HashMap<>(), gameSettingsColumns = new HashMap<>();
            
            for (Field field : LobbySettings.class.getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                String type;
                if (field.getType().equals(boolean.class)) {
                    type = "varchar(5)";
                } else if (field.getType().equals(int.class)) {
                    if (field.getName().equalsIgnoreCase("id")) {
                        type = "int primary key auto_increment";
                    } else {
                        type = "int";
                    }
                } else if (field.getType().equals(String.class)) {
                    type = "varchar(100)";
                } else {
                    NexusAPI.getApi().getLogger().severe("Found an unhandled lobby setting type, field name: " + name);
                    continue;
                }
                
                lobbySettingsColumns.put(name, type);
            }
            
            for (Field field : GameSettings.class.getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                String type;
                if (field.getType().equals(boolean.class)) {
                    type = "varchar(5)";
                } else if (field.getType().equals(int.class)) {
                    if (field.getName().equalsIgnoreCase("id")) {
                        type = "int primary key auto_increment";
                    } else {
                        type = "int";
                    }
                } else if (Enum.class.isAssignableFrom(field.getType())) {
                    type = "varchar(100)";
                } else if (field.getType().equals(String.class)) {
                    type = "varchar(100)";
                } else {
                    NexusAPI.getApi().getLogger().severe("Found an unhandled game setting type, field name: " + name);
                    continue;
                }
                
                gameSettingsColumns.put(name, type);
            }
            
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : lobbySettingsColumns.entrySet()) {
                sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
            }
            
            String lobbySettings = sb.substring(0, sb.length() - 2);
            
            sb = new StringBuilder();
            for (Entry<String, String> entry : gameSettingsColumns.entrySet()) {
                sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
            }
            
            String gameSettings = sb.substring(0, sb.length() - 2);
            
            statement.execute("create table if not exists sglobbysettings(" + lobbySettings + ");");
            statement.execute("create table if not exists sggamesettings(" + gameSettings + ");");
        } catch (SQLException e) {
            getLogger().severe("Error while creating other tables");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        Field[] lobbyFields = LobbySettings.class.getDeclaredFields();
        Field[] gameFields = GameSettings.class.getDeclaredFields();
        
        for (Field lobbyField : lobbyFields) {
            lobbyField.setAccessible(true);
        }
        
        for (Field gameField : gameFields) {
            gameField.setAccessible(true);
        }
        
        try (Connection connection = NexusAPI.getApi().getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from sglobbysettings;");
            
            if (resultSet.next()) {
                do {
                    LobbySettings settings = new LobbySettings();
                    for (Field field : lobbyFields) {
                        Object value;
                        if (field.getType().equals(int.class)) {
                            value = resultSet.getInt(field.getName());
                        } else if (field.getType().equals(boolean.class)) {
                            value = Boolean.parseBoolean(resultSet.getString(field.getName()));
                        } else if (field.getType().equals(String.class)) {
                            value = resultSet.getString(field.getName());
                        } else {
                            continue;
                        }
        
                        try {
                            field.set(settings, value);
                        } catch (Exception ex) {}
                    }
                    getLogger().info("Loaded lobby settings with id " + settings.getId() + " and type " + settings.getType());
                    this.lobbySettings.put(resultSet.getString("type"), settings);
                } while (resultSet.next());
            } else {
                getLogger().info("There was no existing lobby settings, inserting defaults");
                LobbySettings settings = new LobbySettings();
                settings.pushToDatabase();
                this.lobbySettings.put(settings.getType(), settings);
                getLogger().info("Defaults have been inserted");
            }
            
            resultSet = statement.executeQuery("select * from sggamesettings;");
            
            if (resultSet.next()) {
                do {
                    GameSettings settings = new GameSettings();
                    for (Field field : gameFields) {
                        Object value;
                        if (field.getType().equals(int.class)) {
                            value = resultSet.getInt(field.getName());
                        } else if (field.getType().equals(boolean.class)) {
                            value = Boolean.parseBoolean(resultSet.getString(field.getName()));
                        } else if (field.getType().equals(ColorMode.class)) {
                            value = ColorMode.valueOf(resultSet.getString(field.getName()));
                        } else if (field.getType().equals(Weather.class)) {
                            value = Weather.valueOf(resultSet.getString(field.getName()));
                        } else if (field.getType().equals(Time.class)) {
                            value = Time.valueOf(resultSet.getString(field.getName()));
                        } else if (field.getType().equals(String.class)) {
                            value = resultSet.getString(field.getName());
                        } else {
                            continue;
                        }
        
                        try {
                            field.set(settings, value);
                        } catch (Exception ex) {
                        }
                    }
                    getLogger().info("Loaded game settings with id " + settings.getId() + " and type " + settings.getType());
                    this.gameSettings.put(resultSet.getString("type"), settings);
                } while (resultSet.next());
            } else {
                getLogger().info("There was no existing game settings, inserting defaults");
                GameSettings settings = new GameSettings();
                settings.pushToDatabase();
                this.gameSettings.put(settings.getType(), settings);
                getLogger().info("Defaults have been inserted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        getLogger().info("Settings Loaded");
        
        mapManager = new MapManager(this);
        getLogger().info("Loaded Maps");
        lobby = new Lobby(this);
        getLogger().info("Loaded Lobby Settings");
        lootManager = new LootManager(this);
        getLogger().info("Loaded Loot");
        
        lobby.setControlType(ControlType.AUTOMATIC);
        Game.setControlType(ControlType.AUTOMATIC);
        
        if (!(NexusAPI.getApi().getTournament() != null && NexusAPI.getApi().getTournament().isActive())) {
            if (NexusAPI.getApi().getEnvironment() == Environment.DEVELOPMENT) {
                LobbySettings devLobbySettings = getLobbySettings("dev");
                if (devLobbySettings == null) {
                    devLobbySettings = new LobbySettings();
                    devLobbySettings.setType("dev");
                    devLobbySettings.setTimerLength(10);
                    devLobbySettings.pushToDatabase();
                    addLobbySettings(devLobbySettings);
                }
                lobby.setLobbySettings(devLobbySettings);
        
                GameSettings devGameSettings = getGameSettings("dev");
                if (devGameSettings == null) {
                    devGameSettings = new GameSettings();
                    devGameSettings.setType("dev");
                    devGameSettings.setWarmupLength(10);
                    devGameSettings.pushToDatabase();
                    addGameSettings(devGameSettings);
                }
                lobby.setGameSettings(devGameSettings);
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
        
        getLogger().info("Registered Listeners");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game != null) {
                    for (GamePlayer player : game.getPlayers().values()) {
                        if (player.getTeam() != GameTeam.TRIBUTES) {
                            updatePlayerHealthAndFood(player.getNexusPlayer().getPlayer());
                        }
                    }
                } else {
                    for (SpigotNexusPlayer player : lobby.getPlayers()) {
                        updatePlayerHealthAndFood(player.getPlayer());
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
        System.out.println("Registering SG Stats");
        registry.register("sg_score", StatType.INTEGER, 100);
        registry.register("sg_kills", StatType.INTEGER, 0);
        registry.register("sg_highest_kill_streak", StatType.INTEGER, 0);
        registry.register("sg_games", StatType.INTEGER, 0);
        registry.register("sg_wins", StatType.INTEGER, 0);
        registry.register("sg_winstreak", StatType.INTEGER, 0);
        registry.register("sg_deaths", StatType.INTEGER, 0);
        registry.register("sg_deathmatches_reached", StatType.INTEGER, 0);
        registry.register("sg_chests_looted", StatType.INTEGER, 0);
        registry.register("sg_assists", StatType.INTEGER, 0);
//        registry.register("sg_times_mutated", StatType.INTEGER, 0);
//        registry.register("sg_mutation_kills", StatType.INTEGER, 0);
//        registry.register("sg_mutation_deaths", StatType.INTEGER, 0);
//        registry.register("sg_mutation_passes", StatType.INTEGER, 0);
//        registry.register("sg_sponsored_others", StatType.INTEGER, 0);
//        registry.register("sg_sponsors_received", StatType.INTEGER, 0);
        registry.register("sg_tournament_points", StatType.INTEGER, 0);
//        registry.register("sg_tournament_kills", StatType.INTEGER, 0);
//        registry.register("sg_tournament_wins", StatType.INTEGER, 0);
//        registry.register("sg_tournament_survives", StatType.INTEGER, 0);
//        registry.register("sg_tournament_chests_looted", StatType.INTEGER, 0);
//        registry.register("sg_tournament_assists", StatType.INTEGER, 0);
    }
    
    private void updatePlayerHealthAndFood(Player player) {
        if (player == null) return;
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
    
    public Connection getMapConnection() throws SQLException {
        return nexusCore.getConnection("nexusmaps");
        //return nexusCore.getConnection();
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
        this.lobbySettings.put(settings.getType(), settings);
    }
    
    public void addGameSettings(GameSettings settings) {
        this.gameSettings.put(settings.getType(), settings);
    }
}
