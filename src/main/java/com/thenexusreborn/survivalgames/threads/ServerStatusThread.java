package com.thenexusreborn.survivalgames.threads;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class ServerStatusThread extends NexusThread<SurvivalGames> {
    
    public ServerStatusThread(SurvivalGames plugin) {
        super(plugin, 20L, 1L, false);
    }
    
    public void onRun() {
        //TODO
    }
}
