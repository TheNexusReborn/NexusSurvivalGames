package com.thenexusreborn.survivalgames.newsettings.object.impl;

import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.api.storage.annotations.TableInfo;
import com.thenexusreborn.survivalgames.newsettings.object.Setting;

@TableInfo("sglobbysettings")
public class LobbySetting extends Setting {
    public LobbySetting(Info info, String category, Value value) {
        super(info, category, value);
    }
}
