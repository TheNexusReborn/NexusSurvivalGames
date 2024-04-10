package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.api.helper.NumberHelper;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
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
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());

        Game game = sgPlayer.getGame();

        if (game == null) {
            player.sendMessage(ColorUtils.color(MsgType.WARN + "You can only bounty a player during a game."));
            return true;
        }

        if (!(args.length > 1)) {
            player.sendMessage(ColorUtils.color(MsgType.WARN + "Usage: /" + label + " <player> <amount> [type: default score]"));
            return true;
        }

        GamePlayer senderPlayer = game.getPlayer(player.getUniqueId());
        GamePlayer gamePlayer = game.getPlayer(args[0]);
        if (gamePlayer == null) {
            player.sendMessage(ColorUtils.color(MsgType.WARN + "The name you provided is not a player in the game."));
            return true;
        }

        if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
            player.sendMessage(ColorUtils.color(MsgType.WARN + "You can only set a bounty on a Tribute."));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ColorUtils.color(MsgType.WARN + "You provided an invalid number value."));
            return true;
        }

        Bounty.Type type = Bounty.Type.SCORE;
        if (args.length > 2) {
            try {
                type = Bounty.Type.valueOf(args[2].toUpperCase());
            } catch (Exception e) {
                player.sendMessage(ColorUtils.color(MsgType.WARN + "Invalid Bounty Type. Valid Options: CREDIT, SCORE"));
                return true;
            }
        }

        int max = 0;
        if (type == Type.SCORE) {
            if (senderPlayer.getStats().getScore() < amount) {
                senderPlayer.sendMessage(MsgType.WARN + "You do not have enough score to set a bounty of " + amount);
                return true;
            } else {
                senderPlayer.getStats().addScore(-amount);
            }
            max = game.getSettings().getMaxScoreBounty();
        } else if (type == Type.CREDIT) {
            if (senderPlayer.getBalance().getCredits() < amount) {
                senderPlayer.sendMessage(MsgType.WARN + "You do not have enough credits to set a bounty of " + amount);
                return true;
            } else {
                senderPlayer.getBalance().addCredits(-amount);
            }
            max = game.getSettings().getMaxCreditBounty();
        }

        Bounty bounty = gamePlayer.getBounty();

        double currentAmount = bounty.getAmount(type);
        if (currentAmount + amount >= max) {
            senderPlayer.sendMessage(MsgType.WARN + "You cannot set the bounty higher than " + max + " " + type.name().toLowerCase());
            return true;
        }

        bounty.add(type, amount);
        String coloredName = senderPlayer.getColoredName();
        String formattedAmount = NumberHelper.formatNumber(amount);
        String totalFormattedAmount = NumberHelper.formatNumber(bounty.getAmount(type));
        game.sendMessage("&6&l>> &dThe bounty on " + coloredName + " &dwas increased by &b" + formattedAmount + " " + type.name().toLowerCase() + "&d!");
        game.sendMessage("&6&l>> &dThe current value is &b" + totalFormattedAmount + " " + type.name().toLowerCase() + "&d.");
        return true;
    }
}
