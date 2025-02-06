package com.thenexusreborn.survivalgames.scoreboard;

import com.stardevllc.colors.StarColors;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

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
        
        for (Player other : Bukkit.getOnlinePlayers()) {
            NexusPlayer otherNexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(other.getUniqueId());
            if (otherNexusPlayer != null) {
                NexusPlayer player = scoreboard.getPlayer();
                GamePlayer gamePlayer = plugin.getPlayerRegistry().get(player.getUniqueId()).getGamePlayer();
                ITeam otherTeam = getPlayerTeams().get(otherNexusPlayer.getUniqueId());
                if (gamePlayer == null) {
                    continue;
                }
                
                String correctChar = BEGIN_CHARS.get(gamePlayer.getTeam());
                if (otherTeam == null) {
                    createPlayerTeam(otherNexusPlayer);
                } else {
                    try {
                        if (otherTeam.getName().startsWith(correctChar)) {
                            updatePlayerTeam(otherNexusPlayer);
                        } else {
                            refreshPlayerTeam(otherNexusPlayer);
                        }
                    } catch (IllegalStateException e) {
                        refreshPlayerTeam(otherNexusPlayer);
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
        Game game = plugin.getPlayerRegistry().get(player.getUniqueId()).getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            GameTeam gameTeam = gamePlayer.getTeam();
            String beginChar = BEGIN_CHARS.get(gameTeam);
            String teamName = beginChar + "_";
    
            String pName = player.getName();
            if (pName.length() > 13) {
                teamName += pName.substring(0, 14);
            } else {
                teamName += pName;
            }
            return teamName;
        }
        return null;
    }
    
    @Override
    public void setDisplayOptions(NexusPlayer nexusPlayer, ITeam team) {
        Game game = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId()).getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(nexusPlayer.getUniqueId());
            team.setPrefix(StarColors.color(gamePlayer.getTeam().getColor()));
        }
    }
}
