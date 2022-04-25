package com.thenexusreborn.survivalgames.cmd.arg;

import com.thenexusreborn.nexuscore.util.command.Argument;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.map.GameMap;

public class MapNameArgument extends Argument {
    public MapNameArgument() {
        super("mapIdentifier", true, "You must provide either a map name or a map file name");
    
        for (GameMap map : SurvivalGames.getPlugin(SurvivalGames.class).getMapManager().getMaps()) {
            getCompletions().add(map.getName().toLowerCase().replace(" ", "_").replace("'", ""));
            getCompletions().add(map.getFileName().toLowerCase());
        }
    }
}
