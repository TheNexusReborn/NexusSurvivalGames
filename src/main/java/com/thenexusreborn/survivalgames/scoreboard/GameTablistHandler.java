package com.thenexusreborn.survivalgames.scoreboard;

import com.stardevllc.starcore.api.StarColors;
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
        
        for (SGPlayer sgPlayer : plugin.getPlayerRegistry()) {
            
            if (sgPlayer.getGame() != null) {
                GamePlayer gamePlayer = sgPlayer.getGamePlayer();
                if (gamePlayer != null) {
                    ITeam team = getPlayerTeams().get(sgPlayer.getUniqueId());
                    String correctChar = BEGIN_CHARS.get(gamePlayer.getTeam());
                    if (team == null || team.getName().isEmpty()) {
                        createPlayerTeam(sgPlayer.getNexusPlayer());
                    } else {
                        if (team.getName().startsWith(correctChar)) {
                            updatePlayerTeam(sgPlayer.getNexusPlayer());
                        } else {
                            refreshPlayerTeam(sgPlayer.getNexusPlayer());
                        }
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
        String beginChars = "";
        if (sgPlayer == null) {
            beginChars = "z_";
        }
        
        Game game = sgPlayer.getGame();
        
        if (game == null) {
            beginChars =  "y_";
        }
        
        GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        if (gamePlayer == null) {
            beginChars = "x_";
        }
        
        String name = beginChars + "_" + sgPlayer.getName();
        if (name.length() > 15) {
            return name.substring(0, 16);
        }
        
        return name;
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
