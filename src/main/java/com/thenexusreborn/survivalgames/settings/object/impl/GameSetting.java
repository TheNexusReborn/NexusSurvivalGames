package com.thenexusreborn.survivalgames.settings.object.impl;

import com.starmediadev.starsql.annotations.table.TableName;
import com.starmediadev.starlib.util.Value;
import com.thenexusreborn.survivalgames.settings.object.Setting;

@TableName("sggamesettings")
public class GameSetting extends Setting {
    
    private GameSetting() {
        super();
    }
    
    public GameSetting(Info info, String category, Value value) {
        super(info, category, value);
    }
}
