package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.clock.clocks.Timer;
import com.stardevllc.colors.StarColors;
import com.stardevllc.converter.string.StringConverter;
import com.stardevllc.converter.string.StringConverters;
import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.time.TimeFormat;
import com.stardevllc.time.TimeParser;
import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.MapManager;
import com.thenexusreborn.gamemaps.YamlMapManager;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.ControlType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.gamelog.GameCmdAction;
import com.thenexusreborn.survivalgames.gamelog.GameGiveAction;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.lobby.StatSign;
import com.thenexusreborn.survivalgames.lobby.TributeSign;
import com.thenexusreborn.survivalgames.loot.item.Items;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import com.thenexusreborn.survivalgames.map.SQLMapManager;
import com.thenexusreborn.survivalgames.menu.TeamMenu;
import com.thenexusreborn.survivalgames.mutations.MutationType;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.LobbySettings;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class SGCommand implements CommandExecutor {

    private final SurvivalGames plugin;

    private Map<UUID, Runnable> settingsConfirmation = new HashMap<>();

    public SGCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(MsgType.WARN.format("You do not have permission to use that command."));
            return true;
        }

        if (!(args.length > 0)) {
            sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MsgType.WARN.format("Only players can use this command. (Temporary)")); //TODO Fix this
            return true;
        }

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        Lobby lobby = sgPlayer.getLobby();

        String subCommand = args[0].toLowerCase();
        if (subCommand.equals("game") || subCommand.equals("g")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
                return true;
            }

            String gameSubCommand = args[1].toLowerCase();

            if (List.of("automatic", "auto", "manual", "man").contains(gameSubCommand)) {
                ControlType controlType = null;
                try {
                    controlType = ControlType.valueOf(gameSubCommand.toUpperCase());
                } catch (Exception e) {
                    if (gameSubCommand.equals("auto")) {
                        controlType = ControlType.AUTO;
                    } else if (gameSubCommand.equals("man")) {
                        controlType = ControlType.MANUAL;
                    }
                }

                if (controlType == null) {
                    sender.sendMessage(MsgType.SEVERE.format("Invalid control type detection. This is a bug, please report to Firestar311"));
                    return true;
                }

                if (game.getControlType() == controlType) {
                    sender.sendMessage(MsgType.WARN.format("The game is already in %v.", controlType.name().toLowerCase()));
                    return true;
                }

                game.setControlType(controlType);
                sender.sendMessage(MsgType.INFO.format("You set the game to %v control.", controlType.name().toLowerCase()));
                return true;
            }

            if (game == null) {
                sender.sendMessage(MsgType.WARN.format("There is no prepared/running game."));
                return true;
            }

            if (game.getState() == GameState.ERROR) {
                sender.sendMessage(MsgType.WARN.format("The game had an error, you cannot modify it."));
                return true;
            }

            GuiManager guiManager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
            switch (gameSubCommand) {
                case "tributes", "tr" ->
                        guiManager.openGUI(new TeamMenu(plugin, GameTeam.TRIBUTES, game, player.getUniqueId()), player);
                case "spectators", "sp" ->
                        guiManager.openGUI(new TeamMenu(plugin, GameTeam.SPECTATORS, game, player.getUniqueId()), player);
                case "setup" -> {
                    if (game.getState() != GameState.UNDEFINED) {
                        sender.sendMessage(MsgType.WARN.format("The game has already been setup."));
                        return true;
                    }
                    game.setup();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (game.getState() == GameState.SETUP_COMPLETE) {
                                sender.sendMessage(MsgType.INFO.format("The game setup is now complete."));
                                cancel();
                            } else if (game.getState() == GameState.ERROR) {
                                sender.sendMessage(MsgType.ERROR.format("There was a problem during Game Setup"));
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 1L, 1L);
                }
                case "assignstartingteams", "ast" -> {
                    if (game.getState() == GameState.SETUP_COMPLETE) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "assignstartingteams"));
                        game.assignStartingTeams();
                        if (game.getState() == GameState.TEAMS_ASSIGNED) {
                            sender.sendMessage(MsgType.INFO.format("Starting teams have been assigned."));
                        } else {
                            sender.sendMessage(MsgType.WARN.format("&cThere was a problem assigning starting teams"));
                        }
                    } else {
                        sender.sendMessage(MsgType.WARN.format("The game is not yet setup. Please run the setup task before assigning teams."));
                    }
                }
                case "teleportplayers", "tpp" -> {
                    if (game.getState() == GameState.TEAMS_ASSIGNED) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "teleportplayers"));
                        game.teleportStart();
                        if (game.getState() == GameState.TELEPORT_START_DONE) {
                            sender.sendMessage(MsgType.INFO.format("Players have been teleported."));
                        } else {
                            sender.sendMessage(MsgType.WARN.format("There was a problem teleporting players."));
                        }
                    } else {
                        sender.sendMessage(MsgType.WARN.format("The teams have not be assigned yet. Please run the team assignment task."));
                    }
                }
                case "startwarmupcountdown", "swcd" -> {
                    if (game.getState() == GameState.TELEPORT_START_DONE) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "startupwarmupcountdown"));
                        game.startWarmup();
                        if (game.getState() == GameState.WARMUP || game.getState() == GameState.WARMUP_DONE) {
                            sender.sendMessage(MsgType.INFO.format("The warmup countdown has been started successfully."));
                        } else {
                            sender.sendMessage(MsgType.WARN.format("There was a problem starting the warmup countdown."));
                        }
                    } else {
                        sender.sendMessage(MsgType.WARN.format("You must run the teleport players task before starting the countdown."));
                    }
                }
                case "start" -> {
                    if (game.getState() == GameState.WARMUP_DONE || game.getState() == GameState.TELEPORT_START_DONE) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "start"));
                        game.startGame();
                    } else if (game.getState() == GameState.WARMUP) {
                        if (game.getTimer() != null) {
                            game.getTimer().cancel();
                        } else {
                            sender.sendMessage(MsgType.WARN.format("The game state was in warmup countdown, but no timer was actively running. Please report this as a bug, started game anyways."));
                        }
                        game.startGame();
                    } else if (game.getState() != GameState.TELEPORT_START_DONE) {
                        sender.sendMessage(MsgType.WARN.format("You must run the teleport players task at the minimum before starting the game"));
                        return true;
                    }
                    if (game.getState() == GameState.INGAME) {
                        sender.sendMessage(MsgType.INFO.format("The game has been started."));
                    } else {
                        sender.sendMessage(MsgType.WARN.format("There was a problem starting the game."));
                    }
                }
                case "startdeathmatchcountdown", "sdmcd" -> {
                    if (game.getState() == GameState.INGAME) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "startdeathmatchcountdown"));
                        game.startDeathmatchTimer();
                        if (game.getState() == GameState.INGAME_DEATHMATCH) {
                            sender.sendMessage(MsgType.INFO.format("You started the deathmatch timer"));
                        } else {
                            sender.sendMessage(MsgType.WARN.format("There was a problem starting the deathmatch timer."));
                        }
                    } else {
                        sender.sendMessage(MsgType.WARN.format("Invalid state. Please ensure that the game is in the INGAME state."));
                    }
                }
                case "teleportdeathmatch", "tpdm" -> {
                    if (game.getState().ordinal() >= GameState.INGAME.ordinal() && game.getState().ordinal() <= GameState.INGAME_DEATHMATCH.ordinal()) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "teleportdeathmatch"));
                        game.teleportDeathmatch();
                        if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                            sender.sendMessage(MsgType.INFO.format("You teleported everyone to the deathmatch"));
                        } else {
                            sender.sendMessage(MsgType.WARN.format("There was a problem teleporting players to the deathmatch."));
                        }
                    } else {
                        sender.sendMessage(MsgType.WARN.format("Invalid game state. Must be ingame, ingame deathmatch."));
                    }
                }
                case "startdeathmatchwarmup", "sdmw" -> {
                    if (game.getState() == GameState.TELEPORT_DEATHMATCH_DONE) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "startdeathmatchwarmup"));
                        game.startDeathmatchWarmup();
                        sender.sendMessage(MsgType.INFO.format("You started the deathmatch warmup"));
                    } else {
                        sender.sendMessage(MsgType.WARN.format("The players have not been teleported to the deathmatch, or the deathmatch has already started."));
                    }
                }
                case "startdeathmatch", "sdm" -> {
                    if (Stream.of(GameState.TELEPORT_DEATHMATCH_DONE, GameState.DEATHMATCH_WARMUP, GameState.DEATHMATCH_WARMUP_DONE).anyMatch(gameState -> game.getState() == gameState)) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "startdeathmatch"));
                        game.startDeathmatch();
                        sender.sendMessage(MsgType.INFO.format("You started the deathmatch"));
                    } else {
                        sender.sendMessage(MsgType.WARN.format("You must at least teleport players to the deathmatch, or it cannot have been started already."));
                    }
                }
                case "end" -> {
                    if (game.getState() != GameState.ENDING && game.getState() != GameState.ENDED) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "end"));
                        game.end();
                        sender.sendMessage(MsgType.INFO.format("You ended the game."));
                    } else {
                        sender.sendMessage(MsgType.WARN.format("The game has already ended"));
                    }
                }
                case "restockchests", "rc" -> {
                    if (game.getState().ordinal() >= GameState.INGAME.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "restockchests"));
                        game.restockChests();
                        game.sendMessage("&6&l>> &a&lALL CHESTS HAVE BEEN RESTOCKED");
                    } else {
                        sender.sendMessage(MsgType.WARN.format("Invalid game state. Must be ingame, ingame deathmatch, deathmatch countdown or deathmatch countdown complete."));
                    }
                }
                case "nextgame", "ng" -> {
                    if (game.getState() == GameState.ENDING || game.getState() == GameState.ENDED) {
                        game.getGameInfo().getActions().add(new GameCmdAction(sender.getName(), "nextgame"));
                        game.nextGame();
                        sender.sendMessage(MsgType.INFO.format("Moved everyone to the next game"));
                    } else {
                        sender.sendMessage(MsgType.WARN.format("You must end the game first before going to the next one."));
                    }
                }
                case "give" -> {
                    if (!(args.length > 2)) {
                        sender.sendMessage(MsgType.WARN.format("Usage: /" + label + " " + args[0] + " " + args[1] + " <item> [player]"));
                        return true;
                    }

                    LootItem lootItem = Items.REGISTRY.get(args[2]);

                    if (lootItem == null) {
                        sender.sendMessage(MsgType.WARN.format("Unknown item %v", args[2]));
                        return true;
                    }

                    if (args.length == 3) {
                        player.getInventory().addItem(lootItem.getItemStack());
                        game.getGameInfo().getActions().add(new GameGiveAction(sender.getName(), "themselves", lootItem.getName().toLowerCase().replace(" ", "_")));
                        player.sendMessage(MsgType.INFO.format("You gave yourself %v", lootItem.getName()));
                    } else {
                        Player target = Bukkit.getPlayer(args[3]);
                        if (target == null) {
                            sender.sendMessage(MsgType.WARN.format("Unknown player %v", args[3]));
                            return true;
                        }

                        if (!game.getPlayers().containsKey(target.getUniqueId())) {
                            sender.sendMessage(MsgType.WARN.format("Unknown player %v", args[3]));
                            return true;
                        }

                        target.getInventory().addItem(lootItem.getItemStack());
                        game.getGameInfo().getActions().add(new GameGiveAction(sender.getName(), target.getName(), lootItem.getName().toLowerCase().replace(" ", "_")));
                        target.sendMessage(MsgType.INFO.format("You were given %v by %v", lootItem.getName(), sgPlayer.getNexusPlayer().getColoredName()));
                    }

                }
                case "giveall" -> {
                    if (!(args.length > 2)) {
                        sender.sendMessage(MsgType.WARN.format("Usage: /" + label + " " + args[0] + " " + args[1] + " <item>"));
                        return true;
                    }

                    LootItem lootItem = Items.REGISTRY.get(args[2]);

                    if (lootItem == null) {
                        sender.sendMessage(MsgType.WARN.format("Unknown item %v", args[2]));
                        return true;
                    }

                    for (GamePlayer gamePlayer : game.getPlayers().values()) {
                        if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                            continue;
                        }

                        Bukkit.getPlayer(gamePlayer.getUniqueId()).getInventory().addItem(lootItem.getItemStack());
                    }

                    game.getGameInfo().getActions().add(new GameGiveAction(sender.getName(), "all", lootItem.getName().toLowerCase().replace(" ", "_")));
                    game.sendMessage(MsgType.INFO.format("All players were given %v by %v", lootItem.getName(), player.getName()));
                }
                case "player" -> {
                    if (!(args.length > 3)) {
                        sender.sendMessage(MsgType.WARN.format("Invalid argument count."));
                        return true;
                    }

                    GamePlayer target = game.getPlayer(args[3]);
                    if (target == null) {
                        sender.sendMessage(MsgType.WARN.format("Unknown player %v", args[3]));
                        return true;
                    }

                    String playerSubcommand = args[2].toLowerCase();

                    switch (playerSubcommand) {
                        case "add", "a" -> {
                            // /sg game player add|a <player> [<loottable>:<amount>]
                            if (target.getTeam() != GameTeam.SPECTATORS) {
                                sender.sendMessage(MsgType.WARN.format("%v is not a spectator.", target.getName()));
                                return true;
                            }

                            SGLootTable lootTable = null;
                            int amountOfItems = 0;
                            if (args.length > 4) {
                                String[] lootSplit = args[4].split(":");
                                if (lootSplit.length != 2) {
                                    sender.sendMessage(MsgType.WARN.format("Invalid loot format. Must be %v", "<loottable>:<amount>"));
                                    return true;
                                }

                                lootTable = plugin.getLootManager().getLootTable(lootSplit[0]);
                                if (lootTable == null) {
                                    sender.sendMessage(MsgType.WARN.format("Unknown loot table %v.", lootSplit[0]));
                                    return true;
                                }

                                try {
                                    amountOfItems = Integer.parseInt(lootSplit[1]);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(MsgType.WARN.format("Invalid whole number %v.", lootSplit[1]));
                                    return true;
                                }
                            }

                            game.addAsTribute(sgPlayer, target, lootTable, amountOfItems);
                        }
                        case "remove", "rm" -> {
                            // /sg game player remove|rm <player>
                            if (target.getTeam() == GameTeam.SPECTATORS) {
                                sender.sendMessage(MsgType.WARN.format("%v is already a spectator.", target.getName()));
                                return true;
                            }

                            game.removeFromGame(sgPlayer, target);
                        }
                        case "revive", "rv" -> {
                            // /sg game player revive|rv <player> [<loottable>:<amount>]

                            if (target.getTeam() != GameTeam.SPECTATORS) {
                                sender.sendMessage(MsgType.WARN.format("%v is not a spectator.", target.getName()));
                                return true;
                            }

                            if (!target.isSpectatorByDeath()) {
                                sender.sendMessage(MsgType.WARN.format("%v did not die in this game.", target.getName()));
                                return true;
                            }

                            DeathInfo mostRecentDeath = target.getMostRecentDeath();

                            if (mostRecentDeath == null) {
                                sender.sendMessage(MsgType.WARN.format("%v does not have a most recent death.", target.getName()));
                                return true;
                            }

                            SGLootTable lootTable = null;
                            int amountOfItems = 0;
                            if (args.length > 4) {
                                String[] lootSplit = args[4].split(":");
                                if (lootSplit.length != 2) {
                                    sender.sendMessage(MsgType.WARN.format("Invalid loot format. Must be %v", "<loottable>:<amount>"));
                                    return true;
                                }

                                lootTable = plugin.getLootManager().getLootTable(lootSplit[0]);
                                if (lootTable == null) {
                                    sender.sendMessage(MsgType.WARN.format("Unknown loot table %v.", lootSplit[0]));
                                    return true;
                                }

                                try {
                                    amountOfItems = Integer.parseInt(lootSplit[1]);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(MsgType.WARN.format("Invalid whole number %v.", lootSplit[1]));
                                    return true;
                                }
                            }

                            game.revivePlayer(sgPlayer, target, lootTable, amountOfItems);
                        }

                        case "mutate", "m" -> {
                            // /sg game player mutate|m <player> <type|random|select> [target|killer] [bypasstimer]
                            // The select option needs some backend reworks for mutations to work properly
                            // The random option will not be available yet

                            if (target.getTeam() != GameTeam.SPECTATORS) {
                                sender.sendMessage(MsgType.WARN.format("%v is not a spectator.", target.getName()));
                                return true;
                            }

                            if (!(args.length > 4)) {
                                sender.sendMessage(MsgType.WARN.format("You must specify a mutation type."));
                                return true;
                            }

                            MutationType type = MutationType.getType(args[4]);
                            if (type == null) {
                                sender.sendMessage(MsgType.WARN.format("Invalid mutation type %v.", args[4]));
                                return true;
                            }

                            GamePlayer mutationTarget;
                            if (args.length > 5) {
                                if (args[5].equalsIgnoreCase("killer")) {
                                    UUID killerUUID = target.getMutationTarget();
                                    if (killerUUID == null) {
                                        player.sendMessage(MsgType.WARN.format("%v does not have a recent player killer."));
                                        return true;
                                    }

                                    mutationTarget = game.getPlayer(killerUUID);
                                    if (mutationTarget == null) {
                                        player.sendMessage(MsgType.WARN.format("%v's killer is no longer part of the game."));
                                        return true;
                                    }

                                    if (mutationTarget.getTeam() != GameTeam.TRIBUTES) {
                                        player.sendMessage(MsgType.WARN.format("%v's killer is no longer a tribute."));
                                        return true;
                                    }
                                } else {
                                    mutationTarget = game.getPlayer(args[5]);

                                    if (mutationTarget == null) {
                                        player.sendMessage(MsgType.WARN.format("%v is not a valid player for that game.", args[5]));
                                        return true;
                                    }
                                }
                            } else {
                                UUID killerUUID = target.getMutationTarget();
                                if (killerUUID == null) {
                                    player.sendMessage(MsgType.WARN.format("%v does not have a recent player killer.", target.getName()));
                                    return true;
                                }

                                mutationTarget = game.getPlayer(killerUUID);
                                if (mutationTarget == null) {
                                    player.sendMessage(MsgType.WARN.format("%v's killer is no longer part of the game."));
                                    return true;
                                }

                                if (mutationTarget.getTeam() != GameTeam.TRIBUTES) {
                                    player.sendMessage(MsgType.WARN.format("%v's killer is no longer a tribute.", target.getName()));
                                    return true;
                                }
                            }

                            if (mutationTarget == null) {
                                player.sendMessage(MsgType.SEVERE.format("You shouldn't see this message. Invalid Target, report as a bug."));
                                player.sendMessage(MsgType.SEVERE.format("This message comes up as a result of an unhandled check."));
                                return true;
                            }

                            boolean bypassTimer = false;
                            if (args.length > 6) {
                                bypassTimer = Boolean.parseBoolean(args[6]);
                            }

                            game.mutatePlayer(sgPlayer, target, type, mutationTarget, bypassTimer);
                        }

                        default -> {
                        }
                    }

                }
            }
        } else if (subCommand.equals("lobby") || subCommand.equals("l")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
                return true;
            }

            String lobbySubCommand = args[1].toLowerCase();
            switch (lobbySubCommand) {
                case "forcestart", "fs" -> {
                    if (game != null) {
                        sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
                        return true;
                    }
                    if (lobby.getState() != LobbyState.WAITING) {
                        sender.sendMessage(MsgType.WARN.format("Invalid state to start the lobby."));
                        return true;
                    }
                    lobby.forceStart();
                    sender.sendMessage(MsgType.INFO.format("You forcefully started the lobby."));
                }
                case "map", "m" -> {
                    if (!(args.length > 2)) {
                        sender.sendMessage(MsgType.WARN.format("You must provide a map name."));
                        return true;
                    }
                    if (game != null) {
                        sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
                        return true;
                    }
                    SGMap gameMap = plugin.getMapManager().getMap(SGUtils.getMapNameFromCommand(args, 2));
                    if (gameMap == null) {
                        sender.sendMessage(MsgType.WARN.format("Could not find a map with that name."));
                        return true;
                    }
                    if (!gameMap.isActive()) {
                        sender.sendMessage(MsgType.WARN.format("That map is not active."));
                        return true;
                    }
                    lobby.setGameMap(gameMap);
                    sender.sendMessage(MsgType.INFO.format("You set the map to %v.", gameMap.getName()));
                }
                case "automatic", "auto" -> {
                    if (lobby.getControlType() == ControlType.AUTO) {
                        sender.sendMessage(MsgType.WARN.format("The lobby is already in automatic control."));
                        return true;
                    }
                    lobby.automatic();
                    sender.sendMessage(MsgType.INFO.format("You set the lobby to automatic control."));
                }
                case "manual", "mnl" -> {
                    if (lobby.getControlType() == ControlType.MANUAL) {
                        sender.sendMessage(MsgType.WARN.format("The lobby is already in manual control."));
                        return true;
                    }
                    lobby.manual();
                    sender.sendMessage(MsgType.INFO.format("You set the lobby to manual control."));
                }
                case "editmaps", "em" -> {
                    if (game != null) {
                        sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
                        return true;
                    }
                    if (lobby.getState() == LobbyState.MAP_EDITING) {
                        lobby.stopEditingMaps();
                        sender.sendMessage(MsgType.INFO.format("You stopped editing maps."));
                    } else {
                        lobby.editMaps();
                        sender.sendMessage(MsgType.INFO.format("You started editing maps."));
                    }
                }
                case "mapsigns", "ms" -> {
                    if (game != null) {
                        sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
                        return true;
                    }

                    if (!(args.length > 2)) {
                        sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
                        return true;
                    }

                    Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
                    if (targetBlock == null) {
                        player.sendMessage(MsgType.WARN.format("You are not looking at a block."));
                        return true;
                    }

                    if (!(targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN)) {
                        player.sendMessage(MsgType.WARN.format("You are not looking at a sign."));
                        return true;
                    }

                    if (args[2].equalsIgnoreCase("remove")) {
                        Iterator<Entry<Integer, Location>> iterator = lobby.getMapSigns().entrySet().iterator();
                        while (iterator.hasNext()) {
                            Entry<Integer, Location> entry = iterator.next();
                            if (entry.getValue().equals(targetBlock.getLocation())) {
                                iterator.remove();
                                player.sendMessage(MsgType.WARN.format("You removed a sign with the position %v.", entry.getKey()));
                                break;
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("set")) {
                        if (!(args.length > 3)) {
                            player.sendMessage(MsgType.WARN.format("You must provide a position number."));
                            return true;
                        }

                        int position;
                        try {
                            position = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(MsgType.WARN.format("You provided an invalid number."));
                            return true;
                        }

                        lobby.getMapSigns().put(position, targetBlock.getLocation());
                        player.sendMessage(MsgType.INFO.format("You set the sign you are looking at as a map sign in position %v.", position));
                        lobby.generateMapOptions();
                    }
                }
                case "statsigns", "sts" -> {
                    if (game != null) {
                        sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
                        return true;
                    }

                    if (!(args.length > 2)) {
                        sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
                        return true;
                    }

                    Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
                    if (targetBlock == null) {
                        player.sendMessage(MsgType.WARN.format("You are not looking at a block."));
                        return true;
                    }

                    if (!(targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN)) {
                        player.sendMessage(MsgType.WARN.format("You are not looking at a sign."));
                        return true;
                    }

                    if (args[2].equalsIgnoreCase("remove")) {
                        Iterator<StatSign> iterator = lobby.getStatSigns().iterator();
                        while (iterator.hasNext()) {
                            StatSign entry = iterator.next();
                            if (entry.getLocation().equals(targetBlock.getLocation())) {
                                iterator.remove();
                                player.sendMessage(MsgType.INFO.format("You removed that stat sign"));
                                break;
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("add")) {
                        if (!(args.length > 4)) {
                            player.sendMessage(MsgType.WARN.format("Usage: /survivalgames lobby statsigns add <stat> <displayName>"));
                            return true;
                        }

                        String stat = args[3];
                        Field field = SGPlayerStats.getFields().get(stat);
                        if (field == null) {
                            player.sendMessage(MsgType.WARN.format("You provided an invalid stat name."));
                            return true;
                        }

                        for (StatSign statSign : lobby.getStatSigns()) {
                            if (statSign.getStat().equalsIgnoreCase(stat)) {
                                player.sendMessage(MsgType.WARN.format("A stat sign with that stat already exists. You can only have one per stat."));
                                return true;
                            }
                        }

                        StringBuilder sb = new StringBuilder();
                        for (int i = 4; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }

                        String displayName = ChatColor.stripColor(StarColors.color(sb.toString().trim()));
                        if (displayName.length() > 14) {
                            player.sendMessage(MsgType.WARN.format("The display name cannot be larger than 14 characters"));
                            return true;
                        }

                        StatSign statSign = new StatSign(targetBlock.getLocation(), stat, displayName);
                        lobby.getStatSigns().add(statSign);
                        player.sendMessage(MsgType.INFO.format("You added a stat sign for %v with the display name %v.", stat, displayName));
                    }
                }
                case "tributesigns", "ts" -> {
                    if (game != null) {
                        sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
                        return true;
                    }

                    if (!(args.length > 2)) {
                        sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
                        return true;
                    }

                    Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
                    if (targetBlock == null) {
                        player.sendMessage(MsgType.WARN.format("You are not looking at a block."));
                        return true;
                    }

                    if (Stream.of(Material.SIGN, Material.WALL_SIGN, Material.SKULL).noneMatch(material -> targetBlock.getType() == material)) {
                        player.sendMessage(MsgType.WARN.format("You are not looking at a sign or a head."));
                        return true;
                    }

                    if (args[2].equalsIgnoreCase("remove")) {
                        Iterator<TributeSign> iterator = lobby.getTributeSigns().iterator();
                        while (iterator.hasNext()) {
                            TributeSign sign = iterator.next();
                            if (sign.getSignLocation().equals(targetBlock.getLocation()) || sign.getHeadLocation().equals(targetBlock.getLocation())) {
                                iterator.remove();
                                player.sendMessage(MsgType.INFO.format("You removed the tribute sign with index %v.", sign.getIndex()));
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("set")) {
                        if (!(args.length > 3)) {
                            player.sendMessage(MsgType.WARN.format("You must provide an index number."));
                            return true;
                        }

                        int index;
                        try {
                            index = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(MsgType.WARN.format("You provided an invalid number."));
                            return true;
                        }

                        Location signLocation = null, headLocation = null;
                        if (targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN) {
                            signLocation = targetBlock.getLocation();
                        } else if (targetBlock.getType() == Material.SKULL) {
                            headLocation = targetBlock.getLocation();
                        }

                        TributeSign tributeSign = null;
                        for (TributeSign sign : lobby.getTributeSigns()) {
                            if (sign.getIndex() == index) {
                                tributeSign = sign;
                                break;
                            }
                        }

                        if (tributeSign == null) {
                            tributeSign = new TributeSign(index, signLocation, headLocation);
                            String msg = "You created a new tribute sign with index &b" + index;
                            if (signLocation == null) {
                                msg += "&e, however you still need to add a sign to it. Just use the same command while looking at a sign.";
                            } else {
                                msg += "&e, however you still need to add a head to it. Just use the same command while looking at a head.";
                            }
                            lobby.getTributeSigns().add(tributeSign);
                            player.sendMessage(StarColors.color(MsgType.INFO + msg));
                            return true;
                        }

                        if (signLocation != null) {
                            tributeSign.setSignLocation(signLocation);
                            player.sendMessage(MsgType.INFO.format("You set the sign location of the tribute sign at index %v.", index));
                        } else if (headLocation != null) {
                            tributeSign.setHeadLocation(headLocation);
                            player.sendMessage(MsgType.INFO.format("You set the head location of the tribute sign at index %v", index));
                        } else {
                            player.sendMessage(MsgType.SEVERE.format("Unknown error occured. Please report as a bug."));
                            return true;
                        }
                    }
                }
                case "preparegame", "pg" -> {
                    if (game != null) {
                        sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
                        return true;
                    }
                    LobbyState lobbyState = lobby.getState();
                    if (lobbyState == LobbyState.WAITING || lobbyState == LobbyState.COUNTDOWN) {
                        lobby.prepareGame();
                        sender.sendMessage(MsgType.INFO.format("You forcefully had the lobby prepare the game."));
                    } else {
                        sender.sendMessage(MsgType.WARN.format("The lobby is in an invalid state to prepare a game."));
                        return true;
                    }
                }
                case "setspawn", "ss" -> {
                    if (game != null) {
                        sender.sendMessage(MsgType.WARN.format("The server has a game in progress."));
                        return true;
                    }

                    lobby.setSpawnpoint(player.getLocation());
                    sender.sendMessage(MsgType.INFO.format("You set the lobby spawnpoint to your location."));
                }
                case "debug" -> {
                    if (lobby.isDebugMode()) {
                        lobby.disableDebug();
                        sender.sendMessage(MsgType.INFO.format("You disabled lobby debug mode."));
                    } else {
                        lobby.enableDebug();
                        sender.sendMessage(MsgType.INFO.format("You &aenabled lobby debug mode."));
                    }
                }
            }
        } else if (List.of("settings", "setting", "s").contains(subCommand)) {
            if (!(args.length > 1)) {
                sender.sendMessage(MsgType.WARN.format("Usage: /" + label + " " + args[0] + " <type> <name | save | list> [value | savename]"));
                return true;
            }

            if (args[1].equalsIgnoreCase("confirm") || args[1].equals("c")) {
                Runnable task = this.settingsConfirmation.get(player.getUniqueId());
                if (task == null) {
                    sender.sendMessage(MsgType.WARN.format("You have nothing to confirm."));
                    return true;
                }

                task.run();
                this.settingsConfirmation.remove(player.getUniqueId());
                return true;
            }

            boolean confirm = false;
            boolean global = false;
            boolean allRunning = false;
            boolean silent = false;

            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                if (arg.equalsIgnoreCase("-c")) {
                    confirm = true;
                } else if (arg.equalsIgnoreCase("-g")) {
                    global = true;
                } else if (arg.equalsIgnoreCase("-r")) {
                    allRunning = true;
                } else if (arg.equalsIgnoreCase("-gr") || arg.equalsIgnoreCase("-rg")) {
                    allRunning = true;
                    global = true;
                } else if (arg.equalsIgnoreCase("-s")) {
                    silent = true;
                }
                // TODO Add more permutations
                else {
                    sb.append(arg).append(" ");
                }
            }

            args = sb.toString().trim().split(" ");

            String type = switch (args[1].toLowerCase()) {
                case "game", "g" -> "game";
                case "lobby", "l" -> "lobby";
                default -> null;
            };

            if (type == null) {
                MsgType.WARN.send(sender, "Not enough arguments");
                return true;
            }

            Class<?> settingClass = switch (type) {
                case "game" -> GameSettings.class;
                case "lobby" -> LobbySettings.class;
                default -> null; //Can ignore this really as it will never be null
            };

            if (!(args.length > 2)) {
                MsgType.WARN.send(sender, "Not enough arguments");
                return true;
            }

            Field[] declaredFields = settingClass.getDeclaredFields();

            Map<Field, StringConverter<?>> fields = new HashMap<>();
            for (Field declaredField : declaredFields) {
                if (Modifier.isStatic(declaredField.getModifiers())) {
                    continue;
                }

                if (Modifier.isFinal(declaredField.getModifiers())) {
                    continue;
                }

                StringConverter<?> converter = StringConverters.getConverter(declaredField.getType());
                if (converter != null) {
                    fields.put(declaredField, converter);
                }
            }

            if (type == null) {
                sender.sendMessage(MsgType.WARN.format("Invalid setting type. Can only be game (g) or lobby (l)"));
                return true;
            }

            if (args[2].equalsIgnoreCase("save")) {
                sender.sendMessage(MsgType.WARN.format("This functionality is temporarily disabled."));
                return true;
            }

            if (args[2].equalsIgnoreCase("list")) {
                List<String> lines = new LinkedList<>();
                for (Field field : fields.keySet()) {
                    lines.add("  &8 - &a" + field.getName().toLowerCase());
                }

                if (lines.isEmpty()) {
                    sender.sendMessage(MsgType.WARN.format("Could not find any settings to list."));
                    return true;
                }

                sender.sendMessage(MsgType.INFO.format("List of &b" + type + " settings."));
                lines.forEach(line -> StarColors.coloredMessage(sender, line));

                return true;
            }

            if (!(args.length > 3)) {
                MsgType.WARN.send(sender, "Invalid argument count");
                return true;
            }

            String setting = args[2].toLowerCase();
            Field field = null;
            StringConverter<?> converter = null;

            for (Entry<Field, StringConverter<?>> entry : fields.entrySet()) {
                if (entry.getKey().getName().equalsIgnoreCase(setting)) {
                    field = entry.getKey();
                    converter = entry.getValue();
                    break;
                }
            }

            if (field == null) {
                sender.sendMessage(MsgType.WARN.format("Could not find a setting named %v.", args[2]));
                return true;
            }

            field.setAccessible(true);

            Object value = converter.convertTo(args[3]);
            if (value == null) {
                sender.sendMessage(MsgType.WARN.format("Could not convert %v to a(n) %v", args[3], field.getType().getSimpleName()));
                return true;
            }

            boolean isGameType = type.equalsIgnoreCase("game");
            boolean isLobbyType = type.equalsIgnoreCase("lobby");

            Set<Object> settingsInstances = new HashSet<>();
            if (global) {
                if (isGameType) {
                    settingsInstances.add(SurvivalGames.globalGameSettings);
                } else if (isLobbyType) {
                    settingsInstances.add(SurvivalGames.globalLobbySettings);
                }
            }

            if (allRunning) {
                if (isGameType) {
                    plugin.getServers().forEach(server -> {
                        if (server.getGame() != null) {
                            settingsInstances.add(server.getGame().getSettings());
                        }
                    });
                } else if (isLobbyType) {
                    plugin.getServers().forEach(server -> {
                        if (server.getLobby() != null) {
                            settingsInstances.add(server.getGame().getSettings());
                        }
                    });
                }
            }

            //TODO Add instanceof check here when sender limit fixed
            if (isGameType) {
                if (sgPlayer.getGame() != null) {
                    settingsInstances.add(sgPlayer.getGame().getSettings());
                }

                if (sgPlayer.getLobby() != null) {
                    settingsInstances.add(sgPlayer.getLobby().getGameSettings());
                }
            } else if (isLobbyType) {
                if (sgPlayer.getLobby() != null) {
                    settingsInstances.add(sgPlayer.getLobby().getLobbySettings());
                }
            }

            Field finalField = field;
            final boolean finalSilent = silent;
            Runnable task = () -> {
                for (Object instance : settingsInstances) {
                    try {
                        finalField.set(instance, value);
                    } catch (IllegalAccessException e) {
                        sender.sendMessage(MsgType.ERROR.format("Failed to set the %v setting to %v.", type, value));
                        e.printStackTrace();
                        return;
                    }
                }
//                sender.sendMessage(MsgType.INFO.format("You set the %v setting %v to %v.", type, finalField.getName(), value));
                if (!finalSilent) {
                    plugin.getNexusCore().getStaffChannel().sendMessage(new ChatContext(sgPlayer.getNexusPlayer().getColoredName() + " &fset the " + type + " setting " + finalField.getName() + " to " + value + "."));
                }
            };

            if (confirm) {
                task.run();
            } else {
                this.settingsConfirmation.put(player.getUniqueId(), task);
                sender.sendMessage("");
                sender.sendMessage(MsgType.IMPORTANT.format("Please confirm the following action..."));
                sender.sendMessage(MsgType.IMPORTANT.format("Setting the %v setting %v to %v in %v %v.", type, field.getName(), value, settingsInstances.size(), type.equals("game") ? "games" : type.equals("lobby") ? "lobbies" : "unknown"));
                sender.sendMessage(MsgType.IMPORTANT.format("Please type %v to confirm.", "/survivalgames settings confirm"));
                sender.sendMessage("");
            }
        } else if (subCommand.equals("maps") || subCommand.equals("m")) {
            if (!(args.length > 1)) {
                MsgType.WARN.send(sender, "Usage: /" + label + " " + subCommand + " <list|export|import|setsource> [args]");
                return true;
            }
            
            if (args[1].equalsIgnoreCase("list")) {
                StarColors.coloredMessage(sender, "&6&l>> &eList of &bSG Maps");
                for (SGMap map : plugin.getMapManager().getMaps()) {
                    StarColors.coloredMessage(sender, " &6&l> &d" + map.getName() + "  " + (map.isActive() ? "&a&lACTIVE" : "&c&lINACTIVE") + "  " + (map.isValid() ? "&a&lVALID" : "&c&lINVALID"));
                }
                return true;
            }
            
            if (!(args.length > 2)) {
                sender.sendMessage(MsgType.WARN.format("Usage: /" + label + " " + subCommand + " <export|import|setsource> <sql|yml>"));
                return true;
            }

            String option = args[2];
            if (!(option.equalsIgnoreCase("sql") || option.equalsIgnoreCase("yml"))) {
                sender.sendMessage(MsgType.WARN.format("Invalid option, only valid options are sql and yml."));
                return true;
            }

            if (args[1].equalsIgnoreCase("export")) {
                if (option.equalsIgnoreCase("sql")) {
                    SQLMapManager sqlMapManager = new SQLMapManager(plugin);
                    for (SGMap map : plugin.getMapManager().getMaps()) {
                        sqlMapManager.addMap(map);
                    }
                    sqlMapManager.saveMaps();
                    sender.sendMessage(MsgType.INFO.format("Exported %v maps to SQL.", sqlMapManager.getMaps().size()));
                } else {
                    YamlMapManager yamlMapManager = new YamlMapManager(plugin);
                    for (SGMap map : plugin.getMapManager().getMaps()) {
                        yamlMapManager.addMap(map);
                    }
                    yamlMapManager.saveMaps();
                    sender.sendMessage(MsgType.INFO.format("Exported %v maps to YML.", yamlMapManager.getMaps().size()));
                }
            } else if (args[1].equalsIgnoreCase("import")) {
                MapManager importManager;

                if (option.equalsIgnoreCase("sql")) {
                    importManager = new SQLMapManager(plugin);
                } else {
                    importManager = new YamlMapManager(plugin);
                }

                importManager.loadMaps();
                if (importManager.getMaps().isEmpty()) {
                    sender.sendMessage(MsgType.WARN.format("No maps could be loaded from %v.", option.toUpperCase()));
                    return true;
                }

                int newMaps = 0, duplicateMaps = 0;
                for (SGMap map : importManager.getMaps()) {
                    SGMap existingMap = plugin.getMapManager().getMap(map.getName());
                    if (existingMap == null) {
                        plugin.getMapManager().addMap(map);
                        sender.sendMessage(MsgType.INFO.format("Added %v as a new map.", map.getName()));
                        newMaps++;
                    } else {
                        existingMap.copyFrom(map);
                        sender.sendMessage(MsgType.INFO.format("Replaced %v's settings with the values from the imported data.", map.getName()));
                        duplicateMaps++;
                    }
                }

                sender.sendMessage(MsgType.INFO.format("Added %v new map(s) and updated %v map(s).", newMaps, duplicateMaps));
            } else if (args[1].equalsIgnoreCase("setsource")) {
                if (option.equalsIgnoreCase("sql")) {
                    if (plugin.getMapManager() instanceof SQLMapManager) {
                        sender.sendMessage(MsgType.WARN.format("The map souce is already set to SQL."));
                        return true;
                    }

                    plugin.setMapManager(new SQLMapManager(plugin));
                    sender.sendMessage(MsgType.INFO.format("You set the map source to SQL."));
                } else {
                    if (plugin.getMapManager() instanceof YamlMapManager) {
                        sender.sendMessage(MsgType.WARN.format("The map souce is already set to YML."));
                        return true;
                    }

                    plugin.setMapManager(new YamlMapManager(plugin));
                    sender.sendMessage(MsgType.INFO.format("You set the map source to YML."));
                }
            }
        } else if (subCommand.equals("timer") || subCommand.equals("t")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
                return true;
            }

            Timer timer;
            String timerType;
            if (game != null) {
                timer = game.getTimer();
                timerType = "game";
            } else {
                timer = lobby.getTimer();
                timerType = "lobby";
            }

            if (timer == null) {
                sender.sendMessage(MsgType.WARN.format("The %v does not have an active timer. Nothing to control.", timerType));
                return true;
            }

            String timerSubCommand = args[1].toLowerCase();
            switch (timerSubCommand) {
                case "pause" -> {
                    if (timer.isPaused()) {
                        sender.sendMessage(MsgType.WARN.format("The timer is already paused."));
                        return true;
                    }
                    timer.pause();
                    sender.sendMessage(MsgType.INFO.format("You paused the timer."));
                }
                case "resume" -> {
                    if (!timer.isPaused()) {
                        sender.sendMessage(MsgType.WARN.format("The timer is not paused."));
                        return true;
                    }
                    timer.unpause();
                    sender.sendMessage(MsgType.INFO.format("You resumed the timer."));
                }
                case "reset" -> {
                    timer.reset();
                    sender.sendMessage(MsgType.INFO.format("You reset the timer."));
                }
                case "modify" -> {
                    if (!(args.length > 3)) {
                        sender.sendMessage(MsgType.WARN.format("Usage: /survivalgames timer modify set|add|subtract <value>"));
                        return true;
                    }

                    // /sg timer modify set|add|substract <value>
                    String operation = args[2];
                    String rawValue = args[3];

                    TimeFormat timeFormat = new TimeFormat("%*#0h%%*#0m%%*#0s%");
                    TimeParser timeParser = new TimeParser();
                    long timeValue = timeParser.parseTime(rawValue);

                    long oldValue = timer.getTime();

                    if (operation.equalsIgnoreCase("subtract")) {
                        timer.removeTime(timeValue);
                        sender.sendMessage(MsgType.INFO.format("You subtracted %v from the %v's timer.", timeFormat.format(timeValue), timerType));
                    } else if (operation.equalsIgnoreCase("add")) {
                        timer.addTime(timeValue);
                        sender.sendMessage(MsgType.INFO.format("You added %v to the %v's timer.", timeFormat.format(timeValue), timerType));
                    } else if (operation.equalsIgnoreCase("set")) {
                        timer.setTime(timeValue);
                        sender.sendMessage(MsgType.INFO.format("You set %v's timer to %v.", timerType, timeFormat.format(timeValue)));
                    }

                    long newValue = timer.getTime();

                    if (timerType.equalsIgnoreCase("game")) {
                        game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "timermodification").addValueData("actor", sender.getName()).addValueData("oldvalue", oldValue).addValueData("newvalue", newValue));
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("skull")) {
            Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
            Skull skull = (Skull) targetBlock.getState();
            player.sendMessage(skull.getOwner());
        } else if (args[0].equalsIgnoreCase("loottable")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MsgType.WARN.format("You must provide a Loot Table name."));
                return true;
            }

            SGLootTable lootTable = plugin.getLootManager().getLootTable(args[1]);
            if (lootTable == null) {
                sender.sendMessage(MsgType.WARN.format("The value %v is not a valid loot table", args[1]));
                return true;
            }

            if (!(args.length > 2)) {
                sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
                return true;
            }

            if (args[2].equalsIgnoreCase("reload")) {
                lootTable.setReloading(true);
                try {
                    lootTable.saveData();
                    lootTable.loadData();
                    if (lootTable.getItemWeights().isEmpty()) {
                        lootTable.loadDefaultData();
                    }
                    sender.sendMessage(MsgType.INFO.format("Reload of loot table %v was successful.", lootTable.getName()));
                } catch (Throwable throwable) {
                    sender.sendMessage(MsgType.ERROR.format("There was an error reloading that loot table: " + throwable.getMessage()));
                }
                lootTable.setReloading(false);
            } else if (args[2].equalsIgnoreCase("setitemweight") || args[2].equalsIgnoreCase("siw")) {
                if (!(args.length > 4)) {
                    sender.sendMessage(MsgType.WARN.format(String.format("Usage: /%s %s %s %s <item> <weight>", label, args[0], args[1], args[2])));
                    return true;
                }

                String itemName = args[3].toLowerCase().replace("'", "");
                if (!lootTable.getItemWeights().containsKey(itemName)) {
                    sender.sendMessage(MsgType.WARN.format("The loot table %v does not contain an item entry with the id of %v", lootTable.getName(), itemName));
                    return true;
                }

                int weight;
                try {
                    weight = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MsgType.WARN.format("The input value %v is not a valid whole number.", args[4]));
                    return true;
                }

                lootTable.getItemWeights().put(itemName, weight);
                sender.sendMessage(MsgType.INFO.format("You set the item %v's weight to %v in loot table %v", itemName, weight, lootTable.getName()));
                sender.sendMessage(MsgType.INFO.format(String.format("You must use /%s %s %s reload", label, args[0], args[1]) + " to apply your changes."));
            }
        }

        return true;
    }
}