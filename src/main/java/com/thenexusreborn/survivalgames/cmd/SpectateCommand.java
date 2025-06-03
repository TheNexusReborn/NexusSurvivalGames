package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.game.death.DeathType;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class SpectateCommand extends NexusCommand<SurvivalGames> {
    private static final Set<Game.State> INVALID_GAME_STATES = new HashSet<>(Arrays.asList(Game.State.ASSIGN_TEAMS, Game.State.ENDED, Game.State.ENDING,
            Game.State.ERROR, Game.State.TELEPORT_START, Game.State.SETTING_UP, Game.State.SETUP_COMPLETE, Game.State.UNDEFINED));


    public SpectateCommand(SurvivalGames plugin) {
        super(plugin, "spectate", "", Rank.MEDIA);
        this.playerOnly = true;
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender; 
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());

        Game game = sgPlayer.getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

            if (INVALID_GAME_STATES.contains(game.getState())) {
                player.sendMessage(MsgType.WARN.format("Invalid game state to use that command. Please wait to try again."));
                return true;
            }

            if (gamePlayer.getTeam() == GameTeam.TRIBUTES || gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                game.killPlayer(gamePlayer, new DeathInfo(game, System.currentTimeMillis(), gamePlayer, DeathType.SPECTATE, player.getLocation()));
            } else {
                player.sendMessage(MsgType.WARN.format("You are already spectating the game."));
            }
        } else {
            Lobby lobby = sgPlayer.getLobby();
            if (lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN) {
                boolean spectating = false;
                for (UUID spectatingPlayer : lobby.getSpectatingPlayers()) {
                    if (spectatingPlayer.equals(player.getUniqueId())) {
                        spectating = true;
                    }
                }

                if (spectating) {
                    lobby.removeSpectatingPlayer(player.getUniqueId());
                    sender.sendMessage(MsgType.INFO.format("You will no longer be spectating the game"));
                } else {
                    lobby.addSpectatingPlayer(player.getUniqueId());
                    sender.sendMessage(MsgType.INFO.format("You will be spectating the game."));
                }
            } else {
                sender.sendMessage(MsgType.WARN.format("Invalid lobby state. Can only be done before or during the game start countdown"));
            }
        }
        return true;
    }
}
