package com.thenexusreborn.survivalgames.loot.category;

public enum LootCategory {
    
    COOKED_FOOD("cooked_food"),
    RAW_FOOD("raw_food"),
    MISC_FOOD("misc_food"),
    HELMETS("helmets", 1),
    CHESTPLATES("chestplates", 1),
    LEGGINGS("leggings", 1),
    BOOTS("boots", 1),
    WEAPONS("weapons", 1),
    COMPONENTS("components"),
    THROWABLES("throwables"),
    PLACEABLES("placeables"),
    TOOLS("tools", 1);
    
    private final String name;
    private int maxAmountPerChest;

    LootCategory(String name) {
        this.name = name;
    }

    LootCategory(String name, int maxAmountPerChest) {
        this(name);
        this.maxAmountPerChest = maxAmountPerChest;
    }

    public String getName() {
        return name;
    }

    public int getMaxAmountPerChest() {
        return maxAmountPerChest;
    }
}
