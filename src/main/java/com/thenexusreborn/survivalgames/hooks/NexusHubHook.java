package com.thenexusreborn.survivalgames.hooks;

import com.thenexusreborn.hub.NexusHub;
import com.thenexusreborn.hub.api.ServerSelectEvent;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class NexusHubHook implements Listener {
    private final SurvivalGames survivalGames;
    private final NexusHub nexusHub;
    
    public NexusHubHook(SurvivalGames survivalGames, Plugin nexusHubPlugin) {
        this.survivalGames = survivalGames;
        this.nexusHub = (NexusHub) nexusHubPlugin;
    }
    
    @EventHandler
    public void onServerSelect(ServerSelectEvent e) {
        String serverName = e.getServerName();
        for (SGVirtualServer server : survivalGames.getServers()) {
            if (server.getName().equalsIgnoreCase(serverName)) {
                server.join(e.getNexusPlayer());
                break;
            }
        }
    }
}