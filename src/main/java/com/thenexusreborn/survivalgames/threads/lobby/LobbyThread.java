package com.thenexusreborn.survivalgames.threads.lobby;

import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyPlayer;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LobbyThread extends NexusThread<SurvivalGames> {

    private int secondsInPrepareGame;

    public LobbyThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }

    @Override
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Lobby lobby = server.getLobby();
            if (lobby == null) {
                continue;
            }
            World world = Bukkit.getWorld(ServerProperties.getLevelName());
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            }

            LobbyState state = lobby.getState();
            for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                Player player = Bukkit.getPlayer(lobbyPlayer.getUniqueId());
                SGUtils.updatePlayerHealthAndFood(Bukkit.getPlayer(player.getUniqueId()));

                if (state != LobbyState.MAP_EDITING) {
                    if (player.getLocation().getBlockY() < lobby.getSpawnpoint().getBlockY() - 20) {
                        player.teleport(lobby.getSpawnpoint());
                    }
                }
            }

            world.setThundering(false);
            world.setStorm(false);

            world.setGameRuleValue("doFireSpread", "false");

            for (LobbyPlayer player : lobby.getPlayers()) {
                Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                for (LobbyPlayer other : lobby.getPlayers()) {
                    Player otherBukkitPlayer = Bukkit.getPlayer(other.getUniqueId());
                    if (player.getToggleValue("vanish")) {
                        if (other.getRank().ordinal() > Rank.HELPER.ordinal()) {
                            otherBukkitPlayer.hidePlayer(bukkitPlayer);
                        }
                    } else {
                        otherBukkitPlayer.showPlayer(bukkitPlayer);
                    }

                    if (other.getToggleValue("vanish")) {
                        if (player.getRank().ordinal() > Rank.HELPER.ordinal()) {
                            bukkitPlayer.hidePlayer(otherBukkitPlayer);
                        }
                    } else {
                        bukkitPlayer.showPlayer(otherBukkitPlayer);
                    }
                }
            }

            boolean resetLobby = false;
            if (!(state == LobbyState.WAITING || state == LobbyState.MAP_EDITING)) {
                if (lobby.getTimer() == null) {
                    resetLobby = true;
                } else if (TimeUnit.SECONDS.fromMillis(lobby.getTimer().getTime()) <= 0) {
                    if (state == LobbyState.PREPARING_GAME || state == LobbyState.STARTING || state == LobbyState.GAME_PREPARED) {
                        this.secondsInPrepareGame++;
                        if (this.secondsInPrepareGame >= 10) {
                            resetLobby = true;
                        }
                    } else {
                        resetLobby = true;
                    }
                }

//                if (Bukkit.getOnlinePlayers().size() <= 1) {
//                    resetLobby = true;
//                } //TODO
            }

            if (resetLobby) {
                //lobby.resetInvalidState();
            }
        }
    }
}