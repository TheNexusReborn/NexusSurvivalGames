package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.statsigns;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class LobbyStatSignsCmd extends SubCommand<SurvivalGames> {
    public LobbyStatSignsCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "statsigns", "", Rank.ADMIN, "sts");
        
        this.subCommands.add(new StatSignsRemoveSubCmd(plugin, this));
        this.subCommands.add(new StatSignsAddSubCmd(plugin, this));
    }
}
