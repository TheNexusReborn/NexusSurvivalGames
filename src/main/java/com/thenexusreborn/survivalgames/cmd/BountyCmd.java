package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Bounty;
import com.thenexusreborn.survivalgames.game.Bounty.Type;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyCmd implements CommandExecutor {
    
    private final SurvivalGames plugin;
    
    public BountyCmd(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }
        
        Game game = plugin.getGame();
        
        if (game == null) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "You can only bounty a player during a game."));
            return true;
        }
        
        if (!(args.length > 1)) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " <player> <amount> [type: default score]"));
            return true;
        }
        
        GamePlayer gamePlayer = game.getPlayer(args[0]);
        if (gamePlayer == null) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "The name you provided is not a player in the game."));
            return true;
        }
        
        if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "You can only set a bounty on a Tribute."));
            return true;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid number value."));
            return true;
        }
        
        Bounty.Type type = Bounty.Type.SCORE;
        if (args.length > 2) {
            try {
                type = Bounty.Type.valueOf(args[2].toUpperCase());
            } catch (Exception e) {
                player.sendMessage(MCUtils.color(MsgType.WARN + "Invalid Bounty Type. Valid Options: CREDIT, SCORE"));
                return true;
            }
        }
        
        NexusPlayer nexusPlayer = gamePlayer.getNexusPlayer();
        if (type == Type.SCORE) {
            if (nexusPlayer.getStats().getValue("sg_score").getAsInt() < amount) {
                nexusPlayer.sendMessage(MsgType.WARN + "You do not have enough score to set a bounty of " + amount);
                return true;
            } else {
                nexusPlayer.getStats().change("sg_score", amount, StatOperator.SUBTRACT);
            }
        } else if (type == Type.CREDIT) {
            if (nexusPlayer.getStats().getValue("credits").getAsInt() < amount) {
                nexusPlayer.sendMessage(MsgType.WARN + "You do not have enough credits to set a bounty of " + amount);
                return true;
            } else {
                nexusPlayer.getStats().change("credits", amount, StatOperator.SUBTRACT);
            }
        }
        
        Bounty bounty = gamePlayer.getBounty();
        bounty.add(type, amount);
        String coloredName = nexusPlayer.getColoredName();
        game.sendMessage("&6&l>> &dThe bounty on " + coloredName + " &dwas increased by &b" + amount + " " + type.name().toLowerCase() + "&d!");
        game.sendMessage("&6&l>> &dThe current value is &d" + bounty.getAmount(type) + " " + type.name().toLowerCase() + "&d.");
        return true;
    }
}
