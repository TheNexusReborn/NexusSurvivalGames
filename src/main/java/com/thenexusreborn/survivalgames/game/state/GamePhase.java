package com.thenexusreborn.survivalgames.game.state;

import com.stardevllc.starclock.clocks.Timer;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.OldGameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class GamePhase {
    protected Game game;
    protected String name;
    protected PhaseStatus status = PhaseStatus.STARTING;
    
    //A status is only considered completed when it is replaced by a new status. The setStatus method handles this automatically.
    protected Map<PhaseStatus, Long> completedStatuses = new HashMap<>();
    
    public GamePhase(Game game, String name) {
        this.game = game;
        this.name = name;
    }

    /**
     * Called before the phase begins to check and/or setup the necessary things needed for the phase
     */
    public void prephase() {
        
    }

    /**
     * The main function of phase, this is where the the processing of the phase is started and the status is updated here.
     */
    public abstract void beginphase();

    /**
     * 
     */
    public void postphase() {
        
    }

    public PhaseStatus getStatus() {
        return status;
    }

    public void setStatus(PhaseStatus status) {
        this.completedStatuses.put(this.status, System.currentTimeMillis());
        this.status = status;
    }
    
    protected boolean checkPlayerCount() {
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
        return this.status == PhaseStatus.COMPLETE;
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

    @Override
    public String toString() {
        return "GamePhase{" +
                "name='" + name + '\'' +
                ", status=" + status.toString() +
                '}';
    }
}