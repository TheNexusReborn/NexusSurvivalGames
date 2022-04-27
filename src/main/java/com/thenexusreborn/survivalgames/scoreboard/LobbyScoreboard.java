package com.thenexusreborn.survivalgames.scoreboard;

import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.wrapper.*;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class LobbyScoreboard extends SpigotScoreboardView {
    
    private SurvivalGames plugin;
    
    private final String modeLabelName = "modeLabel", modeValueName = "modeValue", blank1Name = "blank1", stateLabelName = "stateLabel", stateValueName = "stateValue", 
            blank2Name = "blank2", timeLabelName = "timeLabel", timeValueName = "timeValue", blank3Name = "blank3", playersLabelName = "playersLabel", 
    playersValueName = "playersValue", blank4name = "blank4", performanceLabelName = "performanceLabel", tpsValueName = "tpsValue", msptValueName = "msptValue";
    
    public LobbyScoreboard(NexusScoreboard nexusScoreboard, SurvivalGames plugin) {
        super(nexusScoreboard, "lobby", MCUtils.color("&6&lLobby"));
        this.plugin = plugin;
    }
    
    @Override
    public List<String> getTeams() {
        return Arrays.asList(modeLabelName, modeValueName, blank1Name, stateLabelName, stateValueName, blank2Name, timeLabelName, timeValueName, blank3Name, playersLabelName, playersValueName, blank4name, performanceLabelName, tpsValueName, msptValueName);
    }
    
    @Override
    public void registerTeams() {
        IScoreboard scoreboard = this.scoreboard.getScoreboard();
        ITeam modeLabel = scoreboard.registerNewTeam(modeLabelName);
        addEntry(objective, modeLabel, MCUtils.color("&6&lMode"), 15);
    
        ITeam modeValue = scoreboard.registerNewTeam(modeValueName);
        modeValue.setPrefix(MCUtils.color("&f"));
        addEntry(objective, modeValue, ChatColor.AQUA.toString(), 14);
    
        ITeam blank1 = scoreboard.registerNewTeam(blank1Name);
        addEntry(objective, blank1, ChatColor.BLACK.toString(), 13);
    
        ITeam stateLabel = scoreboard.registerNewTeam(stateLabelName);
        addEntry(objective, stateLabel, MCUtils.color("&6&lState"), 12);
    
        ITeam stateValue = scoreboard.registerNewTeam(stateValueName);
        stateValue.setPrefix(MCUtils.color("&f"));
        addEntry(objective, stateValue, ChatColor.YELLOW.toString(), 11);
    
        ITeam blank2 = scoreboard.registerNewTeam(blank2Name);
        addEntry(objective, blank2, ChatColor.BLUE.toString(), 10);
    
        ITeam secondsLabel = scoreboard.registerNewTeam(timeLabelName);
        addEntry(objective, secondsLabel, MCUtils.color("&6&lTime"), 9);
    
        ITeam secondsValue = scoreboard.registerNewTeam(timeValueName);
        secondsValue.setPrefix(MCUtils.color("&f0"));
        addEntry(objective, secondsValue, ChatColor.DARK_AQUA.toString(), 8);
    
        ITeam blank3 = scoreboard.registerNewTeam(blank3Name);
        addEntry(objective, blank3, ChatColor.DARK_BLUE.toString(), 7);
    
        ITeam playersLabel = scoreboard.registerNewTeam(playersLabelName);
        addEntry(objective, playersLabel, MCUtils.color("&6&lPlayers"), 6);
    
        ITeam playersValue = scoreboard.registerNewTeam(playersValueName);
        playersValue.setPrefix(MCUtils.color("&f0/0/0"));
        addEntry(objective, playersValue, ChatColor.DARK_GRAY.toString(), 5);
    
        ITeam blank4 = scoreboard.registerNewTeam(blank4name);
        addEntry(objective, blank4, ChatColor.DARK_RED.toString(), 4);
    
        ITeam performanceLabel = scoreboard.registerNewTeam(performanceLabelName);
        addEntry(objective, performanceLabel, MCUtils.color("&6&lPerformance"), 3);
    
        ITeam tpsValue = scoreboard.registerNewTeam(tpsValueName);
        tpsValue.setPrefix(MCUtils.color("&fTPS: "));
        addEntry(objective, tpsValue, ChatColor.GOLD.toString(), 2);
    
        ITeam msptValue = scoreboard.registerNewTeam(msptValueName);
        msptValue.setPrefix(MCUtils.color("&fMS Per Tick: "));
        addEntry(objective, msptValue, ChatColor.GREEN.toString(), 1);
    }
    
    @Override
    public void update() {
        if (plugin.getLobby() == null) {
            return;
        }
        
        Lobby lobby = plugin.getLobby();
        scoreboard.getScoreboard().getTeam(modeValueName).setPrefix(MCUtils.color("&f" + lobby.getMode().toString()));
        LobbyState state = lobby.getState();
        String prefix = "&f", suffix = "&f";
        if (state.name().length() > 14) {
            prefix += state.name().substring(0, 14);
            suffix += state.name().substring(14);
        } else {
            prefix += state.name();
        }
        ITeam stateValue = scoreboard.getScoreboard().getTeam(stateValueName);
        stateValue.setPrefix(MCUtils.color(prefix));
        stateValue.setSuffix(MCUtils.color(suffix));
    
        ITeam secondsValue = scoreboard.getScoreboard().getTeam(timeValueName);
        if (lobby.getTimer() != null) {
            secondsValue.setPrefix(MCUtils.color("&f" + Timer.formatTimeShort(lobby.getTimer().getSecondsLeft())));
        } else {
            secondsValue.setPrefix(MCUtils.color("&f0s"));
        }
        int playing = 0, max = lobby.getLobbySettings().getMaxPlayers(), spectating = 0;
        for (SpigotNexusPlayer player : new ArrayList<>(lobby.getPlayers())) {
            if (player.getPlayer() != null) {
                if (!lobby.getSpectatingPlayers().contains(player.getUniqueId())) {
                    playing++;
                } else {
                    spectating++;
                }
            }
        }
        scoreboard.getScoreboard().getTeam(playersValueName).setPrefix(MCUtils.color("&f" + playing + "/" + max + "/" + spectating));
        DecimalFormat numberFormat = new DecimalFormat("00.00");
        double tps = ((CraftServer) Bukkit.getServer()).getHandle().getServer().recentTps[0];
        scoreboard.getScoreboard().getTeam(tpsValueName).setSuffix(MCUtils.color("&f" + numberFormat.format(tps)));
        long nanoTime = 0;
        for (int i = MinecraftServer.getServer().h.length - 1; i > 0; i--) {
            if (MinecraftServer.getServer().h[i] != 0) {
                nanoTime = MinecraftServer.getServer().h[i];
                break;
            }
        }
        double tickTime = nanoTime / 1000000D;
        scoreboard.getScoreboard().getTeam(msptValueName).setSuffix(MCUtils.color("&f" + numberFormat.format(tickTime) + " ms"));
    }
}
