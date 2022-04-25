package com.thenexusreborn.survivalgames.cmd.game.timer;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;

@SuppressWarnings("DuplicatedCode")
public class GameTimerResetSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public GameTimerResetSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "reset", "Reset the timer", parent.getMinRank());
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
        Game game = plugin.getGame();
        if (game == null) {
            actor.sendMessage("&cThere is no active game.");
            return;
        }
    
        Timer timer = plugin.getGame().getTimer();
        if (timer == null) {
            actor.sendMessage("&cThe game does not have a timer. This is only in the case of Game Setup or an error");
            return;
        }
        
        timer.reset();
        game.sendMessage("&eThe timer has been reset by &b" + actor.getPlayer().getName());
        if(!game.hasPlayer(actor.getPlayer().getUniqueId())) {
            actor.sendMessage("&eYou reset the game timer.");
        }
    }
}
