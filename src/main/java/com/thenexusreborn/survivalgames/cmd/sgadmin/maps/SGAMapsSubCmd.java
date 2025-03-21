package com.thenexusreborn.survivalgames.cmd.sgadmin.maps;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class SGAMapsSubCmd extends SubCommand<SurvivalGames> {
    public SGAMapsSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 0, "maps", "", Rank.ADMIN, "m");
        
        this.subCommands.add(new MapsListSubCmd(plugin, this));
        this.subCommands.add(new MapsExportSubCmd(plugin, this));
        this.subCommands.add(new MapsImportSubCmd(plugin, this));
        this.subCommands.add(new MapsSetSourceSubCmd(plugin, this));
    }
}
