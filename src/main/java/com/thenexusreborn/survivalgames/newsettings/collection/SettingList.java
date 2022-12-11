package com.thenexusreborn.survivalgames.newsettings.collection;

import com.thenexusreborn.api.frameworks.value.Value;
import com.thenexusreborn.survivalgames.newsettings.object.Setting;

import java.util.*;

public class SettingList<T extends Setting> implements Cloneable {
    private String category;
    private Map<String, T> settingsMap = new HashMap<>();
    
    public SettingList(String category) {
        this.category = category;
    }
    
    public void add(T object) {
        settingsMap.put(object.getInfo().getName(), object);
    }
    
    public T get(String name) {
        return settingsMap.get(name);
    }
    
    public void setValue(String name, Object value) {
        get(name).getValue().set(value);
    }
    
    public Value getValue(String name) {
        return get(name).getValue();
    }
    
    @Override
    public SettingList<T> clone() {
        SettingList<T> clone;
        try {
            clone = (SettingList<T>) super.clone();
            clone.settingsMap = new HashMap<>();
            this.settingsMap.forEach((name, setting) -> clone.settingsMap.put(name, (T) setting.clone()));
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
