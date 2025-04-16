package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.mapsigns;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class LobbyMapSignsCmd extends SubCommand<SurvivalGames> {
    public LobbyMapSignsCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "mapsigns", "", Rank.ADMIN, "ms");
        
        this.subCommands.add(new MapSignsRemoveSubCmd(plugin, this));
        this.subCommands.add(new MapSignsSetSubCmd(plugin, this));
    }
}
