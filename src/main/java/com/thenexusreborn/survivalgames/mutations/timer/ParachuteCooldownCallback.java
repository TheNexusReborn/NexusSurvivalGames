package com.thenexusreborn.survivalgames.mutations.timer;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import org.bukkit.Bukkit;

public class ParachuteCooldownCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private ChickenMutation mutation;
    
    public ParachuteCooldownCallback(ChickenMutation mutation) {
        this.mutation = mutation;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (timerSnapshot.getSecondsLeft() == 0) {
            mutation.resetParachuteCooldown();
            Bukkit.getPlayer(mutation.getPlayer()).sendMessage(ColorUtils.color(MsgType.INFO + "Chicken Chute is ready!"));
            return false;
        }
        return true;
    }
}
