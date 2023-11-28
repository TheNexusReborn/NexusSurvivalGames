package com.thenexusreborn.survivalgames.lobby.timer;

import com.stardevllc.starclock.callback.ClockCallback;
import com.stardevllc.starclock.snapshot.TimerSnapshot;
import com.stardevllc.starlib.time.TimeUnit;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LobbyTimerCallback implements ClockCallback<TimerSnapshot> {

    private static final List<Integer> ANNOUNCE = new LinkedList<>(Arrays.asList(45, 30, 15, 10, 5, 4, 3, 2, 1));

    private Lobby lobby;

    public LobbyTimerCallback(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public void callback(TimerSnapshot snapshot) {
        //lobby.getPlugin().getLogger().info("Timer Time: " + snapshot.getTime());
        int remainingSeconds = (int) TimeUnit.SECONDS.fromMillis(snapshot.getTime());
        if (remainingSeconds <= 0) {
            lobby.prepareGame();
            return;
        }

        if (ANNOUNCE.contains(remainingSeconds)) {
            lobby.sendMessage("&6&l>> &e&lVoting closes in &f&l" + remainingSeconds + "&f&ls&e&l.");
            if (lobby.getLobbySettings().isSounds()) {
                lobby.playSound(Sound.CLICK);
            }
        }
    }
}
