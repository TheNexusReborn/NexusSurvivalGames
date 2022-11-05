package com.thenexusreborn.survivalgames.game.deathold;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.survivalgames.game.*;

import java.util.UUID;

public class DeathInfoKilledSuicide extends DeathInfo {
    
    private final UUID killer;
    private final String killerTeamColor;
    protected final double killerHealth;
    
    public DeathInfoKilledSuicide(UUID player, UUID killer, double killerHealth, String killerTeamColor) {
        super(player, DeathType.SUICIDE_KILLED);
        this.killer = killer;
        this.killerTeamColor = killerTeamColor;
        this.killerHealth = killerHealth;
    }
    
    public UUID getKiller() {
        return killer;
    }
    
    public String getKillerTeamColor() {
        return killerTeamColor;
    }
    
    public double getKillerHealth() {
        return killerHealth;
    }
    
    @Override
    public String getDeathMessage(Game game) {
        //String killerName = killerTeamColor + Bukkit.getPlayer(killer).getName();
        NexusPlayer nexusPlayer = game.getPlayer(this.killer).getNexusPlayer();
        String killerName = nexusPlayer.getRanks().get().getColor() + nexusPlayer.getName();
        this.deathMessage = "&4&l>> %playername% &7was killed by " + killerName + "&7's suicide.";
        return super.getDeathMessage(game);
    }
}