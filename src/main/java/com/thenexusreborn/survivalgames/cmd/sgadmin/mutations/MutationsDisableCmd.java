package com.thenexusreborn.survivalgames.cmd.sgadmin.mutations;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.mutations.StandardMutations;
import org.bukkit.command.CommandSender;

public class MutationsDisableCmd extends SubCommand<SurvivalGames> {
    public MutationsDisableCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "disable", "Disables a mutation type", Rank.ADMIN, "d");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a mutation type");
            return true;
        }
        
        StandardMutations type;
        try {
            type = StandardMutations.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            MsgType.WARN.send(sender, "Sorry, but %v is not a valid mutation type.", args[0]);
            return true;
        }
        
        if (plugin.getDisabledMutations().contains(type)) {
            MsgType.WARN.send(sender, "Sorry, but %v is already disabled.", type.name().toLowerCase());
            return true;
        }
        
        plugin.disableMutation(type);
        MsgType.INFO.send(sender, "You %v the mutation type %v", "&cdisabled", type.name());
        
        return true;
    }
}
