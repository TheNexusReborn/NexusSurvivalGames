package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.CombatTag;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;

import java.util.ArrayList;

//TODO Use a Timer for combat tags
public class CombatTagTask extends NexusThread<SurvivalGames> {
    public CombatTagTask(SurvivalGames plugin) {
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
            
            CombatTag combatTag = gamePlayer.getCombatTag();
            
            if (combatTag.getOther() == null) {
                continue;
            }
            
            if (combatTag.getTimestamp() + 10000 > System.currentTimeMillis()) {
                continue;
            }
            
            combatTag.setOther(null);
            gamePlayer.sendMessage("&6&l>> &eYou are no longer in combat.");
        }
    }
}
