package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.api.helper.NumberHelper;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeathInfoProjectile extends DeathInfo {
    
    private Entity shooter;
    private double distance;
    private String killerTeamColor;
    private double killerHealth;
    
    public DeathInfoProjectile(UUID player, Entity shooter, double distance, String teamColor, double health) {
        super(player, DeathType.PROJECTILE);
        this.shooter = shooter;
        this.distance = distance;
        this.killerTeamColor = teamColor;
        this.killerHealth = health;
    }
    
    public String getDeathMessage(Game game) {
        String killerName;
        if (shooter instanceof Player) {
            Player playerShooter = (Player) shooter;
            //killerName = killerTeamColor + playerShooter.getName();
            SpigotNexusPlayer nexusPlayer = game.getPlayer(playerShooter.getUniqueId()).getNexusPlayer();
            killerName = nexusPlayer.getRank().getColor() + nexusPlayer.getName();
        } else {
            killerName = "&f" + EntityNames.getInstance().getName(shooter.getType());
        }
        
        this.deathMessage = "&4&l>> %playername% &7was shot by " + killerName + " &7from &f" + NumberHelper.formatNumber(distance) + " blocks.";
        return super.getDeathMessage(game);
    }
    
    public Entity getShooter() {
        return shooter;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public String getKillerTeamColor() {
        return killerTeamColor;
    }
    
    public double getKillerHealth() {
        return this.killerHealth;
    }
}
