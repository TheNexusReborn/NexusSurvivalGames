package com.thenexusreborn.survivalgames.listener;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.stardevllc.helper.Pair;
import com.stardevllc.starchat.rooms.DefaultPermissions;
import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.base.XMaterial;
import com.stardevllc.starcore.base.itembuilder.ItemBuilder;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.nickname.NickTime;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.api.events.*;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.chat.GameTeamChatroom;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.*;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.loot.LootManager;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import com.thenexusreborn.survivalgames.menu.MutateGui;
import com.thenexusreborn.survivalgames.menu.SwagShackMenu;
import com.thenexusreborn.survivalgames.mutations.*;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import com.thenexusreborn.survivalgames.mutations.impl.CreeperMutation;
import com.thenexusreborn.survivalgames.util.*;
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

    public PlayerListener(SurvivalGames plugin) {
        this.plugin = plugin;
        manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
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

        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getItem() != null) {
                        ItemStack item = e.getItem();
                        if (item.getType() == Material.ROTTEN_FLESH) {
                            Pair<Boolean, String> canMutateResult = gamePlayer.canMutate();
                            if (canMutateResult.key()) {
                                MutationBuilder mutationBuilder = new MutationBuilder(gamePlayer);
                                mutationBuilder.setUsePass(true);
                                manager.openGUI(new MutateGui(plugin, mutationBuilder), player);
                            } else {
                                gamePlayer.sendMessage(MsgType.WARN + canMutateResult.value());
                            }
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
                            player.sendMessage(StarColors.color(MsgType.WARN + "Chicken Launch is still on cooldown: &e" + TimeUnit.MILLISECONDS.toSeconds(chickenMutation.getLaunchCooldownTimer().getTime()) + "s&c!"));
                        }
                    } else if (item.getType() == Material.FEATHER) {
                        if (!chickenMutation.isParachuteOnCooldown()) {
                            if (chickenMutation.isChuteActive()) {
                                chickenMutation.deactivateChute();
                            } else {
                                chickenMutation.activateChute();
                            }
                        } else {
                            player.sendMessage(StarColors.color(MsgType.WARN + "Chicken Chute is still on cooldown: &e" + TimeUnit.MILLISECONDS.toSeconds(chickenMutation.getParachuteCooldownTimer().getTime()) + "s&c!"));
                        }
                    }
                }
            }
        } else {
            Block block = e.getClickedBlock();
            if (block != null) {
                if (block.getType() == Material.NOTE_BLOCK || block.getType() == Material.CHEST || block.getType().name().contains("REDSTONE")) {
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
                for (LobbyPlayer lp : lobby.getPlayers()) {
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
                    XMaterial sponsorsItemMaterial = sponsorsValue ? XMaterial.GLOWSTONE_DUST : XMaterial.GUNPOWDER;
                    String statusMessage = sponsorsValue ? "&a&lENABLED" : "&c&lDISABLED";
                    player.getInventory().setItem(0, ItemBuilder.of(sponsorsItemMaterial).displayName("&e&lSponsors " + statusMessage + " &7&o(Right Click to toggle)").build());
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

                        boolean useTieredLoot = game.getSettings().isUseNewLoot();
                        if (!useTieredLoot) {
                            lootTable = lootManager.getLootTable("tierOne");
                        } else {
                            if (game.getState() == Game.State.DEATHMATCH) {
                                lootTable = lootManager.getLootTable("tierFour");
                            } else {
                                boolean withinCenter = game.getGameMap().getDeathmatchRegion().contains(BukkitUtil.toVector(player.getLocation()));
                                if (game.getState() == Game.State.INGAME || game.getState() == Game.State.INGAME_DEATHMATCH) {
                                    boolean afterRestock = game.getTimedRestockCount() > 0;
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
                            player.sendMessage(StarColors.color(MsgType.ERROR + "Error while determining the loot table."));
                            return;
                        }

                        if (lootTable.isReloading()) {
                            player.sendMessage(MsgType.WARN.format("That loot table is being reloaded, try again in a few seconds."));
                            return;
                        }

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
                        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
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
        if (inv.getRecipe().getResult().getType() == Material.FLINT_AND_STEEL) {
            ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
            itemStack.setDurability((short) (Material.FLINT_AND_STEEL.getMaxDurability() - 4));
            inv.setResult(itemStack);
        } else if (inv.getRecipe().getResult().getType() == Material.DIAMOND_AXE) {
            ItemStack itemStack = new ItemStack(Material.DIAMOND_AXE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(StarColors.color("&fBetty"));
            itemStack.setItemMeta(itemMeta);
            inv.setResult(itemStack);
        } else if (inv.getRecipe().getResult().getType() == Material.IRON_AXE) {
            ItemStack itemStack = new ItemStack(Material.IRON_AXE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(StarColors.color("&fFrederick"));
            itemStack.setItemMeta(itemMeta);
            inv.setResult(itemStack);
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

        DeathInfo deathInfo = new DeathInfo(game, System.currentTimeMillis(), gamePlayer, deathType, deathLocation, killerInfo);

        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
                if (e.getEntity().getLastDamageCause().getCause() == DamageCause.VOID) {
                    player.teleport(game.getGameMap().getSpawnCenter().toLocation(game.getGameMap().getWorld()));
                } else {
                    player.teleport(deathLocation);
                }
                gamePlayer.setPosition(player.getLocation());
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

        SGPlayer sgPlayer = new SGPlayer(nexusPlayer);

        SGPlayerStats stats;
        try {
            stats = NexusAPI.getApi().getPrimaryDatabase().get(SGPlayerStats.class, "uniqueid", e.getNexusPlayer().getUniqueId()).getFirst();
        } catch (Throwable ex) {
            stats = new SGPlayerStats(e.getNexusPlayer().getUniqueId());
        }
        
        NickSGPlayerStats fakeStats;
        try {
            fakeStats = NexusAPI.getApi().getPrimaryDatabase().get(NickSGPlayerStats.class, "uniqueid", e.getNexusPlayer().getUniqueId()).getFirst();
            
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
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(sgPlayer.getStats());
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(sgPlayer.getTrueStats());
        plugin.getPlayerRegistry().unregister(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block fromBlock = e.getFrom().getBlock();
        Block toBlock = e.getTo().getBlock();
        
        if ((fromBlock.getType() == Material.WATER || fromBlock.getType() == Material.STATIONARY_WATER) && 
                toBlock.getType() == Material.WATER || toBlock.getType() == Material.STATIONARY_WATER) {
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
            
            if (gamePlayer.getMutation().getType() != MutationType.PIG_ZOMBIE) {
                return;
            }
            
            Vector changed = e.getTo().clone().subtract(e.getFrom()).toVector().multiply(1.4);
            changed.setY(e.getTo().clone().subtract(e.getFrom()).toVector().getY());
            player.setVelocity(changed);
        }
    }
    
    @EventHandler
    public void onNickRemove(NicknameRemoveEvent e) {
        NexusAPI.getApi().getPrimaryDatabase().deleteSilent(NickTime.class, e.getNexusPlayer().getUniqueId().toString(), new Object[]{"persist"}, new Object[]{false});
    }
}
