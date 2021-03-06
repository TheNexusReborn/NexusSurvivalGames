package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.settings.ColorMode;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DeathInfoTntKill extends DeathInfoPlayerKill {
    
    protected UUID killer;
    protected ItemStack handItem;
    protected double killerHealth;
    protected boolean mutationKill;
    private String killerTeamColor;
    
    public DeathInfoTntKill(UUID player, UUID killer, double killerHealth, String killerTeamColor) {
        super(player, killer, null, killerHealth, killerTeamColor);
        this.killer = killer;
        this.killerHealth = killerHealth;
        this.killerTeamColor = killerTeamColor;
    }
    
    @Override
    public String getDeathMessage(Game game) {
        String targetName, killerName;
        if (game.getSettings().getColorMode() == ColorMode.GAME_TEAM) {
            killerName = killerTeamColor + Bukkit.getPlayer(killer).getName();
            targetName = teamColor + Bukkit.getPlayer(player).getName();
        } else {
            SpigotNexusPlayer nexusPlayer = game.getPlayer(this.killer).getNexusPlayer();
            killerName = nexusPlayer.getRank().getColor() + nexusPlayer.getName();
            SpigotNexusPlayer targetPlayer = game.getPlayer(this.player).getNexusPlayer();
            targetName = targetPlayer.getRank().getColor() + targetPlayer.getName();
        }
        this.deathMessage = "&4&l>> %playername% &7was blown up by " + killerName;
        return this.deathMessage.replace("%playername%", targetName);
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
