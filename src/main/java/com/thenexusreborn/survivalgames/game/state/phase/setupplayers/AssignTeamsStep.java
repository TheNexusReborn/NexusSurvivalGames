package com.thenexusreborn.survivalgames.game.state.phase.setupplayers;

import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;

import java.util.*;

public class AssignTeamsStep extends GamePhaseStep {
    public AssignTeamsStep(GamePhase gamePhase, GamePhaseStep... prerequisiteSteps) {
        super(gamePhase, "assign_teams", prerequisiteSteps);
    }

    @Override
    public boolean requirementsMet() {
        return !gamePhase.checkPlayerCount();
    }

    @Override
    public boolean tryMeetRequirements() {
        return requirementsMet();
    }

    @Override
    public boolean run() {
        try {
            setStatus(StepStatus.STARTING);
            
            SortedSet<GamePlayer> players = new TreeSet<>((player, other) -> {
                SGPlayer sgPlayer = SurvivalGames.getInstance().getPlayerRegistry().get(player.getUniqueId());
                SGPlayer otherPlayer = SurvivalGames.getInstance().getPlayerRegistry().get(other.getUniqueId());
                return Long.compare(otherPlayer.getJoinTime(), sgPlayer.getJoinTime());
            });
            
            players.addAll(game.getPlayers().values());
            
            List<UUID> tributes = new LinkedList<>();
            for (GamePlayer player : players) {
                if (player.getTeam() == null) {
                    if (tributes.size() >= game.getGameMap().getSpawns().size()) {
                        player.setTeam(GameTeam.SPECTATORS);
                    } else {
                        player.setTeam(GameTeam.TRIBUTES);
                        tributes.add(player.getUniqueId());
                    }
                }
            }
        } catch (Exception e) {
            setStatus(StepStatus.ERROR);
            e.printStackTrace();
            return false;
        }
        
        setStatus(StepStatus.COMPLETE);
        return true;
    }
}
