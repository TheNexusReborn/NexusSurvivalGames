package com.thenexusreborn.survivalgames.loot;

import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.api.helper.StringHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Loot {
    protected int maxAmount;
    protected final Material material;
    protected final String name;
    protected final int weight;
    protected final List<String> lore = new ArrayList<>();
    
    public Loot(Material material, String name, int weight) {
        this.material = material;
        this.name = name;
        this.weight = weight;
    }
    
    public Loot(Material material, int weight) {
        this.material = material;
        this.name = StringHelper.capitalizeEveryWord(material.name());
        this.weight = weight;
    }
    
    public Loot(Material material, String name, int weight, int maxAmount) {
        this.material = material;
        this.name = name;
        this.weight = weight;
        this.maxAmount = maxAmount;
    }
    
    public Loot(Material material, int weight, int maxAmount) {
        this.material = material;
        this.name = StringHelper.capitalizeEveryWord(material.name());
        this.weight = weight;
        this.maxAmount = maxAmount;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public List<String> getLore() {
        return lore;
    }
    
    public void addLore(String line) {
        this.lore.add(MCUtils.color(line));
    }
    
    public ItemStack generateItemStack() {
        int amount;
        if (this.maxAmount != 0) {
            amount = new Random().nextInt(maxAmount - 1) + 1;
        } else {
            amount = 1;
        }
        ItemStack itemStack = new ItemStack(this.material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!name.equalsIgnoreCase(material.name().replace("_", " "))) {
            itemMeta.setDisplayName(MCUtils.color("&f" + this.name));
        }
        if (this.material == Material.FLINT_AND_STEEL) {
            itemStack.setDurability((short) (material.getMaxDurability() - 4));
        }
        itemMeta.setLore(this.lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public int getMaxAmount() {
        return maxAmount;
    }
    
    public String getName() {
        return name;
    }
    
    public int getWeight() {
        return weight;
    }
}
