package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.settings.enums.WeatherType;
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

            world.setTime(game.getSettings().world.time.value.getStart());
            WeatherType weather = game.getSettings().world.weather.type;
            if (weather == WeatherType.CLEAR) {
                world.setStorm(false);
                world.setThundering(false);
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThunderDuration(Integer.MAX_VALUE);
            } else if (weather == WeatherType.RAIN) {
                world.setStorm(true);
                world.setThundering(false);
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThunderDuration(Integer.MAX_VALUE);
            } else if (weather == WeatherType.STORM) {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThunderDuration(Integer.MAX_VALUE);
            }
        }
    }
}