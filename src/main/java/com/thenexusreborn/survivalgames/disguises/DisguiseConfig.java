package com.thenexusreborn.survivalgames.disguises;

import com.thenexusreborn.survivalgames.disguises.utilities.PacketsManager;

public class DisguiseConfig {

    private static final boolean animationEnabled = true;
    private static final boolean bedEnabled = true;
    private static final boolean collectEnabled = true;
    private static final boolean colorizeSheep = false;
    private static final boolean colorizeWolf = false;
    private static final boolean entityAnimationsAdded = true;
    private static final boolean entityStatusEnabled = true;
    private static final boolean equipmentEnabled = true;
    private static final boolean hearSelfDisguise = true;
    private static final boolean viewSelfDisguise = false;
    private static final boolean hidingArmor = true;
    private static final boolean hidingHeldItem = false;
    private static final boolean keepDisguiseEntityDespawn = false;
    private static final boolean keepDisguisePlayerDeath = false;
    private static final boolean keepDisguisePlayerLogout = false;
    private static final boolean maxHealthIsDisguisedEntity = true;
    private static final boolean miscDisguisesForLivingEnabled = true;
    private static final boolean modifyBoundingBox = false;
    private static final boolean movementEnabled = true;
    private static final boolean sendsEntityMetadata = true;
    private static final boolean sendVelocity = true;
    private static final boolean showNameAboveHead = false;
    private static final boolean showNameAboveHeadAlwaysVisible = false;
    private static final boolean targetDisguises = true;
    private static final boolean undisguiseSwitchWorlds = true;
    private static final boolean witherSkullEnabled = true;

    public static boolean isAnimationPacketsEnabled() {
        return animationEnabled;
    }

    public static boolean isBedPacketsEnabled() {
        return bedEnabled;
    }

    public static boolean isCollectPacketsEnabled() {
        return collectEnabled;
    }

    public static boolean isEntityAnimationsAdded() {
        return entityAnimationsAdded;
    }

    public static boolean isEntityStatusPacketsEnabled() {
        return entityStatusEnabled;
    }

    public static boolean isEquipmentPacketsEnabled() {
        return equipmentEnabled;
    }

    public static boolean isHidingArmorFromSelf() {
        return hidingArmor;
    }

    public static boolean isHidingHeldItemFromSelf() {
        return hidingHeldItem;
    }

    public static boolean isKeepDisguiseOnEntityDespawn() {
        return keepDisguiseEntityDespawn;
    }

    public static boolean isKeepDisguiseOnPlayerDeath() {
        return keepDisguisePlayerDeath;
    }

    public static boolean isKeepDisguiseOnPlayerLogout() {
        return keepDisguisePlayerLogout;
    }

    public static boolean isMaxHealthDeterminedByDisguisedEntity() {
        return maxHealthIsDisguisedEntity;
    }

    public static boolean isMetadataPacketsEnabled() {
        return sendsEntityMetadata;
    }

    public static boolean isMiscDisguisesForLivingEnabled() {
        return miscDisguisesForLivingEnabled;
    }

    public static boolean isModifyBoundingBox() {
        return modifyBoundingBox;
    }

    public static boolean isMonstersIgnoreDisguises() {
        return targetDisguises;
    }

    public static boolean isMovementPacketsEnabled() {
        return movementEnabled;
    }

    public static boolean isNameAboveHeadAlwaysVisible() {
        return showNameAboveHeadAlwaysVisible;
    }

    public static boolean isNameOfPlayerShownAboveDisguise() {
        return showNameAboveHead;
    }

    public static boolean isSelfDisguisesSoundsReplaced() {
        return hearSelfDisguise;
    }

    public static boolean isSheepDyeable() {
        return colorizeSheep;
    }

    public static boolean isSoundEnabled() {
        return PacketsManager.isHearDisguisesEnabled();
    }

    public static boolean isUndisguiseOnWorldChange() {
        return undisguiseSwitchWorlds;
    }

    public static boolean isVelocitySent() {
        return sendVelocity;
    }

    public static boolean isViewDisguises() {
        return viewSelfDisguise;
    }

    public static boolean isWitherSkullPacketsEnabled() {
        return witherSkullEnabled;
    }

    public static boolean isWolfDyeable() {
        return colorizeWolf;
    }

    private DisguiseConfig() {
    }
}
