package com.thenexusreborn.survivalgames.server;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.survivalgames.SurvivalGames;

public class SGInstanceServer extends InstanceServer {
    
    private SurvivalGames plugin;
    
    public SGInstanceServer(SurvivalGames plugin, String name) {
        super(name, "survivalgames", 24);
        this.plugin = plugin;
        
        this.primaryVirtualServer = new SGVirtualServer(plugin, this, name);
        plugin.getServers().register(1, (SGVirtualServer) this.primaryVirtualServer);
    }

    @Override
    public void join(NexusPlayer nexusPlayer) {
        this.primaryVirtualServer.join(nexusPlayer);
    }

    @Override
    public void quit(NexusPlayer nexusPlayer) {
        this.primaryVirtualServer.quit(nexusPlayer);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
