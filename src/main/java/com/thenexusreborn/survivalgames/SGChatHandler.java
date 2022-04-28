package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.chat.ChatHandler;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SGChatHandler implements ChatHandler {
    
    private SurvivalGames plugin;
    
    public SGChatHandler(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean handleChat(NexusPlayer player, String chatColor, AsyncPlayerChatEvent e) {
        if (plugin.getGame() == null) {
            return false;
        }
        
        Game game = plugin.getGame();
        GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        GameTeam team = gamePlayer.getTeam();
        String prefix = "", suffix = "";
        if (player.getRank() != Rank.MEMBER) {
            prefix = player.getRank().getPrefix() + " ";
        }
        
        if (player.getTag() != null) {
            suffix = " " + player.getTag().getDisplayName();
        }
        
        String displayName = prefix + team.getColor() + player.getName() + suffix;
        String format = "&8<&3100&8> &8(&2&l{level}&8) &r" + displayName + "&8: " + chatColor + e.getMessage().replace("%", "%%");
        format = format.replace("{level}", player.getLevel() + "");
        if (game.getState().ordinal() >= GameState.INGAME_GRACEPERIOD.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
            if (team != GameTeam.TRIBUTES) {
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
        return true;
    }
}
