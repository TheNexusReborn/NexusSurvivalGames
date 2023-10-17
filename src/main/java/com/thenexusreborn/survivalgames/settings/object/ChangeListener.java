package com.thenexusreborn.survivalgames.settings.object;

import me.firestar311.starlib.api.Value;

@FunctionalInterface
public interface ChangeListener {
    void onChange(Setting setting, Value.Type type, Object oldValue, Object newValue);
}