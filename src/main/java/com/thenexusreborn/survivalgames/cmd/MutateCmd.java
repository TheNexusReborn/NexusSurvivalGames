package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.menu.MutateGui;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

//TODO This is strictly a dev command for testing things
public class MutateCmd implements CommandExecutor {
    
    private SurvivalGames plugin;
    
    public MutateCmd(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender; 
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        player.openInventory(new MutateGui(plugin, nexusPlayer).getInventory());
        return true;
    }
}
