package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

import java.util.Collections;

public class TeleportDeathmatchSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public TeleportDeathmatchSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "teleportdeathmatch", "Teleport players to the deathmatch", parent.getMinRank(), true, false, Collections.singletonList("tpdm"));
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_DEATHMATCH ||
                game.getState() == GameState.DEATHMATCH_WARMUP || game.getState() == GameState.DEATHMATCH_WARMUP_DONE) {
            game.teleportDeathmatch();
            if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                actor.sendMessage("&eYou teleported everyone to the deathmatch");
            } else {
                actor.sendMessage("&cThere was a problem teleporting players to the deathmatch.");
            }
        } else {
            actor.sendMessage("&cInvalid game state. Must be playing, playing deathmatch, deathmatch countdown or deathmatch countdown complete.");
        }
    }
}
