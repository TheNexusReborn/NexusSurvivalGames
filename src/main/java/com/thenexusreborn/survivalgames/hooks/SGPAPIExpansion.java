package com.thenexusreborn.survivalgames.hooks;

import com.stardevllc.starclock.clocks.Timer;
import com.stardevllc.starlib.time.TimeFormat;
import com.stardevllc.starmclib.Position;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.util.SGPlayerStats;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

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
     */
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        SGPlayerStats stats = SurvivalGames.PLAYER_STATS.get(player.getUniqueId());

        if (params.equalsIgnoreCase("score")) {
            return MCUtils.formatNumber(stats.getScore());
        }

        if (params.equalsIgnoreCase("displayname")) {
            Game game = plugin.getGame();
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
            Game game = plugin.getGame();
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

            Lobby lobby = plugin.getLobby();

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
                    if (plugin.getLobby().getGameMap() == null) {
                        return "&fNo Map Set";
                    } else {
                        Position center = plugin.getLobby().getGameMap().getCenter();
                        if (center != null) {
                            return center.getX() + "," + center.getY() + "," + center.getZ();
                        } else {
                            return "&fNot Set";
                        }
                    }
                } else if (mapOption.equalsIgnoreCase("spawncount")) {
                    if (plugin.getLobby().getGameMap() == null) {
                        return "0";
                    } else {
                        return String.valueOf(plugin.getLobby().getGameMap().getSpawns().size());
                    }
                } else if (mapOption.equalsIgnoreCase("borderradius")) {
                    if (plugin.getLobby().getGameMap() == null) {
                        return "0";
                    } else {
                        return String.valueOf(plugin.getLobby().getGameMap().getBorderDistance());
                    }
                } else if (mapOption.equalsIgnoreCase("dmborderradius")) {
                    if (plugin.getLobby().getGameMap() == null) {
                        return "0";
                    } else {
                        return String.valueOf(plugin.getLobby().getGameMap().getDeathmatchBorderDistance());
                    }
                } else if (mapOption.equalsIgnoreCase("creatorcount")) {
                    if (plugin.getLobby().getGameMap() == null) {
                        return "0";
                    } else {
                        return String.valueOf(plugin.getLobby().getGameMap().getCreators().size());
                    }
                } else if (mapOption.equalsIgnoreCase("swagshacklocation")) {
                    if (plugin.getLobby().getGameMap() == null) {
                        return "&fNo Map Set";
                    } else {
                        Position swagShack = plugin.getLobby().getGameMap().getSwagShack();
                        if (swagShack != null) {
                            return swagShack.getX() + "," + swagShack.getY() + "," + swagShack.getZ();
                        } else {
                            return "&fNot Set";
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
