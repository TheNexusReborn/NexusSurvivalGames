package com.thenexusreborn.survivalgames.cmd.sgadmin.game;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.control.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress.*;

public class SGAGameCmd extends SubCommand<SurvivalGames> {
    public SGAGameCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 0, "game", "", Rank.ADMIN, "g");
        this.playerOnly = true;
        
        this.subCommands.add(new GameControlSubCmd(plugin, this, ControlType.AUTO));
        this.subCommands.add(new GameControlSubCmd(plugin, this, ControlType.MANUAL));
        this.subCommands.add(new GameSetupCmd(plugin, this));
        this.subCommands.add(new GameAssignTeamsCmd(plugin, this));
        this.subCommands.add(new GameTPPlayersCmd(plugin, this));
        this.subCommands.add(new GameStartWarmupCmd(plugin, this));
        this.subCommands.add(new GameStartCmd(plugin, this));
        this.subCommands.add(new GameRestockChestsCmd(plugin, this));
        this.subCommands.add(new GameStartDMCDCmd(plugin, this));
        this.subCommands.add(new GameTPDMCmd(plugin, this));
        this.subCommands.add(new GameStartDMWarmupCmd(plugin, this));
        this.subCommands.add(new GameStartDeathmatchCmd(plugin, this));
        this.subCommands.add(new GameEndCmd(plugin, this));
        this.subCommands.add(new GameNextGameCmd(plugin, this));
        this.subCommands.add(new GameGiveSubCmd(plugin, this));
        this.subCommands.add(new GameGiveAllSubCmd(plugin, this));
        this.subCommands.add(new SGAGPlayerCmd(plugin, this));
        this.subCommands.add(new GameStatusSubCmd(plugin, this));
        this.subCommands.add(new GameDebugSubCmd(plugin, this));
    }
}
