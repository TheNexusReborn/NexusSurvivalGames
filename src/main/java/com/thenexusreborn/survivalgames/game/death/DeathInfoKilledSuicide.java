package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.survivalgames.game.*;

import java.util.UUID;

public class DeathInfoKilledSuicide extends DeathInfo {
    
    private UUID killer;
    private String killerTeamColor;
    
    public DeathInfoKilledSuicide(UUID player, UUID killer, String killerTeamColor) {
        super(player, DeathType.SUICIDE_KILLED);
        this.killer = killer;
        this.killerTeamColor = killerTeamColor;
    }
    
    public UUID getKiller() {
        return killer;
    }
    
    public String getKillerTeamColor() {
        return killerTeamColor;
    }
    
    @Override
    public String getDeathMessage(Game game) {
        //String killerName = killerTeamColor + Bukkit.getPlayer(killer).getName();
        SpigotNexusPlayer nexusPlayer = game.getPlayer(this.killer).getNexusPlayer();
        String killerName = nexusPlayer.getRank().getColor() + nexusPlayer.getName();
        this.deathMessage = "&4&l>> %playername% &7was killed by " + killerName + "&7's suicide.";
        return super.getDeathMessage(game);
    }
}