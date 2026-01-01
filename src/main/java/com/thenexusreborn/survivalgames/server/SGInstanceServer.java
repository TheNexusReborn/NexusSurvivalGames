package com.thenexusreborn.survivalgames.server;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.survivalgames.SurvivalGames;

import java.util.UUID;

public class SGInstanceServer extends InstanceServer {
    
    private SurvivalGames plugin;
    
    public SGInstanceServer(SurvivalGames plugin, String name) {
        super(name, "survivalgames", 24);
        this.plugin = plugin;
        
        this.primaryVirtualServer.set(new SGVirtualServer(plugin, this, name));
        plugin.getServers().register(1, (SGVirtualServer) this.primaryVirtualServer.get());
    }

    @Override
    public void join(NexusPlayer nexusPlayer) {
        this.primaryVirtualServer.get().join(nexusPlayer);
    }

    @Override
    public void quit(NexusPlayer nexusPlayer) {
        this.primaryVirtualServer.get().quit(nexusPlayer);
    }
    
    @Override
    public void quit(UUID uuid) {
        this.primaryVirtualServer.get().quit(uuid);
    }

    @Override
    public void onStart() {
        this.primaryVirtualServer.get().onStart();
    }

    @Override
    public void onStop() {
        this.primaryVirtualServer.get().onStop();
    }
}
