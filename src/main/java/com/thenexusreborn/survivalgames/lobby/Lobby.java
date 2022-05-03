package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.scoreboard.impl.RankTablistHandler;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.scoreboard.LobbyScoreboardView;
import com.thenexusreborn.survivalgames.settings.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class Lobby {
    private SurvivalGames plugin;
    private Map<UUID, SpigotNexusPlayer> players = new HashMap<>();
    private List<UUID> spectatingPlayers = new ArrayList<>();
    private Timer timer;
    private GameSettings gameSettings;
    private LobbySettings lobbySettings = new LobbySettings();
    private GameMap gameMap;
    private Mode mode = Mode.MANUAL;
    private LobbyState state = LobbyState.WAITING;
    private Location spawnpoint;
    private Set<UUID> voteStart = new HashSet<>();
    private Map<Integer, Location> mapSigns = new HashMap<>();
    private Map<Integer, GameMap> mapOptions = new HashMap<>();
    private Map<Integer, Set<UUID>> mapVotes = new HashMap<>();
    
    public Lobby(SurvivalGames plugin) {
        this.plugin = plugin;
        
        if (plugin.getConfig().contains("mapsigns")) {
            ConfigurationSection signsSection = plugin.getConfig().getConfigurationSection("mapsigns");
            for (String key : signsSection.getKeys(false)) {
                int position = Integer.parseInt(key);
                World world = Bukkit.getWorld(signsSection.getString(key + ".world"));
                int x = signsSection.getInt(key + ".x");
                int y = signsSection.getInt(key + ".y");
                int z = signsSection.getInt(key + ".z");
                Location location = new Location(world, x, y, z);
                this.mapSigns.put(position, location);
            }
        }
        
        generateMapOptions();
    
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entry<Integer, Location> entry : mapSigns.entrySet()) {
                    GameMap map = mapOptions.get(entry.getKey());
                    BlockState state = entry.getValue().getBlock().getState();
                    if (!(state instanceof Sign)) {
                        continue;
                    }
                    Sign sign = (Sign) state;
                    String mapName;
                    if (map.getName().length() > 16) {
                        mapName = map.getName().substring(0, 16);
                    } else {
                        mapName = map.getName();
                    }
                    sign.setLine(1, mapName);
                    int votes = getTotalMapVotes(entry.getKey());
                    
                    sign.setLine(3, MCUtils.color("&n" + votes + " Vote(s)"));
    
                    for (SpigotNexusPlayer player : players.values()) {
                        if (!player.getPlayer().getWorld().getName().equalsIgnoreCase(spawnpoint.getWorld().getName())) {
                            continue;
                        }
                        if (mapVotes.get(entry.getKey()).contains(player.getUniqueId())) {
                            sign.setLine(0, MCUtils.color("&n#" + entry.getKey()));
                            sign.setLine(2, MCUtils.color("&2&lVOTED!"));
                        } else {
                            sign.setLine(0, MCUtils.color("&nClick to Vote"));
                            sign.setLine(2, "");
                        }
                        player.getPlayer().sendSignChange(sign.getLocation(), sign.getLines());
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() < lobbySettings.getMinPlayers()) {
                    sendMessage("&6&l>> &e&lDid you know that you can use &f&l/votestart &e&lto start a game early?");
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 1200L);
    }
    
    public void handleShutdown() {
        this.state = LobbyState.SHUTTING_DOWN;
        sendMessage("&4&l>> THE SERVER IS SHUTTING DOWN!");
        if (timer != null) {
            timer.cancel();
        }
        
        if (state == LobbyState.MAP_EDITING) {
            if (gameMap != null) {
                gameMap.saveSettings();
            }
        }
        
        if (gameMap != null) {
            gameMap.delete(plugin);
        }
        
        this.mapSigns.forEach((position, location) -> {
            plugin.getConfig().set("mapsigns." + position + ".world", location.getWorld().getName());
            plugin.getConfig().set("mapsigns." + position + ".x", location.getBlockX());
            plugin.getConfig().set("mapsigns." + position + ".y", location.getBlockY());
            plugin.getConfig().set("mapsigns." + position + ".z", location.getBlockZ());
        });
    }
    
    public void generateMapOptions() {
        this.mapOptions.clear();
        this.mapVotes.clear();
    
        List<GameMap> maps = new ArrayList<>(plugin.getMapManager().getMaps());
        for (Integer position : new HashSet<>(this.mapSigns.keySet())) {
            int index = new Random().nextInt(maps.size());
            GameMap map = maps.get(index);
            this.mapOptions.put(position, map);
            this.mapVotes.put(position, new HashSet<>());
            maps.remove(index);
        }
    }
    
    public boolean checkMapEditing(Player player) {
        if (this.state == LobbyState.MAP_EDITING) {
            return !player.getWorld().getName().equalsIgnoreCase(this.spawnpoint.getWorld().getName());
        }
        
        return false;
    }
    
    public SurvivalGames getPlugin() {
        return plugin;
    }
    
    public void sendMessage(String message) {
        for (SpigotNexusPlayer player : this.players.values()) {
            player.sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(MCUtils.color(message));
    }
    
    public void editMaps()   {
        this.state = LobbyState.MAP_EDITING;
        if (timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        sendMessage("&eThe lobby has been set to editing maps. Automatic actions are temporarily suspended");
    }
    
    public void stopEditingMaps() {
        this.state = LobbyState.WAITING;
        sendMessage("&eThe lobby has been set to no longer editing maps. Automatic actions resumed.");
    }
    
    public void automatic() {
        this.mode = Mode.AUTOMATIC;
    }
    
    public void manual() {
        this.mode = Mode.MANUAL;
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        this.state = LobbyState.WAITING;
    }
    
    public void startTimer() {
        this.state = LobbyState.COUNTDOWN;
        this.timer = new Timer(new LobbyTimerCallback(this)).run((lobbySettings.getTimerLength() * 1000L) + 50);
    }
    
    private int getVoteCount(int position, UUID uuid) {
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
        if (nexusPlayer == null) {
            return 0;
        }
    
        if (lobbySettings.isVoteWeight()) {
            return (int) nexusPlayer.getRank().getMultiplier();
        } else {
            return 1;
        }
    }
    
    private int getTotalMapVotes(int position) {
        int votes = 0;
        Set<UUID> playerVotes = mapVotes.get(position);
        for (UUID vote : playerVotes) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(vote);
            if (nexusPlayer == null) {
                continue;
            }
        
            if (lobbySettings.isVoteWeight()) {
                votes += nexusPlayer.getRank().getMultiplier();
            } else {
                votes++;
            }
        }
        return votes;
    }
    
    public void prepareGame() {
        this.state = LobbyState.PREPARING_GAME;
        int mapVotes = 0;
        if (this.gameMap == null) {
            GameMap mostVoted = null;
            int mostVotedVotes = 0;
            for (Entry<Integer, GameMap> entry : this.mapOptions.entrySet()) {
                if (mostVoted == null) {
                    mostVoted = entry.getValue();
                    mostVotedVotes = getTotalMapVotes(entry.getKey());
                } else {
                    int votes = getTotalMapVotes(entry.getKey());
                    if (votes > mostVotedVotes) {
                        mostVoted = entry.getValue();
                        mostVotedVotes = votes;
                    }
                }
            }
            
            if (mostVoted == null) {
                this.gameMap = new ArrayList<>(this.mapOptions.values()).get(new Random().nextInt(this.mapOptions.size()));
                this.gameMap.setVotes(0);
                sendMessage("&eThere was no map voted, so the map &b" + this.gameMap.getName() + " &ewas selected at random.");
            } else {
                this.gameMap = mostVoted;
                this.gameMap.setVotes(mostVotedVotes);
            }
        }
        
        if (this.gameSettings == null) {
            this.gameSettings = new GameSettings();
        }
        Game game = new Game(gameMap, this.gameSettings, this.players.values(), this.spectatingPlayers);
        plugin.getChatHandler().disableChat();
        this.voteStart.clear();
        plugin.setGame(game);
        if (plugin.getGamesPlayed() + 1 >= this.lobbySettings.getMaxGames()) {
            plugin.setRestart(true);
        }
        if (Game.getMode() == Mode.AUTOMATIC) {
            this.state = LobbyState.STARTING;
            game.setup();
        } else {
            sendMessage("&eThe game has been prepared and is now ready, however, it has not been started due to the game being in manual mode.");
            this.state = LobbyState.GAME_PREPARED;
        }
    }
    
    public void resetLobby() {
        this.state = LobbyState.SETUP;
        this.players.clear();
        this.spectatingPlayers.clear();
        if (timer != null) {
            if (timer.isRunning()) {
                timer.cancel();
            }
        }
        this.timer = null;
        this.gameMap = null;
        this.gameSettings = null;
        this.state = LobbyState.WAITING;
        generateMapOptions();
    }
    
    public Collection<SpigotNexusPlayer> getPlayers() {
        return this.players.values();
    }
    
    public Timer getTimer() {
        return timer;
    }
    
    public GameSettings getGameSettings() {
        return gameSettings;
    }
    
    public LobbySettings getLobbySettings() {
        return lobbySettings;
    }
    
    public GameMap getGameMap() {
        return gameMap;
    }
    
    public void addPlayer(SpigotNexusPlayer nexusPlayer) {
        if (nexusPlayer == null) {
            System.out.println("Nexus Player was null");
            return;
        }
        if (nexusPlayer.getPlayer() == null) {
            return;
        }
        this.players.put(nexusPlayer.getUniqueId(), nexusPlayer);
        int totalPlayers = 0;
        for (SpigotNexusPlayer player : this.players.values()) {
            if (!this.spectatingPlayers.contains(player.getUniqueId())) {
                totalPlayers++;
            }
        }
        
        if (this.spectatingPlayers.contains(nexusPlayer.getUniqueId())) {
            sendMessage("&a&l>> &f" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined as a spectator.");
        }
        
        if (totalPlayers > lobbySettings.getMaxPlayers()) {
            nexusPlayer.sendMessage("&eYou will be a spectator in the game as you joined with the player count above the maximum game amount. However, you can be a tribute if those before you leave or become spectators");
        }
    
        Location spawn = getSpawnpoint().clone();
        spawn.setY(spawn.getY() + 2);
        nexusPlayer.getPlayer().teleport(spawn);
        sendMessage("&a&l>> &b" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined.");
        Player player = nexusPlayer.getPlayer();
        player.setHealth(player.getMaxHealth());
        if (this.getState() != LobbyState.MAP_EDITING) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            for (PotionEffect pe : player.getActivePotionEffects()) {
                player.removePotionEffect(pe.getType());
            }
        }
        nexusPlayer.getScoreboard().setView(new LobbyScoreboardView(nexusPlayer.getScoreboard(), plugin));
        nexusPlayer.getScoreboard().setTablistHandler(new RankTablistHandler(nexusPlayer.getScoreboard()));
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
            player.showPlayer(online);
        }
        nexusPlayer.setActionBar(new LobbyActionBar(plugin));
    }
    
    public void removePlayer(SpigotNexusPlayer nexusPlayer) {
        this.players.remove(nexusPlayer.getUniqueId());
        int totalPlayers = 0;
        for (SpigotNexusPlayer player : this.players.values()) {
            if (!this.spectatingPlayers.contains(player.getUniqueId())) {
                totalPlayers++;
            }
        }
    
        sendMessage("&c&l<< &b" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft. &5(&d" + totalPlayers + "&5/&d" + lobbySettings.getMaxPlayers() + "&5)");
        
        if (this.voteStart.contains(nexusPlayer.getUniqueId())) {
            removeStartVote(nexusPlayer.getUniqueId());
        }
        
        if (this.state == LobbyState.COUNTDOWN) {
            if (totalPlayers < lobbySettings.getMinPlayers() && !(voteStart.size() >= 2)) {
                sendMessage("&cNot enough players to start.");
                if (this.timer != null) {
                    this.timer.cancel();
                    this.timer = null;
                    this.state = LobbyState.WAITING;
                }
            }
        }
    }
    
    public Mode getMode() {
        return mode;
    }
    
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    public LobbyState getState() {
        return state;
    }
    
    public void setState(LobbyState state) {
        this.state = state;
    }
    
    public void addSpectatingPlayer(UUID uuid) {
        this.spectatingPlayers.add(uuid);
    }
    
    public void removeSpectatingPlayer(UUID uuid) {
        this.spectatingPlayers.remove(uuid);
    }
    
    public List<UUID> getSpectatingPlayers() {
        return spectatingPlayers;
    }
    
    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
    
    public boolean hasPlayer(UUID uniqueId) {
        for (SpigotNexusPlayer player : this.players.values()) {
            if (player.getUniqueId().toString().equalsIgnoreCase(uniqueId.toString())) {
                return true;
            }
        }
        return false;
    }
    
    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    public void fromGame(Game game) {
        for (GamePlayer player : game.getPlayers().values()) {
            if (player.getNexusPlayer().getPlayer() != null) {
                addPlayer(player.getNexusPlayer());
            }
        }
        
        if (this.lobbySettings.isKeepPreviousGameSettings()) {
            this.gameSettings = game.getSettings();
        }
        
        game.getPlayers().clear();
        plugin.getGame().getGameMap().delete(plugin);
        plugin.setGame(null);
    }
    
    public Location getSpawnpoint() {
        return this.spawnpoint;
    }
    
    public void setSpawnpoint(Location location) {
        this.spawnpoint = location;
    }
    
    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
    
    public void playSound(Sound sound) {
        for (SpigotNexusPlayer player : this.players.values()) {
            if (player.getPlayer() != null) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), sound, 1, 1);
            }
        }
    }
    
    public Map<Integer, Location> getMapSigns() {
        return mapSigns;
    }
    
    public boolean isPlayer(UUID uniqueId) {
        return this.players.containsKey(uniqueId);
    }
    
    public void addStartVote(NexusPlayer player) {
        this.voteStart.add(player.getUniqueId());
        sendMessage("&6&l>> " + player.getRank().getColor() + player.getName() + " &ehas voted to start the lobby.");
        if (this.voteStart.size() >= 2) {
            this.startTimer();
        }
    }
    
    public void removeStartVote(UUID uuid) {
        this.voteStart.remove(uuid);
        if (this.voteStart.size() <= 1) {
            if (this.timer != null) {
                sendMessage("&6&l>> &eNot enough votes to start.");
                this.timer.cancel();
                this.timer = null;
            }
        }
    }
    
    public void addMapVote(NexusPlayer nexusPlayer, Location location) {
        for (Entry<Integer, Location> entry : this.mapSigns.entrySet()) {
            boolean contains = this.mapVotes.get(entry.getKey()).contains(nexusPlayer.getUniqueId()); 
            
            if (entry.getValue().equals(location)) {
                if (contains) {
                    nexusPlayer.sendMessage("&cYou have already voted for this map.");
                    return;
                }
                
                this.mapVotes.get(entry.getKey()).add(nexusPlayer.getUniqueId());
                nexusPlayer.sendMessage("&6&l>> &eYou voted for the map &b" + this.mapOptions.get(entry.getKey()).getName());
                return;
            } else {
                if (contains) {
                    nexusPlayer.sendMessage("&cYou cannot vote for more than one map.");
                    return;
                }
            }
        }
        
        nexusPlayer.sendMessage("&cInvalid map voting sign.");
    }
}
