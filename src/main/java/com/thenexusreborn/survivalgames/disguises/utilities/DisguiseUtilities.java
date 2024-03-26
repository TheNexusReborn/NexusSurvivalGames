package com.thenexusreborn.survivalgames.disguises.utilities;

import com.comphenix.protocol.*;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.disguises.DisguiseAPI;
import com.thenexusreborn.survivalgames.disguises.NexusDisguises;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.*;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.TargetedDisguise.TargetType;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers.AgeableWatcher;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers.PlayerWatcher;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers.ZombieWatcher;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"deprecation", "SuspiciousMethodCalls", "DuplicatedCode", "ExtractMethodRecommender"})
public class DisguiseUtilities {

    private static HashSet<String> addedByPlugins = new HashSet<>();
    private static Object bedChunk;
    private static HashMap<UUID, HashSet<TargetedDisguise>> disguisesInUse = new HashMap<>();
    private static HashMap<Integer, HashSet<TargetedDisguise>> futureDisguises = new HashMap<>();
    private static HashMap<String, WrappedGameProfile> gameProfiles = new HashMap<>();
    private static SurvivalGames libsDisguises = NexusDisguises.plugin;
    private static HashMap<String, ArrayList<Object>> runnables = new HashMap<>();
    private static HashSet<UUID> selfDisguised = new HashSet<>();
    private static Field xChunk, zChunk;

    static {
        try {
            Object server = ReflectionManager.getNmsMethod("MinecraftServer", "getServer").invoke(null);
            Object world = ((List<?>) server.getClass().getField("worlds").get(server)).get(0);
            bedChunk = ReflectionManager.getNmsClass("Chunk")
                    .getConstructor(ReflectionManager.getNmsClass("World"), int.class, int.class).newInstance(world, 0, 0);
            Field cSection = bedChunk.getClass().getDeclaredField("sections");
            cSection.setAccessible(true);
            Object chunkSection = ReflectionManager.getNmsClass("ChunkSection").getConstructor(int.class, boolean.class)
                    .newInstance(0, true);
            Object block;
            try {
                block = ReflectionManager.getNmsClass("Block").getMethod("getById", int.class)
                        .invoke(null, Material.BED_BLOCK.getId());
            } catch (Exception ex) {
                block = ((Object[]) ReflectionManager.getNmsField(ReflectionManager.getNmsClass("Block"), "byId").get(null))[Material.BED_BLOCK.getId()];
            }
            Method fromLegacyData = block.getClass().getMethod("fromLegacyData", int.class);
            Method setType = chunkSection.getClass().getMethod("setType", int.class, int.class, int.class,
                    ReflectionManager.getNmsClass("IBlockData"));
            Method setSky = chunkSection.getClass().getMethod("a", int.class, int.class, int.class, int.class);
            Method setEmitted = chunkSection.getClass().getMethod("b", int.class, int.class, int.class, int.class);
            for (BlockFace face : new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH}) {
                setType.invoke(chunkSection, 1 + face.getModX(), 0, 1 + face.getModZ(), fromLegacyData.invoke(block, face.ordinal()));
                setSky.invoke(chunkSection, 1 + face.getModX(), 0, 1 + face.getModZ(), 0);
                setEmitted.invoke(chunkSection, 1 + face.getModX(), 0, 1 + face.getModZ(), 0);
            }

            Object[] array = (Object[]) Array.newInstance(chunkSection.getClass(), 16);
            array[0] = chunkSection;
            cSection.set(bedChunk, array);
            xChunk = bedChunk.getClass().getField("locX");
            xChunk.setAccessible(true);
            zChunk = bedChunk.getClass().getField("locZ");
            zChunk.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public static void addDisguise(UUID entityId, TargetedDisguise disguise) {
        if (!getDisguises().containsKey(entityId)) {
            getDisguises().put(entityId, new HashSet<>());
        }
        getDisguises().get(entityId).add(disguise);
        checkConflicts(disguise, null);
        if (disguise.getDisguiseTarget() == TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS && disguise.isModifyBoundingBox()) {
            doBoundingBox(disguise);
        }
    }

    public static void addFutureDisguise(final int entityId, final TargetedDisguise disguise) {
        if (!futureDisguises.containsKey(entityId)) {
            futureDisguises.put(entityId, new HashSet<>());
        }
        futureDisguises.get(entityId).add(disguise);
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (futureDisguises.containsKey(entityId) && futureDisguises.get(entityId).contains(disguise)) {
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if (entity.getEntityId() == entityId) {
                                UUID uniqueId = entity.getUniqueId();
                                for (TargetedDisguise disguise : futureDisguises.remove(entityId)) {
                                    addDisguise(uniqueId, disguise);
                                }
                                return;
                            }
                        }
                    }
                    futureDisguises.get(entityId).remove(disguise);
                    if (futureDisguises.get(entityId).isEmpty()) {
                        futureDisguises.remove(entityId);
                    }
                }
            }
        };
        runnable.runTaskLater(libsDisguises, 20);
    }

    public static void addGameProfile(String string, WrappedGameProfile gameProfile) {
        getGameProfiles().put(string, gameProfile);
        getAddedByPlugins().add(string.toLowerCase());
    }

    public static void checkConflicts(TargetedDisguise disguise, String name) {
        // If the disguise is being used.. Else we may accidentally undisguise something else
        if (DisguiseAPI.isDisguiseInUse(disguise)) {
            Iterator<TargetedDisguise> disguiseItel = getDisguises().get(disguise.getEntity().getUniqueId()).iterator();
            // Iterate through the disguises
            while (disguiseItel.hasNext()) {
                TargetedDisguise d = disguiseItel.next();
                // Make sure the disguise isn't the same thing
                if (d != disguise) {
                    // If the loop'd disguise is hiding the disguise to everyone in its list
                    if (d.getDisguiseTarget() == TargetType.HIDE_DISGUISE_TO_EVERYONE_BUT_THESE_PLAYERS) {
                        // If player is a observer in the loop
                        if (disguise.getDisguiseTarget() == TargetType.HIDE_DISGUISE_TO_EVERYONE_BUT_THESE_PLAYERS) {
                            // If player is a observer in the disguise
                            // Remove them from the loop
                            if (name != null) {
                                d.removePlayer(name);
                            } else {
                                for (String playername : disguise.getObservers()) {
                                    d.silentlyRemovePlayer(playername);
                                }
                            }
                        } else if (disguise.getDisguiseTarget() == TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS) {
                            // If player is not a observer in the loop
                            if (name != null) {
                                if (!disguise.getObservers().contains(name)) {
                                    d.removePlayer(name);
                                }
                            } else {
                                for (String playername : new ArrayList<>(d.getObservers())) {
                                    if (!disguise.getObservers().contains(playername)) {
                                        d.silentlyRemovePlayer(playername);
                                    }
                                }
                            }
                        }
                    } else if (d.getDisguiseTarget() == TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS) {
                        // Here you add it to the loop if they see the disguise
                        if (disguise.getDisguiseTarget() == TargetType.HIDE_DISGUISE_TO_EVERYONE_BUT_THESE_PLAYERS) {
                            // Everyone who is in the disguise needs to be added to the loop
                            if (name != null) {
                                d.addPlayer(name);
                            } else {
                                for (String playername : disguise.getObservers()) {
                                    d.silentlyAddPlayer(playername);
                                }
                            }
                        } else if (disguise.getDisguiseTarget() == TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS) {
                            // This here is a paradox.
                            // If fed a name. I can do this.
                            // But the rest of the time.. Its going to conflict.
                            // The below is debug output. Most people wouldn't care for it.

                            // System.out.print("Cannot set more than one " + TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS
                            // + " on a entity. Removed the old disguise.");
                            disguiseItel.remove();
                            d.removeDisguise();
                        }
                    }
                }
            }
        }
    }

    public static void destroyEntity(TargetedDisguise disguise) {
        try {
            Object entityTrackerEntry = ReflectionManager.getEntityTrackerEntry(disguise.getEntity());
            if (entityTrackerEntry != null) {
                Set<?> trackedPlayers = (Set<?>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers").get(entityTrackerEntry);
                // If the tracker exists. Remove himself from his tracker
                trackedPlayers = new HashSet<>(trackedPlayers);  //Copy before iterating to prevent ConcurrentModificationException
                PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
                destroyPacket.getIntegerArrays().write(0, new int[]{disguise.getEntity().getEntityId()});
                for (Object p : trackedPlayers) {
                    Player player = (Player) ReflectionManager.getBukkitEntity(p);
                    if (player == disguise.getEntity() || disguise.canSee(player)) {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyPacket);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public static void doBoundingBox(TargetedDisguise disguise) {
        Entity entity = disguise.getEntity();
        if (entity != null) {
            if (isDisguiseInUse(disguise)) {
                DisguiseValues disguiseValues = DisguiseValues.getDisguiseValues(disguise.getType());
                FakeBoundingBox disguiseBox = disguiseValues.getAdultBox();
                if (disguiseValues.getBabyBox() != null) {
                    if ((disguise.getWatcher() instanceof AgeableWatcher && ((AgeableWatcher) disguise.getWatcher()).isBaby())
                            || (disguise.getWatcher() instanceof ZombieWatcher && ((ZombieWatcher) disguise.getWatcher())
                            .isBaby())) {
                        disguiseBox = disguiseValues.getBabyBox();
                    }
                }
                ReflectionManager.setBoundingBox(entity, disguiseBox);
            } else {
                DisguiseValues entityValues = DisguiseValues.getDisguiseValues(DisguiseType.getType(entity.getType()));
                FakeBoundingBox entityBox = entityValues.getAdultBox();
                if (entityValues.getBabyBox() != null) {
                    if ((entity instanceof Ageable && !((Ageable) entity).isAdult())
                            || (entity instanceof Zombie && ((Zombie) entity).isBaby())) {
                        entityBox = entityValues.getBabyBox();
                    }
                }
                ReflectionManager.setBoundingBox(entity, entityBox);
            }
        }
    }

    public static HashSet<String> getAddedByPlugins() {
        return addedByPlugins;
    }

    public static PacketContainer[] getBedChunkPacket(Player player, Location newLoc, Location oldLoc) {
        int i = 0;
        PacketContainer[] packets = new PacketContainer[newLoc != null ? 2 + (oldLoc != null ? 1 : 0) : 1];
        for (Location loc : new Location[]{oldLoc, newLoc}) {
            if (loc == null) {
                continue;
            }
            try {
                int chunkX = (int) Math.floor(loc.getX() / 16D) - 17, chunkZ = (int) Math.floor(loc.getZ() / 16D) - 17;
                chunkX -= chunkX % 8;
                chunkZ -= chunkZ % 8;
                xChunk.set(bedChunk, chunkX);
                zChunk.set(bedChunk, chunkZ);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
            try {
                packets[i] = ProtocolLibrary.getProtocolManager()
                        .createPacketConstructor(PacketType.Play.Server.MAP_CHUNK, bedChunk, true, 0, 40)
                        .createPacket(bedChunk, true, 0, 48);
            } catch (IllegalArgumentException ex) {
                packets[i] = ProtocolLibrary.getProtocolManager()
                        .createPacketConstructor(PacketType.Play.Server.MAP_CHUNK, bedChunk, true, 0)
                        .createPacket(bedChunk, true, 0);
            }
            i++;
            // Make load packets
            if (oldLoc == null || i > 1) {
                packets[i] = ProtocolLibrary.getProtocolManager()
                        .createPacketConstructor(PacketType.Play.Server.MAP_CHUNK_BULK, Collections.singletonList(bedChunk))
                        .createPacket(Collections.singletonList(bedChunk));
                i++;
            }
        }
        return packets;
    }

    public static PacketContainer[] getBedPackets(Player player, Location loc, Location playerLocation, PlayerDisguise disguise) {
        Entity entity = disguise.getEntity();
        PacketContainer setBed = new PacketContainer(PacketType.Play.Server.BED);
        StructureModifier<Integer> bedInts = setBed.getIntegers();
        bedInts.write(0, entity.getEntityId());
        PlayerWatcher watcher = disguise.getWatcher();
        int chunkX = (int) Math.floor(playerLocation.getX() / 16D) - 17, chunkZ = (int) Math
                .floor(playerLocation.getZ() / 16D) - 17;
        chunkX -= chunkX % 8;
        chunkZ -= chunkZ % 8;
        bedInts.write(1, (chunkX * 16) + 1 + watcher.getSleepingDirection().getModX());
        bedInts.write(3, (chunkZ * 16) + 1 + watcher.getSleepingDirection().getModZ());
        PacketContainer teleport = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        StructureModifier<Integer> ints = teleport.getIntegers();
        ints.write(0, entity.getEntityId());
        ints.write(1, (int) Math.floor(loc.getX() * 32));
        ints.write(2, (int) Math.floor((PacketsManager.getYModifier(disguise.getEntity(), disguise) + loc.getY()) * 32));
        ints.write(3, (int) Math.floor(loc.getZ() * 32));
        return new PacketContainer[]{setBed, teleport};

    }

    public static PacketContainer getDestroyPacket(int... ids) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, ids);
        return destroyPacket;
    }

    public static TargetedDisguise getDisguise(Player observer, Entity entity) {
        UUID entityId = entity.getUniqueId();
        if (futureDisguises.containsKey(entity.getEntityId())) {
            for (TargetedDisguise disguise : futureDisguises.remove(entity.getEntityId())) {
                addDisguise(entityId, disguise);
            }
        }
        if (getDisguises().containsKey(entityId)) {
            for (TargetedDisguise disguise : getDisguises().get(entityId)) {
                if (disguise.canSee(observer)) {
                    return disguise;
                }
            }
        }
        return null;
    }

    public static HashMap<UUID, HashSet<TargetedDisguise>> getDisguises() {
        return disguisesInUse;
    }

    public static TargetedDisguise[] getDisguises(UUID entityId) {
        if (getDisguises().containsKey(entityId)) {
            HashSet<TargetedDisguise> disguises = getDisguises().get(entityId);
            return disguises.toArray(new TargetedDisguise[0]);
        }
        return new TargetedDisguise[0];
    }

    public static HashMap<Integer, HashSet<TargetedDisguise>> getFutureDisguises() {
        return futureDisguises;
    }

    public static WrappedGameProfile getGameProfile(String playerName) {
        return gameProfiles.get(playerName.toLowerCase());
    }

    public static HashMap<String, WrappedGameProfile> getGameProfiles() {
        return gameProfiles;
    }

    public static TargetedDisguise getMainDisguise(UUID entityId) {
        TargetedDisguise toReturn = null;
        if (getDisguises().containsKey(entityId)) {
            for (TargetedDisguise disguise : getDisguises().get(entityId)) {
                if (disguise.getDisguiseTarget() == TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS) {
                    return disguise;
                }
                toReturn = disguise;
            }
        }
        return toReturn;
    }

    public static ArrayList<Player> getPerverts(Disguise disguise) {
        ArrayList<Player> players = new ArrayList<>();
        try {
            Object entityTrackerEntry = ReflectionManager.getEntityTrackerEntry(disguise.getEntity());
            if (entityTrackerEntry != null) {
                Set<?> trackedPlayers = (Set<?>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers").get(entityTrackerEntry);
                trackedPlayers = new HashSet<>(trackedPlayers);  //Copy before iterating to prevent ConcurrentModificationException
                for (Object p : trackedPlayers) {
                    Player player = (Player) ReflectionManager.getBukkitEntity(p);
                    if (((TargetedDisguise) disguise).canSee(player)) {
                        players.add(player);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return players;
    }

    public static WrappedGameProfile getProfileFromMojang(final PlayerDisguise disguise) {
        final String nameToFetch = disguise.getSkin() != null ? disguise.getSkin() : disguise.getName();
        final boolean remove = getAddedByPlugins().contains(nameToFetch.toLowerCase());
        return getProfileFromMojang(nameToFetch, gameProfile -> {
            if (remove) {
                getAddedByPlugins().remove(nameToFetch.toLowerCase());
            }
            if (DisguiseAPI.isDisguiseInUse(disguise)
                    && (!gameProfile.getName().equals(
                    disguise.getSkin() != null ? disguise.getSkin() : disguise.getName())
                    || !gameProfile.getProperties().isEmpty())) {
                disguise.setGameProfile(gameProfile);
                DisguiseUtilities.refreshTrackers(disguise);
            }
        });
    }

    private static boolean isHashSet(Object obj) {
        if (obj instanceof HashSet) return true; //It's Spigot/Bukkit
        if (obj instanceof Set) return false; //It's PaperSpigot/SportsBukkit
        throw new IllegalArgumentException("Object passed was not either a hashset or set!");
    }

    public static WrappedGameProfile getProfileFromMojang(String playerName, LibsProfileLookup runnableIfCantReturn) {
        return getProfileFromMojang(playerName, (Object) runnableIfCantReturn);
    }

    private static WrappedGameProfile getProfileFromMojang(final String origName, final Object runnable) {
        final String playerName = origName.toLowerCase();
        if (gameProfiles.containsKey(playerName)) {
            if (gameProfiles.get(playerName) != null) {
                return gameProfiles.get(playerName);
            }
        } else if (Pattern.matches("([A-Za-z0-9_]){1,16}", origName)) {
            getAddedByPlugins().add(playerName);
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null) {
                WrappedGameProfile gameProfile = ReflectionManager.getGameProfile(player);
                if (!gameProfile.getProperties().isEmpty()) {
                    gameProfiles.put(playerName, gameProfile);
                    return gameProfile;
                }
            }
            // Add null so that if this is called again. I already know I'm doing something about it
            gameProfiles.put(playerName, null);
            Bukkit.getScheduler().runTaskAsynchronously(libsDisguises, () -> {
                try {
                    final WrappedGameProfile gameProfile = lookupGameProfile(origName);
                    Bukkit.getScheduler().runTask(libsDisguises, () -> {
                        if (!gameProfile.getProperties().isEmpty()) {
                            if (gameProfiles.containsKey(playerName) && gameProfiles.get(playerName) == null) {
                                gameProfiles.put(playerName, gameProfile);
                            }
                            if (runnables.containsKey(playerName)) {
                                for (Object obj : runnables.remove(playerName)) {
                                    if (obj instanceof Runnable) {
                                        ((Runnable) obj).run();
                                    } else if (obj instanceof LibsProfileLookup) {
                                        ((LibsProfileLookup) obj).onLookup(gameProfile);
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    if (gameProfiles.containsKey(playerName) && gameProfiles.get(playerName) == null) {
                        gameProfiles.remove(playerName);
                        getAddedByPlugins().remove(playerName);
                    }
                    System.out.print("[LibsDisguises] Error when fetching " + playerName + "'s uuid from mojang: "
                            + e.getMessage());
                }
            });
        } else {
            return ReflectionManager.getGameProfile(null, origName);
        }
        if (runnable != null) {
            if (!runnables.containsKey(playerName)) {
                runnables.put(playerName, new ArrayList<>());
            }
            runnables.get(playerName).add(runnable);
        }
        return ReflectionManager.getGameProfile(null, origName);
    }

    /**
     * Thread safe to use. This returns a GameProfile. And if its GameProfile doesn't have a skin blob. Then it does a lookup using schedulers. The runnable is run once the GameProfile has been successfully dealt with
     */
    public static WrappedGameProfile getProfileFromMojang(String playerName, Runnable runnableIfCantReturn) {
        return getProfileFromMojang(playerName, (Object) runnableIfCantReturn);
    }

    public static HashSet<UUID> getSelfDisguised() {
        return selfDisguised;
    }

    public static boolean hasGameProfile(String playerName) {
        return getGameProfile(playerName) != null;
    }

    public static void init(SurvivalGames disguises) {
        libsDisguises = disguises;
    }

    public static boolean isDisguiseInUse(Disguise disguise) {
        return disguise.getEntity() != null && getDisguises().containsKey(disguise.getEntity().getUniqueId())
                && getDisguises().get(disguise.getEntity().getUniqueId()).contains(disguise);
    }

    public static WrappedGameProfile lookupGameProfile(String playerName) {
        return ReflectionManager.getSkullBlob(ReflectionManager.grabProfileAddUUID(playerName));
    }

    public static List<WrappedWatchableObject> rebuildForVersion(Player player, FlagWatcher watcher,
                                                                 List<WrappedWatchableObject> list) {
        if (true) // Use for future protocol compatibility
        {
            return list;
        }
        ArrayList<WrappedWatchableObject> rebuiltList = new ArrayList<>();
        ArrayList<WrappedWatchableObject> backups = new ArrayList<>();
        for (WrappedWatchableObject obj : list) {
            if (obj.getValue().getClass().getName().startsWith("org.")) {
                backups.add(obj);
            }
        }
        Iterator<WrappedWatchableObject> itel = backups.iterator();
        while (itel.hasNext()) {
            int index = itel.next().getIndex();
            for (WrappedWatchableObject obj2 : rebuiltList) {
                if (index == obj2.getIndex()) {
                    itel.remove();
                    break;
                }
            }
        }
        rebuiltList.addAll(backups);
        return rebuiltList;
    }

    /**
     * Resends the entity to this specific player
     */
    public static void refreshTracker(final TargetedDisguise disguise, String player) {
        if (disguise.getEntity() != null && disguise.getEntity().isValid()) {
            try {
                PacketContainer destroyPacket = getDestroyPacket(disguise.getEntity().getEntityId());
                if (disguise.isDisguiseInUse() && disguise.getEntity() instanceof Player
                        && disguise.getEntity().getName().equalsIgnoreCase(player)) {
                    removeSelfDisguise((Player) disguise.getEntity());
                    if (disguise.isSelfDisguiseVisible()) {
                        selfDisguised.add(disguise.getEntity().getUniqueId());
                    }
                    sendSelfDisguise(disguise, destroyPacket);
                } else {
                    final Object entityTrackerEntry = ReflectionManager.getEntityTrackerEntry(disguise.getEntity());
                    if (entityTrackerEntry != null) {
                        Set<?> trackedPlayers = (Set<?>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers")
                                .get(entityTrackerEntry);
                        Method clear = ReflectionManager.getNmsMethod("EntityTrackerEntry", "clear",
                                ReflectionManager.getNmsClass("EntityPlayer"));
                        final Method updatePlayer = ReflectionManager.getNmsMethod("EntityTrackerEntry", "updatePlayer",
                                ReflectionManager.getNmsClass("EntityPlayer"));
                        trackedPlayers = new HashSet<>(trackedPlayers);  //Copy before iterating to prevent ConcurrentModificationException
                        for (final Object p : trackedPlayers) {
                            Player pl = (Player) ReflectionManager.getBukkitEntity(p);
                            if (player.equalsIgnoreCase((pl).getName())) {
                                clear.invoke(entityTrackerEntry, p);
                                ProtocolLibrary.getProtocolManager().sendServerPacket(pl, destroyPacket);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(libsDisguises, () -> {
                                    try {
                                        updatePlayer.invoke(entityTrackerEntry, p);
                                    } catch (Exception ex) {
                                        ex.printStackTrace(System.out);
                                    }
                                }, 2);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }
    
    private static void sendSelfDisguise(TargetedDisguise disguise, PacketContainer destroyPacket) throws InvocationTargetException {
        ProtocolLibrary.getProtocolManager().sendServerPacket((Player) disguise.getEntity(), destroyPacket);
        Bukkit.getScheduler().scheduleSyncDelayedTask(libsDisguises, () -> {
            try {
                DisguiseUtilities.sendSelfDisguise((Player) disguise.getEntity(), disguise);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }, 2);
    }
    
    /**
     * A convenience method for me to refresh trackers in other plugins
     */
    public static void refreshTrackers(Entity entity) {
        if (entity.isValid()) {
            try {
                PacketContainer destroyPacket = getDestroyPacket(entity.getEntityId());
                final Object entityTrackerEntry = ReflectionManager.getEntityTrackerEntry(entity);
                if (entityTrackerEntry != null) {
                    Set<?> trackedPlayers = (Set<?>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers").get(
                            entityTrackerEntry);
                    Method clear = ReflectionManager.getNmsMethod("EntityTrackerEntry", "clear",
                            ReflectionManager.getNmsClass("EntityPlayer"));
                    final Method updatePlayer = ReflectionManager.getNmsMethod("EntityTrackerEntry", "updatePlayer",
                            ReflectionManager.getNmsClass("EntityPlayer"));
                    trackedPlayers = new HashSet<>(trackedPlayers); //Copy before iterating to prevent ConcurrentModificationException
                    for (final Object p : trackedPlayers) {
                        Player player = (Player) ReflectionManager.getBukkitEntity(p);
                        if (player != entity) {
                            clear.invoke(entityTrackerEntry, p);
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyPacket);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(libsDisguises, () -> {
                                try {
                                    updatePlayer.invoke(entityTrackerEntry, p);
                                } catch (Exception ex) {
                                    ex.printStackTrace(System.out);
                                }
                            }, 2);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }

    /**
     * Resends the entity to all the watching players, which is where the magic begins
     */
    public static void refreshTrackers(final TargetedDisguise disguise) {
        if (disguise.getEntity().isValid()) {
            PacketContainer destroyPacket = getDestroyPacket(disguise.getEntity().getEntityId());
            try {
                if (selfDisguised.contains(disguise.getEntity().getUniqueId()) && disguise.isDisguiseInUse()) {
                    removeSelfDisguise((Player) disguise.getEntity());
                    selfDisguised.add(disguise.getEntity().getUniqueId());
                    sendSelfDisguise(disguise, destroyPacket);
                }
                final Object entityTrackerEntry = ReflectionManager.getEntityTrackerEntry(disguise.getEntity());
                if (entityTrackerEntry != null) {
                    Set<?> trackedPlayers = (Set<?>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers").get(entityTrackerEntry);
                    Method clear = ReflectionManager.getNmsMethod("EntityTrackerEntry", "clear",
                            ReflectionManager.getNmsClass("EntityPlayer"));
                    final Method updatePlayer = ReflectionManager.getNmsMethod("EntityTrackerEntry", "updatePlayer",
                            ReflectionManager.getNmsClass("EntityPlayer"));
                    trackedPlayers = new HashSet<>(trackedPlayers);  //Copy before iterating to prevent ConcurrentModificationException
                    for (final Object p : trackedPlayers) {
                        Player player = (Player) ReflectionManager.getBukkitEntity(p);
                        if (disguise.getEntity() != player && disguise.canSee(player)) {
                            clear.invoke(entityTrackerEntry, p);
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyPacket);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(libsDisguises, () -> {
                                try {
                                    updatePlayer.invoke(entityTrackerEntry, p);
                                } catch (Exception ex) {
                                    ex.printStackTrace(System.out);
                                }
                            }, 2);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }

    public static boolean removeDisguise(TargetedDisguise disguise) {
        UUID entityId = disguise.getEntity().getUniqueId();
        if (getDisguises().containsKey(entityId) && getDisguises().get(entityId).remove(disguise)) {
            if (getDisguises().get(entityId).isEmpty()) {
                getDisguises().remove(entityId);
            }
            if (disguise.getDisguiseTarget() == TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS && disguise.isModifyBoundingBox()) {
                doBoundingBox(disguise);
            }
            return true;
        }
        return false;
    }

    public static void removeGameProfile(String string) {
        gameProfiles.remove(string.toLowerCase());
    }

    public static void removeSelfDisguise(Player player) {
        if (selfDisguised.contains(player.getUniqueId())) {
            // Send a packet to destroy the fake entity
            PacketContainer packet = getDestroyPacket(DisguiseAPI.getSelfDisguiseId());
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
            // Remove the fake entity ID from the disguise bin
            selfDisguised.remove(player.getUniqueId());
            // Get the entity tracker
            try {
                Object entityTrackerEntry = ReflectionManager.getEntityTrackerEntry(player);
                if (entityTrackerEntry != null) {
                    Object trackedPlayersObj = ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers").get(entityTrackerEntry);
                    // If the tracker exists. Remove himself from his tracker
                    if (isHashSet(trackedPlayersObj)) {
                        ((Set<Object>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers").get(entityTrackerEntry)).remove(ReflectionManager.getNmsEntity(player));
                    } else {
                        ((Map<Object, Object>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayerMap").get(entityTrackerEntry)).remove(ReflectionManager.getNmsEntity(player));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
            // Resend entity metadata else he will be invisible to himself until its resent
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(
                        player,
                        ProtocolLibrary
                                .getProtocolManager()
                                .createPacketConstructor(PacketType.Play.Server.ENTITY_METADATA, player.getEntityId(),
                                        WrappedDataWatcher.getEntityWatcher(player), true)
                                .createPacket(player.getEntityId(), WrappedDataWatcher.getEntityWatcher(player), true));
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
            player.updateInventory();
        }
    }

    /**
     * Sends the self disguise to the player
     */
    public static void sendSelfDisguise(final Player player, final TargetedDisguise disguise) {
        try {
            if (!disguise.isDisguiseInUse() || !player.isValid() || !player.isOnline() || !disguise.isSelfDisguiseVisible()
                    || !disguise.canSee(player)) {
                return;
            }
            Object entityTrackerEntry = ReflectionManager.getEntityTrackerEntry(player);
            if (entityTrackerEntry == null) {
                // A check incase the tracker is null.
                // If it is, then this method will be run again in one tick. Which is when it should be constructed.
                // Else its going to run in a infinite loop hue hue hue..
                // At least until this disguise is discarded
                Bukkit.getScheduler().runTask(libsDisguises, () -> {
                    if (DisguiseAPI.getDisguise(player, player) == disguise) {
                        sendSelfDisguise(player, disguise);
                    }
                });
                return;
            }
            // Add himself to his own entity tracker
            Object trackedPlayersObj = ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers").get(entityTrackerEntry);
            //Check for code differences in PaperSpigot vs Spigot
            if (isHashSet(trackedPlayersObj)) {
                ((Set<Object>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayers").get(entityTrackerEntry)).add(ReflectionManager.getNmsEntity(player));
            } else {
                ((Map<Object, Object>) ReflectionManager.getNmsField("EntityTrackerEntry", "trackedPlayerMap").get(entityTrackerEntry)).put(ReflectionManager.getNmsEntity(player), true);
            }

            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            // Send the player a packet with himself being spawned
            manager.sendServerPacket(player, manager.createPacketConstructor(PacketType.Play.Server.NAMED_ENTITY_SPAWN, player)
                    .createPacket(player));
            WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(player);
            sendSelfPacket(
                    player,
                    manager.createPacketConstructor(PacketType.Play.Server.ENTITY_METADATA, player.getEntityId(), dataWatcher,
                            true).createPacket(player.getEntityId(), dataWatcher, true));

            boolean isMoving = false;
            try {
                Field field = ReflectionManager.getNmsClass("EntityTrackerEntry").getDeclaredField("isMoving");
                field.setAccessible(true);
                isMoving = field.getBoolean(entityTrackerEntry);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
            // Send the velocity packets
            if (isMoving) {
                Vector velocity = player.getVelocity();
                sendSelfPacket(
                        player,
                        manager.createPacketConstructor(PacketType.Play.Server.ENTITY_VELOCITY, player.getEntityId(),
                                velocity.getX(), velocity.getY(), velocity.getZ()).createPacket(player.getEntityId(),
                                velocity.getX(), velocity.getY(), velocity.getZ()));
            }

            // Why the hell would he even need this. Meh.
            if (player.getVehicle() != null && player.getEntityId() > player.getVehicle().getEntityId()) {
                sendSelfPacket(player,
                        manager.createPacketConstructor(PacketType.Play.Server.ATTACH_ENTITY, 0, player, player.getVehicle())
                                .createPacket(0, player, player.getVehicle()));
            } else if (player.getPassenger() != null && player.getEntityId() > player.getPassenger().getEntityId()) {
                sendSelfPacket(player,
                        manager.createPacketConstructor(PacketType.Play.Server.ATTACH_ENTITY, 0, player.getPassenger(), player)
                                .createPacket(0, player.getPassenger(), player));
            }

            // Resend the armor
            for (int i = 0; i < 5; i++) {
                ItemStack item;
                if (i == 0) {
                    item = player.getItemInHand();
                } else {
                    item = player.getInventory().getArmorContents()[i - 1];
                }

                if (item != null && item.getType() != Material.AIR) {
                    sendSelfPacket(
                            player,
                            manager.createPacketConstructor(PacketType.Play.Server.ENTITY_EQUIPMENT, player.getEntityId(), i,
                                    item).createPacket(player.getEntityId(), i, item));
                }
            }
            Location loc = player.getLocation();
            // If the disguised is sleeping for w/e reason
            if (player.isSleeping()) {
                sendSelfPacket(
                        player,
                        manager.createPacketConstructor(PacketType.Play.Server.BED, player, loc.getBlockX(), loc.getBlockY(),
                                loc.getBlockZ()).createPacket(player, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            }

            // Resend any active potion effects
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                Object mobEffect = ReflectionManager.createMobEffect(potionEffect);
                sendSelfPacket(player,
                        manager.createPacketConstructor(PacketType.Play.Server.ENTITY_EFFECT, player.getEntityId(), mobEffect)
                                .createPacket(player.getEntityId(), mobEffect));
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Method to send a packet to the self disguise, translate his entity ID to the fake id.
     */
    private static void sendSelfPacket(final Player player, PacketContainer packet) {
        PacketContainer[][] transformed = PacketsManager.transformPacket(packet, player, player);
        PacketContainer[] packets = transformed == null ? null : transformed[0];
        final PacketContainer[] delayed = transformed == null ? null : transformed[1];
        try {
            if (packets == null) {
                packets = new PacketContainer[]{packet};
            }
            for (PacketContainer p : packets) {
                p = p.deepClone();
                p.getIntegers().write(0, DisguiseAPI.getSelfDisguiseId());
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, p, false);
            }
            if (delayed != null && delayed.length > 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(libsDisguises, () -> {
                    try {
                        for (PacketContainer packet1 : delayed) {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet1, false);
                        }
                    } catch (InvocationTargetException e) {
                        e.printStackTrace(System.out);
                    }
                });
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void setupFakeDisguise(final Disguise disguise) {
        Entity e = disguise.getEntity();
        // If the disguises entity is null, or the disguised entity isn't a player return
        if (!(e instanceof Player player) || !getDisguises().containsKey(e.getUniqueId())
                || !getDisguises().get(e.getUniqueId()).contains(disguise)) {
            return;
        }
        // Check if he can even see this..
        if (!((TargetedDisguise) disguise).canSee(player)) {
            return;
        }
        // Remove the old disguise, else we have weird disguises around the place
        DisguiseUtilities.removeSelfDisguise(player);
        // If the disguised player can't see himself. Return
        if (!disguise.isSelfDisguiseVisible() || !PacketsManager.isViewDisguisesListenerEnabled() || player.getVehicle() != null) {
            return;
        }
        selfDisguised.add(player.getUniqueId());
        sendSelfDisguise(player, (TargetedDisguise) disguise);
        if (disguise.isHidingArmorFromSelf() || disguise.isHidingHeldItemFromSelf()) {
            if (PacketsManager.isInventoryListenerEnabled()) {
                player.updateInventory();
            }
        }
    }
}
