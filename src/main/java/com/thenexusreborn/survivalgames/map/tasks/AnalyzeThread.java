package com.thenexusreborn.survivalgames.map.tasks;

import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.Position;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.map.GameMap;
import me.firestar311.starlib.spigot.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnalyzeThread implements Runnable {

    private SurvivalGames plugin;
    private GameMap gameMap;
    private Player player;
    private Cuboid cuboid;

    private AtomicInteger totalBlocks = new AtomicInteger(), chests = new AtomicInteger(), enchantTables = new AtomicInteger(), workbenches = new AtomicInteger(), furnaces = new AtomicInteger();

    public AnalyzeThread(SurvivalGames plugin, GameMap map, Player player) {
        this.plugin = plugin;
        this.gameMap = map;
        this.player = player;
        Position center = gameMap.getCenter();
        int borderDistance = gameMap.getBorderDistance();
        Location min = new Location(gameMap.getWorld(), center.getX() - borderDistance, 0, center.getZ() - borderDistance);
        Location max = new Location(gameMap.getWorld(), center.getX() + borderDistance, 256, center.getZ() + borderDistance);
        this.cuboid = new Cuboid(min, max);
    }

    public void run() {
        List<Position> blocks = new ArrayList<>();
        
        for (int x = cuboid.getXMin(); x <= cuboid.getXMax(); x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = cuboid.getZMin(); z <= cuboid.getZMax(); z++) {
                    blocks.add(new Position(x, y, z));
                    if (blocks.size() == 100) {
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, new BlockAnalyzeThread(this, new ArrayList<>(blocks)), 1L);
                        blocks.clear();
                    }
                }
            }
        }

        Bukkit.getServer().getScheduler().runTask(plugin, new BlockAnalyzeThread(this, new ArrayList<>(blocks)));
        player.sendMessage(MCUtils.color(MsgType.INFO + "Analysis Complete. Use /sg map analysis to view results."));
    }

    public SurvivalGames getPlugin() {
        return plugin;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void incrementTotalBlocks() {
        this.totalBlocks.getAndIncrement();
        updateGameMapValues();
    }

    public void incrementChests() {
        this.chests.getAndIncrement();
        updateGameMapValues();
    }

    public void incrementEnchantTables() {
        this.enchantTables.getAndIncrement();
        updateGameMapValues();
    }

    public void incrementWorkbenches() {
        this.workbenches.getAndIncrement();
        updateGameMapValues();
    }

    public void incrementFurnaces() {
        this.furnaces.getAndIncrement();
        updateGameMapValues();
    }

    public void setValues(int totalBlocks, int chests, int enchantTables, int workbenches, int furnaces) {
        this.totalBlocks.getAndAdd(totalBlocks);
        this.chests.getAndAdd(chests);
        this.enchantTables.getAndAdd(enchantTables);
        this.workbenches.getAndAdd(workbenches);
        this.furnaces.getAndAdd(furnaces);
    }
    
    public void updateGameMapValues() {
        this.gameMap.setTotalBlocks(this.totalBlocks.get());
        this.gameMap.setChests(this.chests.get());
        this.gameMap.setEnchantTables(this.enchantTables.get());
        this.gameMap.setWorkbenches(this.workbenches.get());
        this.gameMap.setFurnaces(this.furnaces.get());
    }
}
