package com.thenexusreborn.survivalgames.lobby;

import com.stardevllc.starlib.clock.clocks.Timer;
import com.stardevllc.starlib.helper.*;
import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starchat.rooms.DefaultPermissions;
import com.stardevllc.starcore.utils.ProgressBar;
import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.model.MapRating;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.scoreboard.impl.RankTablistHandler;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.chat.LobbyChatRoom;
import com.thenexusreborn.survivalgames.control.ControlType;
import com.thenexusreborn.survivalgames.control.Controllable;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.lobby.timer.LobbyTimerCallback;
import com.thenexusreborn.survivalgames.scoreboard.lobby.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.LobbySettings;
import com.thenexusreborn.survivalgames.state.IHasState;
import com.thenexusreborn.survivalgames.util.PlayerState;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Lobby implements Controllable, IHasState {
    public static final PlayerState PLAYER_STATE = new PlayerState()
            .totalExperience(0).level(0).exp(0)
            .allowFlight(false).flying(false)
            .clearInventory(true).clearEffects(true)
            .maxHealth(20).health(20)
            .collisions(true)
            .gameMode(GameMode.SURVIVAL);
    
    private final SurvivalGames plugin;
    private final SGVirtualServer server;
    private ControlType controlType = ControlType.AUTO;
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
    
    private Map<GameModifier, Set<UUID>> modifierYesVotes = new EnumMap<>(GameModifier.class);
    private Map<GameModifier, Set<UUID>> modifierNoVotes = new EnumMap<>(GameModifier.class);
    
    private SGMode mode;

    private World world;

    private File file;
    private FileConfiguration config;

    private File worldFolder;

    public Lobby(SurvivalGames plugin, SGVirtualServer server, LobbyType type) {
        this.plugin = plugin;
        this.server = server;
        
        this.mode = SGMode.CLASSIC;
        this.gameSettings = this.mode.getDefaultSettings().clone();

        this.file = new File(plugin.getDataFolder() + File.separator + "lobby" + File.separator + type.name().toLowerCase() + ".yml");
        if (!file.exists()) {
            file.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }
    
    public void addModifierYesVote(GameModifier gameModifier, UUID player) {
        if (this.modifierYesVotes.containsKey(gameModifier)) {
            this.modifierYesVotes.get(gameModifier).add(player);
        } else {
            this.modifierYesVotes.put(gameModifier, new HashSet<>(Set.of(player)));
        }
        
        if (this.modifierNoVotes.containsKey(gameModifier)) {
            this.modifierNoVotes.get(gameModifier).remove(player);
        }
    }
    
    public void addModifierNoVote(GameModifier gameModifier, UUID player) {
        if (this.modifierNoVotes.containsKey(gameModifier)) {
            this.modifierNoVotes.get(gameModifier).add(player);
        } else {
            this.modifierNoVotes.put(gameModifier, new HashSet<>(Set.of(player)));
        }
        
        if (this.modifierYesVotes.containsKey(gameModifier)) {
            this.modifierYesVotes.get(gameModifier).remove(player);
        }
    }
    
    public SGMode getMode() {
        return mode;
    }
    
    public void setup() {
        if (this.getConfig().contains("zipfile")) {
            File zipFile = new File(this.getConfig().getString("zipfile"));
            worldFolder = new File("." + File.separator + getServer().getName() + "-Lobby");
            FileHelper.deleteDirectory(worldFolder.toPath());
            FileHelper.createDirectoryIfNotExists(worldFolder.toPath());
            byte[] buffer = new byte[1024];

            try {
                ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile.toPath()));

                for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
                    Path newFile = this.newFile(worldFolder, zipEntry);
                    if (zipEntry.isDirectory()) {
                        FileHelper.createDirectoryIfNotExists(newFile);
                    } else {
                        Path parent = newFile.getParent();
                        if (!Files.isDirectory(parent)) {
                            FileHelper.createDirectoryIfNotExists(parent);
                            if (Files.notExists(parent)) {
                                throw new IOException("Failed to create directory " + parent);
                            }
                        }

                        FileOutputStream fos = new FileOutputStream(newFile.toFile());

                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }

                        fos.close();
                    }
                }

                zis.closeEntry();
                zis.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            this.world = Bukkit.createWorld(new WorldCreator(getServer().getName() + "-Lobby").environment(World.Environment.NORMAL));
        }

        if (this.getConfig().contains("spawnpoint")) {
            int x = Integer.parseInt(this.getConfig().getString("spawnpoint.x"));
            int y = Integer.parseInt(this.getConfig().getString("spawnpoint.y"));
            int z = Integer.parseInt(this.getConfig().getString("spawnpoint.z"));
            float yaw = Float.parseFloat(this.getConfig().getString("spawnpoint.yaw"));
            float pitch = Float.parseFloat(this.getConfig().getString("spawnpoint.pitch"));

            Location location = new Location(this.world, x, y, z, yaw, pitch);
            setSpawnpoint(location);
        }

        if (this.getConfig().contains("mapsigns")) {
            ConfigurationSection signsSection = this.getConfig().getConfigurationSection("mapsigns");
            for (String key : signsSection.getKeys(false)) {
                int position = Integer.parseInt(key);
                int x = signsSection.getInt(key + ".x");
                int y = signsSection.getInt(key + ".y");
                int z = signsSection.getInt(key + ".z");
                Location location = new Location(world, x, y, z);
                this.mapSigns.put(position, location);
            }
            plugin.getLogger().info("Map Signs Loaded");
        }

        if (this.getConfig().contains("statsigns")) {
            ConfigurationSection signsSection = this.getConfig().getConfigurationSection("statsigns");
            for (String key : signsSection.getKeys(false)) {
                int x = signsSection.getInt(key + ".x");
                int y = signsSection.getInt(key + ".y");
                int z = signsSection.getInt(key + ".z");
                Location location = new Location(world, x, y, z);
                String displayName = signsSection.getString(key + ".displayName");
                this.statSigns.add(new StatSign(location, key, displayName));
            }
        }

        if (this.getConfig().contains("tributesigns")) {
            ConfigurationSection signsSection = this.getConfig().getConfigurationSection("tributesigns");
            for (String key : signsSection.getKeys(false)) {
                int index = Integer.parseInt(key);
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

        this.lobbySettings = new LobbySettings();

        this.lobbyChatRoom = new LobbyChatRoom(this);
        plugin.getStarChat().getRoomRegistry().register(this.lobbyChatRoom.getName(), this.lobbyChatRoom);

        generateMapOptions();
    }

    public ChatRoom getLobbyChatRoom() {
        return lobbyChatRoom;
    }

    public SGVirtualServer getServer() {
        return server;
    }

    private Path newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        Path path = FileHelper.subPath(destinationDir.toPath(), zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = path.toFile().getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        } else {
            return path;
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void resetInvalidState() {
        NexusReborn.logMessage(Level.SEVERE, "Resetting Lobby from an Invalid State, see below for the stored information", this + "");

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
//        sendMessage("&4&l>> THE SERVER IS SHUTTING DOWN!");
        if (timer != null) {
            timer.cancel();
        }

        if (gameMap != null) {
            gameMap.removeFromServer(plugin);
        }

        if (this.world != null) {
            Bukkit.unloadWorld(world, false);
            FileHelper.deleteDirectory(this.worldFolder.toPath());
        }
    }

    public List<LobbyPlayer> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

    public void generateMapOptions() {
        plugin.getLogger().info("Generating Map Options");
        this.mapOptions.clear();

        getPlayers().forEach(player -> player.setMapVote(-1));

        plugin.getLogger().info("Cleared existing data");

        List<SGMap> maps = new ArrayList<>(plugin.getMapManager().getMaps());
        maps.removeIf(map -> !map.isActive());

        plugin.getLogger().info("Found " + maps.size() + " valid maps");

        Collections.shuffle(maps);

        for (int i = 0; i < maps.size(); i++) {
            if (!this.mapSigns.containsKey(i + 1)) {
                plugin.getLogger().warning("Map Signs does not contain " + i);
                break;
            }

            this.mapOptions.put(i + 1, maps.get(i));
        }

        plugin.getLogger().info("Generated the map options");
    }

    public boolean checkMapEditing(Player player) {
        if (this.state == LobbyState.MAP_CONFIGURATING) {
            return !player.getWorld().getName().equalsIgnoreCase(this.spawnpoint.getWorld().getName());
        }

        return false;
    }

    public SurvivalGames getPlugin() {
        return plugin;
    }

    public void sendMessage(String message) {
        this.lobbyChatRoom.sendMessage(new ChatContext(message));
    }

    public void startConfiguringMaps() {
        this.state = LobbyState.MAP_CONFIGURATING;
        if (timer != null) {
            this.timer.cancel();
            this.timer = null;
        }

        for (LobbyPlayer player : this.getPlayers()) {
            player.getPlayer().getScoreboard().setView(new MapEditingBoard(player.getPlayer().getScoreboard(), plugin));
        }

        plugin.getMapManager().setEditMode(true);
    }

    public void stopConfiguringMaps() {
        this.state = LobbyState.WAITING;

        for (LobbyPlayer player : this.getPlayers()) {
            player.getPlayer().getScoreboard().setView(new LobbyBoard(player.getPlayer().getScoreboard(), this));
            player.getPlayer().setActionBar(new LobbyActionBar(plugin, plugin.getPlayerRegistry().get(player.getUniqueId())));
        }

        plugin.getMapManager().setEditMode(false);

        generateMapOptions();
    }

    public void automatic() {
        this.controlType = ControlType.AUTO;
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
        this.timer = NexusReborn.getClockManager().createTimer(TimeUnit.SECONDS.toMillis(lobbySettings.getTimerLength()));
        this.timer.addRepeatingCallback(new LobbyTimerCallback(this), TimeUnit.SECONDS.toMillis(1));
        this.timer.start();
    }
    
    private static final Map<Rank, Integer> VOTE_WEIGHTS = MapHelper.of(
            Rank.NEXUS, 10, 
            Rank.ADMIN, 10, 
            Rank.HEAD_MOD, 9, 
            Rank.SR_MOD, 9, 
            Rank.MOD, 8, 
            Rank.HELPER, 7, 
            Rank.MVP, 10, 
            Rank.VIP, 6, 
            Rank.MEDIA, 5,
            Rank.ARCHITECT, 5, 
            Rank.PLATINUM, 4, 
            Rank.DIAMOND, 3, 
            Rank.BRASS, 3, 
            Rank.GOLD, 2, 
            Rank.INVAR, 2, 
            Rank.IRON, 1, 
            Rank.MEMBER, 1);

    private int getVoteCount(int position, UUID uuid) {
        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(uuid);
        if (nexusPlayer == null) {
            return 0;
        }

        if (lobbySettings.isVoteWeight()) {
            return VOTE_WEIGHTS.get(nexusPlayer.getEffectiveRank());
        } else {
            return 1;
        }
    }

    public int getTotalMapVotes(int position) {
        int votes = 0;
        for (LobbyPlayer player : getPlayers()) {
            if (player.getMapVote() == position) {
                if (getLobbySettings().isVoteWeight() && VOTE_WEIGHTS.containsKey(player.getEffectiveRank())) {
                    votes += VOTE_WEIGHTS.get(player.getEffectiveRank());
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

            ratingMsg = "&7Rating: " + ProgressBar.of(ratingPercent, 5, "* ", "&a", "&7") + " &7&o(Rating: " + ratingRatio + " star(s), based on: " + totalRatings + " vote(s))";
        }

        sendMessage("&6&l> " + ratingMsg);
        sendMessage("&6&l> &7Votes: &e" + gameMap.getVotes());
        sendMessage("");
        
        GameSettings settings = this.gameSettings.clone();
        
        this.mode.getModifiers().forEach((modifier, status) -> {
            if (status != GameModifierStatus.ALLOWED) {
                return;
            }
            
            int yesVotes = getModifierYesVotes(modifier), noVotes = getModifierNoVotes(modifier);
            
            if (yesVotes == 0 && noVotes == 0) {
                return;
            }
            
            if (yesVotes > noVotes) {
                modifier.getYesConsumer().accept(settings);
            } else {
                modifier.getNoConsumer().accept(settings);
            }
        });
        
        Game game = new Game(server, gameMap, this.mode, settings, getPlayers());
        this.players.clear();
        server.setGame(game);
        if (game.getControlType() == ControlType.AUTO) {
            game.setup();
            this.state = LobbyState.STARTING;
            resetLobby();
        } else {
            sendMessage("&eThe game has been prepared and is now ready, however, it has not been started due to not being in auto mode.");
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
        if (this.gameSettings == null) {
            this.gameSettings = mode.getDefaultSettings();
        }

        return gameSettings;
    }

    public LobbySettings getLobbySettings() {
        if (this.lobbySettings == null) {
            this.lobbySettings = new LobbySettings();
        }
        return lobbySettings;
    }

    public SGMap getGameMap() {
        if (this.state == LobbyState.MAP_CONFIGURATING) {
            return plugin.getMapManager().getMapBeingEdited();
        }

        return gameMap;
    }

    public void addPlayer(UUID uniqueId) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(uniqueId);
        if (sgPlayer == null) {
            return;
        }

        addPlayer(sgPlayer);
    }

    public void addPlayer(SGPlayer sgPlayer) {
        Player player = Bukkit.getPlayer(sgPlayer.getUniqueId());

        if (player == null) {
            return;
        }
        
        PLAYER_STATE.apply(player);

        NexusPlayer nexusPlayer = sgPlayer.getNexusPlayer();
        SGPlayerStats stats = sgPlayer.getStats();
        if (nexusPlayer == null) {
            return;
        }

        if (nexusPlayer.getPlayer() == null) {
            return;
        }

        LobbyPlayer lobbyPlayer = this.players.getOrDefault(nexusPlayer.getUniqueId(), new LobbyPlayer(sgPlayer, stats));
        this.players.put(nexusPlayer.getUniqueId(), lobbyPlayer);
        sgPlayer.setLobby(this, lobbyPlayer);
        this.lobbyChatRoom.addMember(nexusPlayer.getUniqueId(), DefaultPermissions.VIEW_MESSAGES, DefaultPermissions.SEND_MESSAGES);
        this.plugin.getStarChat().setPlayerFocus(player, this.lobbyChatRoom);

        int totalPlayers = 0;
        for (LobbyPlayer otherLobbyPlayer : getPlayers()) {
            if (otherLobbyPlayer.isSpectating()) {
                continue;
            }

            totalPlayers++;
        }

        if (totalPlayers > lobbySettings.getMaxPlayers()) {
            nexusPlayer.sendMessage("&eYou will be a spectator in the game as you joined with the player count above the maximum game amount. However, you can be a tribute if those before you leave or become spectators");
        }

        Location spawn = getSpawnpoint().clone();
        spawn.setY(spawn.getY() + 2);
        player.teleport(spawn);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!this.players.containsKey(online.getUniqueId())) {
                continue;
            }
            online.showPlayer(player);
            player.showPlayer(online);
        }

        if (nexusPlayer.getToggleValue("vanish") && !nexusPlayer.isNicked()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                LobbyPlayer psp = this.players.get(p.getUniqueId());
                if (psp != null) {
                    if (psp.getRank().ordinal() > Rank.HELPER.ordinal()) {
                        p.hidePlayer(player);
                    } else {
                        psp.sendMessage("&a&l>> " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined &e&ovanished&e.");
                    }
                }
            }
        } else if (nexusPlayer.getToggleValue("incognito") && !nexusPlayer.isNicked()) {
            for (LobbyPlayer np : getPlayers()) {
                if (np != null) {
                    if (np.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                        np.sendMessage("&a&l>> " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined &e&osilently&e.");
                    }
                }
            }
        } else {
            sendMessage("&a&l>> " + nexusPlayer.getEffectiveRank().getColor() + nexusPlayer.getName() + " &ejoined.");
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

        player.getInventory().setItem(0, SurvivalGames.sponsorsItem.toItemStack());
        if (lobbySettings.isVoteForModifiers()) {
            player.getInventory().setItem(1, SurvivalGames.modifierItem.toItemStack());
        }
        player.getInventory().setItem(8, SurvivalGames.toHubItem.toItemStack());

        if (nexusPlayer.getRank().ordinal() <= Rank.DIAMOND.ordinal()) {
            player.setAllowFlight(nexusPlayer.getToggleValue("fly"));
        }

        if (this.debugMode) {
            nexusPlayer.getScoreboard().setView(new DebugLobbyBoard(nexusPlayer.getScoreboard(), this));
        } else if (this.state == LobbyState.MAP_CONFIGURATING) {
            nexusPlayer.getScoreboard().setView(new MapEditingBoard(nexusPlayer.getScoreboard(), plugin));
        } else {
            nexusPlayer.getScoreboard().setView(new LobbyBoard(nexusPlayer.getScoreboard(), this));
        }
        nexusPlayer.getScoreboard().setTablistHandler(new RankTablistHandler(nexusPlayer.getScoreboard()));
        nexusPlayer.setActionBar(new LobbyActionBar(plugin, plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId())));
        sendMapOptions(nexusPlayer);
    }

    public void removePlayer(UUID uniqueId) {
        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(uniqueId);

        if (nexusPlayer == null || Bukkit.getPlayer(uniqueId) == null) {
            this.players.remove(uniqueId);
            this.lobbyChatRoom.removeMember(uniqueId);
            return;
        }

        removePlayer(nexusPlayer);
    }

    public void removePlayer(NexusPlayer nexusPlayer) {
        if (!this.players.containsKey(nexusPlayer.getUniqueId())) {
            return;
        }

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());

        this.lobbyChatRoom.removeMember(nexusPlayer.getUniqueId());
        sgPlayer.setLobby(null, null);
        this.players.remove(nexusPlayer.getUniqueId());
        int totalPlayers = 0;
        for (LobbyPlayer player : getPlayers()) {
            if (player.isSpectating()) {
                continue;
            }
            totalPlayers++;
        }

        if (nexusPlayer.getToggleValue("vanish") && !nexusPlayer.isNicked()) {
            for (LobbyPlayer snp : getPlayers()) {
                if (snp.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    snp.sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft &e&ovanished&e.");
                }
            }
        } else if (nexusPlayer.getToggleValue("incognito") && !nexusPlayer.isNicked()) {
            for (LobbyPlayer snp : getPlayers()) {
                if (snp.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    snp.sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft &e&osilently&e.");
                }
            }
        } else {
            sendMessage("&c&l<< " + nexusPlayer.getEffectiveRank().getColor() + nexusPlayer.getName() + " &eleft.");
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
        
        if (this.controlType == ControlType.MANUAL) {
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
            
            this.state = LobbyState.WAITING;
        }
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
        if (this.lobbySettings == null) {
            this.lobbySettings = new LobbySettings();
        }
        
        if (this.lobbySettings.isKeepPreviousGameSettings()) {
            this.gameSettings = game.getSettings();
        } else {
            this.gameSettings = this.mode.getDefaultSettings();
        }

        for (UUID player : game.getPlayers().keySet()) {
            addPlayer(plugin.getPlayerRegistry().get(player));
        }

        game.getGameMap().removeFromServer(plugin);
        server.setGame(null);
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
        LobbyPlayer player = this.players.get(nexusPlayer.getUniqueId());

        for (Entry<Integer, Location> entry : this.mapSigns.entrySet()) {
            if (entry.getValue().equals(location)) {
                if (entry.getKey() == player.getMapVote()) {
                    nexusPlayer.sendMessage("&cYou have already voted for this map.");
                    return;
                }

                SGMap sgMap = this.mapOptions.get(entry.getKey());
                
                if (sgMap == null) {
                    MsgType.WARN.send(Bukkit.getPlayer(nexusPlayer.getUniqueId()), "Invalid Vote Option");
                    return;
                }
                
                String creators = StringHelper.join(sgMap.getCreators(), ", ");

                if (player.getMapVote() > -1) {
                    player.sendMessage("&6&l>> &eYou changed your vote to&8: &b" + sgMap.getName() + " &7&oby &3" + creators);
                } else {
                    player.sendMessage("&6&l>> &eYou voted for&8: &b" + sgMap.getName() + " &7&oby &3" + creators + "&e.");
                }
                player.sendMessage("&6&l>> &eVoting Weight&8: &b" + VOTE_WEIGHTS.get(nexusPlayer.getEffectiveRank()) + " Vote(s)&e.");
                player.playSound(Sound.NOTE_PLING);

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

    public World getWorld() {
        return this.world;
    }
    
    public int getModifierYesVotes(GameModifier modifier) {
        if (this.modifierYesVotes.containsKey(modifier)) {
            return this.modifierYesVotes.get(modifier).size();
        }
        
        return 0;
    }
    
    public int getModifierNoVotes(GameModifier modifier) {
        if (this.modifierNoVotes.containsKey(modifier)) {
            return this.modifierNoVotes.get(modifier).size();
        }
        
        return 0;
    }
}