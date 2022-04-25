package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

import java.util.Collections;

public class StartCountdownSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public StartCountdownSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "startcountdown", "Starts the countdown for the game", parent.getMinRank(), true, false, Collections.singletonList("scd"));
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.TELEPORT_START_DONE) {
            game.startWarmup();
            if (game.getState() == GameState.WARMUP || game.getState() == GameState.WARMUP_DONE) {
                actor.sendMessage("&eThe countdown has been started successfully");
            } else {
                actor.sendMessage("&cThere was a problem starting the countdown");
            }
        } else {
            actor.sendMessage("&cYou must run the teleport players task before starting the countdown.");
        }
    }
}
