package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;

public class ZombieWatcher extends LivingWatcher {

    public ZombieWatcher(Disguise disguise) {
        super(disguise);
    }

    public boolean isAdult() {
        return !isBaby();
    }

    public boolean isBaby() {
        return (Byte) getValue(12, (byte) 0) == 1;
    }

    public boolean isShaking() {
        return (Byte) getValue(14, (byte) 0) == 1;
    }

    public boolean isVillager() {
        return (Byte) getValue(13, (byte) 0) == 1;
    }

    public void setAdult() {
        setBaby(false);
    }

    public void setBaby() {
        setBaby(true);
    }

    public void setBaby(boolean baby) {
        setValue(12, (byte) (baby ? 1 : 0));
        sendData(12);
    }

    public void setShaking(boolean shaking) {
        setValue(14, (byte) (shaking ? 1 : 0));
        sendData(14);
    }

    public void setVillager(boolean villager) {
        setValue(13, (byte) (villager ? 1 : 0));
        sendData(13);
    }

}
