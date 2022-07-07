package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.api.collection.IncrementalMap;
import com.thenexusreborn.api.helper.FileHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class GameMap {
    public static final int version = 1;
    private int id;
    private String url;
    private String name;
    
    private Position center = new Position(0, 0, 0);
    private IncrementalMap<Position> spawns = new IncrementalMap<>();
    private int borderDistance = 0, deathmatchBorderDistance = 0;
    private Set<String> creators = new HashSet<>();
    private boolean active;
    
    private UUID uniqueId; 
    private World world; 
    private Path downloadedZip; 
    private Path unzippedFolder; 
    private Path worldFolder; 
    private boolean editing;
    private int votes = 0;
    
    public GameMap(String fileName, String name) {
        this.url = fileName;
        this.name = name;
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
    
    public int addSpawn(Position position) {
        return this.spawns.add(position);
    }
    
    public void setSpawn(int index, Position position) {
        this.spawns.put(index, position);
    }
    
    public void removeSpawn(int index) {
        this.spawns.remove(index);
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getName() {
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
    
    public SortedMap<Integer, Position> getSpawns() {
        return spawns;
    }
    
    public int getBorderDistance() {
        return borderDistance;
    }
    
    public void setBorderDistance(int borderDistance) {
        this.borderDistance = borderDistance;
    }
    
    public int getDeathmatchBorderDistance() {
        return deathmatchBorderDistance;
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
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
}
