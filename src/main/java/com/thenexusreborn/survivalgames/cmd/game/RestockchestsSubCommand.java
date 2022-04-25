package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;

import java.util.Collections;

public class RestockchestsSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public RestockchestsSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "restockchests", "Restock chests", parent.getMinRank(), true, false, Collections.singletonList("rc"));
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_GRACEPERIOD || game.getState() == GameState.INGAME_DEATHMATCH ||
                game.getState() == GameState.DEATHMATCH_WARMUP || game.getState() == GameState.DEATHMATCH_WARMUP_DONE) {
            game.restockChests();
            game.sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED");
        } else {
            actor.sendMessage("&cInvalid game state. Must be playing, playing deathmatch, deathmatch countdown or deathmatch countdown complete.");
        }
    }
}
