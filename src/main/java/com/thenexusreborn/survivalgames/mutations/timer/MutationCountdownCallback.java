package com.thenexusreborn.survivalgames.mutations.timer;

import com.stardevllc.clock.callback.ClockCallback;
import com.stardevllc.clock.snapshot.TimerSnapshot;
import com.stardevllc.colors.StarColors;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MutationCountdownCallback implements ClockCallback<TimerSnapshot> {
    private Game game;
    private Mutation mutation;

    public MutationCountdownCallback(Game game, Mutation mutation) {
        this.game = game;
        this.mutation = mutation;
    }

    @Override
    public void callback(TimerSnapshot timerSnapshot) {
        Player p = Bukkit.getPlayer(mutation.getPlayer());

        if (p == null) {
            return;
        }

        Player t = Bukkit.getPlayer(mutation.getTarget());
        if (t == null) {
            return;
        }

        long secondsLeft = (long) TimeUnit.MILLISECONDS.toSeconds(timerSnapshot.getTime());

        p.sendMessage(StarColors.color(MsgType.INFO + "&lMUTATING: " + secondsLeft) + "s...");

        if (secondsLeft == 15 || secondsLeft == 10 || secondsLeft <= 5 && secondsLeft > 0) {
            t.sendMessage(StarColors.color("&4&l>> &c" + p.getName() + " is &lMUTATING! &cThey spawn in &c&l" + secondsLeft + "s..."));
        }

        if (secondsLeft <= 0) {
            game.addMutation(mutation);
        }
    }

    @Override
    public long getPeriod() {
        return 1000L;
    }
}
