package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.Bukkit;

public class PlayerUpdateThread extends NexusThread<SurvivalGames> {
    public PlayerUpdateThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            if (game != null) {
                for (GamePlayer player : game.getPlayers().values()) {
                    if (player.getTeam() == GameTeam.SPECTATORS) {
                        SGUtils.updatePlayerHealthAndFood(Bukkit.getPlayer(player.getUniqueId()));
                    } else if (player.getTeam() == GameTeam.MUTATIONS) {
                        player.setFood(20, 20F);
                    }
                }
            }
        }
    }
}
