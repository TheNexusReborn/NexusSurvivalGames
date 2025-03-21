package com.thenexusreborn.survivalgames.cmd.sgadmin.timer.modify;

import com.stardevllc.clock.clocks.Timer;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.entity.Player;

public class ModifySubtractSubCmd extends ModifySubCommand {
    public ModifySubtractSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "subtract", "");
    }
    
    @Override
    public boolean handle(Player sender, Timer timer, String timerType, long timeValue, long oldValue) {
        timer.removeTime(timeValue);
        sender.sendMessage(MsgType.INFO.format("You subtracted %v from the %v's timer.", timeFormat.format(timeValue), timerType));
        return true;
    }
}
