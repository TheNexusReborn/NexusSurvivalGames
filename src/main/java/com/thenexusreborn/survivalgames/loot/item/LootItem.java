package com.thenexusreborn.survivalgames.loot.item;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.utils.MaterialNames;
import com.thenexusreborn.survivalgames.loot.category.LootCategory;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LootItem {
    protected final String id;
    protected Set<LootCategory> categories = EnumSet.noneOf(LootCategory.class);
    protected final Material material;
    protected final String name;
    protected List<String> lore = new LinkedList<>();
    protected int amount = 1;
    protected Map<Enchantment, Integer> enchantments = new HashMap<>();
    
    public LootItem(Material material) {
        this(MaterialNames.getDefaultName(material), material);
    }
    
    public LootItem(String name, Material material) {
        this(StarColors.stripColor(name.toLowerCase().replace(" ", "_")), name, material);
    }
    
    public LootItem(String id, String name, Material material) {
        this.id = id;
        this.name = name;
        this.material = material;
    }
    
    public String getId() {
        return id;
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
    
    public Set<LootCategory> getCategories() {
        return categories;
    }
    
    public LootItem setCategories(LootCategory category, LootCategory... categories) {
        this.categories.clear();
        this.categories.add(category);
        if (categories != null) {
            this.categories.addAll(Set.of(categories));
        }
        return this;
    }
    
    public LootItem setLore(String... lore) {
        this.lore.clear();
        if (lore != null) {
            this.lore.addAll(List.of(lore));
        }
        return this;
    }
    
    public LootItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public LootItem addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
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
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        LootItem lootItem = (LootItem) object;
        return Objects.equals(id, lootItem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
