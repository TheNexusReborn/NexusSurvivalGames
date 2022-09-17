package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.stats.Stat;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.api.helper.StringHelper;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.function.Consumer;

public class StatsCommand implements CommandExecutor {
    
    private final SurvivalGames plugin;
    
    public StatsCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    // /stats [player]
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String identifier;
        
        if (!(sender instanceof Player)) {
            if (!(args.length > 0)) {
                sender.sendMessage(MCUtils.color("&cConsole must provide a player name for this command."));
                return true;
            }
            
            identifier = args[0];
        } else {
            if (args.length > 0) {
                identifier = args[0];
            } else {
                identifier = ((Player) sender).getUniqueId().toString();
            }
        }
        
        String format = "#,##0.#";
        Consumer<NexusPlayer> consumer = nexusPlayer -> {
            //TODO Temporary print the stat name and the value
            sender.sendMessage(MCUtils.color("&6&l>> &aSurvival Games Stats for " + nexusPlayer.getRank().getColor() + nexusPlayer.getName()));
            for (Stat stat : nexusPlayer.getStats().values()) {
                String name = stat.getName();
                if (!name.startsWith("sg_")) {
                    continue;
                }
                
                if (name.contains("tournament") || name.contains("mutat") || name.contains("sponsor")) {
                    continue;
                }
                
                name = stat.getDisplayName();
                sender.sendMessage(MCUtils.color("&6&l> &e" + name + "&7: &b" + new DecimalFormat(format).format(nexusPlayer.getStatValue(stat.getName()))));
            }
        };
        
        try {
            NexusAPI.getApi().getPlayerManager().getNexusPlayerAsync(UUID.fromString(identifier), consumer);
        } catch (Exception e) {
            NexusAPI.getApi().getPlayerManager().getNexusPlayerAsync(identifier, consumer);
        }
        
        return true;
    }
}