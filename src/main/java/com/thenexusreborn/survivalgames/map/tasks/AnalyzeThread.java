package com.thenexusreborn.survivalgames.map.tasks;

import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.map.GameMap;
import me.firestar311.starlib.spigot.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AnalyzeThread implements Runnable {
    
    private SurvivalGames plugin;
    private Cuboid cuboid;
    private GameMap map;
    private Player player;

    public AnalyzeThread(SurvivalGames plugin, Cuboid cuboid, GameMap map, Player player) {
        this.plugin = plugin;
        this.cuboid = cuboid;
        this.map = map;
        this.player = player;
    }

    public void run() {
        for(int x = cuboid.getXMin(); x <= cuboid.getXMax(); ++x) {
            for(int y = cuboid.getYMin(); y <= cuboid.getYMax(); ++y) {
                for(int z = cuboid.getZMin(); z <= cuboid.getZMax(); ++z) {
                    Bukkit.getServer().getScheduler().runTask(plugin, new BlockAnalyzeThread(map, x, y, z));
                }
            }
        }

        player.sendMessage(MCUtils.color(MsgType.INFO + "Analysis Complete. Use /sg map analysis to view results."));
    }
}
