package com.thenexusreborn.survivalgames.threads;

import com.google.gson.*;
import com.stardevllc.helper.StringHelper;
import com.stardevllc.starcore.utils.StarThread;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;

public class ServerStatusThread extends StarThread<SurvivalGames> {
    
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
                stateObject.add("teamCounts", playersObject);
                stateObject.addProperty("mode", game.getMode().name());
                stateObject.addProperty("state", game.getState().name()); //Replace with a JsonObject when new state system is fully implemented
                JsonObject timeObject = new JsonObject();
                timeObject.addProperty("main", game.getTimer() != null ? game.getTimer().getTime() : 0L);
                timeObject.addProperty("grace", game.getGraceperiodTimer() != null ? game.getGraceperiodTimer().getTime() : 0);
                stateObject.add("time", timeObject);
                
                JsonArray playersArray = new JsonArray();
                for (GamePlayer player : game.getPlayers().values()) {
                    if (!player.getToggleValue("vanish") && !player.getToggleValue("incognito")) {
                        JsonObject playerObject = new JsonObject();
                        playerObject.addProperty("uuid", player.getUniqueId().toString());
                        playerObject.addProperty("name", player.getName());
                        playerObject.addProperty("team", StringHelper.titlize(player.getTeam().name()));
                        playersArray.add(playerObject);
                    }
                }
            } else if (lobby != null) {
                stateObject.addProperty("type", "lobby");
                stateObject.addProperty("map", lobby.getGameMap() != null ? lobby.getGameMap().getName() : "Voting");
                stateObject.addProperty("control", lobby.getControlType().name());
                stateObject.addProperty("state", lobby.getState().name());
                stateObject.addProperty("time", lobby.getTimer() != null ? TimeUnit.MILLISECONDS.toSeconds(lobby.getTimer().getTime()) : 0);
                JsonObject playersObject = new JsonObject();
                playersObject.addProperty("playing", lobby.getPlayingCount());
                playersObject.addProperty("spectating", lobby.getSpectatingPlayers().size());
                stateObject.add("playerCounts", playersObject);
                
                JsonArray playersArray = new JsonArray();
                for (LobbyPlayer player : lobby.getPlayers()) {
                    if (!player.getToggleValue("vanish") && !player.getToggleValue("incognito")) {
                        JsonObject playerObject = new JsonObject();
                        playerObject.addProperty("uuid", player.getUniqueId().toString());
                        playerObject.addProperty("name", player.getName());
                        playersArray.add(playerObject);
                    }
                }
                
                stateObject.add("players", playersArray);
            }
            server.setState(stateObject.toString());
        }
    }
}
