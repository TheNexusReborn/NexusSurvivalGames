package com.thenexusreborn.survivalgames.cmd.sgadmin.lobby.statsigns;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public abstract class StatSignsSubCmd extends SubCommand<SurvivalGames> {
    public StatSignsSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent, String name, String description, String... aliases) {
        super(plugin, parent, 2, name, description, Rank.ADMIN, aliases);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        
        if (sgPlayer.getGame() != null) {
            sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
            return true;
        }
        
        Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
        if (targetBlock == null) {
            player.sendMessage(MsgType.WARN.format("You are not looking at a block."));
            return true;
        }
        
        if (!(targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN)) {
            player.sendMessage(MsgType.WARN.format("You are not looking at a sign."));
            return true;
        }
        
        return handle(player, sgPlayer, lobby, targetBlock, args, flagResults);
    }
    
    protected abstract boolean handle(Player sender, SGPlayer sgPlayer, Lobby lobby, Block sign, String[] args, FlagResult flagResults);
}
