package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.util.Operator;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.helper.*;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.death.*;
import com.thenexusreborn.survivalgames.game.timer.*;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.scoreboard.*;
import com.thenexusreborn.survivalgames.settings.*;
import com.thenexusreborn.survivalgames.tournament.Tournament;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

import static com.thenexusreborn.survivalgames.game.GameState.*;

@SuppressWarnings({"DuplicatedCode"})
public class Game {
    private static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    private static Mode mode = Mode.MANUAL;
    
    private GameMap gameMap;
    private GameSettings settings;
    private Map<UUID, GamePlayer> players = new HashMap<>();
    private Map<Integer, UUID> spawns = new HashMap<>();
    private GameState state = UNDEFINED;
    private Timer timer, graceperiodTimer, restockTimer;
    private List<Location> lootedChests = new ArrayList<>();
    
    public Game(GameMap gameMap, GameSettings settings, Collection<SpigotNexusPlayer> players, List<UUID> spectatingPlayers) {
        this.gameMap = gameMap;
        this.settings = settings;
        for (SpigotNexusPlayer player : players) {
            GamePlayer gamePlayer = new GamePlayer(player);
            if (spectatingPlayers.contains(player.getUniqueId())) {
                gamePlayer.setTeam(GameTeam.SPECTATORS);
            }
            player.setActionBar(new GameActionBar(plugin, gamePlayer));
            this.players.put(gamePlayer.getUniqueId(), gamePlayer);
        }
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
    
    public static Mode getMode() {
        return mode;
    }
    
    public static void setMode(Mode mode) {
        Game.mode = mode;
        if (mode == Mode.MANUAL) {
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
    
    public void addPlayer(SpigotNexusPlayer nexusPlayer) {
        GamePlayer gamePlayer = new GamePlayer(nexusPlayer);
        gamePlayer.setTeam(GameTeam.SPECTATORS);
        gamePlayer.sendMessage(GameTeam.SPECTATORS.getJoinMessage());
        teleportSpectator(nexusPlayer.getPlayer(), this.gameMap.getCenter().toLocation(this.gameMap.getWorld()));
        this.players.put(nexusPlayer.getUniqueId(), gamePlayer);
        sendMessage("&a&l>> &b" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &ejoined as a spectator.");
        nexusPlayer.getScoreboard().setView(new DebugGameBoard(nexusPlayer.getScoreboard(), plugin));
        recalculateVisibiltiy();
    }
    
    public void removePlayer(SpigotNexusPlayer nexusPlayer) {
        GamePlayer gamePlayer = this.players.get(nexusPlayer.getUniqueId());
        EnumSet<GameState> ignoreStates = EnumSet.of(UNDEFINED, SETTING_UP, SETUP_COMPLETE, ASSIGN_TEAMS, TEAMS_ASSIGNED, TELEPORT_START, TELEPORT_START_DONE, ERROR, ENDING, ENDED);
        if (!ignoreStates.contains(this.state)) {
            if (gamePlayer.getTeam() == GameTeam.TRIBUTES || gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                killPlayer(nexusPlayer.getUniqueId(), new DeathInfoSuicide(nexusPlayer.getUniqueId()));
            }
        }
        
        this.players.remove(nexusPlayer.getUniqueId());
        sendMessage("&c&l<< &b" + nexusPlayer.getRank().getColor() + nexusPlayer.getName() + " &eleft.");
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
                Position spawnPosition = this.gameMap.getSpawns().get(entry.getKey());
                Location spawn = spawnPosition.toLocation(gameMap.getWorld());
                teleportTribute(player, mapSpawn, spawn);
                this.spawns.put(entry.getKey(), player.getUniqueId());
                spawns.remove(index);
            }
        }
    }
    
    public void teleportTribute(Player tribute, Location mapSpawn, Location spawn) {
        spawn.setX(spawn.getX() + 0.5);
        spawn.setY(spawn.getY() + 2.0);
        spawn.setZ(spawn.getZ() + 0.5);
        spawn.setPitch(0);
        spawn.setYaw(SGUtils.getAngle(spawn.toVector(), mapSpawn.toVector()));
        tribute.teleport(spawn);
        new BukkitRunnable() {
            @Override
            public void run() {
                tribute.setGameMode(GameTeam.TRIBUTES.getGameMode());
            }
        }.runTaskLater(plugin, 1L);
    }
    
    public void teleportSpectator(Player spectator, Location mapSpawn) {
        spectator.teleport(mapSpawn);
        spectator.setGameMode(GameTeam.SPECTATORS.getGameMode());
        spectator.setFoodLevel(20);
        spectator.setSaturation(20);
        spectator.getInventory().clear();
        spectator.getInventory().setArmorContents(null);
        spectator.setAllowFlight(true);
        spectator.setFlying(true);
    }
    
    public void recalculateVisibiltiy() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = getPlayer(player.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                GamePlayer otherGamePlayer = getPlayer(other.getUniqueId());
                if (gamePlayer.getTeam() == GameTeam.TRIBUTES && otherGamePlayer.getTeam() == GameTeam.TRIBUTES) {
                    player.showPlayer(other);
                    other.showPlayer(player);
                } else if (gamePlayer.getTeam() == GameTeam.SPECTATORS && otherGamePlayer.getTeam() == GameTeam.TRIBUTES) {
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
            this.state = TELEPORT_START;
            
            List<UUID> tributes = new LinkedList<>(), spectators = new LinkedList<>();
            for (GamePlayer player : this.players.values()) {
                Player p = player.getNexusPlayer().getPlayer();
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                p.setFlying(false);
                p.setAllowFlight(false);
                p.setSaturation(5.0F);
                if (player.getTeam() == GameTeam.TRIBUTES) {
                    tributes.add(player.getUniqueId());
                } else if (player.getTeam() == GameTeam.SPECTATORS) {
                    spectators.add(player.getUniqueId());
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
            
            recalculateVisibiltiy();
            
            this.state = TELEPORT_START_DONE;
        } catch (Exception e) {
            e.printStackTrace();
            handleError("There was an error teleporting players to their starting positions.");
        }
    }
    
    public void assignStartingTeams() {
        try {
            this.state = ASSIGN_TEAMS;
            int totalTributes = 0;
            UUID uuid;
            Queue<UUID> tributes = new LinkedList<>(), spectators = new LinkedList<>();
            while ((uuid = SurvivalGames.PLAYER_QUEUE.poll()) != null) {
                GamePlayer player = this.players.get(uuid);
                if (player.getTeam() != null) {
                    if (player.getTeam() == GameTeam.SPECTATORS || player.getTeam() == GameTeam.HIDDEN_STAFF) {
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
            
            this.state = TEAMS_ASSIGNED;
        } catch (Exception e) {
            e.printStackTrace();
            handleError("There was an error assiging teams.");
        }
    }
    
    public void setup() {
        this.state = SETTING_UP;
        
        for (GamePlayer player : this.players.values()) {
            player.getNexusPlayer().getScoreboard().setView(new GameScoreboard(player.getNexusPlayer().getScoreboard(), plugin));
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
                            state = SETUP_COMPLETE;
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
        this.state = ERROR;
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
        this.state = WARMUP;
        this.timer = new Timer(new CountdownTimerCallback(this)).run((settings.getWarmupLength() * 1000L) + 50L);
    }
    
    public void startGame() {
        this.timer = new Timer(new GameTimerCallback(this)).run(((settings.getGameLength() * 60000L) + 50));
        if (this.settings.isGracePeriod()) {
            this.graceperiodTimer = new Timer(new GraceperiodCountdownCallback(this)).run((settings.getGracePeriodLength() * 1000L) + 50L);
            this.state = INGAME_GRACEPERIOD;
        } else {
            this.state = INGAME;
        }
        this.restockTimer = new Timer(new RestockTimerCallback(this)).run(600050L);
        sendMessage("&6&l>> &a&lMAY THE ODDS BE EVER IN YOUR FAVOR.");
    }
    
    public void restockChests() {
        this.lootedChests.clear();
        this.restockTimer.cancel();
        this.restockTimer = null;
        if (state == INGAME) {
            int secondsLeft = this.timer.getSecondsLeft();
            int minutesLeft = secondsLeft / 60;
            if (minutesLeft > 10) {
                this.restockTimer = new Timer(new RestockTimerCallback(this)).run(600050L);
            }
        }
    }
    
    public void warmupComplete() {
        this.state = WARMUP_DONE;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public boolean hasPlayer(UUID uniqueId) {
        return this.players.containsKey(uniqueId);
    }
    
    public void playSound(Sound sound) {
        for (GamePlayer player : this.players.values()) {
            if (player.getNexusPlayer().getPlayer() != null) {
                player.getNexusPlayer().getPlayer().playSound(player.getNexusPlayer().getPlayer().getLocation(), sound, 1, 1);
            }
        }
    }
    
    public void teleportDeathmatch() {
        try {
            this.state = TELEPORT_DEATHMATCH;
            sendMessage("&6&l>> &e&LPREPARE FOR DEATHMATCH...");
            List<UUID> tributes = new LinkedList<>(), spectators = new LinkedList<>();
            for (GamePlayer player : this.players.values()) {
                if (player.getTeam() == GameTeam.TRIBUTES) {
                    tributes.add(player.getUniqueId());
                } else if (player.getTeam() == GameTeam.SPECTATORS) {
                    spectators.add(player.getUniqueId());
                }
            }
            resetSpawns();
            Location mapSpawn = gameMap.getCenter().toLocation(gameMap.getWorld());
            teleportTributes(tributes, mapSpawn);
            teleportSpectators(spectators, mapSpawn);
            
            recalculateVisibiltiy();
            this.state = TELEPORT_DEATHMATCH_DONE;
        } catch (Exception e) {
            e.printStackTrace();
            handleError("There was an error teleporting tributes to the deathmatch.");
        }
    }
    
    public void startDeathmatchWarmup() {
        this.state = DEATHMATCH_WARMUP;
        
        if (this.timer != null) {
            timer.cancel();
        }
        
        playSound(Sound.ENDERDRAGON_GROWL);
        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                player.getNexusPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
            }
        }
        
        this.timer = new Timer(new DeathmatchCountdownCallback(this)).run(10050);
    }
    
    public void startDeathmatch() {
        this.state = DEATHMATCH;
        
        if (this.timer != null) {
            timer.cancel();
        }
        
        for (GamePlayer player : this.players.values()) {
            if (player.getTeam() == GameTeam.TRIBUTES) {
                player.getNexusPlayer().getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }
        
        sendMessage("&6&l>> &a&lLAST PLAYER STANDING CLAIMS VICTORY!");
        sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED");
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
        this.state = DEATHMATCH_WARMUP_DONE;
    }
    
    public void end() {
        this.state = ENDING;
        plugin.incrementGamesPlayed();
        if (this.timer != null) {
            timer.cancel();
            this.timer = null;
        }
        
        if (this.graceperiodTimer != null) {
            graceperiodTimer.cancel();
            this.graceperiodTimer = null;
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
                if (winner == null) {
                    winner = player;
                } else {
                    winner = null; //More than one winner
                    break;
                }
            }
        }

//        List<GamePlayer> winners = new ArrayList<>();
//        for (GamePlayer player : this.players.values()) {
//            if (player.getTeam() == GameTeam.TRIBUTES) {
//                winners.add(player);
//                if (plugin.getTournament() != null && plugin.getTournament().isActive()) {
//                    plugin.getTournament().incrementScore(player.getUniqueId(), plugin.getTournament().getPointsPerWin());
//                    player.sendMessage("&2&l>> &a+" + plugin.getTournament().getPointsPerWin() + " Points!");
//                }
//            }
//        }
        
        String winnerName;
//        if (winners.size() > 0) {
//            if (winners.size() == 1) {
//                winnerName = new StringBuilder(winners.get(0).getNexusPlayer().getDisplayName());
//            } else {
//                Iterator<GamePlayer> iterator = winners.iterator();
//                while (iterator.hasNext()) {
//                    GamePlayer winner = iterator.next();
//                    winnerName.append(winner.getNexusPlayer().getDisplayName());
//                    if (iterator.hasNext()) {
//                        winnerName.append("&f, ");
//                    }
//                }
//            }
//        } else {
//            winnerName = new StringBuilder("&f&lNo one");
//        }
        
        if (winner != null) {
            winnerName = winner.getNexusPlayer().getDisplayName();
            
        } else {
            winnerName = "&f&lNo one";
        }
        
        sendMessage("&6&l>> " + winnerName + " &a&lhas won Survival Games!");
        
        if (winner != null) {
            int winGain = 50;
            double currentScore = winner.getNexusPlayer().getStatValue("sg_score");
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
            winner.getNexusPlayer().changeStat("sg_score", winGain, Operator.ADD);
            winner.sendMessage("&2&l>> &a+" + winGain + " Score!");
            double multiplier = winner.getNexusPlayer().getRank().getMultiplier();
            Rank rank = winner.getNexusPlayer().getRank();
            String multiplierMessage = rank.getColor() + "&l * x" + MCUtils.formatNumber(multiplier) + " " + rank.getPrefix() + " Bonus";
            if (settings.isGiveXp()) {
                double xp = 10;
                xp *= multiplier;
                winner.getNexusPlayer().changeStat("xp", xp, Operator.ADD);
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
                winner.getNexusPlayer().changeStat("credits", credits, Operator.ADD);
                String baseMessage = "&2&l>> &a&l+" + MCUtils.formatNumber(credits) + " &3&lCREDITS&a&l!";
                if (multiplier > 1) {
                    winner.sendMessage(baseMessage + multiplierMessage);
                } else {
                    winner.sendMessage(baseMessage);
                }
            }
            
        }
        
        this.timer = new Timer(new NextGameTimerCallback(this)).run(10000L);
    }
    
    public void resetPlayer(Player player) {
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        player.spigot().setCollidesWithEntities(true);
    }
    
    public void nextGame() {
        this.state = ENDED;
        
        for (GamePlayer player : this.players.values()) {
            resetPlayer(player.getNexusPlayer().getPlayer());
        }
        
        plugin.getLobby().fromGame(this);
    }
    
    public void killPlayer(UUID uniqueId, DeathInfo deathInfo) {
        GamePlayer gamePlayer = getPlayer(uniqueId);
        if (gamePlayer == null) {
            return;
        }
        Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        gamePlayer.setDeathInfo(deathInfo);
        gamePlayer.setTrackerInfo(null);
        GameTeam oldTeam = gamePlayer.getTeam();
        
        int score = (int) gamePlayer.getNexusPlayer().getStatValue("sg_score");
        int lost = (int) Math.ceil(score / 8D);
        
        gamePlayer.getNexusPlayer().changeStat("sg_score", lost, Operator.SUBTRACT);
        gamePlayer.sendMessage("&4&l>> &cYou lost " + lost + " Score for dying.");
        
        gamePlayer.sendMessage(GameTeam.TRIBUTES.getLeaveMessage());
        GamePlayer killer = null;
        String killerName = null;
        boolean mutationKill = false;
        boolean sendDeathMessage = true;
        double killerHealth = 0;
        GameTeam newTeam = GameTeam.SPECTATORS;
        if (deathInfo instanceof DeathInfoPlayerKill) {
            DeathInfoPlayerKill playerDeath = (DeathInfoPlayerKill) deathInfo;
            if (settings.getColorMode() == ColorMode.GAME_TEAM) {
                GameTeam killerTeam = getPlayer(playerDeath.getKiller()).getTeam();
                if (killerTeam == GameTeam.TRIBUTES) {
                    killerName += "&a";
                } else if (killerTeam == GameTeam.MUTATIONS) {
                    killerName += "&d";
                } else if (killerTeam == GameTeam.SPECTATORS) {
                    killerName += "&c";
                }
            } else {
                killerName = getPlayer(playerDeath.getKiller()).getNexusPlayer().getRank().getColor();
            }
            killer = getPlayer(playerDeath.getKiller());
            killerHealth = playerDeath.getKillerHealth();
        } else if (deathInfo instanceof DeathInfoKilledSuicide) {
            DeathInfoKilledSuicide death = (DeathInfoKilledSuicide) deathInfo;
            if (settings.getColorMode() == ColorMode.GAME_TEAM) {
                if (getPlayer(death.getKiller()).getTeam() == GameTeam.TRIBUTES) {
                    killerName += "&a";
                } else {
                    killerName += "&d";
                }
            } else {
                killerName = getPlayer(death.getKiller()).getNexusPlayer().getRank().getColor();
            }
            killer = getPlayer(death.getKiller());
            Player killerPlayer = Bukkit.getPlayer(killer.getUniqueId());
            killerPlayer.setMaxHealth(settings.getMaxHealth());
            killerHealth = 20; //TODO Add better tracking of this for this type of death
        } else if (deathInfo instanceof DeathInfoProjectile) {
            DeathInfoProjectile death = (DeathInfoProjectile) deathInfo;
            if (death.getShooter() instanceof Player) {
                if (settings.getColorMode() == ColorMode.GAME_TEAM) {
                    if (getPlayer(death.getShooter().getUniqueId()).getTeam() == GameTeam.TRIBUTES) {
                        killerName += "&a";
                    } else {
                        killerName += "&d";
                    }
                } else {
                    killerName = getPlayer(death.getShooter().getUniqueId()).getNexusPlayer().getRank().getColor();
                }
                killer = getPlayer(death.getShooter().getUniqueId());
                newTeam = GameTeam.SPECTATORS;
                killerHealth = death.getKillerHealth();
            }
        }
        
        if (deathInfo.getType() != DeathType.LEAVE) {
            gamePlayer.setSpectatorByDeath(true);
            resetPlayer(gamePlayer.getNexusPlayer().getPlayer());
            player.setGameMode(newTeam.getGameMode());
            player.setAllowFlight(true);
            player.setFlying(true);
            player.spigot().setCollidesWithEntities(false);
            gamePlayer.sendMessage(newTeam.getJoinMessage());
        }
        gamePlayer.setTeam(newTeam);
        
        if (settings.isSounds()) {
            playSound(Sound.WITHER_SPAWN);
        }
        
        recalculateVisibiltiy();
        
        if (killer != null) {
            killerName += killer.getNexusPlayer().getName();
            gamePlayer.sendMessage("&4&l>> &cYour killer &8(" + killerName + "&8) &chad &4" + NumberHelper.formatNumber(killerHealth) + " HP &cremaining!");
        }
        
        int totalTributes = 0;
        if (oldTeam == GameTeam.TRIBUTES) {
            for (GamePlayer gp : this.players.values()) {
                if (gp.getTeam() == GameTeam.TRIBUTES) {
                    totalTributes++;
                }
            }
            sendMessage("&6&l>> &c&l" + totalTributes + " tributes remain.");
        }
        
        if (sendDeathMessage) {
            this.sendMessage(deathInfo.getDeathMessage(this));
        }
        
        if (totalTributes <= settings.getDeathmatchThreshold()) {
            if (this.state == INGAME || this.state == INGAME_GRACEPERIOD) {
                if (totalTributes > 1) {
                    if (mode == Mode.AUTOMATIC) {
                        this.startDeathmatchTimer();
                    } else {
                        sendMessage("&eTribute count reached or went below the deathmatch threashold, but was not automatically started due to being in manual mode.");
                    }
                }
            }
        }
        
        if (oldTeam == GameTeam.TRIBUTES) {
            Tournament tournament = plugin.getTournament();
            if (killer != null) {
                if (tournament != null && tournament.isActive()) {
                    tournament.incrementScore(killer.getUniqueId(), tournament.getPointsPerKill());
                    killer.sendMessage("&2&l>> &a+" + tournament.getPointsPerKill() + " Points!");
                }
            }
            if (tournament != null && tournament.isActive()) {
                for (GamePlayer p : this.players.values()) {
                    if (p.getTeam() == GameTeam.TRIBUTES) {
                        tournament.incrementScore(p.getUniqueId(), tournament.getPointsPerSurvival());
                        p.sendMessage("&2&l>> &a+" + tournament.getPointsPerSurvival() + " Points!");
                    }
                }
            }
        }
        
        if (killer != null) {
            killer.getNexusPlayer().changeStat("sg_score", lost, Operator.ADD);
            killer.sendMessage("&2&l>> &a+" + lost + " Score!");
            double multiplier = killer.getNexusPlayer().getRank().getMultiplier();
            Rank rank = killer.getNexusPlayer().getRank();
            String multiplierMessage = rank.getColor() + "&l * x" + MCUtils.formatNumber(multiplier) + " " + rank.getPrefix() + " Bonus";
            if (settings.isGiveXp()) {
                double xp = 2;
                xp *= multiplier;
                killer.getNexusPlayer().changeStat("xp", xp, Operator.ADD);
                String baseMessage = "&2&l>> &a&l+" + MCUtils.formatNumber(xp) + " &2&lXP&a&l!";
                if (multiplier > 1) {
                    killer.sendMessage(baseMessage + multiplierMessage);
                } else {
                    killer.sendMessage(baseMessage);
                }
            }
            
            if (settings.isGiveCredits()) {
                double credits = 2;
                credits *= multiplier;
                killer.getNexusPlayer().changeStat("credits", credits, Operator.ADD);
                String baseMessage = "&2&l>> &a&l+" + MCUtils.formatNumber(credits) + " &3&lCREDITS&a&l!";
                if (multiplier > 1) {
                    killer.sendMessage(baseMessage + multiplierMessage);
                } else {
                    killer.sendMessage(baseMessage);
                }
            }
        }
        
        new BukkitRunnable() {
            public void run() {
                if (state == ENDING || state == ENDED) {
                    cancel();
                    return;
                }
                if (state == INGAME || state == INGAME_GRACEPERIOD || state == DEATHMATCH || state == INGAME_DEATHMATCH) {
                    checkGameEnd();
                }
            }
        }.runTaskLater(plugin, 1L);
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
        for (GamePlayer player : this.players.values()) {
            if (player.getUniqueId().toString().equalsIgnoreCase(uniqueId.toString())) {
                return player;
            }
        }
        
        return null;
    }
    
    public void startDeathmatchTimer() {
        this.state = INGAME_DEATHMATCH;
        if (this.timer != null) {
            timer.cancel();
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
        this.state = INGAME;
        sendMessage("&6&l>> &eThe &c&lGRACE PERIOD &ehas ended.");
        this.graceperiodTimer.cancel();
        this.graceperiodTimer = null;
    }
    
    
}
