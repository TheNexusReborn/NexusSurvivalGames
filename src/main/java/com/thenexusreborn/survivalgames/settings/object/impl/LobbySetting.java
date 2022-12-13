package com.thenexusreborn.survivalgames.settings.object.impl;

import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.api.storage.annotations.TableInfo;
import com.thenexusreborn.survivalgames.settings.object.Setting;

@TableInfo("sglobbysettings")
public class LobbySetting extends Setting {
    
    private LobbySetting() {
        super();
    }
    
    public LobbySetting(Info info, String category, Value value) {
        super(info, category, value);
    }
}