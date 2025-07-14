package com.thenexusreborn.survivalgames.cmd;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.stardevllc.starui.GuiManager;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.menu.TeamMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameTeamCmd extends NexusCommand<SurvivalGames> {
    
    private GuiManager guiManager;
    private GameTeam gameTeam;
    
    public GameTeamCmd(SurvivalGames plugin, GameTeam team) {
        super(plugin, team.name().toLowerCase(), "", Rank.DIAMOND);
        this.gameTeam = team;
        this.playerOnly = true;
        this.guiManager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        if (sgPlayer == null) {
            MsgType.WARN.send(sender, "You cannot do that right now.");
            return true;
        }

        Game game = sgPlayer.getGame();
        if (game == null) {
            MsgType.WARN.send(sender, "You are not in a game");
            return true;
        }
        
        guiManager.openGUI(new TeamMenu(plugin, gameTeam, game, player.getUniqueId()), player);
        return true;
    }
}
