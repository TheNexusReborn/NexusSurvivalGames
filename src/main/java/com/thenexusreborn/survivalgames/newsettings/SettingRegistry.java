package com.thenexusreborn.survivalgames.newsettings;

import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.api.registry.Registry;
import com.thenexusreborn.survivalgames.newsettings.object.Setting;
import com.thenexusreborn.survivalgames.newsettings.object.Setting.Info;

public class SettingRegistry extends Registry<Setting.Info> {
    
    public void register(String name, String displayName, String description, String type, Value defaultValue) {
        register(new Info(name, displayName, description, type, defaultValue));
    }
    
    public void register(String name, String displayName, String description, String type, Value defaultValue, Value minValue, Value maxValue) {
        register(new Info(name, displayName, description, type, defaultValue, minValue, maxValue));
    }
    
    @Override
    public Setting.Info get(String str) {
        for (Info info : this.getObjects()) {
            if (info.getName().equalsIgnoreCase(str) || info.getDisplayName().equalsIgnoreCase(str)) {
                return info;
            }
        }
        return null;
    }
}
