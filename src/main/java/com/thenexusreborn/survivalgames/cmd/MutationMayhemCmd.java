package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
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
        
        GameSettings settings = null;
        if (sgPlayer.getLobby() != null) {
            settings = sgPlayer.getLobby().getGameSettings();
        } else {
            settings = sgPlayer.getGame().getSettings();
        }
        
        settings.setAllowSingleTribute(true);
        settings.setAllowDeathmatch(false);
        settings.setAllowRecursiveMutations(true);
        settings.setAutomaticGraceperiod(false);
        
        return true;
    }
}
