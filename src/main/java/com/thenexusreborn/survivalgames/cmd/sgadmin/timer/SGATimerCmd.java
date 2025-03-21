package com.thenexusreborn.survivalgames.cmd.sgadmin.timer;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class SGATimerCmd extends SubCommand<SurvivalGames> {
    public SGATimerCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 0, "timer", "", Rank.ADMIN, "t");
        
        this.subCommands.add(new TimerPauseSubCmd(plugin, this));
        this.subCommands.add(new TimerResumeSubCmd(plugin, this));
        this.subCommands.add(new TimerResetSubCmd(plugin, this));
        this.subCommands.add(new TimerModifySubCmd(plugin, this));
    }
}
