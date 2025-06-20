package com.thenexusreborn.survivalgames.scoreboard;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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
        
        return beginChars + "_" + sgPlayer.getName();
    }
    
    @Override
    public void setDisplayOptions(NexusPlayer nexusPlayer, ITeam team) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());
        if (sgPlayer == null) {
            team.setColor(ChatColor.BLACK);
        } else {
            Game game = sgPlayer.getGame();
            
            if (game == null) {
                team.setColor(ChatColor.DARK_BLUE);
            } else {
                GamePlayer gamePlayer = game.getPlayer(nexusPlayer.getUniqueId());
                
                if (gamePlayer == null) {
                    team.setColor(ChatColor.AQUA);
                } else {
                    team.setColor(ChatColor.getByChar(gamePlayer.getTeam().getColor().charAt(1)));
                }
            }
        }
    }
    
    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }
}
