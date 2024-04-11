package com.thenexusreborn.survivalgames.game.state.phase.setupgame;

import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import org.bukkit.Difficulty;

public class WorldSetupStep extends GamePhaseStep {
    public WorldSetupStep(GamePhase gamePhase, GamePhaseStep... prerequisiteSteps) {
        super(gamePhase, "world_setup", prerequisiteSteps);
    }

    @Override
    public boolean run() {
        setStatus(StepStatus.STARTING);
        try {
            SGMap gameMap = game.getGameMap();
            GameSettings settings = game.getSettings();
            gameMap.getWorld().setGameRuleValue("naturalRegeneration", "" + settings.isRegeneration());
            gameMap.getWorld().setGameRuleValue("doDaylightCycle", "" + settings.isTimeProgression());
            gameMap.getWorld().setGameRuleValue("doWeatherCycle", "" + settings.isWeatherProgression());
            gameMap.getWorld().setGameRuleValue("doMobSpawning", "false");
            gameMap.getWorld().setGameRuleValue("announceAdvancements", "false");
            gameMap.getWorld().setGameRuleValue("doFireTick", "false");
            gameMap.getWorld().setGameRuleValue("keepInventory", "false");
            gameMap.getWorld().setDifficulty(Difficulty.EASY);
        } catch (Exception e) {
            setStatus(StepStatus.ERROR);
            e.printStackTrace();
        }
        setStatus(StepStatus.COMPLETE);
        return true;
    }
}
