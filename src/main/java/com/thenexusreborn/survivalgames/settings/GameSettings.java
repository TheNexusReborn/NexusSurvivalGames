package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.survivalgames.settings.enums.ColorMode;
import com.thenexusreborn.survivalgames.settings.enums.Time;
import com.thenexusreborn.survivalgames.settings.enums.Weather;

@TableName("sggamesettings")
public class GameSettings implements Cloneable {

    private int chestRestockInterval = 5;
    private int chestRestockDenomination = 2;
    private boolean chestRestockRelative = true;
    private boolean allowSwagShack = true;
    private int sponsorScoreCost = 100;
    private int sponsorCreditCost = 200;
    private boolean allowSponsoring = true;
    private int combatTagLength = 10;
    private int maxScoreBounty = 10000;
    private int maxCreditBounty = 10000;
    private int assistNexiteGain = 1;
    private int assistXpGain = 1;
    private int assistCreditGain = 1;
    private int killNexiteGain = 2;
    private int killXpGain = 2;
    private int killCreditGain = 2;
    private int winNexiteBaseGain = 10;
    private int winXpBaseGain = 10;
    private int winCreditsBaseGain = 10;
    private int winScoreBaseGain = 50;
    private int maxMutationsAllowed = 10;
    private boolean allowAssists = true;
    private boolean earnNexites = false;
    private int maxMutationAmount = 1;
    private double firstBloodMultiplier = 1.25;
    private double scoreDivisor = 10;
    private float startingSaturation = 5;
    private boolean useAllMutationTypes = true;
    private double passUseChance = 0.99;
    private double passAwardChance = 0.75;
    private int mutationSpawnDelay = 10; //Default: 15
    private boolean enderchestsEnabled = true;
    private boolean useTieredLoot = true;
    private boolean earnNetworkXp = true;
    private boolean earnCredits = true;
    private ColorMode colorMode = ColorMode.RANK;
    private boolean sounds = true;
    private Weather worldWeather = Weather.CLEAR;
    private Time worldTime = Time.NOON;
    private boolean applyMultipliers = true;
    private boolean weatherProgression = false;
    private boolean timeProgression = false;
    private boolean unlimitedMutationPasses = true; //Default: false
    private boolean gracePeriod = false;
    private boolean regeneration = true;
    private boolean mutationsEnabled = true;
    private int nextGameTimerLength = 10;
    private int deathmatchThreshold = 2; //Default: 4
    private int warmupLength = 10; // Default: 30
    private int deathmatchLength = 5;
    private int gameLength = 10; //Default: 20
    private int gracePeriodLength = 60;
    private int maxHealth = 20;
    private int deathmatchWarmupLength = 10;
    private int deathmatchCountdownLength = 60;
    private int maxTeamAmount = 2;
    private boolean allowTeaming = true;
    private float tntYield = 3.0F;
    private int tntFuseTicks = 20;

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
    
    public boolean isMultiplier() {
        return applyMultipliers;
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
    
    public boolean isUseNewLoot() {
        return useTieredLoot;
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

    @Override
    public GameSettings clone() {
        try {
            return (GameSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            return new GameSettings();
        }
    }
}