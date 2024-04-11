package com.thenexusreborn.survivalgames.game.state;

import com.stardevllc.starclock.clocks.Timer;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.OldGameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class GamePhase {
    protected Game game;
    protected String name;
    protected PhaseStatus status = PhaseStatus.STARTING;
    protected int currentStepIndex = 0;
    protected List<GamePhaseStep> steps = new LinkedList<>();
    
    //A status is only considered completed when it is replaced by a new status. The setStatus method handles this automatically.
    protected Map<PhaseStatus, Long> completedStatuses = new HashMap<>();
    
    public GamePhase(Game game, String name) {
        this.game = game;
        this.name = name;
    }
    
    public boolean step() {
        if (currentStepIndex >= steps.size()) {
            setStatus(PhaseStatus.COMPLETE); //At the end of the steps, phase complete
            return false;
        }
        
        GamePhaseStep step = steps.get(currentStepIndex); //This should never be null due to the check above.
        if (!step.requirementsMet()) { //Requirements not met for the step
            if (!step.tryMeetRequirements()) { //tells the step to try to make it so that requirements are met
                return false; //Could not make requirements meet, step failed to start.
            }
        }

        if (!step.setup()) {
            return false; //Step failed to setup
        }

        if (!step.run()) {
            return false; //Step failed to run
        }

        if (!step.cleanup()) {
            return false; //Step failed to cleanup
        }
        
        currentStepIndex++; //Success
        return true;
    }
    
    public boolean requirementsMet() {
        return true;
    }
    
    public boolean setup() {
        return true;
    }
    
    public boolean run() {
        for (int i = 0; i < steps.size(); i++) {
            boolean stepSuccess = step();
            if (!stepSuccess) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean cleanup() {
        return true;
    }

    public PhaseStatus getStatus() {
        return status;
    }

    public void setStatus(PhaseStatus status) {
        this.completedStatuses.put(this.status, System.currentTimeMillis());
        this.status = status;
    }
    
    public boolean checkPlayerCount() {
        if (game.getPlayers().size() <= 1) {
            game.setState(OldGameState.ENDING);
            game.getServer().getLobby().fromGame(game);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(MsgType.WARN + "Detected only one player left in the game, resetting back to lobby.");
            }
            return true;
        }
        
        return false;
    }
    
    //Can be overridden to have custom logic if needed.
    public boolean isComplete() {
        for (GamePhaseStep step : this.steps) {
            if (!step.isCompleted()) {
                return false;
            }
        }
        
        if (this.status == null) {
            return false;
        }
        
        return this.status.equals(PhaseStatus.COMPLETE);
    }

    public boolean tryMeetRequirements() {
        return true;
    }

    public Game getGame() {
        return game;
    }

    public String getName() {
        return name;
    }
    
    public Timer getTimer() {
        return null;
    }
    
    public <T extends GamePhaseStep> T addStep(T step) {
        this.steps.add(step);
        return step;
    }

    @Override
    public String toString() {
        return "GamePhase{" +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", completedStatuses=" + completedStatuses +
                '}';
    }
}