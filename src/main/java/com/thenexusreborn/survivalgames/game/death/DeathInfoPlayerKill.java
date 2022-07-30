package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.settings.ColorMode;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DeathInfoPlayerKill extends DeathInfo {
    
    protected final UUID killer;
    protected ItemStack handItem;
    protected final double killerHealth;
    protected boolean mutationKill;
    private final String killerTeamColor;
    
    public DeathInfoPlayerKill(UUID player, UUID killer, ItemStack handItem, double killerHealth, String killerTeamColor) {
        super(player, DeathType.PLAYER);
        this.killer = killer;
        if (handItem != null) {
            this.handItem = handItem.clone();
        }
        this.killerHealth = killerHealth;
        this.killerTeamColor = killerTeamColor;
    }
    
    public String getDeathMessage(Game game) {
        String killerName;
        if (game.getSettings().getColorMode() == ColorMode.GAME_TEAM) {
            killerName = killerTeamColor + Bukkit.getPlayer(killer).getName();
        } else {
            SpigotNexusPlayer nexusPlayer = game.getPlayer(this.killer).getNexusPlayer();
            killerName = nexusPlayer.getRank().getColor() + nexusPlayer.getName();
        }
        
        String itemName = getHandItem(handItem);
    
        this.deathMessage = "&4&l>> %playername% &7was killed by " + killerName + " &7using " + itemName;
        return super.getDeathMessage(game);
    }
    
    public UUID getKiller() {
        return killer;
    }
    
    public ItemStack getHandItem() {
        return handItem;
    }
    
    public double getKillerHealth() {
        return killerHealth;
    }
    
    public boolean isMutationKill() {
        return mutationKill;
    }
    
    public String getKillerTeamColor() {
        return killerTeamColor;
    }
}
