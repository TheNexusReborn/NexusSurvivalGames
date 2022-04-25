package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

import java.util.Collections;

public class TeleportPlayersSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public TeleportPlayersSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "teleportplayers", "Teleports the players to their spawns", parent.getMinRank(), true, false, Collections.singletonList("tpp"));
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.TEAMS_ASSIGNED) {
            game.teleportStart();
            if (game.getState() == GameState.TELEPORT_START_DONE) {
                actor.sendMessage("&ePlayers have been teleported.");
            } else {
                actor.sendMessage("&cThere was a problem teleporting players.");
            }
        } else {
            actor.sendMessage("&cThe teams have not be assigned yet. Please run the team assignment task.");
        }
    }
}
