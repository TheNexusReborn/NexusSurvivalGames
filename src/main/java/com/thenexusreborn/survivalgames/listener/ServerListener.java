package com.thenexusreborn.survivalgames.listener;

import com.thenexusreborn.api.util.NetworkType;
import com.thenexusreborn.nexuscore.api.events.NexusServerSetupEvent;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.server.SGInstanceServer;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerListener implements Listener {
    
    private SurvivalGames plugin;
    private int numberOfServers;

    public ServerListener(SurvivalGames plugin) {
        this.plugin = plugin;
        this.numberOfServers = plugin.getConfig().getInt("numberofservers");
        if (this.numberOfServers == 0) {
            this.numberOfServers = 5;
        }
    }
    
    @EventHandler
    public void onServerSetup(NexusServerSetupEvent e) {
        if (e.getNetworkType() == NetworkType.MULTI) {
            e.setServer(new SGInstanceServer("SG")); //TODO Name from somewhere
            return;
        }
        
        if (plugin.getNexusHubHook() == null) {
            e.setServer(new SGInstanceServer("SG"));
        } else {
            for (int i = 0; i < numberOfServers; i++) {
                e.addVirtualServer(new SGVirtualServer("SG" + (i+1)));
            }
        }
    }
}
