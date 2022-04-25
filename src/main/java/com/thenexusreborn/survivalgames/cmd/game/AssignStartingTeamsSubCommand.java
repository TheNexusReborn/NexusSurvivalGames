package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

import java.util.*;

public class AssignStartingTeamsSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public AssignStartingTeamsSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "assignstartingteams", "Assigns the starting teams", parent.getMinRank(), true, false, Collections.singletonList("ast"));
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.SETUP_COMPLETE) {
            game.assignStartingTeams();
            if (game.getState() == GameState.TEAMS_ASSIGNED) {
                actor.sendMessage("&eStarting teams have been assigned");
            } else {
                actor.sendMessage("&cThere was a problem assigning starting teams");
            }
        } else {
            actor.sendMessage("&cThe game is not yet setup. Please run the setup task before assigning teams.");
        }
    }
}
