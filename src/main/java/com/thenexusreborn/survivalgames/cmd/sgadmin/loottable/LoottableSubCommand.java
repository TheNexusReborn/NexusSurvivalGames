package com.thenexusreborn.survivalgames.cmd.sgadmin.loottable;

import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import org.bukkit.command.CommandSender;

public abstract class LoottableSubCommand extends SubCommand<SurvivalGames> {
    public LoottableSubCommand(SurvivalGames plugin, ICommand<SurvivalGames> parent, String name, String description, String... aliases) {
        super(plugin, parent, 1, name, description, Rank.ADMIN, aliases);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            sender.sendMessage(MsgType.WARN.format("You must provide a Loot Table name."));
            return true;
        }
        
        SGLootTable lootTable = plugin.getLootManager().getLootTable(args[0]);
        if (lootTable == null) {
            sender.sendMessage(MsgType.WARN.format("The value %v is not a valid loot table", args[0]));
            return true;
        }
        
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        
        args = newArgs;
        
        return handle(sender, lootTable, args, flagResults);
    }
    
    protected abstract boolean handle(CommandSender sender, SGLootTable lootTable, String[] args, FlagResult flagResults);
}
