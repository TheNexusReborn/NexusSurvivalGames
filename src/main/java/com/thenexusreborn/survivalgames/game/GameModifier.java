package com.thenexusreborn.survivalgames.game;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;
import com.thenexusreborn.survivalgames.settings.GameSettings;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public enum GameModifier {
    MUTATIONS(List.of("Allows you to come back as a mob to seek revenge.", "If you kill your target, you return to the game."),
            settings -> settings.setMutationsEnabled(true), settings -> settings.setMutationsEnabled(false), GameSettings::isAllowMutations),
    UNLIMITED_PASSES(List.of("Allows you to mutate.", "Ignoring your pass balance", "You will still earn passes."), 
            settings -> settings.setUnlimitedMutationPasses(true), settings -> settings.setUnlimitedMutationPasses(false), GameSettings::isUnlimitedPasses), 
    ALL_MUTATIONS(List.of("This allows you to use all mutation types"), 
            settings -> settings.setUseAllMutationTypes(true), settings -> settings.setUseAllMutationTypes(false), GameSettings::isUseAllMutations),
    ASSISTS(List.of("Earn rewards for helping in a kill."), 
            settings -> settings.setAllowAssists(true), settings -> settings.setAllowAssists(false), GameSettings::isAllowAssists),
    BOUNTIES(List.of("Set a reward (either score or credit)", "to entice other players to kill someone"), 
            settings -> settings.setAllowBounties(true), settings -> settings.setAllowBounties(false), GameSettings::isAllowBounties), 
    SPONSORS(List.of("Allows you to sponsor another player", "The possible things are either good or bad"), 
            settings -> settings.setAllowSponsoring(true), settings -> settings.setAllowSponsoring(false), GameSettings::isAllowSponsoring), 
    DEATHMATCH(List.of("Teleports all tributes to", "the center with a smaller border"), 
            settings -> settings.setAllowDeathmatch(true), settings -> settings.setAllowDeathmatch(false), GameSettings::isAllowDeathmatch), 
    MUTATION_MAYHEM(),
    
    ;
    
    
    static {
        StringConverters.addConverter(GameModifier.class, new EnumStringConverter<>(GameModifier.class));
    }
    
    private final List<String> description;
    private final Consumer<GameSettings> yesConsumer, noConsumer;
    private final Function<GameSettings, Object> valueFunction;
    
    GameModifier() {
        this(List.of(), null, null, null);
    }
    
    GameModifier(List<String> description, Consumer<GameSettings> yesConsumer, Consumer<GameSettings> noConsumer, Function<GameSettings, Object> valueFunction) {
        this.description = description;
        this.yesConsumer = yesConsumer;
        this.noConsumer = noConsumer;
        this.valueFunction = valueFunction;
    }
    
    public Consumer<GameSettings> getYesConsumer() {
        return yesConsumer;
    }
    
    public Consumer<GameSettings> getNoConsumer() {
        return noConsumer;
    }
    
    public List<String> getDescription() {
        return description;
    }
    
    public Function<GameSettings, Object> getValueFunction() {
        return valueFunction;
    }
}