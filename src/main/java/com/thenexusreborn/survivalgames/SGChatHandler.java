package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.chat.ChatHandler;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SGChatHandler implements ChatHandler {
    
    private SurvivalGames plugin;
    
    public SGChatHandler(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean handleChat(NexusPlayer player, String chatColor, AsyncPlayerChatEvent e) {
        String displayName;
        Game game = plugin.getGame();
        if (plugin.getGame() != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            GameTeam team = gamePlayer.getTeam();
            String prefix = "", suffix = "";
            if (player.getRank() != Rank.MEMBER) {
                prefix = player.getRank().getPrefix() + " ";
            }
    
            if (player.getTag() != null) {
                suffix = " " + player.getTag().getDisplayName();
            }
    
            displayName = prefix + team.getColor() + player.getName() + suffix;
        } else {
            displayName = player.getDisplayName();
        }
        
        
        String format = "&8<&3{score}&8> &8(&2&l{level}&8) &r" + displayName + "{tag}&8: " + chatColor + e.getMessage().replace("%", "%%");
        format = format.replace("{level}", player.getLevel() + "");
        format = format.replace("{score}", MCUtils.formatNumber(player.getStatValue("sg_score")));
        if (player.getTag() != null) {
            format = format.replace("{tag}", " " + player.getTag().getDisplayName());
        } else {
            format = format.replace("{tag}", "");
        }
        if (game != null) {
            if (game.getState().ordinal() >= GameState.INGAME_GRACEPERIOD.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
                if (game.getPlayer(player.getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                    for (GamePlayer p : game.getPlayers().values()) {
                        if (p.getTeam() != GameTeam.TRIBUTES) {
                            p.sendMessage(format);
                        }
                    }
                } else {
                    game.sendMessage(format);
                }
            } else {
                game.sendMessage(format);
            }
        } else {
            plugin.getLobby().sendMessage(format);
        }
        return true;
    }
}
