package com.thenexusreborn.survivalgames.game.state.phase.setupgame;

import com.stardevllc.starcore.utils.Cuboid;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;
import org.bukkit.Location;

public class CalculateDMAreaStep extends GamePhaseStep {
    public CalculateDMAreaStep(GamePhase gamePhase, GamePhaseStep... prerequisiteSteps) {
        super(gamePhase, "calculate_dm_area", prerequisiteSteps);
    }

    @Override
    public boolean run() {
        setStatus(StepStatus.STARTING);
        try {
            SGMap gameMap = game.getGameMap();
            int radius = gameMap.getDeathmatchBorderDistance();
            Location center = gameMap.getCenter().toLocation(gameMap.getWorld());
            Location corner1 = center.clone();
            corner1.add(radius, radius, radius);
            Location corner2 = center.clone();
            corner2.subtract(radius, radius, radius);
            gameMap.setDeathmatchArea(new Cuboid(corner1, corner2));
        } catch (Exception e) {
            setStatus(StepStatus.ERROR);
        }
        setStatus(StepStatus.COMPLETE);
        return true;
    }
}
