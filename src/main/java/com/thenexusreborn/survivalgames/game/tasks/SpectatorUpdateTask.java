package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.nexuscore.api.NexusTask;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.Bukkit;

public class SpectatorUpdateTask extends NexusTask<SurvivalGames> {
    public SpectatorUpdateTask(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    public void onRun() {
        Game game = plugin.getGame();
        if (game != null) {
            for (GamePlayer player : game.getPlayers().values()) {
                if (player.getTeam() == GameTeam.SPECTATORS) {
                    SGUtils.updatePlayerHealthAndFood(Bukkit.getPlayer(player.getUniqueId()));
                }
            }
        }
    }
}
