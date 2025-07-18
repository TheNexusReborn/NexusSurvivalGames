package com.thenexusreborn.survivalgames;

import com.stardevllc.clock.ClockManager;
import com.stardevllc.helper.ReflectionHelper;
import com.stardevllc.registry.IntegerRegistry;
import com.stardevllc.registry.UUIDRegistry;
import com.stardevllc.starchat.ChatSelector;
import com.stardevllc.starchat.StarChat;
import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.staritems.model.CustomItem;
import com.stardevllc.staritems.model.ItemRegistry;
import com.stardevllc.starui.GuiManager;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.registry.ToggleRegistry;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.api.sql.DatabaseRegistry;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.gamemaps.*;
import com.thenexusreborn.gamemaps.model.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.cmds.ToggleCmd;
import com.thenexusreborn.nexuscore.discord.NexusBot;
import com.thenexusreborn.survivalgames.cmd.*;
import com.thenexusreborn.survivalgames.cmd.sgadmin.SGAdminCmd;
import com.thenexusreborn.survivalgames.disguises.NexusDisguises;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.death.DeathType;
import com.thenexusreborn.survivalgames.hooks.NexusHubHook;
import com.thenexusreborn.survivalgames.hooks.SGPAPIExpansion;
import com.thenexusreborn.survivalgames.items.*;
import com.thenexusreborn.survivalgames.listener.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.map.SQLMapManager;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.LobbySettings;
import com.thenexusreborn.survivalgames.threads.ServerStatusThread;
import com.thenexusreborn.survivalgames.threads.game.*;
import com.thenexusreborn.survivalgames.threads.lobby.*;
import com.thenexusreborn.survivalgames.util.NickSGPlayerStats;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

public class SurvivalGames extends NexusSpigotPlugin {
    
    public static SurvivalGames INSTANCE;
    
    private NexusCore nexusCore;
    private StarChat starChat;
    private NexusHubHook nexusHubHook;
    
    private MapManager mapManager;
    
    private final Map<UUID, PlayerMutations> playerUnlockedMutations = new HashMap<>();
    private final Set<IMutationType> disabledMutations = new HashSet<>();
    
    private UUIDRegistry<SGPlayer> playerRegistry = new UUIDRegistry<>(null, null, SGPlayer::getUniqueId, null, null);
    
    private File deathMessagesFile;
    private FileConfiguration deathMessagesConfig;
    
    private InstanceServer instanceServer;
    private IntegerRegistry<SGVirtualServer> servers = new IntegerRegistry<>();
    
    private int gamesPlayed;
    
    private ClockManager clockManager;
    private LootManager lootManager;
    
    private boolean sgGlobalDebug;
    
    private IMutationType defaultMutationType;
    
    public static final GameSettings globalGameSettings = new GameSettings();
    public static final LobbySettings globalLobbySettings = new LobbySettings();
    
    public static CustomItem tributesBook;
    public static CustomItem mutationsBook;
    public static CustomItem spectatorsBook;
    public static CustomItem playerTrackerItem;
    public static CustomItem tpToMapCenterItem;
    public static CustomItem toHubItem;
    public static CustomItem mutateItem;
    public static CustomItem modifierItem;
    public static CustomItem creeperBombItem;
    public static CustomItem chickenLaunchItem;
    public static CustomItem chickenParachuteItem;
    public static CustomItem sponsorsItem;
    
    public static final List<Color> COLORS = new ArrayList<>();
    
    static {
        Set<Field> fields = ReflectionHelper.getClassFields(Color.class);
        fields.removeIf(field -> !Color.class.isAssignableFrom(field.getType()));
        for (Field field : fields) {
            try {
                COLORS.add((Color) field.get(null));
            } catch (IllegalAccessException e) {
            }
        }
    }
    
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
        ItemRegistry itemRegistry = Bukkit.getServicesManager().getRegistration(ItemRegistry.class).getProvider();
        tributesBook = itemRegistry.register(new GameTeamBook(this, GameTeam.TRIBUTES));
        mutationsBook = itemRegistry.register(new GameTeamBook(this, GameTeam.MUTATIONS));
        spectatorsBook = itemRegistry.register(new GameTeamBook(this, GameTeam.SPECTATORS));
        playerTrackerItem = itemRegistry.register(new PlayerTrackerItem(this));
        tpToMapCenterItem = itemRegistry.register(new TPToMapCenterItem(this));
        toHubItem = itemRegistry.register(new ToHubItem(this));
        mutateItem = itemRegistry.register(new MutateItem(this));
        modifierItem = itemRegistry.register(new GameModifierItem(this));
        creeperBombItem = itemRegistry.register(new CreeperSuicideBomb(this));
        chickenLaunchItem = itemRegistry.register(new ChickenLaunchItem(this));
        chickenParachuteItem = itemRegistry.register(new ChickenChuteItem(this));
        sponsorsItem = itemRegistry.register(new SponsorsItem(this));
        
        this.defaultMutationType = StandardMutations.PIG_ZOMBIE;
        getLogger().info("Loading NexusSurvivalGames v" + getDescription().getVersion());
        saveDefaultConfig();
        
        if (getConfig().contains("sgglobaldebug")) {
            this.sgGlobalDebug = getConfig().getBoolean("sgglobaldebug");
        }
        
        if (getConfig().contains("disabledmutations")) {
            List<String> rawDisabledMutations = getConfig().getStringList("disabledmutations");
            for (String rawDisabledMutation : rawDisabledMutations) {
                IMutationType type = IMutationType.valueOf(rawDisabledMutation.toUpperCase());
                if (type == null) {
                    getLogger().warning("Could not get the mutation type for the disabled mutation: " + rawDisabledMutation);
                    continue;
                }
                this.disabledMutations.add(type);
            }
        }
        
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
        
        this.starChat.addSelector(new ChatSelector("game") {
            @Override
            public ChatSelection getSelection(Player player, String[] args) {
                if (!getInstanceServer().getPlayers().contains(player.getUniqueId())) {
                    return null;
                }
                
                SGPlayer sgPlayer = playerRegistry.get(player.getUniqueId());
                if (sgPlayer == null) {
                    return null;
                }
                
                Game game = sgPlayer.getGame();
                if (game == null) {
                    return null;
                }
                
                ChatRoom chatroom = game.getChatRooms().get(sgPlayer.getGamePlayer().getTeam());
                return new ChatSelection(chatroom, "game");
            }
        });
        
        this.starChat.addSelector(new ChatSelector("lobby") {
            @Override
            public ChatSelection getSelection(Player player, String[] args) {
                if (!getInstanceServer().getPlayers().contains(player.getUniqueId())) {
                    return null;
                }
                
                SGPlayer sgPlayer = playerRegistry.get(player.getUniqueId());
                if (sgPlayer == null) {
                    return null;
                }
                
                Lobby lobby = sgPlayer.getLobby();
                if (lobby == null) {
                    return null;
                }
                
                ChatRoom chatroom = lobby.getLobbyChatRoom();
                return new ChatSelection(chatroom, "lobby");
            }
        });
        
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
        
        DeathType.generateDefaultDeathMessages(deathMessagesFile, deathMessagesConfig);
        
        String mapSource = getConfig().getString("map-source");
        if (mapSource == null || mapSource.equalsIgnoreCase("sql")) {
            mapManager = new SQLMapManager(this);
        } else if (mapSource.equalsIgnoreCase("yml")) {
            mapManager = new YamlMapManager(this);
        }
        
        mapManager.loadMaps();
        getCommand("sgmap").setExecutor(new SGMapCommand(this, mapManager));
        
        getLogger().info("Loaded " + mapManager.getMaps().size() + " Maps");
        
        for (SGMap sgMap : mapManager.getMaps()) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                getLogger().info("Downloading map " + sgMap.getName());
                if (sgMap.download(this)) {
                    getLogger().info("Downloaded map " + sgMap.getName());
                } else {
                    getLogger().warning("Failed to download map " + sgMap.getName());
                }
            });
        }
        
        getLogger().info("Loading all unlocked mutations");
        SQLDatabase database = NexusReborn.getPrimaryDatabase();
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
        
        new VoteStartCommand(this);
        new StatsCommand(this);
        new SGAdminCmd(this);
        getCommand("survivalgames").setExecutor(new SGCommand(this));
        new SpectateCommand(this);
        new MapVoteCommand(this);
        new BountyCmd(this);
        new ToggleCmd(this, "spectatorchat", "specchat");
        new ToggleCmd(this, "allowsponsors", "sponsors");
        new RateMapCmd(this);
        new GraceperiodCmd(this);
        new ProbabilityCmd(this);
        
        new MutationMayhemCmd(this);
        
        new GameTeamCmd(this, GameTeam.TRIBUTES);
        new GameTeamCmd(this, GameTeam.SPECTATORS);
        new GameTeamCmd(this, GameTeam.MUTATIONS);
        
        getLogger().info("Registered commands");
        
        new GameStateThread(this).start();
        new TimerCountdownCheckThread(this).start();
        new GameWorldThread(this).start();
        new LobbyThread(this).start();
        new PlayerTrackerThread(this).start();
        new MapSignUpdateThread(this).start();
        new MapChatOptionsMsgThread(this).start();
        new VoteStartMsgThread(this).start();
        new StatSignUpdateThread(this).start();
        new TributeSignUpdateThread(this).start();
        new MutationWaterDamageThread(this).start();
        new ChickenMutationThread(this).start();
        new PlayerUpdateThread(this).start();
        new ServerStatusThread(this).start();
        new PlayerScoreboardThread(this).start();
        new WarmupSpawnThread(this).start();
        new GameBoundsThread(this).start();
        
        getLogger().info("Registered Tasks");
        
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        
        getLogger().info("Registered Listeners");
        
        new NexusDisguises().init(this);
        getLogger().info("Loaded the disguises for mutations.");

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
    }
    
    public Set<IMutationType> getDisabledMutations() {
        return disabledMutations;
    }
    
    public void disableMutation(IMutationType IMutationType) {
        this.disabledMutations.add(IMutationType);
    }
    
    public void enableMutation(IMutationType IMutationType) {
        this.disabledMutations.remove(IMutationType);
    }
    
    public boolean isSgGlobalDebug() {
        return sgGlobalDebug;
    }
    
    public void setSgGlobalDebug(boolean sgGlobalDebug) {
        this.sgGlobalDebug = sgGlobalDebug;
    }
    
    @Override
    public void registerChannels(NexusBot nexusBot) {
//        Bukkit.getScheduler().runTaskLater(this, () -> {
//            for (SGVirtualServer server : this.servers) {
//                nexusBot.addServerChannel(server.getName());
//            }
//        }, 1L);
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
        
        this.getConfig().set("sgglobaldebug", this.sgGlobalDebug);
        
        if (this.disabledMutations.isEmpty()) {
            this.getConfig().set("disabledmutations", null);
        } else {
            List<String> rawDisabledMutations = new ArrayList<>();
            for (IMutationType type : this.disabledMutations) {
                if (type == null) {
                    continue;
                }
                
                rawDisabledMutations.add(type.name());
            }
            
            getConfig().set("disabledmutations", rawDisabledMutations);
        }
        
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
                database.registerClass(NickSGPlayerStats.class);
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
    
    public InstanceServer getInstanceServer() {
        if (this.instanceServer == null) {
            this.instanceServer = ((NexusCore) Bukkit.getPluginManager().getPlugin("NexusCore")).getNexusServer();
        }
        
        return instanceServer;
    }
    
    public GuiManager getGuiManager() {
        return Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
    }
}