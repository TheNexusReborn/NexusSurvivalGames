package com.thenexusreborn.survivalgames.map;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.stardevllc.starlib.helper.FileHelper;
import com.stardevllc.starmclib.Position;
import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.table.TableHandler;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.gamemaps.model.GameMap;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.*;
import java.util.*;

@SuppressWarnings("unused")
@TableName("sgmaps")
@TableHandler(SGMapObjectHandler.class)
public class SGMap extends GameMap {
    @ColumnIgnored
    private Set<MapSpawn> spawns = new LinkedHashSet<>();
    @ColumnIgnored
    private Map<UUID, MapRating> ratings = new HashMap<>();
    private Position swagShack;
    
    private Set<Position> enderChestLocations = new HashSet<>();

    //This is the length of the sides of the world border for the arena
    private int arenaBorderLength;

    private Position deathmatchMinimum;
    private Position deathmatchMaximum;
    private Position deathmatchCenter;

    //This is the length of the sides of the world border
    private int deathmatchBorderLength;

    //Analytics - Done via command /sg map analyze
    private int chests, enchantTables, workbenches, furnaces, totalBlocks;

    @ColumnIgnored
    private CuboidRegion deathmatchRegion;
    @ColumnIgnored
    private Region borderRegion;
    @ColumnIgnored
    private List<Location> borderChangedBlocks = new ArrayList<>();

    private SGMap() {
    }

    public boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        
        if (this.spawns.size() < 2) {
            return false;
        }
        
        if (this.arenaBorderLength == 0) {
            return false;
        }
        
        return !(this.deathmatchMinimum == null || this.deathmatchMaximum == null || this.deathmatchCenter == null || this.deathmatchBorderLength == 0);
    }

    public SGMap(String fileName, String name) {
        this.url = fileName;
        this.name = name;
    }
    
    public Position getDeathmatchCenter() {
        return deathmatchCenter;
    }

    public void setDeathmatchCenter(Position deathmatchCenter) {
        this.deathmatchCenter = deathmatchCenter;
    }

    public int getArenaBorderLength() {
        return arenaBorderLength;
    }

    public void setArenaBorderLength(int arenaBorderLength) {
        this.arenaBorderLength = arenaBorderLength;
    }

    public int getDeathmatchBorderLength() {
        return deathmatchBorderLength;
    }

    public void setDeathmatchBorderLength(int deathmatchBorderLength) {
        this.deathmatchBorderLength = deathmatchBorderLength;
    }
    
    public Position getDeathmatchMinimum() {
        return deathmatchMinimum;
    }

    public void setDeathmatchMinimum(Position deathmatchMinimum) {
        this.deathmatchMinimum = deathmatchMinimum;
    }

    public Position getDeathmatchMaximum() {
        return deathmatchMaximum;
    }

    public void setDeathmatchMaximum(Position deathmathMaximum) {
        this.deathmatchMaximum = deathmathMaximum;
    }

    public void recalculateSpawns() {
        if (spawns.isEmpty()) {
            return;
        }

        List<MapSpawn> spawns = new LinkedList<>(this.spawns);
        Collections.sort(spawns);

        for (int i = 0; i < spawns.size(); i++) {
            MapSpawn spawn = spawns.get(i);
            if (spawn != null) {
                spawn.setIndex(i);
            }
            
            if (world != null && editing) {
                spawn.updateHologram(world);
            }
        }
    }

    public int getNextIndex() {
        if (this.spawns.isEmpty()) {
            return 0;
        }

        int lastIndex = 0;
        for (MapSpawn spawn : this.spawns) {
            if (spawn.getIndex() > lastIndex) {
                lastIndex = spawn.getIndex();
            }
        }

        return lastIndex + 1;
    }

    public Set<Position> getEnderChestLocations() {
        return enderChestLocations;
    }
    
    public void addEnderChestLocation(Position position) {
        this.enderChestLocations.add(position);
    }
    
    public void addEnderChestLocation(Location location) {
        addEnderChestLocation(Position.fromLocation(location));
    }

    public void removeFromServer(JavaPlugin plugin) {
        try {
            for (MapSpawn spawn : this.spawns) {
                spawn.deleteHologram();
            }
            
            super.removeFromServer(plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean download(JavaPlugin plugin) {
        if (downloadedZip == null || !Files.exists(downloadedZip)) {
            Path downloadFolder = FileHelper.subPath(plugin.getDataFolder().toPath(), "mapdownloads");
            FileHelper.createDirectoryIfNotExists(downloadFolder);

            String fileName = getName().toLowerCase().replace("'", "").replace(" ", "_") + ".zip";

            Path existing = FileSystems.getDefault().getPath(downloadFolder.toString(), fileName);
            if (Files.exists(existing)) {
                plugin.getLogger().info("Found existing zip for " + name);
                this.downloadedZip = existing;
                return true;
            }

            downloadedZip = FileHelper.downloadFile(url, downloadFolder, fileName, true);
        }
        return downloadedZip != null && Files.exists(downloadedZip);
    }
    
    public void setSpawns(Collection<MapSpawn> spawns) {
        this.spawns.clear();
        this.spawns.addAll(spawns);
        this.spawns.forEach(spawn -> spawn.setMapId(this.id));
    }

    public int addSpawn(MapSpawn spawn) {
        if (spawn.getIndex() == -1) {
            int index = getNextIndex();
            spawn.setIndex(index);
        }
        this.spawns.add(spawn);
        spawn.setMapId(this.getId());
        return spawn.getIndex();
    }

    public void setSpawn(int index, MapSpawn spawn) {
        spawn.setIndex(index);
        spawn.setMapId(this.getId());
        this.spawns.removeIf(s -> s.getIndex() == index);
        this.spawns.add(spawn);
    }

    public void removeSpawn(int index) {
        this.spawns.removeIf(spawn -> spawn.getIndex() == index);
        recalculateSpawns();
    }
    
    public List<MapSpawn> getSpawns() {
        return new LinkedList<>(spawns);
    }

    public void clearSpawns() {
        this.spawns.clear();
    }
    
    public void setId(long id) {
        super.setId(id);
        this.spawns.forEach(spawn -> spawn.setMapId(id));
    }
    
    public CuboidRegion getDeathmatchRegion() {
        if (this.deathmatchRegion != null) {
            return this.deathmatchRegion;
        }

        if (world == null) {
            return null;
        }

        Vector min = new Vector(this.deathmatchMinimum.getX(), this.deathmatchMinimum.getY(), this.deathmatchMinimum.getZ());
        Vector max = new Vector(this.deathmatchMaximum.getX(), this.deathmatchMaximum.getY(), this.deathmatchMaximum.getZ());

        com.sk89q.worldedit.world.World bukkitWorld = new BukkitWorld(this.world);

        this.deathmatchRegion = new CuboidRegion(bukkitWorld, min, max);
        return deathmatchRegion;
    }

    @Override
    public String toString() {
        return "SGMap{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", spawns=" + spawns +
                ", creators=" + creators +
                ", active=" + active +
                ", ratings=" + ratings +
                ", swagShack=" + swagShack +
                ", uniqueId=" + uniqueId +
                ", world=" + world +
                ", downloadedZip=" + downloadedZip +
                ", unzippedFolder=" + unzippedFolder +
                ", worldFolder=" + worldFolder +
                ", editing=" + editing +
                ", votes=" + votes +
                ", deathmatchArea=" + deathmatchRegion +
                '}';
    }

    public void setRatings(List<MapRating> ratings) {
        ratings.forEach(rating -> this.ratings.put(rating.getPlayer(), rating));
    }

    public Map<UUID, MapRating> getRatings() {
        return ratings;
    }

    public Position getSwagShack() {
        return swagShack;
    }

    public void setSwagShack(Position swagShack) {
        this.swagShack = swagShack;
    }

    public void disableWorldBorder() {
//        if (this.borderRegion != null) {
//            for (BlockVector vector : this.borderRegion) {
//                Block block = getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
//                if (this.borderChangedBlocks.contains(block.getLocation())) {
//                    block.setType(Material.AIR);
//                }
//            }
//        }
        World world = getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.reset();
    }

    public int getChests() {
        return chests;
    }

    public void setChests(int chests) {
        this.chests = chests;
    }

    public int getEnchantTables() {
        return enchantTables;
    }

    public void setEnchantTables(int enchantTables) {
        this.enchantTables = enchantTables;
    }

    public int getWorkbenches() {
        return workbenches;
    }

    public void setWorkbenches(int workbenches) {
        this.workbenches = workbenches;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public void setTotalBlocks(int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    public int getFurnaces() {
        return furnaces;
    }

    public void setFurnaces(int furnaces) {
        this.furnaces = furnaces;
    }
    
    public void applyWorldBoarder(String viewOption, int seconds) {
        disableWorldBorder();
        
        World world = getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        if (viewOption.equalsIgnoreCase("deathmatch")) {
//            borderRegion = getDeathmatchRegion().getFaces();
//            for (BlockVector vector : borderRegion) {
//                Block block = getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
//                if (block.getType() != Material.AIR) {
//                    continue;
//                }
//
//                block.setType(Material.STAINED_GLASS_PANE);
//                block.setData((byte) 14);
//                borderChangedBlocks.add(block.getLocation());
//            }
            worldBorder.setCenter(this.deathmatchCenter.toLocation(world));
            worldBorder.setSize(this.deathmatchBorderLength);
            if (seconds != 0) {
                worldBorder.setSize(10, seconds);
            }
        } else if (viewOption.equalsIgnoreCase("game")) {
            worldBorder.setCenter(this.arenaCenter.toLocation(world));
            worldBorder.setSize(this.arenaBorderLength);
//            borderRegion = getArenaRegion().getFaces();
//            for (BlockVector vector : borderRegion) {
//                Block block = getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
//                if (block.getType() != Material.AIR) {
//                    continue;
//                }
//
//                block.setType(Material.STAINED_GLASS_PANE);
//                block.setData((byte) 4);
//                borderChangedBlocks.add(block.getLocation());
//            }
        }
    }

    public void applyWorldBoarder(String viewOption) {
        applyWorldBoarder(viewOption, 0);
    }

    public static SGMap loadFromYaml(FileConfiguration config) {
        SGMap sgMap = new SGMap();
        sgMap.setId(config.getLong("id"));
        sgMap.setUrl(config.getString("url"));
        sgMap.setName(config.getString("name"));
        sgMap.setPrefix(config.getString("prefix"));

        ConfigurationSection spawnsSection = config.getConfigurationSection("spawns");
        if (spawnsSection != null) {
            for (String key : spawnsSection.getKeys(false)) {
                MapSpawn mapSpawn = (MapSpawn) spawnsSection.get(key);
                sgMap.setSpawn(mapSpawn.getIndex(), mapSpawn);
            }
        }
        
        sgMap.recalculateSpawns();
        
        sgMap.setCreators(config.getStringList("creators"));

        ConfigurationSection ratingsSection = config.getConfigurationSection("ratings");
        if (ratingsSection != null) {
            for (String key : ratingsSection.getKeys(false)) {
                sgMap.addRating((MapRating) ratingsSection.get(key));
            }
        }
        
        ConfigurationSection echestsSection = config.getConfigurationSection("enderchests");
        if (echestsSection != null) {
            for (String key : echestsSection.getKeys(false)) {
                sgMap.addEnderChestLocation((Position) echestsSection.get(key));
            }
        }
        
        sgMap.setSwagShack((Position) config.get("swagshack"));
        sgMap.setSpawnCenter((Position) config.get("spawncenter"));
        
        sgMap.setArenaMinimum((Position) config.get("arena.min"));
        sgMap.setArenaMaximum((Position) config.get("arena.max"));
        sgMap.setArenaCenter((Position) config.get("arena.center"));
        sgMap.setArenaBorderLength(config.getInt("arena.borderlength"));
        
        sgMap.setDeathmatchMinimum((Position) config.get("deathmatch.min"));
        sgMap.setDeathmatchMaximum((Position) config.get("deathmatch.max"));
        sgMap.setDeathmatchCenter((Position) config.get("deathmatch.center"));
        sgMap.setDeathmatchBorderLength(config.getInt("deathmatch.borderlength"));
        
        sgMap.setChests(config.getInt("stats.chests"));
        sgMap.setEnchantTables(config.getInt("stats.enchanttables"));
        sgMap.setWorkbenches(config.getInt("stats.workbenches"));
        sgMap.setFurnaces(config.getInt("stats.furnaces"));
        sgMap.setTotalBlocks(config.getInt("stats.totalblocks"));
        sgMap.setActive(config.getBoolean("active"));

        return sgMap;
    }
    
    public void addRating(MapRating rating) {
        this.ratings.put(rating.getPlayer(), rating);
    }
    
    public MapSpawn getSpawn(int position) {
        for (MapSpawn spawn : this.spawns) {
            if (spawn.getIndex() == position) {
                return spawn;
            }
        }
        
        return null;
    }

    public void saveToYaml(FileConfiguration config) {
        config.set("id", this.id);
        config.set("url", this.url);
        config.set("name", this.name);
        config.set("prefix", this.prefix);
        config.set("active", this.active);
        
        recalculateSpawns();
        
        config.set("spawns", null);
        
        for (MapSpawn spawn : this.spawns) {
            config.set("spawns." + spawn.getIndex(), spawn);
        }
        
        config.set("creators", new ArrayList<>(this.creators));

        for (MapRating rating : this.ratings.values()) {
            config.set("ratings." + rating.getPlayer().toString(), rating);
        }

        int echestIndex = 0;
        for (Position echestLoc : this.enderChestLocations) {
            config.set("enderchests." + echestIndex, echestLoc);
            echestIndex++;
        }
        
        config.set("swagshack", this.swagShack);
        config.set("spawncenter", this.spawnCenter);
        
        config.set("arena.min", this.arenaMinimum);
        config.set("arena.max", this.arenaMaximum);
        config.set("arena.center", this.arenaCenter);
        config.set("arena.borderlength", this.arenaBorderLength);
        
        config.set("deathmatch.min", this.deathmatchMinimum);
        config.set("deathmatch.max", this.deathmatchMaximum);
        config.set("deathmatch.center", this.deathmatchCenter);
        config.set("deathmatch.borderlength", this.deathmatchBorderLength);
        
        config.set("stats.chests", this.chests);
        config.set("stats.enchanttables", this.enchantTables);
        config.set("stats.workbenches", this.workbenches);
        config.set("stats.furnaces", this.furnaces);
        config.set("stats.totalblocks", this.totalBlocks);
    }
    
    public void copyFrom(GameMap other) {
        super.copyFrom(other);

        
        if (other instanceof SGMap otherMap) {
            this.spawns.clear();
            
            for (MapSpawn spawn : otherMap.spawns) {
                addSpawn(spawn.clone());
            }
            
            for (Position enderChestLocation : otherMap.enderChestLocations) {
                this.addEnderChestLocation(enderChestLocation.clone());
            }
            
            this.swagShack = otherMap.swagShack == null ? null : otherMap.swagShack.clone();
            this.arenaBorderLength = otherMap.arenaBorderLength;
            this.deathmatchMinimum = otherMap.deathmatchMinimum == null ? null : otherMap.deathmatchMinimum.clone();
            this.deathmatchMaximum = otherMap.deathmatchMaximum == null ? null : otherMap.deathmatchMaximum.clone();
            this.deathmatchCenter = otherMap.deathmatchCenter == null ? null : otherMap.deathmatchCenter.clone();
            this.deathmatchBorderLength = otherMap.deathmatchBorderLength;
            
            this.chests = otherMap.chests;
            this.enchantTables = otherMap.enchantTables;
            this.workbenches = otherMap.workbenches;
            this.furnaces = otherMap.furnaces;
            this.totalBlocks = otherMap.totalBlocks;
        }
    }

    public void setEnderChests(Set<Position> echestLocs) {
        this.enderChestLocations.clear();
        this.enderChestLocations.addAll(echestLocs);
    }
}