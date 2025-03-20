package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import org.bukkit.entity.Player;

public class LobbyConfigureMapsCmd extends LobbySubCommand {
    public LobbyConfigureMapsCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "configuremaps", "", "cm");
    }
    
    @Override
    public boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults) {
        if (sgPlayer.getGame() != null) {
            sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
            return true;
        }
        
        if (lobby.getState() == LobbyState.MAP_CONFIGURATING) {
            lobby.stopConfiguringMaps();
            sender.sendMessage(MsgType.INFO.format("You stopped configuring maps."));
            lobby.sendMessage("&6&l>> &eConfiguration of maps is now over.");
            lobby.sendMessage("&6&l > &eAutomatic actions are now allowed.");
        } else {
            lobby.startConfiguringMaps();
            lobby.sendMessage("&6&l>> &eThe lobby has been set to configuring maps.");
            lobby.sendMessage("&6&l > &eAll automatic actions have been cancelled and will not trigger.");
        }
        return true;
    }
}
