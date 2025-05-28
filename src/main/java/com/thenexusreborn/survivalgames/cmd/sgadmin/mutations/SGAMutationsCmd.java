package com.thenexusreborn.survivalgames.cmd.sgadmin.mutations;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class SGAMutationsCmd extends SubCommand<SurvivalGames> {
    public SGAMutationsCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 0, "mutations", "Mutation management command", Rank.ADMIN, "m");
        
        this.subCommands.add(new MutationsDisableCmd(plugin, this));
        this.subCommands.add(new MutationsEnableCmd(plugin, this));
    }
}
