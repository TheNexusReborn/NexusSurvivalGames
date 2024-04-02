package com.thenexusreborn.survivalgames.hooks;

import com.stardevllc.starclock.clocks.Timer;
import com.stardevllc.starlib.time.TimeFormat;
import com.stardevllc.starmclib.Position;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.CombatTag;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static com.thenexusreborn.survivalgames.scoreboard.game.CombatTagBoard.TIME_FORMAT;

public class SGPAPIExpansion extends PlaceholderExpansion {

    private SurvivalGames plugin;

    public SGPAPIExpansion(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    /*
    nexussg_score
    nexussg_displayname
    nexussg_gameteam_color
    nexussg_lobby_waiting
    nexussg_lobby_needed
    nexussg_lobby_controltype
    nexussg_lobby_state
    nexussg_lobby_spectating
    nexussg_lobby_hidden
    nexussg_lobby_timeleft
    nexussg_lobby_map_name
    nexussg_lobby_map_center
    nexussg_lobby_map_spawncount
    nexussg_lobby_map_borderradius
    nexussg_lobby_map_dmborderradius
    nexussg_lobby_map_creatorcount
    nexussg_lobby_map_swagshacklocation
    nexussg_game_date
    nexussg_game_map
    nexussg_game_tributes
    nexussg_game_spectators
    nexussg_game_mutations
    nexussg_game_zombies
    nexussg_game_player_score
    nexussg_game_player_kills
    nexussg_game_player_assists
    nexussg_game_mutation_target
    nexussg_game_mutation_type
    nexussg_game_combattag_target
    nexussg_game_combattag_time
     */
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        SGPlayerStats stats = sgPlayer.getStats();

        Game game = sgPlayer.getGame();
        Lobby lobby = sgPlayer.getLobby();
        
        if (params.equalsIgnoreCase("score")) {
            return MCUtils.formatNumber(stats.getScore());
        }

        if (params.equalsIgnoreCase("displayname")) {
            if (game == null) {
                return "";
            }

            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                return "";
            }

            String prefix = "";
            if (gamePlayer.getRank() != Rank.MEMBER) {
                prefix = gamePlayer.getRank().getPrefix() + " ";
            }

            String nameColor;
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                nameColor = gamePlayer.getRank().getColor();
            } else {
                nameColor = gamePlayer.getTeam().getColor();
            }

            String tag = "";
            if (gamePlayer.hasActiveTag()) {
                tag = " " + gamePlayer.getActiveTag().getDisplayName();
            }

            return prefix + nameColor + player.getName() + tag;
        } else if (params.equalsIgnoreCase("gameteam_color")) {
            if (game == null) {
                return "";
            }

            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                return "";
            }

            return gamePlayer.getTeam().getColor();
        } else if (params.startsWith("lobby_")) {
            String option = params.split("_")[1];
            if (option == null || option.isEmpty()) {
                return "";
            }

            if (option.equalsIgnoreCase("waiting")) {
                int waiting = 0;
                for (LobbyPlayer waitingPlayer : lobby.getPlayers()) {
                    if (!waitingPlayer.isSpectating()) {
                        waiting++;
                    }
                }
                return String.valueOf(waiting);
            } else if (option.equalsIgnoreCase("needed")) {
                return String.valueOf(lobby.getLobbySettings().getMinPlayers());
            } else if (option.equalsIgnoreCase("controltype")) {
                return lobby.getControlType().name();
            } else if (option.equalsIgnoreCase("state")) {
                return lobby.getState().name();
            } else if (option.equalsIgnoreCase("spectating")) {
                return String.valueOf(lobby.getSpectatingPlayers().size());
            } else if (option.equalsIgnoreCase("hidden")) {
                int hidden = 0;
                for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                    if (lobbyPlayer.getToggleValue("vanish")) {
                        hidden++;
                    }
                }
                return String.valueOf(hidden);
            } else if (option.equalsIgnoreCase("timeleft")) {
                Timer timer = lobby.getTimer();
                if (timer != null) {
                    return new TimeFormat("%*0m%%0s%").format(timer.getTime());
                }
                return "0s";
            } else if (option.equalsIgnoreCase("map")) {
                String mapOption = params.split("_")[2];
                if (mapOption.equalsIgnoreCase("name")) {
                    SGMap gameMap = lobby.getGameMap();
                    if (gameMap != null) {
                        return gameMap.getName();
                    } else {
                        if (lobby.isDebugMode()) {
                            return "&7None";
                        }
                        return "&7Voting";
                    }
                } else if (mapOption.equalsIgnoreCase("center")) {
                    if (lobby.getGameMap() == null) {
                        return "&fNo Map Set";
                    } else {
                        Position center = lobby.getGameMap().getCenter();
                        if (center != null) {
                            return center.getX() + "," + center.getY() + "," + center.getZ();
                        } else {
                            return "&fNot Set";
                        }
                    }
                } else if (mapOption.equalsIgnoreCase("spawncount")) {
                    if (lobby.getGameMap() == null) {
                        return "0";
                    } else {
                        return String.valueOf(lobby.getGameMap().getSpawns().size());
                    }
                } else if (mapOption.equalsIgnoreCase("borderradius")) {
                    if (lobby.getGameMap() == null) {
                        return "0";
                    } else {
                        return String.valueOf(lobby.getGameMap().getBorderDistance());
                    }
                } else if (mapOption.equalsIgnoreCase("dmborderradius")) {
                    if (lobby.getGameMap() == null) {
                        return "0";
                    } else {
                        return String.valueOf(lobby.getGameMap().getDeathmatchBorderDistance());
                    }
                } else if (mapOption.equalsIgnoreCase("creatorcount")) {
                    if (lobby.getGameMap() == null) {
                        return "0";
                    } else {
                        return String.valueOf(lobby.getGameMap().getCreators().size());
                    }
                } else if (mapOption.equalsIgnoreCase("swagshacklocation")) {
                    if (lobby.getGameMap() == null) {
                        return "&fNo Map Set";
                    } else {
                        Position swagShack = lobby.getGameMap().getSwagShack();
                        if (swagShack != null) {
                            return swagShack.getX() + "," + swagShack.getY() + "," + swagShack.getZ();
                        } else {
                            return "&fNot Set";
                        }
                    }
                }
            } else if (option.equalsIgnoreCase("game")) {
                if (game == null) {
                    return "";
                }
                
                String gameOption = params.split("_")[2];
                if (gameOption.equalsIgnoreCase("date")) {
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                    df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
                    return "&7" + df.format(System.currentTimeMillis());
                } else if (gameOption.equalsIgnoreCase("map")) {
                    return game.getGameMap().getName();
                } else if (gameOption.equalsIgnoreCase("tributes")) {
                    return String.valueOf(game.getTeamCount(GameTeam.TRIBUTES));
                } else if (gameOption.equalsIgnoreCase("spectators")) {
                    return String.valueOf(game.getTeamCount(GameTeam.SPECTATORS));
                } else if (gameOption.equalsIgnoreCase("mutations")) {
                    return String.valueOf(game.getTeamCount(GameTeam.MUTATIONS));
                } else if (gameOption.equalsIgnoreCase("zombies")) {
                    return String.valueOf(game.getTeamCount(GameTeam.ZOMBIES));
                } else if (gameOption.equalsIgnoreCase("player")) {
                    String playerOption = params.split("_")[3];
                    GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
                    if (gamePlayer == null) {
                        return "";
                    }
                    
                    if (playerOption.equalsIgnoreCase("score")) {
                        return String.valueOf(stats.getScore());
                    } else if (playerOption.equalsIgnoreCase("kills")) {
                        int killStreak = gamePlayer.getKillStreak();
                        int hks = gamePlayer.getStats().getHighestKillstreak();
                        return killStreak + "/" + hks;
                    } else if (playerOption.equalsIgnoreCase("assists")) {
                        if (game.getSettings().isAllowAssists()) {
                            return String.valueOf(gamePlayer.getAssists());
                        }
                    }
                } else if (gameOption.equalsIgnoreCase("mutation")) {
                    String mutationOption = params.split("_")[3];

                    GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
                    if (gamePlayer == null) {
                        return "";
                    }

                    Mutation mutation = gamePlayer.getMutation();
                    if (mutation == null) {
                        return "";
                    }

                    if (mutationOption.equalsIgnoreCase("target")) {
                        GamePlayer target = game.getPlayer(mutation.getTarget());
                        if (target == null) {
                            return "None";
                        }
                        return target.getName();
                    } else if (mutationOption.equalsIgnoreCase("type")) {
                        return mutation.getType().getDisplayName();
                    }
                } else if (gameOption.equalsIgnoreCase("combattag")) {
                    GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
                    if (gamePlayer == null) {
                        return "";
                    }

                    CombatTag combatTag = gamePlayer.getCombatTag();
                    String ctOption = params.split("_")[3];
                    if (ctOption.equalsIgnoreCase("target")) {
                        if (combatTag == null || combatTag.getOther() == null) {
                            return "No one";
                        } else {
                            GamePlayer otherGamePlayer = game.getPlayer(combatTag.getOther());
                            if (otherGamePlayer == null) {
                                return "No one";
                            } else {
                                return otherGamePlayer.getName();
                            }
                        }
                    } else if (ctOption.equalsIgnoreCase("time")) {
                        if (combatTag == null || combatTag.getOther() == null || !combatTag.isInCombat()) {
                            return "0s";
                        } else {
                            long combatTagLength = game.getSettings().getCombatTagLength() * 1000L;
                            long timeRemaining = combatTag.getTimestamp() + combatTagLength - System.currentTimeMillis();
                            return TIME_FORMAT.format(timeRemaining);
                        }
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String getIdentifier() {
        return "nexussg";
    }

    @Override
    public String getAuthor() {
        return "Firestar311";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
