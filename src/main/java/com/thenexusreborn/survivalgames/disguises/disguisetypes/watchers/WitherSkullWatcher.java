package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.FlagWatcher;

public class WitherSkullWatcher extends FlagWatcher {

    public WitherSkullWatcher(Disguise disguise) {
        super(disguise);
    }

    public boolean isBlue() {
        return (Byte) getValue(10, (byte) 0) == 1;
    }

    public void setBlue(boolean blue) {
        setValue(10, (byte) (blue ? 1 : 0));
        sendData(10);
    }

}
