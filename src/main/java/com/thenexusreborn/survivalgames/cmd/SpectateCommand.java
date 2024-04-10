package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.OldGameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.game.death.DeathType;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpectateCommand implements CommandExecutor {
    
    private final SurvivalGames plugin;
    
    private static final Set<OldGameState> INVALID_GAME_STATES = new HashSet<>(Arrays.asList(OldGameState.ASSIGN_TEAMS, OldGameState.ENDED, OldGameState.ENDING, 
            OldGameState.ERROR, OldGameState.TELEPORT_START, OldGameState.SETTING_UP, OldGameState.SETUP_COMPLETE, OldGameState.UNDEFINED));
    
    public SpectateCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin.getNexusCore(), sender);
        if (senderRank.ordinal() > Rank.MEDIA.ordinal()) {
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
    
        Game game = sgPlayer.getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        
            if (INVALID_GAME_STATES.contains(game.getState())) {
                player.sendMessage(MsgType.WARN + "Invalid game state to use that command. Please wait to try again.");
                return true;
            }
        
            if (gamePlayer.getTeam() == GameTeam.TRIBUTES || gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                game.killPlayer(gamePlayer, new DeathInfo(game, System.currentTimeMillis(), gamePlayer, DeathType.SPECTATE));
            } else {
                player.sendMessage(MsgType.WARN + "You are already spectating the game.");
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
                    sender.sendMessage(ColorUtils.color(MsgType.INFO + "You will no longer be spectating the game"));
                } else {
                    lobby.addSpectatingPlayer(player.getUniqueId());
                    sender.sendMessage(ColorUtils.color(MsgType.INFO + "You will be spectating the game."));
                }
            } else {
                sender.sendMessage(MsgType.WARN + "Invalid lobby state. Can only be done before or during the game start countdown");
            }
        }
        return true;
    }
}
