package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.survivalgames.settings.enums.ColorMode;
import com.thenexusreborn.survivalgames.settings.enums.Time;
import com.thenexusreborn.survivalgames.settings.enums.Weather;

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
    
    //Player
    protected boolean earnNexites = false;
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
    protected boolean useTieredLoot = true;
    
    //Cosmetic
    protected ColorMode colorMode = ColorMode.RANK;
    protected boolean sounds = true;
    
    //World
    protected Weather worldWeather = Weather.CLEAR;
    protected Time worldTime = Time.NOON;
    protected boolean weatherProgression = false;
    protected boolean timeProgression = false;
    protected boolean showBorders = true;
    protected float tntYield = 3.0F;
    protected int tntFuseTicks = 20;
    
    //Game
    protected boolean gracePeriod = false;
    protected int warmupLength = 10; // Default: 30
    protected int gracePeriodLength = 60;
    protected int gameLength = 10; //Default: 20
    protected int nextGameTimerLength = 10;
    
    //Deathmatch
    protected int deathmatchThreshold = 2; //Default: 4
    protected int deathmatchLength = 5;
    protected int deathmatchWarmupLength = 10;
    protected int deathmatchCountdownLength = 60;
    protected boolean allowDeathmatch = true;

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