package com.thenexusreborn.survivalgames.settings.object;

import com.stardevllc.starlib.misc.Value;

@FunctionalInterface
public interface ChangeListener {
    void onChange(Setting setting, Value.Type type, Object oldValue, Object newValue);
}