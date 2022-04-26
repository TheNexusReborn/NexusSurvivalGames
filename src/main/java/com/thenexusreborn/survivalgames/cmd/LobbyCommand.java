package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.cmd.arg.MapNameArgument;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.util.SGUtils;

import java.util.*;

public class LobbyCommand extends NexusCommand {
    public LobbyCommand(SurvivalGames plugin) {
        super("lobby", "Manage lobby settings", Rank.ADMIN, true, false, new ArrayList<>());
        
        SubCommand mapSubCommand = new SubCommand(this, "map", "Set the map to be used", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                GameMap gameMap = SGUtils.getGameMapFromInput(args[0], actor);
                if (gameMap == null) {
                    return;
                }
                
                plugin.getLobby().setGameMap(gameMap);
                plugin.getLobby().sendMessage("&eThe map has been set to &b" + gameMap.getName());
                if (!plugin.getLobby().hasPlayer(actor.getPlayer().getUniqueId())) {
                    actor.sendMessage("&eYou set the map of the lobby to &b" + gameMap.getName());
                }
            }
        };
        mapSubCommand.addArgument(new MapNameArgument());
        this.addSubCommand(mapSubCommand);
        
        SubCommand modeSubCommand = new SubCommand(this, "mode", "Set the mode of the lobby", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                Mode mode;
                try {
                    mode = Mode.valueOf(args[0].toUpperCase());
                } catch (Exception e) {
                    actor.sendMessage("&cYou provided an invalid value for the mode");
                    return;
                }
                
                if (plugin.getLobby().getMode() == mode) {
                    actor.sendMessage("&cThe lobby is already in that mode.");
                    return;
                }
                
                if (mode == Mode.AUTOMATIC) {
                    plugin.getLobby().automatic();
                    plugin.getLobby().sendMessage("&eThe lobby has been set to automatic mode.");
                } else if (mode == Mode.MANUAL) {
                    plugin.getLobby().manual();
                    plugin.getLobby().sendMessage("&eThe lobby has been set to manual mode.");
                }
            }
        };
        Argument modeArgument = new Argument("mode", true, "You must provide a mode value");
        modeArgument.getCompletions().add(Mode.AUTOMATIC.name().toLowerCase());
        modeArgument.getCompletions().add(Mode.MANUAL.name().toLowerCase());
        modeSubCommand.addArgument(modeArgument);
        this.addSubCommand(modeSubCommand);
        
        SubCommand timerSubCommand = new SubCommand(this, "timer", "Manipulate the timer", this.getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                Timer timer = plugin.getLobby().getTimer();
                
                if (args[0].equalsIgnoreCase("start")) {
                    if (timer != null) {
                        actor.sendMessage("&cThe timer has already been started, this command cannot be used.");
                    } else {
                        plugin.getLobby().startTimer();
                        plugin.getLobby().sendMessage("&eThe lobby timer has been started by &b" + actor.getPlayer().getName());
                    }
                    return;
                }
                
                if (timer == null) {
                    actor.sendMessage("&cThe timer has not been started, please use /lobby timer start to start the timer.");
                    return;
                }
                
                if (args[0].equalsIgnoreCase("pause")) {
                    if (timer.isPaused()) {
                        actor.sendMessage("&cThe timer is already paused.");
                        return;
                    }
                    
                    timer.setPaused(true);
                    plugin.getLobby().sendMessage("&eThe timer has been paused by &b" + actor.getPlayer().getName());
                } else if (args[0].equalsIgnoreCase("resume")) {
                    if (!timer.isPaused()) {
                        actor.sendMessage("&cThe timer is not paused.");
                        return;
                    }
    
                    timer.setPaused(false);
                    plugin.getLobby().sendMessage("&eThe timer has been unpaused by &b" + actor.getPlayer().getName());
                } else if (args[0].equalsIgnoreCase("cancel")) {
                    timer.cancel();
                    plugin.getLobby().setTimer(null);
                    plugin.getLobby().sendMessage("&eThe timer has been cancelled by &b" + actor.getPlayer().getName());
                } 
            }
        };
        
        Argument timerActionArgument = new Argument("action", true, "You must provide a timer action");
        timerActionArgument.getCompletions().addAll(Arrays.asList("pause", "resume", "reset", "start"));
        timerSubCommand.addArgument(timerActionArgument);
        this.addSubCommand(timerSubCommand);
        
        this.addSubCommand(new SubCommand(this, "preparegame", "Prepares the game based on lobby settings", getMinRank(), true, false, Collections.singletonList("pg")) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                plugin.getLobby().prepareGame();
            }
        });
        
        this.addSubCommand(new SubCommand(this, "reset", "Resets the lobby data", getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                plugin.getLobby().resetLobby();
            }
        });
        
        this.addSubCommand(new SubCommand(this, "setspawn", "Sets the spawn", getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                plugin.getLobby().setSpawnpoint(actor.getPlayer().getLocation());
                actor.sendMessage("&eYou set the lobby spawnpoint to your location.");
            }
        });
        
        this.addSubCommand(new SubCommand(this, "editmaps", "Allows editing of maps and prevents automatic things", getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                if (plugin.getLobby().getState() == LobbyState.MAP_EDITING) {
                    plugin.getLobby().stopEditingMaps();
                } else {
                    plugin.getLobby().editMaps();
                }
            }
        });
    }
}
