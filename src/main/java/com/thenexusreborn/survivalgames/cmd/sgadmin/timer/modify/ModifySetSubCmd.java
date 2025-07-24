package com.thenexusreborn.survivalgames.cmd.sgadmin.timer.modify;

import com.stardevllc.starlib.clock.clocks.Timer;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.entity.Player;

public class ModifySetSubCmd extends ModifySubCommand {
    public ModifySetSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "set", "");
    }
    
    @Override
    public boolean handle(Player sender, Timer timer, String timerType, long timeValue, long oldValue) {
        timer.setTime(timeValue);
        sender.sendMessage(MsgType.INFO.format("You set %v's timer to %v.", timerType, timeFormat.format(timeValue)));
        return true;
    }
}
