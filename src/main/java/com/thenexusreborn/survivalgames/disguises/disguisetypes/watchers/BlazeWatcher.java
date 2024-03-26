package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;

public class BlazeWatcher extends LivingWatcher {

    public BlazeWatcher(Disguise disguise) {
        super(disguise);
    }

    public boolean isBlazing() {
        return (Byte) getValue(16, (byte) 0) == 1;
    }

    public void setBlazing(boolean isBlazing) {
        setValue(16, (byte) (isBlazing ? 1 : 0));
        sendData(16);
    }

}
