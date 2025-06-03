package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteStartCommand extends NexusCommand<SurvivalGames> {

    public VoteStartCommand(SurvivalGames plugin) {
        super(plugin, "votestart", "", Rank.MEMBER);
        this.playerOnly = true;
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player senderPlayer = (Player) sender; 
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(senderPlayer.getUniqueId());

        if (sgPlayer.getGame() != null) {
            senderPlayer.sendMessage(MsgType.WARN.format("You cannot use that command while in game."));
            return true;
        }

        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(senderPlayer.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();

        if (!lobby.getLobbySettings().isAllowVoteStart()) {
            nexusPlayer.sendMessage(MsgType.WARN.format("You cannot vote to start because it is disabled."));
            return true;
        }

        if (lobby.getPlayingCount() > lobby.getLobbySettings().getVoteStartAvailableThreshold()) {
            nexusPlayer.sendMessage(MsgType.WARN.format("You cannot vote to start because the player count is too high."));
            return true;
        }

        if (nexusPlayer.getToggleValue("vanish")) {
            nexusPlayer.sendMessage(MsgType.WARN.format("You cannot vote to start while in vanish mode."));
            return true;
        }

        if (lobby.getSpectatingPlayers().contains(nexusPlayer.getUniqueId())) {
            nexusPlayer.sendMessage(MsgType.WARN.format("You are a spectator in the next game. You cannot vote to start the game."));
            return true;
        }

        if (lobby.hasVotedToStart(nexusPlayer)) {
            nexusPlayer.sendMessage(MsgType.WARN.format("You have already voted to start the lobby."));
            return true;
        }

        lobby.addStartVote(nexusPlayer);
        return true;
    }
}
