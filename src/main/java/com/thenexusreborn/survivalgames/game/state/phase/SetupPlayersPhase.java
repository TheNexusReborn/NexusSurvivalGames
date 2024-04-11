package com.thenexusreborn.survivalgames.game.state.phase;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.phase.setupplayers.AssignTeamsStep;
import com.thenexusreborn.survivalgames.game.state.phase.setupplayers.TeleportToMapStep;

public class SetupPlayersPhase extends GamePhase {
    public SetupPlayersPhase(Game game) {
        super(game, "setup_players_phase");
        GamePhaseStep assignTeams = addStep(new AssignTeamsStep(this));
        addStep(new TeleportToMapStep(this, assignTeams));
    }

    @Override
    public boolean requirementsMet() {
        return !checkPlayerCount();
    }
}
