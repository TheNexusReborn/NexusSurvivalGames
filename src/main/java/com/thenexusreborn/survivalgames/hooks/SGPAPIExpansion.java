package com.thenexusreborn.survivalgames.hooks;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
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
     */
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        
        if (params.equalsIgnoreCase("score")) {
            return MCUtils.formatNumber(nexusPlayer.getStatValue("sg_score").getAsInt());
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
            if (gamePlayer.getTags().hasActiveTag()) {
                tag = " " + gamePlayer.getTags().getActive().getDisplayName();
            }

            return prefix + nameColor + player.getName() + tag;
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
