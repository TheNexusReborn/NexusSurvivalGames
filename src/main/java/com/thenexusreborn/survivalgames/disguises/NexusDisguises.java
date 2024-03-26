package com.thenexusreborn.survivalgames.disguises;

import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.DisguiseType;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.FlagWatcher;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers.*;
import com.thenexusreborn.survivalgames.disguises.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;

import java.lang.reflect.Field;

import static com.thenexusreborn.survivalgames.disguises.DisguiseConfig.isHidingArmorFromSelf;
import static com.thenexusreborn.survivalgames.disguises.DisguiseConfig.isHidingHeldItemFromSelf;

public class NexusDisguises {

    public static NexusDisguises instance;
    public static SurvivalGames plugin;
    private DisguiseListener listener;
    
    public void init(SurvivalGames plugin) {
        PacketsManager.init(this);
        DisguiseUtilities.init(plugin);
        NexusDisguises.plugin = plugin;

        PacketsManager.addPacketListeners();
        listener = new DisguiseListener(plugin);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        registerValues();
        instance = this;
        
        PacketsManager.setViewDisguisesListener(true);
        PacketsManager.setupMainPacketsListener();
        PacketsManager.setInventoryListenerEnabled(isHidingHeldItemFromSelf() || isHidingArmorFromSelf());
    }

    private void registerValues() {
        for (DisguiseType disguiseType : DisguiseType.values()) {
            if (disguiseType.getEntityType() == null) {
                continue;
            }
            Class<? extends FlagWatcher> watcherClass;
            try {
                watcherClass = switch (disguiseType) {
                    case MINECART_CHEST, MINECART_COMMAND, MINECART_FURNACE, MINECART_HOPPER, MINECART_MOB_SPAWNER, MINECART_TNT ->
                            MinecartWatcher.class;
                    case DONKEY, MULE, UNDEAD_HORSE, SKELETON_HORSE -> HorseWatcher.class;
                    case ZOMBIE_VILLAGER, PIG_ZOMBIE -> ZombieWatcher.class;
                    case MAGMA_CUBE -> SlimeWatcher.class;
                    case ELDER_GUARDIAN -> GuardianWatcher.class;
                    case ENDERMITE -> LivingWatcher.class;
                    default ->
                            (Class<? extends FlagWatcher>) Class.forName("me.libraryaddict.disguise.disguisetypes."
                                    + toReadable(disguiseType.name()) + "Watcher");
                };
            } catch (ClassNotFoundException ex) {
                Class<?> entityClass = disguiseType.getEntityType().getEntityClass();
                if (entityClass != null) {
                    if (Tameable.class.isAssignableFrom(entityClass)) {
                        watcherClass = TameableWatcher.class;
                    } else if (Ageable.class.isAssignableFrom(entityClass)) {
                        watcherClass = AgeableWatcher.class;
                    } else if (LivingEntity.class.isAssignableFrom(entityClass)) {
                        watcherClass = LivingWatcher.class;
                    } else {
                        watcherClass = FlagWatcher.class;
                    }
                } else {
                    watcherClass = FlagWatcher.class;
                }
            }
            disguiseType.setWatcherClass(watcherClass);
            if (DisguiseValues.getDisguiseValues(disguiseType) != null) {
                continue;
            }
            String nmsEntityName = toReadable(disguiseType.name());
            switch (disguiseType) {
                case WITHER_SKELETON, ZOMBIE_VILLAGER, DONKEY, MULE, UNDEAD_HORSE, SKELETON_HORSE -> {
                    continue;
                }
                case PRIMED_TNT -> nmsEntityName = "TNTPrimed";
                case MINECART_TNT -> nmsEntityName = "MinecartTNT";
                case MINECART -> nmsEntityName = "MinecartRideable";
                case FIREWORK -> nmsEntityName = "Fireworks";
                case SPLASH_POTION -> nmsEntityName = "Potion";
                case GIANT -> nmsEntityName = "GiantZombie";
                case DROPPED_ITEM -> nmsEntityName = "Item";
                case FIREBALL -> nmsEntityName = "LargeFireball";
                case LEASH_HITCH -> nmsEntityName = "Leash";
                case ELDER_GUARDIAN -> nmsEntityName = "Guardian";
                default -> {
                }
            }
            try {
                if (nmsEntityName.equalsIgnoreCase("Unknown")) {
                    DisguiseValues disguiseValues = new DisguiseValues(disguiseType, null, 0, 0);
                    disguiseValues.setAdultBox(new FakeBoundingBox(0, 0, 0));
                    DisguiseSound sound = DisguiseSound.getType(disguiseType.name());
                    if (sound != null) {
                        sound.setDamageAndIdleSoundVolume(1f);
                    }
                    continue;
                }
                Object nmsEntity = ReflectionManager.createEntityInstance(nmsEntityName);
                if (nmsEntity == null) {
                    continue;
                }
                Entity bukkitEntity = ReflectionManager.getBukkitEntity(nmsEntity);
                int entitySize = 0;
                for (Field field : ReflectionManager.getNmsClass("Entity").getFields()) {
                    if (field.getType().getName().equals("EnumEntitySize")) {
                        Enum<?> enumEntitySize = (Enum<?>) field.get(nmsEntity);
                        entitySize = enumEntitySize.ordinal();
                        break;
                    }
                }
                DisguiseValues disguiseValues = new DisguiseValues(disguiseType, nmsEntity.getClass(), entitySize,
                        bukkitEntity instanceof Damageable ? ((Damageable) bukkitEntity).getMaxHealth() : 0);
                for (WrappedWatchableObject watch : WrappedDataWatcher.getEntityWatcher(bukkitEntity).getWatchableObjects()) {
                    disguiseValues.setMetaValue(watch.getIndex(), watch.getValue());
                    // Uncomment when I need to find the new datawatcher values for a class..

//                    System.out.print("Disguise: " + disguiseType + ", ID: " + watch.getIndex() + ", Class: "
//                     + (watch.getValue() == null ? "null" : watch.getValue().getClass()) + ", Value: " + watch.getValue());
                }
                DisguiseSound sound = DisguiseSound.getType(disguiseType.name());
                if (sound != null) {
                    Float soundStrength = ReflectionManager.getSoundModifier(nmsEntity);
                    if (soundStrength != null) {
                        sound.setDamageAndIdleSoundVolume(soundStrength);
                    }
                }

                disguiseValues.setAdultBox(ReflectionManager.getBoundingBox(bukkitEntity));
                if (bukkitEntity instanceof Ageable) {
                    ((Ageable) bukkitEntity).setBaby();
                    disguiseValues.setBabyBox(ReflectionManager.getBoundingBox(bukkitEntity));
                } else if (bukkitEntity instanceof Zombie) {
                    ((Zombie) bukkitEntity).setBaby(true);
                    disguiseValues.setBabyBox(ReflectionManager.getBoundingBox(bukkitEntity));
                }
                disguiseValues.setEntitySize(ReflectionManager.getSize(bukkitEntity));
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException | FieldAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String toReadable(String string) {
        StringBuilder builder = new StringBuilder();
        for (String s : string.split("_")) {
            builder.append(s.charAt(0)).append(s.substring(1).toLowerCase());
        }
        return builder.toString();
    }

    public DisguiseListener getListener() {
        return listener;
    }
}
