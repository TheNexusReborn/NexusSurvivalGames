package com.thenexusreborn.survivalgames.server;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.server.InstanceServer;

public class SGInstanceServer extends InstanceServer {
    public SGInstanceServer(String name) {
        super(name, "survivalgames");
    }

    @Override
    public void join(NexusPlayer nexusPlayer) {
        
    }

    @Override
    public void quit(NexusPlayer nexusPlayer) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
