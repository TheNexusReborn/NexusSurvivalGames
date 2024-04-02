package com.thenexusreborn.survivalgames.registry;

import com.stardevllc.starlib.registry.IntegerRegistry;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;

public class GameRegistry extends IntegerRegistry<Game> {
    
    private SurvivalGames plugin;
    private int nextId = 0;

    public GameRegistry(SurvivalGames plugin) {
        super(Game::getLocalId);
        this.plugin = plugin;
    }

    @Override
    public void register(Game object) {
        if (object.getLocalId() == -1) {
            object.setLocalId(nextId++);
        }
        
        super.register(object);
    }
}