package com.thenexusreborn.survivalgames.cmd.sgadmin.loottable;

import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import org.bukkit.command.CommandSender;

public class LoottableSetItemWeightSubCmd extends LoottableSubCommand {
    public LoottableSetItemWeightSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "setitemweight", "", "siw");
    }
    
    @Override
    protected boolean handle(CommandSender sender, SGLootTable lootTable, String[] args, FlagResult flagResults) {
        if (!(args.length > 1)) {
            MsgType.WARN.format("You must provide an item and a weight");
            return true;
        }
        
        String itemName = args[0].toLowerCase().replace("'", "");
        if (!lootTable.getItemWeights().containsKey(itemName)) {
            sender.sendMessage(MsgType.WARN.format("The loot table %v does not contain an item entry with the id of %v", lootTable.getName(), itemName));
            return true;
        }
        
        int weight;
        try {
            weight = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MsgType.WARN.format("The input value %v is not a valid whole number.", args[1]));
            return true;
        }
        
        lootTable.getItemWeights().put(itemName, weight);
        sender.sendMessage(MsgType.INFO.format("You set the item %v's weight to %v in loot table %v", itemName, weight, lootTable.getName()));
        sender.sendMessage(MsgType.INFO.format("You must use the /survivalgames loottable reload " + lootTable.getName() + " to apply your changes."));
        return true;
    }
}
