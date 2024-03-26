package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.RabbitType;

import java.util.Random;

public class RabbitWatcher extends AgeableWatcher {

    public RabbitWatcher(Disguise disguise) {
        super(disguise);
        setType(RabbitType.values()[new Random().nextInt(RabbitType.values().length)]);
    }

    public RabbitType getType() {
        return RabbitType.getType((Integer) getValue(18, 0));
    }

    public void setType(RabbitType type) {
        setValue(18, (byte) type.getTypeId());
        sendData(18);
    }

}
