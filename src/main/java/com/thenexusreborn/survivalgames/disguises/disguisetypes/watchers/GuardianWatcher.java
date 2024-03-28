package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;

public class GuardianWatcher extends LivingWatcher {

    public GuardianWatcher(Disguise disguise) {
        super(disguise);
    }

    public boolean isBeam() {
        return (Integer) getValue(17, 0) == 1;
    }

    public void setBeam(boolean isBeaming) {
        setValue(17, isBeaming ? 1 : 0);
        sendData(17);
    }

}
