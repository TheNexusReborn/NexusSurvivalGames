package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

public class NextGameSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public NextGameSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "nextgame", "Go to the next game", parent.getMinRank());
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.ENDING || game.getState() == GameState.ENDED) {
            game.nextGame();
            actor.sendMessage("&eMoved everyone to the next game");
        } else {
            actor.sendMessage("&cYou must end the game first before going to the next one.");
        }
    }
}
