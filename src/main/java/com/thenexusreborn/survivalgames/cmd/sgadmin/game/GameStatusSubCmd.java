package com.thenexusreborn.survivalgames.cmd.sgadmin.game;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.stardevllc.starlib.time.TimeFormat;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.Game.SubState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class GameStatusSubCmd extends SubCommand<SurvivalGames> {
    public GameStatusSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "status", "", Rank.ADMIN, "info");
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();

        if (game == null) {
            MsgType.WARN.send(sender, "You are not in a game.");
            return true;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
        TimeFormat timeFormat = new TimeFormat("%*#0y %%*#0mo %%*#0d %%*#0h %%#0m %%00s%");
//        DecimalFormat numberFormat = new DecimalFormat("#,###,###,###.##");
        
        sender.sendMessage(StarColors.color("&6&l>> &eCurrent Game Information"));
        sendStatusLine(sender, "Map", game.getGameMap().getName());
        sendStatusLine(sender, "Server", game.getServer().getName());
        sendStatusLine(sender, "Control Mode", game.getControlType().name());
        sendStatusLine(sender, "Mode", game.getMode().name());
        sendStatusLine(sender, "State", game.getState().name() + (game.getSubState() != SubState.UNDEFINED ? "." + game.getSubState().name() : ""));
        sendStatusLine(sender, "Start", dateFormat.format(game.getStart()));
        sendStatusLine(sender, "First Blood", game.getFirstBlood() != null ? game.getFirstBlood().getName() : "No one");
        sendStatusLine(sender, "Graceperiod", game.getGraceperiod().name());
        sendStatusLine(sender, "Timer", timeFormat.format(game.getTimer() != null ? game.getTimer().getTime() : 0L));
        sendStatusLine(sender, "Time to Restock", timeFormat.format(game.getTimer() != null ? game.getTimer().getTime() - game.getNextRestock() : 0L));
        
        return true;
    }
    
    private void sendStatusLine(CommandSender sender, String prefix, Object data) {
        sender.sendMessage(StarColors.color(" &6&l> &e" + prefix + ": &f" + data.toString()));
    }
}
