package com.thenexusreborn.survivalgames.cmd.sgadmin.timer;

import com.stardevllc.clock.clocks.Timer;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.entity.Player;

public class TimerResetSubCmd extends TimerSubCommand {
    public TimerResetSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "resume", "");
    }
    
    @Override
    protected boolean handle(Player sender, Timer timer) {
        timer.reset();
        sender.sendMessage(MsgType.INFO.format("You reset the timer."));
        return true;
    }
}
