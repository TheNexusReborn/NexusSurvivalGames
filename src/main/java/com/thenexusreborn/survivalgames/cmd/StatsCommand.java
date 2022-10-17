package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.stats.Stat;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private final SurvivalGames plugin;

    public StatsCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }

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
        sender.sendMessage(MCUtils.color(MsgType.DETAIL + "Please wait while we gather information..."));
        new BukkitRunnable() {
            public void run() {
                NexusPlayer nexusPlayer;

                try {
                    UUID uuid = UUID.fromString(identifier);
                    nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
                    if (nexusPlayer == null) {
                        nexusPlayer = NexusAPI.getApi().getPlayerManager().getCachedPlayer(uuid).loadFully();
                    }
                } catch (Exception e) {
                    nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(identifier);
                    if (nexusPlayer == null) {
                        nexusPlayer = NexusAPI.getApi().getPlayerManager().getCachedPlayer(identifier).loadFully();
                    }
                }

                if (nexusPlayer == null) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a player by that identifier."));
                    return;
                }

                sender.sendMessage(MCUtils.color("&6&l>> &aSurvival Games Stats for " + nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName()));
                for (Stat stat : nexusPlayer.getStats().findAll()) {
                    String name = stat.getName();
                    if (!name.startsWith("sg_")) {
                        continue;
                    }

                    if (name.contains("mutat") || name.contains("sponsor")) {
                        continue;
                    }

                    name = stat.getDisplayName();
                    sender.sendMessage(MCUtils.color("&6&l> &e" + name + "&7: &b" + new DecimalFormat(format).format(nexusPlayer.getStats().getValue(stat.getName()))));
                }
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
}