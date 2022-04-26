package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.tournament.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TournamentCommand implements TabExecutor {
    
    private SurvivalGames plugin;
    
    public TournamentCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin.getNexusCore(), sender);
        
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color("&cYou must provide a sub command"));
            return true;
        }
        
        Tournament tournament = plugin.getTournament();
        
        if (args[0].equalsIgnoreCase("create")) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
            
            if (tournament != null) {
                sender.sendMessage("&cThere already is a tournament created.");
                return true;
            }
            
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color("&cYou must provide a host."));
                return true;
            }
            
            UUID host = getUUIDFromInput(args[1], sender);
            
            if (host == null) {
                sender.sendMessage(MCUtils.color("&cYou did not provide a valid host."));
                return true;
            }
            
            if (!(args.length > 2)) {
                sender.sendMessage(MCUtils.color("&cYou must provide a name for the tournament"));
                return true;
            }
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                nameBuilder.append(args[i]).append(" ");
            }
            
            tournament = new Tournament(host, nameBuilder.substring(0, nameBuilder.length() - 1));
            plugin.setTournament(tournament);
            sender.sendMessage(MCUtils.color("&eSuccessfully created a tournament with the name &b" + tournament.getName()));
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
            
            if (tournament == null) {
                sender.sendMessage(MCUtils.color("&cThere is no tournament set up right now."));
                return true;
            }
            
            plugin.setTournament(null);
            sender.sendMessage(MCUtils.color("&eYou successfully deleted the tournament"));
        } else {
            if (tournament == null) {
                sender.sendMessage(MCUtils.color("&cThere is not tournament currently setup."));
                return true;
            }
            
            if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("lb")) {
                Tournament finalTournament = tournament;
                sender.sendMessage(MCUtils.color("&7&oPlease wait, generating leaderboard..."));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        SortedSet<ScoreInfo> leaderboard = new TreeSet<>();
                        finalTournament.getScores().forEach((uuid, score) -> leaderboard.add(new ScoreInfo(uuid, score)));
                        List<String> lines = new LinkedList<>();
                        int place = 1;
                        for (ScoreInfo scoreInfo : leaderboard) {
                            NexusPlayer player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(scoreInfo.getUuid());
                            lines.add("  &a" + place + ". " + player.getRank().getColor() + player.getName() + " &7-> &f" + scoreInfo.getScore());
                            place++;
                        }
                        sender.sendMessage(MCUtils.color("&eLeaderboard for tournament &b" + finalTournament.getName()));
                        lines.forEach(line -> sender.sendMessage(MCUtils.color(line)));
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            } else if (args[0].equalsIgnoreCase("score")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MCUtils.color("&cOnly players can use that command."));
                    return true;
                }
                
                Player player = (Player) sender;
                int score = tournament.getScores().getOrDefault(player.getUniqueId(), 0);
                player.sendMessage(MCUtils.color("&eYour score is currently &b" + score));
                return true;
            }
            
            boolean isHost, isAdmin;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                isHost = player.getUniqueId().equals(tournament.getHost());
                isAdmin = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId()).getRank().ordinal() <= Rank.ADMIN.ordinal();
            } else {
                isHost = false;
                isAdmin = true;
            }
            
            if (!(isHost || isAdmin)) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to do that."));
                return true;
            }
            
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color("&cNot enough arguments."));
                return true;
            }
            
            if (args[0].equalsIgnoreCase("setactive")) {
                boolean value = Boolean.parseBoolean(args[1]);
                tournament.setActive(value);
                sender.sendMessage(MCUtils.color("&eYou set the tournament active status to &b" + value));
            } else if (args[0].equalsIgnoreCase("setname") || args[0].equalsIgnoreCase("sn")) {
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    nameBuilder.append(args[i]).append(" ");
                }
                tournament.setName(nameBuilder.substring(0, nameBuilder.length() - 1));
                sender.sendMessage(MCUtils.color("&eYou set the name of the tournament to &b" + tournament.getName()));
            } else if (args[0].equalsIgnoreCase("setpointsperwin") || args[0].equalsIgnoreCase("sppw")) {
                int value = getIntFromInput(args[1], sender);
                if (value == -1) {
                    return true;
                }
                tournament.setPointsPerWin(value);
                sender.sendMessage(MCUtils.color("&eYou set the points per win to &b" + value));
            } else if (args[0].equalsIgnoreCase("setpointsperkill") || args[0].equalsIgnoreCase("sppk")) {
                int value = getIntFromInput(args[1], sender);
                if (value == -1) {
                    return true;
                }
                tournament.setPointsPerKill(value);
                sender.sendMessage(MCUtils.color("&eYou set the points per kill to &b" + value));
            } else if (args[0].equalsIgnoreCase("setpointspersurvival") || args[0].equalsIgnoreCase("spps")) {
                int value = getIntFromInput(args[1], sender);
                if (value == -1) {
                    return true;
                }
                tournament.setPointsPerSurvival(value);
                sender.sendMessage(MCUtils.color("&eYou set the points per survival to &b" + value));
            } else {
                UUID uuid = getUUIDFromInput(args[1], sender);
                if (uuid == null) {
                    return true;
                }
                
                NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
                String name;
                if (nexusPlayer != null) {
                    name = nexusPlayer.getName();
                } else {
                    name = uuid.toString();
                }
                
                if (args[0].equalsIgnoreCase("sethost") || args[0].equalsIgnoreCase("sh")) {
                    tournament.setHost(uuid);
                    sender.sendMessage(MCUtils.color("&eYou set the host of the tournament to &e" + name));
                } else if (args[0].equalsIgnoreCase("addparticipant") || args[0].equalsIgnoreCase("ap")) {
                    tournament.getParticipants().add(uuid);
                    sender.sendMessage(MCUtils.color("&eYou added &b" + name + " &eto the tournament as a participant"));
                } else if (args[0].equalsIgnoreCase("removeparticipant") || args[0].equalsIgnoreCase("rp")) {
                    tournament.getParticipants().remove(uuid);
                    sender.sendMessage(MCUtils.color("&eYou removed &b" + name + " &efrom the tournament"));
                }
            }
        }
        
        return true;
    }
    
    private int getIntFromInput(String input, CommandSender sender) {
        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            sender.sendMessage(MCUtils.color("&cYou provided an invalid number value."));
            return -1;
        }
        
        if (value < 0) {
            sender.sendMessage(MCUtils.color("&cYou must provide a positive number or 0"));
            return -1;
        }
        
        return value;
    }
    
    private UUID getUUIDFromInput(String input, CommandSender sender) {
        UUID uuid;
        try {
            uuid = UUID.fromString(input);
        } catch (Exception e) {
            NexusPlayer nexusPlayer =NexusAPI.getApi().getPlayerManager().getNexusPlayer(input);
            if (nexusPlayer == null) {
                sender.sendMessage(MCUtils.color("&cA player with that name has not joined the server. Please provide a valid UUID"));
                return null;
            }
            
            uuid = nexusPlayer.getUniqueId();
        }
        return uuid;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
