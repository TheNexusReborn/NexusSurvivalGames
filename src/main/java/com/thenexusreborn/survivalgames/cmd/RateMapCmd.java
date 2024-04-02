package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.gamemaps.model.MapRating;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RateMapCmd implements CommandExecutor {
    private SurvivalGames plugin;
    
    public RateMapCmd(SurvivalGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " <rating>"));
            return true;
        }

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        
        if (game == null) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "You can only rate a map during a game."));
            return true;
        }
    
        SGMap gameMap = game.getGameMap();
        
        int rating;
        try {
            rating = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "Only whole numbers are allowed for the rating."));
            return true;
        }
        
        if (rating < 1 || rating > 5) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "You can only rate a map 1 to 5"));
            return true;
        }
    
        MapRating mapRating = gameMap.getRatings().get(player.getUniqueId());
        if (mapRating != null) {
            if (mapRating.getTimestamp() + TimeUnit.DAYS.toMillis(7) > System.currentTimeMillis()) {
                player.sendMessage(MCUtils.color(MsgType.WARN + "You can only rate the same map only once every 7 days"));
                return true;
            }
            
            if (mapRating.getRating() == rating) {
                player.sendMessage(MCUtils.color(MsgType.WARN + "The rating you provided is the same as your previous rating."));
                return true;
            }
            
            mapRating.setRating(rating);
            player.sendMessage(MCUtils.color(MsgType.INFO + "You changed your rating for map &b" + gameMap.getName() + " &eto &b" + rating));
        } else {
            mapRating = new MapRating(gameMap.getName().toLowerCase().replace("'", "''"), player.getUniqueId(), rating, System.currentTimeMillis());
            player.sendMessage(MCUtils.color(MsgType.INFO + "You rated the map &b" + gameMap.getName() + " &ea &b" + rating));
        }
        
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(mapRating);
        return true;
    }
}
