package com.thenexusreborn.survivalgames.scoreboard;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GameTablistHandler extends TablistHandler {
    
    private static final Map<GameTeam, String> BEGIN_CHARS = new HashMap<>();
    
    static {
        BEGIN_CHARS.put(GameTeam.TRIBUTES, "a");
        BEGIN_CHARS.put(GameTeam.MUTATIONS, "b");
        BEGIN_CHARS.put(GameTeam.SPECTATORS, "c");
        BEGIN_CHARS.put(GameTeam.HIDDEN_STAFF, "d");
    }
    
    private SurvivalGames plugin;
    
    public GameTablistHandler(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard);
        this.plugin = plugin;
    }
    
    @Override
    public void update() {
        for (Player other : Bukkit.getOnlinePlayers()) {
            NexusPlayer otherNexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(other.getUniqueId());
            if (otherNexusPlayer != null) {
                NexusPlayer player = scoreboard.getPlayer();
                GamePlayer gamePlayer = plugin.getGame().getPlayer(player.getUniqueId());
                ITeam otherTeam = getPlayerTeams().get(otherNexusPlayer.getUniqueId());
                String correctChar = BEGIN_CHARS.get(gamePlayer.getTeam());
                if (otherTeam == null) {
                    createPlayerTeam(otherNexusPlayer);
                } else {
                    if (otherTeam.getName().startsWith(correctChar)) {
                        updatePlayerTeam(otherNexusPlayer);
                    } else {
                        refreshPlayerTeam(otherNexusPlayer);
                    }
                }
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
        Game game = plugin.getGame();
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
        if (plugin.getGame() != null) {
            GamePlayer gamePlayer = plugin.getGame().getPlayer(nexusPlayer.getUniqueId());
            team.setPrefix(MCUtils.color(gamePlayer.getTeam().getColor()));
        }
    }
}
