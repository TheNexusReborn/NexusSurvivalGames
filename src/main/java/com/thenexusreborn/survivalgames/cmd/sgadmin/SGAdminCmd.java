package com.thenexusreborn.survivalgames.cmd.sgadmin;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.cmd.sgadmin.game.SGAGameCmd;
import com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.SGALobbyCmd;
import com.thenexusreborn.survivalgames.cmd.sgadmin.maps.SGAMapsSubCmd;
import com.thenexusreborn.survivalgames.cmd.sgadmin.settings.SGASettingsCmd;

public class SGAdminCmd extends NexusCommand<SurvivalGames> {
    public SGAdminCmd(SurvivalGames plugin, String name, String description, Rank minRank, String... aliases) {
        super(plugin, "sgadmin", "", Rank.ADMIN, "sga");
        this.subCommands.add(new SGAGameCmd(plugin, this));
        this.subCommands.add(new SGALobbyCmd(plugin, this));
        this.subCommands.add(new SGASettingsCmd(plugin, this));
        this.subCommands.add(new SGAMapsSubCmd(plugin, this));
    }
}
