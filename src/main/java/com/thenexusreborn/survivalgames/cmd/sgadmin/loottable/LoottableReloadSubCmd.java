package com.thenexusreborn.survivalgames.cmd.sgadmin.loottable;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import org.bukkit.command.CommandSender;

public class LoottableReloadSubCmd extends LoottableSubCommand {
    public LoottableReloadSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "reload", "", "r");
    }
    
    @Override
    protected boolean handle(CommandSender sender, SGLootTable lootTable, String[] args, FlagResult flagResults) {
        lootTable.setReloading(true);
        try {
            lootTable.saveData();
            lootTable.loadData();
            if (lootTable.getItems().isEmpty()) {
                lootTable.loadDefaultData();
            }
            sender.sendMessage(MsgType.INFO.format("Reload of loot table %v was successful.", lootTable.getName()));
        } catch (Throwable throwable) {
            sender.sendMessage(MsgType.ERROR.format("There was an error reloading that loot table: " + throwable.getMessage()));
        }
        lootTable.setReloading(false);
        return true;
    }
}
