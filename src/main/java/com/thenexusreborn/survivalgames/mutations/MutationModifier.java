package com.thenexusreborn.survivalgames.mutations;

import com.stardevllc.helper.StringHelper;

public enum MutationModifier {
    NO_HEALTH_REGEN, 
    ALLERGIC_TO_WATER, 
    FIFTY_PERCENT_INCREASED_DAMAGE("+50% Incoming Damage");
    
    private final String displayName;
    
    MutationModifier() {
        displayName = StringHelper.titlize(name());
    }
    
    MutationModifier(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
