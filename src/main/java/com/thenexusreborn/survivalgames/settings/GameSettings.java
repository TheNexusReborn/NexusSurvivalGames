package com.thenexusreborn.survivalgames.settings;

import com.stardevllc.time.Duration;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.survivalgames.settings.enums.*;

@TableName("sggamesettings")
public class GameSettings implements Cloneable, ISettings {
    
    public static class ChestRestock {
        public boolean enabled = true;
        public int interval = 5;
        public int divisor = 2;
        public boolean relative = true;
    }
    
    public ChestRestock chestRestock = new ChestRestock();
    
    public static class SwagShack {
        public boolean enabled = true;
        
        public static class Currency {
            public boolean credits = true;
            public boolean score = true;
        }
        
        public Currency currency = new Currency();
    }

    public SwagShack swagShack = new SwagShack();
    
    public static class Sponsoring {
        public boolean enabled = true;
        public int maxPerPlayer = 1;
        public int maxPerTribute = -1; //-1 signifies infinite
        
        public static class Costs {
            public int score = 100;
            public int credit = 200;
        }
        
        public Costs costs = new Costs();
    }
    
    public Sponsoring sponsoring = new Sponsoring();
    
    public static class CombatTag {
        public boolean enabled = true;
        public Duration length = new Duration(TimeUnit.SECONDS, 10);
    }
    
    public CombatTag combatTag = new CombatTag();
    
    public static class Bounties {
        public boolean enabled = true;
        public int maxScore = 10000;
        public int maxCredit = 10000;
    }
    
    public Bounties bounties = new Bounties();
    
    public static class Assists {
        public boolean enabled = true;
        
        public static class XP {
            public boolean enabled = true;
            public int amount = 1;
        }
        public XP xp = new XP();
        
        public static class Credits {
            public boolean enabled = true;
            public int amount = 1;
        }
        public Credits credits = new Credits();
        
        public static class Score {
            public boolean enabled = true;
            public boolean relative = true; //False means flat amount
            public int amount = 5; //If relative is true, the score is divided by this. If it is false, the score is subtracted by this
        }
        public Score score = new Score();
    }
    
    public Assists assists = new Assists();
    
    public static class Killer {
        public static class XP {
            public boolean enabled = true;
            public int amount = 2;
        }
        public XP xp = new XP();
        
        public static class Credits {
            public boolean enabled = true;
            public int amount = 2;
        }
        public Credits credits = new Credits();
        
        public static class Score {
            public boolean enabled = true;
            public boolean relative = true; //False means flat amount
            public int amount = 10; //If relative is true, the score is divided by this. If it is false, the score is subtracted by this
            public double firstBloodMultiplier = 1.25;
        }
        public Score score = new Score();
    }
    
    public Killer killer = new Killer();
    
    public static class Winner {
        public static class XP {
            public boolean enabled = true;
            public int amount = 10;
        }
        public XP xp = new XP();
        
        public static class Credits {
            public boolean enabled = true;
            public int amount = 10;
        }
        public Credits credits = new Credits();
        
        public static class Score {
            public boolean enabled = true;
            public int amount = 50;
        }
        public Score score = new Score();
    }
    
    public Winner winner = new Winner();
    
    public static class Mutations {
        public boolean enabled = true;
        public boolean typesLocked; //Default is true
        public int maxSimultaneous = 10;
        public int maxTimesPerPlayer = 1;
        public boolean recursiveRevenge;
        public boolean recursiveKillers = true;
        public Duration spawnDelay = new Duration(TimeUnit.SECONDS, 10);
        public boolean waterMitigation = true;
        
        public static class Passes {
            public boolean enabled = true;
            public boolean unlimited = true; //Default is false
            public double useChance = 0.99;
            public double awardChance = 0.75;
        }
        public Passes passes = new Passes();
        
        public static class Chicken {
            public Duration launchCooldown = new Duration(TimeUnit.MILLISECONDS, 200);
            public int maxAmmo = -1; //-1 means infinite
        }
        public Chicken chicken = new Chicken();
    }
    
    public Mutations mutations = new Mutations();
    
    public static class Loot {
        public LootMode mode = LootMode.TIERED;
        public boolean enderchests = true;
        
        public static class Tiers {
            public String cornucopia = "tierTwo";
            public String regular = "tierOne";
            public String deathmatch = "tierFour";
        }
        public Tiers tiers = new Tiers();
    }
    
    public Loot loot = new Loot();
    
    public static class World {
        public static class Weather {
            public boolean progression;
            public WeatherType type = WeatherType.CLEAR;
        }
        public Weather weather = new Weather();
        
        public static class Time {
            public boolean progression;
            public WorldTime value = WorldTime.NOON;
        }
        public Time time = new Time();
        
        public boolean showBorders = true;
        
        public static class TNT {
            public float yield = 3.0F;
            public int fuseTicks = 20;
        }
        public TNT tnt = new TNT();
    }
    
    public World world = new World();
    
    public static class Graceperiod {
        public boolean enabled;
        public Duration length = new Duration(TimeUnit.SECONDS, 60);
        
        public static class Auto {
            public boolean enabled = true;
            public int threshold = 12;
            public Duration length = new Duration(TimeUnit.SECONDS, 60);
        }
        
        public Auto auto = new Auto();
    }
    
    public Graceperiod graceperiod = new Graceperiod();
    
    public static class Cosmetics {
        public boolean lightning = true;
        public boolean fireworks = true;
        public ColorMode colorMode = ColorMode.RANK;
        public boolean sounds = true;
    }
    
    public Cosmetics cosmetics = new Cosmetics();
    
    public static class Deathmatch {
        public boolean enabled = true;
        public Duration length = new Duration(TimeUnit.MINUTES, 5);
        
        public static class Trigger {
            public boolean enabled = true;
            public boolean autoAdjust = true;
            public int threshold = 2; //Default 4
            public Duration length = new Duration(TimeUnit.MINUTES, 1);
        }
        public Trigger trigger = new Trigger();
        
        public static class Warmup {
            public boolean enabled = true;
            public Duration length = new Duration(TimeUnit.SECONDS, 10);
        }
        public Warmup warmup = new Warmup();
    }
    
    public Deathmatch deathmatch = new Deathmatch();
    
    //Player
    
    public static class Player {
        public float startingSaturation = 5;
        public boolean regeneration = true;
        public int maxHealth = 20;
        public static class Teams {
            public boolean enabled = true;
            public int max = 2;
        }
        public Teams teams = new Teams();
    }
    
    public Player player = new Player();
    
    //Game
    public int warmupLength = 10; // Default: 30
    public int gameLength = 10; //Default: 20
    public int nextGameTimerLength = 10;
    public boolean allowSingleTribute;
    public boolean tributesSeeSpectatorChat = true;
    
    @Override
    public GameSettings clone() {
        try {
            return (GameSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            return new GameSettings();
        }
    }
}