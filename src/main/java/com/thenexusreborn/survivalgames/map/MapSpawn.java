package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.survivalgames.util.SGUtils;
import me.firestar311.starsql.api.annotations.table.TableName;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.Objects;

@TableName("sgmapspawns")
public class MapSpawn implements Comparable<MapSpawn> {
    private long id; 
    private long mapId;
    private int index = -1;
    private int x, y, z;
    
    public static MapSpawn fromLocation(int mapId, int index, Location location) {
        return new MapSpawn(mapId, index, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    private MapSpawn() {}
    
    public MapSpawn(long mapId, int index, int x, int y, int z) {
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
    
    public Location toGameLocation(World world, Location mapCenter) {
        return new Location(world, x + 0.5, y + 2, z + 0.5, SGUtils.getAngle(new Vector(x, y, z), mapCenter.toVector()), 0);
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
    
    @Override
    public int compareTo(MapSpawn o) {
        if (o.mapId != this.mapId) {
            return -1;
        }
        return Integer.compare(this.index, o.index);
    }
}
