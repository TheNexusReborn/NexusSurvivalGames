package com.thenexusreborn.survivalgames.hooks;

import com.thenexusreborn.hub.NexusHub;
import com.thenexusreborn.hub.api.ServerSelectEvent;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.server.SGVirtualServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class NexusHubHook implements Listener {
    private final SurvivalGames survivalGames;
    private final NexusHub nexusHub;
    
    public NexusHubHook(SurvivalGames survivalGames, Plugin nexusHubPlugin) {
        this.survivalGames = survivalGames;
        this.nexusHub = (NexusHub) nexusHubPlugin;
    }
    
    @EventHandler
    public void onServerSelect(ServerSelectEvent e) {
        String serverName = e.getServerName();
        
        if (e.getNexusPlayer() == null) {
            survivalGames.getLogger().severe("NexusPlayer in a ServerSelectEvent was null.");
            UUID uuid = e.getUuid();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                MsgType.ERROR.send(player, "There was an error while processing your server selection. Please leave and rejoin.");
                survivalGames.getLogger().severe("  The player was online: " + player.getName());
            }
            
            return;
        }
        
        for (SGVirtualServer server : survivalGames.getServers().values()) {
            if (server.getName().equalsIgnoreCase(serverName)) {
                server.join(e.getNexusPlayer());
                break;
            }
        }
    }
}