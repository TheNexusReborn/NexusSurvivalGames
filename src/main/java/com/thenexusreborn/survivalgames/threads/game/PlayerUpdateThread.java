package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUpdateThread extends StarThread<SurvivalGames> {
    public PlayerUpdateThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers().values()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            for (GamePlayer player : game.getPlayers().values()) {
                if (player.getTeam() == GameTeam.SPECTATORS) {
                    SGUtils.updatePlayerHealthAndFood(Bukkit.getPlayer(player.getUniqueId()));
                    player.updateMutationItem();
                } else if (player.getTeam() == GameTeam.MUTATIONS) {
                    player.setFood(20, 20F);
                } else if (player.getTeam() == GameTeam.TRIBUTES) {
                    Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                    if (bukkitPlayer.getHealth() >= bukkitPlayer.getMaxHealth()) {
                        player.getDamageInfo().clearDamagers();
                    }
                }
            }
        }
    }
}
