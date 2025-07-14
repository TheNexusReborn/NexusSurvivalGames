package com.thenexusreborn.survivalgames.cmd.sgadmin.game.player;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.stardevllc.time.TimeFormat;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Bounty.Type;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class GamePlayerStatusSubCmd extends GamePlayerSubCmd {
    public GamePlayerStatusSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "status", "", Rank.HELPER);
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer senderPlayer, Game game, GamePlayer target, String[] args, FlagResult flagResults) {
        TimeFormat timeFormat = new TimeFormat("%*#0y %%*#0mo %%*#0d %%*#0h %%#0m %%00s%");
        DecimalFormat numberFormat = new DecimalFormat("#,###,###,###.##");
        
        sender.sendMessage(StarColors.color("&6&l>> &eGame Player Information for &b" + target.getName()));
        sendLine(sender, "Team", target.getTeam().name());
        sendLine(sender, "Status", target.getStatus() != null ? target.getStatus().name() : "UNDEFINED");
        sendLine(sender, "Kills", target.getKills());
        sendLine(sender, "Kill Streak", target.getKillStreak());
        sendLine(sender, "Assists", target.getAssists());
        sendLine(sender, "Spectator By Death", target.isSpectatorByDeath());
        sendLine(sender, "Mutated", target.hasMutated());
        sendLine(sender, "Times Mutated", target.getTimesMutated());
        sendLine(sender, "Death By Mutation", target.deathByMutation());
        sendLine(sender, "Sponsored", target.hasSponsored());
        sendLine(sender, "Times Sponsored", target.getTimesSponsored());
        if (target.getCombatTag().isInCombat()) {
            String ctOther = game.getPlayer(target.getCombatTag().getOther()).getName();
            String ctTime = timeFormat.format(target.getCombatTag().getTimer().getTime());
            sendLine(sender, "Combat", ctOther + ": " + ctTime);
        } else {
            sendLine(sender, "Combat", "No one");
        }
        
        if (target.getTrackerInfo() != null) {
            sendLine(sender, "Tracker Info", target.getTrackerInfo().target() + " - " + target.getTrackerInfo().distance());
        } else {
            sendLine(sender, "Tracker Info", "No one");
        }
        
        if (target.getMutation() != null) {
            String mTarget = game.getPlayer(target.getMutation().getTarget()).getName();
            String type = target.getMutation().getType().getDisplayName();
            sendLine(sender, "Mutation", type + ": " + mTarget);
        } else {
            sendLine(sender, "Mutation", "None");
        }
        
        if (target.getBounty().has()) {
            sendLine(sender, "Bounty", null);
            for (Type type : Type.values()) {
                sendLine(sender, "&8- &f" + type.name(), "&b" + numberFormat.format(target.getBounty().getAmount(type)));
            }
        } else {
            sendLine(sender, "Bounty", "None");
        }
        
        if (target.getDamageInfo().hasDamagers()) {
            sendLine(sender, "Damagers", null);
            for (UUID uuid : target.getDamageInfo().getDamagers()) {
                GamePlayer damager = game.getPlayer(uuid);
                if (damager == null) {
                    continue;
                }
                
                sendLine(sender, "&8- " + damager.getName(), null);
            }
        }
        
        return true;
    }
    
    private void sendLine(CommandSender sender, String prefix, Object data) {
        sender.sendMessage(StarColors.color(" &6&l> &e" + prefix + (data != null ? ": &f" + data : "")));
    }
}
