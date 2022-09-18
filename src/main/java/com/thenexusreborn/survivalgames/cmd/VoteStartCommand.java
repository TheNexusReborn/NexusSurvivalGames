package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.tournament.Tournament;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(MCUtils.color("&cOnly players can use that command."));
            return true;
        }
        
        Player senderPlayer = (Player) sender;
        
        if (plugin.getGame() != null) {
            senderPlayer.sendMessage(MCUtils.color("&cThere is already a game running. You cannot use that command."));
            return true;
        }
    
        Tournament tournament = NexusAPI.getApi().getTournament();
        if (tournament != null && tournament.isActive()) {
            senderPlayer.sendMessage(MCUtils.color(MsgType.WARN + "You cannot vote to start a lobby with an active tournament."));
            return true;
        }
    
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(senderPlayer.getUniqueId());
        Lobby lobby = plugin.getLobby();
        
        if (nexusPlayer.getPreferenceValue("vanish")) {
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
