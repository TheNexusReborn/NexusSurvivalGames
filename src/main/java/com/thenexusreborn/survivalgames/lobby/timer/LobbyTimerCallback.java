package com.thenexusreborn.survivalgames.lobby.timer;

import com.thenexusreborn.survivalgames.lobby.Lobby;
import me.firestar311.starclock.api.callback.ClockCallback;
import me.firestar311.starclock.api.snapshot.TimerSnapshot;
import me.firestar311.starlib.api.time.TimeUnit;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LobbyTimerCallback implements ClockCallback<TimerSnapshot> {

    private static final List<Integer> ANNOUNCE = new LinkedList<>(Arrays.asList(45, 30, 15, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1));

    private Lobby lobby;

    public LobbyTimerCallback(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean callback(TimerSnapshot snapshot) {
        int remainingSeconds = (int) TimeUnit.SECONDS.fromMillis(snapshot.getLength() - snapshot.getTime());
        if (remainingSeconds <= 0) {
            lobby.prepareGame();
            return true;
        }

        if (ANNOUNCE.contains(remainingSeconds)) {
            lobby.sendMessage("&6&l>> &e&lVoting closes in &f&l" + remainingSeconds + "&f&ls&e&l.");
            if (lobby.getLobbySettings().isSounds()) {
                lobby.playSound(Sound.CLICK);
            }
        }
        return true;
    }
}
