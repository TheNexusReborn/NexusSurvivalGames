package com.thenexusreborn.survivalgames.map.tasks;

import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockAnalyzeThread implements Runnable {
    
    private GameMap map;
    private int x, y, z;

    public BlockAnalyzeThread(GameMap map, int x, int y, int z) {
        this.map = map;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void run() {
        Block block = map.getWorld().getBlockAt(x, y, z);
        if (block.getType() != Material.AIR) {
            switch (block.getType()) {
                case CHEST, TRAPPED_CHEST -> map.setChests(map.getChests() + 1);
                case ENCHANTMENT_TABLE -> map.setEnchantTables(map.getEnchantTables() + 1);
                case WORKBENCH -> map.setWorkbenches(map.getWorkbenches() + 1);
                case FURNACE, BURNING_FURNACE -> map.setFurnaces(map.getFurnaces() + 1);
                default -> map.setTotalBlocks(map.getTotalBlocks() + 1);
            }
        } 
    }
}
