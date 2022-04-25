package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

public class StartGameSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public StartGameSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "start", "Starts the game", parent.getMinRank());
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.WARMUP_DONE || game.getState() == GameState.TELEPORT_START_DONE) {
            game.startGame();
        } else if (game.getState() == GameState.WARMUP) {
            if (game.getTimer() != null) {
                game.getTimer().cancel();
            } else {
                actor.sendMessage("&cThe game state was still set to countdown, but no timer was actively running. Please report this as a bug, started game anyways.");
            }
            game.startGame();
        } else if (game.getState() != GameState.TELEPORT_START_DONE){
            actor.sendMessage("&cYou must run the teleport players task at the minimum before starting the game");
            return;
        }
    
        if (game.getState() == GameState.INGAME) {
            actor.sendMessage("&eThe game has been started");
        } else {
            actor.sendMessage("&cThere was a problem starting the game.");
        }
    }
}
