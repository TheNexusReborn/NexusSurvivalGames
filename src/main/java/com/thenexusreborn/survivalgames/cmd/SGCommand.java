package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.stats.Stat;
import com.thenexusreborn.api.stats.StatHelper;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.Position;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.lobby.StatSign;
import com.thenexusreborn.survivalgames.lobby.TributeSign;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.map.MapSpawn;
import com.thenexusreborn.survivalgames.map.tasks.AnalyzeThread;
import com.thenexusreborn.survivalgames.settings.SettingRegistry;
import com.thenexusreborn.survivalgames.settings.collection.SettingList;
import com.thenexusreborn.survivalgames.settings.object.Setting;
import com.thenexusreborn.survivalgames.settings.object.Setting.Info;
import com.thenexusreborn.survivalgames.util.Operator;
import com.thenexusreborn.survivalgames.util.SGUtils;
import me.firestar311.starlib.api.Value;
import me.firestar311.starlib.api.Value.Type;
import me.firestar311.starsql.api.objects.typehandlers.ValueHandler;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static me.firestar311.starlib.api.Value.Type.INTEGER;

public class SGCommand implements CommandExecutor {

    private final SurvivalGames plugin;

    private boolean viewingWorldBorder;

    public SGCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin.getNexusCore(), sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }

        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub command."));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        Game game = plugin.getGame();
        if (subCommand.equals("game") || subCommand.equals("g")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub command."));
                return true;
            }

            String gameSubCommand = args[1].toLowerCase();

            if (List.of("automatic", "auto", "manual", "mnl").contains(gameSubCommand)) {
                ControlType controlType = null;
                try {
                    controlType = ControlType.valueOf(gameSubCommand.toUpperCase());
                } catch (Exception e) {
                    if (gameSubCommand.equals("auto")) {
                        controlType = ControlType.AUTOMATIC;
                    } else if (gameSubCommand.equals("mnl")) {
                        controlType = ControlType.MANUAL;
                    }
                }

                if (controlType == null) {
                    sender.sendMessage(MCUtils.color(MsgType.SEVERE + "Invalid control type detection. This is a bug, please report to Firestar311"));
                    return true;
                }

                if (Game.getControlType() == controlType) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The game is already in " + controlType.name().toLowerCase() + " "));
                    return true;
                }

                Game.setControlType(controlType);
                sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the game to " + MsgType.INFO.getVariableColor() + controlType.name().toLowerCase() + MsgType.INFO.getBaseColor() + " control."));
                return true;
            }

            if (game == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "There is no prepared/running game."));
                return true;
            }

            if (game.getState() == GameState.ERROR) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "The game had an error, you cannot modify it."));
                return true;
            }

            switch (gameSubCommand) {
                case "setup" -> {
                    if (game.getState() != GameState.UNDEFINED) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The game has already been setup."));
                        return true;
                    }
                    game.setup();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (game.getState() == GameState.SETUP_COMPLETE) {
                                sender.sendMessage(MCUtils.color(MsgType.INFO + "The game setup is now complete."));
                                cancel();
                            } else if (game.getState() == GameState.ERROR) {
                                sender.sendMessage(MCUtils.color(MsgType.ERROR + "There was a problem during Game Setup"));
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 1L, 1L);
                }
                case "assignstartingteams", "ast" -> {
                    if (game.getState() == GameState.SETUP_COMPLETE) {
                        game.assignStartingTeams();
                        if (game.getState() == GameState.TEAMS_ASSIGNED) {
                            sender.sendMessage(MCUtils.color(MsgType.INFO + "Starting teams have been assigned."));
                        } else {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "&cThere was a problem assigning starting teams"));
                        }
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The game is not yet setup. Please run the setup task before assigning teams."));
                    }
                }
                case "teleportplayers", "tpp" -> {
                    if (game.getState() == GameState.TEAMS_ASSIGNED) {
                        game.teleportStart();
                        if (game.getState() == GameState.TELEPORT_START_DONE) {
                            sender.sendMessage(MCUtils.color(MsgType.INFO + "Players have been teleported."));
                        } else {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem teleporting players."));
                        }
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The teams have not be assigned yet. Please run the team assignment task."));
                    }
                }
                case "startwarmupcountdown", "swcd" -> {
                    if (game.getState() == GameState.TELEPORT_START_DONE) {
                        game.startWarmup();
                        if (game.getState() == GameState.WARMUP || game.getState() == GameState.WARMUP_DONE) {
                            sender.sendMessage(MCUtils.color(MsgType.INFO + "The warmup countdown has been started successfully."));
                        } else {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem starting the warmup countdown."));
                        }
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "You must run the teleport players task before starting the countdown."));
                    }
                }
                case "start" -> {
                    if (game.getState() == GameState.WARMUP_DONE || game.getState() == GameState.TELEPORT_START_DONE) {
                        game.startGame();
                    } else if (game.getState() == GameState.WARMUP) {
                        if (game.getTimer() != null) {
                            game.getTimer().cancel();
                        } else {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "The game state was in warmup countdown, but no timer was actively running. Please report this as a bug, started game anyways."));
                        }
                        game.startGame();
                    } else if (game.getState() != GameState.TELEPORT_START_DONE) {
                        sender.sendMessage(MsgType.WARN + "You must run the teleport players task at the minimum before starting the game");
                        return true;
                    }
                    if (game.getState() == GameState.INGAME) {
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "The game has been started."));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem starting the game."));
                    }
                }
                case "startdeathmatchcountdown", "sdmcd" -> {
                    if (game.getState() == GameState.INGAME) {
                        game.startDeathmatchTimer();
                        if (game.getState() == GameState.INGAME_DEATHMATCH) {
                            sender.sendMessage(MCUtils.color(MsgType.INFO + "You started the deathmatch timer"));
                        } else {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem starting the deathmatch timer."));
                        }
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid state. Please ensure that the game is in the INGAME state."));
                    }
                }
                case "teleportdeathmatch", "tpdm" -> {
                    if (game.getState().ordinal() >= GameState.INGAME.ordinal() && game.getState().ordinal() <= GameState.INGAME_DEATHMATCH.ordinal()) {
                        game.teleportDeathmatch();
                        if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                            sender.sendMessage(MCUtils.color(MsgType.INFO + "You teleported everyone to the deathmatch"));
                        } else {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem teleporting players to the deathmatch."));
                        }
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid game state. Must be ingame, ingame deathmatch."));
                    }
                }
                case "startdeathmatchwarmup", "sdmw" -> {
                    if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                        game.startDeathmatchWarmup();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You started the deathmatch warmup"));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The players have not been teleported to the deathmatch, or the deathmatch has already started."));
                    }
                }
                case "startdeathmatch", "sdm" -> {
                    if (Stream.of(GameState.TELEPORT_DEATHMATCH_DONE, GameState.DEATHMATCH_WARMUP, GameState.DEATHMATCH_WARMUP_DONE).anyMatch(gameState -> game.getState() == gameState)) {
                        game.startDeathmatch();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You started the deathmatch"));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "You must at least teleport players to the deathmatch, or it cannot have been started already."));
                    }
                }
                case "end" -> {
                    if (game.getState() != GameState.ENDING && game.getState() != GameState.ENDED) {
                        game.end();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You ended the game."));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The game has already ended"));
                    }
                }
                case "restockchests", "rc" -> {
                    if (game.getState().ordinal() >= GameState.INGAME.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
                        game.restockChests();
                        game.sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED");
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid game state. Must be playing, playing deathmatch, deathmatch countdown or deathmatch countdown complete."));
                    }
                }
                case "nextgame", "ng" -> {
                    if (game.getState() == GameState.ENDING || game.getState() == GameState.ENDED) {
                        game.nextGame();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "Moved everyone to the next game"));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "You must end the game first before going to the next one."));
                    }
                }
            }
        } else if (subCommand.equals("lobby") || subCommand.equals("l")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub command."));
                return true;
            }

            String lobbySubCommand = args[1].toLowerCase();
            switch (lobbySubCommand) {
                case "forcestart", "fs" -> {
                    if (game != null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                        return true;
                    }
                    if (plugin.getLobby().getState() != LobbyState.WAITING) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid state to start the lobby."));
                        return true;
                    }
                    plugin.getLobby().forceStart();
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You forcefully started the lobby."));
                }
                case "map", "m" -> {
                    if (!(args.length > 2)) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a map name."));
                        return true;
                    }
                    if (game != null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                        return true;
                    }
                    GameMap gameMap = plugin.getMapManager().getMap(SGUtils.getMapNameFromCommand(args, 2));
                    if (gameMap == null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a map with that name."));
                        return true;
                    }
                    if (!gameMap.isActive()) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "That map is not active."));
                        return true;
                    }
                    plugin.getLobby().setGameMap(gameMap);
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the map to " + MsgType.INFO.getVariableColor() + gameMap.getName()));
                }
                case "automatic", "auto" -> {
                    if (plugin.getLobby().getControlType() == ControlType.AUTOMATIC) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The lobby is already in automatic control."));
                        return true;
                    }
                    plugin.getLobby().automatic();
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the lobby to automatic control."));
                }
                case "manual", "mnl" -> {
                    if (plugin.getLobby().getControlType() == ControlType.MANUAL) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The lobby is already in manual control."));
                        return true;
                    }
                    plugin.getLobby().manual();
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the lobby to manual control."));
                }
                case "editmaps", "em" -> {
                    if (game != null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                        return true;
                    }
                    if (plugin.getLobby().getState() == LobbyState.MAP_EDITING) {
                        plugin.getLobby().stopEditingMaps();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You stopped editing maps."));
                    } else {
                        plugin.getLobby().editMaps();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You started editing maps."));
                    }
                }
                case "mapsigns", "ms" -> {
                    if (game != null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                        return true;
                    }

                    if (!(args.length > 2)) {
                        sender.sendMessage("&cYou must provide a sub command.");
                        return true;
                    }

                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
                        return true;
                    }

                    Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
                    if (targetBlock == null) {
                        player.sendMessage("&cYou are not looking at a block.");
                        return true;
                    }

                    if (!(targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN)) {
                        player.sendMessage("&cYou are not looking at a sign.");
                        return true;
                    }

                    if (args[2].equalsIgnoreCase("remove")) {
                        Iterator<Entry<Integer, Location>> iterator = plugin.getLobby().getMapSigns().entrySet().iterator();
                        while (iterator.hasNext()) {
                            Entry<Integer, Location> entry = iterator.next();
                            if (entry.getValue().equals(targetBlock.getLocation())) {
                                iterator.remove();
                                player.sendMessage(MCUtils.color(MsgType.WARN + "You removed a sign with the position &b" + entry.getKey()));
                                break;
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("set")) {
                        if (!(args.length > 3)) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a position number."));
                            return true;
                        }

                        int position;
                        try {
                            position = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid number."));
                            return true;
                        }

                        plugin.getLobby().getMapSigns().put(position, targetBlock.getLocation());
                        player.sendMessage(MCUtils.color(MsgType.INFO + "You set the sign you are looking at as a map sign in position &b" + position));
                        plugin.getLobby().generateMapOptions();
                    }
                }
                case "statsigns", "sts" -> {
                    if (game != null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                        return true;
                    }

                    if (!(args.length > 2)) {
                        sender.sendMessage("&cYou must provide a sub command.");
                        return true;
                    }

                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
                        return true;
                    }

                    Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
                    if (targetBlock == null) {
                        player.sendMessage("&cYou are not looking at a block.");
                        return true;
                    }

                    if (!(targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN)) {
                        player.sendMessage("&cYou are not looking at a sign.");
                        return true;
                    }

                    if (args[2].equalsIgnoreCase("remove")) {
                        Iterator<StatSign> iterator = plugin.getLobby().getStatSigns().iterator();
                        while (iterator.hasNext()) {
                            StatSign entry = iterator.next();
                            if (entry.getLocation().equals(targetBlock.getLocation())) {
                                iterator.remove();
                                player.sendMessage(MCUtils.color(MsgType.WARN + "You removed that stat sign"));
                                break;
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("add")) {
                        if (!(args.length > 4)) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /survivalgames lobby statsigns add <stat> <displayName>"));
                            return true;
                        }

                        String stat = args[3];
                        Stat.Info info = StatHelper.getInfo(stat);
                        if (info == null) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid stat name."));
                            return true;
                        }

                        for (StatSign statSign : plugin.getLobby().getStatSigns()) {
                            if (statSign.getStat().equalsIgnoreCase(stat)) {
                                player.sendMessage(MCUtils.color(MsgType.WARN + "A stat sign with that stat already exists. You can only have one per stat."));
                                return true;
                            }
                        }

                        StringBuilder sb = new StringBuilder();
                        for (int i = 4; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }

                        String displayName = ChatColor.stripColor(MCUtils.color(sb.toString().trim()));
                        if (displayName.length() > 14) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "The display name cannot be larger than 14 characters"));
                            return true;
                        }

                        StatSign statSign = new StatSign(targetBlock.getLocation(), stat, displayName);
                        plugin.getLobby().getStatSigns().add(statSign);
                        player.sendMessage(MCUtils.color(MsgType.INFO + "You added a stat sign for &b" + stat + " &ewith the display name &b" + displayName));
                    }
                }
                case "tributesigns", "ts" -> {
                    if (game != null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                        return true;
                    }

                    if (!(args.length > 2)) {
                        sender.sendMessage("&cYou must provide a sub command.");
                        return true;
                    }

                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
                        return true;
                    }

                    Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
                    if (targetBlock == null) {
                        player.sendMessage("&cYou are not looking at a block.");
                        return true;
                    }

                    if (Stream.of(Material.SIGN, Material.WALL_SIGN, Material.SKULL).noneMatch(material -> targetBlock.getType() == material)) {
                        player.sendMessage("&cYou are not looking at a sign or a head.");
                        return true;
                    }

                    if (args[2].equalsIgnoreCase("remove")) {
                        Iterator<TributeSign> iterator = plugin.getLobby().getTributeSigns().iterator();
                        while (iterator.hasNext()) {
                            TributeSign sign = iterator.next();
                            if (sign.getSignLocation().equals(targetBlock.getLocation()) || sign.getHeadLocation().equals(targetBlock.getLocation())) {
                                iterator.remove();
                                player.sendMessage(MCUtils.color(MsgType.INFO + "You removed the tribute sign with index " + sign.getIndex()));
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("set")) {
                        if (!(args.length > 3)) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You must provide an index number."));
                            return true;
                        }

                        int index;
                        try {
                            index = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid number."));
                            return true;
                        }

                        Location signLocation = null, headLocation = null;
                        if (targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN) {
                            signLocation = targetBlock.getLocation();
                        } else if (targetBlock.getType() == Material.SKULL) {
                            headLocation = targetBlock.getLocation();
                        }

                        TributeSign tributeSign = null;
                        for (TributeSign sign : plugin.getLobby().getTributeSigns()) {
                            if (sign.getIndex() == index) {
                                tributeSign = sign;
                                break;
                            }
                        }

                        if (tributeSign == null) {
                            tributeSign = new TributeSign(index, signLocation, headLocation);
                            String msg = "You created a new tribute sign with index &b" + index;
                            if (signLocation == null) {
                                msg += "&e, however you still need to add a sign to it. Just use the same command while looking at a sign.";
                            } else {
                                msg += "&e, however you still need to add a head to it. Just use the same command while looking at a head.";
                            }
                            plugin.getLobby().getTributeSigns().add(tributeSign);
                            player.sendMessage(MCUtils.color(MsgType.INFO + msg));
                            return true;
                        }

                        if (signLocation != null) {
                            tributeSign.setSignLocation(signLocation);
                            player.sendMessage(MCUtils.color(MsgType.INFO + "You set the sign location of the tribute sign at index &b" + index));
                        } else if (headLocation != null) {
                            tributeSign.setHeadLocation(headLocation);
                            player.sendMessage(MCUtils.color(MsgType.INFO + "You set the head location of the tribute sign at index &b" + index));
                        } else {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "Unknown error occured. Please report as a bug."));
                            return true;
                        }
                    }
                }
                case "preparegame", "pg" -> {
                    if (game != null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                        return true;
                    }
                    LobbyState lobbyState = plugin.getLobby().getState();
                    if (lobbyState == LobbyState.WAITING || lobbyState == LobbyState.COUNTDOWN) {
                        plugin.getLobby().prepareGame();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You forcefully had the lobby prepare the game."));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The lobby is in an invalid state to prepare a game."));
                        return true;
                    }
                }
                case "setspawn", "ss" -> {
                    if (game != null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                        return true;
                    }

                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that sub command."));
                        return true;
                    }

                    plugin.getLobby().setSpawnpoint(player.getLocation());
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the lobby spawnpoint to your location."));
                }
                case "debug" -> {
                    Lobby lobby = plugin.getLobby();
                    if (lobby.isDebugMode()) {
                        lobby.disableDebug();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You &cdisabled &elobby debug mode."));
                    } else {
                        lobby.enableDebug();
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You &aenabled &elobby debug mode."));
                    }
                }
            }
        } else if (List.of("settings", "setting", "s").contains(subCommand)) {
            if (!(args.length > 2)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " " + args[0] + " <type> <name | save | list> [value | savename]"));
                return true;
            }

            String type = switch (args[1].toLowerCase()) {
                case "game", "g" -> "game";
                case "lobby", "l" -> "lobby";
                default -> null;
            };

            if (type == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid setting type. Can only be game (g) or lobby (l)"));
                return true;
            }

            SettingRegistry registry = switch (type) {
                case "game" -> plugin.getGameSettingRegistry();
                case "lobby" -> plugin.getLobbySettingRegistry();
                default -> null;
            };

            if (args[2].equalsIgnoreCase("save")) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "This functionality is temporarily disabled."));
                return true;
            }

            if (args[2].equalsIgnoreCase("list")) {
                sender.sendMessage(MCUtils.color(MsgType.INFO + "List of &b" + type + " settings."));

                SettingRegistry settingRegistry = switch (type) {
                    case "game" -> plugin.getGameSettingRegistry();
                    case "lobby" -> plugin.getLobbySettingRegistry();
                    default -> null;
                };

                for (Info setting : settingRegistry.getRegisteredObjects().values()) {
                    TextComponent component = new TextComponent(TextComponent.fromLegacyText(MCUtils.color(" &8- &a" + setting.getName())));
                    StringBuilder sb = new StringBuilder();
                    sb.append("&dName: &e").append(setting.getDisplayName()).append("\n");
                    sb.append("&dDescription: &e").append(setting.getDescription());
                    if (setting.getMinValue() != null) {
                        sb.append("\n").append("&dMin: &e").append(setting.getMinValue().get());
                    }
                    if (setting.getMaxValue() != null) {
                        sb.append("\n").append("&dMax: &e").append(setting.getMaxValue().get());
                    }
                    component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(MCUtils.color(sb.toString()))));
                    if (sender instanceof Player player) {
                        player.spigot().sendMessage(component);
                    } else if (sender instanceof ConsoleCommandSender console) {
                        console.sendMessage(MCUtils.color(component.getText()));
                    }
                }

                return true;
            }

            String settingName = args[2].toLowerCase();
            Setting.Info settingInfo = registry.get(settingName);

            if (settingInfo == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "A setting with that name does not exist."));
                return true;
            }

            Value value;
            try {
                value = (Value) new ValueHandler().getDeserializer().deserialize(null, settingInfo.getDefaultValue().getType() + ":" + args[3]);
            } catch (Exception e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "There was an error parsing the value: " + e.getMessage()));
                return true;
            }

            if (value == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem parsing the value."));
                return true;
            }

            Value minValue = settingInfo.getMinValue();
            Value maxValue = settingInfo.getMaxValue();
            if (minValue != null && maxValue != null) {
                if (List.of(INTEGER, Type.DOUBLE, Type.LONG).contains(value.getType())) {
                    boolean lowerInBounds, upperInBounds;
                    if (value.getType() == Type.INTEGER) {
                        lowerInBounds = value.getAsInt() >= minValue.getAsInt();
                        upperInBounds = value.getAsInt() <= maxValue.getAsInt();
                    } else if (value.getType() == Type.DOUBLE) {
                        lowerInBounds = value.getAsDouble() >= minValue.getAsDouble();
                        upperInBounds = value.getAsDouble() <= maxValue.getAsDouble();
                    } else {
                        lowerInBounds = value.getAsLong() >= minValue.getAsLong();
                        upperInBounds = value.getAsLong() <= maxValue.getAsLong();
                    }

                    if (!lowerInBounds) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The value you provided is less than the minimum allowed value. Min: " + minValue.get()));
                        return true;
                    }

                    if (!upperInBounds) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The value you provided is greater than the maximum allowed value. Max: " + maxValue.get()));
                        return true;
                    }
                }
            }

            SettingList<?> settingList;
            if (type.equalsIgnoreCase("game")) {
                if (game == null) {
                    settingList = plugin.getLobby().getGameSettings();
                } else {
                    settingList = plugin.getGame().getSettings();
                }
            } else {
                settingList = plugin.getLobby().getLobbySettings();
            }

            settingList.setValue(settingName, value.get());
            Object settingValue = settingList.get(settingName).getValue().get();
            if (!Objects.equals(value.get(), settingValue)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "The actual setting value is not equal to the new value. Please report to Firestar311."));
                return true;
            }

            sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the &b" + type.toLowerCase() + " &esetting &b" + settingName + " &eto &b" + value.get()));
        } else if (subCommand.equals("map") || subCommand.equals("m")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub command."));
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
                return true;
            }

            String mapSubCommand = args[1].toLowerCase();

            if (plugin.getLobby().getState() != LobbyState.MAP_EDITING) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You can only use that command when the lobby is in the map editing mode."));
                return true;
            }

            if (mapSubCommand.equals("create") || mapSubCommand.equals("c")) {
                if (!(args.length > 3)) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " " + subCommand + " " + mapSubCommand + " <url> <name>"));
                    return true;
                }

                String url = args[2].replace("{url}", SurvivalGames.MAP_URL);
                String mapName = SGUtils.getMapNameFromCommand(args, 3);
                if (plugin.getMapManager().getMap(mapName) != null) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "A map with that name already exists."));
                    return true;
                }

                GameMap gameMap = new GameMap(url, mapName);
                plugin.getMapManager().addMap(gameMap);
                sender.sendMessage(MCUtils.color(MsgType.INFO + "Created a map with the name " + MsgType.INFO.getVariableColor() + gameMap.getName() + MsgType.INFO.getBaseColor() + "."));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getMapManager().saveToDatabase(gameMap);
                        sender.sendMessage(MCUtils.color(MsgType.VERBOSE + "The map has been saved to the database."));
                    }
                }.runTaskAsynchronously(plugin);
            } else {
                GameMap gameMap = null;
                boolean mapFromArgument = false;
                for (GameMap map : plugin.getMapManager().getMaps()) {
                    if (map.getWorld() != null) {
                        if (map.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
                            gameMap = map;
                            break;
                        }
                    }
                }

                if (args.length > 2 && gameMap == null) {
                    gameMap = plugin.getMapManager().getMap(args[2]);
                    mapFromArgument = true;
                }

                if (gameMap == null) {
                    player.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a valid map."));
                    return true;
                }

                switch (mapSubCommand) {
                    case "download", "dl" -> {
                        GameMap finalGameMap = gameMap;
                        plugin.getLobby().setGameMap(finalGameMap);
                        player.sendMessage(MCUtils.color(MsgType.VERBOSE + "Please wait, downloading the map " + MsgType.VERBOSE.getVariableColor() + finalGameMap.getName() + MsgType.VERBOSE.getBaseColor() + "."));
                        NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
                            boolean successful = finalGameMap.download(plugin);
                            if (successful) {
                                player.sendMessage(MCUtils.color(MsgType.INFO + "Downloaded the map " + MsgType.INFO.getVariableColor() + finalGameMap.getName() + MsgType.INFO.getBaseColor() + "."));
                            } else {
                                player.sendMessage(MCUtils.color(MsgType.ERROR + "Failed to download the map " + MsgType.ERROR.getVariableColor() + finalGameMap.getName()));
                            }
                        });
                        return true;
                    }
                    case "load", "l" -> {
                        gameMap.unzip(plugin);
                        gameMap.copyFolder(plugin, false);
                        gameMap.load(plugin);
                        if (gameMap.getWorld() != null) {
                            sender.sendMessage(MCUtils.color(MsgType.INFO + "Successfully loaded the map " + MsgType.INFO.getVariableColor() + gameMap.getName() + MsgType.INFO.getBaseColor() + "."));
                        } else {
                            sender.sendMessage(MCUtils.color(MsgType.ERROR + "Could not load the map " + MsgType.ERROR.getVariableColor() + gameMap.getName() + MsgType.ERROR.getBaseColor() + ". Please report as a bug."));
                        }
                        return true;
                    }
                    case "teleport", "tp" -> {
                        Location spawn;
                        if (gameMap.getWorld() == null) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "That map is not loaded. Please load before teleporting."));
                            return true;
                        }
                        if (gameMap.getCenter() != null) {
                            spawn = gameMap.getCenter().toLocation(gameMap.getWorld());
                        } else {
                            spawn = gameMap.getWorld().getSpawnLocation();
                        }
                        player.teleport(spawn);
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Teleported to the map " + MsgType.INFO.getVariableColor() + gameMap.getName()));
                        return true;
                    }
                    case "save" -> {
                        plugin.getMapManager().saveToDatabase(gameMap);
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Saved the settings for the map " + MsgType.INFO.getVariableColor() + gameMap.getName()));
                    }
                    case "delete" -> {
                        gameMap.delete(plugin);
                        plugin.getLobby().setGameMap(null);
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Deleted the map " + MsgType.INFO.getVariableColor() + gameMap.getName() + MsgType.INFO.getBaseColor() + " from the server."));
                        return true;
                    }
                    case "addspawn", "as" -> {
                        Location location = player.getLocation();
                        int position = gameMap.addSpawn(new MapSpawn(0, -1, location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You added a spawn with index &b" + position + 1 + " &eto the map &b" + gameMap.getName()));
                    }
                    case "setspawn", "sp" -> {
                        int argIndex;
                        if (mapFromArgument) {
                            argIndex = 3;
                        } else {
                            argIndex = 2;
                        }

                        if (!(args.length > argIndex)) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide an index for the spawn."));
                            return true;
                        }

                        int position;
                        try {
                            position = Integer.parseInt(args[argIndex]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid number for the spawn index."));
                            return true;
                        }

                        Location location = player.getLocation();
                        gameMap.setSpawn(position, new MapSpawn(gameMap.getId(), position, location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the spawn at position &b" + position + " &eto your location in the map &b" + gameMap.getName()));
                    }
                    case "setcenter", "sc" -> {
                        Location location = player.getPlayer().getLocation();
                        gameMap.setCenter(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                        player.sendMessage(MCUtils.color(MsgType.INFO + "You set the center of the map &b" + gameMap.getName() + " &eto your current location."));
                    }
                    case "setborderradius", "sbr" -> {
                        int argIndex;
                        if (mapFromArgument) {
                            argIndex = 3;
                        } else {
                            argIndex = 2;
                        }

                        if (!(args.length > argIndex)) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a radius."));
                            return true;
                        }

                        int radius;
                        try {
                            radius = Integer.parseInt(args[argIndex]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid number for the radius."));
                            return true;
                        }

                        gameMap.setBorderDistance(radius);
                        if (this.viewingWorldBorder) {
                            gameMap.applyWorldBoarder(GameState.INGAME);
                        }
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the border radius on map &b" + gameMap.getName() + " &eto &b" + radius));
                    }
                    case "setdeathmatchborderradius", "sdmbr" -> {
                        int argIndex;
                        if (mapFromArgument) {
                            argIndex = 3;
                        } else {
                            argIndex = 2;
                        }

                        if (!(args.length > argIndex)) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a radius."));
                            return true;
                        }

                        int radius;
                        try {
                            radius = Integer.parseInt(args[argIndex]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid number for the radius."));
                            return true;
                        }

                        gameMap.setDeathmatchBorderDistance(radius);
                        if (this.viewingWorldBorder) {
                            gameMap.applyWorldBoarder(GameState.DEATHMATCH);
                        }
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the deathmatch border radius on map &b" + gameMap.getName() + " &eto &b" + radius));
                    }
                    case "creators" -> {
                        int argIndex;
                        if (mapFromArgument) {
                            argIndex = 3;
                        } else {
                            argIndex = 2;
                        }

                        if (!(args.length > argIndex)) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide the creators."));
                            return true;
                        }

                        StringBuilder cb = new StringBuilder();
                        for (int i = argIndex; i < args.length; i++) {
                            cb.append(args[i]).append(" ");
                        }

                        String[] creators = cb.toString().trim().split(",");
                        if (creators.length == 0) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must separate the creators with commas."));
                            return true;
                        }

                        for (String creator : creators) {
                            gameMap.addCreator(creator);
                            sender.sendMessage(MCUtils.color(MsgType.INFO + "You added " + MsgType.INFO.getVariableColor() + creator + MsgType.INFO.getBaseColor() + " as a creator on map " + MsgType.INFO.getVariableColor() + gameMap.getName()));
                        }
                    }
                    case "setactive" -> {
                        int argIndex;
                        if (mapFromArgument) {
                            argIndex = 3;
                        } else {
                            argIndex = 2;
                        }

                        if (!(args.length > argIndex)) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a true or false value."));
                            return true;
                        }

                        boolean value = Boolean.parseBoolean(args[argIndex]);
                        gameMap.setActive(value);
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the status of the map to " + MsgType.INFO.getVariableColor() + value));
                    }
                    case "setswagshack", "sss" -> {
                        Location location = player.getPlayer().getLocation();
                        gameMap.setSwagShack(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                        player.sendMessage(MCUtils.color(MsgType.INFO + "You set the swag shack of the map &b" + gameMap.getName() + " &eto your current location."));
                    }
                    case "viewborder", "vb" -> {
                        if (!(args.length > 2)) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You must say if it is for the game or deathmatch."));
                            return true;
                        }

                        GameState state = switch (args[2].toLowerCase()) {
                            case "game" -> GameState.INGAME;
                            case "deathmatch" -> GameState.DEATHMATCH;
                            default -> null;
                        };

                        if (state == null) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid type."));
                            return true;
                        }

                        gameMap.applyWorldBoarder(state);
                        this.viewingWorldBorder = true;
                        player.sendMessage(MCUtils.color(MsgType.INFO + "You are now viewing the world border as " + args[2].toLowerCase()));
                        return true;
                    }
                    case "disableworldborder", "dwb" -> {
                        if (this.viewingWorldBorder) {
                            this.viewingWorldBorder = false;
                            gameMap.disableWorldBorder();
                            player.sendMessage(MCUtils.color(MsgType.INFO + "You disabled the world border preview."));
                        } else {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "The world border is not being previewed."));
                        }
                        return true;
                    }
                    case "analyze" -> {
                        gameMap.setChests(0);
                        gameMap.setEnchantTables(0);
                        gameMap.setWorkbenches(0);
                        gameMap.setTotalBlocks(0);
                        gameMap.setFurnaces(0);
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Performing map analysis on " + gameMap.getName()));
                        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new AnalyzeThread(plugin, gameMap, player), 1L);
                        return true;
                    }
                    case "analysis" -> {
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Map analysis results for &b" + gameMap.getName()));
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Total Blocks: &b" + gameMap.getTotalBlocks()));
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Total Chests: &b" + gameMap.getChests()));
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Total Workbenches: &b" + gameMap.getWorkbenches()));
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Total Enchantment Tables: &b" + gameMap.getEnchantTables()));
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Total Furnaces: &b" + gameMap.getFurnaces()));
                        return true;
                    }
                    case "downloadloadteleport", "dlltp" -> {
                        GameMap finalGameMap = gameMap;
                        plugin.getLobby().setGameMap(finalGameMap);
                        player.sendMessage(MCUtils.color(MsgType.VERBOSE + "Please wait, setting up the map, then teleporting you to " + MsgType.VERBOSE.getVariableColor() + finalGameMap.getName() + MsgType.VERBOSE.getBaseColor() + "."));
                        NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
                            if (!finalGameMap.download(plugin)) {
                                player.sendMessage(MCUtils.color(MsgType.WARN + "Failed to download the map " + finalGameMap.getName()));
                                return;
                            }

                            finalGameMap.unzip(plugin);
                            finalGameMap.copyFolder(plugin, false);
                            NexusAPI.getApi().getScheduler().runTask(() -> {
                                if (!finalGameMap.load(plugin)) {
                                    sender.sendMessage(MCUtils.color(MsgType.ERROR + "Could not load the map " + MsgType.ERROR.getVariableColor() + finalGameMap.getName() + MsgType.ERROR.getBaseColor() + ". Please report as a bug."));
                                    return;
                                }

                                Location spawn;
                                if (finalGameMap.getCenter() != null) {
                                    spawn = finalGameMap.getCenter().toLocation(finalGameMap.getWorld());
                                } else {
                                    spawn = finalGameMap.getWorld().getSpawnLocation();
                                }
                                player.teleport(spawn);
                                player.sendMessage(MCUtils.color(MsgType.INFO + "Successfully setup and teleported you to the map " + MsgType.INFO.getVariableColor() + finalGameMap.getName()));
                            });
                        });
                        return true;
                    }
                    default -> {
                        return true;
                    }
                }
                GameMap finalGameMap = gameMap;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getMapManager().saveToDatabase(finalGameMap);
                        sender.sendMessage(MCUtils.color(MsgType.VERBOSE + "The map has been saved to the database."));
                    }
                }.runTaskAsynchronously(plugin);
            }
        } else if (subCommand.equals("timer") || subCommand.equals("t")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub command."));
                return true;
            }

            Timer timer = null;
            String timerType;
            if (game != null) {
                timer = game.getTimer();
                timerType = "game";
            } else {
                //TODO timer = plugin.getLobby().getTimer();
                timerType = "lobby";
            }

            if (timer == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "The " + timerType + " does not have an active timer. Nothing to control."));
                return true;
            }

            String timerSubCommand = args[1].toLowerCase();
            switch (timerSubCommand) {
                case "pause" -> {
                    if (timer.isPaused()) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The timer is already paused."));
                        return true;
                    }
                    timer.setPaused(true);
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You paused the timer."));
                }
                case "resume" -> {
                    if (!timer.isPaused()) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The timer is not paused."));
                        return true;
                    }
                    timer.setPaused(false);
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You paused the timer."));
                }
                case "reset" -> {
                    timer.reset();
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You reset the timer."));
                }
                case "modify" -> {
                    if (!(args.length > 2)) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a value."));
                        return true;
                    }
                    String input = args[2];
                    Operator operator = Operator.getOperator(input);
                    if (operator != null) {
                        input = input.substring(1);
                    }
                    int seconds;
                    try {
                        seconds = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(MsgType.WARN + "You provided an invalid integer value.");
                        return true;
                    }
                    long milliseconds = seconds * 1000L + 50;
                    if (operator == null) {
                        timer.setLength(milliseconds);
                    } else {
                        switch (operator) {
                            case ADD -> {
                                long newTime = timer.getTimeLeft() + milliseconds;
                                if (newTime > timer.getLength()) {
                                    newTime = timer.getLength();
                                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The new timer length exceeds the specified length of the timer. Using the max length. Please use this command without an operator to change the specified length"));
                                }
                                timer.setRawTime(newTime);
                            }
                            case SUBTRACT -> {
                                long newTime = timer.getTimeLeft() - milliseconds;
                                if (newTime <= 0) {
                                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The new timer length is less than or equal to 0. Please use the timer cancel command instead."));
                                    return true;
                                }

                                timer.setRawTime(newTime);
                            }
                            case MULTIPLY -> {
                                long newTime = timer.getTimeLeft() * milliseconds;
                                if (newTime > timer.getLength()) {
                                    newTime = timer.getLength();
                                    sender.sendMessage(MCUtils.color(MsgType.VERBOSE + "The new timer length exceeds the specified length of the timer. Using the max length. Please use this command without an operator to change the specified length"));
                                }
                                timer.setRawTime(newTime);
                            }
                            case DIVIDE -> {
                                if (milliseconds == 0) {
                                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Cannot divide by zero"));
                                    return true;
                                }
                                long newTime = timer.getTimeLeft() / milliseconds;
                                if (newTime <= 0) {
                                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The new timer length is less than or equal to 0. Please use the timer cancel command instead."));
                                    return true;
                                }

                                timer.setRawTime(newTime);
                            }
                        }
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You modified the " + timerType + " timer."));
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("skull")) {
            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
            Skull skull = (Skull) targetBlock.getState();
            player.sendMessage(skull.getOwner());
        }

        return true;
    }
}
