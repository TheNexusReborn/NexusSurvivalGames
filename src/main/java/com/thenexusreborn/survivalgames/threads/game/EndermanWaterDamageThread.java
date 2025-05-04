package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class EndermanWaterDamageThread extends StarThread<SurvivalGames> {

    public EndermanWaterDamageThread(SurvivalGames plugin) {
        super(plugin, 20L, 0L, false);
    }

    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            if (!(game.getState() == Game.State.INGAME || game.getState() == Game.State.INGAME_DEATHMATCH)) {
                continue;
            }

            for (GamePlayer gamePlayer : new ArrayList<>(game.getPlayers().values())) {
                if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
                    continue;
                }
                
                if (gamePlayer.getMutation() == null) {
                    continue;
                }

                if (!gamePlayer.getMutation().getType().getId().equals("enderman")) {
                    continue;
                }

                Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
                Location loc = player.getLocation();
                Block block = loc.getBlock();
                if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
                    player.damage(1);
                }
            }
        }
    }
}
