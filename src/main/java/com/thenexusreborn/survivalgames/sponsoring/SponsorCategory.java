package com.thenexusreborn.survivalgames.sponsoring;

import com.stardevllc.itembuilder.XMaterial;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SponsorCategory<T> {
    private String name;
    private XMaterial icon;
    private List<T> entries = new ArrayList<>();
    
    public SponsorCategory(String name, XMaterial icon) {
        this.name = name;
        this.icon = icon;
    }
    
    public String getName() {
        return name;
    }
    
    public XMaterial getIcon() {
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
