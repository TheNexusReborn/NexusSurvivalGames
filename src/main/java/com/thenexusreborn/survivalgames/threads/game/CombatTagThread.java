package com.thenexusreborn.survivalgames.threads.game;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.CombatTag;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import me.firestar311.starlib.api.time.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

//TODO Use a Timer for combat tags
public class CombatTagThread extends NexusThread<SurvivalGames> {
    public CombatTagThread(SurvivalGames plugin) {
        super(plugin, 5L, 0L, false);
    }
    
    public void onRun() {
        Game game = plugin.getGame();
        if (game == null) {
            return;
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
            
            if (combatTag.getTimestamp() + TimeUnit.SECONDS.toMilliseconds(game.getSettings().getCombatTagLength()) > System.currentTimeMillis()) {
                continue;
            }
            
            combatTag.setOther(null);
            gamePlayer.sendMessage("&6&l>> &eYou are no longer in combat.");
        }
    }
}
