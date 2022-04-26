package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.DeathInfoSpectate;
import com.thenexusreborn.survivalgames.lobby.*;

import java.util.*;

public class SpectateCommand extends NexusCommand {
    
    private SurvivalGames plugin;
    
    private static final Set<GameState> INVALID_GAME_STATES = new HashSet<>(Arrays.asList(GameState.ASSIGN_TEAMS, GameState.ENDED, GameState.ENDING, 
            GameState.ERROR, GameState.TELEPORT_START, GameState.SETTING_UP, GameState.SETUP_COMPLETE, GameState.UNDEFINED));
    
    public SpectateCommand(SurvivalGames plugin) {
        super("spectate", "Spectate a game", Rank.HELPER, true, false, new ArrayList<>());
        this.plugin = plugin;
    }
    
    @Override
    public void handleCommand(CommandActor actor, String label, String[] args) {
        Game game = plugin.getGame();
        if (game != null) {
            GamePlayer player = game.getPlayer(actor.getPlayer().getUniqueId());
            
            if (INVALID_GAME_STATES.contains(game.getState())) {
                actor.sendMessage("&cInvalid game state to use that command. Please wait to try again.");
                return;
            }
            
            if (player.getTeam() == GameTeam.TRIBUTES || player.getTeam() == GameTeam.MUTATIONS) {
                game.killPlayer(player.getUniqueId(), new DeathInfoSpectate(player.getUniqueId(), player.getTeam().getColor()));
            } else {
                actor.sendMessage("&cYou are already spectating the game.");
            }
        } else {
            Lobby lobby = plugin.getLobby();
            if (lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN) {
                boolean spectating = false;
                for (UUID spectatingPlayer : lobby.getSpectatingPlayers()) {
                    if (spectatingPlayer.toString().equalsIgnoreCase(actor.getPlayer().getUniqueId().toString())) {
                        spectating = true;
                    }
                }
                
                if (spectating) {
                    lobby.removeSpectatingPlayer(actor.getPlayer().getUniqueId());
                    actor.sendMessage("&eYou will no longer be spectating the game");
                } else {
                    lobby.addSpectatingPlayer(actor.getPlayer().getUniqueId());
                    actor.sendMessage("&eYou will be spectating the game.");
                }
            } else {
                actor.sendMessage("&cInvalid lobby state. Can only be done before or during the game start countdown");
            }
        }
    }
}
