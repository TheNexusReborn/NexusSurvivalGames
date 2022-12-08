package com.thenexusreborn.survivalgames.newsettings.collection;

import com.thenexusreborn.survivalgames.newsettings.object.Setting;

import java.util.*;

public class SettingList<T extends Setting> {
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
}
