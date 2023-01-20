package com.thenexusreborn.survivalgames.scoreboard.newboards;

import com.starmediadev.starlib.TimeFormat;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.ChatColor;

public class DebugLobbyBoard extends SpigotScoreboardView {
    
    private SurvivalGames plugin;
    
    public DebugLobbyBoard(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard, "debuglobbyboard", MCUtils.color("&e&lDEBUG LOBBY"));
        this.plugin = plugin;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("controlTypeLabel").entry("&6&lControl Type:").score(15));
        createTeam(new TeamBuilder("controlTypeValye").entry(ChatColor.BLACK).score(14).valueUpdater((player, team) -> SGUtils.setTeamValueForString(plugin.getLobby().getControlType().name(), team)));
        createTeam(new TeamBuilder("stateLabel").entry("&6&lState:").score(13));
        createTeam(new TeamBuilder("stateValue").entry(ChatColor.DARK_BLUE).score(12).valueUpdater((player, team) -> SGUtils.setTeamValueForString(plugin.getLobby().getState().name(), team)));
        createTeam(new TeamBuilder("playersLabel").entry("&6&lPlayers:").score(11));
        createTeam(new TeamBuilder("waitingValue").entry("&fWaiting: &a").score(10).valueUpdater((player, team) -> team.setSuffix(plugin.getLobby().getPlayingCount() + "")));
        createTeam(new TeamBuilder("spectatingValue").entry("&fSpectating: &c").score(9).valueUpdater((player, team) -> team.setSuffix(plugin.getLobby().getSpectatingPlayers().size() + "")));
        if (scoreboard.getPlayer().getRank().ordinal() <= Rank.HELPER.ordinal()) {
            createTeam(new TeamBuilder("hiddenValue").entry("&fHidden: &b").score(8).valueUpdater((player, team) -> {
                int hidden = 0;
                for (LobbyPlayer lobbyPlayer : plugin.getLobby().getPlayers()) {
                    if (lobbyPlayer.getToggleValue("vanish")) {
                        hidden++;
                    }
                }
                team.setSuffix(hidden + "");
            }));
        }
        createTeam(new TeamBuilder("timeLabel").entry("&6&lTime:").score(7));
        createTeam(new TeamBuilder("timeValue").entry(ChatColor.DARK_GREEN).score(6).valueUpdater((player, team) -> {
            Timer timer = plugin.getLobby().getTimer();
            if (timer != null) {
                team.setSuffix("&f" + new TimeFormat("%*0m%%0s%").format(timer.getTimeLeft()));
            } else {
                team.setSuffix("&f0s");
            }
        }));
        createTeam(new TeamBuilder("mapLabel").entry("&6&lMAP:").score(5));
        createTeam(new TeamBuilder("mapValue").entry(ChatColor.AQUA.toString()).score(4).valueUpdater((player, team) -> {
            Lobby lobby = plugin.getLobby();
            if (lobby.getGameMap() != null) {
                SGUtils.setMapNameForScoreboard(lobby.getGameMap(), team);
            } else {
                team.setPrefix(MCUtils.color("&7None"));
                team.setSuffix("");
            }
        }));
    }
}
