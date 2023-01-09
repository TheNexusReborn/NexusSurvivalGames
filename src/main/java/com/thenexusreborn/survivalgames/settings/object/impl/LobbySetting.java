package com.thenexusreborn.survivalgames.settings.object.impl;

import com.starmediadev.starsql.annotations.table.TableName;
import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.survivalgames.settings.object.Setting;

@TableName("sglobbysettings")
public class LobbySetting extends Setting {
    
    private LobbySetting() {
        super();
    }
    
    public LobbySetting(Info info, String category, Value value) {
        super(info, category, value);
    }
}
