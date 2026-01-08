package com.thenexusreborn.survivalgames.map;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.staritems.model.CustomItem;
import com.stardevllc.starmclib.Position;
import com.thenexusreborn.gamemaps.MapManager;
import com.thenexusreborn.gamemaps.items.CmdItem;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("DuplicatedCode")
public class SGMapCommand implements CommandExecutor {
    private JavaPlugin plugin;
    private MapManager<SGMap> mapManager;
    
    public static final CustomItem SAVE_ITEM = new CmdItem("Save", SMaterial.PAPER, "sgmap save");
    public static final CustomItem SET_BOUNDS_ARENA_ITEM = new CmdItem("Set Arena Bounds", SMaterial.OAK_FENCE, "sgmap setbounds arena");
    public static final CustomItem SET_BOUNDS_DEATHMATCH_ITEM = new CmdItem("Set Deathmatch Bounds", SMaterial.OAK_FENCE_GATE, "sgmap setbounds deathmatch");
    public static final CustomItem ADD_SPAWN_ITEM = new CmdItem("Add Spawn", SMaterial.RED_BED, "sgmap addspawn");
    public static final CustomItem SET_SPAWN_CENTER_ITEM = new CmdItem("Set Center", SMaterial.COMPASS, "sgmap setspawncenter");
    public static final CustomItem SET_SWAG_SHACK_ITEM = new CmdItem("Set Swag Shack", SMaterial.CHEST, "sgmap setswagshack");
    public static final CustomItem VIEW_ARENA_BORDER = new CmdItem("View Arena Border", SMaterial.GLASS, "sgmap viewborder game");
    public static final CustomItem VIEW_DEATHMATCH_BORDER = new CmdItem("View Deathmatch Border", SMaterial.WHITE_WOOL, "sgmap viewborder deathmatch");
    
    private static final String URL_BASE = "https://assets.thenexusreborn.com/survivalgames/maps/";
    
    public SGMapCommand(JavaPlugin plugin, MapManager mapManager) {
        this.plugin = plugin;
        this.mapManager = mapManager;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length > 0)) {
            sender.sendMessage(StarColors.color("&cYou must provide a sub command."));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StarColors.color("&cOnly players can use that command."));
            return true;
        }
        
        if (args[0].equals("recalculateregions")) {
            StarColors.coloredMessage(player, "&7&oPlease wait while regions are recalculated...");
            for (SGMap map : this.mapManager.getMaps()) {
                Position arenaCenter = map.getArenaCenter();
                int arenaBorderLength = map.getArenaBorderLength() / 2;
                Position arenaMin = new Position(arenaCenter.getX() - arenaBorderLength - 1, map.getArenaMinimum().getY(), arenaCenter.getZ() - arenaBorderLength - 1);
                Position arenaMax = new Position(arenaCenter.getX() + arenaBorderLength + 1, map.getArenaMaximum().getY(), arenaCenter.getZ() + arenaBorderLength + 1);
                
                map.setArenaMinimum(arenaMin);
                map.setArenaMaximum(arenaMax);
                
                Position deathmatchCenter = map.getDeathmatchCenter();
                int deathmatchBorderLength = map.getDeathmatchBorderLength() / 2;
                Position deathmatchMin = new Position(deathmatchCenter.getX() - deathmatchBorderLength - 1, map.getDeathmatchMinimum().getY(), deathmatchCenter.getZ() - deathmatchBorderLength - 1);
                Position deathmatchMax = new Position(deathmatchCenter.getX() + deathmatchBorderLength + 1, map.getDeathmatchMaximum().getY(), deathmatchCenter.getZ() + deathmatchBorderLength + 1);
                
                map.setDeathmatchMinimum(deathmatchMin);
                map.setDeathmatchMaximum(deathmatchMax);
                
                mapManager.saveMap(map);
                
                StarColors.coloredMessage(player, "&eUpdated the regions for &e" + map.getName());
            }
            return true;
        }

        String mapSubCommand = args[0].toLowerCase();

        if (!mapManager.isEditMode()) {
            if (!(mapSubCommand.equalsIgnoreCase("setactive") || mapSubCommand.equalsIgnoreCase("sa") || mapSubCommand.equalsIgnoreCase("validate"))) {
                sender.sendMessage(StarColors.color("&cYou can only use that command when map editing mode is active."));
                return true;  
            }
        }

        if (mapSubCommand.equals("create") || mapSubCommand.equals("c")) {
            if (!(args.length > 2)) {
                sender.sendMessage(StarColors.color("&cUsage: /" + label + " create <url> <name>"));
                return true;
            }

            String url = URL_BASE + args[1];
            String mapName = getMapNameFromCommand(args, 2);
            if (mapManager.getMap(mapName) != null) {
                sender.sendMessage(StarColors.color("&cA map with that name already exists."));
                return true;
            }

            SGMap gameMap = new SGMap(url, mapName);
            mapManager.addMap(gameMap);
            sender.sendMessage(StarColors.color("&eCreated a map with the name &b" + gameMap.getName() + "&e."));
            new BukkitRunnable() {
                @Override
                public void run() {
                    mapManager.saveMap(gameMap);
                    sender.sendMessage(StarColors.color("&7&oThe map has been saved to the database."));
                }
            }.runTaskAsynchronously(plugin);
        } else {
            SGMap gameMap = null;
            boolean mapFromArgument = false;
            for (SGMap map : mapManager.getMaps()) {
                if (map.getWorld() != null) {
                    if (map.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
                        gameMap = map;
                        break;
                    }
                }
            }

            if (args.length > 1 && gameMap == null) {
                gameMap = mapManager.getMap(args[1]);
                mapFromArgument = true;
            }

            int argIndex;
            if (mapFromArgument) {
                argIndex = 2;
            } else {
                argIndex = 1;
            }

            if (gameMap == null) {
                player.sendMessage(StarColors.color("&cCould not find a valid map."));
                return true;
            }

            switch (mapSubCommand) {
                case "validate" -> {
                    StarColors.coloredMessage(player, "&6&l>> &e&lSettings for map &b&l" + gameMap.getName());
                    StarColors.coloredMessage(player, "&6&l>> &dKey: &aGreen = Good&d, &cRed = Invalid (Required)&d, &eYellow = Invalid (Optional)");
                    StarColors.coloredMessage(player, " &6&l> &7Status: " + (gameMap.isActive() ? "&a&lACTIVE" : "&c&lINACTIVE") + "  " + (gameMap.isValid() ? "&a&lVALID" : "&c&lINVALID"));
                    String url = gameMap.getUrl();
                    String urlMsg = url == null || url.isBlank() ? "&cNot Set" : "&a" + url;
                    StarColors.coloredMessage(player, " &6&l> &7URL: " + urlMsg);
                    StarColors.coloredMessage(player, " &6&l> &7Spawns: " + (gameMap.getSpawns().size() < 2 ? "&c" + gameMap.getSpawns().size() + "/2" : "&a" + gameMap.getSpawns().size()));
                    StringBuilder creators = new StringBuilder();
                    if (gameMap.getCreators().isEmpty()) {
                        creators.append("&cNone");
                    } else {
                        for (String creator : gameMap.getCreators()) {
                            creators.append("&a").append(creator).append("&7, ");
                        }
                        
                        creators.delete(creators.length() - 4, creators.length());
                    }
                    
                    StarColors.coloredMessage(player, " &6&l> &7Creators: " + creators);
                    StarColors.coloredMessage(player, " &6&l> &7Spawn Center: " + validatePosition(gameMap.getSpawnCenter()));
                    StarColors.coloredMessage(player, " &6&l> &7Arena Min: " + validatePosition(gameMap.getArenaMinimum()));
                    StarColors.coloredMessage(player, " &6&l> &7Arena Max: " + validatePosition(gameMap.getArenaMaximum()));
                    StarColors.coloredMessage(player, " &6&l> &7Arena Center: " + validatePosition(gameMap.getArenaCenter()));
                    StarColors.coloredMessage(player, " &6&l> &7Deathmatch Min: " + validatePosition(gameMap.getDeathmatchMinimum()));
                    StarColors.coloredMessage(player, " &6&l> &7Deathmatch Max: " + validatePosition(gameMap.getDeathmatchMaximum()));
                    StarColors.coloredMessage(player, " &6&l> &7Deathmatch Center: " + validatePosition(gameMap.getDeathmatchCenter()));
                    StarColors.coloredMessage(player, " &6&l> &7Swag Shack: " + validatePosition(gameMap.getSwagShack(), true));
                    return true;
                }
                case "download", "dl" -> {
                    SGMap finalGameMap = gameMap;
                    mapManager.setMapBeingEdited(finalGameMap);
                    player.sendMessage(StarColors.color("&7&oPlease wait, downloading the map " + finalGameMap.getName() + "."));
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        boolean successful = finalGameMap.download(plugin);
                        if (successful) {
                            player.sendMessage(StarColors.color("&eDownloaded the map &b" + finalGameMap.getName() + "."));
                        } else {
                            player.sendMessage(StarColors.color("&cFailed to download the map " + finalGameMap.getName()));
                        }
                    });
                    return true;
                }
                case "load", "l" -> {
                    gameMap.unzip(plugin);
                    gameMap.copyFolder(plugin, "", false);
                    gameMap.load(plugin);
                    if (gameMap.getWorld() != null) {
                        gameMap.setEditing(true);
                        if (gameMap.getCenterLocation() != null) {
                            gameMap.getCenterLocation().getBlock().setType(Material.BEDROCK);
                        }
                        
                        if (gameMap.getSwagShack() != null) {
                            gameMap.getSwagShack().toBlockLocation(gameMap.getWorld()).getBlock().setType(Material.BEDROCK);
                        }
                        
                        if (!gameMap.getSpawns().isEmpty()) {
                            for (MapSpawn mapSpawn : gameMap.getSpawns()) {
                                mapSpawn.updateHologram(gameMap.getWorld());
                            }
                        }
                        
                        sender.sendMessage(StarColors.color("&eSuccessfully loaded the map &b" + gameMap.getName() + "."));
                    } else {
                        sender.sendMessage(StarColors.color("&cCould not load the map " + gameMap.getName() + ". Please report as a bug."));
                    }
                    return true;
                }
                case "teleport", "tp" -> {
                    if (gameMap.getWorld() == null) {
                        sender.sendMessage(StarColors.color("&cThat map is not loaded. Please load before teleporting."));
                        return true;
                    }
                    teleportToMap(player, gameMap);
                    player.sendMessage(StarColors.color("&eTeleported to the map " + gameMap.getName()));
                    return true;
                }
                case "save", "s" -> {
                    mapManager.saveMap(gameMap);
                    player.sendMessage(StarColors.color("&eSaved the settings for the map &b" + gameMap.getName()));
                }
                case "removefromserver", "rfs" -> {
                    mapManager.setMapBeingEdited(null);
                    gameMap.removeFromServer(plugin);
                    gameMap.setEditing(false);
                    player.sendMessage(StarColors.color("&eRemoved the map &b" + gameMap.getName() + " &efrom the server."));
                    return true;
                }
                case "delete" -> player.sendMessage(StarColors.color("&cThis command is not yet implemented."));
                case "setbounds", "sb" -> {
                    Selection selection = WorldEditPlugin.getPlugin(WorldEditPlugin.class).getSelection(player);
                    if (selection == null) {
                        StarColors.coloredMessage(player, "&cYou must have a WorldEdit selection");
                        return true;
                    }

                    if (!(selection instanceof CuboidSelection cuboidRegion)) {
                        StarColors.coloredMessage(player, "&cThe selection must be cuboid");
                        return true;
                    }
                    
                    if (!(args.length > 1)) {
                        StarColors.coloredMessage(player, "&cYou must provide a type: arena|a or deathmatch|dm");
                        return true;
                    }
                    
                    String type = switch (args[1].toLowerCase()) {
                        case "arena", "a" -> "arena";
                        case "deathmatch", "dm" -> "deathmatch";
                        default -> null;
                    };
                    
                    if (type == null) {
                        StarColors.coloredMessage(player, "&cInvalid type, you must provide: arena|a or deathmatch|dm");
                        return true;
                    }
                    
                    Location min = cuboidRegion.getMinimumPoint();
                    Location max = cuboidRegion.getMaximumPoint();
                    
                    Position minimum = new Position(min.getBlockX(), min.getBlockY(), min.getBlockZ());
                    Position maximum = new Position(max.getBlockX(), max.getBlockY(), max.getBlockZ());
                    
                    int sideLength = Math.max(cuboidRegion.getLength(), cuboidRegion.getWidth());

                    Position center = new Position((min.getX() + max.getX()) / 2, (min.getY() + max.getY()) / 2, (min.getZ() + max.getZ()) / 2);
                    if (type.equals("arena")) {
                        gameMap.setArenaCenter(center);
                        gameMap.setArenaMinimum(minimum);
                        gameMap.setArenaMaximum(maximum);
                        gameMap.setArenaBorderLength(sideLength);
                    } else if (type.equals("deathmatch")) {
                        gameMap.setDeathmatchCenter(center);
                        gameMap.setDeathmatchMinimum(minimum);
                        gameMap.setDeathmatchMaximum(maximum);
                        gameMap.setDeathmatchBorderLength(sideLength);
                    }

                    StarColors.coloredMessage(player, "&6&l>> &eSet Bounds Results for &b" + type);
                    StarColors.coloredMessage(player, " &6&l> &7Minimum: &b" + minimum.getBlockX() + "&7, &b" + minimum.getBlockY() + "&7, &b" + minimum.getBlockZ());
                    StarColors.coloredMessage(player, " &6&l> &7Maximum: &b" + maximum.getBlockX() + "&7, &b" + maximum.getBlockY() + "&7, &b" + maximum.getBlockZ());
                    StarColors.coloredMessage(player, " &6&l> &7Center: &b" + center.getBlockX() + "&7, &b" + center.getBlockY() + "&7, &b" + center.getBlockZ());
                    StarColors.coloredMessage(player, " &6&l> &7Border Side Length: &b" + sideLength);
                }
                case "addspawn", "as" -> {
                    Location location = player.getLocation();
                    MapSpawn spawn = new MapSpawn(0, -1, location.getBlockX(), location.getBlockY(), location.getBlockZ());
                    int position = gameMap.addSpawn(spawn);
                    spawn.updateHologram(gameMap.getWorld());
                    sender.sendMessage(StarColors.color("&eYou added a spawn with index &b" + (position + 1) + " &eto the map &b" + gameMap.getName()));
                }
                case "clearspawns" -> {
                    for (MapSpawn spawn : gameMap.getSpawns()) {
                        spawn.deleteHologram();
                        gameMap.clearSpawns();
                    }
                }
                case "setspawn", "ss" -> {
                    if (!(args.length > argIndex)) {
                        sender.sendMessage(StarColors.color("&cYou must provide an index for the spawn."));
                        return true;
                    }

                    int position;
                    try {
                        position = Integer.parseInt(args[argIndex]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(StarColors.color("&cYou provided an invalid number for the spawn index."));
                        return true;
                    }

                    MapSpawn existingSpawnPosition = gameMap.getSpawn(position - 1);
                    if (existingSpawnPosition != null) {
                        existingSpawnPosition.deleteHologram();
                    }
                    
                    Location location = player.getLocation();
                    MapSpawn existingSpawnLocation = null;
                    for (MapSpawn spawn : gameMap.getSpawns()) {
                        if (spawn.getBlockX() == location.getBlockX() && spawn.getBlockY() == location.getBlockY() && spawn.getBlockZ() == location.getBlockZ()) {
                            existingSpawnLocation = spawn;
                            break;
                        }
                    }

                    MapSpawn mapSpawn;
                    if (existingSpawnLocation == null) {
                        mapSpawn = new MapSpawn(gameMap.getId(), position - 1, location.getBlockX(), location.getBlockY(), location.getBlockZ());
                    } else {
                        mapSpawn = existingSpawnLocation;
                        mapSpawn.setIndex(position - 1);
                    }
                    gameMap.setSpawn(position - 1, mapSpawn);
                    mapSpawn.updateHologram(gameMap.getWorld());
                    sender.sendMessage(StarColors.color("&eYou set the spawn at position &b" + position + " &eto your location in the map &b" + gameMap.getName()));
                }
                case "removespawn", "rs" -> {
                    if (!(args.length > argIndex)) {
                        sender.sendMessage(StarColors.color("&cYou must provide an index for the spawn."));
                        return true;
                    }

                    int position;
                    try {
                        position = Integer.parseInt(args[argIndex]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(StarColors.color("&cYou provided an invalid number for the spawn index."));
                        return true;
                    }

                    MapSpawn existingSpawn = gameMap.getSpawn(position - 1);
                    if (existingSpawn == null) {
                        MsgType.WARN.send(player, "A spawn with the index %v does not exist.", position);
                        return true;
                    }
                    
                    existingSpawn.deleteHologram();
                    
                    gameMap.removeSpawn(position - 1);
                    MsgType.INFO.send(player, "You removed the spawn with the index %v", position);
                    MsgType.INFO.send(player, "Spawns have been recalculated");
                }
                case "calculatespawns", "cs" -> {
                    if (!(args.length > argIndex)) {
                        sender.sendMessage(StarColors.color("&cYou must provide a type to search for."));
                        return true;
                    }
                    
                    Material spawnBlockType;
                    
                    if (args[argIndex].equalsIgnoreCase("hand")) {
                        ItemStack handItem = player.getInventory().getItemInHand();
                        if (handItem == null) {
                            MsgType.WARN.send(player, "You are not holding a valid item");
                            return true;
                        }
                        
                        spawnBlockType = handItem.getType();
                    } else {
                        try {
                            spawnBlockType = Material.valueOf(args[argIndex].toUpperCase());
                        } catch (IllegalArgumentException e) {
                            MsgType.WARN.send(player, "Invalid material type %v", args[argIndex]);
                            return true;
                        }
                    }
                    
                    Selection selection = WorldEditPlugin.getPlugin(WorldEditPlugin.class).getSelection(player);
                    if (selection == null) {
                        StarColors.coloredMessage(player, "&cYou must have a WorldEdit selection");
                        return true;
                    }

                    if (!(selection instanceof CuboidSelection cuboidSelection)) {
                        StarColors.coloredMessage(player, "&cThe selection must be cuboid");
                        return true;
                    }

                    try {
                        for (BlockVector blockVector : cuboidSelection.getRegionSelector().getRegion()) {
                            Block block = gameMap.getWorld().getBlockAt(blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ());
                            if (block.getType() == spawnBlockType) {
                                Location location = block.getLocation();
                                MapSpawn spawn = new MapSpawn(0, -1, location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
                                int position = gameMap.addSpawn(spawn);
                                spawn.updateHologram(gameMap.getWorld());
                                sender.sendMessage(StarColors.color("&eYou added a spawn with index &b" + (position + 1) + " &eto the map &b" + gameMap.getName()));
                            }
                        }
                    } catch (IncompleteRegionException e) {
                        MsgType.WARN.send(player, "Your WorldEdit selection is incomplete");
                        return true;
                    }
                }
                case "setspawncenter", "ssc" -> {
                    Location centerLocation = gameMap.getCenterLocation();
                    if (centerLocation != null) {
                        centerLocation.getBlock().setType(Material.AIR);
                    }

                    Location location = player.getLocation();
                    gameMap.setSpawnCenter(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                    player.teleport(location.clone().add(0, 1, 0));
                    location.getBlock().setType(Material.BEDROCK);
                    player.sendMessage(StarColors.color("&eYou set the center of the map &b" + gameMap.getName() + " &eto your current location."));
                }
                case "creators", "cr" -> {
                    if (!(args.length > argIndex)) {
                        sender.sendMessage(StarColors.color("&cYou must provide the creators."));
                        return true;
                    }

                    StringBuilder cb = new StringBuilder();
                    for (int i = argIndex; i < args.length; i++) {
                        cb.append(args[i]).append(" ");
                    }

                    String[] creators = cb.toString().trim().split(",");
                    if (creators.length == 0) {
                        sender.sendMessage(StarColors.color("&cYou must separate the creators with commas."));
                        return true;
                    }

                    for (String creator : creators) {
                        gameMap.addCreator(creator);
                        sender.sendMessage(StarColors.color("&eYou added &b" + creator + " &eas a creator on map &b" + gameMap.getName()));
                    }
                }
                case "setactive", "sa" -> {
                    if (!(args.length > argIndex)) {
                        sender.sendMessage(StarColors.color("&cYou must provide a true or false value."));
                        return true;
                    }

                    boolean value = Boolean.parseBoolean(args[argIndex]);
                    gameMap.setActive(value);
                    if (value && !gameMap.isActive()) {
                        sender.sendMessage(StarColors.color("&cFailed to set the map to an active status, there are required elements missing."));
                    } else {
                        sender.sendMessage(StarColors.color("&eYou set the status of the map to " + value));
                    }
                }
                case "setswagshack", "sss" -> {
                    if (gameMap.getSwagShack() != null) {
                        gameMap.getSwagShack().toBlockLocation(gameMap.getWorld()).getBlock().setType(Material.AIR);
                    }
                    
                    Location location = player.getPlayer().getLocation();
                    gameMap.setSwagShack(new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                    player.teleport(location.clone().add(0, 1, 0));
                    location.getBlock().setType(Material.BEDROCK);
                    player.sendMessage(StarColors.color("&eYou set the swag shack of the map &b" + gameMap.getName() + " &eto your current location."));
                }
                case "viewborder", "vb" -> {
                    if (!(args.length > 1)) {
                        player.sendMessage(StarColors.color("&cYou must say if it is for the game or deathmatch."));
                        return true;
                    }
                    
                    String viewOption = args[1].toLowerCase();
                    if (!(viewOption.equals("game") || viewOption.equals("deathmatch"))) {
                        player.sendMessage(StarColors.color("&cYou provided an invalid type."));
                        return true;
                    }
                    
                    gameMap.applyWorldBoarder(viewOption);
                    mapManager.setBorderViewOption(viewOption);
                    mapManager.setViewingWorldBorder(true);
                    player.sendMessage(StarColors.color("&eYou are now viewing the world border as " + args[1].toLowerCase()));
                    return true;
                }
                case "disableworldborder", "dwb" -> {
                    if (mapManager.isViewingWorldBorder()) {
                        mapManager.setViewingWorldBorder(false);
                        mapManager.setBorderViewOption("");
                        gameMap.disableWorldBorder();
                        player.sendMessage(StarColors.color("&eYou disabled the world border preview."));
                    } else {
                        player.sendMessage(StarColors.color("&cThe world border is not being previewed."));
                    }
                    return true;
                }
                case "analyze" -> {
                    player.sendMessage(StarColors.color("&eStarting map anaylsis for &b" + gameMap.getName() + "&e..."));
                    gameMap.setChests(0);
                    gameMap.setEnchantTables(0);
                    gameMap.setWorkbenches(0);
                    gameMap.setTotalBlocks(0);
                    gameMap.setFurnaces(0);
                    
                    new AnalyzeThread(plugin, gameMap, player).runTask(plugin);
                    return true;
                }
                case "analysis" -> {
                    player.sendMessage(StarColors.color("&6&l>> &eMap analysis results for &b" + gameMap.getName()));
                    player.sendMessage(StarColors.color(" &6&l> &7Total Blocks: &b" + gameMap.getTotalBlocks()));
                    player.sendMessage(StarColors.color(" &6&l> &7Total Chests: &b" + gameMap.getChests()));
                    player.sendMessage(StarColors.color(" &6&l> &7Total Ender Chests: &b" + gameMap.getEnderChestLocations().size()));
                    player.sendMessage(StarColors.color(" &6&l> &7Workbenches: &b" + gameMap.getWorkbenches()));
                    player.sendMessage(StarColors.color(" &6&l> &7Enchantment Tables: &b" + gameMap.getEnchantTables()));
                    player.sendMessage(StarColors.color(" &6&l> &7Furnaces: &b" + gameMap.getFurnaces()));
                    return true;
                }
                case "downloadloadteleport", "dlltp" -> {
                    SGMap finalGameMap = gameMap;
                    mapManager.setMapBeingEdited(finalGameMap);
                    player.sendMessage(StarColors.color("&6&l>> &ePlease wait..."));
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        String name = finalGameMap.getName();
                        if (!finalGameMap.download(plugin)) {
                            player.sendMessage(StarColors.color("&4&l>> &cFailed to download the map " + name));
                            return;
                        }
                        
                        player.sendMessage(StarColors.color("&2&l>> &aDownloaded the map &b" + name));

                        finalGameMap.unzip(plugin);
                        player.sendMessage(StarColors.color("&2&l>> &aUnzipped the map &b" + name));
                        finalGameMap.copyFolder(plugin, "", false);
                        player.sendMessage(StarColors.color("&2&l>> &aCopied the map files for &b" + name));
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (!finalGameMap.load(plugin)) {
                                sender.sendMessage(StarColors.color("&cCould not load the map " + name + ". Please report as a bug."));
                                return;
                            }
                            
                            player.sendMessage(StarColors.color("&2&l>> &aLoaded the map &b" + name + " &aas a world"));
                            
                            finalGameMap.setEditing(true);

                            if (finalGameMap.getCenterLocation() != null) {
                                finalGameMap.getCenterLocation().getBlock().setType(Material.BEDROCK);
                            }

                            if (finalGameMap.getSwagShack() != null) {
                                finalGameMap.getSwagShack().toBlockLocation(finalGameMap.getWorld()).getBlock().setType(Material.BEDROCK);
                            }

                            if (!finalGameMap.getSpawns().isEmpty()) {
                                for (MapSpawn mapSpawn : finalGameMap.getSpawns()) {
                                    mapSpawn.updateHologram(finalGameMap.getWorld());
                                }
                            }
                            
                            teleportToMap(player, finalGameMap);
                            player.sendMessage(StarColors.color("&eSuccessfully setup and teleported you to the map " + name));
                        });
                    });
                    return true;
                }
                default -> {
                    return true;
                }
            }
            SGMap finalGameMap = gameMap;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (mapSubCommand.equalsIgnoreCase("validate")) {
                        return;
                    }
                    
                    mapManager.saveMap(finalGameMap);
                    sender.sendMessage(StarColors.color("&7&oThe map has been saved to the database."));
                }
            }.runTaskAsynchronously(plugin);
            return true;
        }
        return true;
    }
    
    public static void teleportToMap(Player player, SGMap sgMap) {
        Location spawn;
        if (sgMap.getSpawnCenter() != null) {
            spawn = sgMap.getSpawnCenter().toLocation(sgMap.getWorld());
        } else {
            spawn = sgMap.getWorld().getSpawnLocation();
        }

        player.teleport(spawn);
        player.setGameMode(GameMode.CREATIVE);
        
        PlayerInventory inv = player.getInventory();
        inv.clear();
        
        inv.addItem(new ItemStack(Material.WOOD_AXE)); //This is the world edit wand
        inv.addItem(SAVE_ITEM.toItemStack());
        inv.addItem(SET_BOUNDS_ARENA_ITEM.toItemStack());
        inv.addItem(SET_BOUNDS_DEATHMATCH_ITEM.toItemStack());
        inv.addItem(ADD_SPAWN_ITEM.toItemStack());
        inv.addItem(SET_SPAWN_CENTER_ITEM.toItemStack());
        inv.addItem(SET_SWAG_SHACK_ITEM.toItemStack());
        inv.addItem(VIEW_ARENA_BORDER.toItemStack());
        inv.addItem(VIEW_DEATHMATCH_BORDER.toItemStack());
    }

    public static String getMapNameFromCommand(String[] args, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }
    
    private static String validatePosition(Position position) {
        return validatePosition(position, false);
    }
    
    private static String validatePosition(Position position, boolean optional) {
        if (position == null) {
            return (optional ? "&e" : "&c") + "Not Set";
        }
        
        return "&a" + position.getBlockX() + "&7, &a" + position.getBlockY() + "&7, &a" + position.getBlockZ();
    }
}
