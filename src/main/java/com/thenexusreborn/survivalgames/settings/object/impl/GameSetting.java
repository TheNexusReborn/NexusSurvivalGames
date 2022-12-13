package com.thenexusreborn.survivalgames.settings.object.impl;

import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.api.storage.annotations.TableInfo;
import com.thenexusreborn.survivalgames.settings.object.Setting;

@TableInfo("sggamesettings")
public class GameSetting extends Setting {
    
    private GameSetting() {
        super();
    }
    
    public GameSetting(Info info, String category, Value value) {
        super(info, category, value);
    }
}
