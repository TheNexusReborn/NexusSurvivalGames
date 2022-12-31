package com.thenexusreborn.survivalgames.map;

import com.starmediadev.starsql.annotations.Primary;
import com.starmediadev.starsql.annotations.column.*;
import com.starmediadev.starsql.annotations.table.TableInfo;
import com.thenexusreborn.api.storage.codec.StringSetCodec;
import com.thenexusreborn.api.helper.FileHelper;
import com.thenexusreborn.nexuscore.data.codec.PositionCodec;
import com.thenexusreborn.nexuscore.util.Position;
import com.thenexusreborn.nexuscore.util.region.Cuboid;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.data.handler.GameMapObjectHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

@TableInfo(value = "sgmaps", handler = GameMapObjectHandler.class)
public class GameMap {
    @Primary
    private long id;
    private String url;
    private String name;
    
    @ColumnInfo(type = "varchar(100)", codec = PositionCodec.class) 
    private Position center = new Position(0, 0, 0);
    @ColumnIgnored
    private List<MapSpawn> spawns = new LinkedList<>();
    private int borderDistance, deathmatchBorderDistance;
    @ColumnInfo(type = "varchar(1000)", codec = StringSetCodec.class)
    private Set<String> creators = new HashSet<>();
    private boolean active;
    @ColumnIgnored
    private Map<UUID, MapRating> ratings = new HashMap<>();
    
    @ColumnIgnored
    private UUID uniqueId;
    @ColumnIgnored
    private World world;
    
    @ColumnIgnored
    private Path downloadedZip, unzippedFolder, worldFolder;
    @ColumnIgnored
    private boolean editing;
    @ColumnIgnored
    private int votes;
    @ColumnIgnored
    private Cuboid deathmatchArea;
    
    private GameMap() {}
    
    public GameMap(String fileName, String name) {
        this.url = fileName;
        this.name = name;
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
    
    public void delete(SurvivalGames plugin) {
        try {
            uniqueId = null;
            if (downloadedZip != null) {
                Files.deleteIfExists(downloadedZip);
                downloadedZip = null;
            }
            
            if (this.world != null) {
                for (Player player : world.getPlayers()) {
                    player.teleport(plugin.getLobby().getSpawnpoint());
                }
                
                boolean success = Bukkit.unloadWorld(world, false);
                if (!success) {
                    plugin.getLogger().severe("Failed to unload world for map " + this.name);
                }
                world = null;
            }
    
            if (Files.exists(worldFolder)) {
                FileHelper.deleteDirectory(worldFolder);
                worldFolder = null;
            }
            
            if (Files.exists(unzippedFolder)) {
                FileHelper.deleteDirectory(unzippedFolder);
                unzippedFolder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean download(SurvivalGames plugin) {
        Path downloadFolder = FileHelper.subPath(plugin.getDataFolder().toPath(), "mapdownloads");
        FileHelper.createDirectoryIfNotExists(downloadFolder);
        downloadedZip = FileHelper.downloadFile(url, downloadFolder, getName().toLowerCase().replace("'", "").replace(" ", "_"), true);
        return true;
    }
    
    public void addCreator(String creator) {
        this.creators.add(creator);
    }
    
    public void addCreators(String... creators) {
        this.creators.addAll(Arrays.asList(creators));
    }
    
    public void removeCreator(String creator) {
        this.creators.remove(creator);
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
        } else {
            this.spawns.add(spawn);
        }
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
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getName() {
        if (this.name.contains("''")) {
            this.name = name.replace("''", "'");
        }
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Position getCenter() {
        return center;
    }
    
    public void setCenter(Position center) {
        this.center = center;
    }
    
    public List<MapSpawn> getSpawns() {
        return spawns;
    }
    
    public int getBorderDistance() {
        return borderDistance;
    }
    
    public void setBorderDistance(int borderDistance) {
        this.borderDistance = borderDistance;
    }
    
    public int getDeathmatchBorderDistance() {
        return deathmatchBorderDistance == 0 ? 30 : deathmatchBorderDistance;
    }
    
    public void setDeathmatchBorderDistance(int deathmatchBorderDistance) {
        this.deathmatchBorderDistance = deathmatchBorderDistance;
    }
    
    public Set<String> getCreators() {
        return creators;
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public World getWorld() {
        return world;
    }
    
    public void setWorld(World world) {
        this.world = world;
    }
    
    public Path getDownloadedZip() {
        return downloadedZip;
    }
    
    public void setDownloadedZip(Path downloadedZip) {
        this.downloadedZip = downloadedZip;
    }
    
    public boolean unzip(SurvivalGames plugin) {
        Path unzippedMapsFolder = FileHelper.subPath(plugin.getDataFolder().toPath(), "unzippedmaps");
        unzippedFolder = FileHelper.subPath(unzippedMapsFolder, this.name);
        FileHelper.createDirectoryIfNotExists(unzippedFolder);
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zis = new ZipInputStream(Files.newInputStream(this.downloadedZip.toFile().toPath()));
            ZipEntry zipEntry = zis.getNextEntry();
            
            while (zipEntry != null) {
                Path newFile = newFile(unzippedFolder.toFile(), zipEntry);
                if (zipEntry.isDirectory()) {
                    FileHelper.createDirectoryIfNotExists(newFile);
                } else {
                    // fix for Windows-created archives
                    Path parent = newFile.getParent();
                    if (!Files.isDirectory(parent)) {
                        FileHelper.createDirectoryIfNotExists(parent);
                        if (Files.notExists(parent)) {
                            throw new IOException("Failed to create directory " + parent);
                        }
                    }
        
                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile.toFile());
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            unzippedFolder = null;
            return false;
        }
        return true;
    }
    
    public Path getUnzippedFolder() {
        return unzippedFolder;
    }
    
    private Path newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        Path path = FileHelper.subPath(destinationDir.toPath(), zipEntry.getName());
    
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = path.toFile().getCanonicalPath();
    
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
    
        return path;
    }
    
    public boolean copyFolder(SurvivalGames plugin, boolean randomizeName) {
        try {
            if (this.unzippedFolder != null) {
                String worldName;
                if (randomizeName) {
                    uniqueId = UUID.randomUUID();
                    worldName = uniqueId.toString();
                }  else {
                    worldName = this.name;
                }
                this.worldFolder = FileHelper.subPath(Bukkit.getServer().getWorldContainer().toPath(), worldName);
                FileHelper.createDirectoryIfNotExists(worldFolder);
                FileHelper.copyFolder(this.unzippedFolder, worldFolder);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public boolean load(SurvivalGames plugin) {
        try {
            if (this.worldFolder != null) {
                this.world = Bukkit.createWorld(new WorldCreator(this.name));
                return this.world != null;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    
    public void setEditing(boolean editing) {
        this.editing = editing;
    }
    
    public boolean isEditing() {
        return editing;
    }
    
    public int getVotes() {
        return votes;
    }
    
    public void setVotes(int votes) {
        this.votes = votes;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
        this.spawns.forEach(spawn -> spawn.setMapId(id));
    }
    
    public void setDeathmatchArea(Cuboid deathmatchArea) {
        this.deathmatchArea = deathmatchArea;
    }
    
    public Cuboid getDeathmatchArea() {
        return deathmatchArea;
    }
    
    @Override
    public String toString() {
        return "GameMap{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", center=" + center +
                ", spawns=" + spawns +
                ", borderDistance=" + borderDistance +
                ", deathmatchBorderDistance=" + deathmatchBorderDistance +
                ", creators=" + creators +
                ", active=" + active +
                ", uniqueId=" + uniqueId +
                ", world=" + world +
                ", downloadedZip=" + downloadedZip +
                ", unzippedFolder=" + unzippedFolder +
                ", worldFolder=" + worldFolder +
                ", editing=" + editing +
                ", votes=" + votes +
                '}';
    }
    
    public void setRatings(List<MapRating> ratings) {
        ratings.forEach(rating -> this.ratings.put(rating.getPlayer(), rating));
    }
    
    public Map<UUID, MapRating> getRatings() {
        return ratings;
    }
}
