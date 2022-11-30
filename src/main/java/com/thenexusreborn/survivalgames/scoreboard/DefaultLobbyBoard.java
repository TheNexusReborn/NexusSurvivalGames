package com.thenexusreborn.survivalgames.scoreboard;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.ChatColor;

@SuppressWarnings("DuplicatedCode")
public class DefaultLobbyBoard extends SpigotScoreboardView {
    
    private final SurvivalGames plugin;
    
    private final String mapLabelName = "mapLabel", mapValueName = "mapValue", blank1Name = "blank1",
            playersLabelName = "playersLabel", waitingValueName = "waitingValue", neededValueName = "neededValue", blank2Name = "blank2",
            serverLabelName = "serverLabel", serverValueName = "serverValue";
    
    public DefaultLobbyBoard(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard, "lobby", MCUtils.color("&a&lLobby"));
        this.plugin = plugin;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("mapLabel").entry("&6&lMAP:").score(15));
        createTeam(new TeamBuilder("mapValue").entry(ChatColor.AQUA.toString()).score(14).valueUpdater((player, team) -> {
            Lobby lobby = plugin.getLobby();
            if (lobby.getGameMap() != null) {
                GameMap map = lobby.getGameMap();
                String prefix = "&f", suffix = "&f";
                if (map.getName().length() > 14) {
                    prefix += map.getName().substring(0, 14);
                    suffix += map.getName().substring(14);
                } else {
                    prefix += map.getName();
                }
                team.setPrefix(MCUtils.color(prefix));
                team.setSuffix(MCUtils.color(suffix));
            } else {
                team.setPrefix(MCUtils.color("&7Voting"));
                team.setSuffix("");
            }
        }));
        createTeam(new TeamBuilder("blank1").entry(ChatColor.DARK_GRAY).score(13));
        createTeam(new TeamBuilder("playersLabel").entry("&6&lPLAYERS:").score(12));
        createTeam(new TeamBuilder("waitingValue").entry("&fWaiting: &e").score(11).valueUpdater((player, team) -> {
            Lobby lobby = plugin.getLobby();
            int waiting = 0;
            for (LobbyPlayer waitingPlayer : lobby.getPlayers()) {
                if (!waitingPlayer.isSpectating()) {
                    waiting++;
                }
            }
            team.setSuffix(waiting + "");
        }));
        createTeam(new TeamBuilder("neededValue").entry("&fNeeded: &e").score(10).valueUpdater((player, team) -> {
            Lobby lobby = plugin.getLobby();
            team.setSuffix(lobby.getLobbySettings().getMinPlayers() + "");
        }));
        createTeam(new TeamBuilder("blank2").entry(ChatColor.DARK_PURPLE).score(9));
        createTeam(new TeamBuilder("serverLabel").entry("&6&lSERVER:").score(2));
        createTeam(new TeamBuilder("serverValue").entry(NexusAPI.getApi().getServerManager().getCurrentServer().getName()).score(1));
    }
}