package com.thenexusreborn.survivalgames.lobby.timer;

import com.stardevllc.clock.callback.ClockCallback;
import com.stardevllc.clock.snapshot.TimerSnapshot;
import com.stardevllc.time.TimeUnit;
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
        //lobby.getPlugin().getLogger().info("Remaining Seconds: " + remainingSeconds);
        if (remainingSeconds <= 0) {
            lobby.prepareGame();
            return;
        }

        if (ANNOUNCE.contains(remainingSeconds)) {
            lobby.sendMessage("&6&l>> &e&lVoting closes in &f&l" + remainingSeconds + "&f&ls&e&l.");
            if (lobby.getLobbySettings().isSounds()) {
                lobby.playSound(Sound.UI_BUTTON_CLICK);
            }
        }
    }
}
