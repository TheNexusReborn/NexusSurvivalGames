package com.thenexusreborn.survivalgames.disguises.disguisetypes;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.thenexusreborn.survivalgames.disguises.DisguiseAPI;
import com.thenexusreborn.survivalgames.disguises.DisguiseConfig;
import com.thenexusreborn.survivalgames.disguises.NexusDisguises;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers.AgeableWatcher;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers.BatWatcher;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers.HorseWatcher;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers.ZombieWatcher;
import com.thenexusreborn.survivalgames.disguises.utilities.DisguiseUtilities;
import com.thenexusreborn.survivalgames.disguises.utilities.DisguiseValues;
import com.thenexusreborn.survivalgames.disguises.utilities.PacketsManager;
import com.thenexusreborn.survivalgames.disguises.utilities.ReflectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

@SuppressWarnings({"SuspiciousMethodCalls", "ExtractMethodRecommender"})
public abstract class Disguise {
    
    private boolean disguiseInUse;
    private DisguiseType disguiseType;
    private Entity entity;
    private boolean hearSelfDisguise = DisguiseConfig.isSelfDisguisesSoundsReplaced();
    private boolean hideArmorFromSelf = DisguiseConfig.isHidingArmorFromSelf();
    private boolean hideHeldItemFromSelf = DisguiseConfig.isHidingHeldItemFromSelf();
    private boolean keepDisguiseEntityDespawn = DisguiseConfig.isKeepDisguiseOnEntityDespawn();
    private boolean keepDisguisePlayerDeath = DisguiseConfig.isKeepDisguiseOnPlayerDeath();
    private boolean keepDisguisePlayerLogout = DisguiseConfig.isKeepDisguiseOnPlayerLogout();
    private boolean modifyBoundingBox = DisguiseConfig.isModifyBoundingBox();
    private boolean replaceSounds = DisguiseConfig.isSoundEnabled();
    private BukkitTask task = null;
    private Runnable velocityRunnable;
    private boolean velocitySent = DisguiseConfig.isVelocitySent();
    private boolean viewSelfDisguise = DisguiseConfig.isViewDisguises();
    private FlagWatcher watcher;
    private boolean showName = false;
    
    private static List<UUID> viewSelf = new ArrayList<>();
    
    @Override
    public abstract Disguise clone();
    
    @SuppressWarnings("ExtractMethodRecommender")
    protected void createDisguise(DisguiseType newType) {
        if (getWatcher() != null) {
            return;
        }
        if (newType.getEntityType() == null) {
            throw new RuntimeException(
                    "DisguiseType "
                            + newType
                            + " was used in a futile attempt to construct a disguise, but this version of craftbukkit does not have that entity");
        }
        // Set the disguise type
        disguiseType = newType;
        // Get if they are a adult now..
        boolean isAdult = true;
        if (isMobDisguise()) {
            isAdult = ((MobDisguise) this).isAdult();
        }
        try {
            // Construct the FlagWatcher from the stored class
            setWatcher((FlagWatcher) getType().getWatcherClass().getConstructor(Disguise.class).newInstance(this));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        // Set the disguise if its a baby or not
        if (!isAdult) {
            if (getWatcher() instanceof AgeableWatcher) {
                ((AgeableWatcher) getWatcher()).setBaby(true);
            } else if (getWatcher() instanceof ZombieWatcher) {
                ((ZombieWatcher) getWatcher()).setBaby(true);
            }
        }
        // If the disguise type is a wither, set the flagwatcher value for the skeleton to a wither skeleton
        if (getType() == DisguiseType.WITHER_SKELETON) {
            getWatcher().setValue(13, (byte) 1);
        } // Else if its a zombie, but the disguise type is a zombie villager. Set the value.
        else if (getType() == DisguiseType.ZOMBIE_VILLAGER) {
            getWatcher().setValue(13, (byte) 1);
        } else if (getType() == DisguiseType.ELDER_GUARDIAN) {
            getWatcher().setValue(16, 4);
        } // Else if its a horse. Set the horse watcher type
        else if (getWatcher() instanceof HorseWatcher) {
            try {
                // Don't mess with this because Varient is something like ZombieHorse and so on.
                // Not something that a watcher needs to access.
                Variant horseType = Variant.valueOf(getType().name());
                getWatcher().setValue(19, (byte) horseType.ordinal());
            } catch (Exception ex) {
                // Ok.. So it aint a horse
            }
        }
        boolean alwaysSendVelocity = switch (getType()) {
            case EGG, ENDER_PEARL, BAT, EXPERIENCE_ORB, FIREBALL, SMALL_FIREBALL, SNOWBALL, SPLASH_POTION, THROWN_EXP_BOTTLE, WITHER_SKULL, FIREWORK ->
                    true;
            default -> false;
        };
        double velocitySpeed = 0.0005;
        switch (getType()) {
            case FIREWORK -> velocitySpeed = -0.040;
            case WITHER_SKULL -> velocitySpeed = 0.000001D;
            case ARROW, BOAT, ENDER_CRYSTAL, ENDER_DRAGON, GHAST, ITEM_FRAME, MINECART, MINECART_CHEST, MINECART_COMMAND, MINECART_FURNACE, MINECART_HOPPER, MINECART_MOB_SPAWNER, MINECART_TNT, PAINTING, PLAYER, SQUID ->
                    velocitySpeed = 0;
            case DROPPED_ITEM, PRIMED_TNT, WITHER, FALLING_BLOCK -> velocitySpeed = 0.04;
            case EXPERIENCE_ORB -> velocitySpeed = 0.0221;
            case SPIDER, BAT, CAVE_SPIDER -> velocitySpeed = 0.004;
            default -> {
            }
        }
        final double vectorY = velocitySpeed;
        final TargetedDisguise disguise = (TargetedDisguise) this;
        // A scheduler to clean up any unused disguises.
        velocityRunnable = new Runnable() {
            private int blockX, blockY, blockZ, facing;
            private int deadTicks = 0;
            private int refreshDisguise = 0;
            
            @Override
            public void run() {
                // If entity is no longer valid. Remove it.
                if (!getEntity().isValid()) {
                    // If it has been dead for 30+ ticks
                    // This is to ensure that this disguise isn't removed while clients think its the real entity
                    // The delay is because if it sends the destroy entity packets straight away, then it means no death animation
                    // This is probably still a problem for wither and enderdragon deaths.
                    if (deadTicks++ > (getType() == DisguiseType.ENDER_DRAGON ? 200 : 20)) {
                        deadTicks = 0;
                        if (isRemoveDisguiseOnDeath()) {
                            removeDisguise();
                        } else {
                            entity = null;
                            watcher = getWatcher().clone(disguise);
                            task.cancel();
                            task = null;
                        }
                    }
                } else {
                    deadTicks = 0;
                    // If the disguise type is tnt, we need to resend the entity packet else it will turn invisible
                    if (getType() == DisguiseType.PRIMED_TNT || getType() == DisguiseType.FIREWORK) {
                        refreshDisguise++;
                        if (refreshDisguise % 40 == 0) {
                            refreshDisguise = 0;
                            DisguiseUtilities.refreshTrackers(disguise);
                        }
                    }
                    if (getType() == DisguiseType.ITEM_FRAME) {
                        Location loc = getEntity().getLocation();
                        int newFacing = (((int) loc.getYaw() + 720 + 45) / 90) % 4;
                        if (loc.getBlockX() != blockX || loc.getBlockY() != blockY || loc.getBlockZ() != blockZ
                                || newFacing != facing) {
                            blockX = loc.getBlockX();
                            blockY = loc.getBlockY();
                            blockZ = loc.getBlockZ();
                            facing = newFacing;
                            DisguiseUtilities.refreshTrackers(disguise);
                        }
                    }
                    if (isModifyBoundingBox()) {
                        DisguiseUtilities.doBoundingBox(disguise);
                    }
                    if (getType() == DisguiseType.BAT && !((BatWatcher) getWatcher()).isFlying()) {
                        return;
                    }
                    // If the vectorY isn't 0. Cos if it is. Then it doesn't want to send any vectors.
                    // If this disguise has velocity sending enabled and the entity is flying.
                    if (isVelocitySent() && vectorY != 0 && (alwaysSendVelocity || !getEntity().isOnGround())) {
                        Vector vector = getEntity().getVelocity();
                        // If the entity doesn't have velocity changes already - You know. I really can't wrap my head about the
                        // if statement.
                        // But it doesn't seem to do anything wrong..
                        if (vector.getY() != 0 && !(vector.getY() < 0 && alwaysSendVelocity && getEntity().isOnGround())) {
                            return;
                        }
                        // If disguise isn't a experience orb, or the entity isn't standing on the ground
                        if (getType() != DisguiseType.EXPERIENCE_ORB || !getEntity().isOnGround()) {
                            PacketContainer lookPacket = null;
                            if (getType() == DisguiseType.WITHER_SKULL && DisguiseConfig.isWitherSkullPacketsEnabled()) {
                                lookPacket = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
                                StructureModifier<Object> mods = lookPacket.getModifier();
                                lookPacket.getIntegers().write(0, getEntity().getEntityId());
                                Location loc = getEntity().getLocation();
                                mods.write(
                                        4,
                                        PacketsManager.getYaw(getType(), getEntity().getType(),
                                                (byte) Math.floor(loc.getYaw() * 256.0F / 360.0F)));
                                mods.write(5, PacketsManager.getPitch(getType(), DisguiseType.getType(getEntity().getType()),
                                        (byte) Math.floor(loc.getPitch() * 256.0F / 360.0F)));
                                if (isSelfDisguiseVisible() && getEntity() instanceof Player) {
                                    PacketContainer selfLookPacket = lookPacket.shallowClone();
                                    selfLookPacket.getIntegers().write(0, DisguiseAPI.getSelfDisguiseId());
                                    try {
                                        ProtocolLibrary.getProtocolManager().sendServerPacket((Player) getEntity(),
                                                selfLookPacket, false);
                                    } catch (Exception e) {
                                        e.printStackTrace(System.out);
                                    }
                                }
                            }
                            try {
                                PacketContainer velocityPacket = new PacketContainer(PacketType.Play.Server.ENTITY_VELOCITY);
                                StructureModifier<Integer> mods = velocityPacket.getIntegers();
                                mods.write(1, (int) (vector.getX() * 8000));
                                mods.write(3, (int) (vector.getZ() * 8000));
                                for (Player player : DisguiseUtilities.getPerverts(disguise)) {
                                    if (getEntity() == player) {
                                        if (!isSelfDisguiseVisible()) {
                                            continue;
                                        }
                                        mods.write(0, DisguiseAPI.getSelfDisguiseId());
                                    } else {
                                        mods.write(0, getEntity().getEntityId());
                                    }
                                    mods.write(2, (int) (8000D * (vectorY * ReflectionManager.getPing(player)) * 0.069D));
                                    if (lookPacket != null && player != getEntity()) {
                                        ProtocolLibrary.getProtocolManager().sendServerPacket(player, lookPacket, false);
                                    }
                                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, velocityPacket.shallowClone(),
                                            false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace(System.out);
                            }
                        }
                        // If we need to send a packet to update the exp position as it likes to gravitate client sided to
                        // players.
                    }
                    if (getType() == DisguiseType.EXPERIENCE_ORB) {
                        PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
                        packet.getIntegers().write(0, getEntity().getEntityId());
                        try {
                            for (Player player : DisguiseUtilities.getPerverts(disguise)) {
                                if (getEntity() != player) {
                                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, false);
                                } else if (isSelfDisguiseVisible()) {
                                    PacketContainer selfPacket = packet.shallowClone();
                                    selfPacket.getModifier().write(0, DisguiseAPI.getSelfDisguiseId());
                                    try {
                                        ProtocolLibrary.getProtocolManager().sendServerPacket((Player) getEntity(), selfPacket,
                                                false);
                                    } catch (Exception e) {
                                        e.printStackTrace(System.out);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace(System.out);
                        }
                    }
                }
            }
        };
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public DisguiseType getType() {
        return disguiseType;
    }
    
    public FlagWatcher getWatcher() {
        return watcher;
    }
    
    public boolean isDisguiseInUse() {
        return disguiseInUse;
    }
    
    public boolean isHidingArmorFromSelf() {
        return hideArmorFromSelf;
    }
    
    public boolean isHidingHeldItemFromSelf() {
        return hideHeldItemFromSelf;
    }
    
    public boolean isKeepDisguiseOnEntityDespawn() {
        return this.keepDisguiseEntityDespawn;
    }
    
    public boolean isKeepDisguiseOnPlayerDeath() {
        return this.keepDisguisePlayerDeath;
    }
    
    public boolean isKeepDisguiseOnPlayerLogout() {
        return this.keepDisguisePlayerLogout;
    }
    
    public boolean isMiscDisguise() {
        return false;
    }
    
    public boolean isMobDisguise() {
        return false;
    }
    
    public boolean isModifyBoundingBox() {
        return modifyBoundingBox;
    }
    
    public boolean isPlayerDisguise() {
        return false;
    }
    
    public boolean isRemoveDisguiseOnDeath() {
        if (getEntity() == null) {
            return true;
        }
        return getEntity() instanceof Player
                ? (!((Player) getEntity()).isOnline() ? !isKeepDisguiseOnPlayerLogout() : !isKeepDisguiseOnPlayerDeath())
                : (!isKeepDisguiseOnEntityDespawn() || getEntity().isDead());
    }
    
    public boolean isSelfDisguiseSoundsReplaced() {
        return hearSelfDisguise;
    }
    
    public boolean isSelfDisguiseVisible() {
        return viewSelfDisguise;
    }
    
    public boolean isSoundsReplaced() {
        return replaceSounds;
    }
    
    public boolean isVelocitySent() {
        return velocitySent;
    }
    
    public boolean isShowName() {
        return showName;
    }
    
    public boolean removeDisguise() {
        if (disguiseInUse) {
            disguiseInUse = false;
            if (task != null) {
                task.cancel();
                task = null;
            }
            HashMap<UUID, HashSet<TargetedDisguise>> disguises = DisguiseUtilities.getDisguises();
            // If this disguise has a entity set
            if (getEntity() != null) {
                // If this disguise is active
                // Remove the disguise from the current disguises.
                if (DisguiseUtilities.removeDisguise((TargetedDisguise) this)) {
                    if (getEntity() instanceof Player) {
                        DisguiseUtilities.removeSelfDisguise((Player) getEntity());
                    }
                    
                    // Better refresh the entity to undisguise it
                    if (getEntity().isValid()) {
                        DisguiseUtilities.refreshTrackers((TargetedDisguise) this);
                    } else {
                        DisguiseUtilities.destroyEntity((TargetedDisguise) this);
                    }
                }
            } else {
                // Loop through the disguises because it could be used with a unknown entity id.
                HashMap<Integer, HashSet<TargetedDisguise>> future = DisguiseUtilities.getFutureDisguises();
                DisguiseUtilities.getFutureDisguises().keySet().removeIf(id -> future.get(id).remove(this) && future.get(id).isEmpty());
            }
            
            if (isPlayerDisguise()) {
                String name = ((PlayerDisguise) this).getName();
                if (!DisguiseUtilities.getAddedByPlugins().contains(name.toLowerCase())) {
                    for (HashSet<TargetedDisguise> disguise : disguises.values()) {
                        for (Disguise d : disguise) {
                            if (d.isPlayerDisguise() && ((PlayerDisguise) d).getName().equals(name)) {
                                return true;
                            }
                        }
                    }
                    DisguiseUtilities.getGameProfiles().remove(name.toLowerCase());
                }
            }
            return true;
        }
        return false;
    }
    
    public Disguise setEntity(Entity entity) {
        if (this.getEntity() != null) {
            if (getEntity() == entity) {
                return this;
            }
            throw new RuntimeException("This disguise is already in use! Try .clone()");
        }
        if (isMiscDisguise() && !DisguiseConfig.isMiscDisguisesForLivingEnabled() && entity instanceof LivingEntity) {
            throw new RuntimeException(
                    "Cannot disguise a living entity with a misc disguise. Renable MiscDisguisesForLiving in the config to do this");
        }
        this.entity = entity;
        setupWatcher();
        return this;
    }
    
    public Disguise setShowName(boolean showName) {
        this.showName = showName;
        return this;
    }
    
    public Disguise setHearSelfDisguise(boolean hearSelfDisguise) {
        this.hearSelfDisguise = hearSelfDisguise;
        return this;
    }
    
    public Disguise setHideArmorFromSelf(boolean hideArmor) {
        this.hideArmorFromSelf = hideArmor;
        if (getEntity() instanceof Player) {
            ((Player) getEntity()).updateInventory();
        }
        return this;
    }
    
    public Disguise setHideHeldItemFromSelf(boolean hideHeldItem) {
        this.hideHeldItemFromSelf = hideHeldItem;
        if (getEntity() instanceof Player) {
            ((Player) getEntity()).updateInventory();
        }
        return this;
    }
    
    public Disguise setKeepDisguiseOnEntityDespawn(boolean keepDisguise) {
        this.keepDisguiseEntityDespawn = keepDisguise;
        return this;
    }
    
    public Disguise setKeepDisguiseOnPlayerDeath(boolean keepDisguise) {
        this.keepDisguisePlayerDeath = keepDisguise;
        return this;
    }
    
    public Disguise setKeepDisguiseOnPlayerLogout(boolean keepDisguise) {
        this.keepDisguisePlayerLogout = keepDisguise;
        return this;
    }
    
    public Disguise setModifyBoundingBox(boolean modifyBox) {
        if (((TargetedDisguise) this).getDisguiseTarget() != TargetedDisguise.TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS) {
            throw new RuntimeException(
                    "Cannot modify the bounding box of a disguise which is not TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS");
        }
        if (isModifyBoundingBox() != modifyBox) {
            this.modifyBoundingBox = modifyBox;
            if (DisguiseUtilities.isDisguiseInUse(this)) {
                DisguiseUtilities.doBoundingBox((TargetedDisguise) this);
            }
        }
        return this;
    }
    
    public Disguise setReplaceSounds(boolean areSoundsReplaced) {
        replaceSounds = areSoundsReplaced;
        return this;
    }
    
    private void setupWatcher() {
        HashMap<Integer, Object> disguiseValues = DisguiseValues.getMetaValues(getType());
        HashMap<Integer, Object> entityValues = DisguiseValues.getMetaValues(DisguiseType.getType(getEntity().getType()));
        // Start from 2 as they ALL share 0 and 1
        for (int dataNo = 0; dataNo <= 31; dataNo++) {
            // STEP 1. Find out if the watcher has set data on it.
            // If the watcher already set a metadata on this
            if (getWatcher().hasValue(dataNo)) {
                // Better check that the value is stable.
                // To check this, I'm going to check if there exists a default value
                // Then I'm going to check if the watcher value is the same as the default value.
                if (disguiseValues.containsKey(dataNo)) {
                    // Now check if they are the same class, or both null.
                    if (disguiseValues.get(dataNo) == null || getWatcher().getValue(dataNo, null) == null) {
                        if (disguiseValues.get(dataNo) == null && getWatcher().getValue(dataNo, null) == null) {
                            // They are both null. Idk what this means really.
                            continue;
                        }
                    } else if (getWatcher().getValue(dataNo, null).getClass() == disguiseValues.get(dataNo).getClass()) {
                        // The classes are the same. The client "shouldn't" crash.
                        continue;
                    }
                }
            }
            // STEP 2. As the watcher has not set data on it, check if I need to set the default data.
            // If neither of them touch it
            if (!entityValues.containsKey(dataNo) && !disguiseValues.containsKey(dataNo)) {
                continue;
            }
            // If the disguise has this, but not the entity. Then better set it!
            if (!entityValues.containsKey(dataNo) && disguiseValues.containsKey(dataNo)) {
                getWatcher().setBackupValue(dataNo, disguiseValues.get(dataNo));
                continue;
            }
            // Else if the disguise doesn't have it. But the entity does. Better remove it!
            if (entityValues.containsKey(dataNo) && !disguiseValues.containsKey(dataNo)) {
                getWatcher().setBackupValue(dataNo, null);
                continue;
            }
            Object eObj = entityValues.get(dataNo);
            Object dObj = disguiseValues.get(dataNo);
            if (eObj == null || dObj == null) {
                if (eObj != null || dObj != null) {
                    getWatcher().setBackupValue(dataNo, dObj);
                }
                continue;
            }
            if (eObj.getClass() != dObj.getClass()) {
                getWatcher().setBackupValue(dataNo, dObj);
                continue;
            }
            
            // Since they both share it. With the same classes. Time to check if its from something they extend.
            // Better make this clear before I compare the values because some default values are different!
            // Entity is 0 & 1 - But we aint gonna be checking that
            // EntityAgeable is 16
            // EntityInsentient is 10 & 11
            // EntityZombie is 12 & 13 & 14 - But it overrides other values and another check already does this.
            // EntityLiving is 6 & 7 & 8 & 9
            // Lets use switch
            Class<?> baseClass = null;
            switch (dataNo) {
                case 6, 7, 8, 9 -> baseClass = ReflectionManager.getNmsClass("EntityLiving");
                case 10, 11 -> baseClass = ReflectionManager.getNmsClass("EntityInsentient");
                case 16 -> baseClass = ReflectionManager.getNmsClass("EntityAgeable");
                default -> {
                }
            }
            Class<?> nmsEntityClass = ReflectionManager.getNmsEntity(getEntity()).getClass();
            Class<?> nmsDisguiseClass = DisguiseValues.getNmsEntityClass(getType());
            if (nmsDisguiseClass != null) {
                // If they both extend the same base class. They OBVIOUSLY share the same datavalue. Right..?
                if (baseClass != null && baseClass.isAssignableFrom(nmsDisguiseClass)
                        && baseClass.isAssignableFrom(nmsEntityClass)) {
                    continue;
                }
                
                // So they don't extend a basic class.
                // Maybe if I check that they extend each other..
                // Seeing as I only store the finished forms of entitys. This should raise no problems and allow for more shared
                // datawatchers.
                if (nmsEntityClass.isAssignableFrom(nmsDisguiseClass) || nmsDisguiseClass.isAssignableFrom(nmsEntityClass)) {
                    continue;
                }
            }
            // Well I can't find a reason I should leave it alone. They will probably conflict.
            // Time to set the value to the disguises value so no conflicts!
            getWatcher().setBackupValue(dataNo, disguiseValues.get(dataNo));
        }
    }
    
    public Disguise setVelocitySent(boolean sendVelocity) {
        this.velocitySent = sendVelocity;
        return this;
    }
    
    public Disguise setViewSelfDisguise(boolean viewSelfDisguise) {
        if (isSelfDisguiseVisible() != viewSelfDisguise) {
            this.viewSelfDisguise = viewSelfDisguise;
            if (getEntity() != null && getEntity() instanceof Player) {
                if (DisguiseAPI.getDisguise((Player) getEntity(), getEntity()) == this) {
                    if (isSelfDisguiseVisible()) {
                        DisguiseUtilities.setupFakeDisguise(this);
                    } else {
                        DisguiseUtilities.removeSelfDisguise((Player) getEntity());
                    }
                }
            }
        }
        return this;
    }
    
    public Disguise setWatcher(FlagWatcher newWatcher) {
        if (!getType().getWatcherClass().isInstance(newWatcher)) {
            throw new IllegalArgumentException(newWatcher.getClass().getSimpleName() + " is not a instance of "
                    + getType().getWatcherClass().getSimpleName() + " for DisguiseType " + getType().name());
        }
        watcher = newWatcher;
        if (getEntity() != null) {
            setupWatcher();
        }
        return this;
    }
    
    public boolean startDisguise() {
        if (!isDisguiseInUse()) {
            if (getEntity() == null) {
                throw new RuntimeException("No entity is assigned to this disguise!");
            }
            // Fire a disguise event
            disguiseInUse = true;
            task = Bukkit.getScheduler().runTaskTimer(NexusDisguises.plugin, velocityRunnable, 1, 1);
            // Stick the disguise in the disguises bin
            DisguiseUtilities.addDisguise(entity.getUniqueId(), (TargetedDisguise) this);
            if (isSelfDisguiseVisible() && getEntity() instanceof Player) {
                DisguiseUtilities.removeSelfDisguise((Player) getEntity());
            }
            // Resend the disguised entity's packet
            DisguiseUtilities.refreshTrackers((TargetedDisguise) this);
            // If he is a player, then self disguise himself
            Bukkit.getScheduler().scheduleSyncDelayedTask(NexusDisguises.plugin, () -> DisguiseUtilities.setupFakeDisguise(Disguise.this), 2);
            return true;
        }
        return false;
    }
    
    public boolean stopDisguise() {
        return removeDisguise();
    }
    
    public static List<UUID> getViewSelf() {
        return viewSelf;
    }
}
