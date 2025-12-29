package com.thenexusreborn.survivalgames.settings.gamemodes;

import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.enums.*;

@TableName("sggamesettings")
public class MutationMayhemGameSettings extends GameSettings {

    public MutationMayhemGameSettings() {
        //Chest Restock
        chestRestockInterval = 5;
        chestRestockDenomination = 2;
        chestRestockRelative = true;
        
        //Swag Shack
        allowSwagShack = true;
        
        //Sponsoring
        sponsorScoreCost = 100;
        sponsorCreditCost = 200;
        allowSponsoring = true;
        maxSponsorships = 1;
        
        //Combat Tag
        combatTagLength = 10;
        allowCombatTag = true;
        
        //Bounty
        maxScoreBounty = 10000;
        maxCreditBounty = 10000;
        allowBounties = true;
        
        //Assister Stat
        assistNexiteGain = 1;
        assistXpGain = 1;
        assistCreditGain = 1;
        allowAssists = true;
        
        //Killer Stat
        killNexiteGain = 2;
        killXpGain = 2;
        killCreditGain = 2;
        
        //Win Stat
        winNexiteBaseGain = 10;
        winXpBaseGain = 10;
        winCreditsBaseGain = 10;
        winScoreBaseGain = 50;
        
        //Mutations
        maxMutationsAllowed = 24;
        useAllMutationTypes = true;
        passUseChance = 0.99;
        passAwardChance = 0.75;
        mutationSpawnDelay = 10; //Default: 15
        unlimitedMutationPasses = true; //Default: false
        allowKillersKiller = true;
        maxMutationAmount = 3;
        mutationsEnabled = true;
        allowRecursiveMutations = true;
        allowRoddingMutations = false;
        chickenMaxAmmo = -1;
        
        //Player
        earnNexites = false;
        firstBloodMultiplier = 1.25;
        scoreDivisor = 10;
        startingSaturation = 5;
        earnNetworkXp = true;
        earnCredits = true;
        regeneration = true;
        maxHealth = 20;
        maxTeamAmount = 2;
        allowTeaming = true;
        tributesSeeSpectatorChat = true;
        allowSingleTribute = true;
        
        //Loot
        enderchestsEnabled = true;
        lootMode = LootMode.TIERED;
        cornucopiaTier = "tierTwo";
        regularTier = "tierOne";
        
        //Cosmetic
        colorMode = ColorMode.RANK;
        sounds = true;
        
        //World
        worldWeather = Weather.CLEAR;
        worldTime = Time.NOON;
        weatherProgression = false;
        timeProgression = false;
        showBorders = true;
        tntYield = 3.0F;
        tntFuseTicks = 20;
        
        //Graceperiod
        gracePeriod = true;
        gracePeriodLength = 60;
        automaticGraceperiod = false;
        graceperiodThreshold = 24;
        autoGracePeriodLength = 30;
        
        //Game
        warmupLength = 10;
        gameLength = 15;
        nextGameTimerLength = 10;
        lightning = true;
        fireworks = true;
        sacrifices = false;
        
        //Deathmatch
        deathmatchThreshold = 2; //Default: 4
        deathmatchLength = 5;
        deathmatchWarmupLength = 10;
        deathmatchCountdownLength = 60;
        allowDeathmatch = false;
        deathmatchPlayerCount = false;
        automaticDeathmatchThreshold = false;
    }
}