package com.thenexusreborn.survivalgames.scoreboard;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.wrapper.*;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.ChatColor;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class GameScoreboardView extends SpigotScoreboardView {
    
    private final SurvivalGames plugin;
    
    private final String mapLabelName = "mapLabel", mapValueName = "mapValue", blank1Name = "blank1", 
            playersLabelName = "playersLabel", tributesValueName = "tributesValue", watchingValueName = "watchingValue", 
            blank2Name = "blank2", infoLabelName = "infoLabel", scoreValueName = "scoreValue", passesValueName = "passesValue", killStreakValueName = "killstreakValue", 
            blank3Name = "blank3", serverLabelName = "serverLabel", serverValueName = "serverValue";
    
    public GameScoreboardView(NexusScoreboard nexusScoreboard, SurvivalGames plugin) {
        super(nexusScoreboard, "survivalgames", MCUtils.color("&d&lSurvival Games"));
        this.plugin = plugin;
    }
    
    @Override
    public List<String> getTeams() {
        return Arrays.asList(mapLabelName, mapValueName, blank1Name, playersLabelName, tributesValueName, watchingValueName, blank2Name, infoLabelName, scoreValueName, 
                passesValueName, killStreakValueName, blank3Name, serverLabelName, serverValueName);
    }
    
    @Override
    public void registerTeams() {
        createTeam(mapLabelName, "&6&lMAP:", 15);
        createTeam(mapValueName, ChatColor.AQUA.toString(), "&f", 14);
        createTeam(blank1Name, ChatColor.BLACK.toString(), 13);
        createTeam(playersLabelName, "&6&lPLAYERS:", 12);
        createTeam(tributesValueName, ChatColor.YELLOW.toString(), "&fTributes: ", 11);
        createTeam(watchingValueName, ChatColor.DARK_BLUE.toString(), "&fWatching: ", 10);
        createTeam(blank2Name, ChatColor.BLUE.toString(), 9);
        createTeam(infoLabelName, "&6&lINFO:", 8);    
        createTeam(scoreValueName, ChatColor.DARK_AQUA.toString(), "&fScore: ", 7);
        createTeam(passesValueName, ChatColor.LIGHT_PURPLE.toString(), "&fPasses: ", "&cWIP", 6);    
        createTeam(killStreakValueName, ChatColor.DARK_RED.toString(), "&fKS: ", "", 5);
        createTeam(blank3Name, ChatColor.DARK_PURPLE.toString(), 4);    
        createTeam(serverLabelName, "&6&lSERVER:", 3);
        createTeam(serverValueName, ChatColor.DARK_GRAY.toString(), "&f" + NexusAPI.getApi().getServerManager().getCurrentServer().getName(), 2);    
    }
    
    @Override
    public void update() {
        if (plugin.getGame() == null) {
            return;
        }
        
        try {
            Game game = plugin.getGame();
            GameMap map = game.getGameMap();
            String prefix = "&f", suffix = "&f";
            if (map.getName().length() > 14) {
                prefix += map.getName().substring(0, 14);
                suffix += map.getName().substring(14);
            } else {
                prefix += map.getName();
            }
            ITeam mapValue = scoreboard.getScoreboard().getTeam(mapValueName);
            mapValue.setPrefix(MCUtils.color(prefix));
            mapValue.setSuffix(MCUtils.color(suffix));
            
            int tributes = 0, watching = 0;
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (gamePlayer.getTeam() == GameTeam.TRIBUTES) {
                    tributes++;
                } else if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                    if (!gamePlayer.getNexusPlayer().getToggles().getValue("vanish")) {
                        watching++;
                    }
                }
            }
            
            scoreboard.getScoreboard().getTeam(tributesValueName).setSuffix(MCUtils.color("&a" + tributes));
            scoreboard.getScoreboard().getTeam(watchingValueName).setSuffix(MCUtils.color("&c" + watching));
            scoreboard.getScoreboard().getTeam(scoreValueName).setSuffix(MCUtils.color("&e" + MCUtils.formatNumber((int) this.scoreboard.getPlayer().getStats().getValue("sg_score"))));
            GamePlayer player = game.getPlayer(this.scoreboard.getPlayer().getUniqueId());
            int kills = player.getKills();
            int hks = (int) player.getNexusPlayer().getStats().getValue("sg_highest_kill_streak");
            scoreboard.getScoreboard().getTeam(killStreakValueName).setSuffix(MCUtils.color("&e" + kills + "/" + hks));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
