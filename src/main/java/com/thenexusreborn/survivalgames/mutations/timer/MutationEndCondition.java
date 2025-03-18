package com.thenexusreborn.survivalgames.mutations.timer;

import com.stardevllc.clock.condition.ClockEndCondition;
import com.stardevllc.clock.snapshot.TimerSnapshot;
import com.stardevllc.colors.StarColors;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        
        if (game.getPlayer(mutation.getPlayer()).getTeam() != GameTeam.SPECTATORS) {
            return true;
        }

        Player t = Bukkit.getPlayer(mutation.getTarget());
        if (t == null) {
            p.sendMessage(StarColors.color(MsgType.WARN + "Your target is no longer online, mutation cancelled."));
            return true;
        }

        Game game = mutation.getGame();
        if (game == null) {
            return true;
        }

        if (!(game.getState() == Game.State.INGAME || game.getState() == Game.State.INGAME_DEATHMATCH)) {
            p.sendMessage(StarColors.color(MsgType.WARN + "You are no longer allowed to mutate due to the game's current progress."));
            return true;
        }

        GamePlayer targetPlayer = game.getPlayer(mutation.getTarget());
        if (targetPlayer.getTeam() != GameTeam.TRIBUTES) {
            if (game.getSettings().isAllowKillersKiller()) {
                GamePlayer mutationPlayer = game.getPlayer(mutation.getPlayer());
                UUID killer = mutationPlayer.getMutationTarget();
                mutation.setTarget(killer);
                return false;
            }
            
            p.sendMessage(StarColors.color(MsgType.WARN + "Your target is no longer a tribute, mutation cancelled."));
            return true;
        }

        return false;
    }
}
