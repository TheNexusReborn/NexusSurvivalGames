package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.settings.*;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SettingsCommand extends NexusCommand {
    
    private SurvivalGames plugin;
    
    public SettingsCommand(SurvivalGames plugin) {
        super("settings", "Set different setting values", Rank.ADMIN);
        this.plugin = plugin;
    
        Argument type = new Argument("type", true, "You must provide the setting type (Lobby, Game)");
        type.getCompletions().addAll(Arrays.asList("lobby", "game"));
        addArgument(type);
        
        Argument settingName = new Argument("settingName", true, "You must provide the setting name");
        addArgument(settingName);
        
        Argument settingValue = new Argument("settingValue", true, "You must provide the setting value");
        addArgument(settingValue);
    }
    
    @Override
    public void handleCommand(CommandActor actor, String label, String[] args) {
        String type = args[0];
        String settingName = args[1];
        String rawValue = args[2];
    
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
            actor.sendMessage("&cInvalid setting type. Only Lobby and Game are allowed (Case insensitive)");
            return;
        }
    
        if (settingField == null) {
            actor.sendMessage("&cA setting with that name does not exist.");
            return;
        }
    
        Object value;
        if (settingField.getType().equals(int.class)) {
            try {
                value = Integer.parseInt(rawValue);
            } catch (NumberFormatException e) {
                actor.sendMessage("&cYou provided an invalid number for that setting.");
                return;
            }
        } else if (settingField.getType().equals(boolean.class)) {
            if (rawValue.equalsIgnoreCase("true") || rawValue.equalsIgnoreCase("yes")) {
                value = true;
            } else if (rawValue.equalsIgnoreCase("false") || rawValue.equalsIgnoreCase("no")) {
                value = false;
            } else {
                actor.sendMessage("&cInvalid value, only true, false, yes and no are accepted for that setting");
                return;
            }
        } else if (settingField.getType().equals(Time.class)) {
            try {
                value = Time.valueOf(rawValue.toUpperCase());
            } catch (Exception e) {
                actor.sendMessage("&cYou provided an invalid time value");
                return;
            }
        } else if (settingField.getType().equals(Weather.class)) {
            try {
                value = Weather.valueOf(rawValue.toUpperCase());
            } catch (Exception e) {
                actor.sendMessage("&cYou provided an invalid weather value");
                return;
            }
        } else if (settingField.getType().equals(ColorMode.class)) {
            try {
                value = ColorMode.valueOf(rawValue.toUpperCase());
            } catch (Exception e) {
                actor.sendMessage("&cYou provided an invalid color mode value.");
                return;
            }
        } else {
            actor.sendMessage("&cUnhandled setting type: " + settingField.getType().getName() + ". This is a BUG");
            return;
        }
    
        try {
            settingField.setAccessible(true);
            Object object = null;
            if (type.equalsIgnoreCase("game")) {
                if (plugin.getGame() == null) {
                    if (plugin.getLobby().getGameSettings() == null) {
                        plugin.getLobby().setGameSettings(new GameSettings());
                    }
                    object = plugin.getLobby().getGameSettings();
                } else {
                    object = plugin.getGame().getSettings();
                }
            } else if (type.equalsIgnoreCase("lobby")) {
                object = plugin.getLobby().getLobbySettings();
            }
            settingField.set(object, value);
            actor.sendMessage("&eYou set the &b" + type.toLowerCase() + " &esetting &b" + settingField.getName() + " &eto &b" + value);
        } catch (Exception e) {
            actor.sendMessage("&cError while setting the value. Please report with the following message.");
            actor.sendMessage("&c" + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
