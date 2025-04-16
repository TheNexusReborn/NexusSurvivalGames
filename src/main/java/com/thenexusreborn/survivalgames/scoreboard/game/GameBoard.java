package com.thenexusreborn.survivalgames.scoreboard.game;

import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TeamBuilder;
import com.thenexusreborn.api.scoreboard.ValueUpdater;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.Mode;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class GameBoard extends SpigotScoreboardView {
    
    private final SurvivalGames plugin;
    
    public GameBoard(NexusScoreboard nexusScoreboard, SurvivalGames plugin) {
        super(nexusScoreboard, "survivalgames", StarColors.color("&d&lSurvival Games"));
        this.plugin = plugin;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("dateValue").entry(ChatColor.GOLD).score(15).valueUpdater((player, team) -> {
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
            team.setSuffix("&7" + df.format(System.currentTimeMillis()));
        }));
        createTeam(new TeamBuilder("mapLabel").entry("&6&lMAP:").score(14));
        createTeam(new TeamBuilder("mapValue").entry(ChatColor.AQUA.toString()).prefix("&f").score(13).valueUpdater((player, team) -> SGUtils.setMapNameForScoreboard(plugin.getPlayerRegistry().get(player.getUniqueId()).getGame().getGameMap(), team)));
        createTeam(new TeamBuilder("blank2").entry(ChatColor.BLACK.toString()).score(12));
        
        createTeam(new TeamBuilder("playersLabel").entry("&6&lPLAYERS:").score(11));
        ValueUpdater playerCountUpdater = (player, team) -> {
            Game game = plugin.getPlayerRegistry().get(player.getUniqueId()).getGame();
            int tributes = 0, spectators = 0;
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (gamePlayer.getTeam() == GameTeam.TRIBUTES) {
                    tributes++;
                } else if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                    if (!gamePlayer.getToggleValue("vanish")) {
                        spectators++;
                    }
                }
            }
            
            if (team.getName().equalsIgnoreCase("tributesValue")) {
                team.setSuffix("&a" + tributes);
            } else if (team.getName().equalsIgnoreCase("spectatorsValue")) {
                team.setSuffix("&c" + spectators);
            }
        };
        
        ValueUpdater undeadCountUpdater = (player, team) -> {
            Game game = plugin.getPlayerRegistry().get(player.getUniqueId()).getGame();
            int mutations = game.getTeamCount(GameTeam.MUTATIONS);
            int zombies = game.getTeamCount(GameTeam.ZOMBIES);
            if (game.getMode() == Mode.CLASSIC) {
                team.setPrefix("&fMutations: ");
                team.setSuffix("&d" + mutations);
            } else if (game.getMode() == Mode.UNDEAD) {
                team.setPrefix("&fZombies: ");
                team.setSuffix("&2" + zombies);
            } 
        };
        
        createTeam(new TeamBuilder("tributesValue").entry("&fTributes: ").score(10).valueUpdater(playerCountUpdater));
        createTeam(new TeamBuilder("spectatorsValue").entry("&fSpectators: ").score(9).valueUpdater(playerCountUpdater));
        createTeam(new TeamBuilder("undeadValue").entry(ChatColor.RED).score(8).valueUpdater(undeadCountUpdater));
        createTeam(new TeamBuilder("blank3").entry(ChatColor.DARK_BLUE.toString()).score(7));
        createTeam(new TeamBuilder("infoLabel").entry("&6&lINFO:").score(6));
        createTeam(new TeamBuilder("scoreValue").entry("&fScore: ").score(5).valueUpdater((player, team) -> team.setSuffix("&e" + plugin.getPlayerRegistry().get(player.getUniqueId()).getStats().getScore())));
        createTeam(new TeamBuilder("killsValue").entry("&fKills: ").score(4).valueUpdater((player, team) -> {
            Game game = plugin.getPlayerRegistry().get(player.getUniqueId()).getGame();
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            int killStreak = gamePlayer.getKillStreak();
            int hks = gamePlayer.getStats().getHighestKillstreak();
            team.setSuffix("&e" + killStreak + "/" + hks);
        }));
        createTeam(new TeamBuilder("assistsValue").entry("&fAssists: ").score(3).valueUpdater((player, team) -> {
            Game game = plugin.getPlayerRegistry().get(player.getUniqueId()).getGame();
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (game.getSettings().isAllowAssists()) {
                team.setSuffix("&e" + gamePlayer.getAssists());
            } 
        }));
        createTeam(new TeamBuilder("blank4").entry(ChatColor.DARK_PURPLE.toString()).score(2));
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(scoreboard.getPlayer().getUniqueId());
        if (sgPlayer == null) {
            return;
        }
        
        if (sgPlayer.getGame() == null) {
            return;
        }
        createTeam(new TeamBuilder("serverValue").prefix("&6&lSERVER: ").entry("&f" + sgPlayer.getGame().getServer().getName()));
    }
}
