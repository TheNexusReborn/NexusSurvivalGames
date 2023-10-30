package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.PlayerStats;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.stats.Stat;
import com.thenexusreborn.api.stats.StatChange;
import com.thenexusreborn.api.storage.codec.RanksCodec;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID uniqueId;
            String name;
            try {
                uniqueId = UUID.fromString(identifier);
                name = NexusAPI.getApi().getPlayerManager().getNameFromUUID(uniqueId);
            } catch (Exception e) {
                uniqueId = NexusAPI.getApi().getPlayerManager().getUUIDFromName(identifier);
                name = NexusAPI.getApi().getPlayerManager().getNameFromUUID(uniqueId);
            }

            if (uniqueId == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a player by that identifier."));
                return;
            }

            sender.sendMessage(MCUtils.color(MsgType.DETAIL + "Please wait while we gather information..."));
            Rank rank;
            try {
                rank = new RanksCodec().decode(NexusAPI.getApi().getPrimaryDatabase().executeQuery("select `ranks` from `players` where `uniqueid` = '" + uniqueId + "';").get(0).getString("ranks")).get();
            } catch (SQLException e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem getting that player's rank information from the database."));
                return;
            }

            List<Stat> stats;
            List<StatChange> statChanges;
            try {
                stats = NexusAPI.getApi().getPrimaryDatabase().get(Stat.class, "uuid", uniqueId.toString());
                statChanges = NexusAPI.getApi().getPrimaryDatabase().get(StatChange.class, "uuid", uniqueId.toString());
            } catch (SQLException e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "There was an error while retrieving that player's stat information from the database."));
                return;
            }
            
            if (stats.isEmpty() && statChanges.isEmpty()) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "That player has no stats yet."));
                return;
            }

            PlayerStats playerStats = new PlayerStats(uniqueId);
            playerStats.addAllStats(stats);
            playerStats.addAllChanges(statChanges);
            
            List<String> lines = new LinkedList<>();
            for (Stat stat : playerStats.findAll()) {
                String statName = stat.getName();
                if (!statName.startsWith("sg_")) {
                    continue;
                }

                if (statName.contains("sponsor")) {
                    continue;
                }

                statName = stat.getDisplayName();
                try {
                    String line = "&6&l> &e" + statName + "&7: &b" + new DecimalFormat(format).format(playerStats.getValue(stat.getName()).get());
                    lines.add(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                if (lines.isEmpty()) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "That player has no Survival Games Stats."));
                    return;
                }

                sender.sendMessage(MCUtils.color("&6&l>> &aSurvival Games Stats for " + rank.getPrefix() + " " + name));
                for (String line : lines) {
                    sender.sendMessage(MCUtils.color(line));
                }
            }
        });
        return true;
    }
}