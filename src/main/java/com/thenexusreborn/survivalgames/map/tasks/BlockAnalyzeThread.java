package com.thenexusreborn.survivalgames.map.tasks;

import com.stardevllc.starmclib.Position;
import com.thenexusreborn.gamemaps.model.SGMap;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class BlockAnalyzeThread implements Runnable {
    private final AnalyzeThread analyzeThread;
    private SGMap map;
    private List<Position> blocks;

    public BlockAnalyzeThread(AnalyzeThread analyzeThread, List<Position> blocks) {
        this.analyzeThread = analyzeThread;
        this.map = analyzeThread.getGameMap();
        this.blocks = blocks;
    }

    public void run() {
        for (Position pos : blocks) {
            Block block = map.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            if (block.getType() != Material.AIR) {
                switch (block.getType()) {
                    case CHEST, TRAPPED_CHEST -> analyzeThread.incrementChests();
                    case ENCHANTMENT_TABLE -> analyzeThread.incrementEnchantTables();
                    case WORKBENCH -> analyzeThread.incrementWorkbenches();
                    case FURNACE, BURNING_FURNACE -> analyzeThread.incrementFurnaces();
                }
                analyzeThread.incrementTotalBlocks();
            }
        }
        
    }
}
