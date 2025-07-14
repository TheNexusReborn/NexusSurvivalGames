package com.thenexusreborn.survivalgames.scoreboard.lobby;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starmclib.Position;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TeamBuilder;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.ChatColor;

public class MapEditingBoard extends SpigotScoreboardView {
    
    private SurvivalGames plugin;
    
    public MapEditingBoard(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard, "mapeditingboard", StarColors.color("&a&lMap Editing"));
        this.plugin = plugin;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("mapLabel").entry("&6&lMAP:").score(15));
        createTeam(new TeamBuilder("mapValue").entry(ChatColor.DARK_PURPLE).score(14).valueUpdater((player, team) -> SGUtils.setMapNameForScoreboard(plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap(), team)));
        createTeam(new TeamBuilder("blank1").entry(ChatColor.DARK_BLUE).score(13));
        createTeam(new TeamBuilder("centerLabel").entry("&6&lCENTER:").score(12));
        createTeam(new TeamBuilder("centerValue").entry(ChatColor.AQUA).score(11).valueUpdater((player, team) -> {
            if (plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap() == null) {
                SGUtils.setTeamValueForString("&fNo Map Set", team);
            } else {
                Position center = plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap().getSpawnCenter();
                if (center != null) {
                    String centerText = center.getX() + "," + center.getY() + "," + center.getZ();
                    SGUtils.setTeamValueForString(centerText, team);
                } else {
                    SGUtils.setTeamValueForString("&fNot set", team);
                }
            }
        }));
        createTeam(new TeamBuilder("blank2").entry(ChatColor.GREEN).score(10));
        createTeam(new TeamBuilder("infoLabel").entry("&6&lINFO:").score(9));
        createTeam(new TeamBuilder("spawnCount").entry("&eTotal Spawns: &b").score(8).valueUpdater((player, team) -> {
            if (plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap() == null) {
                team.setSuffix("0");
            } else {
                team.setSuffix("" + plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap().getSpawns().size());
            }
        }));
        createTeam(new TeamBuilder("borderDistance").entry("&eArena Border Length: &b").score(7).valueUpdater((player, team) -> {
            if (plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap() == null) {
                team.setSuffix("0");
            } else {
                team.setSuffix("" + plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap().getArenaBorderLength());
            }
        }));
        createTeam(new TeamBuilder("dmBorderDistance").entry("&eDM Border Length: &b").score(6).valueUpdater((player, team) -> {
            if (plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap() == null) {
                team.setSuffix("0");
            } else {
                team.setSuffix("" + plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap().getDeathmatchBorderLength());
            }
        }));
        createTeam(new TeamBuilder("creatorCount").entry("&eTotal Creators: &b").score(5).valueUpdater((player, team) -> {
            if (plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap() == null) {
                team.setSuffix("0");
            } else {
                team.setSuffix("" + plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap().getCreators().size());
            }
        }));
        createTeam(new TeamBuilder("blank3").entry(ChatColor.RED).score(4));
        createTeam(new TeamBuilder("swagShackLabel").entry("&6&lSWAG SHACK:").score(3));
        createTeam(new TeamBuilder("swagShackValue").entry(ChatColor.DARK_AQUA).score(2).valueUpdater((player, team) -> {
            if (plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap() == null) {
                SGUtils.setTeamValueForString("&fNo Map Set", team);
            } else {
                Position swagShack = plugin.getPlayerRegistry().get(player.getUniqueId()).getLobby().getGameMap().getSwagShack();
                if (swagShack != null) {
                    String swagShackText = swagShack.getX() + "," + swagShack.getY() + "," + swagShack.getZ();
                    SGUtils.setTeamValueForString(swagShackText, team);
                } else {
                    SGUtils.setTeamValueForString("&fNot Set", team);
                }
            }
        }));
    }
}