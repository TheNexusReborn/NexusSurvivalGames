package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

import java.util.Collections;

public class StartDeathmatchTimerSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public StartDeathmatchTimerSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "startdeathmatchtimer", "Start the deathmatch timer", parent.getMinRank(), true, false, Collections.singletonList("sdmt"));
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.INGAME) {
            game.startDeathmatchTimer();
            if (game.getState() == GameState.INGAME_DEATHMATCH) {
                actor.sendMessage("&eYou started the deathmatch timer");
            } else {
                actor.sendMessage("&cThere was a problem starting the deathmatch timer.");
            }
        } else {
            actor.sendMessage("Invalid state. Please ensure that the game is in the PLAYING state.");
        }
    }
}
