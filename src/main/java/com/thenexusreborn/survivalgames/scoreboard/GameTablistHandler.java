package com.thenexusreborn.survivalgames.scoreboard;

import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.logging.Logger;

public class GameTablistHandler extends TablistHandler {
    
    private static final Map<GameTeam, String> BEGIN_CHARS = new EnumMap<>(GameTeam.class);
    
    static {
        BEGIN_CHARS.put(GameTeam.TRIBUTES, "a");
        BEGIN_CHARS.put(GameTeam.MUTATIONS, "b");
        BEGIN_CHARS.put(GameTeam.ZOMBIES, "c");
        BEGIN_CHARS.put(GameTeam.SPECTATORS, "d");
    }
    
    private SurvivalGames plugin;
    
    public GameTablistHandler(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard);
        this.plugin = plugin;
    }
    
    @Override
    public void update() {
        removeDisconnectedPlayers();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer != null) {
                GamePlayer gamePlayer = plugin.getPlayerRegistry().get(player.getUniqueId()).getGamePlayer();
                if (gamePlayer == null) {
                    continue;
                }
                
                ITeam team = getPlayerTeams().get(nexusPlayer.getUniqueId());
                String correctChar = BEGIN_CHARS.get(gamePlayer.getTeam());
                if (team == null) {
                    createPlayerTeam(nexusPlayer);
                } else {
                    if (team.getName().startsWith(correctChar)) {
                        updatePlayerTeam(nexusPlayer);
                    } else {
                        refreshPlayerTeam(nexusPlayer);
                    }
                }
            }
        }
    }
    
    public void removeDisconnectedPlayers() {
        Iterator<Map.Entry<UUID, ITeam>> teamIterator = this.playerTeams.entrySet().iterator();
        while (teamIterator.hasNext()) {
            Map.Entry<UUID, ITeam> entry = teamIterator.next();
            if (Bukkit.getPlayer(entry.getKey()) == null) {
                entry.getValue().unregister();
                teamIterator.remove();
            }
        }
    }
    
    @Override
    public void unregister() {
        for (ITeam team : this.playerTeams.values()) {
            team.unregister();
        }
    }
    
    @Override
    public String getPlayerTeamName(NexusPlayer player) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        if (sgPlayer == null) {
            return "z_" + player.getName();
        }
        
        Game game = sgPlayer.getGame();
        
        if (game == null) {
            return "y_" + player.getName();
        }
        
        GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        if (gamePlayer != null) {
            return "x_" + player.getName();
        }
        
        return BEGIN_CHARS.get(gamePlayer.getTeam()) + "_" + player.getName();
    }
    
    @Override
    public void setDisplayOptions(NexusPlayer nexusPlayer, ITeam team) {
        String color;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());
        if (sgPlayer == null) {
            color = "&0";
        } else {
            Game game = sgPlayer.getGame();
            
            if (game == null) {
                color = "&1";
            } else {
                GamePlayer gamePlayer = game.getPlayer(nexusPlayer.getUniqueId());
                
                if (gamePlayer == null) {
                    color = "&3";
                } else {
                    color = gamePlayer.getTeam().getColor();
                }
            }
        }
        
        team.setPrefix(StarColors.color(color));
    }
    
    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }
}
