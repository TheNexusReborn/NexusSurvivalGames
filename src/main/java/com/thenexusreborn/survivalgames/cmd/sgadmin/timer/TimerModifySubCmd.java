package com.thenexusreborn.survivalgames.cmd.sgadmin.timer;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.cmd.sgadmin.timer.modify.*;

public class TimerModifySubCmd extends SubCommand<SurvivalGames> {
    public TimerModifySubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "modify", "", Rank.ADMIN);
        
        this.subCommands.add(new ModifySubtractSubCmd(plugin, this));
        this.subCommands.add(new ModifyAddSubCmd(plugin, this));
        this.subCommands.add(new ModifySetSubCmd(plugin, this));
    }
}
