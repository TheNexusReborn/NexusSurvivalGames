package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.scoreboard.impl.RankTablistHandler;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.loot.*;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.scoreboard.LobbyScoreboardView;
import com.thenexusreborn.survivalgames.settings.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class Lobby {
    private final SurvivalGames plugin;
    private final Map<UUID, NexusPlayer> players = new HashMap<>();
    private final List<UUID> spectatingPlayers = new ArrayList<>();
    private Timer timer;
    private GameSettings gameSettings;
    private LobbySettings lobbySettings;
    private GameMap gameMap;
    private ControlType controlType = ControlType.MANUAL;
    private LobbyState state = LobbyState.WAITING;
    private Location spawnpoint;
    private final Set<UUID> voteStart = new HashSet<>();
    private final Map<Integer, Location> mapSigns = new HashMap<>();
    private final Map<Integer, GameMap> mapOptions = new HashMap<>();
    private final Map<Integer, Set<UUID>> mapVotes = new HashMap<>();
    private boolean forceStarted;
    private final List<StatSign> statSigns = new ArrayList<>();
    private final List<TributeSign> tributeSigns = new ArrayList<>();
    
    public Lobby(SurvivalGames plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Setting up the lobby.");
        
        if (plugin.getConfig().contains("mapsigns")) {
            plugin.getLogger().info("Loading Map Signs");
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
            plugin.getLogger().info("Map Signs Loaded");
        }
        
        if (plugin.getConfig().contains("statsigns")) {
            plugin.getLogger().info("Loading Stat Signs");
            ConfigurationSection signsSection = plugin.getConfig().getConfigurationSection("statsigns");
            for (String key : signsSection.getKeys(false)) {
                World world = Bukkit.getWorld(signsSection.getString(key + ".world"));
                int x = signsSection.getInt(key + ".x");
                int y = signsSection.getInt(key + ".y");
                int z = signsSection.getInt(key + ".z");
                Location location = new Location(world, x, y, z);
                String displayName = signsSection.getString(key + ".displayName");
                this.statSigns.add(new StatSign(location, key, displayName));
            }
        }
        
        if (plugin.getConfig().contains("tributesigns")) {
            plugin.getLogger().info("Loading Tribute Signs");
            ConfigurationSection signsSection = plugin.getConfig().getConfigurationSection("tributesigns");
            for (String key : signsSection.getKeys(false)) {
                int index = Integer.parseInt(key);
                World world = Bukkit.getWorld(signsSection.getString(key + ".world"));
                int signX = signsSection.getInt(key + ".sign.x");
                int signY = signsSection.getInt(key + ".sign.y");
                int signZ = signsSection.getInt(key + ".sign.z");
                
                int headX = signsSection.getInt(key + ".head.x");
                int headY = signsSection.getInt(key + ".head.y");
                int headZ = signsSection.getInt(key + ".head.z");
                
                Location signLocation = new Location(world, signX, signY, signZ);
                Location headLocation = new Location(world, headX, headY, headZ);
                
                TributeSign tributeSign = new TributeSign(index, signLocation, headLocation);
                this.tributeSigns.add(tributeSign);
            }
        }
        
        this.lobbySettings = plugin.getLobbySettings("default");
        this.gameSettings = plugin.getGameSettings("default");
        
        generateMapOptions();
        
        for (LootTable lootTable : LootManager.getInstance().getLootTables()) {
            lootTable.generateNewProbabilities(new Random());
        }
    }
    
    public void resetInvalidState() {
        NexusAPI.logMessage(Level.SEVERE, "Resetting Lobby from an Invalid State, see below for the stored information", this + "");
        
        this.players.entrySet().removeIf(entry -> Bukkit.getPlayer(entry.getKey()) == null);
        
        sendMessage(MsgType.ERROR + "Resetting lobby from an Invalid State...");
        this.timer = null;
        this.gameMap = null;
        this.state = LobbyState.WAITING;
        this.voteStart.clear();
        this.generateMapOptions();
        this.forceStarted = false;
        sendMessage(MsgType.SUCCESS + "Lobby Reset from Invalid State complete");
    }
    
    public void sendMapOptions(NexusPlayer nexusPlayer) {
        nexusPlayer.sendMessage(MsgType.INFO + "&e&lVOTING OPTIONS - &7Click an option to vote!");
        for (Entry<Integer, GameMap> entry : mapOptions.entrySet()) {
            String mapName = entry.getValue().getName();
            StringBuilder creatorBuilder = new StringBuilder();
            for (String creator : entry.getValue().getCreators()) {
                if (creator != null && !creator.equals("") && !creator.equals(" ")) {
                    creatorBuilder.append(creator).append(", ");
                }
            }
            if (creatorBuilder.length() == 0) {
                creatorBuilder.append(" ");
            }
            String creators = creatorBuilder.substring(0, creatorBuilder.length() - 2);
            String votesText = " (" + getTotalMapVotes(entry.getKey()) + " votes)";
            
            ComponentBuilder builder = new ComponentBuilder("").append("> ").color(ChatColor.GOLD).bold(true)
                    .append(entry.getKey() + "").color(ChatColor.RED).bold(true).append(": ").color(ChatColor.DARK_RED).bold(false)
                    .append(mapName).color(ChatColor.AQUA).append(" by ").color(ChatColor.GRAY).italic(true)
                    .append(creators).italic(false).color(ChatColor.DARK_AQUA).append(votesText).color(ChatColor.GRAY).italic(true);
            
            TextComponent line = new TextComponent(builder.create());
            line.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/map " + entry.getKey()));
            line.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to vote").create()));
            
            Bukkit.getPlayer(nexusPlayer.getUniqueId()).spigot().sendMessage(line);
        }
    }
    
    public Map<Integer, GameMap> getMapOptions() {
        return mapOptions;
    }
    
    public void handleShutdown() {
        this.state = LobbyState.SHUTTING_DOWN;
        sendMessage("&4&l>> THE SERVER IS SHUTTING DOWN!");
        if (timer != null) {
            timer.cancel();
        }
        
        if (gameMap != null) {
            gameMap.delete(plugin);
        }
        
        FileConfiguration config = plugin.getConfig();
        this.mapSigns.forEach((position, location) -> {
            config.set("mapsigns." + position + ".world", location.getWorld().getName());
            config.set("mapsigns." + position + ".x", location.getBlockX());
            config.set("mapsigns." + position + ".y", location.getBlockY());
            config.set("mapsigns." + position + ".z", location.getBlockZ());
        });
        
        this.statSigns.forEach(statSign -> {
            Location location = statSign.getLocation();
            config.set("statsigns." + statSign.getStat() + ".world", location.getWorld().getName());
            config.set("statsigns." + statSign.getStat() + ".x", location.getBlockX());
            config.set("statsigns." + statSign.getStat() + ".y", location.getBlockY());
            config.set("statsigns." + statSign.getStat() + ".z", location.getBlockZ());
            config.set("statsigns." + statSign.getStat() + ".displayName", statSign.getDisplayName());
        });
        
        this.tributeSigns.forEach(tributeSign -> {
            Location sl = tributeSign.getSignLocation();
            Location hl = tributeSign.getHeadLocation();
            
            config.set("tributesigns." + tributeSign.getIndex() + ".world", sl.getWorld().getName());
            config.set("tributesigns." + tributeSign.getIndex() + ".sign.x", sl.getBlockX());
            config.set("tributesigns." + tributeSign.getIndex() + ".sign.y", sl.getBlockY());
            config.set("tributesigns." + tributeSign.getIndex() + ".sign.z", sl.getBlockZ());
            config.set("tributesigns." + tributeSign.getIndex() + ".head.x", hl.getBlockX());
            config.set("tributesigns." + tributeSign.getIndex() + ".head.y", hl.getBlockY());
            config.set("tributesigns." + tributeSign.getIndex() + ".head.z", hl.getBlockZ());
        });
    }
    
    public void generateMapOptions() {
        plugin.getLogger().info("Generating Map Options");
        this.mapOptions.clear();
        this.mapVotes.clear();
        
        
        if (plugin.getMapManager().getMaps().size() == 1 && this.mapSigns.size() == 1) {
            this.mapOptions.put(1, plugin.getMapManager().getMaps().get(0));
            this.mapVotes.put(1, new HashSet<>());
        } else if (plugin.getMapManager().getMaps().size() >= this.mapSigns.size()) {
            List<GameMap> maps = new ArrayList<>(plugin.getMapManager().getMaps());
            for (Integer position : new HashSet<>(this.mapSigns.keySet())) {
                GameMap map;
                int index;
                do {
                    index = new Random().nextInt(maps.size());
                    map = maps.get(index);
                } while (!map.isActive());
                this.mapOptions.put(position, map);
                this.mapVotes.put(position, new HashSet<>());
                maps.remove(index);
            }
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
        for (NexusPlayer player : this.players.values()) {
            player.sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(MCUtils.color(message));
    }
    
    public void editMaps() {
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
        this.controlType = ControlType.AUTOMATIC;
    }
    
    public void manual() {
        this.controlType = ControlType.MANUAL;
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
            return (int) nexusPlayer.getRanks().get().getMultiplier();
        } else {
            return 1;
        }
    }
    
    public int getTotalMapVotes(int position) {
        int votes = 0;
        Set<UUID> playerVotes = mapVotes.get(position);
        for (UUID vote : playerVotes) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(vote);
            if (nexusPlayer == null) {
                continue;
            }
            
            if (lobbySettings.isVoteWeight()) {
                votes += nexusPlayer.getRanks().get().getMultiplier();
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
        
        Game game = new Game(gameMap, this.gameSettings, this.players.values(), this.spectatingPlayers);
        plugin.getChatHandler().disableChat();
        this.voteStart.clear();
        plugin.setGame(game);
        if (plugin.getGamesPlayed() + 1 >= this.lobbySettings.getMaxGames()) {
            plugin.setRestart(true);
        }
        if (Game.getControlType() == ControlType.AUTOMATIC) {
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
        this.state = LobbyState.WAITING;
        this.forceStarted = false;
        generateMapOptions();
    }
    
    public List<NexusPlayer> getPlayers() {
        return new ArrayList<>(this.players.values());
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
        
        this.players.put(nexusPlayer.getUniqueId(), nexusPlayer);
        
        int totalPlayers = 0;
        for (NexusPlayer player : this.players.values()) {
            if (!this.spectatingPlayers.contains(player.getUniqueId())) {
                if (!player.getToggles().getValue("vanish")) {
                    totalPlayers++;
                }
            }
        }
        
        if (totalPlayers > lobbySettings.getMaxPlayers()) {
            nexusPlayer.sendMessage("&eYou will be a spectator in the game as you joined with the player count above the maximum game amount. However, you can be a tribute if those before you leave or become spectators");
        }
        Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
        
        Location spawn = getSpawnpoint().clone();
        spawn.setY(spawn.getY() + 2);
        player.teleport(spawn);
        
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
            player.showPlayer(online);
        }
        
        if (nexusPlayer.getToggles().getValue("vanish")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                NexusPlayer psp = this.players.get(p.getUniqueId());
                if (psp != null) {
                    if (psp.getRanks().get().ordinal() > Rank.HELPER.ordinal()) {
                        p.hidePlayer(player);
                    } else {
                        psp.sendMessage("&a&l>> " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &ejoined &e&overy silently&e.");
                    }
                }
            }
        } else if (nexusPlayer.getToggles().getValue("incognito")) {
            for (NexusPlayer np : this.players.values()) {
                if (np != null) {
                    if (np.getRanks().get().ordinal() <= Rank.HELPER.ordinal()) {
                        np.sendMessage("&a&l>> " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &ejoined &e&osilently&e.");
                    }
                }
            }
        } else {
            sendMessage("&a&l>> " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &ejoined.");
        }
        
        boolean joiningPlayerStaff = nexusPlayer.getRanks().get().ordinal() <= Rank.HELPER.ordinal();
        for (Player p : Bukkit.getOnlinePlayers()) {
            NexusPlayer psp = this.players.get(p.getUniqueId());
            if (psp != null) {
                if (psp.getToggles().getValue("vanish") && !joiningPlayerStaff) {
                    Bukkit.getPlayer(nexusPlayer.getUniqueId()).hidePlayer(p);
                }
            }
        }
        
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SURVIVAL);
        if (this.getState() != LobbyState.MAP_EDITING) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            for (PotionEffect pe : player.getActivePotionEffects()) {
                player.removePotionEffect(pe.getType());
            }
        }
        
        if (nexusPlayer.getRanks().get().ordinal() <= Rank.DIAMOND.ordinal()) {
            player.setAllowFlight(nexusPlayer.getToggles().getValue("fly"));
        }
        
        nexusPlayer.getScoreboard().setView(new LobbyScoreboardView(nexusPlayer.getScoreboard(), plugin));
        nexusPlayer.getScoreboard().setTablistHandler(new RankTablistHandler(nexusPlayer.getScoreboard()));
        nexusPlayer.setActionBar(new LobbyActionBar(plugin));
        sendMapOptions(nexusPlayer);
    }
    
    public void removePlayer(NexusPlayer nexusPlayer) {
        if (!this.players.containsKey(nexusPlayer.getUniqueId())) {
            return;
        }
        this.players.remove(nexusPlayer.getUniqueId());
        this.spectatingPlayers.remove(nexusPlayer.getUniqueId());
        int totalPlayers = 0;
        for (NexusPlayer player : this.players.values()) {
            if (!this.spectatingPlayers.contains(player.getUniqueId())) {
                totalPlayers++;
            }
        }
        
        if (nexusPlayer.getToggles().getValue("vanish")) {
            for (NexusPlayer snp : this.players.values()) {
                if (snp.getRanks().get().ordinal() <= Rank.HELPER.ordinal()) {
                    snp.sendMessage("&c&l<< " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &eleft &e&overy silently&e.");
                }
            }
        } else if (nexusPlayer.getToggles().getValue("incognito")) {
            for (NexusPlayer snp : this.players.values()) {
                if (snp.getRanks().get().ordinal() <= Rank.HELPER.ordinal()) {
                    snp.sendMessage("&c&l<< " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &eleft &e&osilently&e.");
                }
            }
        } else {
            sendMessage("&c&l<< " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &eleft.");
        }
        
        if (this.voteStart.contains(nexusPlayer.getUniqueId())) {
            removeStartVote(nexusPlayer.getUniqueId());
        }
        
        for (Set<UUID> votes : this.mapVotes.values()) {
            votes.remove(nexusPlayer.getUniqueId());
        }
        
        if (this.state == LobbyState.COUNTDOWN) {
            if (totalPlayers < lobbySettings.getMinPlayers() && !(voteStart.size() >= 2)) {
                if (this.players.size() > 1 && !forceStarted) {
                    sendMessage("&cNot enough players to start.");
                    if (this.timer != null) {
                        this.timer.cancel();
                        this.timer = null;
                        this.state = LobbyState.WAITING;
                    }
                }
            }
        }
    }
    
    public ControlType getControlType() {
        return controlType;
    }
    
    public void setControlType(ControlType controlType) {
        this.controlType = controlType;
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
        for (NexusPlayer player : this.players.values()) {
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
        if (this.lobbySettings.isKeepPreviousGameSettings()) {
            this.gameSettings = game.getSettings();
        } else {
            this.gameSettings = plugin.getGameSettings("default");
        }
        
        for (GamePlayer player : game.getPlayers().values()) {
            if (player.getNexusPlayer().getPlayer() != null) {
                addPlayer(player.getNexusPlayer());
            }
        }
        
        game.getPlayers().clear();
        plugin.getGame().getGameMap().delete(plugin);
        plugin.setGame(null);
        plugin.getChatHandler().enableChat();
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
        for (NexusPlayer player : this.players.values()) {
            Player p = Bukkit.getPlayer(player.getUniqueId());
            if (p != null) {
                p.playSound(p.getLocation(), sound, 1, 1);
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
        sendMessage("&6&l>> " + player.getRanks().get().getColor() + player.getName() + " &ehas voted to start the lobby.");
        if (this.state == LobbyState.WAITING) {
            if (this.voteStart.size() >= 2) {
                this.startTimer();
            }
        }
    }
    
    public boolean hasVotedToStart(NexusPlayer player) {
        return this.voteStart.contains(player.getUniqueId());
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
        if (plugin.getGame() != null) {
            return;
        }
        for (Set<UUID> value : this.mapVotes.values()) {
            if (value.contains(nexusPlayer.getUniqueId())) {
                nexusPlayer.sendMessage("&cYou cannot vote for more than one map.");
                return;
            }
        }
        
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
            }
        }
        
        nexusPlayer.sendMessage("&cInvalid map voting sign.");
    }
    
    public void forceStart() {
        this.forceStarted = true;
        this.startTimer();
    }
    
    public boolean isForceStarted() {
        return forceStarted;
    }
    
    public void setLobbySettings(LobbySettings settings) {
        this.lobbySettings = settings;
        if (this.timer != null && this.timer.isRunning()) {
            int playerCount = 0;
            for (NexusPlayer player : getPlayers()) {
                if (!getSpectatingPlayers().contains(player.getUniqueId())) {
                    if (!player.getToggles().getValue("vanish")) {
                        playerCount++;
                    }
                }
            }
            
            if (playerCount < this.lobbySettings.getMinPlayers()) {
                if (!this.forceStarted) {
                    sendMessage("&6&l>> &cNot enough players to start.");
                    this.state = LobbyState.WAITING;
                    timer.cancel();
                    timer = null;
                }
            }
        }
    }
    
    public int getPlayingCount() {
        int playerCount = 0;
        for (NexusPlayer player : getPlayers()) {
            if (!getSpectatingPlayers().contains(player.getUniqueId())) {
                if (!player.getToggles().getValue("vanish")) {
                    playerCount++;
                }
            }
        }
        return playerCount;
    }
    
    public List<StatSign> getStatSigns() {
        return statSigns;
    }
    
    public Map<Integer, Set<UUID>> getMapVotes() {
        return this.mapVotes;
    }
    
    public List<TributeSign> getTributeSigns() {
        return tributeSigns;
    }
    
    @Override
    public String toString() {
        return "Lobby{" +
                "plugin=" + plugin.getName() +
                ", players=" + players +
                ", spectatingPlayers=" + spectatingPlayers +
                ", timer=" + timer +
                ", gameSettings=" + gameSettings +
                ", lobbySettings=" + lobbySettings +
                ", gameMap=" + gameMap +
                ", controlType=" + controlType +
                ", state=" + state +
                ", spawnpoint=" + spawnpoint +
                ", voteStart=" + voteStart +
                ", mapSigns=" + mapSigns +
                ", mapOptions=" + mapOptions +
                ", mapVotes=" + mapVotes +
                ", forceStarted=" + forceStarted +
                '}';
    }
    
    public void recaculateVisibility() {
        for (NexusPlayer player : this.getPlayers()) {
            Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
            boolean vanish = player.getToggles().getValue("vanish");
            for (NexusPlayer other : this.getPlayers()) {
                Player otherBukkit = Bukkit.getPlayer(other.getUniqueId());
                if (!vanish) {
                    otherBukkit.showPlayer(bukkitPlayer);
                } else {
                    if (other.getRanks().get().ordinal() > Rank.HELPER.ordinal()) {
                        otherBukkit.hidePlayer(bukkitPlayer);
                    }
                }
            }
        }
    }
}