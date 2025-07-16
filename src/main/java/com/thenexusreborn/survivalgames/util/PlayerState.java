package com.thenexusreborn.survivalgames.util;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.disguises.DisguiseAPI;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;
import java.util.function.*;

public class PlayerState {
    protected Function<SGPlayer, GameMode> gameMode;
    protected ToIntFunction<SGPlayer> food;
    protected ToIntFunction<SGPlayer> saturation;
    protected Function<SGPlayer, Boolean> allowFlight;
    protected Function<SGPlayer, Boolean> flying;
    protected Function<SGPlayer, Boolean> collisions;
    protected Function<SGPlayer, Boolean> clearInventory;
    protected Function<SGPlayer, Boolean> clearEffects;
    protected ToIntFunction<SGPlayer> totalExperience;
    protected ToIntFunction<SGPlayer> level;
    protected ToIntFunction<SGPlayer> exp;
    protected ToDoubleFunction<SGPlayer> maxHealth;
    protected ToDoubleFunction<SGPlayer> health;
    protected ToDoubleFunction<SGPlayer> flySpeed; //MC Default is 0.2
    protected ToDoubleFunction<SGPlayer> walkSpeed; //MC Default is 0.2
    protected Function<SGPlayer, Boolean> itemPickup;
    protected Function<SGPlayer, Boolean> disguises;

    public PlayerState itemPickup(Function<SGPlayer, Boolean> itemPickup) {
        this.itemPickup = itemPickup;
        return this;
    }

    public PlayerState flySpeed(ToDoubleFunction<SGPlayer> flySpeed) {
        this.flySpeed = flySpeed;
        return this;
    }

    public PlayerState walkSpeed(ToDoubleFunction<SGPlayer> walkSpeed) {
        this.walkSpeed = walkSpeed;
        return this;
    }

    public PlayerState gameMode(Function<SGPlayer, GameMode> gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    public PlayerState food(ToIntFunction<SGPlayer> food) {
        this.food = food;
        return this;
    }

    public PlayerState saturation(ToIntFunction<SGPlayer> saturation) {
        this.saturation = saturation;
        return this;
    }

    public PlayerState allowFlight(Function<SGPlayer, Boolean> allowFlight) {
        this.allowFlight = allowFlight;
        return this;
    }

    public PlayerState flying(Function<SGPlayer, Boolean> flying) {
        this.flying = flying;
        return this;
    }

    public PlayerState collisions(Function<SGPlayer, Boolean> collisions) {
        this.collisions = collisions;
        return this;
    }

    public PlayerState clearInventory(Function<SGPlayer, Boolean> clearInventory) {
        this.clearInventory = clearInventory;
        return this;
    }

    public PlayerState clearEffects(Function<SGPlayer, Boolean> clearEffects) {
        this.clearEffects = clearEffects;
        return this;
    }

    public PlayerState totalExperience(ToIntFunction<SGPlayer> totalExperience) {
        this.totalExperience = totalExperience;
        return this;
    }

    public PlayerState level(ToIntFunction<SGPlayer> level) {
        this.level = level;
        return this;
    }

    public PlayerState exp(ToIntFunction<SGPlayer> exp) {
        this.exp = exp;
        return this;
    }

    public PlayerState maxHealth(ToDoubleFunction<SGPlayer> maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public PlayerState health(ToDoubleFunction<SGPlayer> health) {
        this.health = health;
        return this;
    }
    
    public PlayerState disguises(Function<SGPlayer, Boolean> disguises) {
        this.disguises = disguises;
        return this;
    }

    public PlayerState itemPickup(boolean itemPickup) {
        this.itemPickup = p -> itemPickup;
        return this;
    }

    public PlayerState flySpeed(double flySpeed) {
        this.flySpeed = p -> flySpeed;
        return this;
    }

    public PlayerState walkSpeed(double walkSpeed) {
        this.walkSpeed = p -> walkSpeed;
        return this;
    }

    public PlayerState gameMode(GameMode gameMode) {
        this.gameMode = p -> gameMode;
        return this;
    }

    public PlayerState food(int food) {
        this.food = p -> food;
        return this;
    }

    public PlayerState saturation(int saturation) {
        this.saturation = p -> saturation;
        return this;
    }

    public PlayerState allowFlight(boolean allowFlight) {
        this.allowFlight = p -> allowFlight;
        return this;
    }

    public PlayerState flying(boolean flying) {
        this.flying = p -> flying;
        return this;
    }

    public PlayerState collisions(boolean collisions) {
        this.collisions = p -> collisions;
        return this;
    }

    public PlayerState clearInventory(boolean clearInventory) {
        this.clearInventory = p -> clearInventory;
        return this;
    }

    public PlayerState clearEffects(boolean clearEffects) {
        this.clearEffects = p -> clearEffects;
        return this;
    }

    public PlayerState totalExperience(int totalExperience) {
        this.totalExperience = p -> totalExperience;
        return this;
    }

    public PlayerState level(int level) {
        this.level = p -> level;
        return this;
    }

    public PlayerState exp(int exp) {
        this.exp = p -> exp;
        return this;
    }

    public PlayerState maxHealth(double maxHealth) {
        this.maxHealth = p -> maxHealth;
        return this;
    }

    public PlayerState health(double health) {
        this.health = p -> health;
        return this;
    }
    
    public PlayerState disguises(boolean disguises) {
        this.disguises = p -> disguises;
        return this;
    }
    
    public void apply(Player player) {
        if (player == null) {
            return;
        }
        
        SGPlayer sgPlayer = SurvivalGames.getInstance().getPlayerRegistry().get(player.getUniqueId());
        
        if (this.gameMode != null) {
            player.setGameMode(this.gameMode.apply(sgPlayer));
        }
        
        if (this.food != null) {
            player.setFoodLevel(this.food.applyAsInt(sgPlayer));
        }
        
        if (this.saturation != null) {
            player.setSaturation(this.saturation.applyAsInt(sgPlayer));
        }
        
        if (this.allowFlight != null) {
            player.setAllowFlight(this.allowFlight.apply(sgPlayer));
        }
        
        if (this.flying != null) {
            player.setFlying(this.flying.apply(sgPlayer));
        }
        
        if (this.collisions != null) {
            player.spigot().setCollidesWithEntities(this.collisions.apply(sgPlayer));
        }
        
        if (this.clearInventory != null) {
            if (this.clearInventory.apply(sgPlayer)) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
            }
        }
        
        if (this.clearEffects != null) {
            if (this.clearEffects.apply(sgPlayer)) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
            }
        }

        if (this.totalExperience != null) {
            int totalExperience = this.totalExperience.applyAsInt(sgPlayer);
            if (totalExperience > -1) {
                player.setTotalExperience(totalExperience);
            }
        }

        if (this.level != null) {
            int level = this.level.applyAsInt(sgPlayer);
            if (level > -1) {
                player.setLevel(level);
            }
        }
        
        if (this.exp != null) {
            int exp = this.exp.applyAsInt(sgPlayer);
            if (exp > -1) {
                player.setExp(exp);
            }
        }

        if (this.maxHealth != null) {
            double maxHealth = this.maxHealth.applyAsDouble(sgPlayer);
            if (maxHealth > -1) {
                player.setMaxHealth(maxHealth);
            }
        }

        if (this.health != null) {
            double health = this.health.applyAsDouble(sgPlayer);
            if (health > -1) {
                player.setHealth(health);
            }
        }

        if (this.flySpeed != null) {
            double flySpeed = this.flySpeed.applyAsDouble(sgPlayer);
            if (flySpeed > -1) {
                player.setFlySpeed((float) flySpeed);
            }
        }
        
        if (this.walkSpeed != null) {
            double walkSpeed = this.walkSpeed.applyAsDouble(sgPlayer);
            if (walkSpeed > -1) {
                player.setWalkSpeed((float) walkSpeed);
            }
        }
        
        if (this.itemPickup != null) {
            player.setCanPickupItems(this.itemPickup.apply(sgPlayer));
        }
        
        if (this.disguises != null && !this.disguises.apply(sgPlayer)) {
            if (DisguiseAPI.isDisguised(player)) {
                DisguiseAPI.undisguiseToAll(player);
            }
        }
    }
    
    public void apply(NexusPlayer nexusPlayer) {
        apply(nexusPlayer.getUniqueId());
    }
    
    public void apply(SGPlayer sgPlayer) {
        apply(sgPlayer.getUniqueId());
    }
    
    public void apply(GamePlayer gamePlayer) {
        apply(gamePlayer.getUniqueId());
    }
    
    public void apply(LobbyPlayer lobbyPlayer) {
        apply(lobbyPlayer.getUniqueId());
    }
    
    public void apply(UUID uuid) {
        apply(Bukkit.getPlayer(uuid));
    }
}
