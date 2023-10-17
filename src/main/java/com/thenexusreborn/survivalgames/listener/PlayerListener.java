package com.thenexusreborn.survivalgames.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.player.Toggle;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.api.events.NexusPlayerLoadEvent;
import com.thenexusreborn.nexuscore.api.events.ToggleChangeEvent;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.game.death.DeathType;
import com.thenexusreborn.survivalgames.game.death.KillerInfo;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.loot.LootTable;
import com.thenexusreborn.survivalgames.menu.MutateGui;
import com.thenexusreborn.survivalgames.menu.SwagShackMenu;
import com.thenexusreborn.survivalgames.menu.TeamMenu;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import com.thenexusreborn.survivalgames.mutations.impl.CreeperMutation;
import com.thenexusreborn.survivalgames.util.SGUtils;
import me.firestar311.starlib.api.Pair;
import me.firestar311.starui.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Openable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class PlayerListener implements Listener {
    private final SurvivalGames plugin;
    private final GuiManager manager;
    
    public PlayerListener(SurvivalGames plugin) {
        this.plugin = plugin;
        manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
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
                                    manager.openGUI(new TeamMenu(plugin, team), player);
                                }
                            }
                        } else if (item.getType() == Material.ROTTEN_FLESH) {
                            Pair<Boolean, String> canMutateResult = gamePlayer.canMutate();
                            if (canMutateResult.firstValue()) {
                                manager.openGUI(new MutateGui(plugin, gamePlayer), player);
                            } else {
                                player.sendMessage(MsgType.WARN + canMutateResult.secondValue());
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
            } else {
                if (e.getItem() == null) {
                    return;
                }
    
                if (e.getItem().getItemMeta() == null) {
                    return;
                }
    
                if (e.getItem().getItemMeta().getDisplayName() == null) {
                    return;
                }
    
                LobbyPlayer lobbyPlayer = null;
                for (LobbyPlayer lp : plugin.getLobby().getPlayers()) {
                    if (lp.getUniqueId().equals(player.getUniqueId())) {
                        lobbyPlayer = lp;
                    }
                }
    
                if (lobbyPlayer == null) {
                    return;
                }
    
                if (e.getItem().getItemMeta().getDisplayName().contains("Sponsors")) {
                    boolean sponsorsValue = lobbyPlayer.getToggleValue("allowsponsors");
                    lobbyPlayer.getPlayer().setToggleValue("allowsponsors", !sponsorsValue);
                    sponsorsValue = lobbyPlayer.getToggleValue("allowsponsors");
                    Material sponsorsItemMaterial = sponsorsValue ? Material.GLOWSTONE_DUST : Material.SULPHUR;
                    //TODO For some reason it does not update properly
                    player.getInventory().setItem(0, ItemBuilder.start(sponsorsItemMaterial).displayName("&e&lSponsors &7&o(Right click to toggle)").build());
                } else if (e.getItem().getItemMeta().getDisplayName().contains("Return to Hub")) {
                    SGUtils.sendToHub(player);
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
                if (Stream.of(Material.DISPENSER, Material.FURNACE, Material.BURNING_FURNACE, Material.WORKBENCH, Material.ENCHANTMENT_TABLE, Material.ANVIL).noneMatch(material -> block.getType() == material)) {
                    LootManager lootManager = LootManager.getInstance();
                    if (Stream.of(Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST).anyMatch(material -> block.getType() == material)) {
                        if (game == null) {
                            return;
                        }
                        if (game.getPlayer(player.getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                            e.setCancelled(true);
                            return;
                        }
                        
                        Inventory inv;
                        if (block.getType() == Material.ENDER_CHEST) {
                            inv = game.getEnderchestInventories().get(block.getLocation());
                            if (inv == null) {
                                inv = Bukkit.createInventory(null, 27, "Ender Chest");
                                game.getEnderchestInventories().put(block.getLocation(), inv);
                            } else {
                                e.setCancelled(true);
                                player.openInventory(inv);
                                return;
                            }
                        } else {
                            inv = ((Chest) block.getState()).getBlockInventory();
                        }
    
                        if (game.isLootedChest(block)) {
                            return;
                        }
    
                        game.getPlayer(player.getUniqueId()).changeStat("sg_chests_looted", 1, StatOperator.ADD);
                        
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
                                if (game.isGraceperiod()) {
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
                        if (nexusPlayer.getToggleValue("vanish")) {
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
        boolean incognito = nexusPlayer.getToggleValue("incognito");
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
            plugin.getLobby().getPlayers().forEach(p -> players.add(p.getPlayer()));
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
                if (p.getRank().ordinal() <= Rank.HELPER.ordinal() || p.getUniqueId().equals(e.getNexusPlayer().getUniqueId())) {
                    p.sendMessage(message);
                }
            }
        } else {
            if (game == null) {
                Bukkit.getScheduler().runTaskLater(plugin, lobby::recalculateVisibility, 1L);
                lobby.sendMessage(message);
            } else {
                if (e.newValue()) {
                    GamePlayer gamePlayer = game.getPlayer(nexusPlayer.getUniqueId());
                    if (gamePlayer.getTeam() != GameTeam.SPECTATORS) {
                        game.killPlayer(gamePlayer, new DeathInfo(game, System.currentTimeMillis(), gamePlayer, DeathType.VANISH));
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
        
        if (e.getRightClicked() instanceof Villager villager) {
            e.setCancelled(true);
            if (villager.getCustomName().contains("Swag Shack")) {
                Game game = plugin.getGame();
                if (game == null) {
                    e.getPlayer().sendMessage(MCUtils.color(MsgType.WARN + "You cannot open the Swag Shack when not in a game."));
                    return;
                }
                
                if (!game.getSettings().isAllowSwagShack()) {
                    e.getPlayer().sendMessage(MCUtils.color(MsgType.WARN + "The Swag Shack is disabled for this game."));
                    return;
                }
                
                GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
                if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                    gamePlayer.sendMessage(MsgType.WARN + "You can only open the Swag Shack as a Tribute.");
                    return;
                }
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        manager.openGUI(new SwagShackMenu(plugin, game, gamePlayer), e.getPlayer());
                    }
                }.runTaskLater(plugin, 1L);
            }
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
            
            if (e.getInventory() instanceof EnchantingInventory) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK) {
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
        
        if (Stream.of(GameState.WARMUP, GameState.WARMUP_DONE, GameState.DEATHMATCH_WARMUP, GameState.DEATHMATCH_WARMUP_DONE, GameState.TELEPORT_START, GameState.TELEPORT_DEATHMATCH, GameState.TELEPORT_START_DONE, GameState.TELEPORT_DEATHMATCH_DONE).anyMatch(gameState -> game.getState() == gameState)) {
            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                e.getPlayer().teleport(from);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Game game = plugin.getGame();
        if (game == null) {
            return;
        }

        e.setDeathMessage(null);
    
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
        
        DeathInfo deathInfo = new DeathInfo(game, System.currentTimeMillis(), gamePlayer, deathType, killerInfo);
    
        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
                if (e.getEntity().getLastDamageCause().getCause() == DamageCause.VOID) {
                    player.teleport(game.getGameMap().getCenter().toLocation(game.getGameMap().getWorld()));
                } else {
                    player.teleport(deathLocation);
                }
                game.killPlayer(gamePlayer, deathInfo);
            }
        }.runTaskLater(plugin, 2L);
    }
    
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
                boolean isStaff = nexusPlayer.getRank().ordinal() <= Rank.HELPER.ordinal();
                boolean isInVanish = nexusPlayer.getToggleValue("vanish");
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
