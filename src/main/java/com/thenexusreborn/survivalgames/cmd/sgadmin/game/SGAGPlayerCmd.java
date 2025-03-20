package com.thenexusreborn.survivalgames.cmd.sgadmin.game;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.cmd.sgadmin.game.player.*;

public class SGAGPlayerCmd extends SubCommand<SurvivalGames> {
    public SGAGPlayerCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "player", "", Rank.ADMIN, "p");
        
        this.subCommands.add(new GameAddPlayerSubCmd(plugin, this));
        this.subCommands.add(new GameRevivePlayerSubCmd(plugin, this));
        this.subCommands.add(new GameRemovePlayerSubCmd(plugin, this));
        this.subCommands.add(new GameMutatePlayerSubCmd(plugin, this));
        this.subCommands.add(new GamePlayerStatusSubCmd(plugin, this));
    }
}
