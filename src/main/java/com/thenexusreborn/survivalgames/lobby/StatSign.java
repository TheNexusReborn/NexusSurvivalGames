package com.thenexusreborn.survivalgames.lobby;

import org.bukkit.Location;

public class StatSign {
    private Location location;
    private String stat, displayName;
    
    public StatSign(Location location, String stat, String displayName) {
        this.location = location;
        this.stat = stat;
        this.displayName = displayName;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public String getStat() {
        return stat;
    }
    
    public void setStat(String stat) {
        this.stat = stat;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "StatSign{" +
                "location=" + location +
                ", stat='" + stat + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
