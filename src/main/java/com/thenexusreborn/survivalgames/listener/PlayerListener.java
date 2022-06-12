package com.thenexusreborn.survivalgames.listener;

import com.google.common.io.*;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.util.Operator;
import com.thenexusreborn.nexuscore.api.events.*;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.*;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.loot.Loot;
import com.thenexusreborn.survivalgames.lootv2.LootManager;
import com.thenexusreborn.survivalgames.menu.TeamMenu;
import com.thenexusreborn.survivalgames.settings.ColorMode;
import com.thenexusreborn.survivalgames.util.SGUtils;
import me.vagdedes.spartan.api.PlayerViolationEvent;
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

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class PlayerListener implements Listener {
    private SurvivalGames plugin;
    
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
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerViolation(PlayerViolationEvent e) {
        Game game = plugin.getGame();
        if (game != null) {
            if (game.getState() == GameState.WARMUP || game.getState() == GameState.DEATHMATCH_WARMUP) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (plugin.getLobby().checkMapEditing(e.getPlayer())) {
            return;
        }
        
        Game game = plugin.getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(e.getPlayer().getUniqueId());
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
                                    gamePlayer.sendMessage("&6&l>> &cThat feature is not implemented yet.");
                                } else if (displayName.toLowerCase().contains("spectators")) {
                                    team = GameTeam.SPECTATORS;
                                }
                                
                                if (team != null) {
                                    e.getPlayer().openInventory(new TeamMenu(plugin, team).getInventory());
                                }
                            }
                        } else if (item.getType() == Material.ROTTEN_FLESH) {
                            gamePlayer.sendMessage("&6&l>> &cThat is currently not implemented.");
                        } else if (item.getType() == Material.WATCH) {
                            e.getPlayer().teleport(game.getGameMap().getCenter().toLocation(game.getGameMap().getWorld()));
                            gamePlayer.sendMessage("&6&l>> &eTeleported to the Map Center.");
                        } else if (item.getType() == Material.WOOD_DOOR) {
                            gamePlayer.sendMessage("&6&l>> &eSending you to the hub.");
                            SGUtils.sendToHub(e.getPlayer());
                        }
                    }
                }
                
                
                e.setCancelled(true);
                return;
            }
        } else {
            Block block = e.getClickedBlock();
            if (block != null) {
                if (block.getType() == Material.NOTE_BLOCK || block.getType() == Material.CHEST) {
                    e.setCancelled(true);
                }
            }
            
            return;
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
                    if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                        if (game == null) {
                            return;
                        }
                        if (game.getPlayer(e.getPlayer().getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                            e.setCancelled(true);
                            return;
                        }
                        if (game.isLootedChest(block)) {
                            return;
                        }
                        
                        game.getPlayer(e.getPlayer().getUniqueId()).getNexusPlayer().changeStat("sg_chests_looted", 1, Operator.ADD);
                        
                        Inventory inv = ((Chest) block.getState()).getBlockInventory();
                        int maxAmount = 6;
                        
                        Block secondHalf = null;
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
                        
                        inv.clear();
                        List<ItemStack> items = new ArrayList<>();
                        if (game.getLootChances() == null || !game.getSettings().isUseNewLoot()) {
                            List<Loot> loot = plugin.getLootManager().generateLoot(new Random().nextInt(maxAmount) + 2);
                            for (Loot l : loot) {
                                items.add(l.generateItemStack());
                            }
                        } else {
                            items = LootManager.getInstance().getLootTable("basic").generateLoot(new Random().nextInt(maxAmount) + 2, game.getLootChances());
                        }
                        
                        for (ItemStack item : items) {
                            int slot;
                            do {
                                slot = new Random().nextInt(inv.getSize());
                            } while (inv.getItem(slot) != null);
                            inv.setItem(slot, item);
                        }
                        
                        game.addLootedChest(block.getLocation());
                        if (secondHalf != null) {
                            game.addLootedChest(secondHalf.getLocation());
                        }
                    } else if (block.getType() == Material.ENDER_CHEST) {
                        if (game == null) {
                            return;
                        }
                        
                        if (game.getPlayer(e.getPlayer().getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                            e.setCancelled(true);
                            return;
                        }
                        
                        if (!game.getSettings().isAllowEnderchests()) {
                            e.getPlayer().sendMessage(MCUtils.color(MsgType.WARN + "You cannot open ender chests."));
                            e.setCancelled(true);
                            return;
                        }
                        
                        Inventory inventory = game.getEnderchestInventories().get(block.getLocation());
                        if (inventory == null) {
                            inventory = Bukkit.createInventory(null, 27, "Ender Chest");
                            game.getEnderchestInventories().put(block.getLocation(), inventory);
                        }
                        
                        if (!game.isLootedChest(block)) {
                            List<ItemStack> items = new ArrayList<>();
                            if (!game.getSettings().isUseNewLoot()) {
                                List<Loot> loot = plugin.getLootManager().generateLoot(new Random().nextInt(6) + 2);
                                inventory.clear();
                                for (Loot l : loot) {
                                    items.add(l.generateItemStack());
                                }
                            } else {
                                items = LootManager.getInstance().getLootTable("basic").generateLoot(new Random().nextInt(6) + 2, game.getLootChances());
                            }
                            
                            for (ItemStack item : items) {
                                int slot;
                                do {
                                    slot = new Random().nextInt(inventory.getSize());
                                } while (inventory.getItem(slot) != null);
                                inventory.setItem(slot, item);
                            }
                            
                            game.addLootedChest(block.getLocation());
                        }
                        
                        Inventory finalInventory = inventory;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                e.getPlayer().openInventory(finalInventory);
                            }
                        }.runTaskLater(plugin, 1L);
                    } else if (block.getState() instanceof Sign) {
                        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(e.getPlayer().getUniqueId());
                        if (nexusPlayer.getPreferences().get("vanish").getValue()) {
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
    public void onVanishToggle(VanishToggleEvent e) {
        String coloredName = e.getNexusPlayer().getRank().getColor() + e.getNexusPlayer().getName();
        Game game = plugin.getGame();
        
        Collection<SpigotNexusPlayer> players;
        if (game == null) {
            players = plugin.getLobby().getPlayers();
        } else {
            players = new ArrayList<>();
            for (GamePlayer value : game.getPlayers().values()) {
                players.add(value.getNexusPlayer());
            }
        }
        
        String message;
        boolean incognito = e.getNexusPlayer().getPreferences().get("incognito").getValue();
        if (e.getNewValue()) {
            if (incognito) {
                message = "";
            } else {
                message = "&c&l<< " + coloredName + " &eleft.";
                if (game != null) {
                    GamePlayer gamePlayer = game.getPlayers().get(e.getNexusPlayer().getUniqueId());
                    if (gamePlayer.getTeam() == GameTeam.TRIBUTES || gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                        game.killPlayer(gamePlayer.getUniqueId(), new DeathInfoVanish(gamePlayer.getUniqueId()));
                    }
                }
            }
        } else {
            if (incognito) {
                message = "&a&l>> " + coloredName + " &ejoined &e&osilently&e.";
            } else {
                message = "&a&l>> " + coloredName + " &ejoined.";
            }
        }
    
        if (!message.equals("")) {
            if (incognito) {
                for (SpigotNexusPlayer player : players) {
                    if (player.getRank().ordinal() <= Rank.HELPER.ordinal() || player.getUniqueId().equals(e.getNexusPlayer().getUniqueId())) {
                        player.sendMessage(message);
                    }
                }
            } else {
                if (game == null) {
                    plugin.getLobby().sendMessage(message);
                } else {
                    game.recalculateVisibiltiy();
                    game.sendMessage(message);
                }
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
        if (inv.getRecipe() == null) return; 
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
            if (e.getInventory() instanceof EnchantingInventory) {
                Game game = plugin.getGame();
                if (game.getPlayer(e.getPlayer().getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                    e.setCancelled(true);
                    return;
                }
                
                EnchantingInventory enchantingInventory = (EnchantingInventory) e.getInventory();
                enchantingInventory.setSecondary(new ItemStack(Material.INK_SACK, 64, (short) 4));
            }
        }
        //TODO custom guis when implemented
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
                EnchantingInventory enchantingInventory = (EnchantingInventory) e.getInventory();
                if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.INK_SACK)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerCloseEnchant(InventoryCloseEvent e) {
        try {
            if (e.getInventory() instanceof EnchantingInventory) {
                EnchantingInventory enchantingInventory = (EnchantingInventory) e.getInventory();
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
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (plugin.getLobby().checkMapEditing(e.getEntity())) {
            return;
        }
        e.setDeathMessage(null);
        
        Game game = plugin.getGame();
        if (game == null) {
            return;
        }
        Player player = e.getEntity();
        GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
            return;
        }
        
        DeathInfo deathInfo = null;
        final Player killer = player.getKiller();
        if (killer != null && player.getLastDamageCause().getCause() != DamageCause.PROJECTILE) {
            GamePlayer killerPlayer = game.getPlayer(killer.getUniqueId());
            if (player.getLastDamageCause().getCause() == DamageCause.ENTITY_EXPLOSION) {
                deathInfo = new DeathInfoTntKill(player.getUniqueId(), killerPlayer.getUniqueId(), killer.getHealth(), game.getPlayer(killer.getUniqueId()).getTeam().getColor());
            } else {
                deathInfo = new DeathInfoPlayerKill(player.getUniqueId(), killerPlayer.getUniqueId(), killer.getItemInHand(), killer.getHealth(), game.getPlayer(killer.getUniqueId()).getTeam().getColor());
            }
        } else {
            EntityDamageEvent lastDamageCause = player.getLastDamageCause();
            DamageCause damageCause;
            if (lastDamageCause == null) {
                damageCause = DamageCause.VOID;
            } else {
                damageCause = lastDamageCause.getCause();
            }
            
            String teamColor = gamePlayer.getTeam().getColor();
            if (damageCause == DamageCause.PROJECTILE) {
                if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) lastDamageCause;
                    if (edbee.getDamager() instanceof Projectile) {
                        Projectile projectile = (Projectile) edbee.getDamager();
                        if (projectile.getShooter() instanceof Entity) {
                            Entity entity = ((Entity) projectile.getShooter());
                            String color = "";
                            if (entity instanceof Player) {
                                if (game.getSettings().getColorMode() == ColorMode.GAME_TEAM) {
                                    color = game.getPlayer(entity.getUniqueId()).getTeam().getColor();
                                } else {
                                    color = game.getPlayer(entity.getUniqueId()).getNexusPlayer().getRank().getColor();
                                }
                            }
                            deathInfo = new DeathInfoProjectile(player.getUniqueId(), ((Entity) projectile.getShooter()), player.getLocation().distance(entity.getLocation()), color, ((LivingEntity) projectile.getShooter()).getHealth());
                        }
                    }
                }
            } else if (damageCause == DamageCause.SUFFOCATION) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.SUFFOCATION, "&4&l>> %playername% &7suffocated to death.", teamColor);
            } else if (damageCause == DamageCause.FALL) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FALL, "&4&l>> %playername% &7fell to their death.", teamColor);
            } else if (damageCause == DamageCause.LAVA) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.LAVA, "&4&l>> %playername% &7roasted in lava.", teamColor);
            } else if (damageCause == DamageCause.FIRE || damageCause == DamageCause.FIRE_TICK) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FIRE, "&4&l>> %playername% &7burned to death.", teamColor);
            } else if (damageCause == DamageCause.DROWNING) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.DROWNING, "&4&l>> %playername% &7drowned.", teamColor);
            } else if (damageCause == DamageCause.ENTITY_EXPLOSION || damageCause == DamageCause.BLOCK_EXPLOSION) {
                if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbe = ((EntityDamageByEntityEvent) player.getLastDamageCause());
                    Location dLoc = edbe.getDamager().getLocation().clone();
                    Location loc = new Location(dLoc.getWorld(), dLoc.getBlockX(), dLoc.getBlockY(), dLoc.getBlockZ());
//                    if (game.getSuicideLocations().containsKey(loc)) {
//                        UUID cause = game.getSuicideLocations().get(loc);
//                        deathInfo = new DeathInfoKilledSuicide(player.getUniqueId(), cause, game.getGameTeam(cause).getColor());
//                    } else {
                    //}
                }
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.EXPLOSION, "&4&l>> %playername% &7exploded.", teamColor);
            } else if (damageCause == DamageCause.VOID) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.VOID, "&4&l>> %playername% &7fell in the void.", teamColor);
            } else if (damageCause == DamageCause.LIGHTNING) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.LIGHTNING, "&4&l>> %playername% &7was struck by lightning and died.", teamColor);
            } else if (damageCause == DamageCause.SUICIDE) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.SUICIDE, "&4&l>> %playername% &7died.", teamColor);
            } else if (damageCause == DamageCause.STARVATION) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.STARVATION, "&4&l>> %playername% &7starved to death.", teamColor);
            } else if (damageCause == DamageCause.POISON) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.POISON, "&4&l>> %playername% &7was poisoned to death.", teamColor);
            } else if (damageCause == DamageCause.MAGIC) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.MAGIC, "&4&l>> %playername% &7was killed by magic.", teamColor);
            } else if (damageCause == DamageCause.WITHER) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.WITHER, "&4&l>> %playername% &7withered away.", teamColor);
            } else if (damageCause == DamageCause.MELTING) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.WITHER, "&4&l>> %playername% &7melted to death.", teamColor);
            } else if (damageCause == DamageCause.FALLING_BLOCK) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.FALLING_BLOCK, "&4&l>> %playername% &7had a block fall on their head.", teamColor);
            } else if (damageCause == DamageCause.THORNS) {
                deathInfo = new DeathInfo(player.getUniqueId(), DeathType.THORNS, "&4&l>> %playername% &7was poked to death by armor.", teamColor);
            } else if (damageCause == DamageCause.ENTITY_ATTACK) {
                if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) lastDamageCause;
                    Entity damager = edbee.getDamager();
                    if (damager instanceof Player) {
                        deathInfo = new DeathInfoSuicide(player.getUniqueId());
                    } else {
                        deathInfo = new DeathInfoEntity(player.getUniqueId(), damager.getType());
                    }
                }
            }
        }
        
        if (deathInfo == null) {
            deathInfo = new DeathInfo(player.getUniqueId(), DeathType.UNKNOWN, "%playername% &7died to unknown reasons.", gamePlayer.getTeam().getColor());
        }
        
        Location deathLocation = player.getLocation().clone();
        DeathInfo finalDeathInfo = deathInfo;
        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
                if (e.getEntity().getLastDamageCause().getCause() == DamageCause.VOID) {
                    player.teleport(game.getGameMap().getCenter().toLocation(game.getGameMap().getWorld()));
                } else {
                    player.teleport(deathLocation);
                }
                game.killPlayer(e.getEntity().getUniqueId(), finalDeathInfo);
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
        SpigotNexusPlayer nexusPlayer = (SpigotNexusPlayer) e.getNexusPlayer();
        if (plugin.getGame() == null) {
            if (plugin.getLobby().getPlayingCount() >= plugin.getLobby().getLobbySettings().getMaxPlayers()) {
                boolean isStaff = nexusPlayer.getRank().ordinal() <= Rank.HELPER.ordinal();
                boolean isInVanish = nexusPlayer.getPreferences().get("vanish").getValue();
                if (!(isStaff && isInVanish)) {
                    nexusPlayer.sendMessage("&cThe lobby is full.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("Connect");
                            out.writeUTF("H1");
                            nexusPlayer.getPlayer().sendPluginMessage(plugin.getNexusCore(), "BungeeCord", out.toByteArray());
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
            plugin.getGame().removePlayer((SpigotNexusPlayer) nexusPlayer);
        } else {
            plugin.getLobby().removePlayer((SpigotNexusPlayer) nexusPlayer);
        }
        e.setQuitMessage(null);
    }
}
