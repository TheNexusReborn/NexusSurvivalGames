package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class EndermanWaterDamageThread extends NexusThread<SurvivalGames> {

    public EndermanWaterDamageThread(SurvivalGames plugin) {
        super(plugin, 20L, 0L, false);
    }

    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            if (!(game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_DEATHMATCH)) {
                continue;
            }

            for (GamePlayer gamePlayer : new ArrayList<>(game.getPlayers().values())) {
                if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
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
