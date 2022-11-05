package com.thenexusreborn.survivalgames.mutations;

import org.bukkit.potion.PotionEffectType;

public final class MutationEffect {
    private PotionEffectType potionType;
    private int amplifier;
    
    public MutationEffect(PotionEffectType potionType, int amplifier) {
        this.potionType = potionType;
        this.amplifier = amplifier;
    }
    
    public PotionEffectType getPotionType() {
        return potionType;
    }
    
    public int getAmplifier() {
        return amplifier;
    }
}
