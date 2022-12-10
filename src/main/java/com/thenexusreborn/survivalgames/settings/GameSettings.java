package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.storage.annotations.*;
import com.thenexusreborn.api.helper.ReflectionHelper;
import com.thenexusreborn.survivalgames.newsettings.collection.SettingList;
import com.thenexusreborn.survivalgames.newsettings.object.impl.GameSetting;

import java.lang.reflect.Field;

@TableInfo("sggamesettings")
public class GameSettings extends SettingList<GameSetting> {
    private int maxPlayers = 24;
    private int maxHealth = 20;
    private int gracePeriodLength = 60;
    private int gameLength = 10;
    private int deathmatchLength = 5;
    private int warmupLength = 30;
    private int deathmatchThreshold = 2; //Default: 4
    private int nextGameStart = 10;
    private int deathmatchTimerLength = 1;
    private int mutationSpawnDelay = 10;
    private double passRewardChance = 0.75;
    private double passUseChance = 0.99;
    private boolean teamingAllowed = true;
    private boolean allowMutations = true;
    private boolean regeneration = true;
    private boolean gracePeriod = false;
    private boolean unlimitedPasses = true; //Default: false
    private boolean timeProgression = false;
    private boolean weatherProgression = false;
    private boolean multiplier = true;
    private boolean sounds = true;
    private boolean giveCredits = true;
    private boolean giveXp = true;
    private boolean useNewLoot = true;
    private boolean allowEnderchests = true;
    private boolean useAllMutations = true; //Default: false
    private ColorMode colorMode = ColorMode.RANK;
    private Time time = Time.NOON;
    private Weather weather = Weather.CLEAR;
    
    public GameSettings() {
        super("game");
    }
    
    public GameSettings(String type) {
        super(type);
    }
    
    public GameSettings setTeamingAllowed(boolean value) {
        this.teamingAllowed = value;
        return this;
    }
    
    public boolean isTeamingAllowed() {
        return teamingAllowed;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getDeathmatchTimerLength() {
        return deathmatchTimerLength;
    }
    
    public GameSettings setDeathmatchTimerLength(int deathmatchTimerLength) {
        this.deathmatchTimerLength = deathmatchTimerLength;
        return this;
    }
    
    public GameSettings setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public GameSettings setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }
    
    public int getGracePeriodLength() {
        return gracePeriodLength;
    }
    
    public GameSettings setGracePeriodLength(int gracePeriodLength) {
        this.gracePeriodLength = gracePeriodLength;
        return this;
    }
    
    public int getGameLength() {
        return gameLength;
    }
    
    public GameSettings setGameLength(int gameLength) {
        this.gameLength = gameLength;
        return this;
    }
    
    public int getDeathmatchLength() {
        return deathmatchLength;
    }
    
    public GameSettings setDeathmatchLength(int deathmatchLength) {
        this.deathmatchLength = deathmatchLength;
        return this;
    }
    
    public int getWarmupLength() {
        return warmupLength;
    }
    
    public GameSettings setWarmupLength(int warmupLength) {
        this.warmupLength = warmupLength;
        return this;
    }
    
    public int getDeathmatchThreshold() {
        return deathmatchThreshold;
    }
    
    public GameSettings setDeathmatchThreshold(int deathmatchThreshold) {
        this.deathmatchThreshold = deathmatchThreshold;
        return this;
    }
    
    public int getNextGameStart() {
        return nextGameStart;
    }
    
    public GameSettings setNextGameStart(int nextGameStart) {
        this.nextGameStart = nextGameStart;
        return this;
    }
    
    public boolean isAllowMutations() {
        return allowMutations;
    }
    
    public GameSettings setAllowMutations(boolean allowMutations) {
        this.allowMutations = allowMutations;
        return this;
    }
    
    public boolean isRegeneration() {
        return regeneration;
    }
    
    public GameSettings setRegeneration(boolean regeneration) {
        this.regeneration = regeneration;
        return this;
    }
    
    public boolean isGracePeriod() {
        return gracePeriod;
    }
    
    public GameSettings setGracePeriod(boolean gracePeriod) {
        this.gracePeriod = gracePeriod;
        return this;
    }
    
    public boolean isUnlimitedPasses() {
        return unlimitedPasses;
    }
    
    public GameSettings setUnlimitedPasses(boolean unlimitedPasses) {
        this.unlimitedPasses = unlimitedPasses;
        return this;
    }
    
    public boolean isTimeProgression() {
        return timeProgression;
    }
    
    public GameSettings setTimeProgression(boolean timeProgression) {
        this.timeProgression = timeProgression;
        return this;
    }
    
    public boolean isWeatherProgression() {
        return weatherProgression;
    }
    
    public GameSettings setWeatherProgression(boolean weatherProgression) {
        this.weatherProgression = weatherProgression;
        return this;
    }
    
    public boolean isMultiplier() {
        return multiplier;
    }
    
    public GameSettings setMultiplier(boolean multiplier) {
        this.multiplier = multiplier;
        return this;
    }
    
    public Time getTime() {
        return time;
    }
    
    public GameSettings setTime(Time time) {
        this.time = time;
        return this;
    }
    
    public Weather getWeather() {
        return weather;
    }
    
    public GameSettings setWeather(Weather weather) {
        this.weather = weather;
        return this;
    }
    
    public boolean isSounds() {
        return sounds;
    }
    
    public GameSettings setSounds(boolean sounds) {
        this.sounds = sounds;
        return this;
    }
    
    public ColorMode getColorMode() {
        return colorMode;
    }
    
    public GameSettings setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
        return this;
    }
    
    public GameSettings setGiveCredits(boolean giveCredits) {
        this.giveCredits = giveCredits;
        return this;
    }
    
    public boolean isGiveCredits() {
        return giveCredits;
    }
    
    public boolean isGiveXp() {
        return giveXp;
    }
    
    public GameSettings setGiveXp(boolean giveXp) {
        this.giveXp = giveXp;
        return this;
    }
    
    public boolean isUseNewLoot() {
        return useNewLoot;
    }
    
    public GameSettings setUseNewLoot(boolean useNewLoot) {
        this.useNewLoot = useNewLoot;
        return this;
    }
    
    public boolean isAllowEnderchests() {
        return allowEnderchests;
    }
    
    public void setAllowEnderchests(boolean allowEnderchests) {
        this.allowEnderchests = allowEnderchests;
    }
    
    public GameSettings setMutationSpawnDelay(int mutationSpawnDelay) {
        this.mutationSpawnDelay = mutationSpawnDelay;
        return this;
    }
    
    public GameSettings setPassRewardChance(double passRewardChance) {
        this.passRewardChance = passRewardChance;
        return this;
    }
    
    public GameSettings setPassUseChance(double passUseChance) {
        this.passUseChance = passUseChance;
        return this;
    }
    
    public int getMutationSpawnDelay() {
        return mutationSpawnDelay;
    }
    
    public double getPassRewardChance() {
        return passRewardChance;
    }
    
    public double getPassUseChance() {
        return passUseChance;
    }
    
    public boolean isUseAllMutations() {
        return useAllMutations;
    }
    
    public void setUseAllMutations(boolean useAllMutations) {
        this.useAllMutations = useAllMutations;
    }
    
    @Override
    public String toString() {
        return "GameSettings{" +
                "maxPlayers=" + maxPlayers +
                ", maxHealth=" + maxHealth +
                ", gracePeriodLength=" + gracePeriodLength +
                ", gameLength=" + gameLength +
                ", deathmatchLength=" + deathmatchLength +
                ", warmupLength=" + warmupLength +
                ", deathmatchThreshold=" + deathmatchThreshold +
                ", nextGameStart=" + nextGameStart +
                ", deathmatchTimerLength=" + deathmatchTimerLength +
                ", mutationSpawnDelay=" + mutationSpawnDelay +
                ", passRewardChance=" + passRewardChance +
                ", passUseChance=" + passUseChance +
                ", teamingAllowed=" + teamingAllowed +
                ", allowMutations=" + allowMutations +
                ", regeneration=" + regeneration +
                ", gracePeriod=" + gracePeriod +
                ", unlimitedPasses=" + unlimitedPasses +
                ", timeProgression=" + timeProgression +
                ", weatherProgression=" + weatherProgression +
                ", multiplier=" + multiplier +
                ", sounds=" + sounds +
                ", giveCredits=" + giveCredits +
                ", giveXp=" + giveXp +
                ", useNewLoot=" + useNewLoot +
                ", allowEnderchests=" + allowEnderchests +
                ", useAllMutations=" + useAllMutations +
                ", colorMode=" + colorMode +
                ", time=" + time +
                ", weather=" + weather +
                '}';
    }
    
    @Override
    public GameSettings clone() {
        GameSettings settings = new GameSettings();
    
        for (Field field : ReflectionHelper.getClassFields(getClass())) {
            field.setAccessible(true);
            try {
                field.set(settings, field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    
        return settings;
    }
}