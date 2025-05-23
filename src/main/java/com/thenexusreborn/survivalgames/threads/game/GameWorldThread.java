package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.settings.enums.Weather;
import org.bukkit.World;

public class GameWorldThread extends StarThread<SurvivalGames> {
    
    public GameWorldThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    @Override
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            if (game.getState().ordinal() < Game.State.SETUP_COMPLETE.ordinal()) {
                continue;
            }

            World world = game.getGameMap().getWorld();
            if (world == null) {
                continue;
            }

            world.setTime(game.getSettings().getTime().getStart());
            Weather weather = game.getSettings().getWeather();
            if (weather == Weather.CLEAR) {
                world.setStorm(false);
                world.setThundering(false);
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThunderDuration(Integer.MAX_VALUE);
            } else if (weather == Weather.RAIN) {
                world.setStorm(true);
                world.setThundering(false);
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThunderDuration(Integer.MAX_VALUE);
            } else if (weather == Weather.STORM) {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThunderDuration(Integer.MAX_VALUE);
            }
        }
    }
}