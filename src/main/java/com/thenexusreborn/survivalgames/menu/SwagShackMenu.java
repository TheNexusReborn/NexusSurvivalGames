package com.thenexusreborn.survivalgames.menu;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.menu.element.button.*;
import com.thenexusreborn.nexuscore.menu.gui.Menu;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.loot.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SwagShackMenu extends Menu {
    
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
        super(plugin, "swagshack", "&lSwag Shack", 3);
    
        for (ShopItem item : items) {
            ItemStack itemStack = item.getItem().getItemStack();
            itemStack.setAmount(item.getAmount());
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = new LinkedList<>();
            lore.add("");
            lore.add(MCUtils.color("&e&lPrice:"));
            lore.add(MCUtils.color("  &6SG Points: &b" + item.getPointsCost()));
            lore.add(MCUtils.color("  &6Credits: &b" + item.getCreditsCost()));
            lore.add("");
            lore.add(MCUtils.color("&6&lLeft Click &7to buy with points."));
            lore.add(MCUtils.color("&6&lRight Click &7to buy with credits."));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            
            Button button = new Button(itemStack);
            button.setLeftClickAction(new ShopClickListener(player, item.getPointsCost(), "sg_score", itemStack));
            button.setRightClickAction(new ShopClickListener(player, item.getCreditsCost(), "credits", itemStack));
            
            setElement(item.getPosition(), button);
        }
    }
    
    public static class ShopClickListener implements ButtonAction {
        
        private GamePlayer player;
        private int cost;
        private String currency;
        private ItemStack itemStack;
    
        public ShopClickListener(GamePlayer player, int cost, String currency, ItemStack itemStack) {
            this.player = player;
            this.cost = cost;
            this.currency = currency;
            this.itemStack = itemStack;
        }
    
        @Override
        public void onClick(Player p, Menu menu, ClickType click) {
            int amount = this.player.getStatValue(currency).getAsInt();
            if (amount < cost) {
                player.sendMessage(MsgType.WARN + "You do not have enough " + NexusAPI.getApi().getStatRegistry().get(currency).getDisplayName() + " to buy this item.");
                return;
            }
            
            player.changeStat(currency, cost, StatOperator.SUBTRACT).push();
            p.getInventory().addItem(itemStack);
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
    }
}
