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
            e.setServer(new SGInstanceServer(plugin, "SG")); //TODO Name from somewhere
            return;
        }
        
        if (plugin.getNexusHubHook() == null) {
            e.setServer(new SGInstanceServer(plugin, "SG"));
        } else {
            SGVirtualServer sg1 = new SGVirtualServer(plugin, "SG1");
            e.setPrimaryVirtualServer(sg1);
            e.addVirtualServer(sg1);
            plugin.getServers().register(1, sg1);
            for (int i = 1; i < numberOfServers; i++) {
                SGVirtualServer server = new SGVirtualServer(plugin, "SG" + (i + 1));
                e.addVirtualServer(server);
                plugin.getServers().register(i + 1, server);
            }
        }
    }
}
