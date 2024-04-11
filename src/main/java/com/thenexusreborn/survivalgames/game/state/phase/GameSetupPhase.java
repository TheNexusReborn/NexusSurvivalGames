package com.thenexusreborn.survivalgames.game.state.phase;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.PhaseStatus;
import com.thenexusreborn.survivalgames.game.state.phase.setupgame.*;

public class GameSetupPhase extends GamePhase {
    
    public GameSetupPhase(Game game) {
        super(game, "game_setup");
        addStep(new SetScoreboardsStep(this));
        GamePhaseStep downloadMapStep = addStep(new DownloadMapStep(this));
        GamePhaseStep unzipMapStep = addStep(new UnzipMapStep(this, downloadMapStep));
        GamePhaseStep copyMapStep = addStep(new CopyMapStep(this, unzipMapStep));
        GamePhaseStep loadMapStep = addStep(new LoadMapStep(this, copyMapStep));
        addStep(new CalculateDMAreaStep(this, loadMapStep));
        addStep(new SetupSpawnsStep(this, loadMapStep));
        addStep(new WorldSetupStep(this, loadMapStep));
    }

    @Override
    public boolean requirementsMet() {
        return !checkPlayerCount();
    }

    public enum Status implements PhaseStatus {
        SETTING_SCOREBOARDS,
        DOWNLOADING_MAP,
        UNZIPPING_MAP,
        COPYING_MAP,
        LOADING_MAP,
        CALCULATE_DM_AREA,
        SETUP_SPAWNS,
        SET_GAMERULES,
    }
}