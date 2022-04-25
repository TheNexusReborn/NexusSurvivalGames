package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.nexuscore.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.scoreboard.LobbyScoreboard;
import com.thenexusreborn.survivalgames.settings.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class Lobby {
    private SurvivalGames plugin;
    private List<NexusPlayer> players = new LinkedList<>();
    private List<UUID> spectatingPlayers = new ArrayList<>();
    private Timer timer;
    private GameSettings gameSettings;
    private LobbySettings lobbySettings = new LobbySettings();
    private GameMap gameMap;
    private Mode mode = Mode.MANUAL;
    private LobbyState state = LobbyState.WAITING;
    private Location spawnpoint;
    private LobbyScoreboard lobbyScoreboard;
    
    public Lobby(SurvivalGames plugin) {
        this.plugin = plugin;
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
        
        gameMap.delete(plugin);
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
        for (NexusPlayer player : this.players) {
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
    
    public void prepareGame() {
        this.state = LobbyState.PREPARING_GAME;
        if (this.gameMap == null) {
            this.gameMap = plugin.getMapManager().getMaps().get(new Random().nextInt(plugin.getMapManager().getMaps().size()));
            sendMessage("&eThere was no map set, so the map &b" + this.gameMap.getName() + " &ewas selected at random.");
        }
        
        if (this.gameSettings == null) {
            this.gameSettings = new GameSettings();
        }
        Game game = new Game(gameMap, this.gameSettings, this.players, this.spectatingPlayers);
        plugin.setGame(game);
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
    }
    
    public List<NexusPlayer> getPlayers() {
        return this.players;
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
    
    public void addPlayer(NexusPlayer nexusPlayer) {
        if (nexusPlayer == null) {
            System.out.println("Nexus Player was null");
            return;
        }
        if (nexusPlayer.getPlayer() == null) {
            return;
        }
        this.players.add(nexusPlayer);
        int totalPlayers = 0;
        for (NexusPlayer player : this.players) {
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
        sendMessage("&a&l>> &b" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined. &5(&d" + totalPlayers + "&5/&d" + lobbySettings.getMaxPlayers() + "&5)");
        Player player = nexusPlayer.getPlayer();
        player.setHealth(player.getMaxHealth());
        if (this.getState() != LobbyState.MAP_EDITING) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            for (PotionEffect pe : player.getActivePotionEffects()) {
                player.removePotionEffect(pe.getType());
            }
        }
        nexusPlayer.getScoreboard().setView(new LobbyScoreboard(nexusPlayer.getScoreboard(), plugin));
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
            player.showPlayer(online);
        }
    }
    
    public void removePlayer(NexusPlayer nexusPlayer) {
        this.players.remove(nexusPlayer);
        int totalPlayers = 0;
        for (NexusPlayer player : this.players) {
            if (!this.spectatingPlayers.contains(player.getUniqueId())) {
                totalPlayers++;
            }
        }
    
        sendMessage("&c&l<< &b" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft. &5(&d" + totalPlayers + "&5/&d" + lobbySettings.getMaxPlayers() + "&5)");
        
        if (this.state == LobbyState.COUNTDOWN) {
            if (totalPlayers < lobbySettings.getMinPlayers()) {
                sendMessage("&cNot enough players to start.");
                this.timer.cancel();
                this.timer = null;
                this.state = LobbyState.WAITING;
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
        for (NexusPlayer player : this.players) {
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
        for (NexusPlayer player : this.players) {
            if (player.getPlayer() != null) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), sound, 1, 1);
            }
        }
    }
    
    
}
