package com.thenexusreborn.survivalgames.cmd.sgadmin.loottable;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class SGALoottableCmd extends SubCommand<SurvivalGames> {
    public SGALoottableCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 0, "loottable", "", Rank.ADMIN, "lt");
        
        this.subCommands.add(new LoottableReloadSubCmd(plugin, this));
        this.subCommands.add(new LoottableSetItemWeightSubCmd(plugin, this));
    }
}
