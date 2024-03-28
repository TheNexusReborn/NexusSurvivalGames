package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;

import java.util.Random;

public class SlimeWatcher extends LivingWatcher {

    public SlimeWatcher(Disguise disguise) {
        super(disguise);
        setSize(new Random().nextInt(4) + 1);
    }

    public int getSize() {
        return (Byte) getValue(16, (byte) 1);
    }

    public void setSize(int size) {
        if (size <= 0 || size >= 128) {
            size = 1;
        }
        setValue(16, (byte) size);
        sendData(16);
    }

}
