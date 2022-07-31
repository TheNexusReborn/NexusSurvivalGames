package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.api.data.annotations.*;
import org.bukkit.*;

import java.util.Objects;

@TableInfo("sgmapspawns")
public class MapSpawn {
    @Primary
    private long id; 
    private long mapId;
    private int index;
    private int x, y, z;
    
    public static MapSpawn fromLocation(int mapId, int index, Location location) {
        return new MapSpawn(mapId, index, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    private MapSpawn() {}
    
    public MapSpawn(int mapId, int index, int x, int y, int z) {
        this.mapId = mapId;
        this.index = index;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setZ(int z) {
        this.z = z;
    }
    
    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ')';
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getMapId() {
        return mapId;
    }
    
    public void setMapId(long mapId) {
        this.mapId = mapId;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapSpawn mapSpawn = (MapSpawn) o;
        return mapId == mapSpawn.mapId && index == mapSpawn.index && x == mapSpawn.x && y == mapSpawn.y && z == mapSpawn.z;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(mapId, index, x, y, z);
    }
}
