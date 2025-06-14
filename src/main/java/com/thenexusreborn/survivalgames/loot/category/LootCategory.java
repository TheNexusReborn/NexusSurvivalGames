package com.thenexusreborn.survivalgames.loot.category;

public enum LootCategory {
    
    FOOD("food"), 
    COOKED_FOOD("cooked_food"),
    RAW_FOOD("raw_food"),
    MISC_FOOD("misc_food"),
    
    ARMOR("armor"),
    LEATHER_ARMOR("leather_armor"),
    GOLD_ARMOR("gold_armor"),
    IRON_ARMOR("iron_armor"),
    CHAINMAIL_ARMOR("chainmail_armor", 2),
    DIAMOND_ARMOR("diamond_armor"),
    
    HELMETS("helmets", 1),
    CHESTPLATES("chestplates", 1),
    LEGGINGS("leggings", 1),
    BOOTS("boots", 1),
    
    WEAPONS("weapons", 1),
    WOODEN_WEAPONS("wooden_weapons"),
    GOLD_WEAPONS("gold_weapons"),
    STONE_WEAPONS("stone_weapons"),
    IRON_WEAPONS("iron_weapons"),
    DIAMOND_WEAPONS("diamond_weapons"),
    
    SWORDS("swords"), 
    AXES("axes"),
    RANGED("ranged"),
    
    COMPONENTS("components"),
    WEAPON_COMPONENT("weapon_component"),
    TOOL_COMPONENT("tool_component"),
    ARMOR_COMPONENT("armor_component"),
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
