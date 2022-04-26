package com.thenexusreborn.survivalgames.game.death;

import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.settings.ColorMode;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DeathInfo {
    protected UUID player;
    protected DeathType type;
    protected String deathMessage, teamColor;
    
    public DeathInfo(UUID player, DeathType type) {
        this.player = player;
        this.type = type;
        this.teamColor = SurvivalGames.getPlugin(SurvivalGames.class).getGame().getPlayer(player).getTeam().getColor();
    }
    
    public DeathInfo(UUID player, DeathType type, String deathMessage) {
        this.player = player;
        this.type = type;
        this.deathMessage = deathMessage;
        this.teamColor = SurvivalGames.getPlugin(SurvivalGames.class).getGame().getPlayer(player).getTeam().getColor();
    }
    
    public DeathInfo(UUID player, DeathType type, String deathMessage, String teamColor) {
        this.player = player;
        this.type = type;
        this.deathMessage = deathMessage;
        this.teamColor = teamColor;
    }
    
    public String getDeathMessage(Game game) {
        String message = deathMessage;
        if (message != null) {
            if (game.getSettings().getColorMode() == ColorMode.GAME_TEAM) {
                message = message.replace("%playername%", teamColor + Bukkit.getPlayer(player).getName());
            } else {
                SpigotNexusPlayer nexusPlayer = game.getPlayer(this.player).getNexusPlayer();
                message = message.replace("%playername%", nexusPlayer.getRank().getColor() + nexusPlayer.getName());
            }
        }
        return message;
    }
    
    public static String getKillerName(Game game, UUID killer) {
        String teamColor = game.getPlayer(killer).getTeam().getColor();
        return teamColor + game.getPlayer(killer).getNexusPlayer().getName();
    }
    
    public static String getHandItem(ItemStack handItem) {
        String itemName;
        if (handItem != null && !handItem.getType().equals(Material.AIR)) {
            if (!handItem.hasItemMeta() || handItem.getItemMeta().getDisplayName() == null) {
                itemName = handItem.getType().name().toLowerCase().replace("_", " ");
            } else {
                itemName = handItem.getItemMeta().getDisplayName();
            }
        } else {
            itemName = "their fists";
        }
        return itemName;
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
}