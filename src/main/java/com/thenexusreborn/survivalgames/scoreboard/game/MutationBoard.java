package com.thenexusreborn.survivalgames.scoreboard.game;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class MutationBoard extends SpigotScoreboardView {
    private SurvivalGames plugin;
    
    public MutationBoard(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard, "mutationinfo", StarColors.color("&d&lMutation"));
        this.plugin = plugin;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("dateValue").entry(ChatColor.GOLD).score(15).valueUpdater((player, team) -> {
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
            team.setSuffix("&7" + df.format(System.currentTimeMillis()));
        }));
        
        createTeam(new TeamBuilder("blank1").entry(ChatColor.AQUA).score(14));
    
        createTeam(new TeamBuilder("targetLabel").entry("&6&lTARGET:").score(13));
        createTeam(new TeamBuilder("targetValue").entry(ChatColor.WHITE).score(12).valueUpdater((player, team) -> {
            GamePlayer gamePlayer = plugin.getPlayerRegistry().get(player.getUniqueId()).getGamePlayer();
            
            if (gamePlayer == null) {
                return;
            }
            
            if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
                team.setSuffix("None");
                return; 
            }
            
            Mutation mutation = gamePlayer.getMutation();
            
            if (mutation == null) {
                return;
            }
            
            if (mutation.getTarget() == null) {
                return;
            }
            
            SGPlayer targetPlayer = plugin.getPlayerRegistry().get(mutation.getTarget());
            if (targetPlayer == null) {
                return;
            }
            
            GamePlayer target = targetPlayer.getGamePlayer();
            if (target == null) {
                team.setSuffix("None");
                return;
            }
            team.setSuffix(target.getName());
        }));
        
        createTeam(new TeamBuilder("blank2").entry(ChatColor.RED).score(11));
        
        createTeam(new TeamBuilder("typeLabel").entry("&6&lTYPE:").score(10));
        createTeam(new TeamBuilder("typeValue").entry(ChatColor.BLACK).score(9).valueUpdater((player, team) -> {
            GamePlayer gamePlayer = plugin.getPlayerRegistry().get(player.getUniqueId()).getGamePlayer();
            
            if (gamePlayer == null) {
                return;
            }
            
            if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
                team.setSuffix("None");
                return;
            }
            Mutation mutation = gamePlayer.getMutation();
            
            if (mutation == null) {
                return;
            }
            
            if (mutation.getType() == null) {
                return;
            }
            
            team.setSuffix("&f" + mutation.getType().getDisplayName());
        }));
    }
}
