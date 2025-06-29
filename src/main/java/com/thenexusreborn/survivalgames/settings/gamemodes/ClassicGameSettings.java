package com.thenexusreborn.survivalgames.settings.gamemodes;

import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.enums.*;

@TableName("sggamesettings")
public class ClassicGameSettings extends GameSettings {

    public ClassicGameSettings() {
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
        maxMutationsAllowed = 10;
        useAllMutationTypes = true;
        passUseChance = 0.99;
        passAwardChance = 0.75;
        mutationSpawnDelay = 10; //Default: 15
        unlimitedMutationPasses = true; //Default: false
        allowKillersKiller = true;
        maxMutationAmount = 1;
        mutationsEnabled = true;
        allowRecursiveMutations = false;
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
        allowSingleTribute = false;
        
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
        gracePeriod = false;
        gracePeriodLength = 60;
        automaticGraceperiod = true;
        graceperiodThreshold = 12;
        autoGracePeriodLength = 30;
        
        //Game
        warmupLength = 10; // Default: 30
        gameLength = 10; //Default: 20
        nextGameTimerLength = 10;
        lightning = true;
        fireworks = true;
        sacrifices = true;
        
        //Deathmatch
        deathmatchThreshold = 2; //Default: 4
        deathmatchLength = 5;
        deathmatchWarmupLength = 10;
        deathmatchCountdownLength = 60;
        allowDeathmatch = true;
        deathmatchPlayerCount = true;
        automaticDeathmatchThreshold = true;
    }
}