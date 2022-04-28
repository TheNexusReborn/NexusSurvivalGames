package com.thenexusreborn.survivalgames.settings;

public class GameSettings {
    
    private int maxPlayers = 24;
    private int maxHealth = 20;
    private int gracePeriodLength = 60;
    private int gameLength = 20;
    private int deathmatchLength = 5;
    private int warmupLength = 30;
    private int deathmatchThreshold = 4;
    private int pearlCooldown = 5;
    private int mutationDelay = 2;
    private int nextGameStart = 10;
    private int deathmatchTimerLength = 1;
    private boolean mutations = false;
    private boolean regeneration = true;
    private boolean gracePeriod = false;
    private boolean unlimitedPasses = false;
    private boolean timeProgression = false;
    private boolean weatherProgression = false;
    private boolean multiplier = true;
    private boolean fishingrods = true;
    private boolean sounds = true;
    private boolean giveCredits = true;
    private boolean giveXp = true;
    private ColorMode colorMode = ColorMode.RANK;
    private Time time = Time.NOON;
    private Weather weather = Weather.CLEAR;
    
    public boolean isFishingrods() {
        return fishingrods;
    }
    
    public void setFishingrods(boolean fishingrods) {
        this.fishingrods = fishingrods;
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
    
    public int getPearlCooldown() {
        return pearlCooldown;
    }
    
    public GameSettings setPearlCooldown(int pearlCooldown) {
        this.pearlCooldown = pearlCooldown;
        return this;
    }
    
    public int getMutationDelay() {
        return mutationDelay;
    }
    
    public GameSettings setMutationDelay(int mutationDelay) {
        this.mutationDelay = mutationDelay;
        return this;
    }
    
    public int getNextGameStart() {
        return nextGameStart;
    }
    
    public GameSettings setNextGameStart(int nextGameStart) {
        this.nextGameStart = nextGameStart;
        return this;
    }
    
    public boolean isMutations() {
        return mutations;
    }
    
    public GameSettings setMutations(boolean mutations) {
        this.mutations = mutations;
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
}
