package com.thenexusreborn.survivalgames.sponsoring;

import com.stardevllc.smaterial.SMaterial;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SponsorCategory<T> {
    private String name;
    private SMaterial icon;
    private List<T> entries = new ArrayList<>();
    
    public SponsorCategory(String name, SMaterial icon) {
        this.name = name;
        this.icon = icon;
    }
    
    public String getName() {
        return name;
    }
    
    public SMaterial getIcon() {
        return icon;
    }
    
    public SponsorCategory<T> addEntry(T item) {
        this.entries.add(item);
        return this;
    }
    
    @SafeVarargs
    public final void addEntries(T... items) {
        this.entries.addAll(List.of(items));
    }
    
    public List<T> getEntries() {
        return new ArrayList<>(entries);
    }
    
    public abstract void apply(Player player, Object entry);
    public abstract List<String> getListOfEntries();
}
