package com.thenexusreborn.survivalgames.cmd.sgadmin.timer;

import com.stardevllc.clock.clocks.Timer;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.entity.Player;

public class TimerResumeSubCmd extends TimerSubCommand {
    public TimerResumeSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "resume", "");
    }
    
    @Override
    protected boolean handle(Player sender, Timer timer) {
        if (!timer.isPaused()) {
            sender.sendMessage(MsgType.WARN.format("The timer is not paused."));
            return true;
        }
        timer.unpause();
        sender.sendMessage(MsgType.INFO.format("You resumed the timer."));
        return true;
    }
}
