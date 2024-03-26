package com.thenexusreborn.survivalgames.game;

import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starchat.rooms.DefaultPermissions;
import com.stardevllc.starlib.registry.StringRegistry;
import com.stardevllc.starlib.time.TimeFormat;
import com.stardevllc.starlib.time.TimeUnit;
import com.stardevllc.starmclib.actor.Actor;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.api.gamearchive.GameInfo;
import com.thenexusreborn.api.helper.NumberHelper;
import com.thenexusreborn.api.helper.StringHelper;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.gamemaps.model.MapSpawn;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.chat.GameTeamChatroom;
import com.thenexusreborn.survivalgames.disguises.DisguiseAPI;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.MobDisguise;
import com.thenexusreborn.survivalgames.game.Bounty.Type;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.game.death.DeathType;
import com.thenexusreborn.survivalgames.game.death.KillerInfo;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.phase.AssignTeamsPhase;
import com.thenexusreborn.survivalgames.game.state.phase.SetupPhase;
import com.thenexusreborn.survivalgames.game.state.phase.TeleportToMapPhase;
import com.thenexusreborn.survivalgames.game.timer.old.*;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.loot.Items;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.mutations.MutationEffect;
import com.thenexusreborn.survivalgames.mutations.MutationItem;
import com.thenexusreborn.survivalgames.mutations.MutationType;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.sponsoring.SponsorManager;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static com.thenexusreborn.survivalgames.game.GameState.*;

@SuppressWarnings("unused")
public class Game {
    private static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    public static final TimeFormat SHORT_TIME_FORMAT = new TimeFormat("%*00h%%*#0m%%*#0s%");
    public static final TimeFormat TIME_FORMAT = new TimeFormat("%*00h%%#0m%%00s%");
    public static final TimeFormat LONG_TIME_FORMAT = new TimeFormat("%*00h%%00m%%00s%");

    private final int localId;
    private final SGMap gameMap;
    private ControlType controlType = ControlType.AUTOMATIC;
    private final GameSettings settings;
    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private final Map<Integer, UUID> spawns = new HashMap<>();
    private final ChatRoom gameChatroom;
    private final Map<GameTeam, GameTeamChatroom> chatRooms = new HashMap<>();
    private GameState state = UNDEFINED;
    private Timer timer, graceperiodTimer, restockTimer, ratingPromptTimer;
    private final List<Location> lootedChests = new ArrayList<>();
    private final GameInfo gameInfo;
    private long start, end;
    private GamePlayer firstBlood;
    private final Map<Location, Inventory> enderchestInventories = new HashMap<>();
    private SponsorManager sponsorManager = new SponsorManager();
    private Mode mode = Mode.CLASSIC; //This will be implemented later, this is mainly for some other checks to exist
    private boolean debugMode; //Debug Mode. This may be replaced with a class with other settings
    private Graceperiod graceperiod = Graceperiod.INACTIVE;

    private GamePhase setupPhase, assignTeamsPhase, teleportToMapPhase;

    public Game(SGMap gameMap, GameSettings settings, Collection<LobbyPlayer> players) {
        this.localId = plugin.getLastLocalGameId();
        plugin.setLastLocalGameId(plugin.getLastLocalGameId() + 1);
        this.gameMap = gameMap;
        this.settings = settings;
        this.gameInfo = new GameInfo();
        
        this.gameChatroom = new ChatRoom(plugin, "room-game-" + this.localId + "-main", Actor.getServerActor(), "{message}", "{message}");
        plugin.getStarChat().getRoomRegistry().register(gameChatroom.getSimplifiedName(), gameChatroom);

        for (GameTeam team : GameTeam.values()) {
            GameTeamChatroom chatroom = new GameTeamChatroom(plugin, this, team);
            this.chatRooms.put(team, chatroom);
            plugin.getStarChat().getRoomRegistry().register(chatroom.getSimplifiedName(), chatroom);
        }
        
        gameInfo.setMapName(this.gameMap.getName().replace("'", "''"));
        gameInfo.setServerName(NexusAPI.getApi().getServerManager().getCurrentServer().getName());
        for (MapSpawn spawn : this.gameMap.getSpawns()) {
            this.spawns.put(spawn.getIndex(), null);
        }
        List<String> playerNames = new ArrayList<>();
        int tributeCount = 0;
        for (LobbyPlayer player : players) {
            GamePlayer gamePlayer = new GamePlayer(player.getPlayer(), this, player.getStats());
            if (player.isSpectating()) {
                gamePlayer.setTeam(GameTeam.SPECTATORS);
            } else {
                gamePlayer.setTeam(GameTeam.TRIBUTES);
                playerNames.add(player.getName());
                tributeCount++;
            }
            player.setActionBar(new GameActionBar(plugin, gamePlayer));
            this.players.put(gamePlayer.getUniqueId(), gamePlayer);
            this.gameChatroom.addMember(player.getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
        }
        gameInfo.setPlayerCount(tributeCount);
        gameInfo.setPlayers(playerNames.toArray(new String[0]));

        this.setupPhase = new SetupPhase(this);
        this.assignTeamsPhase = new AssignTeamsPhase(this);
        this.teleportToMapPhase = new TeleportToMapPhase(this);
    }

    public int getLocalId() {
        return localId;
    }

    public void setState(GameState state) {
        this.state = state;
        this.gameInfo.getActions().add(new GameAction(System.currentTimeMillis(), "statechange", state.name()));
    }

    public GamePhase getSetupPhase() {
        return setupPhase;
    }

    public GamePhase getAssignTeamsPhase() {
        return assignTeamsPhase;
    }

    public GamePhase getTeleportToMapPhase() {
        return teleportToMapPhase;
    }

    public void handleShutdown() {
        this.state = SHUTTING_DOWN;
        sendMessage("&4&l>> THE SERVER IS SHUTTING DOWN!");
        if (this.timer != null) {
            this.timer.cancel();
        }

        if (this.graceperiodTimer != null) {
            this.graceperiodTimer.cancel();
        }

        if (this.ratingPromptTimer != null) {
            this.ratingPromptTimer.cancel();
        }

        if (this.restockTimer != null) {
            this.restockTimer.cancel();
        }

        if (this.gameMap != null) {
            this.gameMap.removeFromServer(plugin);
        }
    }

    public ControlType getControlType() {
        return controlType;
    }

    public void setControlType(ControlType controlType) {
        this.controlType = controlType;
        if (controlType == ControlType.MANUAL) {
            if (plugin.getGame() != null) {
                if (plugin.getGame().getTimer() != null) {
                    plugin.getGame().getTimer().cancel();
                    plugin.getGame().timer = null;
                }
                if (plugin.getGame().getGraceperiodTimer() != null) {
                    plugin.getGame().getGraceperiodTimer().cancel();
                    plugin.getGame().graceperiodTimer = null;
                }
            }
        }
    }

    public SGMap getGameMap() {
        return gameMap;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public Timer getGraceperiodTimer() {
        return graceperiodTimer;
    }

    public Timer getRestockTimer() {
        if (this.restockTimer != null) {
            if (this.restockTimer.getTimeLeft() <= 0) {
                this.restockTimer = null;
            }
        }
        return restockTimer;
    }

    public void addPlayer(NexusPlayer nexusPlayer, SGPlayerStats stats) {
        GamePlayer gamePlayer = new GamePlayer(nexusPlayer, this, stats);
        gamePlayer.setStatus(GamePlayer.Status.ADDING_TO_GAME);
        gamePlayer.setTeam(GameTeam.SPECTATORS);
        gamePlayer.sendMessage(GameTeam.SPECTATORS.getJoinMessage());
        this.players.put(nexusPlayer.getUniqueId(), gamePlayer);
        gamePlayer.setStatus(GamePlayer.Status.SETTING_UP_PLAYER);
        Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
        this.gameChatroom.addMember(player.getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        giveSpectatorItems(player);
        player.spigot().setCollidesWithEntities(false);
        gamePlayer.setStatus(GamePlayer.Status.TELEPORTING_TO_CENTER);
        teleportSpectator(player, this.gameMap.getCenter().toLocation(this.gameMap.getWorld()));

        gamePlayer.setStatus(GamePlayer.Status.CALCULATING_VISIBILITY);
        if (nexusPlayer.getToggleValue("vanish")) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getRank().ordinal() <= Rank.HELPER.ordinal() || gp.getUniqueId().equals(nexusPlayer.getUniqueId())) {
                    gp.sendMessage("&a&l>> " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined &e&ovanished&e.");
                }
            }
        } else if (nexusPlayer.getToggleValue("incognito")) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getRank().ordinal() <= Rank.HELPER.ordinal() || gp.getUniqueId().equals(nexusPlayer.getUniqueId())) {
                    gp.sendMessage("&a&l>> " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined &e&osilently&e.");
                }
            }
        } else {
            sendMessage("&a&l>> &b" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined.");
        }

        gamePlayer.setStatus(GamePlayer.Status.SETTING_UP_SCOREBOARD);
        gamePlayer.applyScoreboard();
        gamePlayer.setStatus(GamePlayer.Status.SETTING_UP_ACTIONBAR);
        gamePlayer.applyActionBar();
        gamePlayer.setStatus(GamePlayer.Status.READY);
        recalculateVisibility();
    }

    public void removePlayer(NexusPlayer nexusPlayer) {
        if (!this.players.containsKey(nexusPlayer.getUniqueId())) {
            return;
        }
        GamePlayer gamePlayer = this.players.get(nexusPlayer.getUniqueId());
        this.chatRooms.get(gamePlayer.getTeam()).removeMember(gamePlayer.getUniqueId());
        this.gameChatroom.removeMember(gamePlayer.getUniqueId());
        EnumSet<GameState> ignoreStates = EnumSet.of(UNDEFINED, SETTING_UP, SETUP_COMPLETE, ASSIGN_TEAMS, TEAMS_ASSIGNED, TELEPORT_START, TELEPORT_START_DONE, ERROR, ENDING, ENDED);
        if (!ignoreStates.contains(this.state)) {
            if (gamePlayer.getTeam() == GameTeam.TRIBUTES || gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                killPlayer(gamePlayer, new DeathInfo(plugin.getGame(), System.currentTimeMillis(), gamePlayer, DeathType.SUICIDE));
            }
        }

        this.players.remove(nexusPlayer.getUniqueId());

        if (nexusPlayer.getToggleValue("vanish")) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    gp.sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft &e&ovanished&e.");
                }
            }
        } else if (nexusPlayer.getToggleValue("incognito")) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    gp.sendMessage("&c&l<< " + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft &e&osilently&e.");
                }
            }
        } else {
            sendMessage("&c&l<< &b" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft.");
        }
    }

    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }

    public void resetSpawns() {
        this.spawns.entrySet().forEach(entry -> entry.setValue(null));
    }

    public void teleportTributes(List<UUID> tributes, Location mapSpawn) {
        List<Entry<Integer, UUID>> spawns = new ArrayList<>(this.spawns.entrySet());
        Collections.shuffle(spawns);
        for (UUID tribute : tributes) {
            Player player = Bukkit.getPlayer(tribute);
            if (player != null) {
                int index = new Random().nextInt(spawns.size());
                Entry<Integer, UUID> entry = spawns.get(index);
                MapSpawn spawnPosition = this.gameMap.getSpawns().get(entry.getKey());
                Location spawn = spawnPosition.toGameLocation(gameMap.getWorld(), mapSpawn);
                teleportTribute(player, spawn);
                this.spawns.put(entry.getKey(), player.getUniqueId());
                spawns.remove(index);
            }
        }
    }

    public void ingameComplete() {
        setState(GameState.INGAME_DONE);
    }

    private void teleportToGameSpawn(Player player, Location spawn, GameTeam gameTeam) {
        player.teleport(spawn);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.setGameMode(gameTeam.getGameMode()), 1L);
    }

    public void teleportTribute(Player tribute, Location spawn) {
        teleportToGameSpawn(tribute, spawn, GameTeam.TRIBUTES);
    }

    public void teleportMutation(Player mutation, Location spawn) {
        teleportToGameSpawn(mutation, spawn, GameTeam.MUTATIONS);
    }

    public void teleportSpectator(Player spectator, Location mapSpawn) {
        try {
            spectator.teleport(mapSpawn);
            spectator.setGameMode(GameTeam.SPECTATORS.getGameMode());
            spectator.setFoodLevel(20);
            spectator.setSaturation(20);
            spectator.setAllowFlight(true);
            spectator.setFlying(true);
        } catch (NullPointerException e) {
            if (spectator != null) {
                throw e;
            }
        }
    }

    public void recalculateVisibility() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                continue;
            }
            for (Player other : Bukkit.getOnlinePlayers()) {
                GamePlayer otherGamePlayer = getPlayer(other.getUniqueId());

                if (otherGamePlayer == null) {
                    continue;
                }

                if (gamePlayer.getToggleValue("vanish")) {
                    player.showPlayer(other);
                    if (otherGamePlayer.getRank().ordinal() > Rank.HELPER.ordinal()) {
                        other.hidePlayer(player);
                    }
                } else if (gamePlayer.getTeam() != GameTeam.SPECTATORS && otherGamePlayer.getTeam() != GameTeam.SPECTATORS) {
                    player.showPlayer(other);
                    other.showPlayer(player);
                } else if (gamePlayer.getTeam() == GameTeam.SPECTATORS && otherGamePlayer.getTeam() != GameTeam.SPECTATORS) {
                    player.showPlayer(other);
                    other.hidePlayer(player);
                } else {
                    player.hidePlayer(other);
                }
            }
        }
    }

    public void teleportSpectators(List<UUID> spectators, Location mapSpawn) {
        for (UUID spectator : spectators) {
            Player player = Bukkit.getPlayer(spectator);
            teleportSpectator(player, mapSpawn);
        }
    }

    public void teleportStart() {
        setState(TELEPORT_START);
        this.teleportToMapPhase.beginphase(); //TODO Temporary until it can be fully implemented
    }

    public void assignStartingTeams() {
        setState(ASSIGN_TEAMS);
        this.assignTeamsPhase.beginphase(); //TODO Temporary until it can be fully implemented
    }

    public void setup() {
        setState(SETTING_UP);
        this.setupPhase.beginphase(); //TODO Temporary until it can be fully fully implemented
    }

    public void handleError(String message) {
        setState(ERROR);
        sendMessage("&4&l>> &4" + message + " Resetting back to lobby.");
        plugin.getLobby().resetLobby();
        plugin.getLobby().fromGame(this);
    }

    public void sendMessage(String message) {
        this.gameChatroom.sendMessage(message);
        Bukkit.getConsoleSender().sendMessage(MCUtils.color(message));
    }

    public GameState getState() {
        return state;
    }

    public void startWarmup() {
        setState(WARMUP);
        this.timer = new Timer(new CountdownTimerCallback(this)).run(TimeUnit.SECONDS.toMillis(settings.getWarmupLength()) + 50L);
    }

    public void startGame() {
        this.timer = new Timer(new GameTimerCallback(this)).run(TimeUnit.MINUTES.toMillis(settings.getGameLength()) + 50);
        this.ratingPromptTimer = new Timer(snapshot -> {
            if (snapshot.getSecondsLeft() == 0) {
                sendMessage("");
                sendMessage("&6&l>> &9&lWHAT DO YOU THINK OF &e&l" + getGameMap().getName().toUpperCase() + "&9&l?");
                sendMessage("&6&l>> &7Type &8[&6/ratemap &4&l1 &c&l2 &6&l3 &e&l4 &a&l5&8] &7to submit a rating!");
                sendMessage("");
                return false;
            }
            return true;
        }).run(TimeUnit.MINUTES.toMillis(settings.getGameLength()) / 4);
        this.start = System.currentTimeMillis();
        if (this.settings.isGracePeriod()) {
            this.graceperiodTimer = new Timer(new GraceperiodCountdownCallback(this)).run(TimeUnit.SECONDS.toMillis(settings.getGracePeriodLength()) + 50L);
            this.graceperiod = Graceperiod.ACTIVE;
        }
        setState(INGAME);
        long restockLength;
        if (settings.isChestRestockRelative()) {
            restockLength = settings.getGameLength() / settings.getChestRestockDenomination();
        } else {
            restockLength = settings.getChestRestockInterval();
        }
        this.restockTimer = new Timer(new RestockTimerCallback(this)).run(TimeUnit.MINUTES.toMillis(restockLength) + 50L);
        sendMessage("&6&l>> &a&lMAY THE ODDS BE EVER IN YOUR FAVOR.");
        if (this.settings.isTeamingAllowed()) {
            sendMessage("&6&l>> &d&lTHERE IS A MAX OF " + this.settings.getMaxTeamAmount() + " PLAYER TEAMS.");
        } else {
            sendMessage("&6&l>> &d&lTEAMING IS NOT ALLOWED IN THIS GAME.");
        }

        if (gameMap.getSwagShack() != null) {
            Villager entity = (Villager) gameMap.getWorld().spawnEntity(gameMap.getSwagShack().toLocation(gameMap.getWorld()), EntityType.VILLAGER);
            entity.setCustomNameVisible(true);
            entity.setCustomName(MCUtils.color("&e&lSwag Shack"));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));
        }
    }

    public void restockChests() {
        this.lootedChests.clear();
    }

    public void warmupComplete() {
        setState(WARMUP_DONE);
    }

    public Map<Location, Inventory> getEnderchestInventories() {
        return enderchestInventories;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public void playSound(Sound sound) {
        for (GamePlayer player : this.players.values()) {
            Player p = Bukkit.getPlayer(player.getUniqueId());
            if (p != null) {
                p.playSound(p.getLocation(), sound, 0.5F, 1);
            }
        }
    }

    public void teleportDeathmatch() {
        try {
            setState(TELEPORT_DEATHMATCH);

            for (GamePlayer gp : this.players.values()) {
                if (gp.getTeam() == GameTeam.MUTATIONS) {
                    removeMutation(gp.getMutation());
                    gp.sendMessage(gp.getTeam().getLeaveMessage());
                    gp.setTeam(GameTeam.SPECTATORS);
                    gp.sendMessage(gp.getTeam().getJoinMessage());
                    giveSpectatorItems(Bukkit.getPlayer(gp.getUniqueId()));
                    recalculateVisibility();
                    gp.sendMessage("&6&l>> &cYou were made a spectator because deathmatch started.");
                }
            }

            sendMessage("&6&l>> &e&LPREPARE FOR DEATHMATCH...");
            List<UUID> tributes = new LinkedList<>(), spectators = new LinkedList<>();
            for (GamePlayer player : this.players.values()) {
                if (player.getTeam() == GameTeam.TRIBUTES) {
                    tributes.add(player.getUniqueId());
                    player.getStats().addDeathmatchesReached(1);
                } else {
                    spectators.add(player.getUniqueId());
                }
            }
            resetSpawns();
            Location mapSpawn = gameMap.getCenter().toLocation(gameMap.getWorld());
            teleportTributes(tributes, mapSpawn);
            teleportSpectators(spectators, mapSpawn);

            recalculateVisibility();
            setState(TELEPORT_DEATHMATCH_DONE);
        } catch (Exception e) {
            e.printStackTrace();
            handleError("There was an error teleporting tributes to the deathmatch.");
        }
    }

    public void startDeathmatchWarmup() {
        setState(DEATHMATCH_WARMUP);

        if (this.timer != null) {
            timer.cancel();
        }

        playSound(Sound.ENDERDRAGON_GROWL);
        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                Bukkit.getPlayer(player.getUniqueId()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
            }
        }

        this.timer = new Timer(new DeathmatchCountdownCallback(this)).run(TimeUnit.SECONDS.toMillis(settings.getDeathmatchWarmupLength()) + 50L);
    }

    public void startDeathmatch() {
        setState(DEATHMATCH);

        if (this.timer != null) {
            timer.cancel();
        }

        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                Bukkit.getPlayer(player.getUniqueId()).removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }

        sendMessage("&6&l>> &a&lLAST PLAYER STANDING CLAIMS VICTORY!");
        sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED.");
        sendMessage("&6&l>> &d&lTHERE IS NO TEAMING ALLOWED IN DEATHMATCH.");
        restockChests();

        this.gameMap.applyWorldBoarder("deathmatch", settings.getDeathmatchLength() * 60);

        this.timer = new Timer(new GameEndTimerCallback(this)).run(TimeUnit.MINUTES.toMillis(settings.getDeathmatchLength()) + 50);
    }

    public void deathmatchWarmupDone() {
        setState(DEATHMATCH_WARMUP_DONE);
    }

    public void end() {
        setState(ENDING);
        this.end = System.currentTimeMillis();
        plugin.incrementGamesPlayed();
        if (this.timer != null) {
            timer.cancel();
            this.timer = null;
        }

        if (this.graceperiodTimer != null) {
            graceperiodTimer.cancel();
            this.graceperiodTimer = null;
        }

        if (this.restockTimer != null) {
            this.restockTimer.cancel();
            this.restockTimer = null;
        }

        if (this.ratingPromptTimer != null) {
            this.ratingPromptTimer.cancel();
            this.ratingPromptTimer = null;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setAllowFlight(true);
            for (Player p : Bukkit.getOnlinePlayers()) {
                player.showPlayer(p);
                p.showPlayer(player);
            }

            GamePlayer gamePlayer = this.getPlayer(player.getUniqueId());
            gamePlayer.getCombatTag().setOther(null);
        }

        GamePlayer winner = null;
        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                player.getStats().addGames(1);
                if (winner == null) {
                    winner = player;
                } else {
                    winner = null;
                    break;
                }
            }
        }

        String winnerName;
        if (winner != null) {
            winnerName = winner.getDisplayName();
        } else {
            winnerName = "&f&lNo one";
        }

        sendMessage("&6&l>> " + winnerName + " &a&lhas won Survival Games!");

        if (winner != null) {
            winner.getStats().addWins(1);
            winner.getStats().addWinStreak(1);
            double winGain = settings.getWinScoreBaseGain();
            int currentScore = winner.getStats().getScore();
            if (currentScore < 100 && currentScore > 50) {
                winGain *= 1.25;
            } else if (currentScore <= 50 && currentScore > 25) {
                winGain *= 1.5;
            } else if (currentScore < 25) {
                winGain *= 2;
            } else if (currentScore < 1000 && currentScore > 500) {
                winGain *= .75;
            } else if (currentScore >= 1000) {
                winGain *= .5;
            }
            winner.getStats().addScore((int) winGain);
            winner.sendMessage("&2&l>> &a+" + (int) winGain + " Score!");
            double multiplier = winner.getRank().getMultiplier();
            Rank rank = winner.getRank();
            String multiplierMessage = rank.getColor() + "&l * x" + MCUtils.formatNumber(multiplier) + " " + rank.getPrefix() + " Bonus";
            if (settings.isGiveXp()) {
                double xp = settings.getWinXPBaseGain();
                xp *= multiplier;
                winner.getNexusPlayer().addXp(xp);
                String baseMessage = "&2&l>> &a&l+" + MCUtils.formatNumber(xp) + " &2&lXP&a&l!";
                if (multiplier > 1) {
                    winner.sendMessage(baseMessage + multiplierMessage);
                } else {
                    winner.sendMessage(baseMessage);
                }
            }

            if (settings.isGiveCredits()) {
                double credits = settings.getWinCreditsBaseGain();
                credits *= multiplier;
                winner.getBalance().addCredits(credits);
                String baseMessage = "&2&l>> &a&l+" + MCUtils.formatNumber(credits) + " &3&lCREDITS&a&l!";
                if (multiplier > 1) {
                    winner.sendMessage(baseMessage + multiplierMessage);
                } else {
                    winner.sendMessage(baseMessage);
                }
            }

            if (settings.isEarnNexites()) {
                double nexites = settings.getWinNexiteBaseGain();
                if (winner.getRank().isNexiteBoost()) {
                    nexites *= multiplier;
                }

                String baseMessage = "&2&l>> &a&l" + nexites + " &9&lNEXITES&a&l!";
                if (multiplier > 1 && winner.getRank().isNexiteBoost()) {
                    winner.sendMessage(baseMessage + multiplierMessage);
                } else {
                    winner.sendMessage(baseMessage);
                }
            }

            double passWinValue = new Random().nextDouble();
            if (passWinValue <= getSettings().getPassRewardChance()) {
                winner.getStats().addMutationPasses(1);
                winner.sendMessage("&2&l>> &a&lYou won a mutation pass! Great job!");
            } else {
                winner.sendMessage(MsgType.INFO + "You did not win a mutation pass this time.");
            }

            Bounty bounty = winner.getBounty();
            for (Type type : Type.values()) {
                double amount = bounty.getAmount(type);
                if (amount > 0) {
                    sendMessage("&6&l>> For winning the game, " + winner.getColoredName() + " &6&l has kept their &b&l" + NumberHelper.formatNumber(amount) + " " + StringHelper.capitalizeEveryWord(type.name()) + " &6&lbounty!");
                    if (type == Type.CREDIT) {
                        winner.getBalance().addCredits(amount);
                    } else if (type == Type.SCORE) {
                        winner.getStats().addScore((int) amount);
                    }
                }
            }
        }

        gameInfo.setGameStart(this.start);
        gameInfo.setGameEnd(this.end);
        if (winner != null) {
            gameInfo.setWinner(winner.getName());
        } else {
            gameInfo.setWinner("No one");
        }

        if (this.firstBlood != null) {
            gameInfo.setFirstBlood(firstBlood.getName());
        } else {
            gameInfo.setFirstBlood("No one");
        }

        gameInfo.setLength(this.end - this.start);

        NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
            NexusAPI.getApi().getPrimaryDatabase().saveSilent(gameInfo);
            if (gameInfo.getId() == 0) {
                sendMessage("&4&l>> &cThere was a database error archiving the game. Please report with date and time.");
            } else {
                sendMessage("&6&l>> &aThis game has been archived!");
                sendMessage("&6&l>> &aGame ID: &b" + gameInfo.getId() + " &7&oCustom Website Coming Soon.");

                if (gameInfo.getId() % 1000 == 0) {
                    for (String p : gameInfo.getPlayers()) {
                        NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
                            UUID uuid = NexusAPI.getApi().getPlayerManager().getUUIDFromName(p);

                            Tag tag = new Tag(uuid, gameInfo.getId() + "th", System.currentTimeMillis());
                            //TODO nexusPlayer.sendMessage(MsgType.INFO + "Unlocked the tag " + tag.getDisplayName());
                            NexusAPI.getApi().getPrimaryDatabase().saveSilent(tag);
                        });
                    }
                }
            }
        });

        if (!(this.players.isEmpty() || Bukkit.getOnlinePlayers().isEmpty())) {
            this.timer = new Timer(new NextGameTimerCallback(this)).run(TimeUnit.SECONDS.toMillis(settings.getNextGameStart()));
        } else {
            this.nextGame();
        }
    }

    public void resetPlayer(Player player) {
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.spigot().setCollidesWithEntities(true);
    }

    public void nextGame() {
        setState(ENDED);
        for (Player player : Bukkit.getOnlinePlayers()) {
            resetPlayer(player);
        }

        StringRegistry<ChatRoom> roomRegistry = plugin.getStarChat().getRoomRegistry();
        roomRegistry.deregister(this.gameChatroom.getSimplifiedName());
        for (GameTeamChatroom chatroom : this.getChatRooms().values()) {
            roomRegistry.deregister(chatroom.getSimplifiedName());
        }

        plugin.getLobby().fromGame(this);
    }

    public void killPlayer(GamePlayer gamePlayer, DeathInfo deathInfo) {
        GameTeam oldTeam = gamePlayer.getTeam();
        gamePlayer.addDeathInfo(deathInfo);
        gamePlayer.setTeam(GameTeam.SPECTATORS);
        recalculateVisibility();
        Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
        String strippedDeathMessage = ChatColor.stripColor(deathInfo.getDeathMessage());
        strippedDeathMessage = strippedDeathMessage.substring(3, strippedDeathMessage.length() - 1);
        this.gameInfo.getActions().add(new GameAction(System.currentTimeMillis(), "death", strippedDeathMessage));
        if (player != null) {
            resetPlayer(player);
            player.setGameMode(GameTeam.SPECTATORS.getGameMode());
            player.spigot().setCollidesWithEntities(false);
            giveSpectatorItems(player);
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        boolean deathByLeave = deathInfo.getType() == DeathType.SUICIDE;
        gamePlayer.setSpectatorByDeath(!deathByLeave);
        KillerInfo killer = deathInfo.getKiller();
        gamePlayer.setDeathByMutation(killer != null && killer.isMutationKill());

        boolean deathByVanish = deathInfo.getType() == DeathType.VANISH;
        int score = gamePlayer.getStats().getScore();
        int lost = (int) Math.ceil(score / settings.getScoreDivisor());
        if (score - lost < 0) {
            lost = 0;
        }

        if (!(deathByVanish || deathByLeave)) {
            if (lost > 0) {
                gamePlayer.getStats().addScore(-lost);
            }
            gamePlayer.getStats().addGames(1);
            gamePlayer.getStats().setWinStreak(0);
            gamePlayer.getStats().addDeaths(1);
            if (oldTeam == GameTeam.MUTATIONS) {
                gamePlayer.getStats().addMutationDeaths(1);
            }
        }

        boolean playerKiller = killer != null && killer.getType() == EntityType.PLAYER;

        boolean claimedFirstBlood = false;

        int scoreGain = 0, currentStreak = 0, personalBest = 0, xpGain = 0, creditGain = 0, nexiteGain = 0;
        Rank killerRank = null;
        boolean claimedScoreBounty = false, claimedCreditBounty = false;
        Bounty bounty = gamePlayer.getBounty();
        double scoreBounty = bounty.getAmount(Bounty.Type.SCORE);
        double creditBounty = bounty.getAmount(Bounty.Type.CREDIT);
        if (playerKiller) {
            GamePlayer killerPlayer = getPlayer(killer.getKiller());
            killerRank = killerPlayer.getRank();
            scoreGain = lost;

            if (this.firstBlood == null) {
                this.firstBlood = killerPlayer;
                claimedFirstBlood = true;
            }

            if (claimedFirstBlood) {
                scoreGain = (int) (scoreGain * settings.getFirstBloodMultiplier());
            }

            if (scoreBounty > 0) {
                scoreGain += (int) scoreBounty;
                claimedScoreBounty = true;
                bounty.remove(Bounty.Type.SCORE);
            }

            killerPlayer.getStats().addScore(scoreGain);

            killerPlayer.setKillStreak(killerPlayer.getKillStreak() + 1);
            currentStreak = killerPlayer.getKillStreak();
            killerPlayer.setKills(killerPlayer.getKills() + 1);
            personalBest = killerPlayer.getStats().getHighestKillstreak();

            if (currentStreak > personalBest) {
                killerPlayer.getStats().setHighestKillstreak(currentStreak);
            }

            if (getSettings().isGiveXp()) {
                xpGain = settings.getKillXPGain();
                if (getSettings().isMultiplier()) {
                    xpGain *= (int) killerRank.getMultiplier();
                }
                killerPlayer.getNexusPlayer().addXp(xpGain);
            }

            if (getSettings().isGiveCredits()) {
                creditGain = settings.getKillCreditGain();
                if (getSettings().isMultiplier()) {
                    creditGain *= (int) killerRank.getMultiplier();
                }

                if (creditBounty > 0) {
                    creditGain += (int) creditBounty;
                    bounty.remove(Bounty.Type.CREDIT);
                    claimedCreditBounty = true;
                }
                killerPlayer.getBalance().addCredits(creditGain);
            }

            if (getSettings().isEarnNexites()) {
                nexiteGain = settings.getKillNexiteGain();
                if (getSettings().isMultiplier() && killerRank.isNexiteBoost()) {
                    nexiteGain *= (int) killerRank.getMultiplier();
                }

                killerPlayer.getBalance().addNexites(nexiteGain);
            }

            killerPlayer.getStats().addKills(1);
            if (killer.isMutationKill()) {
                killerPlayer.getStats().addMutationKills(1);
                removeMutation(killerPlayer.getMutation());
                killerPlayer.sendMessage(killerPlayer.getTeam().getLeaveMessage());
                killerPlayer.setTeam(GameTeam.TRIBUTES);
                Bukkit.getPlayer(killerPlayer.getUniqueId()).setGameMode(GameTeam.TRIBUTES.getGameMode());
                killerPlayer.sendMessage(killerPlayer.getTeam().getJoinMessage());
            }
        }

        List<UUID> damagers = gamePlayer.getDamageInfo().getDamagers();
        List<AssisterInfo> assistors = new ArrayList<>();
        if (settings.isAllowAssists()) {
            if (!damagers.isEmpty()) {
                for (UUID damager : damagers) {
                    if (killer != null && killer.getKiller().equals(damager)) {
                        continue;
                    }

                    GamePlayer assisterPlayer = getPlayer(damager);
                    assisterPlayer.setAssists(assisterPlayer.getAssists() + 1);
                    assisterPlayer.getStats().addAssists(1);
                    assistors.add(new AssisterInfo(this, assisterPlayer));
                    this.gameInfo.getActions().add(new GameAction(System.currentTimeMillis(), "assist", assisterPlayer.getName() + " assisted the death of " + gamePlayer.getName()));
                }
            }
        }

        int oldTeamRemaining = 0;
        for (GamePlayer gp : new ArrayList<>(getPlayers().values())) {
            if (gp.getTeam() == oldTeam) {
                oldTeamRemaining++;
            }
        }

        if (oldTeam == GameTeam.MUTATIONS) {
            removeMutation(gamePlayer.getMutation());
        }

        if (oldTeam == GameTeam.TRIBUTES) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getTeam() != GameTeam.MUTATIONS) {
                    continue;
                }

                Mutation mutation = gp.getMutation();
                if (!mutation.getTarget().equals(gamePlayer.getUniqueId())) {
                    continue;
                }

                if (!playerKiller) {
                    gp.sendMessage("&6&l>> &cYour target died without a killer, you have been set back as a spectator.");
                    removeMutation(mutation);
                    gp.sendMessage(gp.getTeam().getLeaveMessage());
                    gp.setTeam(GameTeam.SPECTATORS);
                    Player mutationPlayer = Bukkit.getPlayer(gp.getUniqueId());
                    mutationPlayer.spigot().setCollidesWithEntities(false);
                    mutationPlayer.setAllowFlight(true);
                    giveSpectatorItems(mutationPlayer);
                    gp.sendMessage(gp.getTeam().getJoinMessage());
                } else {
                    mutation.setTarget(killer.getKiller());
                    gp.sendMessage("&6&l>> &cYour target died, your new target is &a" + getPlayer(killer.getKiller()).getName());
                }
            }
        }

        int totalTributes = 0;
        for (GamePlayer gp : new ArrayList<>(this.players.values())) {
            if (gp.getTeam() == GameTeam.TRIBUTES) {
                totalTributes++;
            }
        }

        if (totalTributes <= settings.getDeathmatchThreshold()) {
            if (state == INGAME) {
                if (totalTributes > 1) {
                    if (controlType == ControlType.AUTOMATIC) {
                        this.startDeathmatchTimer();
                    } else {
                        sendMessage("&eTribute count reached or went below the deathmatch threashold, but was not automatically started due to being in manual mode.");
                    }
                }
            }
        }

        GamePlayer killerPlayer = null;
        if (playerKiller) {
            killerPlayer = getPlayer(killer.getKiller());
        }

        gamePlayer.sendMessage(oldTeam.getLeaveMessage());

        if (killerPlayer != null) {
            if (killer.isMutationKill()) {
                sendMessage("&6&l>> " + killerPlayer.getColoredName() + " &ahas taken revenge and is back in the game!");
            }
            if (currentStreak > personalBest) {
                if (!killerPlayer.isNewPersonalBestNotified()) {
                    killerPlayer.sendMessage("&6&l>> &a&lNEW PERSONAL BEST!");
                    killerPlayer.setNewPersonalBestNotified(true);
                }
                personalBest = currentStreak;
            }
            killerPlayer.sendMessage("&6&l>> &f&lCurrent Streak: &a" + currentStreak + "  &f&lPersonal Best: &a" + personalBest);
            killerPlayer.sendMessage("&2&l>> &a+" + scoreGain + " Score!" + (claimedScoreBounty ? " &e&lClaimed Bounty" : "") + (claimedFirstBlood ? " &c&lFirst Blood" : ""));
            double multiplier = killerRank.getMultiplier();
            String multiplierMessage = "";
            if (multiplier > 1) {
                multiplierMessage = killerRank.getColor() + "&l * x" + MCUtils.formatNumber(multiplier) + " " + killerRank.getPrefix() + " Bonus";
            }
            if (settings.isGiveXp()) {
                String xpMsg = "&2&l>> &a&l+" + xpGain + " &2&lXP&a&l!";
                if (settings.isMultiplier()) {
                    xpMsg += multiplierMessage;
                }
                killerPlayer.sendMessage(xpMsg);
            }

            if (settings.isGiveCredits()) {
                String creditsMsg = "&2&l>> &a&l+" + creditGain + " &3&lCREDITS&a&l!";
                if (settings.isMultiplier()) {
                    creditsMsg += multiplierMessage;
                }

                if (claimedCreditBounty) {
                    creditsMsg += " &e&lClaimed Bounty";
                }

                killerPlayer.sendMessage(creditsMsg);
            }

            if (settings.isEarnNexites()) {
                String nexiteMsg = "&2&l>> &a&l" + nexiteGain + " &9&lNEXITES&a&l!";
                if (settings.isMultiplier() && killerRank.isNexiteBoost()) {
                    nexiteMsg += multiplierMessage;
                }

                killerPlayer.sendMessage(nexiteMsg);
            }
        }

        for (AssisterInfo assister : assistors) {
            GamePlayer assisterPlayer = assister.getGamePlayer();
            assisterPlayer.sendMessage("&2&l>> &a+1 &aAssist");
            String multiplierMsg = assisterPlayer.getRank().getColor() + "&l * x" + MCUtils.formatNumber(assisterPlayer.getRank().getMultiplier()) + " " + assisterPlayer.getRank().getPrefix() + " Bonus";
            String xpMsg = "&2&l>> &a&l+" + (int) assister.getXp() + " &2&lXP&a&l!" + (settings.isMultiplier() ? " " + multiplierMsg : "");
            String creditsMsg = "&2&l>> &a&l+" + (int) assister.getCredits() + " &3&lCREDITS&a&l!" + (settings.isMultiplier() ? " " + multiplierMsg : "");
            String nexitesMsg = "&2&l>> &a&l" + (int) assister.getNexites() + " &9&lNEXITES&a&l!" + (settings.isMultiplier() && assisterPlayer.getRank().isNexiteBoost() ? " " + multiplierMsg : "");
            if (assister.getXp() > 0) {
                assisterPlayer.sendMessage(xpMsg);
            }

            if (assister.getCredits() > 0) {
                assisterPlayer.sendMessage(creditsMsg);
            }

            if (assister.getNexites() > 0) {
                assisterPlayer.sendMessage(nexitesMsg);
            }
        }

        gamePlayer.sendMessage("&4&l>> &cYou lost " + lost + " Points for dying!");
        sendMessage("&6&l>> " + oldTeam.getRemainColor() + "&l" + oldTeamRemaining + " " + oldTeam.name().toLowerCase() + " remain.");
        if (claimedFirstBlood) {
            sendMessage("&6&l>> &c&l" + firstBlood.getName().toUpperCase() + " CLAIMED FIRST BLOOD!");
        }

        if (killerPlayer != null) {
            String killerName = killerPlayer.getColoredName();
            String killerHealth = NumberHelper.formatNumber(killer.getHealth());
            gamePlayer.sendMessage("&4&l>> &cYour killer &8(" + killerName + "&8) &chad &4" + killerHealth + " HP &cremaining!");
        }

        sendMessage(deathInfo.getDeathMessage());
        gamePlayer.sendMessage(GameTeam.SPECTATORS.getJoinMessage());

        if (claimedScoreBounty) {
            sendMessage("&6&l>> " + killerPlayer.getColoredName() + " &6&lhas claimed the &b&l" + scoreBounty + " Score &6&lbounty on " + gamePlayer.getColoredName());
        }

        if (claimedCreditBounty && settings.isGiveCredits()) {
            sendMessage("&6&l>> " + killerPlayer.getColoredName() + " &6&lhas claimed the &b&l" + scoreBounty + " Credit &6&lbounty on " + gamePlayer.getColoredName());
        }

        playSound(oldTeam.getDeathSound());

        new BukkitRunnable() {
            public void run() {
                if (state == ENDING || state == ENDED) {
                    cancel();
                    return;
                }
                if (Stream.of(INGAME, DEATHMATCH, INGAME_DEATHMATCH, WARMUP).anyMatch(gameState -> state == gameState)) {
                    checkGameEnd();
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    public void giveSpectatorItems(Player p) {
        GamePlayer gamePlayer = getPlayer(p.getUniqueId());
        gamePlayer.giveSpectatorItems(this);
    }

    public void checkGameEnd() {
        if (Stream.of(INGAME, INGAME_DEATHMATCH, DEATHMATCH).anyMatch(gameState -> this.state == gameState)) {
            int totalTributes = 0;
            for (GamePlayer player : this.players.values()) {
                if (player.getTeam() == GameTeam.TRIBUTES) {
                    totalTributes++;
                }
            }

            if (totalTributes <= 1) {
                gameComplete();
            }
        }
    }

    public GamePlayer getPlayer(UUID uniqueId) {
        for (GamePlayer player : new ArrayList<>(this.players.values())) {
            if (player.getUniqueId().toString().equalsIgnoreCase(uniqueId.toString())) {
                return player;
            }
        }

        return null;
    }

    public void startDeathmatchTimer() {
        setState(INGAME_DEATHMATCH);
        if (this.timer != null) {
            timer.cancel();
        }

        if (this.graceperiodTimer != null) {
            this.graceperiodTimer.cancel();
            this.graceperiodTimer = null;
        }

        if (this.restockTimer != null) {
            this.restockTimer.cancel();
            this.restockTimer = null;
        }

        sendMessage("&6&l>> &4&lTHE DEATHMATCH COUNTDOWN HAS STARTED");
        this.timer = new Timer(new DeathmatchPlayingCallback(this)).run(TimeUnit.SECONDS.toMillis(settings.getDeathmatchTimerLength()) + 50);
    }

    public boolean isLootedChest(Block block) {
        return this.lootedChests.contains(block.getLocation());
    }

    public void addLootedChest(Location location) {
        this.lootedChests.add(location);
    }

    public void markGraceperiodDone() {
        this.graceperiod = Graceperiod.TIMER_DONE;
    }

    public void endGracePeriod() {
        sendMessage("&6&l>> &eThe &c&lGRACE PERIOD &ehas ended.");
        this.graceperiod = Graceperiod.INACTIVE;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void addMutation(Mutation mutation) {
        GamePlayer gamePlayer = getPlayer(mutation.getPlayer());
        sendMessage("&6&l>> " + gamePlayer.getColoredName() + " &6has &lMUTATED &6as a(n) &l" + mutation.getType().getDisplayName() + " &6and seeks revenge on &a" + Bukkit.getPlayer(mutation.getTarget()).getName() + "&6!");

        MapSpawn spawn = gameMap.getSpawns().get(new Random().nextInt(gameMap.getSpawns().size()));
        Location location = spawn.toGameLocation(this.gameMap.getWorld(), gameMap.getCenter().toLocation(gameMap.getWorld()));
        Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());

        gamePlayer.sendMessage(gamePlayer.getTeam().getLeaveMessage());
        gamePlayer.setTeam(GameTeam.MUTATIONS);
        gamePlayer.sendMessage(gamePlayer.getTeam().getJoinMessage());
        DisguiseAPI.disguiseEntity(player, new MobDisguise(mutation.getType().getDisguiseType()));

        gamePlayer.setMutated(true);
        teleportMutation(player, location);
        recalculateVisibility();
        gamePlayer.clearInventory();
        gamePlayer.setFlight(false, false);
        gamePlayer.setCollisions(true);
        gamePlayer.setFood(20, 20F);

        MutationType type = mutation.getType();
        gamePlayer.setHealth(20, type.getHealth());
        PlayerInventory inv = player.getInventory();
        inv.setItem(0, type.getWeapon());
        inv.setItem(1, Items.PLAYER_TRACKER.getItemStack());
        for (MutationItem item : type.getItems()) {
            inv.setItem(1 + item.slotOffset(), item.itemStack());
        }
        gamePlayer.setArmor(type.getArmorType());
        for (MutationEffect effect : type.getEffects()) {
            player.addPotionEffect(new PotionEffect(effect.getPotionType(), Integer.MAX_VALUE, effect.getAmplifier(), false, false));
        }
    }

    public void removeMutation(Mutation mutation) {
        Player player = Bukkit.getPlayer(mutation.getPlayer());
        DisguiseAPI.undisguiseToAll(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setMaxHealth(settings.getMaxHealth());
        player.setHealth(settings.getMaxHealth());
        player.setExp(0);
        player.setLevel(0);
        player.spigot().setCollidesWithEntities(true);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        GamePlayer gamePlayer = getPlayer(player.getUniqueId());
        if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
            giveSpectatorItems(player);
        }
    }

    public GamePlayer getPlayer(String name) {
        for (GamePlayer gamePlayer : new ArrayList<>(this.players.values())) {
            if (gamePlayer.getName().equalsIgnoreCase(name)) {
                return gamePlayer;
            }
        }
        return null;
    }

    public int getTeamCount(GameTeam gameTeam) {
        int amount = 0;
        for (GamePlayer gamePlayer : new ArrayList<>(this.players.values())) {
            if (gamePlayer.getTeam() == gameTeam) {
                amount++;
            }
        }
        return amount;
    }

    public SponsorManager getSponsorManager() {
        return sponsorManager;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isDebug() {
        return this.debugMode;
    }

    public boolean isGraceperiod() {
        return graceperiod == Graceperiod.ACTIVE || graceperiod == Graceperiod.TIMER_DONE;
    }

    public void startGraceperiod() {
        graceperiod = Graceperiod.ACTIVE;
    }

    public void setSpawn(int index, UUID uuid) {
        this.spawns.put(index, uuid);
    }

    public void gameComplete() {
        setState(GameState.GAME_COMPLETE);
    }

    public void nextGameReady() {
        setState(GameState.NEXT_GAME_READY);
    }

    public static SurvivalGames getPlugin() {
        return plugin;
    }

    public Map<Integer, UUID> getSpawns() {
        return spawns;
    }

    public Map<GameTeam, GameTeamChatroom> getChatRooms() {
        return chatRooms;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameMap=" + gameMap.getName() +
                ", settings=" + settings +
                ", players=" + players +
                ", spawns=" + spawns +
                ", state=" + state +
                ", timer=" + timer +
                ", graceperiodTimer=" + graceperiodTimer +
                ", restockTimer=" + restockTimer +
                ", ratingPromptTimer=" + ratingPromptTimer +
                ", lootedChests=" + lootedChests +
                ", gameInfo=" + gameInfo +
                ", start=" + start +
                ", end=" + end +
                ", firstBlood=" + firstBlood +
                ", enderchestInventories=" + enderchestInventories +
                ", sponsorManager=" + sponsorManager +
                ", mode=" + mode +
                ", debugMode=" + debugMode +
                ", graceperiod=" + graceperiod +
                ", setupPhase=" + setupPhase +
                ", assignTeamsPhase=" + assignTeamsPhase +
                '}';
    }
}