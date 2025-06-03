package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class LobbySubCommand extends SubCommand<SurvivalGames> {
    public LobbySubCommand(SurvivalGames plugin, ICommand<SurvivalGames> parent, String name, String description, String... aliases) {
        super(plugin, parent, 1, name, description, Rank.ADMIN, aliases);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender; 
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        return handle(player, sgPlayer, lobby, args, flagResults);
    }
    
    public abstract boolean handle(Player player, SGPlayer sgPlayer, Lobby lobby, String[] args, FlagResult flagResults);
}
