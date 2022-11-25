package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.nexuscore.api.NexusTask;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DamagersTask extends NexusTask<SurvivalGames> {
    
    public DamagersTask(SurvivalGames plugin) {
        super(plugin, 10L, 0L, false);
    }
    
    public void onRun() {
        Game game = plugin.getGame();
        
        if (game == null) {
            return;
        }
    
        for (GamePlayer gamePlayer : new ArrayList<>(game.getPlayers().values())) {
            Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
            if (player.getHealth() == player.getMaxHealth()) {
                gamePlayer.getDamageInfo().clearDamagers();
            }
        }
    }
}
