package com.thenexusreborn.survivalgames.cmd.sgadmin.settings;

import com.stardevllc.starlib.converter.string.StringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.cmd.sgadmin.settings.type.SettingsGameSubCmd;
import com.thenexusreborn.survivalgames.cmd.sgadmin.settings.type.SettingsLobbySubCmd;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.LobbySettings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class SGASettingsCmd extends SubCommand<SurvivalGames> {
    
    public static final Map<String, Map<Field, StringConverter<Object>>> settingsFields = new HashMap<>();
    
    static {
        settingsFields.put("game", getFieldsFromSettingsClass(GameSettings.class));
        settingsFields.put("lobby", getFieldsFromSettingsClass(LobbySettings.class));
    }
    
    public SGASettingsCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 0, "settings", "", Rank.ADMIN, "s");
        
        this.subCommands.add(new SettingsGameSubCmd(plugin, this));
        this.subCommands.add(new SettingsLobbySubCmd(plugin, this));
    }
    
    private static Map<Field, StringConverter<Object>> getFieldsFromSettingsClass(Class<?> settingsClass) {
        Field[] declaredFields = settingsClass.getDeclaredFields();
        
        Map<Field, StringConverter<Object>> fields = new TreeMap<>(Comparator.comparing(Field::getName));
        for (Field declaredField : declaredFields) {
            if (Modifier.isStatic(declaredField.getModifiers())) {
                continue;
            }
            
            if (Modifier.isFinal(declaredField.getModifiers())) {
                continue;
            }
            
            StringConverter<Object> converter = (StringConverter<Object>) StringConverters.getConverter(declaredField.getType());
            if (converter != null) {
                declaredField.setAccessible(true);
                fields.put(declaredField, converter);
            }
        }
        
        return fields;
    }
}
