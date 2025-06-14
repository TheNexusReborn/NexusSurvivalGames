package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GraceperiodCmd extends NexusCommand<SurvivalGames> {
    public GraceperiodCmd(SurvivalGames plugin) {
        super(plugin, "graceperiod", "", Rank.VIP);
        this.playerOnly = true;
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        if (sgPlayer == null) {
            MsgType.ERROR.send(player, "You cannot use that command at this time.");
            return true;
        }
        
        if (!(args.length > 0)) {
            MsgType.WARN.send(player, "You must provide some arguments");
            return true;
        }

        GameSettings settings = null;
        if (sgPlayer.getLobby() != null) {
            settings = sgPlayer.getLobby().getGameSettings();
        } else if (sgPlayer.getGame() != null) {
            settings = sgPlayer.getGame().getSettings();
        }
        
        if (settings == null) {
            MsgType.ERROR.send(sender, "Could not determing where to change the graceperiod");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("off")) {
            settings.setGracePeriod(false);
            settings.setAutomaticGraceperiod(true);
            MsgType.INFO.send(player, "You turned the graceperiod &coff");
            MsgType.DETAIL.send(player, "Note: It will apply on the next game");
            plugin.getNexusCore().getStaffChannel().sendMessage(new ChatContext(sgPlayer.getTrueColoredName() + " &fturned &c&lOFF &fthe graceperiod in &l" + sgPlayer.getNexusPlayer().getServer().getName()));
            return true;
        } else if (args[0].equalsIgnoreCase("on")) {
            if (!(args.length > 1)) {
                settings.setGracePeriod(true);
                settings.setAutomaticGraceperiod(false);
                MsgType.INFO.send(player, "You turned the graceperiod &aon");
                MsgType.DETAIL.send(player, "Note: It will apply on the next game");
            } else {
                int seconds;
                try {
                    seconds = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    MsgType.ERROR.send(player, "Invalid number provided %v", args[1]);
                    return true;
                }

                settings.setGracePeriod(true);
                settings.setGracePeriodLength(seconds);
                settings.setAutomaticGraceperiod(false);
                MsgType.INFO.send(player, "You turned the graceperiod &aon %bwith a time of %v seconds", seconds);
                MsgType.DETAIL.send(player, "Note: It will apply on the next game");
            }
            
            plugin.getNexusCore().getStaffChannel().sendMessage(new ChatContext(sgPlayer.getTrueColoredName() + " &fturned &a&lON &fthe graceperiod (" + settings.getGracePeriodLength() + "s) in &l" + sgPlayer.getNexusPlayer().getServer().getName()));
        }
        
        return true;
    }

    @Override
    public List<String> getCompletions(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResult) {
        return List.of("on", "off");
    }
}
