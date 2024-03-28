package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.FlagWatcher;

public class ArrowWatcher extends FlagWatcher {

    public ArrowWatcher(Disguise disguise) {
        super(disguise);
    }

    public boolean isCritical() {
        return (Byte) getValue(16, (byte) 0) == 1;
    }

    public void setCritical(boolean critical) {
        setValue(16, (byte) (critical ? 1 : 0));
        sendData(16);
    }

}
