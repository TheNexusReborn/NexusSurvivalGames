package com.thenexusreborn.survivalgames.server;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.api.server.VirtualServer;

public class SGVirtualServer extends VirtualServer {
    public SGVirtualServer(InstanceServer parent, String name) {
        super(parent, name, "survivalgames");
    }

    public SGVirtualServer(String name) {
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
