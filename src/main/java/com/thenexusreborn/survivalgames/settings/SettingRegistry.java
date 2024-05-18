package com.thenexusreborn.survivalgames.settings;

import com.stardevllc.starlib.misc.Value;
import com.stardevllc.starlib.registry.StringRegistry;
import com.thenexusreborn.survivalgames.settings.object.Setting;
import com.thenexusreborn.survivalgames.settings.object.Setting.Info;

public class SettingRegistry extends StringRegistry<Info> {
    
    public void register(String name, String displayName, String description, String type, Value defaultValue) {
        register(name, new Info(name, displayName, description, type, defaultValue));
    }
    
    public void register(String name, String displayName, String description, String type, Value defaultValue, Value minValue, Value maxValue) {
        register(name, new Info(name, displayName, description, type, defaultValue, minValue, maxValue));
    }
    
    @Override
    public Setting.Info get(String str) {
        for (Info info : this.getObjects().values()) {
            if (info.getName().equalsIgnoreCase(str) || info.getDisplayName().equalsIgnoreCase(str)) {
                return info;
            }
        }
        return null;
    }
}
