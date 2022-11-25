package com.thenexusreborn.survivalgames.lobby.tasks;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.api.NexusTask;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.lobby.StatSign;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatSignUpdateTask extends NexusTask<SurvivalGames> {
    public StatSignUpdateTask(SurvivalGames plugin) {
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
            for (NexusPlayer nexusPlayer : lobby.getPlayers()) {
                Player player = Bukkit.getPlayer(nexusPlayer.getUniqueId());
                if (player.getWorld() != statSign.getLocation().getWorld()) {
                    continue;
                }
                
                String[] lines = new String[] {MCUtils.color("&n" + statSign.getDisplayName()), "", nexusPlayer.getStats().getValue(statSign.getStat()).get().toString(), ""};
                player.sendSignChange(statSign.getLocation(), lines);
            }
        }
    }
}
