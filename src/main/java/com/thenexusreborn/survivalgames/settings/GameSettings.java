package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.settings.collection.SettingList;
import com.thenexusreborn.survivalgames.settings.object.Setting;
import com.thenexusreborn.survivalgames.settings.object.enums.*;
import com.thenexusreborn.survivalgames.settings.object.impl.*;

@TableName("sggamesettings")
public class GameSettings extends SettingList<GameSetting> {
    public GameSettings() {
        super("game");
    }
    
    public GameSettings(String type) {
        super(type);
    }
    
    @Override
    public GameSetting createSetting(String name) {
        Setting.Info info = SurvivalGames.getPlugin(SurvivalGames.class).getGameSettingRegistry().get(name);
        return new GameSetting(info, getCategory(), info.getDefaultValue());
    }
    
    public boolean isTeamingAllowed() {
        return getValue("allow_teaming").getAsBoolean();
    }
    
    public int getMaxTeamAmount() {
        return getValue("max_team_amount").getAsInt();
    }
    
    public int getDeathmatchTimerLength() {
        return getValue("deathmatch_countdown_length").getAsInt();
    }
    
    public int getDeathmatchWarmupLength() {
        return getValue("deathmatch_warmup_length").getAsInt();
    }
    
    public int getMaxHealth() {
        return getValue("max_health").getAsInt();
    }
    
    public int getGracePeriodLength() {
        return getValue("grace_period_length").getAsInt();
    }
    
    public int getGameLength() {
        return getValue("game_length").getAsInt();
    }
    
    public int getDeathmatchLength() {
        return getValue("deathmatch_length").getAsInt();
    }
    
    public int getWarmupLength() {
        return getValue("warmup_length").getAsInt();
    }
    
    public int getDeathmatchThreshold() {
        return getValue("deathmatch_threshold").getAsInt();
    }
    
    public int getNextGameStart() {
        return getValue("next_game_timer_length").getAsInt();
    }
    
    public boolean isAllowMutations() {
        return getValue("mutations_enabled").getAsBoolean();
    }
    
    public boolean isRegeneration() {
        return getValue("regeneration").getAsBoolean();
    }
    
    public boolean isGracePeriod() {
        return getValue("grace_period").getAsBoolean();
    }
    
    public boolean isUnlimitedPasses() {
        return getValue("unlimited_mutation_passes").getAsBoolean();
    }
    
    public boolean isTimeProgression() {
        return getValue("time_progression").getAsBoolean();
    }
    
    public boolean isWeatherProgression() {
        return getValue("weather_progression").getAsBoolean();
    }
    
    public boolean isMultiplier() {
        return getValue("apply_multipliers").getAsBoolean();
    }
    
    public Time getTime() {
        return getValue("world_time").getAsEnum(Time.class);
    }
    
    public Weather getWeather() {
        return getValue("world_weather").getAsEnum(Weather.class);
    }
    
    public boolean isSounds() {
        return getValue("sounds").getAsBoolean();
    }
    
    public ColorMode getColorMode() {
        return getValue("color_mode").getAsEnum(ColorMode.class);
    }
    
    public boolean isGiveCredits() {
        return getValue("earn_credits").getAsBoolean();
    }
    
    public boolean isGiveXp() {
        return getValue("earn_network_xp").getAsBoolean();
    }
    
    public boolean isUseNewLoot() {
        return getValue("use_tiered_loot").getAsBoolean();
    }
    
    public boolean isAllowEnderchests() {
        return getValue("enderchests_enabled").getAsBoolean();
    }
    
    public int getMutationSpawnDelay() {
        return getValue("mutation_spawn_delay").getAsInt();
    }
    
    public double getPassRewardChance() {
        return getValue("pass_award_chance").getAsDouble();
    }
    
    public double getPassUseChance() {
        return getValue("pass_use_chance").getAsDouble();
    }
    
    public boolean isUseAllMutations() {
        return getValue("use_all_mutation_types").getAsBoolean();
    }
    
    public float getStartingSaturation() {
        return (float) getValue("starting_saturation").getAsDouble();
    }
    
    public double getScoreDivisor() {
        return getValue("score_divisor").getAsDouble();
    }
    
    public double getFirstBloodMultiplier() {
        return getValue("first_blood_multiplier").getAsDouble();
    }
    
    public int getMaxMutationAmount() {
        return getValue("max_mutation_amount").getAsInt();
    }
    
    public boolean isEarnNexites() {
        return getValue("earn_nexites").getAsBoolean();
    }
    
    public boolean isAllowAssists() {
        return getValue("allow_assists").getAsBoolean();
    }
    
    public int getMaxMutationsAllowed() {
        return getValue("max_mutations_allowed").getAsInt();
    }
    
    public int getWinScoreBaseGain() {
        return getValue("win_score_base_gain").getAsInt();
    }
    
    public int getWinCreditsBaseGain() {
        return getValue("win_credits_base_gain").getAsInt();
    }
    
    public int getWinXPBaseGain() {
        return getValue("win_xp_base_gain").getAsInt();
    }
    
    public int getWinNexiteBaseGain() {
        return getValue("win_nexite_base_gain").getAsInt();
    }
    
    public int getKillCreditGain() {
        return getValue("kill_credit_gain").getAsInt();
    }
    
    public int getKillXPGain() {
        return getValue("kill_xp_gain").getAsInt();
    }
    
    public int getKillNexiteGain() {
        return getValue("kill_nexite_gain").getAsInt();
    }
    
    public int getAssistCreditGain() {
        return getValue("assist_credit_gain").getAsInt();
    }
    
    public int getAssistXPGain() {
        return getValue("assist_xp_gain").getAsInt();
    }
    
    public int getAssistNexiteGain() {
        return getValue("assist_nexite_gain").getAsInt();
    }
    
    public int getMaxCreditBounty() {
        return getValue("max_credit_bounty").getAsInt();
    }
    
    public int getMaxScoreBounty() {
        return getValue("max_score_bounty").getAsInt();
    }
    
    public int getCombatTagLength() {
        return getValue("combat_tag_length").getAsInt();
    }
    
    public boolean isAllowSponsoring() {
        return getValue("allow_sponsoring").getAsBoolean();
    }
    
    public int getSponsorCreditCost() {
        return getValue("sponsor_credit_cost").getAsInt();
    }
    
    public int getSponsorScoreCost() {
        return getValue("sponsor_score_cost").getAsInt();
    }
    
    public boolean isAllowSwagShack() {
        return getValue("allow_swag_shack").getAsBoolean();
    }
    
    public boolean isChestRestockRelative() {
        return getValue("chest_restock_relative").getAsBoolean();
    }
    
    public int getChestRestockDenomination() {
        return getValue("chest_restock_denomination").getAsInt();
    }
    
    public int getChestRestockInterval() {
        return getValue("chest_restock_interval").getAsInt();
    }
    
    @Override
    public GameSettings clone() {
        return (GameSettings) super.clone();
    }
}