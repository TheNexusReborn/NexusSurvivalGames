package com.thenexusreborn.survivalgames.cmd.game;

import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.cmd.game.timer.*;

public class TimerSubCommand extends SubCommand {
    
    private SurvivalGames plugin;
    
    public TimerSubCommand(SurvivalGames plugin, NexusCommand parent) {
        super(parent, "timer", "Control the timer of the game", parent.getMinRank());
        this.plugin = plugin;
        addSubCommand(new GameTimerPauseSubCommand(plugin, this));
        addSubCommand(new GameTimerResumeSubCommand(plugin, this));
        addSubCommand(new GameTimerResetSubCommand(plugin, this));
        addSubCommand(new GameTimerModifySubCommand(plugin, this));
    }
}
