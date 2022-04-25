package com.thenexusreborn.survivalgames.game.timer;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.Mode;
import com.thenexusreborn.survivalgames.game.*;

public class NextGameTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private Game game;
    
    public NextGameTimerCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (game.getState() != GameState.ENDING) {
            return false;
        }
        
        if (timerSnapshot.getTimeLeft() <= 0) {
            if (Game.getMode() == Mode.AUTOMATIC) {
                game.nextGame();
            } else {
                game.sendMessage("&eThe next game timer has concluded, but the mode is not automatic. Skipped automatically performing next game tasks.");
            }
            return false;
        }
        
        return true;
    }
}
