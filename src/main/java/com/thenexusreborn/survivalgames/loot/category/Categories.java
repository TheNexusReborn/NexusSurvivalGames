package com.thenexusreborn.survivalgames.loot.category;

public final class Categories {
    public static final CategoryRegistry REGISTRY = new CategoryRegistry();

    public static final LootCategory COOKED_FOOD = new LootCategory("cooked_food");
    public static final LootCategory RAW_FOOD = new LootCategory("raw_food");
    public static final LootCategory MISC_FOOD = new LootCategory("misc_food");
    public static final LootCategory ARMOR = new LootCategory("armor");
    public static final LootCategory WEAPONS = new LootCategory("weapons");
    public static final LootCategory COMPONENTS = new LootCategory("components");
    public static final LootCategory THROWABLES = new LootCategory("throwables");
    public static final LootCategory PLACEABLES = new LootCategory("placeables");
    public static final LootCategory TOOLS = new LootCategory("tools");
}
