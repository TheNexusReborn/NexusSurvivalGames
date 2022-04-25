package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

import java.util.Collections;

public class StartDeathmatchSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public StartDeathmatchSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "startdeathmatch", "Start the deathmatch", parent.getMinRank(), true, false, Collections.singletonList("sdm"));
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE || game.getState() == GameState.DEATHMATCH_WARMUP || game.getState() == GameState.DEATHMATCH_WARMUP_DONE) {
            game.startDeathmatch();
            actor.sendMessage("&eYou started the deathmatch");
        } else {
            actor.sendMessage("&cYou must at least teleport players to the deathmatch, or it cannot have been started already.");
        }
    }
}
