package com.thenexusreborn.survivalgames.game;

import com.stardevllc.helper.Pair;
import com.stardevllc.itembuilder.ItemBuilder;
import com.stardevllc.itembuilder.XMaterial;
import com.stardevllc.starchat.rooms.DefaultPermissions;
import com.stardevllc.starcore.utils.ArmorSet;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.PlayerBalance;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.survivalgames.chat.GameTeamChatroom;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.game.death.KillerInfo;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.scoreboard.GameTablistHandler;
import com.thenexusreborn.survivalgames.scoreboard.game.GameBoard;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;

public class GamePlayer {
    private final NexusPlayer nexusPlayer;
    private Game game;
    private GameTeam team;
    private boolean spectatorByDeath, newPersonalBestNotified;
    private TrackerInfo trackerInfo;
    private int kills, killStreak, assists;
    private boolean mutated;
    private Mutation mutation;
    private boolean deathByMutation;
    private boolean sponsored;
    private Bounty bounty;
    private CombatTag combatTag;
    private DamageInfo damageInfo;
    private TreeMap<Long, DeathInfo> deaths = new TreeMap<>();
    private Status status;
    private SGPlayerStats stats;
    
    public GamePlayer(NexusPlayer nexusPlayer, Game game, SGPlayerStats stats) {
        this.nexusPlayer = nexusPlayer;
        this.game = game;
        this.bounty = new Bounty(nexusPlayer.getUniqueId());
        this.combatTag = new CombatTag(game, nexusPlayer.getUniqueId());
        this.damageInfo = new DamageInfo(nexusPlayer.getUniqueId());
        this.stats = stats;
    }

    public SGPlayerStats getStats() {
        return stats;
    }

    public PlayerBalance getBalance() {
        return this.nexusPlayer.getBalance();
    }
    
    public String getColoredName() {
        return getNexusPlayer().getColoredName();
    }

    public Rank getRank() {
        return getNexusPlayer().getRank();
    }

    public boolean getToggleValue(String toggle) {
        return getNexusPlayer().getToggleValue(toggle);
    }

    public NexusScoreboard getScoreboard() {
        return getNexusPlayer().getScoreboard();
    }

    public String getDisplayName() {
        return getNexusPlayer().getDisplayName();
    }

    public String getName() {
        return getNexusPlayer().getName();
    }

    public void removeCredits(int credits) {
        getNexusPlayer().removeCredits(credits);
    }
    
    public NexusPlayer getNexusPlayer() {
        return nexusPlayer;
    }
    
    public void sendMessage(String message) {
        nexusPlayer.sendMessage(message);
    }
    
    public TrackerInfo getTrackerInfo() {
        return trackerInfo;
    }
    
    public void setTrackerInfo(TrackerInfo trackerInfo) {
        this.trackerInfo = trackerInfo;
    }
    
    public void setTeam(GameTeam team) {
        if (this.team != null) {
            this.game.getChatRooms().get(this.team).removeMember(getUniqueId());
        }
        
        this.team = team;
        GameTeamChatroom chatroom = game.getChatRooms().get(this.team);
        chatroom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES, DefaultPermissions.SEND_MESSAGES);
        Game.getPlugin().getStarChat().setPlayerFocus(Bukkit.getPlayer(getUniqueId()), chatroom);

        GameTeamChatroom mutationsRoom = game.getChatRooms().get(GameTeam.MUTATIONS);
        GameTeamChatroom zombiesRoom = game.getChatRooms().get(GameTeam.ZOMBIES);
        if (this.team == GameTeam.TRIBUTES) {
            mutationsRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
            zombiesRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
        } else {
            GameTeamChatroom tributesRoom = game.getChatRooms().get(GameTeam.TRIBUTES);
            if (this.team == GameTeam.MUTATIONS) {
                tributesRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
                zombiesRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
            } else if (this.team == GameTeam.ZOMBIES) {
                tributesRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
                mutationsRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
            } else if (this.team == GameTeam.SPECTATORS) {
                tributesRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
                mutationsRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
                zombiesRoom.addMember(getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
            }
        }
    }
    
    public GameTeam getTeam() {
        return team;
    }
    
    public UUID getUniqueId() {
        return nexusPlayer.getUniqueId();
    }
    
    public void setSpectatorByDeath(boolean value) {
        this.spectatorByDeath = value;
    }
    
    public boolean isSpectatorByDeath() {
        return spectatorByDeath;
    }
    
    public int getKills() {
        return kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public int getKillStreak() {
        return killStreak;
    }
    
    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }
    
    public boolean isNewPersonalBestNotified() {
        return newPersonalBestNotified;
    }
    
    public void setNewPersonalBestNotified(boolean newPersonalBestNotified) {
        this.newPersonalBestNotified = newPersonalBestNotified;
    }
    
    public boolean hasMutated() {
        return mutated;
    }
    
    public void setMutated(boolean mutated) {
        this.mutated = mutated;
    }
    
    public void setMutation(Mutation mutation) {
        this.mutation = mutation;
    }
    
    public Mutation getMutation() {
        return mutation;
    }
    
    public void setDeathByMutation(boolean value) {
        this.deathByMutation = value;
    }
    
    public boolean deathByMutation() {
        return deathByMutation;
    }
    
    public Bounty getBounty() {
        return bounty;
    }
    
    public CombatTag getCombatTag() {
        return combatTag;
    }
    
    public DamageInfo getDamageInfo() {
        return damageInfo;
    }
    
    public int getTotalTimesMutated() {
        int totalMutations = 0;
        for (DeathInfo deathInfo : new ArrayList<>(deaths.values())) {
            if (deathInfo.getTeam() == GameTeam.MUTATIONS) {
                totalMutations++;
            }
        }
        return totalMutations;
    }
    
    public Pair<Boolean, String> canMutate() {
        if (game == null) {
            return new Pair<>(false, "You cannot mutate because there is not game running.");
        }

        if (!(game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_DEATHMATCH)) {
            return new Pair<>(false, "You cannot mutate in the current game state.");
        }

        if (!game.getSettings().isAllowMutations()) {
            return new Pair<>(false, "You cannot mutate because mutations are disabled for this game.");
        }
        
        if (team != GameTeam.SPECTATORS) {
            return new Pair<>(false, "You can only mutate if you are a spectator.");
        }

        if (game.getTeamCount(GameTeam.MUTATIONS) >= game.getSettings().getMaxMutationsAllowed()) {
            return new Pair<>(false, "You cannot mutate as there are too many mutations in the game already.");
        }
        
        boolean timesMutatedExceedsMaxAmount = getTotalTimesMutated() >= game.getSettings().getMaxMutationAmount();

        if (timesMutatedExceedsMaxAmount) {
            return new Pair<>(false, "You cannot mutate more than " + game.getSettings().getMaxMutationAmount() + " times.");
        }

        if (getStats().getMutationPasses() <= 0 && !game.getSettings().isUnlimitedPasses()) {
            return new Pair<>(false, "You do not have any mutation passes.");
        }

        if (deathByMutation) {
            return new Pair<>(false, "You cannot mutate because you were killed by a mutation.");
        }

        if (mutated && timesMutatedExceedsMaxAmount) {
            return new Pair<>(false, "You have already mutated, you cannot mutate again.");
        }

        if (mutation != null) {
            return new Pair<>(false, "You have already selected your mutation type.");
        }

        if (getTeam() != GameTeam.SPECTATORS) {
            return new Pair<>(false, "You can only mutate if you have died.");
        }
        
        if (!killedByPlayer()) {
            return new Pair<>(false, "You can only mutate if you died to a player.");
        }

        UUID killerUUID = getKiller();
        GamePlayer killer = game.getPlayer(killerUUID);
        if (killer == null) {
            return new Pair<>(false, "Your killer left, you cannot mutate.");
        }

        if (killer.getTeam() != GameTeam.TRIBUTES) {
            return new Pair<>(false, "Your killer has died, you cannot mutate.");
        }
        
        return new Pair<>(true, "");
    }
    
    public void setCombat(GamePlayer other) {
        if (other.getUniqueId() == this.getUniqueId()) {
            return;
        }

        if (other.getTeam() == GameTeam.SPECTATORS) {
            return;
        }

        if (getTeam() == GameTeam.SPECTATORS) {
            return;
        }

        if (!getCombatTag().isInCombatWith(other.getUniqueId())) {
            sendMessage("&6&l>> &cYou are now in combat with " + other.getNexusPlayer().getColoredName() + "&c!");
        }
        getCombatTag().setOther(other.getUniqueId());
    }
    
    public void addDeathInfo(DeathInfo deathInfo) {
        this.deaths.put(deathInfo.getTime(), deathInfo);
    }
    
    public Map<Long, DeathInfo> getDeaths() {
        return deaths;
    }
    
    public int getAssists() {
        return this.assists;
    }
    
    public void setAssists(int amount) {
        this.assists = amount;
    }

    public boolean killedByPlayer() {
        for (DeathInfo death : this.deaths.values()) {
            if (death.getKiller() != null) {
                if (death.getKiller().getType() == EntityType.PLAYER) {
                    return true;
                }
            }
        }
        return false;
    }

    public UUID getKiller() {
        DeathInfo mostRecentDeath = this.getMostRecentDeath();
        if (mostRecentDeath != null) {
            if (mostRecentDeath.getKiller() != null) {
                if (mostRecentDeath.getKiller().getType() == EntityType.PLAYER) {
                    KillerInfo killer = mostRecentDeath.getKiller();
                    return killer.getKiller();
                }
            }
        }
        return null;
    }
    
    public List<String> getMenuVitals() {
        DecimalFormat format = new DecimalFormat("#0.#");
        Player player = Bukkit.getPlayer(getUniqueId());
        List<String> vitals = new LinkedList<>();
        vitals.add("&fHealth: &c" + format.format(player.getHealth() / 2) + "&f/&c" + format.format(player.getMaxHealth() / 2));
        vitals.add("&fHunger: &e" + format.format(player.getFoodLevel() / 2) + "&f/&e10");
        vitals.add("&fXP Level: &a" + player.getLevel());
        return vitals;
    }
    
    private String generateStatLine(String title, String statName) {
        return "&f" + title + ": &e" + getStats().getValue(statName);
    }
    
    public List<String> getMenuStats() {
        List<String> stats = new LinkedList<>();
        stats.add(generateStatLine("Score", "score"));
        stats.add(generateStatLine("Players Killed", "kills"));
        stats.add(generateStatLine("Highest Killstreak", "highestkillstreak"));
        stats.add(generateStatLine("Games Won", "wins"));
        stats.add(generateStatLine("Deaths", "deaths"));
        stats.add(generateStatLine("Passes Used", "timesmutated"));
        stats.add(generateStatLine("Mutation Kills", "mutation_kills"));
        stats.add(generateStatLine("Mutation Deaths", "mutationdeaths"));
        stats.add(generateStatLine("Deathmatches Reached", "deathmatchesreached"));
        stats.add(generateStatLine("Chests Looted", "chestslooted"));
        stats.add(generateStatLine("Mutation Passes", "mutationpasses"));
        return stats;
    }
    
    public boolean hasSponsored() {
        return sponsored;
    }
    
    public void setSponsored(boolean sponsored) {
        this.sponsored = sponsored;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void applyScoreboard() {
        nexusPlayer.getScoreboard().setView(new GameBoard(nexusPlayer.getScoreboard(), Game.getPlugin()));
        nexusPlayer.getScoreboard().setTablistHandler(new GameTablistHandler(nexusPlayer.getScoreboard(), Game.getPlugin()));
    }

    public void applyActionBar() {
        nexusPlayer.setActionBar(new GameActionBar(Game.getPlugin(), this));
    }

    public void clearInventory() {
        Player player = Bukkit.getPlayer(getUniqueId());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }
    
    public void giveSpectatorItems(Game game) {
        ItemStack tributesBook = ItemBuilder.of(XMaterial.ENCHANTED_BOOK).displayName("&a&lTributes &7&o(Right Click)").build();
        ItemStack mutationsBook = ItemBuilder.of(XMaterial.ENCHANTED_BOOK).displayName("&d&lMutations &7&o(Right Click)").build();
        ItemStack spectatorsBook = ItemBuilder.of(XMaterial.ENCHANTED_BOOK).displayName("&c&lSpectators &7&o(Right Click)").build();

        Pair<Boolean, String> canMutateStatus = canMutate();
        
        String mutateName;
        
        if (canMutateStatus.key()) {
            GamePlayer killer = game.getPlayer(getKiller());
            String passes;
            if (game.getSettings().isUnlimitedPasses()) {
                passes = "Unlimited";
            } else {
                passes = getStats().getMutationPasses() + "";
            }
            mutateName = "&c&lTAKE REVENGE   &eTarget: " + killer.getColoredName() + "   &ePasses: &b" + passes;
        } else {
            mutateName = "&c" + canMutateStatus.value();
        }
        
        ItemStack mutateItem = ItemBuilder.of(XMaterial.ROTTEN_FLESH).displayName(mutateName).build();
        ItemStack compass = ItemBuilder.of(XMaterial.COMPASS).displayName("&fPlayer Tracker").build();
        ItemStack tpCenter = ItemBuilder.of(XMaterial.CLOCK).displayName("&e&lTeleport to Map Center &7&o(Right Click)").build();
        ItemStack hubItem = ItemBuilder.of(XMaterial.OAK_DOOR).displayName("&e&lReturn to Hub &7(Right Click)").build();
        Player p = Bukkit.getPlayer(getUniqueId());
        PlayerInventory inv = p.getInventory();
        inv.setItem(0, tributesBook);
        inv.setItem(1, mutationsBook);
        inv.setItem(2, spectatorsBook);
        inv.setItem(5, mutateItem);
        inv.setItem(6, compass);
        inv.setItem(7, tpCenter);
        inv.setItem(8, hubItem);
    }
    
    private void callPlayerMethod(Consumer<Player> consumer) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            consumer.accept(player);
        }
    }

    public void setFlight(boolean allow, boolean flying) {
        callPlayerMethod(player -> {
            player.setAllowFlight(allow);
            player.setFlying(flying);
        });
    }

    public void setCollisions(boolean collisions) {
        callPlayerMethod(player -> player.spigot().setCollidesWithEntities(collisions));
    }

    public void setFood(int food, float saturation) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            player.setFoodLevel(food);
            player.setSaturation(saturation);
        }
    }

    public void clearPotionEffects() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public void setHealth(int health, double maxHealth) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            player.setMaxHealth(maxHealth);
            player.setHealth(Math.min(health, maxHealth));
        }
    }

    public void setArmor(ArmorSet armorType) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            PlayerInventory inv = player.getInventory();
            inv.setHelmet(new ItemStack(armorType.getHelmet().parseMaterial()));
            inv.setChestplate(new ItemStack(armorType.getChestplate().parseMaterial()));
            inv.setLeggings(new ItemStack(armorType.getLeggings().parseMaterial()));
            inv.setBoots(new ItemStack(armorType.getBoots().parseMaterial()));
        }
    }

    public boolean hasActiveTag() {
        return this.nexusPlayer.hasActiveTag();
    }

    public Tag getActiveTag() {
        return this.nexusPlayer.getActiveTag();
    }

    public void addItem(ItemStack itemStack) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player == null) {
            return;
        }
        
        player.getInventory().addItem(itemStack);
    }

    public DeathInfo getMostRecentDeath() {
        Map.Entry<Long, DeathInfo> mostRecentDeath = this.deaths.lastEntry();
        return mostRecentDeath != null ? mostRecentDeath.getValue() : null;
    }

    public enum Status {
        SETTING_UP_PLAYER, TELEPORTING_TO_CENTER, CALCULATING_VISIBILITY, SETTING_UP_SCOREBOARD, READY, SETTING_UP_ACTIONBAR, ADDING_TO_GAME

    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "nexusPlayer=" + nexusPlayer +
                ", game=" + game +
                ", team=" + team +
                ", spectatorByDeath=" + spectatorByDeath +
                ", newPersonalBestNotified=" + newPersonalBestNotified +
                ", trackerInfo=" + trackerInfo +
                ", kills=" + kills +
                ", killStreak=" + killStreak +
                ", assists=" + assists +
                ", mutated=" + mutated +
                ", mutation=" + mutation +
                ", deathByMutation=" + deathByMutation +
                ", sponsored=" + sponsored +
                ", bounty=" + bounty +
                ", combatTag=" + combatTag +
                ", damageInfo=" + damageInfo +
                ", deaths=" + deaths +
                ", status=" + status +
                ", stats=" + stats +
                '}';
    }
}
