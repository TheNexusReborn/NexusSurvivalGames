package com.thenexusreborn.survivalgames.game.death;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starcore.utils.EntityNames;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.settings.object.enums.ColorMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class DeathInfo {
    
    protected static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    protected Game game;
    protected UUID player;
    protected DeathType type;
    protected String teamColor;
    protected KillerInfo killer;
    protected GameTeam team;
    protected long time;
    protected Location deathLocation;
    
    public DeathInfo(Game game, long time, GamePlayer player, DeathType type, Location deathLocation) {
        this.game = game;
        this.player = player.getUniqueId();
        this.type = type;
        this.teamColor = player.getTeam().getColor();
        this.team = player.getTeam();
        this.time = time;
        this.deathLocation = deathLocation;
    }
    
    public DeathInfo(Game game, long time, GamePlayer player, DeathType type, Location deathLocation, KillerInfo killer) {
        this(game, time, player, type, deathLocation);
        this.killer = killer;
    }
    
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(this.player);
    }
    
    public GamePlayer getGamePlayer() {
        return game.getPlayer(this.player);
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
    
    public void setKiller(KillerInfo killer) {
        this.killer = killer;
    }
    
    public GameTeam getTeam() {
        return team;
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
            playerName = gamePlayer.getRank().getColor();
        }
        
        playerName += gamePlayer.getName() + "&7";
        deathMessage = deathMessage.replace("%playername%", playerName);
        
        if (killer != null) {
            if (killer.getType() == EntityType.PLAYER) {
                String killerName;
                GamePlayer killerPlayer = game.getPlayer(killer.getKiller());
                if (killerPlayer != null) {
                    if (game.getSettings().getColorMode() == ColorMode.GAME_TEAM) {
                        killerName = killer.getTeamColor() + killerPlayer.getName();
                    } else {
                        killerName = killerPlayer.getRank().getColor() + killerPlayer.getName();
                    }
                } else { //TODO Maybe just reset the dead player?
                    killerName = "&r" + killer.getName();
                }
                deathMessage = deathMessage.replace("%killername%", killerName + "&7");

                String itemName = "air";
                ItemStack handItem = killer.getHandItem();
                if (handItem != null) {
                    String displayName = "";
                    ItemMeta itemMeta = handItem.getItemMeta();
                    if (itemMeta != null) {
                        displayName = itemMeta.getDisplayName();
                    }
                    if (displayName != null && !displayName.isEmpty()) {
                        itemName = ChatColor.stripColor(displayName);
                    } else {
                        itemName = handItem.getType().name().toLowerCase().replace("_", " ");
                    }
                }
                deathMessage = deathMessage.replace("%helditem%", itemName);
            } else {
                deathMessage = deathMessage.replace("%entityname%", "&f" + EntityNames.getDefaultName(killer.getType()) + "&7");
            }
            
            if (killer.getDistance() > 0) {
                deathMessage = deathMessage.replace("%distance%", "&f" + MCUtils.formatNumber(killer.getDistance()));
            }
        }
        
        return ColorHandler.getInstance().color("&4&l>> &7" + deathMessage + "&7.");
    }
    
    public long getTime() {
        return this.time;
    }

    public Location getDeathLocation() {
        return deathLocation;
    }

    @Override
    public String toString() {
        return "DeathInfo{" +
                "game=" + game +
                ", player=" + player +
                ", type=" + type +
                ", teamColor='" + teamColor + '\'' +
                ", killer=" + killer +
                ", team=" + team +
                ", time=" + time +
                '}';
    }
}
