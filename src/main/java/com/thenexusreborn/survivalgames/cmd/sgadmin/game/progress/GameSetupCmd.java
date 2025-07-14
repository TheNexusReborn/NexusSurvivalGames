package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameSetupCmd extends GameProgressSubCmd {
    public GameSetupCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "setup", "");
    }

    @Override
    protected void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (game.getState() != Game.State.UNDEFINED) {
            player.sendMessage(MsgType.WARN.format("The game has already been setup."));
            return;
        }
        
        game.setup();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getState() == Game.State.SETUP_COMPLETE) {
                    player.sendMessage(MsgType.INFO.format("The game setup is now complete."));
                    cancel();
                } else if (game.getState() == Game.State.ERROR) {
                    player.sendMessage(MsgType.ERROR.format("There was a problem during Game Setup"));
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
