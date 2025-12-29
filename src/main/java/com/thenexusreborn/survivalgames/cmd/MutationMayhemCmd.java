package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MutationMayhemCmd extends NexusCommand<SurvivalGames> {
    public MutationMayhemCmd(SurvivalGames plugin) {
        super(plugin, "mutationmayhem", "", Rank.NEXUS, "mm");
        this.playerOnly = true;
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        
        GameSettings settings;
        if (sgPlayer.getLobby() != null) {
            settings = sgPlayer.getLobby().getGameSettings();
        } else {
            settings = sgPlayer.getGame().getSettings();
        }
        
        settings.setAllowSingleTribute(true);
        settings.setAllowDeathmatch(false);
        settings.setAllowRecursiveMutations(true);
        settings.setAutomaticGraceperiod(false);
        settings.setGameLength(15);
        settings.setMaxMutationAmount(3);
        MsgType.INFO.send(sender, "You have enabled Mutation Mayhem!");
        plugin.getNexusCore().getStaffChannel().sendMessage(new ChatContext(sgPlayer.getTrueColoredName() + " &fturned &a&lON &eMutation Mayhem"));
        return true;
    }
}
