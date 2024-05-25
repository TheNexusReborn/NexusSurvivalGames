package com.thenexusreborn.survivalgames;

import com.stardevllc.starchat.StarChat;
import com.stardevllc.starlib.clock.ClockManager;
import com.stardevllc.starlib.misc.Value;
import com.stardevllc.starlib.misc.Value.Type;
import com.stardevllc.starlib.registry.IntegerRegistry;
import com.stardevllc.starlib.registry.UUIDRegistry;
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
import com.thenexusreborn.survivalgames.settings.SettingRegistry;
import com.thenexusreborn.survivalgames.settings.object.enums.ColorMode;
import com.thenexusreborn.survivalgames.settings.object.enums.Time;
import com.thenexusreborn.survivalgames.settings.object.enums.Weather;
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
import java.util.*;

public class SurvivalGames extends NexusSpigotPlugin {
    
    public static SurvivalGames INSTANCE;
    public static final String MAP_URL = "https://starmediadev.com/files/nexusreborn/sgmaps/";
    public static final Queue<UUID> PLAYER_QUEUE = new LinkedList<>();
    
    private NexusCore nexusCore;
    private StarChat starChat;
    private NexusHubHook nexusHubHook;

    private MapManager mapManager;

    private final Map<String, LobbySettings> lobbySettings = new HashMap<>();
    private final Map<String, GameSettings> gameSettings = new HashMap<>();

    private final Map<UUID, PlayerMutations> playerUnlockedMutations = new HashMap<>();

    private final SettingRegistry lobbySettingRegistry = new SettingRegistry();
    private final SettingRegistry gameSettingRegistry = new SettingRegistry();

    private UUIDRegistry<SGPlayer> playerRegistry = new UUIDRegistry<>(null, null, SGPlayer::getUniqueId, null, null);

    private File deathMessagesFile;
    private FileConfiguration deathMessagesConfig;
    
    private IntegerRegistry<SGVirtualServer> servers = new IntegerRegistry<>();
    
    private int gamesPlayed;
    
    private ClockManager clockManager;
    private LootManager lootManager;

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

        getLogger().info("Loading Game and Lobby Settings");

        registerDefaultSettings();

        this.lobbySettings.put("default", new LobbySettings());
        this.gameSettings.put("default", new GameSettings());

        getLogger().info("Settings Loaded");

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
        getCommand("spectatorchat").setExecutor(nexusCore.getToggleCmdExecutor());
        getCommand("sponsors").setExecutor(nexusCore.getToggleCmdExecutor());
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

    private void registerDefaultSettings() {
        createLobbySetting("max_players", "Maximum Players", "The maximum number of players allowed", Type.INTEGER, 24, 2, 24);
        createLobbySetting("min_players", "Minimum Players", "The minimum number of players needed to auto-start", Type.INTEGER, 4, 2, 24);
        createLobbySetting("max_games", "Maximum Games", "The number of games before auto-restart", Type.INTEGER, 10, 1, 20);
        createLobbySetting("timer_length", "Countdown Timer Length", "The amount of seconds for the countdown timer", Type.INTEGER, 10); //Actual Default is 45
        createLobbySetting("allow_vote_weight", "Allow Vote Weight", "Whether or not if vote weights are counted", Type.BOOLEAN, true);
        createLobbySetting("vote_start_threshold", "Vote Start Threshold", "The minimum number of vote starts required", Type.INTEGER, 2, 2, 24);
        createLobbySetting("keep_previous_game_settings", "Keep Previous Game Settings", "Whether or not to keep the settings from the previous game or go back to defaults", Type.BOOLEAN, true);
        createLobbySetting("sounds", "Sounds", "Whether or not to play custom sounds", Type.BOOLEAN, true);
        createLobbySetting("allow_vote_start", "Allow Vote Start", "Whether or not Vote Start is allowed.", Type.BOOLEAN, true);
        createLobbySetting("vote_start_available_threshold", "Vote Start Available Threshold", "The amount of players that has to be equal to or under to allow voting to start.", Type.INTEGER, 4);

        createGameSetting("max_health", "Maximum Health", "The default maximum health of the tributes", Type.INTEGER, 20, 1, 1024);
        createGameSetting("grace_period_length", "Grace Period Length", "The time in seconds for how long the grace period lasts. Due note that the graceperiod setting must also be true", Type.INTEGER, 60);
        createGameSetting("game_length", "Game Length", "The time in minutes for how long the game lasts. This does not include deathmatch", Type.INTEGER, 10); //Actual default is 20
        createGameSetting("deathmatch_length", "Deathmatch Length", "The time in minutes for how long the deathmatch lasts", Type.INTEGER, 5);
        createGameSetting("warmup_length", "Warmup Length", "The time in seconds for how long the starting warmup lasts", Type.INTEGER, 10); //Actual Default is 30
        createGameSetting("deathmatch_threshold", "Deathmatch Threshold", "The amount of tributes remaining to start the deathmatch countdown", Type.INTEGER, 2, 2, 24); //Actual Default is 4
        createGameSetting("next_game_timer_length", "Next Game Timer Length", "The time in seconds to start the next game (or auto-restart)", Type.INTEGER, 10);
        createGameSetting("deathmatch_countdown_length", "Deathmatch Countdown Length", "The time in seconds for the deathmatch countdown. This refers to either the threshold being triggered or the end of the game length", Type.INTEGER, 60);
        createGameSetting("deathmatch_warmup_length", "Deathmatch Warmup Length", "The time in seconds for the deathmatch warmup", Type.INTEGER, 10);
        createGameSetting("mutation_spawn_delay", "Mutation Spawn Delay", "The time in seconds it takes before a mutation spawns.", Type.INTEGER, 10); //This is probably 15 with the Skills system that then can be lowered to 10
        createGameSetting("pass_award_chance", "Pass Award Chance", "The chance to get a mutation pass on a win", Type.DOUBLE, 0.75, 0, 1.0);
        createGameSetting("pass_use_chance", "Pass Use Chance", "The chance for a mutation pass to be used", Type.DOUBLE, 0.99, 0, 1.0);
        createGameSetting("allow_teaming", "Allow Teaming", "This controls the Teaming message at the start of the game", Type.BOOLEAN, true);
        createGameSetting("max_team_amount", "Maximum Team Amount", "The maximum number in a team. This only controls the message at the start of the game", Type.INTEGER, 2);
        createGameSetting("mutations_enabled", "Mutations Enabled", "This controls if mutations are enabled.", Type.BOOLEAN, true);
        createGameSetting("regeneration", "Regeneration", "This controls if regeneration is enabled. True means enabled and false means disabled", Type.BOOLEAN, true);
        createGameSetting("grace_period", "Grace Period", "This controls if the grace period is enabled or not.", Type.BOOLEAN, false);
        createGameSetting("unlimited_mutation_passes", "Unlimited Mutation Passes", "This controls if players need a mutation pass to mutate. This does not allow more than the max mutations per game though.", Type.BOOLEAN, true);
        createGameSetting("time_progression", "Time Progression", "This controls if time is progressed in the world. True means it does, and false means it does not.", Type.BOOLEAN, false);
        createGameSetting("weather_progression", "Weather Progression", "This controls if weather is progressed in the world. True means it does, and false means it does not.", Type.BOOLEAN, false);
        createGameSetting("apply_multipliers", "Apply Multipliers", "This controls if rank based multipliers are to be applied.", Type.BOOLEAN, true);
        createGameSetting("sounds", "Sounds", "Whether or not to play custom sounds", Type.BOOLEAN, true);
        createGameSetting("earn_credits", "Earn Credits", "Controls if Credits are earned on certain actions", Type.BOOLEAN, true);
        createGameSetting("earn_network_xp", "Earn XP", "Controls if Network XP is earned on certain actions", Type.BOOLEAN, true);
        createGameSetting("use_tiered_loot", "Use Tiered Loot", "Controls if tiered loot is to be used.", Type.BOOLEAN, true);
        createGameSetting("enderchests_enabled", "Enderchests Enabled", "Controls if Ender Chests produce loot. These follow the default tiering rules", Type.BOOLEAN, true);
        createGameSetting("use_all_mutation_types", "Use All Mutation Types", "Controls if all mutation types are unlocked or not", Type.BOOLEAN, false); //Actual Default is false
        createGameSetting("color_mode", "Color Mode", "Controls what colors are displayed in death messages", Type.ENUM, ColorMode.RANK);
        createGameSetting("world_time", "World Time", "Controls the starting world time", Type.ENUM, Time.NOON);
        createGameSetting("world_weather", "World Weather", "Controls the starting world weather", Type.ENUM, Weather.CLEAR);
        createGameSetting("starting_saturation", "Starting Saturation", "The saturation for players at the start", Type.DOUBLE, 5.0, 0.0, 20.0);
        createGameSetting("score_divisor", "Score Divisor", "The number that the score from the dead player is divided by", Type.DOUBLE, 10.0, 1.0, 100);
        createGameSetting("first_blood_multiplier", "First Blood Multiplier", "The multiplier to apply to the score when someone claims first blood.", Type.DOUBLE, 1.25, 1, 100);
        createGameSetting("max_mutation_amount", "Maximum Mutation Amount", "The maximum amount of times that players can mutate per game", Type.INTEGER, 1);
        createGameSetting("earn_nexites", "Earn Nexites", "Controls if Nexites are earned on certain actions", Type.BOOLEAN, false);
        createGameSetting("allow_assists", "Allow Assists", "Controls if assists are counted.", Type.BOOLEAN, true);
        createGameSetting("max_mutations_allowed", "Maximum Mutations Allowed", "The maximum amount of mutations allowed to be in a single game", Type.INTEGER, 10, 0, 20);
        createGameSetting("win_score_base_gain", "Win Score Base Gain", "The base amount of score that is gained when a player wins.", Type.INTEGER, 50);
        createGameSetting("win_credits_base_gain", "Win Credits Base Gain", "The base amount of credits that is gained when a player wins", Type.INTEGER, 10);
        createGameSetting("win_xp_base_gain", "Win XP Base Gain", "The base amount of xp that is gained when a player wins", Type.INTEGER, 10);
        createGameSetting("win_nexite_base_gain", "Win Nexite Base Gain", "The base amount of Nexites that is gained when a player wins", Type.INTEGER, 10);
        createGameSetting("kill_credit_gain", "Kill Credit Gain", "The base amount of credits that is gained on a kill.", Type.INTEGER, 2);
        createGameSetting("kill_xp_gain", "Kill XP Gain", "The base amount of xp that is gained on a kill.", Type.INTEGER, 2);
        createGameSetting("kill_nexite_gain", "Kill Nexite Gain", "The base amount of nexites that is gained on a kill.", Type.INTEGER, 2);
        createGameSetting("assist_credit_gain", "Assist Credit Gain", "The base amount of credits that is gained on an assist.", Type.INTEGER, 1);
        createGameSetting("assist_xp_gain", "Assist XP Gain", "The base amount of xp that is gained on an assist.", Type.INTEGER, 1);
        createGameSetting("assist_nexite_gain", "Assist Nexite Gain", "The base amount of nexites that is gained on an assist.", Type.INTEGER, 1);
        createGameSetting("max_credit_bounty", "Maximum Credit Bounty", "The maximum credit bounty that one player can have", Type.INTEGER, 10000);
        createGameSetting("max_score_bounty", "Maximum Score Bounty", "The maximum score bounty that one player can have", Type.INTEGER, 10000);
        createGameSetting("combat_tag_length", "Combat Tag Length", "The length in seconds that the combat tag lasts", Type.INTEGER, 10, 0, 60);
        createGameSetting("allow_sponsoring", "Allow Sponsoring", "Controls if sponsoring is enabled for the game", Type.BOOLEAN, true);
        createGameSetting("allow_swag_shack", "Allow Swag Shack", "Controls if the swag shack is enabled for the game", Type.BOOLEAN, true);
        createGameSetting("sponsor_credit_cost", "Sponsor Credit Cost", "The cost of credits for the Credit Sponsorship", Type.INTEGER, 200);
        createGameSetting("sponsor_score_cost", "Sponsor Score Cost", "The cost of score for the Score Sponsorship", Type.INTEGER, 100);
        createGameSetting("allow_swag_shack", "Allow Swag Shack", "Whether or not the Swag Shack is allowed to be used", Type.BOOLEAN, true);
        createGameSetting("chest_restock_relative", "Chest Restock Relative", "Whether or not the Restock Timer is relative to the game timer.", Type.BOOLEAN, true);
        createGameSetting("chest_restock_denomination", "Chest Restock Denomination", "The value to divide the game time by for the restock relative.", Type.INTEGER, 2);
        createGameSetting("chest_restock_interval", "Chest Restock Interval", "The interval for chest restock, the relative setting must be false.", Type.INTEGER, 5);
    }

    private void createLobbySetting(String name, String displayName, String description, Type valueType, Object valueDefault) {
        lobbySettingRegistry.register(name, displayName, description, "lobby", new Value(valueType, valueDefault));
    }

    @SuppressWarnings("SameParameterValue")
    private void createLobbySetting(String name, String displayName, String description, Type valueType, Object valueDefault, Object minValue, Object maxValue) {
        if (!lobbySettingRegistry.contains(name)) {
            lobbySettingRegistry.register(name, displayName, description, "lobby", new Value(valueType, valueDefault), new Value(valueType, minValue), new Value(valueType, maxValue));
        }
    }

    private void createGameSetting(String name, String displayName, String description, Type valueType, Object valueDefault) {
        if (!gameSettingRegistry.contains(name)) {
            gameSettingRegistry.register(name, displayName, description, "game", new Value(valueType, valueDefault));
        }
    }

    private void createGameSetting(String name, String displayName, String description, Type valueType, Object valueDefault, Object minValue, Object maxValue) {
        if (!(gameSettingRegistry.contains(name))) {
            gameSettingRegistry.register(name, displayName, description, "game", new Value(valueType, valueDefault), new Value(valueType, minValue), new Value(valueType, maxValue));
        }
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

    public SettingRegistry getLobbySettingRegistry() {
        return lobbySettingRegistry;
    }

    public SettingRegistry getGameSettingRegistry() {
        return gameSettingRegistry;
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