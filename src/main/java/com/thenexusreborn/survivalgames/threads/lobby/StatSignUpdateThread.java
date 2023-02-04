package com.thenexusreborn.survivalgames.threads.lobby;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatSignUpdateThread extends NexusThread<SurvivalGames> {
    public StatSignUpdateThread(SurvivalGames plugin) {
        super(plugin, 20L, false);
    }
    
    public void onRun() {
        Lobby lobby = plugin.getLobby();
        if (lobby == null) {
            return;
        }
    
        if (plugin.getGame() != null) {
            return;
        }
    
        if (lobby.getState() == LobbyState.MAP_EDITING) {
            return;
        }
    
        if (lobby.getStatSigns().size() < 1) {
            return;
        }
    
        for (StatSign statSign : lobby.getStatSigns()) {
            for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                Player player = Bukkit.getPlayer(lobbyPlayer.getUniqueId());
                if (player.getWorld() != statSign.getLocation().getWorld()) {
                    continue;
                }
                
                String[] lines = {MCUtils.color("&n" + statSign.getDisplayName()), "", lobbyPlayer.getStatValue(statSign.getStat()).get().toString(), ""};
                player.sendSignChange(statSign.getLocation(), lines);
            }
        }
    }
}
