package com.thenexusreborn.survivalgames.game.deathold;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.api.helper.NumberHelper;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeathInfoProjectile extends DeathInfo {
    
    private final Entity shooter;
    private final double distance;
    private final String killerTeamColor;
    private final double killerHealth;
    
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
            NexusPlayer nexusPlayer = game.getPlayer(playerShooter.getUniqueId()).getNexusPlayer();
            killerName = nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName();
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
