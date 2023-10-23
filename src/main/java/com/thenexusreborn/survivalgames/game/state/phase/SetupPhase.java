package com.thenexusreborn.survivalgames.game.state.phase;

import com.thenexusreborn.nexuscore.util.region.Cuboid;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.PhaseStatus;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.scoreboard.game.GameBoard;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;

public class SetupPhase extends GamePhase {
    public SetupPhase(Game game) {
        super(game, "Setup");
    }

    @Override
    public void beginphase() {
        Bukkit.getScheduler().runTaskAsynchronously(Game.getPlugin(), () -> {
            GameMap gameMap = game.getGameMap();
            SurvivalGames plugin = Game.getPlugin();
            GameSettings settings = game.getSettings();

            setStatus(Status.SETTING_SCOREBOARDS);
            for (GamePlayer player : game.getPlayers().values()) {
                player.getScoreboard().setView(new GameBoard(player.getScoreboard(), plugin));
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
            if (!gameMap.copyFolder(plugin, false)) {
                game.handleError("There was an error copying the map files.");
                return;
            }

            Bukkit.getScheduler().runTask(Game.getPlugin(), () -> {
                setStatus(Status.LOADING_MAP);
                if (!gameMap.load(plugin)) {
                    game.handleError("There was an error loading the world.");
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
                    plugin.getLobby().resetLobby();
                    setStatus(Status.COMPLETE);
                    game.setState(GameState.SETUP_COMPLETE); //TODO
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
        COMPLETE
    }
}
