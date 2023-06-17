package com.thenexusreborn.survivalgames.settings.collection;

import com.starmediadev.starlib.util.Value;
import com.thenexusreborn.survivalgames.settings.object.Setting;

import java.util.*;

public abstract class SettingList<T extends Setting> implements Cloneable {
    private String category;
    private Map<String, T> settingsMap = new HashMap<>();
    
    public SettingList(String category) {
        this.category = category;
    }
    
    public abstract T createSetting(String name);
    
    public void add(T object) {
        settingsMap.put(object.getInfo().getName().toLowerCase(), object);
    }
    
    public T get(String name) {
        if (!settingsMap.containsKey(name)) {
            return createSetting(name);
        }
        
        return settingsMap.get(name.toLowerCase());
    }
    
    public void setValue(String name, Object value) {
        T setting = get(name);
        if (!settingsMap.containsKey(name)) {
            add(setting);
        }
        setting.getValue().set(value);
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
    
    public boolean contains(String name) {
        return this.settingsMap.containsKey(name);
    }
    
    public String getCategory() {
        return category;
    }
    
    public List<T> findAll() {
        return new ArrayList<>(this.settingsMap.values());
    }
}
