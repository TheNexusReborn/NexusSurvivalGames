package com.thenexusreborn.survivalgames.cmd.sgadmin.timer.modify;

import com.stardevllc.starlib.clock.clocks.Timer;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.entity.Player;

public class ModifyAddSubCmd extends ModifySubCommand {
    public ModifyAddSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "add", "");
    }
    
    @Override
    public boolean handle(Player sender, Timer timer, String timerType, long timeValue, long oldValue) {
        timer.addTime(timeValue);
        sender.sendMessage(MsgType.INFO.format("You added %v to the %v's timer.", timeFormat.format(timeValue), timerType));
        return true;
    }
}
