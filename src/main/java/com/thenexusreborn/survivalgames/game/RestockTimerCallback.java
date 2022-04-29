package com.thenexusreborn.survivalgames.game;

import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;

public class RestockTimerCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    private final Game game;
    
    public RestockTimerCallback(Game game) {
        this.game = game;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (game == null) {
            return false;
        }
        
        if (!(game.getState() == GameState.INGAME_GRACEPERIOD || game.getState() == GameState.INGAME)) {
            return false;
        }
        
        if (timerSnapshot.getSecondsLeft() == 0) {
            game.restockChests();
            game.sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED");
            return false;
        }
        return true;
    }
}