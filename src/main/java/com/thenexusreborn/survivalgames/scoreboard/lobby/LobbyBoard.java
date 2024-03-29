package com.thenexusreborn.survivalgames.scoreboard.lobby;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.ChatColor;

public class LobbyBoard extends SpigotScoreboardView {
    
    private final Lobby lobby;
    
    public LobbyBoard(NexusScoreboard scoreboard, Lobby lobby) {
        super(scoreboard, "lobby", MCUtils.color("&a&lLobby"));
        this.lobby = lobby;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("mapLabel").entry("&6&lMAP:").score(15));
        createTeam(new TeamBuilder("mapValue").entry(ChatColor.AQUA.toString()).score(14).valueUpdater((player, team) -> {
            if (lobby.getGameMap() != null) {
                SGUtils.setMapNameForScoreboard(lobby.getGameMap(), team);
            } else {
                team.setPrefix(MCUtils.color("&7Voting"));
                team.setSuffix("");
            }
        }));
        createTeam(new TeamBuilder("blank1").entry(ChatColor.DARK_GRAY).score(13));
        createTeam(new TeamBuilder("playersLabel").entry("&6&lPLAYERS:").score(12));
        createTeam(new TeamBuilder("waitingValue").entry("&fWaiting: &e").score(11).valueUpdater((player, team) -> {
            int waiting = 0;
            for (LobbyPlayer waitingPlayer : lobby.getPlayers()) {
                if (!waitingPlayer.isSpectating()) {
                    waiting++;
                }
            }
            team.setSuffix(waiting + "");
        }));
        createTeam(new TeamBuilder("neededValue").entry("&fNeeded: &e").score(10).valueUpdater((player, team) -> team.setSuffix(lobby.getLobbySettings().getMinPlayers() + "")));
        createTeam(new TeamBuilder("blank2").entry(ChatColor.DARK_PURPLE).score(9));
        createTeam(new TeamBuilder("serverValue").entry(NexusAPI.getApi().getServerManager().getCurrentServer().getName()).score(1));
    }
}