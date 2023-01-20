package com.thenexusreborn.survivalgames.scoreboard;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.ChatColor;

public class OldGameBoard extends SpigotScoreboardView {
    
    private final SurvivalGames plugin;
    
    public OldGameBoard(NexusScoreboard nexusScoreboard, SurvivalGames plugin) {
        super(nexusScoreboard, "survivalgames", MCUtils.color("&d&lSurvival Games"));
        this.plugin = plugin;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("mapLabel").entry("&6&lMAP:").score(15));
        createTeam(new TeamBuilder("mapValue").entry(ChatColor.AQUA.toString()).prefix("&f").score(14).valueUpdater((player, team) -> SGUtils.setMapNameForScoreboard(plugin.getGame().getGameMap(), team)));
        createTeam(new TeamBuilder("blank1").entry(ChatColor.BLACK.toString()).score(13));
        createTeam(new TeamBuilder("playersLabel").entry("&6&lPLAYERS:").score(12));
        
        ValueUpdater playerCountUpdater = (player, team) -> {
            Game game = plugin.getGame();
            int tributes = 0, watching = 0;
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (gamePlayer.getTeam() == GameTeam.TRIBUTES) {
                    tributes++;
                } else if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                    if (!gamePlayer.getToggleValue("vanish")) {
                        watching++;
                    }
                }
            }
            
            if (team.getName().equalsIgnoreCase("tributesValue")) {
                team.setSuffix("&a" + tributes);
            } else if (team.getName().equalsIgnoreCase("watchingValue")) {
                team.setSuffix("&c" + watching);
            }
        };
        
        createTeam(new TeamBuilder("tributesValue").entry("&fTributes: ").score(11).valueUpdater(playerCountUpdater));
        createTeam(new TeamBuilder("watchingValue").entry("&fWatching: ").score(10).valueUpdater(playerCountUpdater));
        createTeam(new TeamBuilder("blank2").entry(ChatColor.DARK_BLUE.toString()).score(9));
        createTeam(new TeamBuilder("infoLabel").entry("&6&lINFO:").score(8));
        createTeam(new TeamBuilder("scoreValue").entry("&fScore: ").score(7).valueUpdater((player, team) -> team.setSuffix("&e" + MCUtils.formatNumber(player.getStatValue("sg_score").getAsInt()))));
        createTeam(new TeamBuilder("passesValue").entry("&fPasses: ").score(6).valueUpdater((player, team) -> team.setSuffix("&e" + MCUtils.formatNumber(player.getStatValue("sg_mutation_passes").getAsInt()))));
        createTeam( new TeamBuilder("killstreakValue").entry("&fKS: ").score(5).valueUpdater((player, team) -> {
            Game game = plugin.getGame();
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            int killStreak = gamePlayer.getKillStreak();
            int hks = player.getStatValue("sg_highest_kill_streak").getAsInt();
            team.setSuffix("&e" + killStreak + "/" + hks);
        }));
        createTeam(new TeamBuilder("blank3").entry(ChatColor.DARK_PURPLE.toString()).score(4));
        createTeam(new TeamBuilder("serverName").entry("&6&lSERVER:").score(3));
        createTeam(new TeamBuilder("serverValue").entry("&f" + NexusAPI.getApi().getServerManager().getCurrentServer().getName()).score(2));
    }
}
