package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

import java.util.Collections;

public class StartDeathmatchCountdownSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public StartDeathmatchCountdownSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "startdeathmatchcountdown", "Start the deathmatch countdown", parent.getMinRank(), true, false, Collections.singletonList("sdmcd"));
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
            game.startDeathmatchWarmup();
            actor.sendMessage("&eYou started the deathmatch countdown");
        } else {
            actor.sendMessage("&cThe players have not been teleported to the deathmatch, or the deathmatch has already started.");
        }
    }
}
