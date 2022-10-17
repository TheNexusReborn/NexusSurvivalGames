package com.thenexusreborn.survivalgames.scoreboard;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.ChatColor;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class LobbyScoreboardView extends SpigotScoreboardView {
    
    private final SurvivalGames plugin;
    
    private final String mapLabelName = "mapLabel", mapValueName = "mapValue", blank1Name = "blank1",
            playersLabelName = "playersLabel", waitingValueName = "waitingValue", neededValueName = "neededValue", blank2Name = "blank2",
            serverLabelName = "serverLabel", serverValueName = "serverValue";
    
    public LobbyScoreboardView(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard, "lobby", MCUtils.color("&a&lLobby"));
        this.plugin = plugin;
    }
    
    @Override
    public void registerTeams() {
        createTeam(mapLabelName, "&6&lMAP:", 15);
        createTeam(mapValueName, ChatColor.AQUA.toString(), "&7Voting", 14);
        createTeam(blank1Name, ChatColor.DARK_GRAY.toString(), 13);
        createTeam(playersLabelName, "&6&lPLAYERS:", 12);
        createTeam(waitingValueName, "&fWaiting: ", 11);
        createTeam(neededValueName, "&fNeeded: ", 10);
        createTeam(blank2Name, ChatColor.DARK_PURPLE.toString(), 9);
        createTeam(serverLabelName, "&6&lSERVER:", 2);
        createTeam(serverValueName, NexusAPI.getApi().getServerManager().getCurrentServer().getName(), 1);
    }
    
    @Override
    public void update() {
        if (plugin.getGame() != null) {
            return;
        }
        Lobby lobby = plugin.getLobby();
        NexusPlayer player = this.scoreboard.getPlayer();
        ITeam mapValue = scoreboard.getScoreboard().getTeam(mapValueName);
        if (lobby.getGameMap() != null) {
            GameMap map = lobby.getGameMap();
            String prefix = "&f", suffix = "&f";
            if (map.getName().length() > 14) {
                prefix += map.getName().substring(0, 14);
                suffix += map.getName().substring(14);
            } else {
                prefix += map.getName();
            }
            mapValue.setPrefix(MCUtils.color(prefix));
            mapValue.setSuffix(MCUtils.color(suffix));
        } else {
            mapValue.setPrefix(MCUtils.color("&7Voting"));
            mapValue.setSuffix("");
        }
        
        int waiting = 0;
        for (NexusPlayer waitingPlayer : lobby.getPlayers()) {
            if (!lobby.getSpectatingPlayers().contains(waitingPlayer.getUniqueId())) {
                if (!waitingPlayer.getToggles().getValue("vanish")) {
                    waiting++;
                }
            }
        }
        
        this.scoreboard.getScoreboard().getTeam(waitingValueName).setSuffix(MCUtils.color("&e" + waiting));
        if (lobby.getLobbySettings() != null) {
            this.scoreboard.getScoreboard().getTeam(neededValueName).setSuffix(MCUtils.color("&e" + lobby.getLobbySettings().getMinPlayers()));
        }
    }
    
    @Override
    public List<String> getTeams() {
        return Arrays.asList(mapLabelName, mapValueName, blank1Name, playersLabelName, waitingValueName, neededValueName, blank2Name, serverLabelName, serverValueName);
    }
}
