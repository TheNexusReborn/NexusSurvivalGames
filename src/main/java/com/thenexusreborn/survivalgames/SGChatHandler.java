package com.thenexusreborn.survivalgames;

import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.chat.ChatHandler;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SGChatHandler implements ChatHandler {
    
    private SurvivalGames plugin;
    
    private boolean enabled = true;
    
    public SGChatHandler(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean handleChat(NexusPlayer player, String chatColor, AsyncPlayerChatEvent e) {
        if (!enabled) {
            player.sendMessage("&cChat is currently disabled. You cannot speak.");
            return true;
        }
        
        String format = "{score} {level} &r{displayName}&8: {message}";
        String nameColor; 
        String score = "&8<&3" + MCUtils.formatNumber(player.getStatValue("sg_score")) + "&8>";
        String level = "&8(&2&l" + player.getLevel() + "&8)";
        String spectators = "&8[&cSpectators&8]";
        String tag = "";
        if (player.getTag() != null && !player.getTag().getName().equalsIgnoreCase("null")) {
            tag = " " + player.getTag().getDisplayName();
        }
        
        Game game = plugin.getGame();
        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer.getTeam() == GameTeam.SPECTATORS) {
                format = "{score} &8[&cSpectators&8] {level} &r{displayName}&8: {message}";
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
        if (player.getRank().ordinal() < Rank.MEMBER.ordinal()) {
            prefix = player.getRank().getPrefix() + " ";
        }
        
        String displayName = prefix + nameColor + player.getName() + tag;
        
        format = format.replace("{score}", score);
        format = format.replace("{level}", level);
        format = format.replace("{displayName}", displayName);
        format = format.replace("{message}", chatColor + ChatColor.stripColor(e.getMessage()));
        
        if (game != null) {
            if (game.getState().ordinal() >= GameState.INGAME_GRACEPERIOD.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
                if (game.getPlayer(player.getUniqueId()).getTeam() != GameTeam.TRIBUTES) {
                    for (GamePlayer p : game.getPlayers().values()) {
                        if (p.getTeam() != GameTeam.TRIBUTES) {
                            p.sendMessage(format);
                            game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "deadchat", e.getPlayer().getName() + ":" + e.getMessage().replace("'", "''")));
                        }
                    }
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
    
    public void enableChat() {
        this.enabled = true;
        Bukkit.broadcastMessage(MCUtils.color("&d&l>> &7Chat &aenabled&7."));
    }
    
    public void disableChat() {
        this.enabled = false;
        Bukkit.broadcastMessage(MCUtils.color("&d&l>> &7Chat &cdisabled&7."));
    }
}
