package com.thenexusreborn.survivalgames.mutations.timer;

import com.stardevllc.clock.condition.ClockEndCondition;
import com.stardevllc.clock.snapshot.TimerSnapshot;
import com.stardevllc.starcore.color.ColorHandler;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameState;
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
        
        if (game.getPlayer(mutation.getPlayer()).getTeam() != GameTeam.MUTATIONS) {
            return true;
        }

        Player t = Bukkit.getPlayer(mutation.getTarget());
        if (t == null) {
            p.sendMessage(ColorHandler.getInstance().color(MsgType.WARN + "Your target is no longer online, mutation cancelled."));
            return true;
        }

        Game game = mutation.getGame();
        if (game == null) {
            return true;
        }

        if (!(game.getState() == GameState.INGAME || game.getState() == GameState.INGAME_DEATHMATCH)) {
            p.sendMessage(ColorHandler.getInstance().color(MsgType.WARN + "You are no longer allowed to mutate due to the game's current progress."));
            return true;
        }

        GamePlayer targetPlayer = game.getPlayer(mutation.getTarget());
        if (targetPlayer.getTeam() != GameTeam.TRIBUTES) {
            p.sendMessage(ColorHandler.getInstance().color(MsgType.WARN + "Your target is no longer a tribute, mutation cancelled."));
            return true;
        }
        
        return false;
    }
}
