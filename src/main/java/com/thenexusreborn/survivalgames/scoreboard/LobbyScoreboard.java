package com.thenexusreborn.survivalgames.scoreboard;

import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class LobbyScoreboard extends ScoreboardView {
    
    private SurvivalGames plugin;
    
    private final String modeLabelName = "modeLabel", modeValueName = "modeValue", blank1Name = "blank1", stateLabelName = "stateLabel", stateValueName = "stateValue", 
            blank2Name = "blank2", timeLabelName = "timeLabel", timeValueName = "timeValue", blank3Name = "blank3", playersLabelName = "playersLabel", 
    playersValueName = "playersValue", blank4name = "blank4", performanceLabelName = "performanceLabel", tpsValueName = "tpsValue", msptValueName = "msptValue";
    
    public LobbyScoreboard(NexusScoreboard nexusScoreboard, SurvivalGames plugin) {
        super(nexusScoreboard);
        this.plugin = plugin;
    }
    
    @Override
    public List<String> getTeams() {
        return Arrays.asList(modeLabelName, modeValueName, blank1Name, stateLabelName, stateValueName, blank2Name, timeLabelName, timeValueName, blank3Name, playersLabelName, playersValueName, blank4name, performanceLabelName, tpsValueName, msptValueName);
    }
    
    @Override
    public void registerTeams(Scoreboard scoreboard, Objective objective) {
        Team modeLabel = scoreboard.registerNewTeam(modeLabelName);
        addEntry(objective, modeLabel, MCUtils.color("&6&lMODE"), 15);
        
        Team modeValue = scoreboard.registerNewTeam(modeValueName);
        modeValue.setPrefix(MCUtils.color("&f"));
        addEntry(objective, modeValue, ChatColor.AQUA.toString(), 14);
        
        Team blank1 = scoreboard.registerNewTeam(blank1Name);
        addEntry(objective, blank1, ChatColor.BLACK.toString(), 13);
    
        Team stateLabel = scoreboard.registerNewTeam(stateLabelName);
        addEntry(objective, stateLabel, MCUtils.color("&6&lState"), 12);
    
        Team stateValue = scoreboard.registerNewTeam(stateValueName);
        stateValue.setPrefix(MCUtils.color("&f"));
        addEntry(objective, stateValue, ChatColor.YELLOW.toString(), 11);
    
        Team blank2 = scoreboard.registerNewTeam(blank2Name);
        addEntry(objective, blank2, ChatColor.BLUE.toString(), 10);
    
        Team secondsLabel = scoreboard.registerNewTeam(timeLabelName);
        addEntry(objective, secondsLabel, MCUtils.color("&6&lTime"), 9);
    
        Team secondsValue = scoreboard.registerNewTeam(timeValueName);
        secondsValue.setPrefix(MCUtils.color("&f0"));
        addEntry(objective, secondsValue, ChatColor.DARK_AQUA.toString(), 8);
    
        Team blank3 = scoreboard.registerNewTeam(blank3Name);
        addEntry(objective, blank3, ChatColor.DARK_BLUE.toString(), 7);
    
        Team playersLabel = scoreboard.registerNewTeam(playersLabelName);
        addEntry(objective, playersLabel, MCUtils.color("&6&lPlayers"), 6);
    
        Team playersValue = scoreboard.registerNewTeam(playersValueName);
        playersValue.setPrefix(MCUtils.color("&f0/0/0"));
        addEntry(objective, playersValue, ChatColor.DARK_GRAY.toString(), 5);
        
        Team blank4 = scoreboard.registerNewTeam(blank4name);
        addEntry(objective, blank4, ChatColor.DARK_RED.toString(), 4);
        
        Team performanceLabel = scoreboard.registerNewTeam(performanceLabelName);
        addEntry(objective, performanceLabel, MCUtils.color("&6&lPerformance"), 3);
        
        Team tpsValue = scoreboard.registerNewTeam(tpsValueName);
        tpsValue.setPrefix(MCUtils.color("&fTPS: "));
        addEntry(objective, tpsValue, ChatColor.GOLD.toString(), 2);
        
        Team msptValue = scoreboard.registerNewTeam(msptValueName);
        msptValue.setPrefix(MCUtils.color("&fMS Per Tick: "));
        addEntry(objective, msptValue, ChatColor.GREEN.toString(), 1);
    }
    
    @Override
    public void update() {
        if (plugin.getLobby() == null) {
            return;
        }
        
        Lobby lobby = plugin.getLobby();
        scoreboard.getTeam(modeValueName).setPrefix(MCUtils.color("&f" + lobby.getMode().toString()));
        LobbyState state = lobby.getState();
        String prefix = "&f", suffix = "&f";
        if (state.name().length() > 14) {
            prefix += state.name().substring(0, 14);
            suffix += state.name().substring(14);
        } else {
            prefix += state.name();
        }
        Team stateValue = scoreboard.getTeam(stateValueName);
        stateValue.setPrefix(MCUtils.color(prefix));
        stateValue.setSuffix(MCUtils.color(suffix));
        
        Team secondsValue = scoreboard.getTeam(timeValueName);
        if (lobby.getTimer() != null) {
            secondsValue.setPrefix(MCUtils.color("&f" + Timer.formatTimeShort(lobby.getTimer().getSecondsLeft())));
        } else {
            secondsValue.setPrefix(MCUtils.color("&f0s"));
        }
        int playing = 0, max = lobby.getLobbySettings().getMaxPlayers(), spectating = 0;
        for (NexusPlayer player : new ArrayList<>(lobby.getPlayers())) {
            if (player.getPlayer() != null) {
                if (!lobby.getSpectatingPlayers().contains(player.getUniqueId())) {
                    playing++;
                } else {
                    spectating++;
                }
            }
        }
        scoreboard.getTeam(playersValueName).setPrefix(MCUtils.color("&f" + playing + "/" + max + "/" + spectating));
        DecimalFormat numberFormat = new DecimalFormat("00.00");
        double tps = ((CraftServer) Bukkit.getServer()).getHandle().getServer().recentTps[0];
        scoreboard.getTeam(tpsValueName).setSuffix(MCUtils.color("&f" + numberFormat.format(tps)));
        long nanoTime = 0;
        for (long l : MinecraftServer.getServer().h) {
            if (l > nanoTime) {
                nanoTime = l;
            }
        }
        double tickTime = nanoTime / 1000000D;
        scoreboard.getTeam(msptValueName).setSuffix(MCUtils.color("&f" + numberFormat.format(tickTime) + " ms"));
    }
    
    @Override
    public Objective registerObjective(Scoreboard scoreboard) {
        objective = scoreboard.registerNewObjective("lobby", "dummy");
        objective.setDisplayName(MCUtils.color("&6&lLobby"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return objective;
    }
}
