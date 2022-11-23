package com.thenexusreborn.survivalgames.listener;

import com.google.common.io.*;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.api.events.*;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.*;
import com.thenexusreborn.survivalgames.game.deathold.DeathInfoVanish;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.loot.*;
import com.thenexusreborn.survivalgames.menu.*;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.mutations.impl.*;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Openable;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayerListener implements Listener {
    private final SurvivalGames plugin;
    
    public PlayerListener(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (plugin.getLobby().checkMapEditing(e.getPlayer())) {
            return;
        }
        if (plugin.getGame() == null) {
            e.setCancelled(true);
        } else {
            Game game = plugin.getGame();
            GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
            if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBucketPlace(PlayerBucketEmptyEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        if (e.getState() == State.CAUGHT_FISH) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (plugin.getLobby().checkMapEditing(player)) {
            return;
        }
        
        Game game = plugin.getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getItem() != null) {
                        ItemStack item = e.getItem();
                        if (item.getType() == Material.ENCHANTED_BOOK) {
                            ItemMeta itemMeta = item.getItemMeta();
                            String displayName = itemMeta.getDisplayName();
                            if (displayName != null && !displayName.equals("")) {
                                GameTeam team = null;
                                if (displayName.toLowerCase().contains("tributes")) {
                                    team = GameTeam.TRIBUTES;
                                } else if (displayName.toLowerCase().contains("mutations")) {
                                    team = GameTeam.MUTATIONS;
                                } else if (displayName.toLowerCase().contains("spectators")) {
                                    team = GameTeam.SPECTATORS;
                                }
                                
                                if (team != null) {
                                    player.openInventory(new TeamMenu(plugin, team).getInventory());
                                }
                            }
                        } else if (item.getType() == Material.ROTTEN_FLESH) {
                            if (gamePlayer.canMutate()) {
                                player.openInventory(new MutateGui(plugin, gamePlayer.getNexusPlayer()).getInventory());
                            }
                        } else if (item.getType() == Material.WATCH) {
                            player.teleport(game.getGameMap().getCenter().toLocation(game.getGameMap().getWorld()));
                            gamePlayer.sendMessage("&6&l>> &eTeleported to the Map Center.");
                        } else if (item.getType() == Material.WOOD_DOOR) {
                            gamePlayer.sendMessage("&6&l>> &eSending you to the hub.");
                            SGUtils.sendToHub(player);
                        }
                    }
                }
                
                e.setCancelled(true);
                return;
            } else if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                Mutation mutation = gamePlayer.getMutation();
                ItemStack item = player.getItemInHand();
                if (item == null) {
                    return;
                }
                if (mutation instanceof CreeperMutation) {
                    if (item.getType() == Material.SULPHUR) {
                        Location loc = player.getLocation();
                        game.getSuicideLocations().put(loc, player.getUniqueId());
                        SGUtils.spawnTNTWithSource(loc, player, 1, 4F);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                                e.getPlayer().setHealth(0);
                            }
                        }, 10L);
                    }
                } else if (mutation instanceof ChickenMutation chickenMutation) {
                    if (item.getType() == Material.SLIME_BALL) {
                        if (!chickenMutation.isLaunchOnCooldown()) {
                            player.setVelocity(new Vector(0, 2, 0));
                            chickenMutation.startLaunchCooldown();
                        } else {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "Chicken Launch is still on cooldown: &e" + chickenMutation.getLaunchCooldownTimer().getSecondsLeft() + "s&c!"));
                        }
                    } else if (item.getType() == Material.FEATHER) {
                        if (!chickenMutation.isParachuteOnCooldown()) {
                            if (chickenMutation.isChuteActive()) {
                                chickenMutation.deactivateChute();
                            } else {
                                chickenMutation.activateChute();
                            }
                        } else {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "Chicken Chute is still on cooldown: &e" + chickenMutation.getParachuteCooldownTimer().getSecondsLeft() + "s&c!"));
                        }
                    }
                }
            }
        } else {
            Block block = e.getClickedBlock();
            if (block != null) {
                if (block.getType() == Material.NOTE_BLOCK || block.getType() == Material.CHEST) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        
        Block block = e.getClickedBlock();
        if (block != null) {
            if (block.getType().name().contains("_DOOR") || block.getType().name().contains("_BUTTON") || block.getType() == Material.LEVER || block.getType().name().contains("_GATE")) {
                return;
            }
        }
        
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getClickedBlock() != null) {
                if (!(block.getType() == Material.DISPENSER || block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE || block.getType() == Material.WORKBENCH || block.getType() == Material.ENCHANTMENT_TABLE || block.getType() == Material.ANVIL)) {
                    LootManager lootManager = LootManager.getInstance();
                    if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getType() == Material.ENDER_CHEST) {
                        if (game == null) {
                            return;
                        }
                        if (game.getPlayer(player.getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                            e.setCancelled(true);
                            return;
                        }
                        if (game.isLootedChest(block)) {
                            return;
                        }
                        
                        game.getPlayer(player.getUniqueId()).getNexusPlayer().getStats().change("sg_chests_looted", 1, StatOperator.ADD);
                        
                        Inventory inv;
                        if (block.getType() == Material.ENDER_CHEST) {
                            inv = game.getEnderchestInventories().get(block.getLocation());
                            if (inv == null) {
                                inv = Bukkit.createInventory(null, 27, "Ender Chest");
                                game.getEnderchestInventories().put(block.getLocation(), inv);
                            }
                        } else {
                            inv = ((Chest) block.getState()).getBlockInventory();
                        }
                        
                        int maxAmount = 8;
                        
                        Block secondHalf = null;
                        if (block.getType() != Material.ENDER_CHEST) {
                            Block north = block.getRelative(BlockFace.NORTH);
                            Block south = block.getRelative(BlockFace.SOUTH);
                            Block east = block.getRelative(BlockFace.EAST);
                            Block west = block.getRelative(BlockFace.WEST);
                            if (north != null && north.getType() == Material.CHEST) {
                                secondHalf = north;
                            } else if (south != null && south.getType() == Material.CHEST) {
                                secondHalf = south;
                            } else if (east != null && east.getType() == Material.CHEST) {
                                secondHalf = east;
                            } else if (west != null && west.getType() == Material.CHEST) {
                                secondHalf = west;
                            }
                            
                            if (secondHalf != null) {
                                maxAmount += 3;
                            }
                        }
                        
                        inv.clear();
                        
                        LootTable lootTable = null;
                        
                        boolean useTieredLoot = game.getSettings().isUseNewLoot();
                        if (!useTieredLoot) {
                            lootTable = lootManager.getLootTable("tierOne");
                        } else {
                            if (game.getState() == GameState.DEATHMATCH) {
                                lootTable = lootManager.getLootTable("tierFour");
                            } else {
                                boolean withinCenter = game.getGameMap().getDeathmatchArea().contains(player);
                                if (game.getState() == GameState.INGAME_GRACEPERIOD) {
                                    lootTable = lootManager.getLootTable("tierOne");
                                } else if (game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_DEATHMATCH) {
                                    boolean afterRestock = game.getRestockTimer() == null;
                                    if (withinCenter) {
                                        if (afterRestock) {
                                            lootTable = lootManager.getLootTable("tierThree");
                                        } else {
                                            lootTable = lootManager.getLootTable("tierTwo");
                                        }
                                    } else {
                                        if (afterRestock) {
                                            lootTable = lootManager.getLootTable("tierTwo");
                                        } else {
                                            lootTable = lootManager.getLootTable("tierOne");
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (lootTable == null) {
                            player.sendMessage(MCUtils.color(MsgType.ERROR + "Error while determining the loot table."));
                            return;
                        }
                        
                        List<ItemStack> items = lootTable.generateLoot(2, maxAmount);
                        
                        for (ItemStack item : items) {
                            int slot;
                            do {
                                slot = new Random().nextInt(inv.getSize());
                            } while (inv.getItem(slot) != null);
                            inv.setItem(slot, item);
                        }
                        
                        if (block.getType() == Material.ENDER_CHEST) {
                            Inventory finalInventory = inv;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.openInventory(finalInventory);
                                }
                            }.runTaskLater(plugin, 1L);
                        }
                        
                        game.addLootedChest(block.getLocation());
                        if (secondHalf != null) {
                            game.addLootedChest(secondHalf.getLocation());
                        }
                    } else if (block.getState() instanceof Sign) {
                        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
                        if (nexusPlayer.getToggles().getValue("vanish")) {
                            nexusPlayer.sendMessage(MsgType.WARN + "You cannot vote for a map while in vanish.");
                            return;
                        }
                        if (plugin.getLobby().getState() == LobbyState.WAITING || plugin.getLobby().getState() == LobbyState.COUNTDOWN) {
                            plugin.getLobby().addMapVote(nexusPlayer, block.getLocation());
                        }
                    } else {
                        if (e.getItem() != null) {
                            return;
                        }
                        
                        if (BlockListener.ALLOWED_PLACE.contains(block.getType())) {
                            return;
                        }
                        
                        if (BlockListener.ALLOWED_BREAK.contains(block.getType())) {
                            return;
                        }
                        
                        if (block.getState() instanceof Openable) {
                            return;
                        }
                        
                        e.setCancelled(true);
                    }
                }
            }
        } else if (e.getAction() == Action.PHYSICAL) {
            if (block != null) {
                if (!block.getType().name().contains("_PLATE")) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onToggleChange(ToggleChangeEvent e) {
        NexusPlayer nexusPlayer = e.getNexusPlayer();
        boolean incognito = nexusPlayer.getToggles().getValue("incognito");
        Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
        Toggle toggle = e.getToggle();
        Game game = plugin.getGame();
        Lobby lobby = plugin.getLobby();
        
        if (toggle.getInfo().getName().equalsIgnoreCase("fly")) {
            if (game != null) {
                e.setCancelled(true);
                e.setCancelReason("You cannot toggle fly during a game.");
            } else {
                player.setAllowFlight(e.newValue());
            }
            
            return;
        }
        
        if (!toggle.getInfo().getName().equalsIgnoreCase("vanish")) {
            return;
        }
        
        Collection<NexusPlayer> players = new ArrayList<>();
        if (game == null) {
            players.addAll(plugin.getLobby().getPlayers());
        } else {
            for (GamePlayer value : game.getPlayers().values()) {
                players.add(value.getNexusPlayer());
            }
        }
        
        String symbolColor = e.newValue() ? "a" : "c";
        String symbol = !e.newValue() ? ">>" : "<<";
        String action = !e.newValue() ? "joined" : "left";
        String silent = incognito ? " &e&osilently" : "";
        
        String message = "&" + symbolColor + "&l" + symbol + " " + nexusPlayer.getColoredName() + " &e" + action + silent + "&e.";
        
        if (incognito) {
            for (NexusPlayer p : players) {
                if (p.getRanks().get().ordinal() <= Rank.HELPER.ordinal() || p.getUniqueId().equals(e.getNexusPlayer().getUniqueId())) {
                    p.sendMessage(message);
                }
            }
        } else {
            if (game == null) {
                Bukkit.getScheduler().runTaskLater(plugin, lobby::recalculateVisibility, 1L);
                lobby.sendMessage(message);
            } else {
                if (e.newValue()) {
                    if (game.getPlayer(nexusPlayer.getUniqueId()).getTeam() != GameTeam.SPECTATORS) {
                        game.killPlayer(nexusPlayer.getUniqueId(), new DeathInfoVanish(nexusPlayer.getUniqueId()));
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, game::recalculateVisibility, 1L);
                game.sendMessage(message);
            }
        }
    }
    
    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractAtEntityEvent e) {
        if (plugin.getLobby().checkMapEditing(e.getPlayer())) {
            return;
        }
        if (e.getRightClicked() instanceof ArmorStand || e.getRightClicked() instanceof ItemFrame || e.getRightClicked() instanceof Painting) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        CraftingInventory inv = e.getInventory();
        if (inv.getRecipe() == null) {
            return;
        }
        if (inv.getRecipe().getResult().getType() == Material.FLINT_AND_STEEL) {
            ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
            itemStack.setDurability((short) (Material.FLINT_AND_STEEL.getMaxDurability() - 4));
            inv.setResult(itemStack);
        } else if (inv.getRecipe().getResult().getType() == Material.DIAMOND_AXE) {
            ItemStack itemStack = new ItemStack(Material.DIAMOND_AXE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(MCUtils.color("&fBetty"));
            itemStack.setItemMeta(itemMeta);
            inv.setResult(itemStack);
        } else if (inv.getRecipe().getResult().getType() == Material.IRON_AXE) {
            ItemStack itemStack = new ItemStack(Material.IRON_AXE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(MCUtils.color("&fFrederick"));
            itemStack.setItemMeta(itemMeta);
            inv.setResult(itemStack);
        }
    }
    
    @EventHandler
    public void onPlayerOpenEnchant(InventoryOpenEvent e) {
        if (plugin.getGame() != null) {
            if (e.getInventory() instanceof EnchantingInventory enchantingInventory) {
                Game game = plugin.getGame();
                if (game.getPlayer(e.getPlayer().getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                    e.setCancelled(true);
                    return;
                }
    
                enchantingInventory.setSecondary(new ItemStack(Material.INK_SACK, 64, (short) 4));
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (plugin.getLobby().checkMapEditing(player)) {
            return;
        }
        
        Game game = plugin.getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                e.setCancelled(true);
            }
            
            if (e.getInventory() instanceof EnchantingInventory enchantingInventory) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.INK_SACK)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerCloseEnchant(InventoryCloseEvent e) {
        try {
            if (e.getInventory() instanceof EnchantingInventory enchantingInventory) {
                enchantingInventory.setSecondary(null);
            }
        } catch (Exception ex) {
        }
    }
    
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if (plugin.getLobby().checkMapEditing(e.getPlayer())) {
            return;
        }
        if (plugin.getGame() != null) {
            Game game = plugin.getGame();
            e.setCancelled(game.getPlayer(e.getPlayer().getUniqueId()).getTeam() != GameTeam.TRIBUTES);
        } else {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (plugin.getLobby().checkMapEditing(e.getPlayer())) {
            return;
        }
        if (plugin.getGame() == null) {
            return;
        }
        
        Location from = e.getFrom(), to = e.getTo();
        
        Game game = plugin.getGame();
        GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
        if (gamePlayer != null) {
            if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                return;
            }
        }
        
        if (game.getState() == GameState.WARMUP || game.getState() == GameState.WARMUP_DONE ||
                game.getState() == GameState.DEATHMATCH_WARMUP || game.getState() == GameState.DEATHMATCH_WARMUP_DONE ||
                game.getState() == GameState.TELEPORT_START || game.getState() == GameState.TELEPORT_DEATHMATCH ||
                game.getState() == GameState.TELEPORT_START_DONE || game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                e.getPlayer().teleport(from);
            }
        }
    }
    
    //TODO Rename when done
    @EventHandler
    public void onPlayerDeathRewrite(PlayerDeathEvent e) {
        Game game = plugin.getGame();
        if (game == null) {
            return;
        }
    
        Player player = e.getEntity();
        GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        Location deathLocation = player.getLocation().clone();
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
    
        if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
    
        DeathType deathType = switch (lastDamageCause.getCause()) {
            case CONTACT -> DeathType.CACTUS;
            case SUFFOCATION -> DeathType.SUFFOCATION;
            case FALL -> DeathType.FALL;
            case FIRE, FIRE_TICK -> DeathType.FIRE;
            case MELTING -> DeathType.MELTING;
            case LAVA -> DeathType.LAVA;
            case DROWNING -> DeathType.DROWNING;
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> DeathType.EXPLOSION;
            case VOID -> DeathType.VOID;
            case LIGHTNING -> DeathType.LIGHTNING;
            case SUICIDE -> DeathType.SUICIDE;
            case STARVATION -> DeathType.STARVATION;
            case POISON -> DeathType.POISON;
            case MAGIC -> DeathType.MAGIC;
            case WITHER -> DeathType.WITHER;
            case FALLING_BLOCK -> DeathType.FALLING_BLOCK;
            case THORNS -> DeathType.THORNS;
            case CUSTOM -> DeathType.UNKNOWN;
            default -> null;
        };
    
        KillerInfo killerInfo = null;
    
        if (deathType == null) {
            if (player.getKiller() != null) {
                Player killer = player.getKiller();
                GamePlayer killerGamePlayer = game.getPlayer(killer.getUniqueId());
                if (lastDamageCause.getCause() == DamageCause.ENTITY_ATTACK) {
                    killerInfo = KillerInfo.createPlayerKiller(killerGamePlayer);
                    deathType = DeathType.PLAYER;
                } else if (lastDamageCause.getCause() == DamageCause.PROJECTILE) {
                    killerInfo = KillerInfo.createPlayerProjectileKiller(killerGamePlayer, player.getLocation().distance(killer.getLocation()));
                    deathType = DeathType.PLAYER_PROJECTILE;
                }
            } else {
                EntityDamageByEntityEvent edee = (EntityDamageByEntityEvent) lastDamageCause;
                if (edee.getDamager() instanceof Projectile projectile) {
                    Entity shooter = (Entity) projectile.getShooter();
                    killerInfo = KillerInfo.createMobProjectileKiller(shooter, player.getLocation().distance(shooter.getLocation()));
                    deathType = DeathType.ENTITY_PROJECTILE;                
                } else {
                    killerInfo = KillerInfo.createMobKiller(edee.getDamager());
                    deathType = DeathType.ENTITY;
                }
            }
        }
        
        if (killerInfo == null) {
            CombatTag combatTag = gamePlayer.getCombatTag();
            if (combatTag.isInCombat()) {
                killerInfo = KillerInfo.createPlayerKiller(game.getPlayer(combatTag.getOther()));
            }
        }
        
        DeathInfo deathInfo = new DeathInfo(game, gamePlayer, deathType, killerInfo);
    
        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
                if (e.getEntity().getLastDamageCause().getCause() == DamageCause.VOID) {
                    player.teleport(game.getGameMap().getCenter().toLocation(game.getGameMap().getWorld()));
                } else {
                    player.teleport(deathLocation);
                }
                game.killPlayerRewrite(gamePlayer, deathInfo);
            }
        }.runTaskLater(plugin, 2L);
    }
    
    //TODO Remove when done
//    @EventHandler
//    public void onPlayerDeath(PlayerDeathEvent e) {
//        if (plugin.getLobby().checkMapEditing(e.getEntity())) {
//            return;
//        }
//        e.setDeathMessage(null);
//    
//        Game game = plugin.getGame();
//        if (game == null) {
//            return;
//        }
//        Player player = e.getEntity();
//        System.out.println(player.getLastDamageCause().getCause());
//        System.out.println(player.getKiller());
//        GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
//        if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
//            return;
//        }
//    
//        if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
//            e.getDrops().clear();
//        }
//    
//        DeathInfo deathInfo = null;
//        final Player killer = player.getKiller();
//        if (killer != null && player.getLastDamageCause().getCause() != DamageCause.PROJECTILE) {
//            GamePlayer killerPlayer = game.getPlayer(killer.getUniqueId());
//            if (player.getLastDamageCause().getCause() == DamageCause.ENTITY_EXPLOSION) {
//                deathInfo = new DeathInfoTntKill(player.getUniqueId(), killerPlayer.getUniqueId(), killer.getHealth(), game.getPlayer(killer.getUniqueId()).getTeam().getColor());
//            } else {
//                deathInfo = new DeathInfoPlayerKill(player.getUniqueId(), killerPlayer.getUniqueId(), killer.getItemInHand(), killer.getHealth(), game.getPlayer(killer.getUniqueId()).getTeam().getColor());
//            }
//        } else {
//            EntityDamageEvent lastDamageCause = player.getLastDamageCause();
//            DamageCause damageCause;
//            if (lastDamageCause == null) {
//                damageCause = DamageCause.VOID;
//            } else {
//                damageCause = lastDamageCause.getCause();
//            }
//        
//            String teamColor = gamePlayer.getTeam().getColor();
//            if (damageCause == DamageCause.PROJECTILE) {
//                if (lastDamageCause instanceof EntityDamageByEntityEvent edbee) {
//                    if (edbee.getDamager() instanceof Projectile projectile) {
//                        if (projectile.getShooter() instanceof Entity entity) {
//                            String color = "";
//                            if (entity instanceof Player) {
//                                if (game.getSettings().getColorMode() == ColorMode.GAME_TEAM) {
//                                    color = game.getPlayer(entity.getUniqueId()).getTeam().getColor();
//                                } else {
//                                    color = game.getPlayer(entity.getUniqueId()).getNexusPlayer().getRanks().get().getColor();
//                                }
//                            }
//                            deathInfo = new DeathInfoProjectile(player.getUniqueId(), ((Entity) projectile.getShooter()), player.getLocation().distance(entity.getLocation()), color, ((LivingEntity) projectile.getShooter()).getHealth());
//                        }
//                    }
//                }
//            } else if (damageCause == DamageCause.SUFFOCATION) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.SUFFOCATION, "&4&l>> %playername% &7suffocated to death.", teamColor);
//            } else if (damageCause == DamageCause.FALL) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FALL, "&4&l>> %playername% &7fell to their death.", teamColor);
//            } else if (damageCause == DamageCause.LAVA) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.LAVA, "&4&l>> %playername% &7roasted in lava.", teamColor);
//            } else if (damageCause == DamageCause.FIRE || damageCause == DamageCause.FIRE_TICK) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FIRE, "&4&l>> %playername% &7burned to death.", teamColor);
//            } else if (damageCause == DamageCause.DROWNING) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.DROWNING, "&4&l>> %playername% &7drowned.", teamColor);
//            } else if (damageCause == DamageCause.ENTITY_EXPLOSION || damageCause == DamageCause.BLOCK_EXPLOSION) {
//                if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent edbe) {
//                    Location dLoc = edbe.getDamager().getLocation().clone();
//                    Location loc = new Location(dLoc.getWorld(), dLoc.getBlockX(), dLoc.getBlockY(), dLoc.getBlockZ());
//                    if (game.getSuicideLocations().containsKey(loc)) {
//                        UUID cause = game.getSuicideLocations().get(loc);
//                        double health = Bukkit.getPlayer(cause).getHealth();
//                        deathInfo = new DeathInfoKilledSuicide(player.getUniqueId(), cause, health, game.getPlayer(cause).getTeam().getColor());
//                    }
//                } else {
//                    deathInfo = new DeathInfo(player.getUniqueId(), DeathType.EXPLOSION, "&4&l>> %playername% &7exploded.", teamColor);
//                }
//            } else if (damageCause == DamageCause.VOID) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.VOID, "&4&l>> %playername% &7fell in the void.", teamColor);
//            } else if (damageCause == DamageCause.LIGHTNING) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.LIGHTNING, "&4&l>> %playername% &7was struck by lightning and died.", teamColor);
//            } else if (damageCause == DamageCause.SUICIDE) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.SUICIDE, "&4&l>> %playername% &7died.", teamColor);
//            } else if (damageCause == DamageCause.STARVATION) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.STARVATION, "&4&l>> %playername% &7starved to death.", teamColor);
//            } else if (damageCause == DamageCause.POISON) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.POISON, "&4&l>> %playername% &7was poisoned to death.", teamColor);
//            } else if (damageCause == DamageCause.MAGIC) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.MAGIC, "&4&l>> %playername% &7was killed by magic.", teamColor);
//            } else if (damageCause == DamageCause.WITHER) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.WITHER, "&4&l>> %playername% &7withered away.", teamColor);
//            } else if (damageCause == DamageCause.MELTING) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.MELTING, "&4&l>> %playername% &7melted to death.", teamColor);
//            } else if (damageCause == DamageCause.FALLING_BLOCK) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FALLING_BLOCK, "&4&l>> %playername% &7had a block fall on their head.", teamColor);
//            } else if (damageCause == DamageCause.THORNS) {
//                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.THORNS, "&4&l>> %playername% &7was poked to death by armor.", teamColor);
//            } else if (damageCause == DamageCause.ENTITY_ATTACK) {
//                if (lastDamageCause instanceof EntityDamageByEntityEvent edbee) {
//                    Entity damager = edbee.getDamager();
//                    if (damager instanceof Player) {
//                        deathInfo = new DeathInfoSuicide(player.getUniqueId());
//                    } else {
//                        deathInfo = new DeathInfoEntity(player.getUniqueId(), damager.getType());
//                    }
//                }
//            }
//        }
//    
//        if (deathInfo == null) {
//            deathInfo = new DeathInfo(player.getUniqueId(), DeathType.UNKNOWN, "%playername% &7died to unknown reasons.", gamePlayer.getTeam().getColor());
//        }
//    
//        Location deathLocation = player.getLocation().clone();
//        DeathInfo finalDeathInfo = deathInfo;
//        new BukkitRunnable() {
//            public void run() {
//                player.spigot().respawn();
//                if (e.getEntity().getLastDamageCause().getCause() == DamageCause.VOID) {
//                    player.teleport(game.getGameMap().getCenter().toLocation(game.getGameMap().getWorld()));
//                } else {
//                    player.teleport(deathLocation);
//                }
//                game.killPlayer(e.getEntity().getUniqueId(), finalDeathInfo);
//            }
//        }.runTaskLater(plugin, 2L);
//    }
    
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.ROTTEN_FLESH) {
            if (new Random().nextInt(100) < 15) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 0));
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENDERMAN_STARE, 1.0F, 1.0F);
            }
        }
    }
    
    @EventHandler
    public void onNexusPlayerLoad(NexusPlayerLoadEvent e) {
        NexusPlayer nexusPlayer = e.getNexusPlayer();
        if (plugin.getGame() == null) {
            if (plugin.getLobby().getPlayingCount() >= plugin.getLobby().getLobbySettings().getMaxPlayers()) {
                boolean isStaff = nexusPlayer.getRanks().get().ordinal() <= Rank.HELPER.ordinal();
                boolean isInVanish = nexusPlayer.getToggles().getValue("vanish");
                if (!(isStaff && isInVanish)) {
                    nexusPlayer.sendMessage("&cThe lobby is full.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("Connect");
                            out.writeUTF("H1");
                            Bukkit.getPlayer(nexusPlayer.getUniqueId()).sendPluginMessage(plugin.getNexusCore(), "BungeeCord", out.toByteArray());
                        }
                    }.runTaskLater(plugin, 10L);
                    
                    return;
                }
            }
        }
        
        SurvivalGames.PLAYER_QUEUE.offer(e.getNexusPlayer().getUniqueId());
        if (plugin.getGame() != null) {
            GameState state = plugin.getGame().getState();
            if (state == GameState.ASSIGN_TEAMS) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (plugin.getGame().getState() != GameState.ASSIGN_TEAMS) {
                            plugin.getGame().addPlayer(nexusPlayer);
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 1L, 1L);
            } else {
                plugin.getGame().addPlayer(nexusPlayer);
            }
        } else {
            plugin.getLobby().addPlayer(nexusPlayer);
        }
        e.setJoinMessage(null);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        SurvivalGames.PLAYER_QUEUE.remove(e.getPlayer().getUniqueId());
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(e.getPlayer().getUniqueId());
        if (plugin.getGame() != null) {
            plugin.getGame().removePlayer(nexusPlayer);
        } else {
            plugin.getLobby().removePlayer(nexusPlayer);
        }
        e.setQuitMessage(null);
    }
}
