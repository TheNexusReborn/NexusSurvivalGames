package com.thenexusreborn.survivalgames.cmd.sgadmin.timer;

import com.stardevllc.starlib.clock.clocks.Timer;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.entity.Player;

public class TimerPauseSubCmd extends TimerSubCommand {
    public TimerPauseSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "pause", "");
    }
    
    @Override
    protected boolean handle(Player sender, Timer timer) {
        if (timer.isPaused()) {
            sender.sendMessage(MsgType.WARN.format("The timer is already paused."));
            return true;
        }
        timer.pause();
        sender.sendMessage(MsgType.INFO.format("You paused the timer."));
        return true;
    }
}
