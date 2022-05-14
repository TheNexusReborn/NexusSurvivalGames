package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.multicraft.MulticraftAPI;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.nexuscore.*;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import com.thenexusreborn.survivalgames.cmd.*;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.tasks.*;
import com.thenexusreborn.survivalgames.listener.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.tasks.*;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.map.MapManager;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.tournament.Tournament;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;

public class SurvivalGames extends JavaPlugin {
    
    public static final String MAP_URL = "https://starmediadev.com/files/nexusreborn/sgmaps/";
    public static final Queue<UUID> PLAYER_QUEUE = new LinkedList<>();
    
    private NexusCore nexusCore;
    
    private MapManager mapManager;
    private Lobby lobby;
    private LootManager lootManager;
    
    private Game game;
    private Tournament tournament;
    
    private int gamesPlayed = 0;
    
    private SGChatHandler chatHandler;
    private boolean restart = false;
    
    @Override
    public void onEnable() {
        getLogger().info("Loading NexusSurvivalGames v" + getDescription().getVersion());
        saveDefaultConfig();
        nexusCore = (NexusCore) Bukkit.getPluginManager().getPlugin("NexusCore");
        getLogger().info("Loaded NexusCore");
        
        getLogger().info("Loading map database tables.");
        try (Connection connection = getMapConnection(); Statement statement = connection.createStatement()) {
            statement.execute("create table if not exists sgmaps(id int primary key not null auto_increment, name varchar(32), url varchar(1000), centerX int, centerY int, centerZ int, borderRadius int, dmBorderRadius int, creators varchar(1000), active varchar(5));");
            statement.execute("create table if not exists sgmapspawns(id int, mapId int, x int, y int, z int);");
        } catch (SQLException e) {
            getLogger().severe("Error while creating map tables tables");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        mapManager = new MapManager(this);
        getLogger().info("Loaded Maps");
        lobby = new Lobby(this);
        getLogger().info("Loaded Lobby Settings");
        lootManager = new LootManager(this);
        getLogger().info("Loaded Loot");
        
        lobby.setControlType(ControlType.AUTOMATIC);
        Game.setControlType(ControlType.AUTOMATIC);
        
        if (NexusAPI.getApi().getEnvironment() == Environment.DEVELOPMENT) {
            lobby.getLobbySettings().setTimerLength(10);
            GameSettings gameSettings = new GameSettings().setWarmupLength(10);
            lobby.setGameSettings(gameSettings);
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
        
        getLogger().info("Loaded spawnpoint");
        
        if (getConfig().contains("tournament")) {
            UUID host = UUID.fromString(getConfig().getString("tournament.host"));
            String name = getConfig().getString("tournament.name");
            this.tournament = new Tournament(host, name);
            tournament.setActive(getConfig().getBoolean("tournament.active"));
            tournament.setPointsPerWin(getConfig().getInt("tournament.pointsperwin"));
            tournament.setPointsPerKill(getConfig().getInt("tournament.pointsperkill"));
            tournament.setPointsPerSurvival(getConfig().getInt("tournament.pointspersurvival"));
            for (String p : getConfig().getStringList("tournament.participants")) {
                tournament.getParticipants().add(UUID.fromString(p));
            }
    
            ConfigurationSection scoresSection = getConfig().getConfigurationSection("tournament.scores");
            if (scoresSection != null) {
                for (String key : scoresSection.getKeys(false)) {
                    tournament.getScores().put(UUID.fromString(key), scoresSection.getInt(key));
                }
            }
            getLogger().info("Loaded tournament");
        }
    
        this.chatHandler = new SGChatHandler(this);
        nexusCore.getChatManager().setHandler(chatHandler);
    
        getCommand("tournament").setExecutor(new TournamentCommand(this));
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
        new LobbyWorldChecker(this).runTaskTimer(this, 1L, 20L);
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
                if (NexusAPI.getApi().getEnvironment() != Environment.DEVELOPMENT) {
                    serverInfo.setStatus(MulticraftAPI.getInstance().getServerStatus(serverInfo.getMulticraftId()).status);
                } else {
                    serverInfo.setStatus("online");
                }
                serverInfo.setPlayers(Bukkit.getOnlinePlayers().size());
                if (game != null) {
                    serverInfo.setState("game:" + game.getState().toString());
                } else {
                    serverInfo.setState("lobby:" + lobby.getState().toString());
                }
                NexusAPI.getApi().getDataManager().pushServerInfo(serverInfo);
            }
        }.runTaskTimerAsynchronously(this, 20L, 20L);
    }
    
    private void updatePlayerHealthAndFood(Player player) {
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
        
        if (this.tournament != null) {
            getConfig().set("tournament.host", tournament.getHost().toString());
            getConfig().set("tournament.active", tournament.isActive());
            getConfig().set("tournament.name", tournament.getName());
            getConfig().set("tournament.pointsperwin", tournament.getPointsPerWin());
            getConfig().set("tournament.pointsperkill", tournament.getPointsPerKill());
            getConfig().set("tournament.pointspersurvival", tournament.getPointsPerSurvival());
            List<String> participants = new ArrayList<>();
            for (UUID participant : tournament.getParticipants()) {
                participants.add(participant.toString());
            }
            getConfig().set("tournament.participants", participants);
            tournament.getScores().forEach((uuid, score) -> getConfig().set("tournament.scores." + uuid.toString(), score));
        }
        
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
    
    public Tournament getTournament() {
        return tournament;
    }
    
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
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
}
