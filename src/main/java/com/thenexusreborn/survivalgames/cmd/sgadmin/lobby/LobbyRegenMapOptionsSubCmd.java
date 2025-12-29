package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.entity.Player;

public class LobbyRegenMapOptionsSubCmd extends LobbySubCommand {
    
    public LobbyRegenMapOptionsSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "regeneratemapoptions", "", "rmo");
    }
    
    @Override
    public boolean handle(Player player, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults) {
        lobby.generateMapOptions();
        MsgType.INFO.send(player, "You regenerated the map options");
        return true;
    }
}
