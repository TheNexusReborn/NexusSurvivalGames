package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.util.Operator;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.map.*;
import com.thenexusreborn.survivalgames.settings.*;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class SGCommand implements CommandExecutor {
    
    private SurvivalGames plugin;
    
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
    
            if (gameSubCommand.equals("automatic") || gameSubCommand.equals("auto") || gameSubCommand.equals("manual") || gameSubCommand.equals("mnl")) {
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
            
            if (gameSubCommand.equals("setup")) {
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
            } else if (gameSubCommand.equals("assignstartingteams") || gameSubCommand.equals("ast")) {
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
            } else if (gameSubCommand.equals("teleportplayers") || gameSubCommand.equals("tpp")) {
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
            } else if (gameSubCommand.equals("startwarmupcountdown") || gameSubCommand.equals("swcd")) {
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
            } else if (gameSubCommand.equals("start")) {
                if (game.getState() == GameState.WARMUP_DONE || game.getState() == GameState.TELEPORT_START_DONE) {
                    game.startGame();
                } else if (game.getState() == GameState.WARMUP) {
                    if (game.getTimer() != null) {
                        game.getTimer().cancel();
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "The game state was in warmup countdown, but no timer was actively running. Please report this as a bug, started game anyways."));
                    }
                    game.startGame();
                } else if (game.getState() != GameState.TELEPORT_START_DONE){
                    sender.sendMessage(MsgType.WARN + "You must run the teleport players task at the minimum before starting the game");
                    return true;
                }
    
                if (game.getState() == GameState.INGAME) {
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "The game has been started."));
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem starting the game."));
                }
            } else if (gameSubCommand.equals("startdeathmatchcountdown") || gameSubCommand.equals("sdmcd")) {
                if (game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_GRACEPERIOD) {
                    game.startDeathmatchTimer();
                    if (game.getState() == GameState.INGAME_DEATHMATCH) {
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You started the deathmatch timer"));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem starting the deathmatch timer."));
                    }
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid state. Please ensure that the game is in the INGAME state."));
                }
            } else if (gameSubCommand.equals("teleportdeathmatch") || gameSubCommand.equals("tpdm")) {
                if (game.getState().ordinal() >= GameState.INGAME_GRACEPERIOD.ordinal() && game.getState().ordinal() <= GameState.INGAME_DEATHMATCH.ordinal()) {
                    game.teleportDeathmatch();
                    if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You teleported everyone to the deathmatch"));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem teleporting players to the deathmatch."));
                    }
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid game state. Must be ingame, ingame deathmatch."));
                }
            } else if (gameSubCommand.equals("startdeathmatchwarmup") || gameSubCommand.equals("sdmw")) {
                if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                    game.startDeathmatchWarmup();
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You started the deathmatch warmup"));
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The players have not been teleported to the deathmatch, or the deathmatch has already started."));
                }
            } else if (gameSubCommand.equals("startdeathmatch") || gameSubCommand.equals("sdm")) {
                if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE || game.getState() == GameState.DEATHMATCH_WARMUP || game.getState() == GameState.DEATHMATCH_WARMUP_DONE) {
                    game.startDeathmatch();
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You started the deathmatch"));
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "You must at least teleport players to the deathmatch, or it cannot have been started already."));
                }
            } else if (gameSubCommand.equals("end")) {
                if (game.getState() != GameState.ENDING && game.getState() != GameState.ENDED) {
                    game.end();
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You ended the game."));
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The game has already ended"));
                }
            } else if (gameSubCommand.equals("restockchests") || gameSubCommand.equals("rc")) {
                if (game.getState().ordinal() >= GameState.INGAME_GRACEPERIOD.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
                    game.restockChests();
                    game.sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED");
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid game state. Must be playing, playing deathmatch, deathmatch countdown or deathmatch countdown complete."));
                }
            } else if (gameSubCommand.equals("nextgame") || gameSubCommand.equals("ng")) {
                if (game.getState() == GameState.ENDING || game.getState() == GameState.ENDED) {
                    game.nextGame();
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "Moved everyone to the next game"));
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "You must end the game first before going to the next one."));
                }
            }
        } else if (subCommand.equals("lobby") || subCommand.equals("l")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub command."));
                return true;
            }
            
            String lobbySubCommand = args[1].toLowerCase();
            if (lobbySubCommand.equals("forcestart") || lobbySubCommand.equals("fs")) {
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
            } else if (lobbySubCommand.equals("map") || lobbySubCommand.equals("m")) {
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
                
                plugin.getLobby().setGameMap(gameMap);
                sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the map to " + MsgType.INFO.getVariableColor() + gameMap.getName()));
            } else if (lobbySubCommand.equals("automatic") || lobbySubCommand.equals("auto")) {
                if (plugin.getLobby().getControlType() == ControlType.AUTOMATIC) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The lobby is already in automatic control."));
                    return true;
                }
                
                plugin.getLobby().automatic();
                sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the lobby to automatic control."));
            } else if (lobbySubCommand.equals("manual") || lobbySubCommand.equals("mnl")) {
                if (plugin.getLobby().getControlType() == ControlType.MANUAL) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The lobby is already in manual control."));
                    return true;
                }
                
                plugin.getLobby().manual();
                sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the lobby to manual control."));
            } else if (lobbySubCommand.equals("editmaps") || lobbySubCommand.equals("em")) {
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
            } else if (lobbySubCommand.equals("mapsigns") || lobbySubCommand.equals("ms")) {
                if (game != null) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                    return true;
                }
                
                if (!(args.length > 2)) {
                    sender.sendMessage("&cYou must provide a sub command.");
                    return true;
                }
                
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
                    return true;
                }
                Player player = (Player) sender;
                
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
            } else if (lobbySubCommand.equals("preparegame") || lobbySubCommand.equals("pg")) {
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
            } else if (lobbySubCommand.equals("setspawn") || lobbySubCommand.equals("ss")) {
                if (game != null) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The server has a game in progress."));
                    return true;
                }
                
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that sub command."));
                    return true;
                }
                
                Player player = (Player) sender;
                plugin.getLobby().setSpawnpoint(player.getLocation());
                sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the lobby spawnpoint to your location."));
            }
        } else if (subCommand.equals("settings") || subCommand.equals("setting") || subCommand.equals("s")) {
            if (!(args.length > 3)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " " + args[0] + " <type> <name> <value>"));
                return true;
            }
            String type = args[1];
            String settingName = args[2];
            String rawValue = args[3];
            
            Field settingField = null;
            if (type.equalsIgnoreCase("game")) {
                for (Field field : GameSettings.class.getDeclaredFields()) {
                    if (field.getName().equalsIgnoreCase(settingName)) {
                        settingField = field;
                        break;
                    }
                }
            } else if (type.equalsIgnoreCase("lobby")) {
                for (Field field : LobbySettings.class.getDeclaredFields()) {
                    if (field.getName().equalsIgnoreCase(settingName)) {
                        settingField = field;
                        break;
                    }
                }
            } else {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid setting type. Only Lobby and Game are allowed (Case insensitive)"));
                return true;
            }
            
            if (settingField == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "A setting with that name does not exist."));
                return true;
            }
            
            Object value;
            if (settingField.getType().equals(int.class)) {
                try {
                    value = Integer.parseInt(rawValue);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid number for that setting."));
                    return true;
                }
            } else if (settingField.getType().equals(boolean.class)) {
                if (rawValue.equalsIgnoreCase("true") || rawValue.equalsIgnoreCase("yes")) {
                    value = true;
                } else if (rawValue.equalsIgnoreCase("false") || rawValue.equalsIgnoreCase("no")) {
                    value = false;
                } else {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid value, only true, false, yes and no are accepted for that setting"));
                    return true;
                }
            } else if (settingField.getType().equals(Time.class)) {
                try {
                    value = Time.valueOf(rawValue.toUpperCase());
                } catch (Exception e) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid time value"));
                    return true;
                }
            } else if (settingField.getType().equals(Weather.class)) {
                try {
                    value = Weather.valueOf(rawValue.toUpperCase());
                } catch (Exception e) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid weather value"));
                    return true;
                }
            } else if (settingField.getType().equals(ColorMode.class)) {
                try {
                    value = ColorMode.valueOf(rawValue.toUpperCase());
                } catch (Exception e) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "You provided an invalid color mode value."));
                    return true;
                }
            } else {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Unhandled setting type: " + settingField.getType().getName() + ". This is a BUG"));
                return true;
            }
            
            try {
                settingField.setAccessible(true);
                Object object = null;
                if (type.equalsIgnoreCase("game")) {
                    if (game == null) {
                        if (plugin.getLobby().getGameSettings() == null) {
                            plugin.getLobby().setGameSettings(new GameSettings());
                        }
                        object = plugin.getLobby().getGameSettings();
                    } else {
                        object = game.getSettings();
                    }
                } else if (type.equalsIgnoreCase("lobby")) {
                    object = plugin.getLobby().getLobbySettings();
                }
                settingField.set(object, value);
                sender.sendMessage(MCUtils.color("&6&l>> &eYou set the &b" + type.toLowerCase() + " &esetting &b" + settingField.getName() + " &eto &b" + value)); //TODO MsgType
            } catch (Exception e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Error while setting the value. Please report with the following message."));
                sender.sendMessage(MCUtils.color(MsgType.WARN + "" + e.getClass().getName() + ": " + e.getMessage()));
            }
        } else if (subCommand.equals("map") || subCommand.equals("m")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub command."));
                return true;
            }
            
            if (!(sender instanceof Player)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
                return true;
            }
            
            Player player = (Player) sender;
            
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
                
                String url = args[2];
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
                
                if (mapSubCommand.equals("download") || mapSubCommand.equals("dl")) {
                    GameMap finalGameMap = gameMap;
                    player.sendMessage(MCUtils.color(MsgType.VERBOSE + "Please wait, downloading the map " + MsgType.VERBOSE.getVariableColor() + finalGameMap.getName() + MsgType.VERBOSE.getBaseColor() + "."));
                    NexusAPI.getApi().getThreadFactory().runAsync(() -> {
                        finalGameMap.download(plugin);
                        player.sendMessage(MCUtils.color(MsgType.INFO + "Downloaded the map " + MsgType.INFO.getVariableColor() + finalGameMap.getName() + MsgType.INFO.getBaseColor() + "."));
                    });
                } else if (mapSubCommand.equals("load") || mapSubCommand.equals("l")) {
                    gameMap.unzip(plugin);
                    gameMap.copyFolder(plugin, false);
                    gameMap.load(plugin);
                    if (gameMap.getWorld() != null) {
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "Successfully loaded the map " + MsgType.INFO.getVariableColor() + gameMap.getName() + MsgType.INFO.getBaseColor() + "."));
                    } else {
                        sender.sendMessage(MCUtils.color(MsgType.ERROR + "Could not load the map " + MsgType.ERROR.getVariableColor() + gameMap.getName() + MsgType.ERROR.getBaseColor() + ". Please report as a bug."));
                    }
                } else if (mapSubCommand.equals("teleport") || mapSubCommand.equals("tp")) {
                    Location spawn;
                    if (gameMap.getWorld() == null) {
                        sender.sendMessage(MsgType.WARN + "That map is not loaded. Please load before teleporting.");
                        return true;
                    }
                    if (gameMap.getCenter() != null) {
                        spawn = gameMap.getCenter().toLocation(gameMap.getWorld());
                    } else {
                        spawn = gameMap.getWorld().getSpawnLocation();
                    }
                    
                    player.teleport(spawn);
                    player.sendMessage(MCUtils.color(MsgType.INFO + "Teleported to the map " + MsgType.INFO.getVariableColor() + gameMap.getName()));
                } else if (mapSubCommand.equals("save")) {
                    plugin.getMapManager().saveToDatabase(gameMap);
                    player.sendMessage(MCUtils.color(MsgType.INFO + "Saved the settings for the map " + MsgType.INFO.getVariableColor() + gameMap.getName()));
                } else if (mapSubCommand.equals("delete")) {
                    gameMap.delete(plugin);
                    player.sendMessage(MCUtils.color(MsgType.INFO + "Deleted the map " + MsgType.INFO.getVariableColor() + gameMap.getName() + MsgType.INFO.getBaseColor() + " from the server."));
                } else if (mapSubCommand.equals("addspawn") || mapSubCommand.equals("as")) {
                    Location location = player.getLocation();
                    int position = gameMap.addSpawn(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You added a spawn with index &b" + position + " &eto the map &b" + gameMap.getName()));
                } else if (mapSubCommand.equals("setspawn") || mapSubCommand.equals("sp")) {
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
                    gameMap.setSpawn(position, new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You set the spawn at position &b" + position + " &eto your location in the map &b" + gameMap.getName()));
                } else if (mapSubCommand.equals("setcenter") || mapSubCommand.equals("sc")) {
                    Location location = player.getPlayer().getLocation();
                    gameMap.setCenter(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                    player.sendMessage(MCUtils.color(MsgType.INFO + "You set the center of the map &b" + gameMap.getName() + " &eto your current location."));
                } else if (mapSubCommand.equals("setborderradius") || mapSubCommand.equals("sbr")) {
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
                    sender.sendMessage(MCUtils.color("You set the border radius on map &b" + gameMap.getName() + " &eto &b" + radius));
                } else if (mapSubCommand.equals("setdeathmatchborderradius") || mapSubCommand.equals("sdmbr")) {
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
                    sender.sendMessage(MCUtils.color("You set the deathmatch border radius on map &b" + gameMap.getName() + " &eto &b" + radius));
                } else if (mapSubCommand.equals("creators")) {
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
                    
                    String[] creators = args[argIndex].split(",");
                    if (creators == null || creators.length == 0) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "You must separate the creators with commas."));
                        return true;
                    }
                    
                    for (String creator : creators) {
                        gameMap.addCreator(creator);
                        sender.sendMessage(MCUtils.color(MsgType.INFO + "You added " + MsgType.INFO.getVariableColor() + creator + MsgType.INFO.getBaseColor() + " as a creator on map " + MsgType.INFO.getVariableColor() + gameMap.getName()));
                    }
                } else if (mapSubCommand.equals("setactive")) {
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
            
            Timer timer;
            String timerType;
            if (game != null) {
                timer = game.getTimer();
                timerType = "game";
            } else {
                timer = plugin.getLobby().getTimer();
                timerType = "lobby";
            }
            
            if (timer == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "The " + timerType + " does not have an active timer. Nothing to control."));
                return true;
            }
            
            String timerSubCommand = args[1].toLowerCase();
            if (timerSubCommand.equals("pause")) {
                if (timer.isPaused()) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The timer is already paused."));
                    return true;
                }
                
                timer.setPaused(true);
                sender.sendMessage(MCUtils.color(MsgType.INFO + "You paused the timer."));
            } else if (timerSubCommand.equals("resume")) {
                if (!timer.isPaused()) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "The timer is not paused."));
                    return true;
                }
                
                timer.setPaused(false);
                sender.sendMessage(MCUtils.color(MsgType.INFO + "You paused the timer."));
            } else if (timerSubCommand.equals("reset")) {
                timer.reset();
                sender.sendMessage(MCUtils.color(MsgType.INFO + "You reset the timer."));
            } else if (timerSubCommand.equals("modify")) {
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
                
                long milliseconds = (seconds * 1000L) + 50;
                if (operator == null) {
                    timer.setLength(milliseconds);
                } else {
                    if (operator == Operator.ADD) {
                        long newTime = timer.getTimeLeft() + milliseconds;
                        if (newTime > timer.getLength()) {
                            newTime = timer.getLength();
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "The new timer length exceeds the specified length of the timer. Using the max length. Please use this command without an operator to change the specified length"));
                        }
                        timer.setRawTime(newTime);
                    } else if (operator == Operator.SUBTRACT) {
                        long newTime = timer.getTimeLeft() - milliseconds;
                        if (newTime <= 0) {
                            sender.sendMessage(MCUtils.color(MsgType.WARN + "The new timer length is less than or equal to 0. Please use the timer cancel command instead."));
                            return true;
                        }
                        
                        timer.setRawTime(newTime);
                    } else if (operator == Operator.MULTIPLY) {
                        long newTime = timer.getTimeLeft() * milliseconds;
                        if (newTime > timer.getLength()) {
                            newTime = timer.getLength();
                            sender.sendMessage(MCUtils.color(MsgType.VERBOSE + "The new timer length exceeds the specified length of the timer. Using the max length. Please use this command without an operator to change the specified length"));
                        }
                        timer.setRawTime(newTime);
                    } else if (operator == Operator.DIVIDE) {
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
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You modified the " + timerType + " timer."));
                }
            }
        }
        
        return true;
    }
}
