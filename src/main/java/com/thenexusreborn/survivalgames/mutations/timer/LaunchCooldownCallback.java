package com.thenexusreborn.survivalgames.mutations.timer;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import org.bukkit.Bukkit;

public class LaunchCooldownCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private ChickenMutation mutation;
    
    public LaunchCooldownCallback(ChickenMutation mutation) {
        this.mutation = mutation;
    }
    
    @Override
    public Boolean callback(TimerSnapshot timerSnapshot) {
        if (timerSnapshot.getSecondsLeft() == 0) {
            mutation.resetLaunchCooldown();
            Bukkit.getPlayer(mutation.getPlayer()).sendMessage(ColorUtils.color(MsgType.INFO + "Chicken Launch is ready!"));
            return false;
        }
        return true;
    }
}
