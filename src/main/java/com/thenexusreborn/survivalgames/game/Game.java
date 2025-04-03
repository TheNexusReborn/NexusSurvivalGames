package com.thenexusreborn.survivalgames.game;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.stardevllc.clock.clocks.Timer;
import com.stardevllc.helper.StringHelper;
import com.stardevllc.registry.StringRegistry;
import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starchat.rooms.DefaultPermissions;
import com.stardevllc.starcore.StarColors;
import com.stardevllc.time.TimeFormat;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.api.gamearchive.GameInfo;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.gamemaps.model.MapSpawn;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.chat.GameChatRoom;
import com.thenexusreborn.survivalgames.chat.GameTeamChatroom;
import com.thenexusreborn.survivalgames.control.ControlType;
import com.thenexusreborn.survivalgames.control.Controllable;
import com.thenexusreborn.survivalgames.disguises.DisguiseAPI;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.MobDisguise;
import com.thenexusreborn.survivalgames.game.Bounty.Type;
import com.thenexusreborn.survivalgames.game.death.*;
import com.thenexusreborn.survivalgames.game.timer.callbacks.GameMinutesCallback;
import com.thenexusreborn.survivalgames.game.timer.callbacks.GameSecondsCallback;
import com.thenexusreborn.survivalgames.game.timer.endconditions.*;
import com.thenexusreborn.survivalgames.gamelog.*;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.loot.item.Items;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.sponsoring.SponsorManager;
import com.thenexusreborn.survivalgames.state.*;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.thenexusreborn.survivalgames.game.Game.State.*;

@SuppressWarnings({"unused"})
public class Game implements Controllable, IHasState {
    private static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    public static final TimeFormat SHORT_TIME_FORMAT = new TimeFormat("%*#0h%%*#0m%%*#0s%");
    public static final TimeFormat TIME_FORMAT = new TimeFormat("%*00h%%#0m%%00s%");
    public static final TimeFormat LONG_TIME_FORMAT = new TimeFormat("%*00h%%00m%%00s%");

    public enum State implements IState {
        UNDEFINED, ERROR, SHUTTING_DOWN,
        SETTING_UP, SETUP_COMPLETE,
        ASSIGN_TEAMS, TEAMS_ASSIGNED,
        TELEPORT_START, TELEPORT_START_DONE,
        WARMUP, WARMUP_DONE,
        INGAME, INGAME_DEATHMATCH,
        TELEPORT_DEATHMATCH, TELEPORT_DEATHMATCH_DONE,
        DEATHMATCH_WARMUP, DEATHMATCH_WARMUP_DONE,
        DEATHMATCH,
        GAME_COMPLETE, NEXT_GAME_READY,
        ENDING, ENDED
    }
    
    public enum SubState implements ISubState {
        UNDEFINED, 
        DOWNLOADING_MAP, UNZIPPING_MAP, COPYING_MAP, LOADING_MAP, DEFINING_ARENA, DEFINING_DEATHMATCH, CLEARING_SPAWNS, DEFINING_GAMERULES,
        TELEPORT_TRIBUTES, TELEPORT_SPECTATORS, 
        TIMER_INIT, CALCULATE_RESTOCK, SETUP_GRACE_PERIOD, SETUP_BORDER, SETUP_SWAG_SHACK, 
        RESTOCKING_CHESTS, PLAYER_DEATH, ADD_MUTATION, 
        ADD_AS_TRIBUTE, REMOVE_FROM_GAME, REVIVE_PLAYER, MUTATE_PLAYER, PLAYER_JOIN, PLAYER_QUIT, 
        REMOVING_MUTATIONS, 
        TIMER_SHUTDOWN, DETERMINE_WINNER, SET_GAME_STATS
    }
    
    private final SGMap gameMap;
    private final SGVirtualServer server;
    private ControlType controlType = ControlType.AUTO;
    private final GameSettings settings;
    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private final Map<Integer, UUID> spawns = new HashMap<>();
    private final ChatRoom gameChatroom;
    private final Map<GameTeam, GameTeamChatroom> chatRooms = new HashMap<>();
    private State state = UNDEFINED;
    private SubState subState = SubState.UNDEFINED;
    private Timer timer, graceperiodTimer;
    private final List<Location> lootedChests = new ArrayList<>();
    private final GameInfo gameInfo;
    private long start, end;
    private GamePlayer firstBlood;
    private final Map<Location, Inventory> enderchestInventories = new HashMap<>();
    private SponsorManager sponsorManager = new SponsorManager();
    private Mode mode = Mode.CLASSIC; //This will be implemented later, this is mainly for some other checks to exist
    private boolean debugMode; //Debug Mode. This may be replaced with a class with other settings
    private Graceperiod graceperiod = Graceperiod.INACTIVE;

    private UUID restockCallbackId;
    private int timedRestockCount;

    public Game(SGVirtualServer server, SGMap gameMap, GameSettings settings, Collection<LobbyPlayer> players) {
        this.gameMap = gameMap;
        this.server = server;
        this.settings = settings;
        this.gameInfo = new GameInfo();
        
        this.gameChatroom = new GameChatRoom(this);
        plugin.getStarChat().getRoomRegistry().register(gameChatroom.getName(), gameChatroom);

        for (GameTeam team : GameTeam.values()) {
            GameTeamChatroom chatroom = new GameTeamChatroom(plugin, this, team);
            this.chatRooms.put(team, chatroom);
            plugin.getStarChat().getRoomRegistry().register(chatroom.getName(), chatroom);
        }

        gameInfo.setMapName(this.gameMap.getName().replace("'", "''"));
        gameInfo.setServerName(server.getName());
        for (MapSpawn spawn : this.gameMap.getSpawns()) {
            this.spawns.put(spawn.getIndex(), null);
        }
        List<String> playerNames = new ArrayList<>();
        int tributeCount = 0;
        for (LobbyPlayer player : players) {
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            GamePlayer gamePlayer = new GamePlayer(player.getPlayer(), this, player.getStats());
            sgPlayer.setGame(this, gamePlayer);
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

            if (sgPlayer.getNexusPlayer().getToggleValue("spectatorchat")) {
                this.chatRooms.get(GameTeam.SPECTATORS).addMember(player.getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
            }
        }
        gameInfo.setPlayerCount(tributeCount);
        gameInfo.setPlayers(playerNames.toArray(new String[0]));
    }
    
    public Graceperiod getGraceperiod() {
        return graceperiod;
    }
    
    public GamePlayer getFirstBlood() {
        return firstBlood;
    }
    
    public long getStart() {
        return start;
    }
    
    public SGVirtualServer getServer() {
        return server;
    }

    public void setState(State state) {
        this.gameInfo.getActions().add(new GameAction(System.currentTimeMillis(), "statechange").addValueData("oldvalue", this.state.name()).addValueData("newvalue", state.name()));
        this.state = state;
        this.subState = SubState.UNDEFINED;
    }
    
    public SubState getSubState() {
        return subState;
    }
    
    public void handleShutdown() {
        this.state = SHUTTING_DOWN;
//        sendMessage("&4&l>> THE SERVER IS SHUTTING DOWN!");
        if (this.timer != null) {
            this.timer.cancel();
        }

        if (this.graceperiodTimer != null) {
            this.graceperiodTimer.cancel();
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
            if (getTimer() != null) {
                getTimer().cancel();
                timer = null;
            }
            if (getGraceperiodTimer() != null) {
                getGraceperiodTimer().cancel();
                graceperiodTimer = null;
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

    public void addAsTribute(SGPlayer actor, GamePlayer target, SGLootTable lootTable, int amountOfItems) {
        if (target.getTeam() != GameTeam.SPECTATORS) {
            return;
        }
        
        setSubState(SubState.ADD_AS_TRIBUTE);

        target.sendMessage(target.getTeam().getLeaveMessage());
        target.setTeam(GameTeam.TRIBUTES);
        GameTeam.TRIBUTES.getPlayerState().apply(target);
        target.sendMessage(target.getTeam().getJoinMessage());

        int index = new Random().nextInt(getSpawns().size());
        MapSpawn spawnPosition = getGameMap().getSpawns().get(index);
        Location spawn = spawnPosition.toGameLocation(getGameMap().getWorld(), getGameMap().getCenterLocation());
        teleportTribute(Bukkit.getPlayer(target.getUniqueId()), spawn);

        if (lootTable != null && amountOfItems > 1) {
            List<ItemStack> loot = lootTable.generateLoot(amountOfItems);
            for (ItemStack item : loot) {
                target.addItem(item);
            }
        }

        sendMessage(MsgType.INFO.format("%v was added to the game by %v.", target.getColoredName(), actor.getNexusPlayer().getColoredName()));
        if (lootTable != null && amountOfItems > 1) {
            getGameInfo().getActions().add(new GamePlayerAddAction(actor.getName(), target.getName(), lootTable.getName(), amountOfItems));
        } else {
            getGameInfo().getActions().add(new GamePlayerAddAction(actor.getName(), target.getName()));
        }
        
        setSubState(SubState.UNDEFINED);
    }

    public void removeFromGame(SGPlayer actor, GamePlayer target) {
        if (target.getTeam() == GameTeam.SPECTATORS) {
            return;
        }

        setSubState(SubState.REMOVE_FROM_GAME);
        target.sendMessage(target.getTeam().getLeaveMessage());
        target.setTeam(GameTeam.SPECTATORS);
        GameTeam.SPECTATORS.getPlayerState().apply(target);
        target.giveSpectatorItems(this);
        target.sendMessage(target.getTeam().getJoinMessage());

        teleportSpectator(Bukkit.getPlayer(target.getUniqueId()), getGameMap().getCenterLocation());
        sendMessage(MsgType.INFO.format("%v was removed from the game by %v.", target.getColoredName(), actor.getNexusPlayer().getColoredName()));
        getGameInfo().getActions().add(new GamePlayerRemoveAction(actor.getName(), target.getName()));
        setSubState(SubState.UNDEFINED);
    }

    public void revivePlayer(SGPlayer actor, GamePlayer target, SGLootTable lootTable, int amountOfItems) {
        if (target.getTeam() != GameTeam.SPECTATORS) {
            return;
        }

        if (!target.isSpectatorByDeath()) {
            return;
        }

        DeathInfo mostRecentDeath = target.getMostRecentDeath();

        if (mostRecentDeath == null) {
            return;
        }

        setSubState(SubState.REVIVE_PLAYER);
        target.sendMessage(target.getTeam().getLeaveMessage());
        target.setTeam(GameTeam.TRIBUTES);
        GameTeam.TRIBUTES.getPlayerState().apply(target);
        target.sendMessage(target.getTeam().getJoinMessage());

        if (lootTable != null && amountOfItems > 0) {
            List<ItemStack> loot = lootTable.generateLoot(amountOfItems);
            for (ItemStack item : loot) {
                target.addItem(item);
            }
        }

        sendMessage(MsgType.INFO.format("%v was revived by %v.", target.getColoredName(), actor.getNexusPlayer().getColoredName()));
        if (lootTable != null && amountOfItems > 1) {
            getGameInfo().getActions().add(new GamePlayerReviveAction(actor.getName(), target.getName(), lootTable.getName(), amountOfItems));
        } else {
            getGameInfo().getActions().add(new GamePlayerReviveAction(actor.getName(), target.getName()));
        }
        
        setSubState(SubState.UNDEFINED);
    }

    public void mutatePlayer(SGPlayer actor, MutationBuilder builder) {
        mutatePlayer(actor, builder.getPlayer(), builder.getType(), builder.getTarget(), builder.isBypassTimer());
    }

    public void mutatePlayer(SGPlayer actor, GamePlayer target, MutationType type, GamePlayer mutationTarget, boolean bypassTimer) {
        if (target.getTeam() != GameTeam.SPECTATORS) {
            return;
        }

        if (type == null) {
            return;
        }

        if (mutationTarget == null) {
            return;
        }

        if (mutationTarget.getTeam() != GameTeam.TRIBUTES) {
            return;
        }
        
        setSubState(SubState.MUTATE_PLAYER);

        Mutation mutation = Mutation.createInstance(this, type, target.getUniqueId(), mutationTarget.getUniqueId());
        target.setMutation(mutation);

        getGameInfo().getActions().add(new GamePlayerForceMutateAction(actor.getName(), target.getName(), type, mutationTarget.getName(), bypassTimer));

        if (!bypassTimer) {
            mutation.startCountdown();
        } else {
            mutationTarget.sendMessage(StarColors.color("&6&l>> " + target.getColoredName().toUpperCase() + " &c&lIS AFTER YOU! RUN!"));

            getGameInfo().getActions().add(new GameMutateAction(target.getName(), mutationTarget.getName(), mutation.getType()));
            addMutation(mutation);
        }
        
        setSubState(SubState.UNDEFINED);
    }

    public void join(NexusPlayer nexusPlayer, SGPlayerStats stats) {
        setSubState(SubState.PLAYER_JOIN);
        GamePlayer gamePlayer = new GamePlayer(nexusPlayer, this, stats);
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());
        sgPlayer.setGame(this, gamePlayer);
        gamePlayer.setStatus(GamePlayer.Status.ADDING_TO_GAME);
        gamePlayer.setTeam(GameTeam.SPECTATORS);
        gamePlayer.sendMessage(GameTeam.SPECTATORS.getJoinMessage());
        this.players.put(nexusPlayer.getUniqueId(), gamePlayer);
        gamePlayer.setStatus(GamePlayer.Status.SETTING_UP_PLAYER);
        Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
        this.gameChatroom.addMember(player.getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
        GameTeam.SPECTATORS.getPlayerState().apply(player);
        gamePlayer.giveSpectatorItems(this);
        gamePlayer.setStatus(GamePlayer.Status.TELEPORTING_TO_CENTER);
        teleportSpectator(player, this.gameMap.getSpawnCenter().toLocation(this.gameMap.getWorld()));

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
        setSubState(SubState.UNDEFINED);
    }

    public void quit(UUID uuid) {
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
        if (nexusPlayer != null) {
            quit(nexusPlayer);
        } else {
            this.players.remove(uuid);
            for (GameTeamChatroom gtc : this.chatRooms.values()) {
                gtc.removeMember(uuid);
                this.gameChatroom.removeMember(uuid);
            }
        }
    }

    public void quit(NexusPlayer nexusPlayer) {
        if (!this.players.containsKey(nexusPlayer.getUniqueId())) {
            return;
        }
        setSubState(SubState.PLAYER_QUIT);
        GamePlayer gamePlayer = this.players.get(nexusPlayer.getUniqueId());
        this.chatRooms.get(gamePlayer.getTeam()).removeMember(gamePlayer.getUniqueId());
        this.gameChatroom.removeMember(gamePlayer.getUniqueId());
        Set<IState> ignoreStates = Set.of(UNDEFINED, SETTING_UP, SETUP_COMPLETE, ASSIGN_TEAMS, TEAMS_ASSIGNED, TELEPORT_START, TELEPORT_START_DONE, ERROR, ENDING, ENDED);
        if (!ignoreStates.contains(this.state)) {
            if (gamePlayer.getTeam() == GameTeam.TRIBUTES || gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                killPlayer(gamePlayer, new DeathInfo(this, System.currentTimeMillis(), gamePlayer, DeathType.SUICIDE, null));
            }
        }

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());
        sgPlayer.setGame(null, null);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            SQLDatabase database = NexusAPI.getApi().getPrimaryDatabase();
            database.saveSilent(sgPlayer.getStats());
            database.saveSilent(sgPlayer.getNexusPlayer().getBalance());
            database.saveSilent(sgPlayer.getNexusPlayer().getExperience());
        });

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
        setSubState(SubState.UNDEFINED);
    }

    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }

    public void resetSpawns() {
        setSubState(SubState.CLEARING_SPAWNS);
        this.spawns.entrySet().forEach(entry -> entry.setValue(null));
        setSubState(SubState.UNDEFINED);
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

    private void teleportToGameSpawn(Player player, Location spawn, GameTeam gameTeam) {
        player.teleport(spawn);
        GamePlayer gamePlayer = this.players.get(player.getUniqueId());
        gamePlayer.setPosition(player.getLocation());
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
            this.players.get(spectator.getUniqueId()).setPosition(mapSpawn);
        } catch (NullPointerException e) {
            if (spectator != null) {
                throw e;
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
        resetSpawns();
        List<UUID> tributes = new ArrayList<>(), spectators = new ArrayList<>();
        for (GamePlayer player : getPlayers().values()) {
            player.getTeam().getPlayerState().apply(player);
            if (player.getTeam() == GameTeam.TRIBUTES) {
                tributes.add(player.getUniqueId());
            } else if (player.getTeam() == GameTeam.SPECTATORS) {
                spectators.add(player.getUniqueId());
                player.giveSpectatorItems(this);
            }
        }

        Location mapSpawn = getGameMap().getSpawnCenter().toLocation(getGameMap().getWorld());
        setSubState(SubState.TELEPORT_TRIBUTES);
        teleportTributes(tributes, mapSpawn);
        setSubState(SubState.TELEPORT_SPECTATORS);
        teleportSpectators(spectators, mapSpawn);
        for (Entity entity : getGameMap().getWorld().getEntities()) {
            if (entity instanceof Monster) {
                entity.remove();
            }
        }
        setState(TELEPORT_START_DONE);
    }

    public void assignStartingTeams() {
        setState(ASSIGN_TEAMS);
        //TODO Add a secondary check for total games played as well
        SortedSet<GamePlayer> players = new TreeSet<>((player, other) -> {
            SGPlayer sgPlayer = SurvivalGames.getInstance().getPlayerRegistry().get(player.getUniqueId());
            SGPlayer otherPlayer = SurvivalGames.getInstance().getPlayerRegistry().get(other.getUniqueId());
            return Long.compare(otherPlayer.getJoinTime(), sgPlayer.getJoinTime());
        });

        players.addAll(getPlayers().values());

        List<UUID> tributes = new LinkedList<>();
        for (GamePlayer player : players) {
            if (player.getTeam() == null) {
                if (tributes.size() >= getGameMap().getSpawns().size()) {
                    player.setTeam(GameTeam.SPECTATORS);
                } else {
                    player.setTeam(GameTeam.TRIBUTES);
                    tributes.add(player.getUniqueId());
                }
            }
            player.applyScoreboard();
        }
        setState(TEAMS_ASSIGNED);
    }
    
    public void setSubState(SubState subState) {
        this.subState = subState;
    }

    public void setup() {
        setState(SETTING_UP);
        
        setSubState(SubState.DOWNLOADING_MAP);
        if (!getGameMap().download(Game.getPlugin())) {
            handleError("Could not download map");
            return;
        }

        setSubState(SubState.UNZIPPING_MAP);
        if (!getGameMap().unzip(Game.getPlugin())) {
            handleError("Could not unzip map");
            return;
        }

        setSubState(SubState.COPYING_MAP);
        if (!getGameMap().copyFolder(Game.getPlugin(), getServer().getName() + "-", false)) {
            handleError("Could not copy map folder");
            return;
        }

        setSubState(SubState.LOADING_MAP);
        if (!getGameMap().load(Game.getPlugin())) {
            handleError("Could not load map");
            return;
        }

        setSubState(SubState.DEFINING_ARENA);
        CuboidRegion arenaRegion = getGameMap().getArenaRegion();
        if (arenaRegion == null) {
            handleError("Could not define the region for the arena");
            return;
        }

        setSubState(SubState.DEFINING_DEATHMATCH);
        CuboidRegion deathmatchArea = getGameMap().getDeathmatchRegion();
        if (deathmatchArea == null) {
            handleError("Could not define the region for the deathmatch");
            return;
        }

        setSubState(SubState.CLEARING_SPAWNS);
        try {
            for (int i = 0; i < getGameMap().getSpawns().size(); i++) {
                setSpawn(i, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleError("Could not setup the spawns.");
            return;
        }

        setSubState(SubState.DEFINING_GAMERULES);
        try {
            gameMap.getWorld().setGameRuleValue("naturalRegeneration", "" + settings.isRegeneration());
            gameMap.getWorld().setGameRuleValue("doDaylightCycle", "" + settings.isTimeProgression());
            gameMap.getWorld().setGameRuleValue("doWeatherCycle", "" + settings.isWeatherProgression());
            gameMap.getWorld().setGameRuleValue("doMobSpawning", "false");
            gameMap.getWorld().setGameRuleValue("announceAdvancements", "false");
            gameMap.getWorld().setGameRuleValue("doFireTick", "false");
            gameMap.getWorld().setGameRuleValue("keepInventory", "false");
            gameMap.getWorld().setDifficulty(Difficulty.EASY);
        } catch (Exception e) {
            e.printStackTrace();
            handleError("Could not setup the world settings..");
        }

        setState(SETUP_COMPLETE);
    }

    public void handleError(String message) {
        setState(ERROR);
        sendMessage("&4&l>> &4" + message + " Resetting back to lobby.");
        Lobby lobby = new Lobby(plugin, server, LobbyType.CUSTOM);
        lobby.fromGame(this);
    }

    public void sendMessage(String message) {
        this.gameChatroom.sendMessage(new ChatContext(message));
    }

    public State getState() {
        return state;
    }

    public void startWarmup() {
        setState(WARMUP);
        setSubState(SubState.TIMER_INIT);
        this.timer = Game.getPlugin().getClockManager().createTimer(TimeUnit.SECONDS.toMillis(getSettings().getWarmupLength()) + 50L);
        this.timer.setEndCondition(new WarmupEndCondition(this));
        this.timer.addRepeatingCallback(new GameSecondsCallback(this, "&6&l>> &eThe game begins in &b{time}&e."), TimeUnit.SECONDS, 1);
        this.timer.addCallback(timerSnapshot -> {
            if (getSettings().isSounds()) {
                playSound(Sound.WOLF_HOWL);
            }
            sendMessage("&5&l/ / / / / / &d&lTHE NEXUS REBORN &5&l/ / / / / /");
            sendMessage("&6&lSurvival Games &7&oFree-for-all Deathmatch &8- &3Classic Mode");
            sendMessage("&8- &7Loot chests scattered around the map for gear.");
            sendMessage("&8- &7Outlast the other tributes and be the last one standing!");
            if (settings.isAllowDeathmatch()) {
                sendMessage("&8- &7Arena deathmatch begins after &e" + getSettings().getGameLength() + " minutes&7.");
            }
            sendMessage("");
            StringBuilder creatorBuilder = new StringBuilder();
            for (String creator : getGameMap().getCreators()) {
                creatorBuilder.append("&e").append(creator).append("&7, ");
            }

            if (creatorBuilder.length() < 2) {
                creatorBuilder.append("&eNot Configured, ");
            }

            sendMessage("&d&l>> &7Playing on &a" + getGameMap().getName() + " &7created by " + creatorBuilder.substring(0, creatorBuilder.length() - 2));
            if (getSettings().isGracePeriod()) {
                sendMessage("&d&l>> &7There is a &e" + getSettings().getGracePeriodLength() + " second &7grace period.");
            }
        }, TimeUnit.SECONDS.toMillis(getSettings().getWarmupLength()) / 2);
        this.timer.start();
        setSubState(SubState.UNDEFINED);
    }

    public void startGame() {
        setSubState(SubState.TIMER_INIT);
        this.timer = plugin.getClockManager().createTimer(TimeUnit.MINUTES.toMillis(settings.getGameLength()) + 50);

        Supplier<String> msg = () -> {
            String type;
            if (this.settings.isAllowDeathmatch()) {
                type = "DEATHMATCH";
            } else {
                type = "GAME";
            }

            return "&6&l>> &eThe &c&l" + type + " &ebegins in &b{time}&e.";
        };
        
        this.timer.addRepeatingCallback(new GameMinutesCallback(this, msg), TimeUnit.MINUTES, 1);
        this.timer.addRepeatingCallback(new GameSecondsCallback(this, msg, false), TimeUnit.SECONDS, 1);
        this.timer.addCallback(timerSnapshot -> {
            sendMessage("");
            sendMessage("&6&l>> &9&lWHAT DO YOU THINK OF &e&l" + getGameMap().getName().toUpperCase() + "&9&l?");
            sendMessage("&6&l>> &7Type &8[&6/ratemap &4&l1 &c&l2 &6&l3 &e&l4 &a&l5&8] &7to submit a rating!");
            sendMessage("");
        }, TimeUnit.MINUTES.toMillis(settings.getGameLength()) / 4);
        
        setSubState(SubState.CALCULATE_RESTOCK);
        long restockLength;
        if (settings.isChestRestockRelative()) {
            restockLength = settings.getGameLength() / settings.getChestRestockDenomination();
        } else {
            restockLength = settings.getChestRestockInterval();
        }
        this.restockCallbackId = this.timer.addRepeatingCallback(timerSnapshot -> {
            if (timerSnapshot.getTime() == timerSnapshot.getLength()) {
                return;
            }
            timedRestockCount++;
            restockChests();
            sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED");
        }, TimeUnit.MINUTES, restockLength);

        this.timer.setEndCondition(new InGameEndCondition(this));
        this.timer.start();

        this.start = System.currentTimeMillis();
        setSubState(SubState.SETUP_GRACE_PERIOD);
        if (this.settings.isGracePeriod()) {
            this.graceperiodTimer = plugin.getClockManager().createTimer(TimeUnit.SECONDS.toMillis(settings.getGracePeriodLength()) + 50L);
            this.graceperiodTimer.addRepeatingCallback(new GameSecondsCallback(this, "&6&l>> &eThe &c&lGRACE PERIOD &eends in &b{time}&e."), TimeUnit.SECONDS, 1);
            this.graceperiodTimer.setEndCondition(new GraceperiodEndCondition(this));
            this.graceperiodTimer.start();
            this.graceperiod = Graceperiod.ACTIVE;
        }
        setState(INGAME);
        sendMessage("&6&l>> &a&lMAY THE ODDS BE EVER IN YOUR FAVOR.");
        if (this.settings.isTeamingAllowed()) {
            sendMessage("&6&l>> &d&lTHERE IS A MAX OF " + this.settings.getMaxTeamAmount() + " PLAYER TEAMS.");
        } else {
            sendMessage("&6&l>> &d&lTEAMING IS NOT ALLOWED IN THIS GAME.");
        }
        
        if (this.settings.isShowBorders()) {
            setSubState(SubState.SETUP_BORDER);
            this.gameMap.applyWorldBoarder("game");
            setSubState(SubState.UNDEFINED);
        }

        if (gameMap.getSwagShack() != null) {
            setSubState(SubState.SETUP_SWAG_SHACK);
            Villager entity = (Villager) gameMap.getWorld().spawnEntity(gameMap.getSwagShack().toLocation(gameMap.getWorld()), EntityType.VILLAGER);
            entity.setCustomNameVisible(true);
            entity.setCustomName(StarColors.color("&e&lSwag Shack"));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));
        }
        
        setSubState(SubState.UNDEFINED);
    }

    public void restockChests() {
        setSubState(SubState.RESTOCKING_CHESTS);
        this.lootedChests.clear();
        setSubState(SubState.UNDEFINED);
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

            if (this.timer != null) {
                timer.cancel();
            }

            setSubState(SubState.REMOVING_MUTATIONS);
            for (GamePlayer gp : this.players.values()) {
                if (gp.getTeam() == GameTeam.MUTATIONS) {
                    gp.sendMessage(gp.getTeam().getLeaveMessage());
                    removeMutation(gp.getMutation());
                    gp.setTeam(GameTeam.SPECTATORS);
                    gp.giveSpectatorItems(this);
                    gp.sendMessage(gp.getTeam().getJoinMessage());
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
            Location mapSpawn = gameMap.getSpawnCenter().toLocation(gameMap.getWorld());
            setSubState(SubState.TELEPORT_TRIBUTES);
            teleportTributes(tributes, mapSpawn);
            setSubState(SubState.TELEPORT_SPECTATORS);
            teleportSpectators(spectators, mapSpawn);

            setState(TELEPORT_DEATHMATCH_DONE);
        } catch (Exception e) {
            e.printStackTrace();
            handleError("There was an error teleporting tributes to the deathmatch.");
        }
    }

    public void startDeathmatchWarmup() {
        setState(DEATHMATCH_WARMUP);
        
        playSound(Sound.ENDERDRAGON_GROWL);
        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                Bukkit.getPlayer(player.getUniqueId()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
            }
        }
        
        setSubState(SubState.TIMER_INIT);
        if (this.timer != null) {
            timer.cancel();
        }

        this.timer = plugin.getClockManager().createTimer(TimeUnit.SECONDS.toMillis(settings.getDeathmatchWarmupLength()) + 50L);
        this.timer.addRepeatingCallback(new GameSecondsCallback(this, "&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b{time}&e."), TimeUnit.SECONDS, 1);
        this.timer.setEndCondition(new DMWarmupEndCondition(this));
        this.timer.start();
        setSubState(SubState.UNDEFINED);
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

        if (this.settings.isShowBorders()) {
            setSubState(SubState.SETUP_BORDER);
            this.gameMap.applyWorldBoarder("deathmatch", settings.getDeathmatchLength() * 60);
        }

        setSubState(SubState.TIMER_INIT);
        this.timer = plugin.getClockManager().createTimer(TimeUnit.MINUTES.toMillis(settings.getDeathmatchLength()) + 50L);
        this.timer.addRepeatingCallback(new GameMinutesCallback(this, "&6&l>> &eThe &c&lGAME &eends &ein &b{time}&e."), TimeUnit.MINUTES, 1);
        this.timer.addRepeatingCallback(new GameSecondsCallback(this, "&6&l>> &eThe &c&lGAME &eends &ein &b{time}&e."), TimeUnit.SECONDS, 1);
        this.timer.setEndCondition(new DeathmatchEndCondition(this));
        this.timer.start();
        setSubState(SubState.UNDEFINED);
    }

    public void deathmatchWarmupDone() {
        setState(DEATHMATCH_WARMUP_DONE);
    }

    public void end() {
        setState(ENDING);
        this.end = System.currentTimeMillis();
        plugin.incrementGamesPlayed();
        setSubState(SubState.TIMER_SHUTDOWN);
        if (this.timer != null) {
            timer.cancel();
            this.timer = null;
        }

        if (this.graceperiodTimer != null) {
            graceperiodTimer.cancel();
            this.graceperiodTimer = null;
        }

        setSubState(SubState.DETERMINE_WINNER);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.players.containsKey(player.getUniqueId())) {
                continue;
            }
            player.setAllowFlight(true);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!this.players.containsKey(player.getUniqueId())) {
                    continue;
                }
                player.showPlayer(p);
                p.showPlayer(player);
            }

            GamePlayer gamePlayer = this.getPlayer(player.getUniqueId());
            gamePlayer.getCombatTag().setOther(null);

            if (gamePlayer.getMutation() != null) {
                removeMutation(gamePlayer.getMutation());
            }
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

                winner.getBalance().addNexites(nexites);
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
                    sendMessage("&6&l>> For winning the game, " + winner.getColoredName() + " &6&l has kept their &b&l" + MCUtils.formatNumber(amount) + " " + StringHelper.titlize(type.name()) + " &6&lbounty!");
                    if (type == Type.CREDITS) {
                        winner.getBalance().addCredits(amount);
                    } else if (type == Type.SCORE) {
                        winner.getStats().addScore((int) amount);
                    }
                }
            }
        }

        setSubState(SubState.SET_GAME_STATS);
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

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            NexusAPI.getApi().getPrimaryDatabase().saveSilent(gameInfo);
            try {
                NexusAPI.getApi().getGameLogExporter().exportGameInfo(gameInfo);
            } catch (IOException e) {
            }
            if (gameInfo.getId() == 0) {
                sendMessage("&4&l>> &cThere was a database error archiving the game. Please report with date and time.");
            } else {
                sendMessage("&6&l>> &aThis game has been archived!");
                sendMessage("&6&l>> &aCustom Website Coming Soon!");
                //sendMessage("&6&l>> &aGame Log: &bhttp://thenexusreborn.com:8051/game?id=" + gameInfo.getId());

                if (gameInfo.getId() % 1000 == 0) {
                    for (String p : gameInfo.getPlayers()) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            UUID uuid = NexusAPI.getApi().getPlayerManager().getUUIDFromName(p);

                            Tag tag = new Tag(uuid, gameInfo.getId() + "th", System.currentTimeMillis());
                            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
                            if (nexusPlayer != null) {
                                nexusPlayer.sendMessage(MsgType.INFO + "Unlocked the tag " + tag.getDisplayName());
                            }
                            NexusAPI.getApi().getPrimaryDatabase().saveSilent(tag);
                        });
                    }
                }

                for (GamePlayer gamePlayer : players.values()) {
                    NexusAPI.getApi().getPrimaryDatabase().queue(gamePlayer.getStats());
                    NexusAPI.getApi().getPrimaryDatabase().queue(gamePlayer.getBalance());
                    NexusAPI.getApi().getPrimaryDatabase().queue(gamePlayer.getNexusPlayer().getExperience());
                }

                NexusAPI.getApi().getPrimaryDatabase().flush();
            }
        });

        if (!(this.players.isEmpty() || Bukkit.getOnlinePlayers().isEmpty())) {
            this.timer = plugin.getClockManager().createTimer(TimeUnit.SECONDS.toMillis(settings.getNextGameStart()));
            this.timer.addRepeatingCallback(new GameSecondsCallback(this, "&6&l>> &eNext game starts in &b{time}&e."), TimeUnit.SECONDS, 1);
            this.timer.setEndCondition(snapshot -> {
                if (snapshot.getTime() == 0) {
                    if (getState() != ENDING) {
                        return true;
                    }
                    if (getControlType() == ControlType.AUTO) {
                        nextGame();
                    } else {
                        sendMessage("&eThe next game timer has concluded, but the mode is not automatic. Skipped automatically performing next game tasks.");
                    }
                    return true;
                }
                return false;
            });
            this.timer.start();
        } else {
            this.nextGame();
        }
        
        setSubState(SubState.UNDEFINED);
    }

    public void nextGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.players.containsKey(player.getUniqueId())) {
                Lobby.PLAYER_STATE.apply(player);
            }
        }

        StringRegistry<ChatRoom> roomRegistry = plugin.getStarChat().getRoomRegistry();
        roomRegistry.unregister(this.gameChatroom.getName());
        for (GameTeamChatroom chatroom : this.getChatRooms().values()) {
            roomRegistry.unregister(chatroom.getName());
        }

        setState(ENDED);
        server.getLobby().fromGame(this);
    }

    public void killPlayer(GamePlayer gamePlayer, DeathInfo deathInfo) {
        setSubState(SubState.PLAYER_DEATH);
        try {
            GameTeam oldTeam = gamePlayer.getTeam();
            gamePlayer.addDeathInfo(deathInfo);
            gamePlayer.setTeam(GameTeam.SPECTATORS);
            Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
            String strippedDeathMessage = ChatColor.stripColor(deathInfo.getDeathMessage());
            strippedDeathMessage = strippedDeathMessage.substring(3, strippedDeathMessage.length() - 1);
            GameAction deathAction = new GameAction(deathInfo.getTime(), "death");
            gameInfo.getActions().add(deathAction);
            deathAction.addValueData("message", strippedDeathMessage);
            deathAction.addValueData("type", deathInfo.getType().name());
            deathAction.addValueData("team", deathInfo.getTeam().name());
            boolean deathByLeave = deathInfo.getType() == DeathType.SUICIDE;
            gamePlayer.setSpectatorByDeath(!deathByLeave);
            KillerInfo killer = deathInfo.getKiller();
            gamePlayer.setDeathByMutation(killer != null && killer.isMutationKill());
            if (deathInfo.getKiller() != null) {
                deathAction.addValueData("killerType", killer.getType().name());
                if (killer.getDistance() > 0) {
                    deathAction.addValueData("killerDistance", killer.getDistance());
                }
                if (killer.getType() == EntityType.PLAYER) {
                    deathAction.addValueData("killerIsMutation", killer.isMutationKill());
                    deathAction.addValueData("killerName", killer.getName());
                    deathAction.addValueData("killerHealth", killer.getHealth());
                    if (killer.getHandItem() != null) {
                        deathAction.addValueData("killerHand", killer.getHandItem().getType().name().toLowerCase());
                    }
                }
            }

            GameTeam.SPECTATORS.getPlayerState().apply(player);
            gamePlayer.giveSpectatorItems(this);

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
            double creditBounty = bounty.getAmount(Bounty.Type.CREDITS);
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
                        bounty.remove(Bounty.Type.CREDITS);
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
                    killerPlayer.sendMessage(killerPlayer.getTeam().getJoinMessage());
                }
            }

            List<UUID> damagers = gamePlayer.getDamageInfo().getDamagers();
            List<AssisterInfo> assistors = new ArrayList<>();
            List<String> assistorNames = new ArrayList<>();
            if (settings.isAllowAssists()) {
                if (!damagers.isEmpty()) {
                    for (UUID damager : damagers) {
                        if (killer != null && killer.getKiller().equals(damager)) {
                            continue;
                        }

                        GamePlayer assisterPlayer = getPlayer(damager);
                        if (assisterPlayer != null) {
                            assistorNames.add(assisterPlayer.getName());
                            assisterPlayer.setAssists(assisterPlayer.getAssists() + 1);
                            assisterPlayer.getStats().addAssists(1);
                            assistors.add(new AssisterInfo(this, assisterPlayer));
                        }
                    }
                }
            }

            if (!assistorNames.isEmpty()) {
                StringBuilder assistorBuilder = new StringBuilder();
                for (String assistorName : assistorNames) {
                    assistorBuilder.append(assistorName).append(",");
                }
                assistorBuilder.deleteCharAt(assistorBuilder.length() - 1);
                deathAction.addValueData("assistors", assistorBuilder.toString());
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
                        gp.sendMessage("&6&l>> &cYour target, you have been made a spectator.");
                        gp.setTeam(GameTeam.SPECTATORS);
                        removeMutation(mutation);
                        gp.sendMessage(gp.getTeam().getLeaveMessage());
                        Player mutationPlayer = Bukkit.getPlayer(gp.getUniqueId());
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
                String killerHealth = MCUtils.formatNumber(killer.getHealth());
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
        } catch (Throwable t) {
            sendMessage(MsgType.SEVERE.format("&lThere was an error while handling " + gamePlayer.getName() + "'s death. Please report to Firestar311"));
            t.printStackTrace();
        }

        new BukkitRunnable() {
            public void run() {
                if (state == ENDING || state == ENDED) {
                    cancel();
                    return;
                }
                checkDeathmatchThreshold();
                checkGameEnd();

            }
        }.runTaskLater(plugin, 1L);
        
        setSubState(SubState.UNDEFINED);
    }

    public void checkDeathmatchThreshold() {
        if (state != INGAME) {
            return;
        }
        
        if (!settings.isAllowDeathmatch()) {
            return;
        }

        int totalTributes = 0;
        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                totalTributes++;
            }
        }

        if (totalTributes <= settings.getDeathmatchThreshold()) {
            if (totalTributes > 1) {
                if (controlType == ControlType.AUTO) {
                    if (this.timer.getTime() > 60000) {
                        this.startDeathmatchTimer();
                    }
                } else {
                    sendMessage("&eTribute count reached or went below the deathmatch threashold, but was not automatically started due to being in manual mode.");
                }
            }
        }
    }

    public void checkGameEnd() {
        //Count total tributes
        int totalTributes = 0;
        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                totalTributes++;
            }
        }

        //Check to see if the game is in progress, if it is in progress and one or less players remain, mark game complete, this will detect the winner
        if (Stream.of(INGAME, INGAME_DEATHMATCH, DEATHMATCH, TELEPORT_DEATHMATCH, TELEPORT_DEATHMATCH_DONE, DEATHMATCH_WARMUP, DEATHMATCH_WARMUP_DONE).anyMatch(gameState -> this.state == gameState)) {
            if (totalTributes <= 1) {
                gameComplete();
            }
        }

        //Check to see if the game is still setting up, if it is, reset the game back to the lobby, don't save stats, prevents stat farming for games and games won
        if (Stream.of(TEAMS_ASSIGNED, TELEPORT_START, TELEPORT_START_DONE, WARMUP, WARMUP_DONE).anyMatch(gameState -> this.state == gameState)) {
            if (totalTributes <= 1) {
                nextGameReady();
            }
        }
    }

    public GamePlayer getPlayer(UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }

        for (GamePlayer player : new ArrayList<>(this.players.values())) {
            if (player.getUniqueId().toString().equalsIgnoreCase(uniqueId.toString())) {
                return player;
            }
        }

        return null;
    }

    public void cleanup() {
        //TODO Implement this method, this will just cleanup things that it did. Just moving some of the logic around.
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

        sendMessage("&6&l>> &4&lTHE DEATHMATCH COUNTDOWN HAS STARTED");
        this.timer = plugin.getClockManager().createTimer(TimeUnit.SECONDS.toMillis(settings.getDeathmatchTimerLength()) + 50L);
        this.timer.addRepeatingCallback(new GameSecondsCallback(this, "&6&l>> &eThe &c&lDEATHMATCH &ebegins in &b{time}&e."), TimeUnit.SECONDS, 1);
        this.timer.setEndCondition(new DMTimerEndCondition(this));
        this.timer.start();
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
        setSubState(SubState.ADD_MUTATION);
        GamePlayer gamePlayer = getPlayer(mutation.getPlayer());
        sendMessage("&6&l>> " + gamePlayer.getColoredName() + " &6has &lMUTATED &6as a(n) &l" + mutation.getType().getDisplayName() + " &6and seeks revenge on &a" + Bukkit.getPlayer(mutation.getTarget()).getName() + "&6!");
        
        GamePlayer target = getPlayer(mutation.getTarget());
        target.sendMessage(StarColors.color("&6&l>> " + gamePlayer.getColoredName().toUpperCase() + " &c&lIS AFTER YOU! RUN!"));
        
        MapSpawn spawn = gameMap.getSpawns().get(new Random().nextInt(gameMap.getSpawns().size()));
        Location location = spawn.toGameLocation(this.gameMap.getWorld(), gameMap.getSpawnCenter().toLocation(gameMap.getWorld()));
        Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());

        gamePlayer.sendMessage(gamePlayer.getTeam().getLeaveMessage());
        gamePlayer.setTeam(GameTeam.MUTATIONS);
        gamePlayer.sendMessage(gamePlayer.getTeam().getJoinMessage());
        DisguiseAPI.disguiseEntity(player, new MobDisguise(mutation.getType().getDisguiseType()));
        gamePlayer.incrementTimesMutated();
        gamePlayer.sendMessage("&6&l>> &dYou have &b" + gamePlayer.getStats().getMutationPasses() + " Passes &dremaining.");
        gamePlayer.sendMessage("&d&l>> &7You're now disguised.");

        gamePlayer.setMutated(true);
        teleportMutation(player, location);
        GameTeam.MUTATIONS.getPlayerState().apply(player);
        mutation.cancelTimer();

        MutationType type = mutation.getType();
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
        
        getGameInfo().getActions().add(new GameMutateAction(gamePlayer.getName(), target.getName(), mutation.getType()));
        setSubState(SubState.UNDEFINED);
    }

    public void removeMutation(Mutation mutation) {
        Player player = Bukkit.getPlayer(mutation.getPlayer());
        DisguiseAPI.undisguiseToAll(player);
        GameTeam.TRIBUTES.getPlayerState().apply(player);
        GamePlayer gamePlayer = getPlayer(player.getUniqueId());
        gamePlayer.setMutation(null);
        gamePlayer.sendMessage("&&d&l>> &7You're no longer disguised.");
        if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
            gamePlayer.giveSpectatorItems(this);
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
        setState(GAME_COMPLETE);
    }

    public void nextGameReady() {
        setState(NEXT_GAME_READY);
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

    public long getNextRestock() {
        if (this.getState() != INGAME) {
            return 0;
        }

        if (this.restockCallbackId == null) {
            return 0;
        }

        long nextRun = this.timer.getNextRun(this.restockCallbackId);

        if (nextRun < 0) {
            return 0;
        }

        return timer.getTime() - nextRun;
    }

    public int getTimedRestockCount() {
        return timedRestockCount;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameMap=" + gameMap +
                ", server=" + server +
                ", controlType=" + controlType +
                ", settings=" + settings +
                ", players=" + players +
                ", spawns=" + spawns +
                ", gameChatroom=" + gameChatroom +
                ", chatRooms=" + chatRooms +
                ", state=" + state +
                ", timer=" + timer +
                ", graceperiodTimer=" + graceperiodTimer +
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
                ", restockCallbackId=" + restockCallbackId +
                ", timedRestockCount=" + timedRestockCount +
                '}';
    }
}