package com.thenexusreborn.survivalgames.mutations.timer;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starlib.clock.callback.ClockCallback;
import com.stardevllc.starlib.clock.snapshot.TimerSnapshot;
import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.gamelog.GameMutateAction;
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

        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timerSnapshot.getTime());

        p.sendMessage(ColorHandler.getInstance().color(MsgType.INFO + "&lMUTATING: " + secondsLeft) + "s...");

        if (secondsLeft == 15 || secondsLeft == 10 || secondsLeft <= 5 && secondsLeft > 0) {
            t.sendMessage(ColorHandler.getInstance().color("&4&l>> &c" + p.getName() + " is &lMUTATING! &cThey spawn in &c&l" + secondsLeft + "s..."));
        }

        if (secondsLeft <= 0) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(p.getUniqueId());
            t.sendMessage(ColorHandler.getInstance().color("&6&l>> " + nexusPlayer.getColoredName().toUpperCase() + " &c&lIS AFTER YOU! RUN!"));

            game.addMutation(mutation);
            game.getGameInfo().getActions().add(new GameMutateAction(nexusPlayer.getName(), t.getName(), mutation.getType()));
            game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "mutation", nexusPlayer.getName() + " mutated agaisnt " + t.getName() + " as a " + mutation.getType().getDisplayName()));
        }
    }

    @Override
    public long getPeriod() {
        return 1000L;
    }
}
