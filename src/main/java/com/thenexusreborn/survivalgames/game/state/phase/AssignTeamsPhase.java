package com.thenexusreborn.survivalgames.game.state.phase;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.PhaseStatus;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class AssignTeamsPhase extends GamePhase {
    public AssignTeamsPhase(Game game) {
        super(game, "Assign Starting Teams");
    }

    @Override
    public void beginphase() {
        try {
            if (checkPlayerCount()) {
                return;
            }
            setStatus(Status.SET_TEAMS);
            UUID uuid;
            Queue<UUID> tributes = new LinkedList<>(), spectators = new LinkedList<>();
            while ((uuid = SurvivalGames.PLAYER_QUEUE.poll()) != null) {
                GamePlayer player = game.getPlayers().get(uuid);
                if (player == null) {
                    continue;
                }
                if (player.getTeam() != null) {
                    if (player.getTeam() == GameTeam.SPECTATORS) {
                        spectators.offer(player.getUniqueId());
                    }
                } else {
                    if (tributes.size() >= game.getGameMap().getSpawns().size()) {
                        player.setTeam(GameTeam.SPECTATORS);
                        spectators.offer(uuid);
                    } else {
                        player.setTeam(GameTeam.TRIBUTES);
                        tributes.offer(uuid);
                    }
                }

                player.sendMessage(player.getTeam().getJoinMessage());
            }

            if (checkPlayerCount()) {
                return;
            }
            setStatus(Status.OFFER_SPECTATORS);
            UUID spectator;
            while ((spectator = spectators.poll()) != null) {
                SurvivalGames.PLAYER_QUEUE.offer(spectator);
            }

            if (checkPlayerCount()) {
                return;
            }
            setStatus(Status.OFFER_TRIBUTES);
            UUID tribute;
            while ((tribute = tributes.poll()) != null) {
                SurvivalGames.PLAYER_QUEUE.offer(tribute);
            }

            if (checkPlayerCount()) {
                return;
            }

            if (checkPlayerCount()) {
                return;
            }
            setStatus(PhaseStatus.COMPLETE);
            game.setState(GameState.TEAMS_ASSIGNED);
        } catch (Exception e) {
            e.printStackTrace();
            game.handleError("There was an error assiging teams.");
        }
    }
    
    public enum Status implements PhaseStatus {
        OFFER_SPECTATORS, OFFER_TRIBUTES, SET_TEAMS
    }
}
