package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.settings.Weather;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class GameWorldTask extends BukkitRunnable {
    
    private SurvivalGames plugin;
    
    public GameWorldTask(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        Game game = plugin.getGame();
        if (game == null) {
            return;
        }
        
        if (game.getState().ordinal() < GameState.SETUP_COMPLETE.ordinal()) {
            return;
        }
    
        World world = game.getGameMap().getWorld();
        if (world == null) {
            return;
        }
        
        world.setTime(game.getSettings().getTime().getStart());
        Weather weather = game.getSettings().getWeather();
        if (weather == Weather.CLEAR) {
            world.setStorm(false);
            world.setThundering(false);
            world.setWeatherDuration(0);
            world.setThunderDuration(0);
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
