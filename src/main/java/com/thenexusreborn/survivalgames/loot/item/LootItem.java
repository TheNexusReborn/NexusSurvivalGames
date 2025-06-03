package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.utils.MaterialNames;
import com.thenexusreborn.survivalgames.loot.category.LootCategory;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LootItem {
    protected final LootCategory category;
    protected final Material material;
    protected final String name;
    protected final List<String> lore = new LinkedList<>();
    protected final int amount;
    
    public LootItem(LootCategory category, Material material, String name, int amount, List<String> lore) {
        this.category = category;
        this.material = material;
        this.name = name;
        this.lore.addAll(lore);
        this.amount = amount;
    }
    
    public LootItem(LootCategory category, Material material, String name, int amount) {
        this(category, material, name, amount, new ArrayList<>());
    }
    
    public LootItem(LootCategory category, Material material, int amount) {
        this(category, material, MaterialNames.getDefaultName(material), amount);
    }
    
    public LootItem(LootCategory category, Material material, String name, List<String> lore) {
        this(category, material, name, 1, lore);
    }
    
    public LootItem(LootCategory category, Material material, String name) {
        this(category, material, name, new ArrayList<>());
    }
    
    public LootItem(LootCategory category, Material material) {
        this(category, material, MaterialNames.getDefaultName(material));
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }
    
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new LinkedList<>();
        for (String line : this.getLore()) {
            lore.add(StarColors.color(line));
        }
        if (!getName().equalsIgnoreCase(getMaterial().name().replace("_", " "))) {
            itemMeta.setDisplayName(StarColors.color("&f" + this.getName()));
        }
        
        if (this.getMaterial() == Material.FLINT_AND_STEEL) {
            itemStack.setDurability((short) (getMaterial().getMaxDurability() - 4));
        } else if (this.getMaterial() == Material.FISHING_ROD) {
            lore = List.of("", StarColors.color("&fUses Left: &e10"));
        }
        
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        
        if (this.getMaterial() == Material.FISHING_ROD) {
            NBT.modify(itemStack, nbt -> {
                nbt.setInteger("sg_uses", 20);
                nbt.setInteger("sg_display_uses", 10);
            });
        }
        
        int amount = new Random().nextInt(1, this.amount + 1);
        
        itemStack.setAmount(amount);
        return itemStack;
    }
    
    public LootItem setAmount(int amount) {
        return new LootItem(category, material, name, amount, lore);
    }
    
    public LootCategory getCategory() {
        return category;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        LootItem lootItem = (LootItem) object;
        return Objects.equals(name, lootItem.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
