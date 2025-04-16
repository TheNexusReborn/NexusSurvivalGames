package com.thenexusreborn.survivalgames.cmd.sgadmin.settings.type;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.cmd.sgadmin.settings.SettingsListSubCmd;
import com.thenexusreborn.survivalgames.cmd.sgadmin.settings.SettingsSetSubCmd;

import java.util.function.Function;

public class SettingsGameSubCmd extends SubCommand<SurvivalGames> {
    
    private static final Function<SGPlayer, Object> SETTINGS_GETTER = sgPlayer -> {
        if (sgPlayer.getGame() != null) {
            return sgPlayer.getGame().getSettings();
        } else if (sgPlayer.getLobby() != null) {
            return sgPlayer.getLobby().getGameSettings();
        }
        
        return null;
    };
    
    public SettingsGameSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "game", "", Rank.ADMIN, "g");
        
        this.subCommands.add(new SettingsListSubCmd(plugin, this, "game", SETTINGS_GETTER));
        this.subCommands.add(new SettingsSetSubCmd(plugin, this, "game", SETTINGS_GETTER));
    }
}
