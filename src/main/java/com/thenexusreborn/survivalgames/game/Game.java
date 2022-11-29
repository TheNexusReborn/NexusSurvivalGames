package com.thenexusreborn.survivalgames.game;

import com.google.common.io.*;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.*;
import com.thenexusreborn.api.helper.*;
import com.thenexusreborn.api.multicraft.MulticraftAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.server.Environment;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.disguise.DisguiseAPI;
import com.thenexusreborn.disguise.disguisetypes.MobDisguise;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.nexuscore.util.region.Cuboid;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.Bounty.Type;
import com.thenexusreborn.survivalgames.game.death.*;
import com.thenexusreborn.survivalgames.game.timer.*;
import com.thenexusreborn.survivalgames.loot.Items;
import com.thenexusreborn.survivalgames.map.*;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.scoreboard.*;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import static com.thenexusreborn.survivalgames.game.GameState.*;

public class Game {
    private static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    private static ControlType controlType = ControlType.MANUAL;
    
    private final GameMap gameMap;
    private final GameSettings settings;
    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private final Map<Integer, UUID> spawns = new HashMap<>();
    private GameState state = UNDEFINED;
    private Timer timer, graceperiodTimer, restockTimer;
    private final List<Location> lootedChests = new ArrayList<>();
    private final GameInfo gameInfo;
    private long start, end;
    private GamePlayer firstBlood;
    private final Map<Location, Inventory> enderchestInventories = new HashMap<>();
    private final Map<Location, UUID> suicideLocations = new HashMap<>();
    
    public Game(GameMap gameMap, GameSettings settings, Collection<NexusPlayer> players, List<UUID> spectatingPlayers) {
        this.gameMap = gameMap;
        this.settings = settings;
        this.gameInfo = new GameInfo();
        gameInfo.setMapName(this.gameMap.getName().replace("'", "''"));
        gameInfo.setPlayerCount(players.size() - spectatingPlayers.size());
        gameInfo.setServerName(NexusAPI.getApi().getServerManager().getCurrentServer().getName());
        List<String> playerNames = new ArrayList<>();
        for (NexusPlayer player : players) {
            GamePlayer gamePlayer = new GamePlayer(player);
            if (spectatingPlayers.contains(player.getUniqueId()) || player.getToggles().getValue("vanish")) {
                gamePlayer.setTeam(GameTeam.SPECTATORS);
            } else {
                playerNames.add(player.getName());
            }
            player.setActionBar(new GameActionBar(plugin, gamePlayer));
            this.players.put(gamePlayer.getUniqueId(), gamePlayer);
        }
        gameInfo.setPlayers(playerNames.toArray(new String[0]));
        StringBuilder sb = new StringBuilder();
        for (Field field : this.settings.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                sb.append(field.getName()).append("=").append(field.get(this.settings).toString()).append(",");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        gameInfo.setSettings(sb.substring(0, sb.length() - 1));
    }
    
    protected void setState(GameState state) {
        this.state = state;
        this.gameInfo.getActions().add(new GameAction(System.currentTimeMillis(), "statechange", state.name()));
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
        
        if (this.gameMap != null) {
            this.gameMap.delete(plugin);
        }
    }
    
    public static ControlType getControlType() {
        return controlType;
    }
    
    public static void setControlType(ControlType controlType) {
        Game.controlType = controlType;
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
    
    public GameMap getGameMap() {
        return gameMap;
    }
    
    public GameSettings getSettings() {
        return settings;
    }
    
    public Timer getGraceperiodTimer() {
        return graceperiodTimer;
    }
    
    public Timer getRestockTimer() {
        return restockTimer;
    }
    
    public void addPlayer(NexusPlayer nexusPlayer) {
        GamePlayer gamePlayer = new GamePlayer(nexusPlayer);
        gamePlayer.setTeam(GameTeam.SPECTATORS);
        gamePlayer.sendMessage(GameTeam.SPECTATORS.getJoinMessage());
        this.players.put(nexusPlayer.getUniqueId(), gamePlayer);
        Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        giveSpectatorItems(player);
        player.spigot().setCollidesWithEntities(false);
        teleportSpectator(player, this.gameMap.getCenter().toLocation(this.gameMap.getWorld()));
        
        if (nexusPlayer.getToggles().getValue("vanish")) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getNexusPlayer().getRanks().get().ordinal() <= Rank.HELPER.ordinal() || gp.getUniqueId().equals(nexusPlayer.getUniqueId())) {
                    gp.sendMessage("&a&l>> " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &ejoined &e&overy silently&e.");
                }
            }
        } else if (nexusPlayer.getToggles().getValue("incognito")) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getNexusPlayer().getRanks().get().ordinal() <= Rank.HELPER.ordinal() || gp.getUniqueId().equals(nexusPlayer.getUniqueId())) {
                    gp.sendMessage("&a&l>> " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &ejoined &e&osilently&e.");
                }
            }
        } else {
            sendMessage("&a&l>> &b" + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &ejoined.");
        }
        nexusPlayer.getScoreboard().setView(new DefaultGameBoard(nexusPlayer.getScoreboard(), plugin));
        nexusPlayer.getScoreboard().setTablistHandler(new GameTablistHandler(nexusPlayer.getScoreboard(), plugin));
        nexusPlayer.setActionBar(new GameActionBar(plugin, gamePlayer));
        recalculateVisibility();
    }
    
    public void removePlayer(NexusPlayer nexusPlayer) {
        if (!this.players.containsKey(nexusPlayer.getUniqueId())) {
            return;
        }
        GamePlayer gamePlayer = this.players.get(nexusPlayer.getUniqueId());
        EnumSet<GameState> ignoreStates = EnumSet.of(UNDEFINED, SETTING_UP, SETUP_COMPLETE, ASSIGN_TEAMS, TEAMS_ASSIGNED, TELEPORT_START, TELEPORT_START_DONE, ERROR, ENDING, ENDED);
        if (!ignoreStates.contains(this.state)) {
            if (gamePlayer.getTeam() == GameTeam.TRIBUTES || gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                killPlayer(gamePlayer, new DeathInfo(plugin.getGame(), System.currentTimeMillis(), gamePlayer, com.thenexusreborn.survivalgames.game.death.DeathType.SUICIDE));
            }
        }
        
        this.players.remove(nexusPlayer.getUniqueId());
        
        if (nexusPlayer.getToggles().getValue("vanish")) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getNexusPlayer().getRanks().get().ordinal() <= Rank.HELPER.ordinal()) {
                    gp.sendMessage("&c&l<< " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &eleft &e&overy silently&e.");
                }
            }
        } else if (nexusPlayer.getToggles().getValue("incognito")) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getNexusPlayer().getRanks().get().ordinal() <= Rank.HELPER.ordinal()) {
                    gp.sendMessage("&c&l<< " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &eleft &e&osilently&e.");
                }
            }
        } else {
            sendMessage("&c&l<< &b" + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName() + " &eleft.");
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
                Location spawn = spawnPosition.toLocation(gameMap.getWorld());
                teleportTribute(player, mapSpawn, spawn);
                this.spawns.put(entry.getKey(), player.getUniqueId());
                spawns.remove(index);
            }
        }
    }
    
    private void teleportToGameSpawn(Player player, Location mapSpawn, Location spawn, GameTeam gameTeam) {
        spawn.setX(spawn.getX() + 0.5);
        spawn.setY(spawn.getY() + 2.0);
        spawn.setZ(spawn.getZ() + 0.5);
        spawn.setPitch(0);
        spawn.setYaw(SGUtils.getAngle(spawn.toVector(), mapSpawn.toVector()));
        player.teleport(spawn);
        player.setAllowFlight(false);
        player.setFlying(false);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(gameTeam.getGameMode());
            }
        }.runTaskLater(plugin, 1L);
    }
    
    public void teleportTribute(Player tribute, Location mapSpawn, Location spawn) {
        teleportToGameSpawn(tribute, mapSpawn, spawn, GameTeam.TRIBUTES);
    }
    
    public void teleportMutation(Player mutation, Location mapSpawn, Location spawn) {
        teleportToGameSpawn(mutation, mapSpawn, spawn, GameTeam.MUTATIONS);
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
                
                if (gamePlayer.getNexusPlayer().getToggles().getValue("vanish")) {
                    player.showPlayer(other);
                    if (otherGamePlayer.getNexusPlayer().getRanks().get().ordinal() > Rank.HELPER.ordinal()) {
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
        try {
            setState(TELEPORT_START);
            
            List<UUID> tributes = new LinkedList<>(), spectators = new LinkedList<>();
            for (GamePlayer player : this.players.values()) {
                Player p = Bukkit.getPlayer(player.getUniqueId());
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                p.setFlying(false);
                p.setAllowFlight(false);
                p.setSaturation(5.0F);
                if (player.getTeam() == GameTeam.TRIBUTES) {
                    tributes.add(player.getUniqueId());
                } else if (player.getTeam() == GameTeam.SPECTATORS) {
                    spectators.add(player.getUniqueId());
                    giveSpectatorItems(Bukkit.getPlayer(player.getUniqueId()));
                }
                for (PotionEffect potionEffect : p.getActivePotionEffects()) {
                    p.removePotionEffect(potionEffect.getType());
                }
            }
            resetSpawns();
            Location mapSpawn = gameMap.getCenter().toLocation(gameMap.getWorld());
            teleportTributes(tributes, mapSpawn);
            teleportSpectators(spectators, mapSpawn);
            for (Entity entity : this.gameMap.getWorld().getEntities()) {
                if (entity instanceof Monster) {
                    entity.remove();
                }
            }
            
            recalculateVisibility();
            
            setState(TELEPORT_START_DONE);
        } catch (Exception e) {
            e.printStackTrace();
            handleError("There was an error teleporting players to their starting positions.");
        }
    }
    
    public void assignStartingTeams() {
        try {
            setState(ASSIGN_TEAMS);
            UUID uuid;
            Queue<UUID> tributes = new LinkedList<>(), spectators = new LinkedList<>();
            while ((uuid = SurvivalGames.PLAYER_QUEUE.poll()) != null) {
                GamePlayer player = this.players.get(uuid);
                if (player.getTeam() != null) {
                    if (player.getTeam() == GameTeam.SPECTATORS) {
                        spectators.offer(player.getUniqueId());
                    }
                } else {
                    if (tributes.size() >= settings.getMaxPlayers()) {
                        player.setTeam(GameTeam.SPECTATORS);
                        spectators.offer(uuid);
                    } else {
                        player.setTeam(GameTeam.TRIBUTES);
                        tributes.offer(uuid);
                    }
                }
                
                player.sendMessage(player.getTeam().getJoinMessage());
            }
            
            UUID spectator;
            while ((spectator = spectators.poll()) != null) {
                SurvivalGames.PLAYER_QUEUE.offer(spectator);
            }
            
            UUID tribute;
            while ((tribute = tributes.poll()) != null) {
                SurvivalGames.PLAYER_QUEUE.offer(tribute);
            }
            
            for (GamePlayer player : new ArrayList<>(this.players.values())) {
                player.getNexusPlayer().getScoreboard().setTablistHandler(new GameTablistHandler(player.getNexusPlayer().getScoreboard(), plugin));
            }
            
            setState(TEAMS_ASSIGNED);
        } catch (Exception e) {
            e.printStackTrace();
            handleError("There was an error assiging teams.");
        }
    }
    
    public void setup() {
        setState(SETTING_UP);
        
        for (GamePlayer player : this.players.values()) {
            player.getNexusPlayer().getScoreboard().setView(new DefaultGameBoard(player.getNexusPlayer().getScoreboard(), plugin));
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameMap.download(plugin)) {
                    handleError("There was an error downloading the map.");
                    return;
                }
                if (!gameMap.unzip(plugin)) {
                    handleError("There was an error extracting the map files.");
                    return;
                }
                if (!gameMap.copyFolder(plugin, false)) {
                    handleError("There was an error copying the map files.");
                    return;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!gameMap.load(plugin)) {
                            handleError("There was an error loading the world.");
                            return;
                        }
                        
                        int radius = gameMap.getDeathmatchBorderDistance();
                        Location center = gameMap.getCenter().toLocation(gameMap.getWorld());
                        Location corner1 = center.clone();
                        corner1.add(radius, radius, radius);
                        Location corner2 = center.clone();
                        corner2.subtract(radius, radius, radius);
                        gameMap.setDeathmatchArea(new Cuboid(corner1, corner2));
                        
                        try {
                            for (int i = 0; i < gameMap.getSpawns().size(); i++) {
                                spawns.put(i, null);
                            }
                            
                            gameMap.getWorld().setGameRuleValue("naturalRegeneration", "" + settings.isRegeneration());
                            gameMap.getWorld().setGameRuleValue("doDaylightCycle", "" + settings.isTimeProgression());
                            gameMap.getWorld().setGameRuleValue("doWeatherCycle", "" + settings.isWeatherProgression());
                            gameMap.getWorld().setGameRuleValue("doMobSpawning", "false");
                            gameMap.getWorld().setGameRuleValue("announceAdvancements", "false");
                            gameMap.getWorld().setGameRuleValue("doFireTick", "false");
                            gameMap.getWorld().setGameRuleValue("keepInventory", "false");
                            gameMap.getWorld().setDifficulty(Difficulty.EASY);
                            setState(SETUP_COMPLETE);
                            plugin.getLobby().resetLobby();
                        } catch (Exception e) {
                            e.printStackTrace();
                            handleError("There was an error setting up the world.");
                        }
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }
    
    public void handleError(String message) {
        setState(ERROR);
        sendMessage("&4&l>> &4" + message + " Resetting back to lobby.");
        plugin.getLobby().resetLobby();
        plugin.getLobby().fromGame(this);
    }
    
    public void sendMessage(String message) {
        for (GamePlayer player : this.players.values()) {
            player.sendMessage(message);
        }
        
        Bukkit.getConsoleSender().sendMessage(MCUtils.color(message));
    }
    
    public GameState getState() {
        return state;
    }
    
    public void startWarmup() {
        setState(WARMUP);
        this.timer = new Timer(new CountdownTimerCallback(this)).run((settings.getWarmupLength() * 1000L) + 50L);
    }
    
    public void startGame() {
        this.timer = new Timer(new GameTimerCallback(this)).run(((settings.getGameLength() * 60000L) + 50));
        this.start = System.currentTimeMillis();
        if (this.settings.isGracePeriod()) {
            this.graceperiodTimer = new Timer(new GraceperiodCountdownCallback(this)).run((settings.getGracePeriodLength() * 1000L) + 50L);
            setState(INGAME_GRACEPERIOD);
        } else {
            setState(INGAME);
        }
        this.restockTimer = new Timer(new RestockTimerCallback(this)).run(600050L);
        sendMessage("&6&l>> &a&lMAY THE ODDS BE EVER IN YOUR FAVOR.");
        sendMessage("&6&l>> &c&lCLICKING MORE THAN 16 CPS WILL LIKELY RESULT IN A BAN.");
        if (this.settings.isTeamingAllowed()) {
            sendMessage("&6&l>> &d&lTHERE IS A MAXIUMUM OF 2 PLAYER TEAMS.");
        } else {
            sendMessage("&6&l>> &d&lTEAMING IS NOT ALLOWED.");
        }
    }
    
    public void restockChests() {
        this.lootedChests.clear();
        if (this.restockTimer != null) {
            this.restockTimer.cancel();
            this.restockTimer = null;
        }
        if (state == INGAME) {
            int secondsLeft = this.timer.getSecondsLeft();
            int minutesLeft = secondsLeft / 60;
            if (minutesLeft > 10) {
                this.restockTimer = new Timer(new RestockTimerCallback(this)).run(600050L);
            }
        }
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
    
    public boolean hasPlayer(UUID uniqueId) {
        return this.players.containsKey(uniqueId);
    }
    
    public void playSound(Sound sound) {
        for (GamePlayer player : this.players.values()) {
            Player p = Bukkit.getPlayer(player.getUniqueId());
            if (p != null) {
                p.playSound(p.getLocation(), sound, 1, 1);
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
                    player.getNexusPlayer().getStats().change("sg_deathmatches_reached", 1, StatOperator.ADD);
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
        
        this.timer = new Timer(new DeathmatchCountdownCallback(this)).run(10050);
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
        
        World world = this.gameMap.getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(this.gameMap.getCenter().toLocation(world));
        worldBorder.setSize(100);
        worldBorder.setSize(10, 300);
        
        long length = (settings.getDeathmatchLength() * (60000L)) + 50;
        this.timer = new Timer(new GameEndTimerCallback(this)).run(length);
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
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setAllowFlight(true);
            for (Player p : Bukkit.getOnlinePlayers()) {
                player.showPlayer(p);
                p.showPlayer(player);
            }
        }
        
        GamePlayer winner = null;
        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                player.getNexusPlayer().getStats().change("sg_games", 1, StatOperator.ADD);
                if (winner == null) {
                    winner = player;
                } else {
                    winner = null; //More than one winner
                    break;
                }
            }
        }
        
        String winnerName;
        NexusPlayer winnerPlayer = (winner != null) ? winner.getNexusPlayer() : null;
        if (winner != null) {
            winnerName = winnerPlayer.getDisplayName();
        } else {
            winnerName = "&f&lNo one";
        }
        
        sendMessage("&6&l>> " + winnerName + " &a&lhas won Survival Games!");
        
        if (winner != null) {
            winnerPlayer.getStats().change("sg_wins", 1, StatOperator.ADD);
            winnerPlayer.getStats().change("sg_win_streak", 1, StatOperator.ADD);
            int winGain = 50;
            int currentScore = winnerPlayer.getStats().getValue("sg_score").getAsInt();
            if (currentScore < 100 && currentScore > 50) {
                winGain *= 1.25;
            } else if (currentScore <= 50 && currentScore > 25) {
                winGain *= 1.5;
            } else if (currentScore < 25) {
                winGain *= 2;
            } else if (currentScore >= 500) {
                winGain *= .8;
            } else if (currentScore < 1000 && currentScore > 500) {
                winGain *= .75;
            } else if (currentScore >= 1000) {
                winGain *= .5;
            }
            winnerPlayer.getStats().change("sg_score", winGain, StatOperator.ADD);
            winner.sendMessage("&2&l>> &a+" + winGain + " Score!");
            double multiplier = winnerPlayer.getRanks().get().getMultiplier();
            Rank rank = winnerPlayer.getRanks().get();
            String multiplierMessage = rank.getColor() + "&l * x" + MCUtils.formatNumber(multiplier) + " " + rank.getPrefix() + " Bonus";
            if (settings.isGiveXp()) {
                double xp = 10;
                xp *= multiplier;
                winnerPlayer.getStats().change("xp", xp, StatOperator.ADD);
                String baseMessage = "&2&l>> &a&l+" + MCUtils.formatNumber(xp) + " &2&lXP&a&l!";
                if (multiplier > 1) {
                    winner.sendMessage(baseMessage + multiplierMessage);
                } else {
                    winner.sendMessage(baseMessage);
                }
            }
            
            if (settings.isGiveCredits()) {
                double credits = 10;
                credits *= multiplier;
                winnerPlayer.getStats().change("credits", credits, StatOperator.ADD);
                String baseMessage = "&2&l>> &a&l+" + MCUtils.formatNumber(credits) + " &3&lCREDITS&a&l!";
                if (multiplier > 1) {
                    winner.sendMessage(baseMessage + multiplierMessage);
                } else {
                    winner.sendMessage(baseMessage);
                }
            }
            
            double passWinValue = new Random().nextDouble();
            if (passWinValue <= getSettings().getPassRewardChance()) {
                winnerPlayer.getStats().change("sg_mutation_passes", 1, StatOperator.ADD);
                winnerPlayer.sendMessage("&2&l>> &a&lYou won a mutation pass! Great job!");
            } else {
                winnerPlayer.sendMessage(MsgType.INFO + "You did not win a mutation pass this time.");
            }
            
            Bounty bounty = winner.getBounty();
            for (Type type : Type.values()) {
                double amount = bounty.getAmount(type);
                if (amount > 0) {
                    sendMessage("&6&l>> For winning the game, " + winnerPlayer.getColoredName() + " &6&l has kept their &b&l" + NumberHelper.formatNumber(amount) + " " + StringHelper.capitalizeEveryWord(type.name()) + " &6&lbounty!");
                    if (type == Type.CREDIT) {
                        winnerPlayer.getStats().change("credits", (int) amount, StatOperator.ADD);
                    } else if (type == Type.SCORE) {
                        winnerPlayer.getStats().change("sg_score", (int) amount, StatOperator.ADD);
                    }
                }
            }
        }
        
        gameInfo.setGameStart(this.start);
        gameInfo.setGameEnd(this.end);
        if (winner != null) {
            gameInfo.setWinner(winnerPlayer.getName());
        } else {
            gameInfo.setWinner("No one");
        }
        
        if (this.firstBlood != null) {
            gameInfo.setFirstBlood(firstBlood.getNexusPlayer().getName());
        } else {
            gameInfo.setFirstBlood("No one");
        }
        
        gameInfo.setLength(this.end - this.start);
        
        NexusAPI.getApi().getThreadFactory().runAsync(() -> {
            NexusAPI.getApi().getPrimaryDatabase().push(gameInfo);
            if (gameInfo.getId() == 0) {
                sendMessage("&4&l>> &cThere was a database error archiving the game. Please report with date and time.");
            } else {
                sendMessage("&6&l>> &aThis game has been archived!");
                sendMessage("&6&l>> &aGame ID: &b" + gameInfo.getId() + " &7&oCustom Website Coming Soon.");
                
                if ((gameInfo.getId() % 1000) == 0) {
                    for (String p : gameInfo.getPlayers()) {
                        NexusAPI.getApi().getThreadFactory().runAsync(() -> {
                            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(p);
                            if (nexusPlayer == null) {
                                for (CachedPlayer cachedPlayer : NexusAPI.getApi().getPlayerManager().getCachedPlayers().values()) {
                                    if (cachedPlayer.getName().equalsIgnoreCase(p)) {
                                        try {
                                            nexusPlayer = NexusAPI.getApi().getPrimaryDatabase().get(NexusPlayer.class, "name", p).get(0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            
                            Tag tag = new Tag(nexusPlayer.getUniqueId(), gameInfo.getId() + "th", System.currentTimeMillis());
                            nexusPlayer.getTags().add(tag);
                            nexusPlayer.sendMessage(MsgType.INFO + "Unlocked the tag " + tag.getDisplayName());
                            NexusAPI.getApi().getPrimaryDatabase().push(nexusPlayer);
                        });
                    }
                }
            }
        });
        
        this.timer = new Timer(new NextGameTimerCallback(this)).run(10050L);
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
        
        if (plugin.restart()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("H1");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendPluginMessage(plugin.getNexusCore(), "BungeeCord", out.toByteArray());
            }
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (NexusAPI.getApi().getEnvironment() != Environment.DEVELOPMENT) {
                        MulticraftAPI.getInstance().restartServer(NexusAPI.getApi().getServerManager().getCurrentServer().getMulticraftId());
                    } else {
                        Bukkit.shutdown();
                    }
                }
            }.runTaskLater(plugin, 100L);
        } else {
            for (GamePlayer player : this.players.values()) {
                resetPlayer(Bukkit.getPlayer(player.getUniqueId()));
            }
            
            plugin.getLobby().fromGame(this);
        }
    }
    
    public void killPlayer(GamePlayer gamePlayer, DeathInfo deathInfo) {
        //TODO Handle the stat changes stuff differently in the future. 
        GameTeam oldTeam = gamePlayer.getTeam();
        gamePlayer.setTeam(GameTeam.SPECTATORS);
        recalculateVisibility();
        Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
        giveSpectatorItems(player);
        String strippedDeathMessage = ChatColor.stripColor(deathInfo.getDeathMessage());
        strippedDeathMessage = strippedDeathMessage.substring(3, strippedDeathMessage.length() - 1);
        this.gameInfo.getActions().add(new GameAction(System.currentTimeMillis(), "death", strippedDeathMessage));
        if (player != null) {
            resetPlayer(player);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setGameMode(GameTeam.SPECTATORS.getGameMode());
            player.spigot().setCollidesWithEntities(false);
        }
        
        gamePlayer.addDeathInfo(deathInfo);
        gamePlayer.setSpectatorByDeath(deathInfo.getType() != com.thenexusreborn.survivalgames.game.death.DeathType.LEAVE);
        KillerInfo killer = deathInfo.getKiller();
        gamePlayer.setDeathByMutation(killer != null && killer.isMutationKill());
        
        boolean vanished = deathInfo.getType() == com.thenexusreborn.survivalgames.game.death.DeathType.VANISH;
        NexusPlayer nexusPlayer = gamePlayer.getNexusPlayer();
        PlayerStats stats = nexusPlayer.getStats();
        int score = stats.getValue("sg_score").getAsInt();
        int lost = (int) Math.ceil(score / 10D);
        if (score - lost < 0) {
            lost = 0;
        }
    
        if (!vanished) {
            if (lost > 0) {
                stats.change("sg_score", lost, StatOperator.SUBTRACT);
            }
            stats.change("sg_games", 1, StatOperator.ADD);
            stats.change("sg_win_streak", 0, StatOperator.SET);
            stats.change("sg_deaths", 1, StatOperator.ADD);
            if (oldTeam == GameTeam.MUTATIONS) {
                gamePlayer.getNexusPlayer().getStats().change("sg_mutation_deaths", 1, StatOperator.ADD);
            }
        }
        
        boolean playerKiller = killer != null && killer.getType() == EntityType.PLAYER;
    
        boolean claimedFirstBlood = false;
        
        int scoreGain = 0, currentStreak = 0, personalBest = 0, xpGain = 0, creditGain = 0;
        Rank killerRank = null;
        boolean claimedScoreBounty = false, claimedCreditBounty = false;
        Bounty bounty = gamePlayer.getBounty();
        double scoreBounty = bounty.getAmount(Bounty.Type.SCORE);
        double creditBounty = bounty.getAmount(Bounty.Type.CREDIT);
        if (playerKiller) {
            GamePlayer killerPlayer = getPlayer(killer.getKiller());
            PlayerStats killerStats = killerPlayer.getNexusPlayer().getStats();
            killerRank = killerPlayer.getNexusPlayer().getRanks().get();
            scoreGain = lost;
    
            if (this.firstBlood == null) {
                this.firstBlood = killerPlayer;
                claimedFirstBlood = true;
            }
            
            if (claimedFirstBlood) {
                scoreGain = (int) (scoreGain * 1.25);
            }
            
            if (scoreBounty > 0) {
                scoreGain += scoreBounty;
                claimedScoreBounty = true;
                bounty.remove(Bounty.Type.SCORE);
            }

            killerStats.change("sg_score", scoreGain, StatOperator.ADD);
            
            killerPlayer.setKillStreak(killerPlayer.getKillStreak() + 1);
            currentStreak = killerPlayer.getKillStreak();
            killerPlayer.setKills(killerPlayer.getKills() + 1);
            personalBest = killerStats.getValue("sg_highest_kill_streak").getAsInt();
            
            if (currentStreak > personalBest) {
                killerStats.change("sg_highest_kill_streak", currentStreak, StatOperator.SET);
            }
            
            if (getSettings().isGiveXp()) {
                xpGain = 2;
                if (getSettings().isMultiplier()) {
                    xpGain *= killerRank.getMultiplier();
                }
                killerStats.change("xp", xpGain, StatOperator.ADD);
            }
            
            if (getSettings().isGiveCredits()) {
                creditGain = 2;
                if (getSettings().isMultiplier()) {
                    creditGain *= killerRank.getMultiplier();
                }

                if (creditBounty > 0) {
                    creditGain += creditBounty;
                    bounty.remove(Bounty.Type.CREDIT);
                    claimedCreditBounty = true;
                }

                killerStats.change("credits", creditGain, StatOperator.ADD);
            }
            
            killerStats.change("sg_kills", 1, StatOperator.ADD);
            if (killer.isMutationKill()) {
                killerStats.change("sg_mutation_kills", 1, StatOperator.ADD);
                removeMutation(killerPlayer.getMutation());
                killerPlayer.sendMessage(killerPlayer.getTeam().getLeaveMessage());
                killerPlayer.setTeam(GameTeam.TRIBUTES);
                Bukkit.getPlayer(killerPlayer.getUniqueId()).setGameMode(GameTeam.TRIBUTES.getGameMode());
                killerPlayer.sendMessage(killerPlayer.getTeam().getJoinMessage());
            }
        }
        
        List<UUID> assistors = gamePlayer.getDamageInfo().getDamagers();
        if (!assistors.isEmpty()) {
            for (UUID damager : assistors) {
                GamePlayer assisterPlayer = getPlayer(damager);
                assisterPlayer.setAssists(assisterPlayer.getAssists() + 1);
                assisterPlayer.getNexusPlayer().getStats().change("sg_assists", 1, StatOperator.ADD);
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
                    gp.sendMessage("&6&l>> &cYour killer died without a killer, you have been set back as a spectator.");
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
                    gp.sendMessage("&6&l>> &cYour target died, your  new target is &a" + getPlayer(killer.getKiller()).getNexusPlayer().getName());
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
            if (state == INGAME || state == INGAME_GRACEPERIOD) {
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
                sendMessage("&6&l>> " + killerPlayer.getNexusPlayer().getColoredName() + " &ahas taken revenge and is back in the game!");
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
            String multiplierMessage = killerRank.getColor() + "&l * x" + MCUtils.formatNumber(multiplier) + " " + killerRank.getPrefix() + " Bonus";
            
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
        }
        
        gamePlayer.sendMessage("&4&l>> &cYou lost " + lost + " Points for dying!");
        sendMessage("&6&l>> " + oldTeam.getRemainColor() + "&l" + oldTeamRemaining + " " + oldTeam.name().toLowerCase() + " remain.");
        if (claimedFirstBlood) {
            sendMessage("&6&l>> &c&l" + firstBlood.getNexusPlayer().getName().toUpperCase() + " CLAIMED FIRST BLOOD!");
        }
    
        if (killerPlayer != null) {
            String killerName = killerPlayer.getNexusPlayer().getColoredName();
            String killerHealth = NumberHelper.formatNumber(killer.getHealth());
            gamePlayer.sendMessage("&4&l>> &cYour killer &8(" + killerName + "&8) &chad &4" + killerHealth + " HP &cremaining!");
        }
        
        sendMessage(deathInfo.getDeathMessage());
        gamePlayer.sendMessage(GameTeam.SPECTATORS.getJoinMessage());

        if (claimedScoreBounty) {
            sendMessage("&6&l>> " + killerPlayer.getNexusPlayer().getColoredName() + " &6&lhas claimed the &b&l" + scoreBounty + " Score &6&lbounty on " + nexusPlayer.getColoredName());
        }

        if (claimedCreditBounty && settings.isGiveCredits()) {
            sendMessage("&6&l>> " + killerPlayer.getNexusPlayer().getColoredName() + " &6&lhas claimed the &b&l" + scoreBounty + " Credit &6&lbounty on " + nexusPlayer.getColoredName());
        }

        playSound(oldTeam.getDeathSound());
    
        new BukkitRunnable() {
            public void run() {
                if (state == ENDING || state == ENDED) {
                    cancel();
                    return;
                }
                if (state == INGAME || state == INGAME_GRACEPERIOD || state == DEATHMATCH || state == INGAME_DEATHMATCH || state == WARMUP) {
                    checkGameEnd();
                }
            }
        }.runTaskLater(plugin, 1L);
    }
    
    public void giveSpectatorItems(Player p) {
        GamePlayer gamePlayer = getPlayer(p.getUniqueId());
        ItemStack tributesBook = ItemBuilder.start(Material.ENCHANTED_BOOK).displayName("&a&lTributes &7&o(Right Click)").build();
        ItemStack mutationsBook = ItemBuilder.start(Material.ENCHANTED_BOOK).displayName("&d&lMutations &7&o(Right Click)").build();
        ItemStack spectatorsBook = ItemBuilder.start(Material.ENCHANTED_BOOK).displayName("&c&lSpectators &7&o(Right Click)").build();
        String mutateName;
        if (!getSettings().isAllowMutations()) {
            mutateName = "&cMutations Disabled";
        } else if (!(getState() == INGAME || getState() == INGAME_DEATHMATCH)) {
            mutateName = "&cCannot mutate.";
        } else if (gamePlayer.hasMutated()) {
            mutateName = "&cCan't mutate again.";
        } else {
            if (!(gamePlayer.killedByPlayer()) || gamePlayer.deathByMutation()) {
                mutateName = "&cCannot mutate.";
            } else {
                GamePlayer killer = getPlayer(gamePlayer.getKiller());
                String passes;
                if (getSettings().isUnlimitedPasses()) {
                    passes = "Unlimited";
                } else {
                    passes = gamePlayer.getNexusPlayer().getStats().getValue("sg_mutation_passes").getAsInt() + "";
                }
                mutateName = "&c&lTAKE REVENGE   &eTarget: " + killer.getNexusPlayer().getColoredName() + "   &ePasses: &b" + passes;
            }
        }
        ItemStack mutateItem = ItemBuilder.start(Material.ROTTEN_FLESH).displayName(mutateName).build();
        ItemStack compass = ItemBuilder.start(Material.COMPASS).displayName("&fPlayer Tracker").build();
        ItemStack tpCenter = ItemBuilder.start(Material.WATCH).displayName("&e&lTeleport to Map Center &7&o(Right Click)").build();
        ItemStack hubItem = ItemBuilder.start(Material.WOOD_DOOR).displayName("&e&lReturn to Hub &7(Right Click)").build();
        PlayerInventory inv = p.getInventory();
        inv.setItem(0, tributesBook);
        inv.setItem(1, mutationsBook);
        inv.setItem(2, spectatorsBook);
        inv.setItem(5, mutateItem);
        inv.setItem(6, compass);
        inv.setItem(7, tpCenter);
        inv.setItem(8, hubItem);
    }
    
    public void checkGameEnd() {
        if (this.state == INGAME || this.state == INGAME_DEATHMATCH || this.state == DEATHMATCH) {
            int totalTributes = 0;
            for (GamePlayer player : this.players.values()) {
                if (player.getTeam() == GameTeam.TRIBUTES) {
                    totalTributes++;
                }
            }
            
            if (totalTributes <= 1) {
                this.end();
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
        
        this.timer = new Timer(new DeathmatchPlayingCallback(this)).run(settings.getDeathmatchTimerLength() * 60000L + 50);
    }
    
    public boolean isLootedChest(Block block) {
        return this.lootedChests.contains(block.getLocation());
    }
    
    public void addLootedChest(Location location) {
        this.lootedChests.add(location);
    }
    
    public List<Location> getLootedChests() {
        return this.lootedChests;
    }
    
    public void endGracePeriod() {
        setState(INGAME);
        sendMessage("&6&l>> &eThe &c&lGRACE PERIOD &ehas ended.");
        this.graceperiodTimer.cancel();
        this.graceperiodTimer = null;
    }
    
    public GameInfo getGameInfo() {
        return gameInfo;
    }
    
    public void addMutation(Mutation mutation) {
        GamePlayer gamePlayer = getPlayer(mutation.getPlayer());
        NexusPlayer nexusPlayer = gamePlayer.getNexusPlayer();
        sendMessage("&6&l>> " + nexusPlayer.getColoredName() + " &6has &lMUTATED &6as a(n) &l" + mutation.getType().getDisplayName() + " &6and seeks revenge on &a" + Bukkit.getPlayer(mutation.getTarget()).getName() + "&6!");
        
        MapSpawn spawn = gameMap.getSpawns().get(new Random().nextInt(gameMap.getSpawns().size()));
        Location location = spawn.toLocation(this.gameMap.getWorld());
        Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
        
        gamePlayer.sendMessage(gamePlayer.getTeam().getLeaveMessage());
        gamePlayer.setTeam(GameTeam.MUTATIONS);
        gamePlayer.sendMessage(gamePlayer.getTeam().getJoinMessage());
        DisguiseAPI.disguiseEntity(player, new MobDisguise(mutation.getType().getDisguiseType()));
        
        gamePlayer.setMutated(true);
        teleportMutation(player, this.gameMap.getCenter().toLocation(gameMap.getWorld()), location);
        recalculateVisibility();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setSaturation(20);
        
        MutationType type = mutation.getType();
        PlayerInventory inv = player.getInventory();
        inv.setItem(0, type.getWeapon());
        inv.setItem(1, Items.PLAYER_TRACKER.getItemStack());
        for (MutationItem item : type.getItems()) {
            inv.setItem(1 + item.slotOffset(), item.itemStack());
        }
        ArmorType armorType = type.getArmorType();
        inv.setHelmet(new ItemStack(armorType.getHelmet()));
        inv.setChestplate(new ItemStack(armorType.getChestplate()));
        inv.setLeggings(new ItemStack(armorType.getLeggings()));
        inv.setBoots(new ItemStack(armorType.getBoots()));
        player.setMaxHealth(type.getHealth());
        for (MutationEffect effect : type.getEffects()) {
            player.addPotionEffect(new PotionEffect(effect.getPotionType(), Integer.MAX_VALUE, effect.getAmplifier(), false, false));
        }
    }
    
    public void removeMutation(Mutation mutation) {
        Player player = Bukkit.getPlayer(mutation.getPlayer());
        DisguiseAPI.undisguiseToAll(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setExp(0);
        player.setLevel(0);
        player.spigot().setCollidesWithEntities(true);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
    
    public Map<Location, UUID> getSuicideLocations() {
        return suicideLocations;
    }
    
    public GamePlayer getPlayer(String name) {
        for (GamePlayer gamePlayer : new ArrayList<>(this.players.values())) {
            if (gamePlayer.getNexusPlayer().getName().equalsIgnoreCase(name)) {
                return gamePlayer;
            }
        }
        return null;
    }
}