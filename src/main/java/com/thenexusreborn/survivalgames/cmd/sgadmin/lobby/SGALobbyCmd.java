package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.*;
import com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.mapsigns.LobbyMapSignsCmd;
import com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.statsigns.LobbyStatSignsCmd;
import com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.tributesigns.LobbyTributeSignsCmd;
import com.thenexusreborn.survivalgames.control.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class SGALobbyCmd extends SubCommand<SurvivalGames> {
    public SGALobbyCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 0, "lobby", "", Rank.ADMIN, "l");
        this.playerOnly = true;
        
        //sub commands
        this.subCommands.add(new LobbyControlSubCmd(plugin, this, ControlType.AUTO));
        this.subCommands.add(new LobbyControlSubCmd(plugin, this, ControlType.MANUAL));
        this.subCommands.add(new LobbyRegenMapOptionsSubCmd(plugin, this));
        this.subCommands.add(new LobbyForceStartCmd(plugin, this));
        this.subCommands.add(new LobbyConfigureMapsCmd(plugin, this));
        this.subCommands.add(new LobbyPrepareGameCmd(plugin, this));
        this.subCommands.add(new LobbySetSpawnCmd(plugin, this));
        this.subCommands.add(new LobbyDebugCmd(plugin, this));
        this.subCommands.add(new LobbySetMapCmd(plugin, this));
        this.subCommands.add(new LobbyMapSignsCmd(plugin, this));
        this.subCommands.add(new LobbyStatSignsCmd(plugin, this));
        this.subCommands.add(new LobbyTributeSignsCmd(plugin, this));
    }
}
