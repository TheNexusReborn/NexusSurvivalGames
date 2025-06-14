package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.item.Items;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class ProbabilityCmd extends NexusCommand<SurvivalGames> {
    public ProbabilityCmd(SurvivalGames plugin) {
        super(plugin, "probability", "See the probability of the loot", Rank.GOLD, "prob");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        if (sgPlayer == null) {
            MsgType.ERROR.send(player, "You cannot use that command at this time.");
            return true;
        }
        
        if (!(args.length > 1)) {
            MsgType.WARN.send(player, "You must provide a loottable name and item name");
            return true;
        }
        
        SGLootTable lootTable = plugin.getLootManager().getLootTable(args[0]);
        if (lootTable == null) {
            MsgType.WARN.send(player, "Invalid loot table name %v.", args[0]);
            return true;
        }
        
        LootItem lootItem = Items.REGISTRY.get(args[1]);
        if (lootItem == null) {
            MsgType.WARN.send(player, "Invalid item name %v.", args[1]);
            return true;
        }
        
        double weight = lootTable.getItemWeight(lootItem.getName());
        double totalWeight = lootTable.getWeightTotal();
        
        double probability = weight / totalWeight * 100;
        
        DecimalFormat decimalFormat = new DecimalFormat("##0.###");
        
        MsgType.INFO.send(player, "The probability of %v is %v% for the loot table %v.", StarColors.stripColor(lootItem.getName()), decimalFormat.format(probability), lootTable.getName());
        return true;
    }
}
