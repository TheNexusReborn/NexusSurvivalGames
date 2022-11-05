package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.deathold.DeathInfoSpectate;
import com.thenexusreborn.survivalgames.lobby.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class SpectateCommand implements CommandExecutor {
    
    private final SurvivalGames plugin;
    
    private static final Set<GameState> INVALID_GAME_STATES = new HashSet<>(Arrays.asList(GameState.ASSIGN_TEAMS, GameState.ENDED, GameState.ENDING, 
            GameState.ERROR, GameState.TELEPORT_START, GameState.SETTING_UP, GameState.SETUP_COMPLETE, GameState.UNDEFINED));
    
    public SpectateCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin.getNexusCore(), sender);
        if (senderRank.ordinal() > Rank.MEDIA.ordinal()) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }
        
        Player player = (Player) sender; 
        
        Game game = plugin.getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        
            if (INVALID_GAME_STATES.contains(game.getState())) {
                player.sendMessage(MsgType.WARN + "Invalid game state to use that command. Please wait to try again.");
                return true;
            }
        
            if (gamePlayer.getTeam() == GameTeam.TRIBUTES || gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                game.killPlayer(gamePlayer.getUniqueId(), new DeathInfoSpectate(gamePlayer.getUniqueId(), gamePlayer.getTeam().getColor()));
            } else {
                player.sendMessage(MsgType.WARN + "You are already spectating the game.");
            }
        } else {
            Lobby lobby = plugin.getLobby();
            if (lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN) {
                boolean spectating = false;
                for (UUID spectatingPlayer : lobby.getSpectatingPlayers()) {
                    if (spectatingPlayer.equals(player.getUniqueId())) {
                        spectating = true;
                    }
                }
            
                if (spectating) {
                    lobby.removeSpectatingPlayer(player.getUniqueId());
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You will no longer be spectating the game"));
                } else {
                    lobby.addSpectatingPlayer(player.getUniqueId());
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You will be spectating the game."));
                }
            } else {
                sender.sendMessage(MsgType.WARN + "Invalid lobby state. Can only be done before or during the game start countdown");
            }
        }
        return true;
    }
}
