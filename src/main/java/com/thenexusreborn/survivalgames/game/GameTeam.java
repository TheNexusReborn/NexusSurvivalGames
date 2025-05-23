package com.thenexusreborn.survivalgames.game;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.util.PlayerState;
import org.bukkit.*;

public enum GameTeam {
    TRIBUTES(
            "Tributes", 
            "&a", 
            "&c", 
            Sound.WITHER_SPAWN, 
            new PlayerState()
                    .gameMode(GameMode.SURVIVAL)
                    .allowFlight(false)
                    .flying(false)
                    .collisions(true)
                    .maxHealth(p -> p.getGame().getSettings().getMaxHealth())
                    .health(p -> p.getGame().getSettings().getMaxHealth())
                    .saturation(p -> (int) p.getGame().getSettings().getStartingSaturation())
                    .clearInventory(true)
                    .clearEffects(true)
                    .totalExperience(0)
                    .level(0)
                    .exp(0)
                    .disguises(false), 
            "&8<&3%nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexussg_displayname%&8: %nexuscore_chatcolor%{message}"
    ), 
    
    SPECTATORS(
            "Spectators", 
            "&c", 
            Sound.ARROW_HIT, 
            new PlayerState()
                    .gameMode(GameMode.SURVIVAL)
                    .allowFlight(true)
                    .flying(true)
                    .collisions(false)
                    .maxHealth(20)
                    .health(20)
                    .clearInventory(true)
                    .clearEffects(true)
                    .totalExperience(0)
                    .level(0)
                    .exp(0)
                    .disguises(false),
            "&8[&cSpectators&8] &r%nexussg_displayname%&8: %nexuscore_chatcolor%{message}"
    ), 
    
    MUTATIONS(
            "Mutations", 
            "&d", 
            Sound.ZOMBIE_PIG_DEATH, 
            new PlayerState()
                    .gameMode(GameMode.SURVIVAL)
                    .allowFlight(false)
                    .flying(false)
                    .collisions(true)
                    .maxHealth(p -> {
                        Mutation mutation = p.getGamePlayer().getMutation();
                        if (mutation != null) {
                            return mutation.getType().getHealth();
                        } else {
                            SurvivalGames.getInstance().getLogger().warning("Mutations Team player state apply().maxHealth called and mutation is null");
                            return p.getGame().getSettings().getMaxHealth();
                        }
                    })
                    .health(p -> {
                        Mutation mutation = p.getGamePlayer().getMutation();
                        if (mutation != null) {
                            return mutation.getType().getHealth();
                        } else {
                            SurvivalGames.getInstance().getLogger().warning("Mutations Team player state apply().health() called and mutation is null");
                            return p.getGame().getSettings().getMaxHealth();
                        }
                    })
                    .food(20)
                    .saturation(20)
                    .clearInventory(true)
                    .clearEffects(true)
                    .totalExperience(0)
                    .level(0)
                    .exp(0),
            "&8<&3%nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexussg_displayname%&8: %nexuscore_chatcolor%{message}"
    ), 
    
    ZOMBIES(
            "Zombies", "&2", 
            Sound.ZOMBIE_DEATH, 
            new PlayerState(), 
            "%&8<&3nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexussg_displayname%&8: %nexuscore_chatcolor%{message}"
    );
    
    static {
        StringConverters.addConverter(GameTeam.class, new EnumStringConverter<>(GameTeam.class));
    }
    
    private final String name, color, remainColor, chatFormat;
    private final Sound deathSound;
    private final PlayerState playerState;
    
    GameTeam(String name, String color, Sound sound, PlayerState state, String chatFormat) {
        this(name, color, color, sound, state, chatFormat);
    }
    
    GameTeam(String name, String color, String remainColor, Sound deathSound, PlayerState state, String chatFormat) {
        this.name = name;
        this.color = color;
        this.remainColor = remainColor;
        this.deathSound = deathSound;
        this.chatFormat = chatFormat;
        this.playerState = state;
    }
    
    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public String getRemainColor() {
        return remainColor;
    }
    
    public Sound getDeathSound() {
        return deathSound;
    }
    
    public String getChatFormat() {
        return chatFormat;
    }
    
    public String getJoinMessage() {
        return "&a&l>> &7You joined " + getColor() + getName();    
    }
    
    public String getLeaveMessage() {
        return "&c&l<< &7You left " + getColor() + getName();
    }
}
