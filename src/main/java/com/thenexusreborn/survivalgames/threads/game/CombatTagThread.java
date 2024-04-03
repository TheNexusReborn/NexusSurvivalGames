package com.thenexusreborn.survivalgames.threads.game;

import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.CombatTag;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

//TODO Use a Timer for combat tags
public class CombatTagThread extends NexusThread<SurvivalGames> {
    public CombatTagThread(SurvivalGames plugin) {
        super(plugin, 5L, 0L, false);
    }

    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Game game = server.getGame();
            if (game == null) {
                continue;
            }
            
            for (GamePlayer gamePlayer : new ArrayList<>(game.getPlayers().values())) {
                if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                    continue;
                }

                Player player = Bukkit.getPlayer(gamePlayer.getUniqueId());
                if (player.getHealth() == player.getMaxHealth()) {
                    gamePlayer.getDamageInfo().clearDamagers();
                }

                CombatTag combatTag = gamePlayer.getCombatTag();

                if (combatTag.getOther() == null) {
                    continue;
                }

                if (combatTag.getTimestamp() + TimeUnit.SECONDS.toMillis(game.getSettings().getCombatTagLength()) > System.currentTimeMillis()) {
                    continue;
                }

                combatTag.setOther(null);
                gamePlayer.sendMessage("&6&l>> &eYou are no longer in combat.");
            }
        }
    }
}
