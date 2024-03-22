package com.thenexusreborn.survivalgames.lobby;

import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starchat.rooms.DefaultPermissions;
import com.stardevllc.starlib.time.TimeUnit;
import com.stardevllc.starclock.clocks.Timer;
import com.stardevllc.starmclib.actor.Actor;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.model.MapRating;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.scoreboard.impl.RankTablistHandler;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.ProgressBar;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.lobby.timer.LobbyTimerCallback;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.loot.LootTable;
import com.thenexusreborn.survivalgames.scoreboard.lobby.DebugLobbyBoard;
import com.thenexusreborn.survivalgames.scoreboard.lobby.LobbyBoard;
import com.thenexusreborn.survivalgames.scoreboard.lobby.MapEditingBoard;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.LobbySettings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
public class Lobby {
    private final SurvivalGames plugin;
    private int localId;
    private ControlType controlType = ControlType.MANUAL;
    private LobbyState state = LobbyState.WAITING;
    private ChatRoom lobbyChatRoom;
    private Timer timer;
    private final Map<UUID, LobbyPlayer> players = new HashMap<>();
    private GameSettings gameSettings;
    private LobbySettings lobbySettings;
    private Location spawnpoint;
    private SGMap gameMap;
    private final Map<Integer, Location> mapSigns = new HashMap<>();
    private final Map<Integer, SGMap> mapOptions = new HashMap<>();
    private boolean forceStarted;
    private final List<StatSign> statSigns = new ArrayList<>();
    private final List<TributeSign> tributeSigns = new ArrayList<>();
    private boolean debugMode;

    public Lobby(SurvivalGames plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Setting up the lobby.");
        
        this.localId = plugin.getLastLocalLobbyId();
        plugin.setLastLocalLobbyId(plugin.getLastLocalLobbyId() + 1);

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
        
        this.lobbyChatRoom = new ChatRoom(plugin, "room-lobby-" + getLocalId(), Actor.getServerActor(), "&8<&3%nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexuscore_displayname%%nexuscore_tag%&8: {message}", "{message}");
        plugin.getStarChat().getRoomRegistry().register(this.lobbyChatRoom.getSimplifiedName(), this.lobbyChatRoom);

        generateMapOptions();

        for (LootTable lootTable : LootManager.getInstance().getLootTables()) {
            lootTable.generateNewProbabilities(new Random());
        }
    }

    public int getLocalId() {
        return localId;
    }

    public void resetInvalidState() {
        NexusAPI.logMessage(Level.SEVERE, "Resetting Lobby from an Invalid State, see below for the stored information", this + "");

        this.players.entrySet().removeIf(entry -> Bukkit.getPlayer(entry.getKey()) == null);

        this.players.values().forEach(player -> {
            player.setSpectating(false);
            player.setMapVote(-1);
            player.setVoteStart(false);
        });

        sendMessage(MsgType.ERROR + "Resetting lobby from an Invalid State...");
        this.timer = null;
        this.gameMap = null;
        this.state = LobbyState.WAITING;
        this.generateMapOptions();
        this.forceStarted = false;
        sendMessage(MsgType.SUCCESS + "Lobby Reset from Invalid State complete");
    }

    public void sendMapOptions(NexusPlayer nexusPlayer) {
        nexusPlayer.sendMessage(MsgType.INFO + "&e&lVOTING OPTIONS - &7Click an option to vote!");
        for (Entry<Integer, SGMap> entry : mapOptions.entrySet()) {
            String mapName = entry.getValue().getName();
            StringBuilder creatorBuilder = new StringBuilder();
            for (String creator : entry.getValue().getCreators()) {
                if (creator != null && !creator.isEmpty() && !creator.equals(" ")) {
                    creatorBuilder.append(creator).append(", ");
                }
            }
            if (creatorBuilder.isEmpty()) {
                creatorBuilder.append(" ");
            }
            String creators = creatorBuilder.substring(0, creatorBuilder.length() - 2);
            String votesText = " (" + getTotalMapVotes(entry.getKey()) + " votes)";

            ComponentBuilder builder = new ComponentBuilder("").append("> ").color(ChatColor.GOLD).bold(true)
                    .append(entry.getKey() + "").color(ChatColor.RED).bold(true).append(": ").color(ChatColor.DARK_RED).bold(false)
                    .append(mapName).color(ChatColor.AQUA).append(" by ").color(ChatColor.GRAY).italic(true)
                    .append(creators).italic(false).color(ChatColor.DARK_AQUA).append(votesText).color(ChatColor.GRAY).italic(true);

            TextComponent line = new TextComponent(builder.create());
            line.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/mapvote " + entry.getKey()));
            line.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to vote").create()));

            Bukkit.getPlayer(nexusPlayer.getUniqueId()).spigot().sendMessage(line);
        }
    }

    public Map<Integer, SGMap> getMapOptions() {
        return mapOptions;
    }

    public void handleShutdown() {
        this.state = LobbyState.SHUTTING_DOWN;
        sendMessage("&4&l>> THE SERVER IS SHUTTING DOWN!");
        if (timer != null) {
            timer.cancel();
        }

        if (gameMap != null) {
            gameMap.removeFromServer(plugin);
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

    public List<LobbyPlayer> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

    public void generateMapOptions() {
        plugin.getLogger().info("Generating Map Options");
        this.mapOptions.clear();

        getPlayers().forEach(player -> player.setMapVote(-1));

        if (plugin.getMapManager().getMaps().size() == 1 && !this.mapSigns.isEmpty()) {
            this.mapOptions.put(1, plugin.getMapManager().getMaps().get(0));
        } else if (plugin.getMapManager().getMaps().size() >= this.mapSigns.size()) {
            List<SGMap> maps = new ArrayList<>(plugin.getMapManager().getMaps());
            for (Integer position : new HashSet<>(this.mapSigns.keySet())) {
                SGMap map;
                int index;
                do {
                    index = new Random().nextInt(maps.size());
                    map = maps.get(index);
                } while (!map.isActive());
                this.mapOptions.put(position, map);
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
        this.lobbyChatRoom.sendMessage(message);
        Bukkit.getConsoleSender().sendMessage(MCUtils.color(message));
    }

    public void editMaps() {
        this.state = LobbyState.MAP_EDITING;
        if (timer != null) {
            this.timer.cancel();
            this.timer = null;
        }

        for (LobbyPlayer player : this.getPlayers()) {
            player.getPlayer().getScoreboard().setView(new MapEditingBoard(player.getPlayer().getScoreboard(), plugin));
        }

        sendMessage("&eThe lobby has been set to editing maps. Automatic actions are temporarily suspended");
    }

    public void stopEditingMaps() {
        this.state = LobbyState.WAITING;

        for (LobbyPlayer player : this.getPlayers()) {
            player.getPlayer().getScoreboard().setView(new LobbyBoard(player.getPlayer().getScoreboard(), this));
        }

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
        this.timer = NexusAPI.getApi().getClockManager().createTimer(TimeUnit.SECONDS.toMillis(lobbySettings.getTimerLength()));
        this.timer.addRepeatingCallback(new LobbyTimerCallback(this), TimeUnit.SECONDS.toMillis(1));
        this.timer.start();
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

    public int getTotalMapVotes(int position) {
        int votes = 0;
        for (LobbyPlayer player : getPlayers()) {
            if (player.getMapVote() == position) {
                if (getLobbySettings().isVoteWeight()) {
                    votes += (int) player.getRank().getMultiplier();
                } else {
                    votes += 1;
                }
            }
        }
        return votes;
    }

    public void prepareGame() {
        this.state = LobbyState.PREPARING_GAME;
        if (this.gameMap == null) {
            SGMap mostVoted = null;
            int mostVotedVotes = 0;
            for (Entry<Integer, SGMap> entry : this.mapOptions.entrySet()) {
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

        sendMessage("");
        sendMessage("&6&l>> &a&lTHE MAP HAS BEEN SELECTED!");
        sendMessage("&6&l> &7Map: &e" + gameMap.getName());
        int totalRatings = gameMap.getRatings().size();

        String ratingMsg;
        if (totalRatings == 0) {
            ratingMsg = "&cNo Ratings Yet";
        } else {
            int rating = 0;
            for (MapRating playerRating : gameMap.getRatings().values()) {
                rating += playerRating.getRating();
            }
            
            double ratingRatio = rating * 1.0 / totalRatings;
            int ratingPercent = (int) (ratingRatio * 100);

            ratingMsg = "&7Rating: " + new ProgressBar(ratingPercent, 100, 5, "✦ ", "&a", "&7").display() + " &7&o(rating: " + rating / totalRatings + " star(s), based on: " + totalRatings + " vote(s))";
        }

        sendMessage("&6&l> " + ratingMsg);
        sendMessage("&6&l> &7Votes: &e" + gameMap.getVotes());
        sendMessage("");

        Game game = new Game(gameMap, this.gameSettings, getPlayers());
        this.players.clear();
        plugin.setGame(game);
        if (game.getControlType() == ControlType.AUTOMATIC) {
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
        if (timer != null) {
            timer.cancel();
        }

        this.timer = null;
        this.gameMap = null;
        this.state = LobbyState.WAITING;
        this.forceStarted = false;
        generateMapOptions();
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

    public SGMap getGameMap() {
        return gameMap;
    }

    public void addPlayer(NexusPlayer nexusPlayer) {
        if (nexusPlayer == null) {
            return;
        }

        if (nexusPlayer.getPlayer() == null) {
            return;
        }

        this.players.put(nexusPlayer.getUniqueId(), new LobbyPlayer(nexusPlayer));
        this.lobbyChatRoom.addMember(nexusPlayer.getUniqueId(), DefaultPermissions.VIEW_MESSAGES, DefaultPermissions.SEND_MESSAGES);
        this.plugin.getStarChat().setPlayerFocus(Bukkit.getPlayer(nexusPlayer.getUniqueId()), this.lobbyChatRoom);

        int totalPlayers = 0;
        for (LobbyPlayer player : getPlayers()) {
            if (player.isSpectating()) {
                continue;
            }

            totalPlayers++;
        }

        if (totalPlayers > lobbySettings.getMaxPlayers()) {
            nexusPlayer.sendMessage("&eYou will be a spectator in the game as you joined with the player count above the maximum game amount. However, you can be a tribute if those before you leave or become spectators");
        }

        Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());

        Location spawn = getSpawnpoint().clone();
        spawn.setY(spawn.getY() + 2);
        player.teleport(spawn);
        player.setMaxHealth(20);
        player.setLevel(0);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
            player.showPlayer(online);
        }

        if (nexusPlayer.getToggleValue("vanish")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                LobbyPlayer psp = this.players.get(p.getUniqueId());
                if (psp != null) {
                    if (psp.getRank().ordinal() > Rank.HELPER.ordinal()) {
                        p.hidePlayer(player);
                    } else {
                        psp.sendMessage("&a&l>> " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined &e&overy silently&e.");
                    }
                }
            }
        } else if (nexusPlayer.getToggleValue("incognito")) {
            for (LobbyPlayer np : getPlayers()) {
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
            LobbyPlayer psp = this.players.get(p.getUniqueId());
            if (psp != null) {
                if (psp.getToggleValue("vanish") && !joiningPlayerStaff) {
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

            boolean sponsors = nexusPlayer.getToggleValue("allowsponsors");
            Material sponsorsItemMaterial = sponsors ? Material.GLOWSTONE_DUST : Material.SULPHUR;
            player.getInventory().setItem(0, ItemBuilder.start(sponsorsItemMaterial).displayName("&e&lSponsors &7&o(Right click to toggle)").build());
            player.getInventory().setItem(8, ItemBuilder.start(Material.WOOD_DOOR).displayName("&e&lReturn to Hub &7(Right Click)").build());
        }

        if (nexusPlayer.getRank().ordinal() <= Rank.DIAMOND.ordinal()) {
            player.setAllowFlight(nexusPlayer.getToggleValue("fly"));
        }

        if (this.debugMode) {
            nexusPlayer.getScoreboard().setView(new DebugLobbyBoard(nexusPlayer.getScoreboard(), this));
        } else {
            nexusPlayer.getScoreboard().setView(new LobbyBoard(nexusPlayer.getScoreboard(), this));
        }
        nexusPlayer.getScoreboard().setTablistHandler(new RankTablistHandler(nexusPlayer.getScoreboard()));
        nexusPlayer.setActionBar(new LobbyActionBar(plugin));
        sendMapOptions(nexusPlayer);
    }

    public void removePlayer(NexusPlayer nexusPlayer) {
        if (!this.players.containsKey(nexusPlayer.getUniqueId())) {
            return;
        }
        this.lobbyChatRoom.removeMember(nexusPlayer.getUniqueId());
        this.players.remove(nexusPlayer.getUniqueId());
        int totalPlayers = 0;
        for (LobbyPlayer player : getPlayers()) {
            if (player.isSpectating()) {
                continue;
            }
            totalPlayers++;
        }

        if (nexusPlayer.getToggleValue("vanish")) {
            for (LobbyPlayer snp : getPlayers()) {
                if (snp.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    snp.sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft &e&ovanished&e.");
                }
            }
        } else if (nexusPlayer.getToggleValue("incognito")) {
            for (LobbyPlayer snp : getPlayers()) {
                if (snp.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    snp.sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft &e&osilently&e.");
                }
            }
        } else {
            sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft.");
        }

        if (this.state == LobbyState.COUNTDOWN) {
            if (totalPlayers < lobbySettings.getMinPlayers() && !(getVoteStartCount() >= 2)) {
                if (getPlayers().size() > 1 && !forceStarted) {
                    sendMessage("&cNot enough players to start.");
                    if (this.timer != null) {
                        this.timer.cancel();
                        this.timer = null;
                    }

                    this.state = LobbyState.WAITING;
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
        this.players.get(uuid).setSpectating(true);
    }

    public void removeSpectatingPlayer(UUID uuid) {
        this.players.get(uuid).setSpectating(false);
    }

    public List<UUID> getSpectatingPlayers() {
        return getPlayers().stream().filter(LobbyPlayer::isSpectating).map(LobbyPlayer::getUniqueId).collect(Collectors.toList());
    }

    public void setGameMap(SGMap gameMap) {
        this.gameMap = gameMap;
    }

    public boolean hasPlayer(UUID uniqueId) {
        for (LobbyPlayer player : getPlayers()) {
            if (player.getUniqueId().equals(uniqueId)) {
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

        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            addPlayer(nexusPlayer);
        }

        plugin.getGame().getGameMap().removeFromServer(plugin);
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
        for (LobbyPlayer player : getPlayers()) {
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

    public int getVoteStartCount() {
        int count = 0;
        for (LobbyPlayer player : getPlayers()) {
            if (player.isVoteStart()) {
                count++;
            }
        }

        return count;
    }

    public void addStartVote(NexusPlayer player) {
        this.players.get(player.getUniqueId()).setVoteStart(true);
        sendMessage("&6&l>> " + player.getColoredName() + " &evoted to run the game early. &7&o(/votestart)");
        int votes = getVoteStartCount();
        int threshold = getLobbySettings().getVoteStartThreshold();
        int neededVotes = Math.max(threshold - votes, 0);
        sendMessage("&6&l>> &eVotes: &l" + votes + "/" + threshold + " &f(" + neededVotes + " Votes Needed)");

        if (this.state != LobbyState.WAITING) {
            return;
        }

        if (votes < threshold) {
            return;
        }

        if (!getLobbySettings().isAllowVoteStart()) {
            return;
        }

        if (getPlayingCount() > getLobbySettings().getVoteStartAvailableThreshold()) {
            return;
        }

        sendMessage("&6&l>> &f&lThe game lobby has been started!");
        this.startTimer();
    }

    public boolean hasVotedToStart(NexusPlayer player) {
        return this.players.get(player.getUniqueId()).isVoteStart();
    }

    public void removeStartVote(UUID uuid) {
        this.players.get(uuid).setVoteStart(false);

        if (getVoteStartCount() < getLobbySettings().getVoteStartThreshold()) {
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

        LobbyPlayer player = this.players.get(nexusPlayer.getUniqueId());

        for (Entry<Integer, Location> entry : this.mapSigns.entrySet()) {
            if (entry.getValue().equals(location)) {
                if (entry.getKey() == player.getMapVote()) {
                    nexusPlayer.sendMessage("&cYou have already voted for this map.");
                    return;
                }

                if (player.getMapVote() > -1) {
                    player.sendMessage("&6&l>> &eYou changed your vote to &b" + this.mapOptions.get(entry.getKey()).getName());
                } else {
                    player.sendMessage("&6&l>> &eYou voted for the map &b" + this.mapOptions.get(entry.getKey()).getName());
                }
                player.setMapVote(entry.getKey());
                return;
            }
        }
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
        if (this.timer != null) {
            int playerCount = 0;
            for (LobbyPlayer player : getPlayers()) {
                if (!player.isSpectating()) {
                    playerCount++;
                }
            }
            
            this.timer.setLengthAndReset(TimeUnit.SECONDS.toMillis(settings.getTimerLength()));

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
        for (LobbyPlayer player : getPlayers()) {
            if (!player.isSpectating()) {
                playerCount++;
            }
        }
        return playerCount;
    }

    public List<StatSign> getStatSigns() {
        return statSigns;
    }

    public List<TributeSign> getTributeSigns() {
        return tributeSigns;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void enableDebug() {
        this.debugMode = true;
        for (LobbyPlayer player : this.getPlayers()) {
            player.getPlayer().getScoreboard().setView(new DebugLobbyBoard(player.getPlayer().getScoreboard(), this));
        }
    }

    public void disableDebug() {
        this.debugMode = false;
        for (LobbyPlayer player : this.getPlayers()) {
            player.getPlayer().getScoreboard().setView(new LobbyBoard(player.getPlayer().getScoreboard(), this));
        }
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "plugin=" + plugin +
                ", controlType=" + controlType +
                ", state=" + state +
                ", timer=" + timer +
                ", players=" + players +
                ", gameSettings=" + gameSettings +
                ", lobbySettings=" + lobbySettings +
                ", spawnpoint=" + spawnpoint +
                ", gameMap=" + gameMap +
                ", mapSigns=" + mapSigns +
                ", mapOptions=" + mapOptions +
                ", forceStarted=" + forceStarted +
                ", statSigns=" + statSigns +
                ", tributeSigns=" + tributeSigns +
                ", debugMode=" + debugMode +
                '}';
    }

    public void recalculateVisibility() {
        for (LobbyPlayer player : this.getPlayers()) {
            Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
            boolean vanish = player.getToggleValue("vanish");
            for (LobbyPlayer other : this.getPlayers()) {
                Player otherBukkit = Bukkit.getPlayer(other.getUniqueId());
                if (!vanish) {
                    otherBukkit.showPlayer(bukkitPlayer);
                } else {
                    if (other.getRank().ordinal() > Rank.HELPER.ordinal()) {
                        otherBukkit.hidePlayer(bukkitPlayer);
                    }
                }
            }
        }
    }
}