package com.thenexusreborn.survivalgames.mutations.timer;

import com.stardevllc.starclock.condition.ClockEndCondition;
import com.stardevllc.starclock.snapshot.TimerSnapshot;
import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.OldGameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MutationEndCondition implements ClockEndCondition<TimerSnapshot> {
    
    private Mutation mutation;
    private Game game;

    public MutationEndCondition(Mutation mutation, Game game) {
        this.mutation = mutation;
        this.game = game;
    }

    @Override
    public boolean shouldEnd(TimerSnapshot timerSnapshot) {
        Player p = Bukkit.getPlayer(mutation.getPlayer());

        if (p == null) {
            return true;
        }

        Player t = Bukkit.getPlayer(mutation.getTarget());
        if (t == null) {
            p.sendMessage(ColorUtils.color(MsgType.WARN + "Your target is no longer online, mutation cancelled."));
            return true;
        }

        Game game = mutation.getGame();
        if (game == null) {
            return true;
        }

        if (!(game.getState() == OldGameState.INGAME || game.getState() == OldGameState.INGAME_DEATHMATCH)) {
            p.sendMessage(ColorUtils.color(MsgType.WARN + "You are no longer allowed to mutate due to the game's current progress."));
            return true;
        }

        GamePlayer targetPlayer = game.getPlayer(mutation.getTarget());
        if (targetPlayer.getTeam() != GameTeam.TRIBUTES) {
            p.sendMessage(ColorUtils.color(MsgType.WARN + "Your target is no longer a tribute, mutation cancelled."));
            return true;
        }
        
        return false;
    }
}
