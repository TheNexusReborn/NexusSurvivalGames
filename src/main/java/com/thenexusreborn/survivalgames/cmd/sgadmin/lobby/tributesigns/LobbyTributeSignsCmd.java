package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.tributesigns;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class LobbyTributeSignsCmd extends SubCommand<SurvivalGames> {
    public LobbyTributeSignsCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "tributesigns", "", Rank.ADMIN, "ts");
        
        this.subCommands.add(new TributeSignsRemoveSubCmd(plugin, this));
        this.subCommands.add(new TributeSignsSetSubCmd(plugin, this));
    }
}
