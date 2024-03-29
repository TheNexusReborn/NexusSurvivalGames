package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class VoteStartCommand implements CommandExecutor {
    
    private final SurvivalGames plugin;
    
    public VoteStartCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage(MCUtils.color("&cOnly players can use that command."));
            return true;
        }
    
        if (plugin.getGame() != null) {
            senderPlayer.sendMessage(MCUtils.color("&cThere is already a game running. You cannot use that command."));
            return true;
        }
    
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(senderPlayer.getUniqueId());
        Lobby lobby = plugin.getLobby();
    
        if (!lobby.getLobbySettings().isAllowVoteStart()) {
            nexusPlayer.sendMessage(MsgType.WARN + "You cannot vote to start because it is disabled.");
            return true;
        }
        
        if (lobby.getPlayingCount() > lobby.getLobbySettings().getVoteStartAvailableThreshold()) {
            nexusPlayer.sendMessage(MsgType.WARN + "You cannot vote to start because the player count is too high.");
            return true;
        }
        
        if (nexusPlayer.getToggleValue("vanish")) {
            nexusPlayer.sendMessage(MsgType.WARN + "You cannot vote to start while in vanish mode.");
            return true;
        }
        
        if (lobby.getSpectatingPlayers().contains(nexusPlayer.getUniqueId())) {
            nexusPlayer.sendMessage(MsgType.WARN + "You are a spectator in the next game. You cannot vote to start the game.");
            return true;
        }
        
        if (lobby.hasVotedToStart(nexusPlayer)) {
            nexusPlayer.sendMessage(MsgType.WARN + "You have already voted to start the lobby.");   
            return true;
        }
        
        lobby.addStartVote(nexusPlayer);
        return true;
    }
}
