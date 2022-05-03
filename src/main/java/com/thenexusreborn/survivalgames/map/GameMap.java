package com.thenexusreborn.survivalgames.map;

import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.collection.IncrementalMap;
import com.thenexusreborn.nexuscore.util.helper.FileHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class GameMap {
    public static final int version = 1;
    private String fileName; // This is the file name on the file url for this map. All other settings come from files in the ZIP file
    private String name; // This is the map name. This is stored locally so that it doesn't have to be downloaded to get the name.
    
    //Settings from the config file in the ZIP file
    private Position center = new Position(0, 0, 0);
    private IncrementalMap<Position> spawns = new IncrementalMap<>();
    private int borderDistance = 0, deathmatchBorderDistance = 0;
    private List<String> creators = new ArrayList<>();
    
    //Fields for using this map actively
    private UUID uniqueId; //This is for when the map is being used to prevent issues that can happen
    private World world; //This is the Bukkit world of the loaded map
    private Path downloadedZip; // This is the path of the ZIP file that was downloaded
    private Path unzippedFolder; //This is the path of the unzipped folder
    private Config config; //This the the loaded configuration information for the map, this populates the settings
    private Path worldFolder; //This is the folder that the world is located
    private boolean editing;
    private int votes = 0;
    
    public GameMap(String fileName, String name) {
        this.fileName = fileName;
        this.name = name;
    }
    
    public void delete(SurvivalGames plugin) {
        try {
            uniqueId = null;
            if (downloadedZip != null) {
                Files.deleteIfExists(downloadedZip);
                downloadedZip = null;
            }
            
            saveSettings();
            config = null;
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
    
    //Downloads the map and loads settings, but does not load the actual world or map
    public boolean download(SurvivalGames plugin) {
        Path downloadFolder = FileHelper.subPath(plugin.getDataFolder().toPath(), "mapdownloads");
        FileHelper.createDirectoryIfNotExists(downloadFolder);
        downloadedZip = FileHelper.downloadFile(SurvivalGames.MAP_URL + fileName, downloadFolder, fileName, true);
        
        Path mapConfigsDirectory = FileHelper.subPath(plugin.getDataFolder().toPath(), "mapconfigs");
        FileHelper.createDirectoryIfNotExists(mapConfigsDirectory);
        byte[] buffer = new byte[1024];
        boolean hasFile = false;
        try {
            ZipInputStream zis = new ZipInputStream(Files.newInputStream(downloadedZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.getName().equalsIgnoreCase("settings.yml")) {
                    Path settingsFilePath = FileHelper.subPath(mapConfigsDirectory, name.toLowerCase().replace(" ", "_").replace("'", "") + ".yml");
                    FileOutputStream fos = new FileOutputStream(settingsFilePath.toFile());
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    
                    this.config = new Config(plugin, mapConfigsDirectory.getFileName().toString(), settingsFilePath.getFileName().toString());
                    this.config.setup();
                    if (config.contains("name")) {
                        this.name = config.getString("name");
                    }
                    if (config.contains("borderdistance")) {
                        this.borderDistance = Integer.parseInt(config.getString("borderdistance"));
                    }
                    if (config.contains("deathmatchborderdistance")) {
                        this.deathmatchBorderDistance = Integer.parseInt(config.getString("deathmatchborderdistance"));
                    }
                    if (config.contains("center")) {
                        int x = Integer.parseInt(config.getString("center.x"));
                        int y = Integer.parseInt(config.getString("center.y"));
                        int z = Integer.parseInt(config.getString("center.z"));
                        this.center = new Position(x, y, z);
                    }
                    if (config.contains("spawns")) {
                        for (String rawSpawnId : config.getConfigurationSection("spawns").getKeys(false)) {
                            Integer spawnId = Integer.parseInt(rawSpawnId);
                            int x = Integer.parseInt(config.getString("spawns." + rawSpawnId + ".x"));
                            int y = Integer.parseInt(config.getString("spawns." + rawSpawnId + ".y"));
                            int z = Integer.parseInt(config.getString("spawns." + rawSpawnId + ".z"));
                            this.spawns.put(spawnId, new Position(x, y, z));
                        }
                    }
                    if (config.contains("creators")) {
                        this.creators.addAll(config.getStringList("creators"));
                    }
                    hasFile = true;
                    break;
                } else if (zipEntry.getName().contains("settings.properties")) {
                    //From MCTheNexus settings
                    Properties properties = new Properties();
                    Path propertiesFile = FileHelper.subPath(mapConfigsDirectory, name.toLowerCase().replace(" ", "_").replace("'", "") + ".properties");
                    FileOutputStream fos = new FileOutputStream(propertiesFile.toFile());
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    
                    properties.load(Files.newInputStream(propertiesFile.toFile().toPath()));
                    try {
                        this.deathmatchBorderDistance = Integer.parseInt(properties.getProperty("dmradius"));
                    } catch (Exception e) {}
                }
                
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        if (!hasFile) {
            Path settingsFilePath = FileHelper.subPath(mapConfigsDirectory, name.toLowerCase().replace(" ", "_").replace("'", "") + ".yml");
            FileHelper.createFileIfNotExists(settingsFilePath);
            this.config = new Config(plugin, mapConfigsDirectory.getFileName().toString(), settingsFilePath.getFileName().toString());
            this.config.setup();
            saveSettings();
        }
        return true;
    }
    
    public void saveSettings() {
        this.config.set("name", this.name);
        this.config.set("center.x", this.center.getX());
        this.config.set("center.y", this.center.getY());
        this.config.set("center.z", this.center.getZ());
        this.config.set("borderdistance", this.borderDistance + "");
        this.config.set("creators", this.creators);
        this.config.set("deathmatchborderdistance", this.deathmatchBorderDistance + "");
        if (!this.spawns.isEmpty()) {
            this.spawns.forEach((id, position) -> {
                this.config.set("spawns." + id + ".x", position.getX());
                this.config.set("spawns." + id + ".y", position.getY());
                this.config.set("spawns." + id + ".z", position.getZ());
            });
        }
        this.config.save();
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
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
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
    
    public List<String> getCreators() {
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
    
    public Config getConfig() {
        return config;
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
}
