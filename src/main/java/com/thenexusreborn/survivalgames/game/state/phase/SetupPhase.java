package com.thenexusreborn.survivalgames.game.state.phase;

import com.stardevllc.starcore.utils.Cuboid;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.OldGameState;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.PhaseStatus;
import com.thenexusreborn.survivalgames.scoreboard.GameTablistHandler;
import com.thenexusreborn.survivalgames.scoreboard.game.GameBoard;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;

public class SetupPhase extends GamePhase {
    public SetupPhase(Game game) {
        super(game, "setup");
    }

    @Override
    public void beginphase() {
        if (checkPlayerCount()) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Game.getPlugin(), () -> {
            SGMap gameMap = game.getGameMap();
            SurvivalGames plugin = Game.getPlugin();
            GameSettings settings = game.getSettings();

            setStatus(Status.SETTING_SCOREBOARDS);
            for (GamePlayer player : game.getPlayers().values()) {
                NexusScoreboard scoreboard = player.getScoreboard();
                scoreboard.setView(new GameBoard(scoreboard, plugin));
                scoreboard.setTablistHandler(new GameTablistHandler(scoreboard, plugin));
            }

            setStatus(Status.DOWNLOADING_MAP);
            if (!gameMap.download(plugin)) {
                game.handleError("There was an error downloading the map.");
                return;
            }

            setStatus(Status.UNZIPPING_MAP);
            if (!gameMap.unzip(plugin)) {
                game.handleError("There was an error extracting the map files.");
                return;
            }

            setStatus(Status.COPYING_MAP);
            if (!gameMap.copyFolder(plugin, game.getServer().getName() + "-", false)) {
                game.handleError("There was an error copying the map files.");
                return;
            }

            Bukkit.getScheduler().runTask(Game.getPlugin(), () -> {
                if (checkPlayerCount()) {
                    return;
                }
                
                setStatus(Status.LOADING_MAP);
                if (!gameMap.load(plugin)) {
                    game.handleError("There was an error loading the world.");
                    return;
                }
                
                if (checkPlayerCount()) {
                    return;
                }
                setStatus(Status.CALCULATE_DM_AREA);
                int radius = gameMap.getDeathmatchBorderDistance();
                Location center = gameMap.getCenter().toLocation(gameMap.getWorld());
                Location corner1 = center.clone();
                corner1.add(radius, radius, radius);
                Location corner2 = center.clone();
                corner2.subtract(radius, radius, radius);
                gameMap.setDeathmatchArea(new Cuboid(corner1, corner2));

                try {
                    if (checkPlayerCount()) {
                        return;
                    }
                    setStatus(Status.SETUP_SPAWNS);
                    for (int i = 0; i < gameMap.getSpawns().size(); i++) {
                        game.setSpawn(i, null);
                    }

                    setStatus(Status.SET_GAMERULES);
                    gameMap.getWorld().setGameRuleValue("naturalRegeneration", "" + settings.isRegeneration());
                    gameMap.getWorld().setGameRuleValue("doDaylightCycle", "" + settings.isTimeProgression());
                    gameMap.getWorld().setGameRuleValue("doWeatherCycle", "" + settings.isWeatherProgression());
                    gameMap.getWorld().setGameRuleValue("doMobSpawning", "false");
                    gameMap.getWorld().setGameRuleValue("announceAdvancements", "false");
                    gameMap.getWorld().setGameRuleValue("doFireTick", "false");
                    gameMap.getWorld().setGameRuleValue("keepInventory", "false");
                    gameMap.getWorld().setDifficulty(Difficulty.EASY);
                    game.getServer().getLobby().resetLobby();
                    setStatus(PhaseStatus.COMPLETE);
                    checkPlayerCount();
                    game.setState(OldGameState.SETUP_COMPLETE);
                } catch (Exception e) {
                    e.printStackTrace();
                    game.handleError("There was an error setting up the world.");
                }
            });
        });
    }

    public enum Status implements PhaseStatus {
        SETTING_SCOREBOARDS,
        DOWNLOADING_MAP,
        UNZIPPING_MAP,
        COPYING_MAP,
        LOADING_MAP,
        CALCULATE_DM_AREA,
        SETUP_SPAWNS,
        SET_GAMERULES,
    }
}
