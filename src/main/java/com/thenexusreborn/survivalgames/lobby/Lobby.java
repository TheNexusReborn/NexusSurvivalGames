package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.scoreboard.impl.RankTablistHandler;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.lootv2.*;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.scoreboard.LobbyScoreboardView;
import com.thenexusreborn.survivalgames.settings.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class Lobby {
    private final SurvivalGames plugin;
    private final Map<UUID, SpigotNexusPlayer> players = new HashMap<>();
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
    private LootChances lootChances;
    
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
        
        if (NexusAPI.getApi().getTournament() != null && NexusAPI.getApi().getTournament().isActive()) {
            this.lobbySettings = plugin.getLobbySettings("tournament");
            if (lobbySettings == null) {
                this.lobbySettings = plugin.getLobbySettings("default");
            }
    
            this.gameSettings = plugin.getGameSettings("tournament");
            if (gameSettings == null) {
                this.gameSettings = plugin.getGameSettings("default");
            }
        } else {
            this.lobbySettings = plugin.getLobbySettings("default");
            this.gameSettings = plugin.getGameSettings("default");
        }
        
        generateMapOptions();
        generateLootChances();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getState() == LobbyState.MAP_EDITING) {
                    return;
                }
                
                if (mapOptions.size() < 1) {
                    return;
                }
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
                        if (!player.getPlayer().getWorld().getName().equalsIgnoreCase(spawnpoint.getWorld().getName())) { //TODO Error here
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
                if (plugin.getGame() != null) {
                    return;
                }
                
                if (players.size() == 0) {
                    return;
                }
                
                if (!(getState() == LobbyState.WAITING || getState() == LobbyState.COUNTDOWN)) {
                    return;
                }
                
                for (SpigotNexusPlayer nexusPlayer : players.values()) {
                    sendMapOptions(nexusPlayer);
                }
            }
        }.runTaskTimer(plugin, 60L, 2400);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getGame() != null) {
                    return;
                }
                
                if (players.size() == 0) {
                    return;
                }
                
                if (getState() != LobbyState.WAITING) {
                    return;
                }
                
                if (players.size() < lobbySettings.getMinPlayers()) {
                    sendMessage("&6&l>> &e&lDid you know that you can use &f&l/votestart &e&lto start a game early?");
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 2400L);
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
        this.generateLootChances();
        this.forceStarted = false;
        sendMessage(MsgType.SUCCESS + "Lobby Reset from Invalid State complete");
    }
    
    public void generateLootChances() {
        plugin.getLogger().info("Generating Loot chances");
        List<String> categoryChances = new ArrayList<>();
        Map<String, List<Material>> entryChances = new HashMap<>();
        for (LootCategory category : LootManager.getInstance().getLootTable("basic").getCategories()) {
            int amount = new Random().nextInt(category.getRarity().getMax() - category.getRarity().getMin()) + category.getRarity().getMin();
            for (int i = 0; i < amount; i++) {
                categoryChances.add(category.getName());
                for (LootEntry entry : category.getEntries()) {
                    int entryAmount = new Random().nextInt(entry.getRarity().getMax() - entry.getRarity().getMin()) + entry.getRarity().getMin();
                    List<Material> materials = entryChances.computeIfAbsent(category.getName(), k -> new ArrayList<>());
                    for (int h = 0; h < entryAmount; h++) {
                        materials.add(entry.getMaterial());
                    }
                    
                    if (materials != null) {
                        Collections.shuffle(materials);
                    }
                }
            }
        }
        
        if (categoryChances != null) {
            Collections.shuffle(categoryChances);
        }
        this.lootChances = new LootChances(categoryChances, entryChances);
    }
    
    public void setLootChances(LootChances lootChances) {
        this.lootChances = lootChances;
    }
    
    public void sendMapOptions(SpigotNexusPlayer nexusPlayer) {
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
            
            nexusPlayer.getPlayer().spigot().sendMessage(line);
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
        
        this.mapSigns.forEach((position, location) -> {
            plugin.getConfig().set("mapsigns." + position + ".world", location.getWorld().getName());
            plugin.getConfig().set("mapsigns." + position + ".x", location.getBlockX());
            plugin.getConfig().set("mapsigns." + position + ".y", location.getBlockY());
            plugin.getConfig().set("mapsigns." + position + ".z", location.getBlockZ());
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
        for (SpigotNexusPlayer player : this.players.values()) {
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
        
        Game game = new Game(gameMap, this.gameSettings, this.players.values(), this.spectatingPlayers);
        game.setLootChances(lootChances);
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
        new BukkitRunnable() {
            @Override
            public void run() {
                generateLootChances();
            }
        }.runTaskAsynchronously(plugin);
    }
    
    public Collection<SpigotNexusPlayer> getPlayers() {
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
                if (!player.getPreferences().get("vanish").getValue()) {
                    totalPlayers++;
                }
            }
        }
        
        if (totalPlayers > lobbySettings.getMaxPlayers()) {
            nexusPlayer.sendMessage("&eYou will be a spectator in the game as you joined with the player count above the maximum game amount. However, you can be a tribute if those before you leave or become spectators");
        }
        
        Location spawn = getSpawnpoint().clone();
        spawn.setY(spawn.getY() + 2);
        nexusPlayer.getPlayer().teleport(spawn);
        
        Player player = nexusPlayer.getPlayer();
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
            player.showPlayer(online);
        }
        
        if (nexusPlayer.getPreferences().get("vanish").getValue()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                SpigotNexusPlayer psp = this.players.get(p.getUniqueId());
                if (psp != null) {
                    if (psp.getRank().ordinal() > Rank.HELPER.ordinal()) {
                        p.hidePlayer(player);
                    } else {
                        psp.sendMessage("&a&l>> " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined &e&overy silently&e.");
                    }
                }
            }
        } else if (nexusPlayer.getPreferences().get("incognito").getValue()) {
            for (SpigotNexusPlayer np : this.players.values()) {
                if (np != null) {
                    if (np.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                        np.sendMessage("&a&l>> " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined &e&osilently&e.");
                    }
                }
            }
        } else {
            sendMessage("&a&l>> " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined.");
        }
        
        boolean joiningPlayerStaff = nexusPlayer.getRank().ordinal() <= Rank.HELPER.ordinal();
        for (Player p : Bukkit.getOnlinePlayers()) {
            SpigotNexusPlayer psp = this.players.get(p.getUniqueId());
            if (psp != null) {
                if (psp.getPreferences().get("vanish").getValue() && !joiningPlayerStaff) {
                    nexusPlayer.getPlayer().hidePlayer(p);
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
        nexusPlayer.getScoreboard().setView(new LobbyScoreboardView(nexusPlayer.getScoreboard(), plugin));
        nexusPlayer.getScoreboard().setTablistHandler(new RankTablistHandler(nexusPlayer.getScoreboard()));
        nexusPlayer.setActionBar(new LobbyActionBar(plugin));
        sendMapOptions(nexusPlayer);
    }
    
    public void removePlayer(SpigotNexusPlayer nexusPlayer) {
        if (!this.players.containsKey(nexusPlayer.getUniqueId())) {
            return;
        }
        this.players.remove(nexusPlayer.getUniqueId());
        this.spectatingPlayers.remove(nexusPlayer.getUniqueId());
        int totalPlayers = 0;
        for (SpigotNexusPlayer player : this.players.values()) {
            if (!this.spectatingPlayers.contains(player.getUniqueId())) {
                totalPlayers++;
            }
        }
        
        if (nexusPlayer.getPreferences().get("vanish").getValue()) {
            for (SpigotNexusPlayer snp : this.players.values()) {
                if (snp.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    snp.sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft &e&overy silently&e.");
                }
            }
        } else if (nexusPlayer.getPreferences().get("incognito").getValue()) {
            for (SpigotNexusPlayer snp : this.players.values()) {
                if (snp.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    snp.sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft &e&osilently&e.");
                }
            }
        } else {
            sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft.");
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
        Tournament tournament = NexusAPI.getApi().getTournament();
        if (tournament != null && tournament.isActive()) {
            this.lobbySettings = plugin.getLobbySettings("tournament");
            if (lobbySettings == null) {
                this.lobbySettings = plugin.getLobbySettings("default");
            }
        
            this.gameSettings = plugin.getGameSettings("tournament");
            if (gameSettings == null) {
                this.gameSettings = plugin.getGameSettings("default");
            }
        } else {
            if (this.lobbySettings.isKeepPreviousGameSettings()) {
                this.gameSettings = game.getSettings();
                if (this.gameSettings.getType().equals("tournament")) {
                    this.gameSettings = plugin.getGameSettings("default");
                }
            } else {
                this.gameSettings = plugin.getGameSettings("default");
            }
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
            for (SpigotNexusPlayer player : getPlayers()) {
                if (!getSpectatingPlayers().contains(player.getUniqueId())) {
                    if (!player.getPreferences().get("vanish").getValue()) {
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
        for (SpigotNexusPlayer player : getPlayers()) {
            if (!getSpectatingPlayers().contains(player.getUniqueId())) {
                if (!player.getPreferences().get("vanish").getValue()) {
                    playerCount++;
                }
            }
        }
        return playerCount;
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
                ", lootChances=" + lootChances +
                '}';
    }
}
