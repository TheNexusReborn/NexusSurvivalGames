package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;

public class CreeperWatcher extends LivingWatcher {

    public CreeperWatcher(Disguise disguise) {
        super(disguise);
    }

    public boolean isIgnited() {
        return (Byte) getValue(18, (byte) 0) == 1;
    }

    public boolean isPowered() {
        return (Byte) getValue(17, (byte) 0) == 1;
    }

    public void setIgnited(boolean ignited) {
        setValue(18, (byte) (ignited ? 1 : 0));
        sendData(18);
    }

    public void setPowered(boolean powered) {
        setValue(17, (byte) (powered ? 1 : 0));
        sendData(17);
    }

}
