package com.thenexusreborn.survivalgames.scoreboard.lobby;

import com.stardevllc.clock.clocks.Timer;
import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.time.TimeFormat;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TeamBuilder;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.ChatColor;

public class DebugLobbyBoard extends SpigotScoreboardView {
    
    private final Lobby lobby;
    
    public DebugLobbyBoard(NexusScoreboard scoreboard, Lobby lobby) {
        super(scoreboard, "debuglobbyboard", ColorHandler.getInstance().color("&e&lDEBUG LOBBY"));
        this.lobby = lobby;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("controlTypeLabel").entry("&6&lControl Type:").score(15));
        createTeam(new TeamBuilder("controlTypeValye").entry(ChatColor.BLACK).score(14).valueUpdater((player, team) -> SGUtils.setTeamValueForString(lobby.getControlType().name(), team)));
        createTeam(new TeamBuilder("stateLabel").entry("&6&lState:").score(13));
        createTeam(new TeamBuilder("stateValue").entry(ChatColor.DARK_BLUE).score(12).valueUpdater((player, team) -> SGUtils.setTeamValueForString(lobby.getState().name(), team)));
        createTeam(new TeamBuilder("playersLabel").entry("&6&lPlayers:").score(11));
        createTeam(new TeamBuilder("waitingValue").entry("&fWaiting: &a").score(10).valueUpdater((player, team) -> team.setSuffix(lobby.getPlayingCount() + "")));
        createTeam(new TeamBuilder("spectatingValue").entry("&fSpectating: &c").score(9).valueUpdater((player, team) -> team.setSuffix(lobby.getSpectatingPlayers().size() + "")));
        if (scoreboard.getPlayer().getRank().ordinal() <= Rank.HELPER.ordinal()) {
            createTeam(new TeamBuilder("hiddenValue").entry("&fHidden: &b").score(8).valueUpdater((player, team) -> {
                int hidden = 0;
                for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                    if (lobbyPlayer.getToggleValue("vanish")) {
                        hidden++;
                    }
                }
                team.setSuffix(hidden + "");
            }));
        }
        createTeam(new TeamBuilder("timeLabel").entry("&6&lTime:").score(7));
        createTeam(new TeamBuilder("timeValue").entry(ChatColor.DARK_GREEN).score(6).valueUpdater((player, team) -> {
            Timer timer = lobby.getTimer();
            if (timer != null) {
                team.setSuffix("&f" + new TimeFormat("%*0m%%0s%").format(timer.getTime()));
            } else {
                team.setSuffix("&f0s");
            }
        }));
        createTeam(new TeamBuilder("mapLabel").entry("&6&lMAP:").score(5));
        createTeam(new TeamBuilder("mapValue").entry(ChatColor.AQUA.toString()).score(4).valueUpdater((player, team) -> {
            if (lobby.getGameMap() != null) {
                SGUtils.setMapNameForScoreboard(lobby.getGameMap(), team);
            } else {
                team.setPrefix(ColorHandler.getInstance().color("&7None"));
                team.setSuffix("");
            }
        }));
    }
}
