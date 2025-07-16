package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.util.ReflectionUtils;

public interface ISettings {
    ISettings clone();
    String getName();
    default Object getValue(String path) {
        return ReflectionUtils.getValue(this, path);
    }
}
