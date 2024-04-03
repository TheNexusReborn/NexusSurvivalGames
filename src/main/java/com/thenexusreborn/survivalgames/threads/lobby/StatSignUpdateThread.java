package com.thenexusreborn.survivalgames.threads.lobby;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatSignUpdateThread extends NexusThread<SurvivalGames> {
    public StatSignUpdateThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    public void onRun() {
        for (SGVirtualServer server : plugin.getServers()) {
            Lobby lobby = server.getLobby();
            if (lobby == null) {
                continue;
            }
            if (lobby.getState() == LobbyState.MAP_EDITING) {
                continue;
            }

            if (lobby.getStatSigns().isEmpty()) {
                continue;
            }

            for (StatSign statSign : lobby.getStatSigns()) {
                for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                    Player player = Bukkit.getPlayer(lobbyPlayer.getUniqueId());
                    if (player.getWorld() != statSign.getLocation().getWorld()) {
                        continue;
                    }

                    String[] lines = {MCUtils.color("&n" + statSign.getDisplayName()), "", lobbyPlayer.getStats().getValue(statSign.getStat()) + "", ""};
                    player.sendSignChange(statSign.getLocation(), lines);
                }
            }
        }
    }
}
