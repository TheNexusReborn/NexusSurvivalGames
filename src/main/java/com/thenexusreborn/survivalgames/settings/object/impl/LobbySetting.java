package com.thenexusreborn.survivalgames.settings.object.impl;

import com.thenexusreborn.survivalgames.settings.object.Setting;
import me.firestar311.starlib.api.Value;
import me.firestar311.starsql.api.annotations.table.TableName;

@TableName("sglobbysettings")
public class LobbySetting extends Setting {
    
    private LobbySetting() {
        super();
    }
    
    public LobbySetting(Info info, String category, Value value) {
        super(info, category, value);
    }
}
