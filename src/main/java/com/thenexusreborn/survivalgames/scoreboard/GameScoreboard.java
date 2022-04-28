package com.thenexusreborn.survivalgames.scoreboard;

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
public class GameScoreboard extends SpigotScoreboardView {
    
    private SurvivalGames plugin;
    
    private final String mapLabelName = "mapLabel", mapValueName = "mapValue", blank1Name = "blank1", 
            playersLabelName = "playersLabel", tributesValueName = "tributesValue", watchingValueName = "watchingValue", 
            blank2Name = "blank2", infoLabelName = "infoLabel", scoreValueName = "scoreValue", passesValueName = "passesValue", killStreakValueName = "killstreakValue", 
            blank3Name = "blank3", serverLabelName = "serverLabel", serverValueName = "serverValue";
    
    public GameScoreboard(NexusScoreboard nexusScoreboard, SurvivalGames plugin) {
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
        IScoreboard scoreboard = this.scoreboard.getScoreboard();
        ITeam mapLabel = scoreboard.registerNewTeam(mapLabelName);
        addEntry(objective, mapLabel, MCUtils.color("&6&lMAP:"), 15);
    
        ITeam mapValue = scoreboard.registerNewTeam(mapValueName);
        mapValue.setPrefix(MCUtils.color("&f"));
        addEntry(objective, mapValue, ChatColor.AQUA.toString(), 14);
    
        ITeam blank1 = scoreboard.registerNewTeam(blank1Name);
        addEntry(objective, blank1, ChatColor.BLACK.toString(), 13);
    
        ITeam stateLabel = scoreboard.registerNewTeam(playersLabelName);
        addEntry(objective, stateLabel, MCUtils.color("&6&lPLAYERS:"), 12);
    
        ITeam tributesValue = scoreboard.registerNewTeam(tributesValueName);
        tributesValue.setPrefix(MCUtils.color("&fTributes: "));
        addEntry(objective, tributesValue, ChatColor.YELLOW.toString(), 11);
    
        ITeam watchingValue = scoreboard.registerNewTeam(watchingValueName);
        watchingValue.setPrefix(MCUtils.color("&fWatching: "));
        addEntry(objective, watchingValue, ChatColor.DARK_PURPLE.toString(), 10);
    
        ITeam blank2 = scoreboard.registerNewTeam(blank2Name);
        addEntry(objective, blank2, ChatColor.BLUE.toString(), 9);
    
        ITeam infoLabel = scoreboard.registerNewTeam(infoLabelName);
        addEntry(objective, infoLabel, MCUtils.color("&6&lINFO:"), 8);
    
        ITeam scoreValue = scoreboard.registerNewTeam(scoreValueName);
        scoreValue.setPrefix(MCUtils.color("&fScore: "));
        addEntry(objective, scoreValue, ChatColor.DARK_AQUA.toString(), 7);
    
        ITeam passesValue = scoreboard.registerNewTeam(passesValueName);
        passesValue.setPrefix(MCUtils.color("&fPasses: "));
        passesValue.setSuffix(MCUtils.color("&cWIP"));
        addEntry(objective, passesValue, ChatColor.LIGHT_PURPLE.toString(), 6);
    
        ITeam killStreakValue = scoreboard.registerNewTeam(killStreakValueName);
        killStreakValue.setPrefix(MCUtils.color("&fKS: "));
        killStreakValue.setSuffix(MCUtils.color("&cWIP"));
        addEntry(objective, killStreakValue, ChatColor.DARK_RED.toString(), 5);
    
        ITeam blank3 = scoreboard.registerNewTeam(blank3Name);
        addEntry(objective, blank3, ChatColor.DARK_BLUE.toString(), 4);
    
        ITeam serverLabel = scoreboard.registerNewTeam(serverLabelName);
        addEntry(objective, serverLabel, MCUtils.color("&6&lSERVER:"), 3);
    
        ITeam serverValue = scoreboard.registerNewTeam(serverValueName);
        serverValue.setPrefix(MCUtils.color("&f" + plugin.getNexusCore().getConfig().getString("serverName")));
        addEntry(objective, serverValue, ChatColor.DARK_GRAY.toString(), 2);
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
                    watching++;
                }
            }
            
            scoreboard.getScoreboard().getTeam(tributesValueName).setSuffix(MCUtils.color("&a" + tributes));
            scoreboard.getScoreboard().getTeam(watchingValueName).setSuffix(MCUtils.color("&c" + watching));
            scoreboard.getScoreboard().getTeam(scoreValueName).setSuffix(MCUtils.color("&e" + MCUtils.formatNumber(this.scoreboard.getPlayer().getStatValue("sg_score"))));
        } catch (Exception e) {}
    }
}
