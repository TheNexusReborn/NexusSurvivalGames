package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.chat.ChatHandler;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SGChatHandler implements ChatHandler {
    
    private final SurvivalGames plugin;
    
    public SGChatHandler(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean handleChat(NexusPlayer player, String chatColor, AsyncPlayerChatEvent e) {
        String format = "{score} {level} &r{displayName}&8: {message}";
        String nameColor;
        String score = "&8<&3" + MCUtils.formatNumber(player.getStatValue("sg_score").getAsInt()) + "&8>";
        String level = "&8(&2&l" + player.getStatValue("level").getAsInt() + "&8)";
        String spectators = "&8[&cSpectators&8]";
        String tag = "";
        if (player.getTags().hasActiveTag()) {
            tag = " " + player.getTags().getActive().getDisplayName();
        }
        
        Game game = plugin.getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                format = "&8[&cSpectators&8] &r{displayName}&8: {message}";
                nameColor = player.getRank().getColor();
            } else {
                nameColor = gamePlayer.getTeam().getColor();
            }
        } else {
            if (player.getRank() != Rank.MEMBER) {
                nameColor = "&f";
            } else {
                nameColor = Rank.MEMBER.getColor();
            }
        }
        
        String prefix = "";
        if (player.getRank() != Rank.MEMBER) {
            prefix = player.getRank().getPrefix() + " ";
        }
        
        String displayName = prefix + nameColor + player.getName() + tag;
        
        format = format.replace("{score}", score);
        format = format.replace("{level}", level);
        format = format.replace("{displayName}", displayName);
        format = format.replace("{message}", chatColor + ChatColor.stripColor(e.getMessage()));
        
        if (game != null) {
            if (game.getState().ordinal() >= GameState.INGAME.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
                if (game.getPlayer(player.getUniqueId()).getTeam() == GameTeam.SPECTATORS) {
                    for (GamePlayer p : game.getPlayers().values()) {
                        if (p.getTeam() == GameTeam.SPECTATORS) {
                            p.sendMessage(format);
                        } else if (p.getToggleValue("spectatorchat")) {
                            String tmpFormat = StaffChat.PREFIX + " " + format;
                            tmpFormat = tmpFormat.replace(score + " ", "");
                            tmpFormat = tmpFormat.replace(level + " ", "");
                            p.sendMessage(tmpFormat);
                        }
                    }
                    game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "deadchat", e.getPlayer().getName() + ":" + e.getMessage().replace("'", "''")));
                } else {
                    game.sendMessage(format);
                    game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "chat", e.getPlayer().getName() + ":" + e.getMessage().replace("'", "''")));
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
