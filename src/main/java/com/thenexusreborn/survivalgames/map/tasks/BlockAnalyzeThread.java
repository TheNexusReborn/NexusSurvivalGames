package com.thenexusreborn.survivalgames.map.tasks;

import com.thenexusreborn.nexuscore.util.Position;
import com.thenexusreborn.survivalgames.map.GameMap;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockAnalyzeThread implements Runnable {
    private final AnalyzeThread analyzeThread;
    private GameMap map;
    private List<Position> blocks;

    public BlockAnalyzeThread(AnalyzeThread analyzeThread, List<Position> blocks) {
        this.analyzeThread = analyzeThread;
        this.map = analyzeThread.getGameMap();
        this.blocks = blocks;
    }

    public void run() {
        for (Position pos : blocks) {
            Block block = map.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
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
