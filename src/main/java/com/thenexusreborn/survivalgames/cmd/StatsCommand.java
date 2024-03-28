package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StatsCommand implements CommandExecutor {

    private final SurvivalGames plugin;

    public StatsCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //TODO Reimplement this command
        sender.sendMessage(MsgType.WARN + "This command is being reworked.");
        return true;
    }
}