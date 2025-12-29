package com.thenexusreborn.survivalgames.threads.lobby;

import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LobbyThread extends StarThread<SurvivalGames> {
    
    private int secondsInPrepareGame;
    
    public LobbyThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    @Override
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers().values()) {
            Lobby lobby = server.getLobby();
            if (lobby == null) {
                continue;
            }
            
            World world = lobby.getWorld();
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            }
            
            LobbyState state = lobby.getState();
            for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                Player player = Bukkit.getPlayer(lobbyPlayer.getUniqueId());
                if (player == null) {
                    continue;
                }
                
                SGUtils.updatePlayerHealthAndFood(Bukkit.getPlayer(player.getUniqueId()));
                
                boolean sponsorsValue = lobbyPlayer.getToggleValue("allowsponsors");
                SMaterial sponsorsItemMaterial = sponsorsValue ? SMaterial.GLOWSTONE_DUST : SMaterial.GUNPOWDER;
                String statusMessage = sponsorsValue ? "&a&lENABLED" : "&c&lDISABLED";
                
                for (ItemStack itemStack : player.getInventory()) {
                    if (itemStack == null) {
                        continue;
                    }
                    
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta.getDisplayName() != null && itemMeta.getDisplayName().contains("Sponsors")) {
                        itemStack.setType(sponsorsItemMaterial.parseMaterial());
                        itemMeta.setDisplayName(StarColors.color("&e&lSponsors " + statusMessage + " &7&o(Right Click to toggle)"));
                        itemStack.setItemMeta(itemMeta);
                        break;
                    }
                }
                
                if (state != LobbyState.MAP_CONFIGURATING) {
                    if (player.getLocation().getBlockY() < lobby.getSpawnpoint().getBlockY() - 20) {
                        player.teleport(lobby.getSpawnpoint());
                    }
                }
            }
            
            if (world.isThundering()) {
                world.setThundering(false);
                world.setWeatherDuration(Integer.MAX_VALUE);
            }
            
            if (world.hasStorm()) {
                world.setStorm(false);
                world.setWeatherDuration(Integer.MAX_VALUE);
            }
            
            world.setGameRuleValue("doFireSpread", "false");
            
            Set<UUID> invalidPlayers = new HashSet<>();
            
            for (LobbyPlayer player : lobby.getPlayers()) {
                Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                
                if (bukkitPlayer == null) {
                    invalidPlayers.add(player.getUniqueId());
                }
            }
            
            for (UUID invalidPlayer : invalidPlayers) {
                lobby.removePlayer(invalidPlayer);
                plugin.getLogger().warning("Removed invalid player " + invalidPlayer);
            }
        }
    }
}