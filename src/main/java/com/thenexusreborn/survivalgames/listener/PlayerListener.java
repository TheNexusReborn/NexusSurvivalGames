package com.thenexusreborn.survivalgames.listener;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.stardevllc.starchat.rooms.DefaultPermissions;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starui.GuiManager;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.api.events.*;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.chat.GameTeamChatroom;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.*;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.loot.item.Items;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import com.thenexusreborn.survivalgames.menu.SwagShackMenu;
import com.thenexusreborn.survivalgames.settings.enums.LootMode;
import com.thenexusreborn.survivalgames.util.NickSGPlayerStats;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import de.tr7zw.nbtapi.NBT;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings({"DuplicatedCode"})
public class PlayerListener implements Listener {
    private final SurvivalGames plugin;
    private final GuiManager manager;
    
    private List<String> tableChancesFirstHalf = new ArrayList<>(), tableChancesSecondHalf = new ArrayList<>();
    
    public PlayerListener(SurvivalGames plugin) {
        this.plugin = plugin;
        manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        
        for (int i = 0; i < 100; i++) {
            if (i > 95) {
                tableChancesFirstHalf.add("tierFour");
            } else if (i > 70) {
                tableChancesFirstHalf.add("tierThree");
            } else if (i > 40) {
                tableChancesFirstHalf.add("tierTwo");
            } else {
                tableChancesFirstHalf.add("tierOne");
            }
            
            if (i > 80) {
                tableChancesSecondHalf.add("tierFour");
            } else if (i > 50) {
                tableChancesSecondHalf.add("tierThree");
            } else {
                tableChancesSecondHalf.add("tierTwo");
            }
        }
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();
        
        if (lobby != null && lobby.checkMapEditing(e.getPlayer())) {
            return;
        }
        if (game == null) {
            e.setCancelled(true);
        } else {
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
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        if (sgPlayer == null) {
            return;
        }
        
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();
        
        if (lobby == null && game == null) {
            return;
        }
        
        if (lobby != null && lobby.checkMapEditing(e.getPlayer())) {
            return;
        }
        
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block != null) {
                if (block.getType() == Material.BEACON) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        
        if (lobby != null) {
            Block block = e.getClickedBlock();
            if (block != null) {
                if (block.getType() == Material.NOTE_BLOCK || block.getType() == Material.CHEST || block.getType().name().contains("REDSTONE")) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        
        Block block = e.getClickedBlock();
        if (block != null) {
            if (game != null) {
                if (sgPlayer.getGamePlayer().getTeam() == GameTeam.SPECTATORS) {
                    e.setCancelled(true);
                    return;
                }
            }
            
            if (block.getType().name().contains("_DOOR") || block.getType().name().contains("_BUTTON") || block.getType() == Material.LEVER || block.getType().name().contains("_GATE")) {
                return;
            }
        }
        
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getItem() != null && e.getItem().getType() == Material.FISHING_ROD) {
                int uses = NBT.get(e.getItem(), nbt -> {
                    if (!nbt.hasTag("sg_uses")) {
                        return 20;
                    }
                    return nbt.getInteger("sg_uses");
                });
                
                int displayUses = NBT.get(e.getItem(), nbt -> {
                    if (!nbt.hasTag("sg_display_uses")) {
                        return 10;
                    }
                    
                    return nbt.getInteger("sg_display_uses");
                });
                
                uses--;
                if (uses % 2 == 0) {
                    displayUses--;
                }
                
                if (uses <= 0) {
                    e.getItem().setDurability((short) (e.getItem().getType().getMaxDurability() + 1));
                    sgPlayer.playSound(Sound.ITEM_BREAK);
                } else {
                    ItemMeta itemMeta = e.getItem().getItemMeta();
                    List<String> lore = List.of("", StarColors.color("&fUses Left: &e" + displayUses));
                    itemMeta.setLore(lore);
                    e.getItem().setItemMeta(itemMeta);
                    int finalUses = uses;
                    int finalDisplayUses = displayUses;
                    NBT.modify(e.getItem(), nbt -> {
                        nbt.setInteger("sg_uses", finalUses);
                        nbt.setInteger("sg_display_uses", finalDisplayUses);
                    });
                }
            }
            
            if (e.getClickedBlock() != null) {
                if (Stream.of(Material.DISPENSER, Material.FURNACE, Material.BURNING_FURNACE, Material.WORKBENCH, Material.ENCHANTMENT_TABLE, Material.ANVIL).noneMatch(material -> block.getType() == material)) {
                    LootManager lootManager = plugin.getLootManager();
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
                        
                        game.getPlayer(player.getUniqueId()).getStats().addChestsLooted(1);
                        
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
                        
                        SGLootTable lootTable = null;
                        
                        if (game.getSettings().getLootMode() == LootMode.CLASSIC) {
                            lootTable = lootManager.getLootTable(game.getSettings().getRegularTier());
                        } else if (game.getSettings().getLootMode() == LootMode.TIERED) {
                            if (game.getState() == Game.State.DEATHMATCH) {
                                lootTable = lootManager.getLootTable(game.getSettings().getDeathmatchTier());
                            } else {
                                boolean withinCenter = game.getGameMap().getDeathmatchRegion().contains(BukkitUtil.toVector(player.getLocation()));
                                if (game.getState() == Game.State.INGAME || game.getState() == Game.State.INGAME_DEATHMATCH) {
                                    boolean afterRestock = game.getTimedRestockCount() > 0;
                                    if (withinCenter) {
                                        lootTable = lootManager.getLootTable(game.getSettings().getCornucopiaTier());
                                    } else {
                                        lootTable = lootManager.getLootTable(game.getSettings().getRegularTier());
                                    }
                                }
                            }
                        } else if (game.getSettings().getLootMode() == LootMode.RANDOM) {
                            if (game.getState() == Game.State.DEATHMATCH) {
                                lootTable = lootManager.getLootTable(game.getSettings().getDeathmatchTier());
                            } else {
                                List<String> chances;
                                if (game.getTimer().getTime() < game.getTimer().getLength() / 2) {
                                    chances = this.tableChancesSecondHalf;
                                } else {
                                    chances = this.tableChancesFirstHalf;
                                }
                                
                                lootTable = lootManager.getLootTable(chances.get(new Random().nextInt(chances.size())));
                            }
                        }
                        
                        if (lootTable == null) {
                            player.sendMessage(StarColors.color(MsgType.ERROR + "Error while determining the loot table."));
                            return;
                        }
                        
                        if (lootTable.isReloading()) {
                            player.sendMessage(MsgType.WARN.format("That loot table is being reloaded, try again in a few seconds."));
                            return;
                        }
                        
                        NexusReborn.sendDebugMessage(player, "Loot Table: " + lootTable.getName());
                        
                        List<ItemStack> items = lootTable.generateLoot(new Random().nextInt(maxAmount - 2) + 2);
                        
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
                        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
                        if (nexusPlayer.getToggleValue("vanish")) {
                            nexusPlayer.sendMessage(MsgType.WARN + "You cannot vote for a map while in vanish.");
                            return;
                        }
                        if (lobby != null) {
                            if (lobby.getState() == LobbyState.WAITING || lobby.getState() == LobbyState.COUNTDOWN) {
                                lobby.addMapVote(nexusPlayer, block.getLocation());
                            }
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
    public void onSpecchatToggleChange(ToggleChangeEvent e) {
        if (!e.getToggle().getInfo().getName().equalsIgnoreCase("spectatorchat")) {
            return;
        }
        
        NexusPlayer nexusPlayer = e.getNexusPlayer();
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());
        if (sgPlayer == null) {
            return;
        }
        
        if (sgPlayer.getGame() == null) {
            return;
        }
        
        GameTeamChatroom specatorChatroom = sgPlayer.getGame().getChatRooms().get(GameTeam.SPECTATORS);
        if (sgPlayer.getGame() != null) {
            if (!specatorChatroom.isMember(nexusPlayer.getUniqueId())) {
                specatorChatroom.addMember(nexusPlayer.getUniqueId(), DefaultPermissions.VIEW_MESSAGES);
            }
        }
    }
    
    @EventHandler
    public void onToggleChange(ToggleChangeEvent e) {
        NexusPlayer nexusPlayer = e.getNexusPlayer();
        boolean incognito = nexusPlayer.getToggleValue("incognito");
        Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
        Toggle toggle = e.getToggle();
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();
        
        if (lobby != null && lobby.checkMapEditing(player)) {
            return;
        }
        
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
            if (lobby != null) {
                lobby.getPlayers().forEach(p -> players.add(p.getPlayer()));
            }
        } else {
            for (GamePlayer value : game.getPlayers().values()) {
                players.add(value.getNexusPlayer());
            }
        }
        
        String symbolColor = !e.newValue() ? "a" : "c";
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
                if (lobby != null) {
                    lobby.sendMessage(message);
                }
            } else {
                if (e.newValue()) {
                    GamePlayer gamePlayer = game.getPlayer(nexusPlayer.getUniqueId());
                    if (gamePlayer.getTeam() != GameTeam.SPECTATORS) {
                        game.killPlayer(gamePlayer, new DeathInfo(game, System.currentTimeMillis(), gamePlayer, DeathType.VANISH, player.getLocation()));
                    }
                }
                game.sendMessage(message);
            }
        }
    }
    
    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();
        
        if (lobby != null && lobby.checkMapEditing(player)) {
            return;
        }
        if (e.getRightClicked() instanceof ArmorStand || e.getRightClicked() instanceof ItemFrame || e.getRightClicked() instanceof Painting) {
            e.setCancelled(true);
            return;
        }
        
        if (e.getRightClicked() instanceof Villager villager) {
            e.setCancelled(true);
            if (villager.getCustomName() != null && villager.getCustomName().contains("Swag Shack")) {
                if (game == null) {
                    e.getPlayer().sendMessage(StarColors.color(MsgType.WARN + "You cannot open the Swag Shack when not in a game."));
                    return;
                }
                
                if (!game.getSettings().isAllowSwagShack()) {
                    e.getPlayer().sendMessage(StarColors.color(MsgType.WARN + "The Swag Shack is disabled for this game."));
                    return;
                }
                
                GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
                if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                    gamePlayer.sendMessage(MsgType.WARN + "You can only open the Swag Shack as a Tribute.");
                    return;
                }
                
                Bukkit.getScheduler().runTaskLater(plugin, () -> manager.openGUI(new SwagShackMenu(plugin, game, gamePlayer), e.getPlayer()), 1L);
            }
        }
    }
    
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        CraftingInventory inv = e.getInventory();
        if (inv.getRecipe() == null) {
            return;
        }
        
        LootItem lootItem = Items.REGISTRY.getByMaterial(inv.getResult().getType());
        if (lootItem != null) {
            if (!lootItem.getId().startsWith("sacrificial")) {
                inv.setResult(lootItem.getItemStack());
            }
        }
    }
    
    @EventHandler
    public void onPlayerOpenEnchant(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();
        
        if (lobby != null && lobby.checkMapEditing(player)) {
            return;
        }
        
        if (game != null) {
            if (e.getInventory() instanceof EnchantingInventory enchantingInventory) {
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
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();
        
        if (lobby != null && lobby.checkMapEditing(player)) {
            return;
        }
        
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
        Player player = e.getPlayer();
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Lobby lobby = sgPlayer.getLobby();
        Game game = sgPlayer.getGame();
        
        if (lobby != null && lobby.checkMapEditing(player)) {
            return;
        }
        if (game != null) {
            e.setCancelled(game.getPlayer(e.getPlayer().getUniqueId()).getTeam() != GameTeam.TRIBUTES);
        } else {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Location deathLocation = player.getLocation().clone();
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        
        if (game == null) {
            return;
        }
        
        e.setDeathMessage(null);
        
        GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        
        if (gamePlayer.getTeam() != GameTeam.TRIBUTES && player.isOnline()) {
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
                    if (game.getSettings().isSacrifices()) {
                        if (killerInfo.getHandItem() != null && killerInfo.getHandItem().getItemMeta().getDisplayName() != null) {
                            if (killerInfo.getHandItem().getItemMeta().getDisplayName().contains("Sacrificial")) {
                                deathType = DeathType.SACRIFICE;
                            }
                        }
                    }
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
        
        DeathInfo deathInfo = new DeathInfo(game, System.currentTimeMillis(), gamePlayer, deathType, deathLocation, killerInfo);
        game.killPlayer(gamePlayer, deathInfo);
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
        
        SGPlayer sgPlayer = new SGPlayer(nexusPlayer);
        
        SGPlayerStats stats;
        try {
            stats = NexusReborn.getPrimaryDatabase().get(SGPlayerStats.class, "uniqueid", e.getNexusPlayer().getUniqueId()).getFirst();
        } catch (Throwable ex) {
            stats = new SGPlayerStats(e.getNexusPlayer().getUniqueId());
        }
        
        NickSGPlayerStats fakeStats;
        try {
            fakeStats = NexusReborn.getPrimaryDatabase().get(NickSGPlayerStats.class, "uniqueid", e.getNexusPlayer().getUniqueId()).getFirst();
            
            if (nexusPlayer.getNickname() != null) {
                fakeStats.setPersist(nexusPlayer.getNickname().isPersist());
            }
            
        } catch (Throwable ex) {
            fakeStats = null;
        }
        
        sgPlayer.setStats(stats);
        sgPlayer.setNickSGPlayerStats(fakeStats);
        
        plugin.getPlayerRegistry().register(sgPlayer);
        e.setJoinMessage(null);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
        if (sgPlayer == null) {
            return;
        }
        
        NexusPlayer nexusPlayer = sgPlayer.getNexusPlayer();
        if (nexusPlayer != null) {
            return;
        }
        if (sgPlayer.getGame() != null) {
            sgPlayer.getGame().quit(nexusPlayer);
        }
        
        if (sgPlayer.getLobby() != null) {
            sgPlayer.getLobby().removePlayer(nexusPlayer);
        }
        
        e.setQuitMessage(null);
        NexusReborn.getPrimaryDatabase().saveSilent(sgPlayer.getStats());
        NexusReborn.getPrimaryDatabase().saveSilent(sgPlayer.getTrueStats());
        plugin.getPlayerRegistry().unregister(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block fromBlock = e.getFrom().getBlock();
        Block toBlock = e.getTo().getBlock();
        
        double fromX = fromBlock.getX();
        double fromZ = fromBlock.getZ();
        double toX = toBlock.getX();
        double toZ = toBlock.getZ();
        
        if ((fromBlock.getType() == Material.WATER || fromBlock.getType() == Material.STATIONARY_WATER) &&
                toBlock.getType() == Material.WATER || toBlock.getType() == Material.STATIONARY_WATER) {
            
            if (fromX == toX && fromZ == toZ) {
                return;
            }
            
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            if (sgPlayer == null) {
                return;
            }
            
            if (sgPlayer.getGame() == null) {
                return;
            }
            
            GamePlayer gamePlayer = sgPlayer.getGamePlayer();
            if (gamePlayer == null) {
                return;
            }
            
            if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
                return;
            }
            
            if (gamePlayer.getMutation() == null) {
                return;
            }
            
            if (!gamePlayer.hasPotionEffect(PotionEffectType.SPEED)) {
                return;
            }
            
            int level = gamePlayer.getEffectLevel(PotionEffectType.SPEED);
            
            Vector changed = e.getTo().clone().subtract(e.getFrom()).toVector().multiply(1 + .2 * level);
            changed.setY(e.getTo().clone().subtract(e.getFrom()).toVector().getY());
            player.setVelocity(changed);
        }
    }
    
    @EventHandler
    public void onNickSet(NicknameSetEvent e) {
        NexusPlayer nexusPlayer = e.getNexusPlayer();
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(nexusPlayer.getUniqueId());
        if (sgPlayer == null) {
            return;
        }
        
        if (sgPlayer.getNickSGPlayerStats() != null) {
            sgPlayer.getNickSGPlayerStats().setPersist(e.getNickname().isPersist());
        } else {
            sgPlayer.setNickSGPlayerStats(new NickSGPlayerStats(nexusPlayer.getUniqueId(), sgPlayer.getTrueStats(), e.getNickname().isPersist()));
        }
    }
    
    @EventHandler
    public void onNickRemove(NicknameRemoveEvent e) {
        NexusReborn.getPrimaryDatabase().deleteSilent(NickSGPlayerStats.class, e.getNexusPlayer().getUniqueId().toString(), new Object[]{"persist"}, new Object[]{false});
    }
}
