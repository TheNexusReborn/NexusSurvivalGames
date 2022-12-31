package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.LobbyState;
import com.thenexusreborn.survivalgames.map.GameMap;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public class MapVoteCommand implements CommandExecutor {
    
    private final SurvivalGames plugin;
    
    public MapVoteCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a map name or position."));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }
        
        if (plugin.getGame() != null) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You cannot vote for a map during a game."));
            return true;
        }
        
        if (!(plugin.getLobby().getState() == LobbyState.WAITING || plugin.getLobby().getState() == LobbyState.COUNTDOWN)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid lobby state to vote."));
            return true;
        }
    
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
        
        if (nexusPlayer.getToggleValue("vanish")) {
            nexusPlayer.sendMessage(MsgType.WARN + "You cannot vote for a map while in vanish.");
            return true;
        }
        
        try {
            int position = Integer.parseInt(args[0]);
            boolean contains = plugin.getLobby().getMapOptions().containsKey(position);
            if (!contains) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "The map options do not contain that position."));
                return true;
            }
    
            plugin.getLobby().addMapVote(nexusPlayer, plugin.getLobby().getMapSigns().get(position));
        } catch (NumberFormatException e) {
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg).append(" ");
            }
    
            GameMap gameMap = SGUtils.getGameMapFromInput(sb.toString().trim(), sender);
            if (gameMap == null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a game map."));
                return true;
            }
    
            for (Entry<Integer, GameMap> entry : plugin.getLobby().getMapOptions().entrySet()) {
                if (gameMap.getName().equalsIgnoreCase(entry.getValue().getName())) {
                    plugin.getLobby().addMapVote(nexusPlayer, plugin.getLobby().getMapSigns().get(entry.getKey()));
                    return true;
                }
            }
        }
        
        return true;
    }
}
