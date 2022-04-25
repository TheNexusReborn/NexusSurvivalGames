package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.scheduler.BukkitRunnable;

public class SetupSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public SetupSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "setup", "Runs setup tasks for the active game", parent.getMinRank());
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
        
        if (game.getState() != GameState.UNDEFINED) {
            actor.sendMessage("&cThe game is not in the proper state to be setup.");
            return;
        }
        
        game.setup();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getState() == GameState.SETUP_COMPLETE) {
                    actor.sendMessage("&eThe game setup is now complete.");
                    cancel();
                } else if (game.getState() == GameState.ERROR) {
                    actor.sendMessage("&cThere was a problem during Game Setup");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
