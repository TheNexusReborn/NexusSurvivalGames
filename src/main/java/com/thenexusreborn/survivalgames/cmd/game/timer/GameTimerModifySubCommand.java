package com.thenexusreborn.survivalgames.cmd.game.timer;

import com.thenexusreborn.nexuscore.util.Operator;
import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;

@SuppressWarnings("DuplicatedCode")
public class GameTimerModifySubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public GameTimerModifySubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "modify", "Modify the length of the timer", parent.getMinRank());
        this.plugin = plugin;
        addArgument(new Argument("value", true, "You must provide a numberic value in seconds"));
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
        
        String input = args[0];
        Operator operator = null;
        if (input.startsWith("+")) {
            input = input.substring(1);
            operator = Operator.ADD;
        } else if (input.startsWith("-")) {
            input = input.substring(1);
            operator = Operator.SUBTRACT;
        } else if (input.startsWith("*")) {
            input = input.substring(1);
            operator = Operator.MULTIPLY;
        } else if (input.startsWith("/")) {
            input = input.substring(1);
            operator = Operator.DIVIDE;
        }
        
        int seconds;
        try {
            seconds = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            actor.sendMessage("&cYou provided an invalid number for the seconds");
            return;
        }
        
        long milliseconds = (seconds * 1000L) + 50;
        if (operator == null) {
            timer.setLength(milliseconds);
        } else {
            if (operator == Operator.ADD) {
                long newTime = timer.getTimeLeft() + milliseconds;
                if (newTime > timer.getLength()) {
                    newTime = timer.getLength();
                    actor.sendMessage("&7&oThe new timer length exceeds the specified length of the timer. Using the max length. Please use this command without an operator to change the specified length");
                }
                timer.setRawTime(newTime);
            } else if (operator == Operator.SUBTRACT) {
                long newTime = timer.getTimeLeft() - milliseconds;
                if (newTime <= 0) {
                    actor.sendMessage("&7&oThe new timer length is less than or equal to 0. Please use the timer cancel command instead.");
                    return;
                }
                
                timer.setRawTime(newTime);
            } else if (operator == Operator.MULTIPLY) {
                long newTime = timer.getTimeLeft() * milliseconds;
                if (newTime > timer.getLength()) {
                    newTime = timer.getLength();
                    actor.sendMessage("&7&oThe new timer length exceeds the specified length of the timer. Using the max length. Please use this command without an operator to change the specified length");
                }
                timer.setRawTime(newTime);
            } else if (operator == Operator.DIVIDE) {
                if (milliseconds == 0) {
                    actor.sendMessage("&cCannot divide by zero");
                    return;
                }
                long newTime = timer.getTimeLeft() / milliseconds;
                if (newTime <= 0) {
                    actor.sendMessage("&7&oThe new timer length is less than or equal to 0. Please use the timer cancel command instead.");
                    return;
                }
    
                timer.setRawTime(newTime);
            }
        }
        
        game.sendMessage("&eThe timer has been modified by &b" + actor.getPlayer().getName());
        if (!game.hasPlayer(actor.getPlayer().getUniqueId())) {
            actor.sendMessage("&eYou modified the game timer.");
        }
    }
}
