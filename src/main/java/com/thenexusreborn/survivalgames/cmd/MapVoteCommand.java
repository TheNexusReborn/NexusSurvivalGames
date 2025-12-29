package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public class MapVoteCommand extends NexusCommand<SurvivalGames> {
    
    public MapVoteCommand(SurvivalGames plugin) {
        super(plugin, "mapvote", "", Rank.MEMBER);
        this.playerOnly = true;
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            sender.sendMessage(MsgType.WARN.format("You must provide a map name or position."));
            return true;
        }

        Player player = (Player) sender; 

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        Lobby lobby = sgPlayer.getLobby();

        if (lobby == null) {
            MsgType.WARN.send(sender, "You cannot use that command at this time.");
            return true;
        }

        if (game != null) {
            sender.sendMessage(MsgType.WARN.format("You cannot vote for a map during a game."));
            return true;
        }

        if (!(lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN)) {
            sender.sendMessage(MsgType.WARN.format("Invalid lobby state to vote."));
            return true;
        }

        NexusPlayer nexusPlayer = sgPlayer.getNexusPlayer();

        if (nexusPlayer.getToggleValue("vanish")) {
            nexusPlayer.sendMessage(MsgType.WARN.format("You cannot vote for a map while in vanish."));
            return true;
        }

        try {
            int position = Integer.parseInt(args[0]);
            boolean contains = lobby.getMapOptions().containsKey(position);
            if (!contains) {
                sender.sendMessage(MsgType.WARN.format("The map options do not contain that position."));
                return true;
            }

            lobby.addMapVote(nexusPlayer, lobby.getMapSigns().get(position));
        } catch (NumberFormatException e) {
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg).append(" ");
            }

            SGMap gameMap = SGUtils.getGameMapFromInput(sb.toString().trim(), sender);
            if (gameMap == null) {
                sender.sendMessage(MsgType.WARN.format("Could not find a game map."));
                return true;
            }

            for (Entry<Integer, SGMap> entry : lobby.getMapOptions().entrySet()) {
                if (gameMap.getName().equalsIgnoreCase(entry.getValue().getName())) {
                    lobby.addMapVote(nexusPlayer, lobby.getMapSigns().get(entry.getKey()));
                    return true;
                }
            }
        }

        return true;
    }
}
