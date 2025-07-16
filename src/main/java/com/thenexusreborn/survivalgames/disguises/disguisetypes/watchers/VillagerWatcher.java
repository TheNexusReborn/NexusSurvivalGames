package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;
import org.bukkit.entity.Villager.Profession;

import java.util.Random;

public class VillagerWatcher extends AgeableWatcher {

    public VillagerWatcher(Disguise disguise) {
        super(disguise);
        setProfession(Profession.values()[new Random().nextInt(Profession.values().length)]);
    }

    public Profession getProfession() {
        return Profession.values()[(Integer) getValue(16, 0)];
    }

    public void setProfession(int professionId) {
        setValue(16, professionId % 6);
        sendData(16);
    }

    public void setProfession(Profession newProfession) {
        setProfession(newProfession.getId());
    }
}
