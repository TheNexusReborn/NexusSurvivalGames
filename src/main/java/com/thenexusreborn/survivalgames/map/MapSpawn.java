package com.thenexusreborn.survivalgames.map;

import com.stardevllc.starmclib.Position;
import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;

import java.util.*;

@TableName("sgmapspawns")
public class MapSpawn extends Position implements Comparable<MapSpawn> {
    
    static {
        ConfigurationSerialization.registerClass(MapSpawn.class);
    }
    
    private long id; 
    private long mapId;
    private int index = -1;
    
    @ColumnIgnored
    private Hologram hologram;
    
    public static MapSpawn fromLocation(int mapId, int index, Location location) {
        return new MapSpawn(mapId, index, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    private MapSpawn() {}
    
    public MapSpawn(long mapId, int index, int x, int y, int z) {
        super(x, y, z);
        this.mapId = mapId;
        this.index = index;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MapSpawn(Map<String, Object> serialized) {
        super(serialized);
        this.id = (long) ((int) serialized.get("id"));
        this.mapId = (long) ((int) serialized.get("mapId"));
        this.index = (int) serialized.get("index");
    }
    
    public Hologram getHologram() {
        return hologram;
    }
    
    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }
    
    public Hologram createHologram(World world) {
        if (this.hologram != null) {
            this.hologram.delete();
            this.hologram = null;
        }
        
        Location location = toBlockLocation(world).add(.5, 1, .5);
        String line = ChatColor.translateAlternateColorCodes('&', "&eSpawn Index: &a" + (this.index + 1));
        
        Hologram existing = DHAPI.getHologram("spawn_" + this.index);
        if (existing != null) {
            existing.delete();
        }
        
        this.hologram = DHAPI.createHologram("spawn_" + this.index, location, List.of(line));
        return this.hologram;
    }
    
    public void updateHologram(World world) {
        if (this.hologram == null) {
            createHologram(world);
            return;
        }
        
        String line = ChatColor.translateAlternateColorCodes('&', "&eSpawn Index: &a" + (this.index + 1));
        DHAPI.setHologramLine(this.hologram, 0, line);
    }
    
    public void deleteHologram() {
        if (this.hologram != null) {
            this.hologram.delete();
            this.hologram = null;
        }
    }
    
    @Override
    public String toString() {
        return "(" + id + "," + index + "," + mapId + ") (" + x + "," + y + "," + z + ')';
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

    public Location toGameLocation(World world, Location mapCenter) {
        return new Location(world, x + 0.5, y + 2, z + 0.5, getAngle(new Vector(x, y, z), mapCenter.toVector()), 0);
    }

    private static float getAngle(Vector point1, Vector point2) {
        double dx = point2.getX() - point1.getX();
        double dz = point2.getZ() - point1.getZ();
        float angle = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        if (angle < 0) {
            angle += 360.0F;
        }
        return angle;
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

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serialize();
        serialized.put("id", this.id);
        serialized.put("mapId", this.mapId);
        serialized.put("index", this.index);
        return serialized;
    }
    
    @Override
    public MapSpawn clone() {
        return new MapSpawn(-1, this.index, (int) this.x, (int) this.y, (int) this.z);
    }
}
