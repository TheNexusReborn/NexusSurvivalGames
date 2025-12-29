package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.Bounty.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BountyCmd extends NexusCommand<SurvivalGames> {

    public BountyCmd(SurvivalGames plugin) {
        super(plugin, "bounty", "", Rank.MEMBER);
        this.playerOnly = true;
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender; 
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();

        if (game == null) {
            player.sendMessage(MsgType.WARN.format("You can only bounty a player during a game."));
            return true;
        }

        if (!game.getSettings().isAllowBounties()) {
            MsgType.WARN.send(player, "Bounties are disabled for this game.");
            return true;
        }

        if (!(args.length > 1)) {
            player.sendMessage(MsgType.WARN.format("Usage: /" + label + " <player> <amount> [type: default score]"));
            return true;
        }

        GamePlayer senderPlayer = game.getPlayer(player.getUniqueId());
        GamePlayer gamePlayer = game.getPlayer(args[0]);
        if (gamePlayer == null) {
            player.sendMessage(MsgType.WARN.format("The name you provided is not a player in the game."));
            return true;
        }

        if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
            player.sendMessage(MsgType.WARN.format("You can only set a bounty on a Tribute."));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(MsgType.WARN.format("You provided an invalid number value."));
            return true;
        }

        if (amount < 1) {
            senderPlayer.sendMessage(MsgType.WARN.format("Invalid Bount Amount. Must be greater than 1"));
            return true;
        }

        Bounty.Type type = Bounty.Type.SCORE;
        if (args.length > 2) {
            try {
                type = Bounty.Type.valueOf(args[2].toUpperCase());
            } catch (Exception e) {
                player.sendMessage(MsgType.WARN.format("Invalid Bounty Type. Valid Options: CREDITS, SCORE"));
                return true;
            }
        }

        int max = 0;
        if (type == Type.SCORE) {
            if (senderPlayer.getStats().getScore() < amount) {
                senderPlayer.sendMessage(MsgType.WARN.format("You do not have enough score to set a bounty of %v.", amount));
                return true;
            } else {
                senderPlayer.getStats().addScore(-amount);
            }
            max = game.getSettings().getMaxScoreBounty();
        } else if (type == Type.CREDITS) {
            if (senderPlayer.getBalance().getCredits() < amount) {
                senderPlayer.sendMessage(MsgType.WARN.format("You do not have enough credits to set a bounty of %v.", amount));
                return true;
            } else {
                senderPlayer.getBalance().addCredits(-amount);
            }
            max = game.getSettings().getMaxCreditBounty();
        }

        Bounty bounty = gamePlayer.getBounty();

        double currentAmount = bounty.getAmount(type);
        if (currentAmount + amount >= max) {
            senderPlayer.sendMessage(MsgType.WARN.format("You cannot set the bounty higher than %v %v", max, type.name().toLowerCase()));
            return true;
        }

        bounty.add(type, amount);
        String coloredName = gamePlayer.getColoredName();
        String formattedAmount = MCUtils.formatNumber(amount);
        String totalFormattedAmount = MCUtils.formatNumber(bounty.getAmount(type));
        game.sendMessage("&6&l>> &dThe bounty on " + coloredName + " &dwas increased by &b" + formattedAmount + " " + type.name().toLowerCase() + "&d!");
        game.sendMessage("&6&l>> &dThe current value is &b" + totalFormattedAmount + " " + type.name().toLowerCase() + "&d.");
        return true;
    }

    @Override
    public List<String> getCompletions(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResult) {
        List<String> completions = new ArrayList<>();
        
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        
        if (sgPlayer.getGame() == null) {
            return null;
        }

        for (GamePlayer gp : sgPlayer.getGame().getPlayers().values()) {
            if (gp.getTeam() == GameTeam.TRIBUTES) {
                completions.add(gp.getName());
            }
        }
        
        return completions;
    }
}
