package com.thenexusreborn.survivalgames.scoreboard.game;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starlib.time.TimeFormat;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TeamBuilder;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.CombatTag;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CombatTagBoard extends SpigotScoreboardView {
    public static final TimeFormat TIME_FORMAT = new TimeFormat("%0s%");
    
    private SurvivalGames plugin;
    
    public CombatTagBoard(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard, "combattag", StarColors.color("&9&lCombat Tag"));
        this.plugin = plugin;
    }
    
    @Override
    public void registerTeams() {
        createTeam(new TeamBuilder("dateValue").entry(ChatColor.GOLD).score(15).valueUpdater((player, team) -> {
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
            team.setSuffix("&7" + df.format(System.currentTimeMillis()));
        }));
        
        createTeam(new TeamBuilder("targetLabel").entry("&6&lTARGET:").score(14));
        createTeam(new TeamBuilder("targetValue").entry(ChatColor.WHITE).score(13).valueUpdater((player, team) -> {
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            Game game = sgPlayer.getGame();
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                return;
            }
            CombatTag combatTag = gamePlayer.getCombatTag();
            if (combatTag == null || combatTag.getOther() == null) {
                team.setSuffix("No one");
            } else {
                GamePlayer otherGamePlayer = game.getPlayer(combatTag.getOther());
                if (otherGamePlayer == null) {
                    team.setSuffix("No one");
                } else {
                    team.setSuffix(otherGamePlayer.getName());
                }
            }
        }));
        
        createTeam(new TeamBuilder("blank1").entry(ChatColor.RED).score(12));
        createTeam(new TeamBuilder("timeLabel").entry("&6&lTIME:").score(11));
        createTeam(new TeamBuilder("timeValue").entry(ChatColor.AQUA).score(10).valueUpdater((player, team) -> {
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            Game game = sgPlayer.getGame();
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                return;
            }
            CombatTag combatTag = gamePlayer.getCombatTag();
            if (combatTag == null || combatTag.getOther() == null || !combatTag.isInCombat()) {
                team.setSuffix("0s");
            } else {
                team.setSuffix("&f" + TIME_FORMAT.format(combatTag.getTimer().getTime()));
            }
        }));
    }
}
