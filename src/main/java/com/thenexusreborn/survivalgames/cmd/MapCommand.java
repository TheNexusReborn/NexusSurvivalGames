package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.cmd.arg.MapNameArgument;
import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.Location;

import java.util.*;

import static com.thenexusreborn.survivalgames.util.SGUtils.*;

public class MapCommand extends NexusCommand {
    
    private SurvivalGames plugin;
    
    public MapCommand(SurvivalGames plugin) {
        super("map", "Manage the game maps", Rank.ADMIN);
        this.plugin = plugin;
    
        SubCommand createSubCommand = new SubCommand(this, "create", "Create a map", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                String fileName = args[0];
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                String mapName = sb.toString().trim();
                if (plugin.getMapManager().getMap(mapName) != null) {
                    actor.sendMessage(MCUtils.color("A map with that name already exists"));
                    return;
                }
    
                GameMap gameMap = new GameMap(fileName, mapName);
                plugin.getMapManager().addMap(gameMap);
                actor.sendMessage("&eYou added a map for the file &b" + fileName + " &ewith the name &b" + mapName);
            }
        };
        createSubCommand.addArgument(new Argument("mapFile", true, "You must provide a File Name"));
        createSubCommand.addArgument(new Argument("mapName", true, "You must provide a Map Name"));
        addSubCommand(createSubCommand);
        
        SubCommand downloadSubCommand = new SubCommand(this, "download", "Download the map", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getGameMapFromInput(args[0], actor);
                if (gameMap == null) {
                    return;
                }
                
                gameMap.download(plugin);
                actor.sendMessage("&eDownloaded the map &b" + gameMap.getName());
            }
        };
        Argument mapNameArgument = new MapNameArgument();
        downloadSubCommand.addArgument(mapNameArgument);
        addSubCommand(downloadSubCommand);
        
        SubCommand loadSubCommand = new SubCommand(this, "load", "Loads a Map as a Bukkit World", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getGameMapFromInput(args[0], actor);
                if (gameMap == null) {
                    return;
                }
                
                gameMap.unzip(plugin);
                gameMap.copyFolder(plugin, false);
                gameMap.load(plugin);
                if (gameMap.getWorld() != null) {
                    actor.sendMessage("&eSuccessfully loaded the map &b" + gameMap.getName());
                } else {
                    actor.sendMessage("&cCould not load the map, check console for errors");
                }
            }
        };
        
        loadSubCommand.addArgument(mapNameArgument);
        addSubCommand(loadSubCommand);
        
        SubCommand teleportSubCommand = new SubCommand(this, "teleport", "Teleport to a map", this.getMinRank(), true, false, Collections.singletonList("tp")) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getLoadedGameMap(args[0], actor);
                if (gameMap == null) {
                    return;
                }
                
                actor.getPlayer().teleport(gameMap.getWorld().getSpawnLocation());
                actor.sendMessage("&eTeleported to the map &b" + gameMap.getName());
            }
        };
        
        teleportSubCommand.addArgument(mapNameArgument);
        addSubCommand(teleportSubCommand);
        
        SubCommand addSpawnSubCommand = new SubCommand(this, "addspawn", "Add a spawn", this.getMinRank(), true, false, new ArrayList<>()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getLoadedGameMap(args[0], actor);
                if (gameMap == null) {
                    return;
                }
    
                Location location = actor.getPlayer().getLocation();
                int position = gameMap.addSpawn(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                actor.sendMessage("&eYou added a spawn with index &b" + position + " &eto the map &b" + gameMap.getName());
            }
        };
        addSpawnSubCommand.addArgument(mapNameArgument);
        addSubCommand(addSpawnSubCommand);
    
        SubCommand setSpawnSubCommand = new SubCommand(this, "setspawn", "Sets a spawn", this.getMinRank(), true, false, new ArrayList<>()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getLoadedGameMap(args[0], actor);
                if (gameMap == null) {
                    return;
                }
                
                int index;
                try {
                    index = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    actor.sendMessage("&cYou provided an invalid number");
                    return;
                }
    
                Location location = actor.getPlayer().getLocation();
                gameMap.setSpawn(index, new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                actor.sendMessage("&eYou set the spawn at index &b" + index + " &eto your location in the map &b" + gameMap.getName());
            }
        };
        setSpawnSubCommand.addArgument(mapNameArgument);
        setSpawnSubCommand.addArgument(new Argument("index", true, "You must provide a spawn index"));
        addSubCommand(setSpawnSubCommand);
    
        SubCommand setcenterSubCommand = new SubCommand(this, "setcenter", "Set the center", this.getMinRank(), true, false, new ArrayList<>()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getLoadedGameMap(args[0], actor);
                if (gameMap == null) {
                    return;
                }
            
                Location location = actor.getPlayer().getLocation();
                gameMap.setCenter(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                actor.sendMessage("&eYou set the center of the map &b" + gameMap.getName() + " &eto your current location.");
            }
        };
        setcenterSubCommand.addArgument(mapNameArgument);
        addSubCommand(setcenterSubCommand);
        
        SubCommand setBorderRadiusSubCommand = new SubCommand(this, "setborderradius", "Sets the border radius", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getGameMapFromInput(args[0], actor);
                if (gameMap == null) {
                    return;
                }
                int radius;
                try {
                    radius = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    actor.sendMessage("&cYou provided an invalid number");
                    return;
                }
                
                gameMap.setBorderDistance(radius);
                actor.sendMessage("&eYou set the border radius on map &b" + gameMap.getName() + " &eto &b" + radius);
            }
        };
        setBorderRadiusSubCommand.addArgument(mapNameArgument);
        setBorderRadiusSubCommand.addArgument(new Argument("radius", true, "You must provide a radius"));
        addSubCommand(setBorderRadiusSubCommand);
    
        SubCommand setDMBorderRadiusSubCommand = new SubCommand(this, "setdmborderradius", "Sets the deathmatch border radius", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getGameMapFromInput(args[0], actor);
                if (gameMap == null) {
                    return;
                }
                int radius;
                try {
                    radius = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    actor.sendMessage("&cYou provided an invalid number");
                    return;
                }
            
                gameMap.setDeathmatchBorderDistance(radius);
                actor.sendMessage("&eYou set the deathmatch border radius on map &b" + gameMap.getName() + " &eto &b" + radius);
            }
        };
        setDMBorderRadiusSubCommand.addArgument(mapNameArgument);
        setDMBorderRadiusSubCommand.addArgument(new Argument("radius", true, "You must provide a radius"));
        addSubCommand(setDMBorderRadiusSubCommand);
        
        SubCommand saveSubCommand = new SubCommand(this, "save", "Saves a map", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getGameMapFromInput(args[0], actor);
                if (gameMap == null) {
                    return;
                }
                
                gameMap.saveSettings();
                actor.sendMessage("&eSaved the settings for map &b" + gameMap.getName());
            }
        };
        saveSubCommand.addArgument(mapNameArgument);
        this.addSubCommand(saveSubCommand);
    
        SubCommand deleteSubcommand = new SubCommand(this, "delete", "Deletes map information on the server", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = getGameMapFromInput(args[0], actor);
                if (gameMap == null) {
                    return;
                }
            
                gameMap.delete(plugin);
                actor.sendMessage("&eDeleted the map &b" + gameMap.getName() + " &efrom the server.");
            }
        };
        deleteSubcommand.addArgument(mapNameArgument);
        this.addSubCommand(deleteSubcommand);
    }
}
