package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.api.helper.NumberHelper;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.settings.ColorMode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;

import java.util.UUID;

public class DeathInfo {
    
    protected static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    protected Game game;
    protected UUID player;
    protected DeathType type;
    protected String teamColor;
    protected KillerInfo killer;
    
    public DeathInfo(Game game, GamePlayer player, DeathType type) {
        this.game = game;
        this.player = player.getUniqueId();
        this.type = type;
        this.teamColor = player.getTeam().getColor();
    }
    
    public DeathInfo(Game game, GamePlayer player, DeathType type, KillerInfo killer) {
        this(game, player, type);
        this.killer = killer;
    }
    
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(this.player);
    }
    
    public GamePlayer getGamePlayer() {
        return game.getPlayer(this.player);
    }
    
    public NexusPlayer getNexusPlayer() {
        return getGamePlayer().getNexusPlayer();
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public DeathType getType() {
        return type;
    }
    
    public String getTeamColor() {
        return teamColor;
    }
    
    public KillerInfo getKiller() {
        return killer;
    }
    
    public String getDeathMessage() {
        GamePlayer gamePlayer = game.getPlayer(player);
        FileConfiguration deathMessagesConfig = plugin.getDeathMessagesConfig();
        String deathMessage;
        
        if (this.type.hasPlayerSubtype()) {
            if (this.killer != null) {
                deathMessage = deathMessagesConfig.getString(this.type.name() + ".player");
            } else {
                deathMessage = deathMessagesConfig.getString(this.type.name() + ".normal");
            }
        } else {
            deathMessage = deathMessagesConfig.getString(type.name());
        }
        String playerName;
        if (game.getSettings().getColorMode() == ColorMode.GAME_TEAM) {
            playerName = teamColor;
        } else {
            playerName = gamePlayer.getNexusPlayer().getRanks().get().getColor();
        }
        
        playerName += gamePlayer.getNexusPlayer().getName() + "&7";
        deathMessage = deathMessage.replace("%playername%", playerName);
        
        if (killer != null) {
            if (killer.getType() == EntityType.PLAYER) {
                String killerName;
                NexusPlayer killerPlayer = game.getPlayer(killer.getKiller()).getNexusPlayer();
                if (game.getSettings().getColorMode() == ColorMode.GAME_TEAM) {
                    killerName = killer.getTeamColor() + killerPlayer.getName();
                } else {
                    killerName = killerPlayer.getRanks().get().getColor() + killerPlayer.getName();
                }
                deathMessage = deathMessage.replace("%killername%", killerName);
                deathMessage = deathMessage.replace("%helditem%", killer.getHandItem().getItemMeta().getDisplayName());
            } else {
                deathMessage = deathMessage.replace("%entityname%", "&f" + EntityNames.getDefaultName(killer.getType()) + "&7");
            }
            
            if (killer.getDistance() > 0) {
                deathMessage = deathMessage.replace("%distance%", "&f" + NumberHelper.formatNumber(killer.getDistance()));
            }
        }
        
        return MCUtils.color("&4&l>> &7" + deathMessage + "&7.");
    }
}
