package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.survivalgames.settings.enums.*;

@TableName("sggamesettings")
public class GameSettings implements Cloneable, ISettings {

    //Chest Restock
    protected int chestRestockInterval = 5;
    protected int chestRestockDenomination = 2;
    protected boolean chestRestockRelative = true;
    
    //Swag Shack
    protected boolean allowSwagShack = true;
    
    //Sponsoring
    protected int sponsorScoreCost = 100;
    protected int sponsorCreditCost = 200;
    protected boolean allowSponsoring = true;
    protected int maxSponsorships = 1;
    
    //Combat Tag
    protected int combatTagLength = 10;
    protected boolean allowCombatTag = true;
    
    //Bounty
    protected int maxScoreBounty = 10000;
    protected int maxCreditBounty = 10000;
    protected boolean allowBounties = true;
    
    //Assister Stat
    protected int assistNexiteGain = 1;
    protected int assistXpGain = 1;
    protected int assistCreditGain = 1;
    protected boolean allowAssists = true;
    
    //Killer Stat
    protected int killNexiteGain = 2;
    protected int killXpGain = 2;
    protected int killCreditGain = 2;
    
    //Win Stat
    protected int winNexiteBaseGain = 10;
    protected int winXpBaseGain = 10;
    protected int winCreditsBaseGain = 10;
    protected int winScoreBaseGain = 50;
    
    //Mutations
    protected int maxMutationsAllowed = 10;
    protected boolean useAllMutationTypes = true;
    protected double passUseChance = 0.99;
    protected double passAwardChance = 0.75;
    protected int mutationSpawnDelay = 10; //Default: 15
    protected boolean unlimitedMutationPasses = true; //Default: false
    protected boolean allowKillersKiller = true;
    protected int maxMutationAmount = 1;
    protected boolean mutationsEnabled = true;
    protected boolean allowRecursiveMutations;
    protected long chickenEggLaunchCooldown = 100;
    
    //Player
    protected boolean earnNexites;
    protected double firstBloodMultiplier = 1.25;
    protected double scoreDivisor = 10;
    protected float startingSaturation = 5;
    protected boolean earnNetworkXp = true;
    protected boolean earnCredits = true;
    protected boolean regeneration = true;
    protected int maxHealth = 20;
    protected int maxTeamAmount = 2;
    protected boolean allowTeaming = true;
    protected boolean tributesSeeSpectatorChat = true;
    
    //Loot
    protected boolean enderchestsEnabled = true;
    protected LootMode lootMode = LootMode.TIERED;
    
    //Cosmetic
    protected ColorMode colorMode = ColorMode.RANK;
    protected boolean sounds = true;
    
    //World
    protected Weather worldWeather = Weather.CLEAR;
    protected Time worldTime = Time.NOON;
    protected boolean weatherProgression;
    protected boolean timeProgression;
    protected boolean showBorders = true;
    protected float tntYield = 3.0F;
    protected int tntFuseTicks = 20;
    
    //Game
    protected boolean gracePeriod;
    protected int warmupLength = 10; // Default: 30
    protected int gracePeriodLength = 60;
    protected int gameLength = 10; //Default: 20
    protected int nextGameTimerLength = 10;
    protected boolean lightning;
    protected boolean fireworks;
    
    //Deathmatch
    protected int deathmatchThreshold = 2; //Default: 4
    protected int deathmatchLength = 5;
    protected int deathmatchWarmupLength = 10;
    protected int deathmatchCountdownLength = 60;
    protected boolean allowDeathmatch = true;
    
    public long getChickenEggLaunchCooldown() {
        return chickenEggLaunchCooldown;
    }
    
    public boolean isFireworks() {
        return fireworks;
    }
    
    public boolean isAllowRecursiveMutations() {
        return allowRecursiveMutations;
    }
    
    public boolean isAllowDeathmatch() {
        return allowDeathmatch;
    }

    public boolean isAllowCombatTag() {
        return allowCombatTag;
    }

    public int getMaxSponsorships() {
        return maxSponsorships;
    }

    public boolean isShowBorders() {
        return showBorders;
    }

    public boolean isAllowBounties() {
        return allowBounties;
    }

    public void setGracePeriod(boolean gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public void setGracePeriodLength(int gracePeriodLength) {
        this.gracePeriodLength = gracePeriodLength;
    }

    public boolean canTributesSeeSpectatorChat() {
        return tributesSeeSpectatorChat;
    }

    public boolean isAllowKillersKiller() {
        return allowKillersKiller;
    }

    public int getTntFuseTicks() {
        return tntFuseTicks;
    }

    public float getTntYield() {
        return tntYield;
    }

    public boolean isTeamingAllowed() {
        return allowTeaming;
    }
    
    public int getMaxTeamAmount() {
        return maxTeamAmount;
    }
    
    public int getDeathmatchTimerLength() {
        return deathmatchCountdownLength;
    }
    
    public int getDeathmatchWarmupLength() {
        return deathmatchWarmupLength;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public int getGracePeriodLength() {
        return gracePeriodLength;
    }
    
    public int getGameLength() {
        return gameLength;
    }
    
    public int getDeathmatchLength() {
        return deathmatchLength;
    }
    
    public int getWarmupLength() {
        return warmupLength;
    }
    
    public int getDeathmatchThreshold() {
        return deathmatchThreshold;
    }
    
    public int getNextGameStart() {
        return nextGameTimerLength;
    }
    
    public boolean isAllowMutations() {
        return mutationsEnabled;
    }
    
    public boolean isRegeneration() {
        return regeneration;
    }
    
    public boolean isGracePeriod() {
        return gracePeriod;
    }
    
    public boolean isUnlimitedPasses() {
        return unlimitedMutationPasses;
    }
    
    public boolean isTimeProgression() {
        return timeProgression;
    }
    
    public boolean isWeatherProgression() {
        return weatherProgression;
    }
    
    public Time getTime() {
        return worldTime;
    }
    
    public Weather getWeather() {
        return worldWeather;
    }
    
    public boolean isSounds() {
        return sounds;
    }
    
    public ColorMode getColorMode() {
        return colorMode;
    }
    
    public boolean isGiveCredits() {
        return earnCredits;
    }
    
    public boolean isGiveXp() {
        return earnNetworkXp;
    }
    
    public LootMode getLootMode() {
        return lootMode;
    }
    
    public boolean isAllowEnderchests() {
        return enderchestsEnabled;
    }
    
    public int getMutationSpawnDelay() {
        return mutationSpawnDelay;
    }
    
    public double getPassRewardChance() {
        return passAwardChance;
    }
    
    public double getPassUseChance() {
        return passUseChance;
    }
    
    public boolean isUseAllMutations() {
        return useAllMutationTypes;
    }
    
    public float getStartingSaturation() {
        return startingSaturation;
    }
    
    public double getScoreDivisor() {
        return scoreDivisor;
    }
    
    public double getFirstBloodMultiplier() {
        return firstBloodMultiplier;
    }
    
    public int getMaxMutationAmount() {
        return maxMutationAmount;
    }
    
    public boolean isEarnNexites() {
        return earnNexites;
    }
    
    public boolean isAllowAssists() {
        return allowAssists;
    }
    
    public int getMaxMutationsAllowed() {
        return maxMutationsAllowed;
    }
    
    public int getWinScoreBaseGain() {
        return winScoreBaseGain;
    }
    
    public int getWinCreditsBaseGain() {
        return winCreditsBaseGain;
    }
    
    public int getWinXPBaseGain() {
        return winXpBaseGain;
    }
    
    public int getWinNexiteBaseGain() {
        return winNexiteBaseGain;
    }
    
    public int getKillCreditGain() {
        return killCreditGain;
    }
    
    public int getKillXPGain() {
        return killXpGain;
    }
    
    public int getKillNexiteGain() {
        return killNexiteGain;
    }
    
    public int getAssistCreditGain() {
        return assistCreditGain;
    }
    
    public int getAssistXPGain() {
        return assistXpGain;
    }
    
    public int getAssistNexiteGain() {
        return assistNexiteGain;
    }
    
    public int getMaxCreditBounty() {
        return maxCreditBounty;
    }
    
    public int getMaxScoreBounty() {
        return maxScoreBounty;
    }
    
    public int getCombatTagLength() {
        return combatTagLength;
    }
    
    public boolean isAllowSponsoring() {
        return allowSponsoring;
    }
    
    public int getSponsorCreditCost() {
        return sponsorCreditCost;
    }
    
    public int getSponsorScoreCost() {
        return sponsorScoreCost;
    }
    
    public boolean isAllowSwagShack() {
        return allowSwagShack;
    }
    
    public boolean isChestRestockRelative() {
        return chestRestockRelative;
    }
    
    public int getChestRestockDenomination() {
        return chestRestockDenomination;
    }
    
    public int getChestRestockInterval() {
        return chestRestockInterval;
    }
    
    public GameSettings setChestRestockInterval(int chestRestockInterval) {
        this.chestRestockInterval = chestRestockInterval;
        return this;
    }
    
    public GameSettings setChestRestockDenomination(int chestRestockDenomination) {
        this.chestRestockDenomination = chestRestockDenomination;
        return this;
    }
    
    public GameSettings setChestRestockRelative(boolean chestRestockRelative) {
        this.chestRestockRelative = chestRestockRelative;
        return this;
    }
    
    public GameSettings setAllowSwagShack(boolean allowSwagShack) {
        this.allowSwagShack = allowSwagShack;
        return this;
    }
    
    public GameSettings setSponsorScoreCost(int sponsorScoreCost) {
        this.sponsorScoreCost = sponsorScoreCost;
        return this;
    }
    
    public GameSettings setSponsorCreditCost(int sponsorCreditCost) {
        this.sponsorCreditCost = sponsorCreditCost;
        return this;
    }
    
    public GameSettings setAllowSponsoring(boolean allowSponsoring) {
        this.allowSponsoring = allowSponsoring;
        return this;
    }
    
    public GameSettings setMaxSponsorships(int maxSponsorships) {
        this.maxSponsorships = maxSponsorships;
        return this;
    }
    
    public GameSettings setCombatTagLength(int combatTagLength) {
        this.combatTagLength = combatTagLength;
        return this;
    }
    
    public GameSettings setAllowCombatTag(boolean allowCombatTag) {
        this.allowCombatTag = allowCombatTag;
        return this;
    }
    
    public GameSettings setMaxScoreBounty(int maxScoreBounty) {
        this.maxScoreBounty = maxScoreBounty;
        return this;
    }
    
    public GameSettings setMaxCreditBounty(int maxCreditBounty) {
        this.maxCreditBounty = maxCreditBounty;
        return this;
    }
    
    public GameSettings setAllowBounties(boolean allowBounties) {
        this.allowBounties = allowBounties;
        return this;
    }
    
    public GameSettings setAssistNexiteGain(int assistNexiteGain) {
        this.assistNexiteGain = assistNexiteGain;
        return this;
    }
    
    public GameSettings setAssistXpGain(int assistXpGain) {
        this.assistXpGain = assistXpGain;
        return this;
    }
    
    public GameSettings setAssistCreditGain(int assistCreditGain) {
        this.assistCreditGain = assistCreditGain;
        return this;
    }
    
    public GameSettings setAllowAssists(boolean allowAssists) {
        this.allowAssists = allowAssists;
        return this;
    }
    
    public GameSettings setKillNexiteGain(int killNexiteGain) {
        this.killNexiteGain = killNexiteGain;
        return this;
    }
    
    public GameSettings setKillXpGain(int killXpGain) {
        this.killXpGain = killXpGain;
        return this;
    }
    
    public GameSettings setKillCreditGain(int killCreditGain) {
        this.killCreditGain = killCreditGain;
        return this;
    }
    
    public GameSettings setWinNexiteBaseGain(int winNexiteBaseGain) {
        this.winNexiteBaseGain = winNexiteBaseGain;
        return this;
    }
    
    public GameSettings setWinXpBaseGain(int winXpBaseGain) {
        this.winXpBaseGain = winXpBaseGain;
        return this;
    }
    
    public GameSettings setWinCreditsBaseGain(int winCreditsBaseGain) {
        this.winCreditsBaseGain = winCreditsBaseGain;
        return this;
    }
    
    public GameSettings setWinScoreBaseGain(int winScoreBaseGain) {
        this.winScoreBaseGain = winScoreBaseGain;
        return this;
    }
    
    public GameSettings setMaxMutationsAllowed(int maxMutationsAllowed) {
        this.maxMutationsAllowed = maxMutationsAllowed;
        return this;
    }
    
    public GameSettings setUseAllMutationTypes(boolean useAllMutationTypes) {
        this.useAllMutationTypes = useAllMutationTypes;
        return this;
    }
    
    public GameSettings setPassUseChance(double passUseChance) {
        this.passUseChance = passUseChance;
        return this;
    }
    
    public GameSettings setPassAwardChance(double passAwardChance) {
        this.passAwardChance = passAwardChance;
        return this;
    }
    
    public GameSettings setMutationSpawnDelay(int mutationSpawnDelay) {
        this.mutationSpawnDelay = mutationSpawnDelay;
        return this;
    }
    
    public GameSettings setUnlimitedMutationPasses(boolean unlimitedMutationPasses) {
        this.unlimitedMutationPasses = unlimitedMutationPasses;
        return this;
    }
    
    public GameSettings setAllowKillersKiller(boolean allowKillersKiller) {
        this.allowKillersKiller = allowKillersKiller;
        return this;
    }
    
    public GameSettings setMaxMutationAmount(int maxMutationAmount) {
        this.maxMutationAmount = maxMutationAmount;
        return this;
    }
    
    public GameSettings setMutationsEnabled(boolean mutationsEnabled) {
        this.mutationsEnabled = mutationsEnabled;
        return this;
    }
    
    public GameSettings setAllowRecursiveMutations(boolean allowRecursiveMutations) {
        this.allowRecursiveMutations = allowRecursiveMutations;
        return this;
    }
    
    public GameSettings setEarnNexites(boolean earnNexites) {
        this.earnNexites = earnNexites;
        return this;
    }
    
    public GameSettings setFirstBloodMultiplier(double firstBloodMultiplier) {
        this.firstBloodMultiplier = firstBloodMultiplier;
        return this;
    }
    
    public GameSettings setScoreDivisor(double scoreDivisor) {
        this.scoreDivisor = scoreDivisor;
        return this;
    }
    
    public GameSettings setStartingSaturation(float startingSaturation) {
        this.startingSaturation = startingSaturation;
        return this;
    }
    
    public GameSettings setEarnNetworkXp(boolean earnNetworkXp) {
        this.earnNetworkXp = earnNetworkXp;
        return this;
    }
    
    public GameSettings setEarnCredits(boolean earnCredits) {
        this.earnCredits = earnCredits;
        return this;
    }
    
    public GameSettings setRegeneration(boolean regeneration) {
        this.regeneration = regeneration;
        return this;
    }
    
    public GameSettings setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }
    
    public GameSettings setMaxTeamAmount(int maxTeamAmount) {
        this.maxTeamAmount = maxTeamAmount;
        return this;
    }
    
    public GameSettings setAllowTeaming(boolean allowTeaming) {
        this.allowTeaming = allowTeaming;
        return this;
    }
    
    public GameSettings setTributesSeeSpectatorChat(boolean tributesSeeSpectatorChat) {
        this.tributesSeeSpectatorChat = tributesSeeSpectatorChat;
        return this;
    }
    
    public GameSettings setEnderchestsEnabled(boolean enderchestsEnabled) {
        this.enderchestsEnabled = enderchestsEnabled;
        return this;
    }
    
    public GameSettings setLootMode(LootMode lootMode) {
        this.lootMode = lootMode;
        return this;
    }
    
    public GameSettings setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
        return this;
    }
    
    public GameSettings setSounds(boolean sounds) {
        this.sounds = sounds;
        return this;
    }
    
    public GameSettings setWorldWeather(Weather worldWeather) {
        this.worldWeather = worldWeather;
        return this;
    }
    
    public GameSettings setWorldTime(Time worldTime) {
        this.worldTime = worldTime;
        return this;
    }
    
    public GameSettings setWeatherProgression(boolean weatherProgression) {
        this.weatherProgression = weatherProgression;
        return this;
    }
    
    public GameSettings setTimeProgression(boolean timeProgression) {
        this.timeProgression = timeProgression;
        return this;
    }
    
    public GameSettings setShowBorders(boolean showBorders) {
        this.showBorders = showBorders;
        return this;
    }
    
    public GameSettings setTntYield(float tntYield) {
        this.tntYield = tntYield;
        return this;
    }
    
    public GameSettings setTntFuseTicks(int tntFuseTicks) {
        this.tntFuseTicks = tntFuseTicks;
        return this;
    }
    
    public GameSettings setWarmupLength(int warmupLength) {
        this.warmupLength = warmupLength;
        return this;
    }
    
    public GameSettings setGameLength(int gameLength) {
        this.gameLength = gameLength;
        return this;
    }
    
    public GameSettings setNextGameTimerLength(int nextGameTimerLength) {
        this.nextGameTimerLength = nextGameTimerLength;
        return this;
    }
    
    public GameSettings setDeathmatchThreshold(int deathmatchThreshold) {
        this.deathmatchThreshold = deathmatchThreshold;
        return this;
    }
    
    public GameSettings setDeathmatchLength(int deathmatchLength) {
        this.deathmatchLength = deathmatchLength;
        return this;
    }
    
    public GameSettings setDeathmatchWarmupLength(int deathmatchWarmupLength) {
        this.deathmatchWarmupLength = deathmatchWarmupLength;
        return this;
    }
    
    public GameSettings setDeathmatchCountdownLength(int deathmatchCountdownLength) {
        this.deathmatchCountdownLength = deathmatchCountdownLength;
        return this;
    }
    
    public GameSettings setAllowDeathmatch(boolean allowDeathmatch) {
        this.allowDeathmatch = allowDeathmatch;
        return this;
    }
    
    public GameSettings setFireworks(boolean fireworks) {
        this.fireworks = fireworks;
        return this;
    }
    
    public boolean isLightning() {
        return lightning;
    }
    
    @Override
    public GameSettings clone() {
        try {
            return (GameSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            return new GameSettings();
        }
    }
}