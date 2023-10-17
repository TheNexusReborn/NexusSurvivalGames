package com.thenexusreborn.survivalgames.scoreboard.game;

import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.nexuscore.scoreboard.SpigotScoreboardView;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import me.firestar311.starlib.api.time.TimeFormat;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CombatTagBoard extends SpigotScoreboardView {
    private static final TimeFormat TIME_FORMAT = new TimeFormat("%0s%");
    
    private SurvivalGames plugin;
    
    public CombatTagBoard(NexusScoreboard scoreboard, SurvivalGames plugin) {
        super(scoreboard, "combattag", MCUtils.color("&9&lCombat Tag"));
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
            Game game = plugin.getGame();
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                return;
            }
            CombatTag combatTag = gamePlayer.getCombatTag();
            if (combatTag == null || combatTag.getOther() == null) {
                team.setSuffix("No one");
            } else {
                team.setSuffix(game.getPlayer(combatTag.getOther()).getName());
            }
        }));
        
        createTeam(new TeamBuilder("blank1").entry(ChatColor.RED).score(12));
        createTeam(new TeamBuilder("timeLabel").entry("&6&lTIME:").score(11));
        createTeam(new TeamBuilder("timeValue").entry(ChatColor.AQUA).score(10).valueUpdater((player, team) -> {
            Game game = plugin.getGame();
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                return;
            }
            CombatTag combatTag = gamePlayer.getCombatTag();
            if (combatTag == null || combatTag.getOther() == null || !combatTag.isInCombat()) {
                team.setSuffix("0s");
            } else {
                long combatTagLength = plugin.getGame().getSettings().getCombatTagLength() * 1000L;
                long timeRemaining = combatTag.getTimestamp() + combatTagLength - System.currentTimeMillis();
                team.setSuffix("&f" + TIME_FORMAT.format(timeRemaining));
            }
        }));
    }
}
