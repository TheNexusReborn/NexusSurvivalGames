package com.thenexusreborn.survivalgames.listener;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import me.vagdedes.spartan.api.PlayerViolationEvent;
import org.bukkit.event.*;

public class AnticheatListener implements Listener {
    
    private SurvivalGames plugin;
    
    public AnticheatListener(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerViolation(PlayerViolationEvent e) {
        Game game = plugin.getGame();
        if (game != null) {
            if (game.getState() == GameState.WARMUP || game.getState() == GameState.DEATHMATCH_WARMUP) {
                e.setCancelled(true);
            }
        }
    }
}
