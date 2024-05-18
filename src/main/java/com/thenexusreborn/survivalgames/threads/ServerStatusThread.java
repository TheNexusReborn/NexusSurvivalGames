package com.thenexusreborn.survivalgames.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

public class ServerStatusThread extends NexusThread<SurvivalGames> {
    
    private static final Gson gson = new GsonBuilder().create();
    
    public ServerStatusThread(SurvivalGames plugin) {
        super(plugin, 20L, 1L, false);
    }
    
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            server.setStatus("online");
            JsonObject stateObject = new JsonObject();
            Game game = server.getGame();
            Lobby lobby = server.getLobby();
            if (game != null) {
                stateObject.addProperty("type", "game");
                stateObject.addProperty("map", game.getGameMap().getName());
                stateObject.addProperty("control", game.getControlType().name());
                JsonObject playersObject = new JsonObject();
                playersObject.addProperty("tributes", game.getTeamCount(GameTeam.TRIBUTES));
                playersObject.addProperty("spectators", game.getTeamCount(GameTeam.SPECTATORS));
                playersObject.addProperty("mutations", game.getTeamCount(GameTeam.MUTATIONS));
                stateObject.add("players", playersObject);
                stateObject.addProperty("mode", game.getMode().name());
                stateObject.addProperty("state", game.getState().name()); //Replace with a JsonObject when new state system is fully implemented
                JsonObject timeObject = new JsonObject();
                timeObject.addProperty("main", game.getTimer() != null ? game.getTimer().getTime() : 0L);
                timeObject.addProperty("grace", game.getGraceperiodTimer() != null ? game.getGraceperiodTimer().getTime() : 0);
                stateObject.add("time", timeObject);
            } else if (lobby != null) {
                stateObject.addProperty("type", "lobby");
                stateObject.addProperty("map", lobby.getGameMap() != null ? lobby.getGameMap().getName() : "Voting");
                stateObject.addProperty("control", lobby.getControlType().name());
                stateObject.addProperty("state", lobby.getState().name());
                stateObject.addProperty("time", lobby.getTimer() != null ? TimeUnit.MILLISECONDS.toSeconds(lobby.getTimer().getTime()) : 0);
                JsonObject playersObject = new JsonObject();
                playersObject.addProperty("playing", lobby.getPlayingCount());
                playersObject.addProperty("spectating", lobby.getSpectatingPlayers().size());
                stateObject.add("players", playersObject);
            }
            server.setState(stateObject.toString());
        }
    }
}
