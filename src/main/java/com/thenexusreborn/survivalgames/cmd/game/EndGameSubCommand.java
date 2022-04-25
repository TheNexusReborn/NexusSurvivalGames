package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

public class EndGameSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public EndGameSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "end", "End the game", parent.getMinRank());
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() != GameState.ENDING && game.getState() != GameState.ENDED) {
            game.end();
            actor.sendMessage("&eYou ended the game.");
        } else {
            actor.sendMessage("&cThe game has already ended");
        }
    }
}
