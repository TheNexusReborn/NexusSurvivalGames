package com.thenexusreborn.survivalgames.registry;

import com.stardevllc.starlib.registry.IntegerRegistry;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;

public class LobbyRegistry extends IntegerRegistry<Lobby> {

    private SurvivalGames plugin;
    private int nextId = 0;

    public LobbyRegistry(SurvivalGames plugin) {
        super(Lobby::getLocalId);
        this.plugin = plugin;
    }

    @Override
    public void register(Lobby object) {
        if (object.getLocalId() == -1) {
            object.setLocalId(nextId++);
        }

        super.register(object);
    }
}