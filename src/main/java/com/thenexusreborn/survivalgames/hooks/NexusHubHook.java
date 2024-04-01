package com.thenexusreborn.survivalgames.hooks;

import com.thenexusreborn.hub.NexusHub;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.plugin.Plugin;

public class NexusHubHook {
    private final SurvivalGames survivalGames;
    private final NexusHub nexusHub;
    
    public NexusHubHook(SurvivalGames survivalGames, Plugin nexusHubPlugin) {
        this.survivalGames = survivalGames;
        this.nexusHub = (NexusHub) nexusHubPlugin;
    }
}