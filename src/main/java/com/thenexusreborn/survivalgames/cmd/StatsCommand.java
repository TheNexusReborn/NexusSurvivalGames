package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.command.CommandSender;

public class StatsCommand extends NexusCommand<SurvivalGames> {

    public StatsCommand(SurvivalGames plugin) {
        super(plugin, "stats", "", Rank.MEMBER);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        //TODO Reimplement this command
        sender.sendMessage(MsgType.WARN + "This command is being reworked.");
        return true;
    }
}