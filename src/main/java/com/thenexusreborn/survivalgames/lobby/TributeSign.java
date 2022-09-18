package com.thenexusreborn.survivalgames.lobby;

import com.thenexusreborn.survivalgames.lobby.tasks.TributeSignUpdateTask;
import org.bukkit.Location;

import java.util.UUID;

public class TributeSign implements Comparable<TributeSign> {
    private int index;
    private Location signLocation, headLocation;
    private UUID player;
    
    public TributeSign(int index, Location signLocation, Location headLocation) {
        this.index = index;
        this.signLocation = signLocation;
        this.headLocation = headLocation;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public Location getSignLocation() {
        return signLocation;
    }
    
    public void setSignLocation(Location signLocation) {
        this.signLocation = signLocation;
    }
    
    public Location getHeadLocation() {
        return headLocation;
    }
    
    public void setHeadLocation(Location headLocation) {
        this.headLocation = headLocation;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public void setPlayer(UUID player) {
        this.player = player;
    }
    
    @Override
    public int compareTo(TributeSign o) {
        return Integer.compare(this.index, o.index);
    }
}
