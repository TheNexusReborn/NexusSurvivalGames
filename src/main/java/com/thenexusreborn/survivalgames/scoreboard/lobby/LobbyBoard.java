package com.thenexusreborn.survivalgames.scoreboard.lobby;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TeamBuilder;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.ChatColor;

public class LobbyBoard extends SpigotScoreboardView {
    
    private final Lobby lobby;
    
    public LobbyBoard(NexusScoreboard scoreboard, Lobby lobby) {
        super(scoreboard, "lobby", StarColors.color("&a&lLobby"));
        this.lobby = lobby;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("mapLabel").entry("&6&lMAP:").score(15));
        createTeam(new TeamBuilder("mapValue").entry(ChatColor.AQUA.toString()).score(14).valueUpdater((player, team) -> {
            if (lobby.getGameMap() != null) {
                SGUtils.setMapNameForScoreboard(lobby.getGameMap(), team);
            } else {
                team.setPrefix(StarColors.color("&7Voting"));
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
        createTeam(new TeamBuilder("serverValue").entry("&f" + lobby.getServer().getName()));
    }
}