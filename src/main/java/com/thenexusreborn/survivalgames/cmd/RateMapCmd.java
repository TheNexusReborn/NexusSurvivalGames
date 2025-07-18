package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.model.MapRating;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RateMapCmd extends NexusCommand<SurvivalGames> {

    public RateMapCmd(SurvivalGames plugin) {
        super(plugin, "ratemap", "", Rank.MEMBER);
        this.playerOnly = true;
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        
        if (!(args.length > 0)) {
            player.sendMessage(MsgType.WARN.format("Usage: /" + label + " <rating>"));
            return true;
        }

        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();

        if (game == null) {
            player.sendMessage(MsgType.WARN.format("You can only rate a map during a game."));
            return true;
        }

        SGMap gameMap = game.getGameMap();

        int rating;
        try {
            rating = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(MsgType.WARN.format("Only whole numbers are allowed for the rating."));
            return true;
        }

        if (rating < 1 || rating > 5) {
            player.sendMessage(MsgType.WARN.format("You can only rate a map 1 to 5"));
            return true;
        }

        MapRating mapRating = gameMap.getRatings().get(player.getUniqueId());
        if (mapRating != null) {
            if (mapRating.getTimestamp() + TimeUnit.DAYS.toMillis(7) > System.currentTimeMillis()) {
                player.sendMessage(MsgType.WARN.format("You can only rate the same map only once every 7 days"));
                return true;
            }

            if (mapRating.getRating() == rating) {
                player.sendMessage(MsgType.WARN.format("The rating you provided is the same as your previous rating."));
                return true;
            }

            mapRating.setRating(rating);
            player.sendMessage(MsgType.INFO.format("You changed your rating for map %v to %v", gameMap.getName(), rating));
        } else {
            mapRating = new MapRating(gameMap.getName().toLowerCase().replace("'", "''"), player.getUniqueId(), rating, System.currentTimeMillis());
            player.sendMessage(MsgType.INFO.format("You rated the map %v &ea %v", gameMap.getName(), rating));
        }

        NexusReborn.getPrimaryDatabase().saveSilent(mapRating);
        return true;
    }
}
