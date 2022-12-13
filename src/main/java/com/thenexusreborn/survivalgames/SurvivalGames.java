package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.api.frameworks.value.Value.Type;
import com.thenexusreborn.api.network.cmd.NetworkCommand;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.registry.*;
import com.thenexusreborn.api.stats.StatType;
import com.thenexusreborn.api.storage.objects.Database;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import com.thenexusreborn.survivalgames.cmd.*;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.listener.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.map.*;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.newsettings.*;
import com.thenexusreborn.survivalgames.newsettings.object.Setting;
import com.thenexusreborn.survivalgames.newsettings.object.Setting.Info;
import com.thenexusreborn.survivalgames.newsettings.object.impl.*;
import com.thenexusreborn.survivalgames.settings.Time;
import com.thenexusreborn.survivalgames.settings.*;
import com.thenexusreborn.survivalgames.threads.ServerStatusThread;
import com.thenexusreborn.survivalgames.threads.game.*;
import com.thenexusreborn.survivalgames.threads.lobby.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;

import java.io.File;
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
    
    private int gamesPlayed;
    
    private SGChatHandler chatHandler;
    private boolean restart;
    
    private final Map<String, LobbySettings> lobbySettings = new HashMap<>();
    private final Map<String, GameSettings> gameSettings = new HashMap<>();
    private Database mapDatabase;
    
    private final Map<UUID, PlayerMutations> playerUnlockedMutations = new HashMap<>();
    
    private File deathMessagesFile;
    private FileConfiguration deathMessagesConfig;
    
    private final SettingRegistry settingRegistry = new SettingRegistry();

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
        
        deathMessagesFile = new File(getDataFolder(), "deathmessages.yml");
        
        if (!deathMessagesFile.exists()) {
            saveResource("deathmessages.yml", false);
        }

        deathMessagesConfig = YamlConfiguration.loadConfiguration(deathMessagesFile);
    
        getLogger().info("Loading Game and Lobby Settings");
    
        Database database = NexusAPI.getApi().getPrimaryDatabase();
        try {
            List<Info> settingInfos = database.get(Info.class);
            settingRegistry.registerAll(settingInfos);
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        registerDefaultSettings();
    
        for (Info settingInfo : this.settingRegistry.getObjects()) {
            database.queue(settingInfo);
        }
        
        database.flush();
        
        try {
            List<LobbySetting> lobbySettings = database.get(LobbySetting.class);
    
            for (LobbySetting lobbySetting : lobbySettings) {
                if (this.lobbySettings.containsKey(lobbySetting.getCategory())) {
                    this.lobbySettings.get(lobbySetting.getCategory()).add(lobbySetting);
                } else {
                    LobbySettings ls = new LobbySettings();
                    ls.add(lobbySetting);
                    this.lobbySettings.put(lobbySetting.getCategory(), ls);
                }
            }
    
            List<GameSetting> gameSettings = database.get(GameSetting.class);
    
            for (GameSetting gameSetting : gameSettings) {
                if (this.gameSettings.containsKey(gameSetting.getCategory())) {
                    this.gameSettings.get(gameSetting.getCategory()).add(gameSetting);
                } else {
                    GameSettings gs = new GameSettings();
                    gs.add(gameSetting);
                    this.gameSettings.put(gameSetting.getCategory(), gs);
                }
            }
            
            if (!this.lobbySettings.containsKey("default")) {
                this.lobbySettings.put("default", new LobbySettings());
            }
            
            if (!this.gameSettings.containsKey("default")) {
                this.gameSettings.put("default", new GameSettings());
            }
    
            for (Info info : this.settingRegistry.getObjects()) {
                if (info.getType().equalsIgnoreCase("game")) {
                    this.gameSettings.forEach((category, settings) -> {
                        if (!settings.contains(info.getName())) {
                            settings.add(new GameSetting(info, "game", info.getDefaultValue()));
                        }
                    });
                } else if (info.getType().equalsIgnoreCase("lobby")) {
                    this.lobbySettings.forEach((category, settings) -> {
                        if (!settings.contains(info.getName())) {
                            settings.add(new LobbySetting(info, "lobby", info.getDefaultValue()));
                        }
                    });
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        getLogger().info("Settings Loaded");
        
        mapManager = new MapManager(this);
        getLogger().info("Loaded Maps");
        lobby = new Lobby(this);
        getLogger().info("Loaded Lobby Settings");
        
        lobby.setControlType(ControlType.AUTOMATIC);
        Game.setControlType(ControlType.AUTOMATIC);
        
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

        this.chatHandler = new SGChatHandler(this);
        nexusCore.getChatManager().setHandler(chatHandler);
        
        getCommand("votestart").setExecutor(new VoteStartCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("survivalgames").setExecutor(new SGCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("map").setExecutor(new MapCommand(this));
        getCommand("bounty").setExecutor(new BountyCmd(this));
        getCommand("spectatorchat").setExecutor(nexusCore.getToggleCmdExecutor());
        
        getLogger().info("Registered commands");
        
        new GameSetupThread(this).start();
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
        new SpectatorUpdateThread(this).start();
        new ServerStatusThread(this).start();
        new CombatTagThread(this).start();
        new DamagersThread(this).start();
        
        getLogger().info("Registered Tasks");
        
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        
        if (getServer().getPluginManager().getPlugin("Spartan") != null) {
            getServer().getPluginManager().registerEvents(new AnticheatListener(this), this);
        }
        
        getLogger().info("Registered Listeners");
    }
    
    private void registerDefaultSettings() {
        createLobbySetting("max_players", "Maximum Players", "The maximum number of players allowed", Type.INTEGER, 24);
        createLobbySetting("min_players", "Minimum Players", "The minimum number of players needed to auto-start", Type.INTEGER, 4);
        createLobbySetting("max_games", "Maximum Games", "The number of games before auto-restart", Type.INTEGER, 10);
        createLobbySetting("timer_length", "Countdown Timer Length", "The amount of seconds for the countdown timer", Type.INTEGER, 10); //Actual Default is 45
        createLobbySetting("allow_vote_weight", "Allow Vote Weight", "Whether or not if vote weights are counted", Type.BOOLEAN, true);
        createLobbySetting("vote_start_threshold", "Vote Start Threshold", "The minimum number of vote starts required", Type.INTEGER, 2);
        createLobbySetting("keep_previous_game_settings", "Keep Previous Game Settings", "Whether or not to keep the settings from the previous game or go back to defaults", Type.BOOLEAN, true);
        createLobbySetting("sounds", "Sounds", "Whether or not to play custom sounds", Type.BOOLEAN, true);
        
        createGameSetting("max_health", "Maximum Health", "The default maximum health of the tributes", Type.INTEGER, 20);
        createGameSetting("grace_period_length", "Grace Period Length", "The time in seconds for how long the grace period lasts. Due note that the graceperiod setting must also be true", Type.INTEGER, 60);
        createGameSetting("game_length", "Game Length", "The time in minutes for how long the game lasts. This does not include deathmatch", Type.INTEGER, 10); //Actual default is 20
        createGameSetting("deathmatch_length", "Deathmatch Length", "The time in minutes for how long the deathmatch lasts", Type.INTEGER, 5);
        createGameSetting("warmup_length", "Warmup Length", "The time in seconds for how long the starting warmup lasts", Type.INTEGER, 10); //Actual Default is 30
        createGameSetting("deathmatch_threshold", "Deathmatch Threshold", "The amount of tributes remaining to start the deathmatch countdown", Type.INTEGER, 2); //Actual Default is 4
        createGameSetting("next_game_timer_length", "Next Game Timer Length", "The time in seconds to start the next game (or auto-restart)", Type.INTEGER, 10);
        createGameSetting("deathmatch_countdown_length", "Deathmatch Countdown Length", "The time in seconds for the deathmatch countdown. This refers to either the threshold being triggered or the end of the game length", Type.INTEGER, 60);
        createGameSetting("mutation_spawn_delay", "Mutation Spawn Delay", "The time in seconds it takes before a mutation spawns.", Type.INTEGER, 10); //This is probably 15 with the Skills system that then can be lowered to 10
        createGameSetting("pass_award_chance", "Pass Award Chance", "The chance to get a mutation pass on a win", Type.DOUBLE, 0.75);
        createGameSetting("pass_use_chance", "Pass Use Chance", "The chance for a mutation pass to be used", Type.DOUBLE, 0.99);
        createGameSetting("allow_teaming", "Allow Teaming", "This controls the Teaming message at the start of the game", Type.BOOLEAN, true);
        createGameSetting("max_team_amount", "Maximum Team Amount", "The maximum number in a team. This only controls the message at the start of the game", Type.INTEGER, 2);
        createGameSetting("mutations_enabled", "Mutations Enabled", "This controls if mutations are enabled.", Type.BOOLEAN, true);
        createGameSetting("regeneration", "Regeneration", "This controls if regeneration is enabled. True means enabled and false means disabled", Type.BOOLEAN, true);
        createGameSetting("grace_period", "Grace Period", "This controls if the grace period is enabled or not.", Type.BOOLEAN, false);
        createGameSetting("unlimited_mutation_passes", "Unlimited Mutation Passes", "This controls if players need a mutation pass to mutate. This does not allow more than the max mutations per game though.", Type.BOOLEAN, false);
        //TODO Create a game setting for maximum mutations allowed with a default of one
        createGameSetting("time_progression", "Time Progression", "This controls if time is progressed in the world. True means it does, and false means it does not.", Type.BOOLEAN, false);
        createGameSetting("weather_progression", "Weather Progression", "This controls if weather is progressed in the world. True means it does, and false means it does not.", Type.BOOLEAN, false);
        createGameSetting("apply_multipliers", "Apply Multipliers", "This controls if rank based multipliers are to be applied.", Type.BOOLEAN, true);
        createGameSetting("sounds", "Sounds", "Whether or not to play custom sounds", Type.BOOLEAN, true);
        createGameSetting("give_credits", "Give Credits", "Controls if Credits are given on certain actions", Type.BOOLEAN, true);
        createGameSetting("give_network_xp", "Give XP", "Controls if Network XP is given on certain actions", Type.BOOLEAN, true);
        createGameSetting("use_tiered_loot", "Use Tiered Loot", "Controls if tiered loot is to be used.", Type.BOOLEAN, true);
        createGameSetting("enderchests_enabled", "Enderchests Enabled", "Controls if Ender Chests produce loot. These follow the default tiering rules", Type.BOOLEAN, true);
        createGameSetting("use_all_mutation_types", "Use All Mutation Types", "Controls if all mutation types are unlocked or not", Type.BOOLEAN, true); //Actual Default is false
        createGameSetting("color_mode", "Color Mode", "Controls what colors are displayed in death messages", Type.ENUM, ColorMode.RANK);
        createGameSetting("world_time", "World Time", "Controls the starting world time", Type.ENUM, Time.NOON);
        createGameSetting("world_weather", "World Weather", "Controls the starting world weather", Type.ENUM, Weather.CLEAR);
    }   
    
    private void createLobbySetting(String name, String displayName, String description, Value.Type valueType, Object valueDefault) {
        settingRegistry.register(name, displayName, description, "lobby", new Value(valueType, valueDefault));
    }
    
    private void createLobbySetting(String name, String displayName, String description, Value.Type valueType, Object valueDefault, Object minValue, Object maxValue) {
        settingRegistry.register(name, displayName, description, "lobby", new Value(valueType, valueDefault), new Value(valueType, minValue), new Value(valueType, maxValue));
    }
    
    private void createGameSetting(String name, String displayName, String description, Value.Type valueType, Object valueDefault) {
        settingRegistry.register(name, displayName, description, "game", new Value(valueType, valueDefault));
    }
    
    private void createGameSetting(String name, String displayName, String description, Value.Type valueType, Object valueDefault, Object minValue, Object maxValue) {
        settingRegistry.register(name, displayName, description, "game", new Value(valueType, valueDefault), new Value(valueType, minValue), new Value(valueType, maxValue));
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
    
    @Override
    public void registerToggles(ToggleRegistry registry) {
        registry.register("spectatorchat", Rank.HELPER, "View Spectator Chat", "Allows you to see the spectator chat", false);
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
                database.registerClass(Setting.Info.class);
                database.registerClass(GameSetting.class);
                database.registerClass(LobbySetting.class);
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
    
    public SettingRegistry getSettingRegistry() {
        return settingRegistry;
    }
}
