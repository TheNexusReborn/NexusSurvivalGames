package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.loot.item.Items;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SwagShackMenu extends InventoryGUI {
    
    public static Set<ShopItem> items = new HashSet<>();
    
    static {
        items.add(new ShopItem(0, Items.WOOD_SWORD, 1, 25, 15));
        items.add(new ShopItem(1, Items.GOLD_CHESTPLATE, 1, 75, 100));
        items.add(new ShopItem(2, Items.GOLD_LEGGINGS, 1, 50, 80));
        items.add(new ShopItem(3, Items.BOW, 1, 100, 250));
        items.add(new ShopItem(5, Items.FISHING_ROD, 5, 100, 300));
        items.add(new ShopItem(6, Items.RAW_FISH, 5, 30, 50));
        items.add(new ShopItem(7, Items.MUSHROOM_SOUP, 1, 30, 50));
        items.add(new ShopItem(8, Items.CAKE, 1, 30, 50));
        items.add(new ShopItem(9, Items.STONE_AXE, 1, 35, 25));
        items.add(new ShopItem(12, Items.ARROW, 5, 50, 100));
        items.add(new ShopItem(24, Items.PLAYER_TRACKER, 1, 50, 100));
        items.add(new ShopItem(25, Items.XP_BOTTLE, 1, 75, 150));
        items.add(new ShopItem(26, Items.TNT, 1, 100, 200));
    }
    
    public SwagShackMenu(SurvivalGames plugin, Game game, GamePlayer player) {
        super(3, "&lSwag Shack");

        for (ShopItem item : items) {
            ItemStack itemStack = item.getItem().getItemStack();
            itemStack.setAmount(item.getAmount());
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = new LinkedList<>();
            lore.add("");
            lore.add(StarColors.color("&e&lPrice:"));
            lore.add(StarColors.color("  &6SG Points: &b" + item.getPointsCost()));
            lore.add(StarColors.color("  &6Credits: &b" + item.getCreditsCost()));
            lore.add("");
            lore.add(StarColors.color("&6&lLeft Click &7to buy with points."));
            lore.add(StarColors.color("&6&lRight Click &7to buy with credits."));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            
            Button button = new Button().iconCreator(p -> itemStack).consumer(e -> {
                String currency;
                int cost;

                if (e.getClick() == ClickType.LEFT) {
                    currency = "sg_score";
                    cost = item.getCreditsCost();
                } else if (e.getClick() == ClickType.RIGHT) {
                    currency = "credits";
                    cost = item.getPointsCost();
                } else {
                    return;
                }

                int amount;
                if (currency.equalsIgnoreCase("credits")) {
                    amount = (int) player.getBalance().getCredits();
                } else {
                    amount = player.getStats().getScore();
                }
                if (amount < cost) {
                    player.sendMessage(MsgType.WARN + "You do not have enough " + currency + " to buy this item.");
                    return;
                }

                if (currency.equalsIgnoreCase("credits")) {
                    player.getBalance().addCredits(-cost);
                } else {
                    player.getStats().addScore(-cost);
                }
                e.getWhoClicked().getInventory().addItem(item.getItemStack());
            });
            
            setElement(item.getPosition(), button);
        }
    }
    
    public static class ShopItem {
        private LootItem item;
        private int amount;
        private int position;
        private int pointsCost;
        private int creditsCost;
    
        public ShopItem(int position, LootItem item, int amount, int pointsCost, int creditsCost) {
            this.position = position;
            this.item = item;
            this.amount = amount;
            this.pointsCost = pointsCost;
            this.creditsCost = creditsCost;
        }
    
        public LootItem getItem() {
            return item;
        }
    
        public int getAmount() {
            return amount;
        }
    
        public int getPosition() {
            return position;
        }
    
        public int getPointsCost() {
            return pointsCost;
        }
    
        public int getCreditsCost() {
            return creditsCost;
        }
        
        public ItemStack getItemStack() {
            ItemStack itemStack = item.getItemStack();
            if (amount > 1) {
                itemStack.setAmount(this.amount);
            }
            
            return itemStack;
        }
    }
}
