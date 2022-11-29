package com.thenexusreborn.survivalgames.mutations.timer;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.timer.TimerSnapshot;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.mutations.Mutation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class MutationCountdownCallback implements ReturnableCallback<TimerSnapshot, Boolean> {
    
    private static final SurvivalGames plugin = SurvivalGames.getPlugin(SurvivalGames.class);
    
    private Mutation mutation;
    
    private List<Integer> announcedPlayer = new ArrayList<>(), announcedTarget = new ArrayList<>();
    
    public MutationCountdownCallback(Mutation mutation) {
        this.mutation = mutation;
    }
    
    public Boolean callback(TimerSnapshot timerSnapshot) {
        Player p = Bukkit.getPlayer(mutation.getPlayer());
    
        if (p == null) {
            return false;
        }
    
        Player t = Bukkit.getPlayer(mutation.getTarget());
        if (t == null) {
            p.sendMessage(MCUtils.color(MsgType.WARN + "Your target is no longer online, mutation cancelled."));
            return false;
        }
    
        Game game = plugin.getGame();
        if (game == null) {
            return false;
        }
    
        int secondsLeft = timerSnapshot.getSecondsLeft();
        
        if (!announcedPlayer.contains(secondsLeft)) {
            p.sendMessage(MCUtils.color(MsgType.INFO + "&lMUTATING: " + secondsLeft) + "s...");
            announcedPlayer.add(secondsLeft);
        }
        
        if (secondsLeft == 15 || secondsLeft == 10 || (secondsLeft <= 5 && secondsLeft > 0)) {
            if (!announcedTarget.contains(secondsLeft)) {
                t.sendMessage(MCUtils.color("&4&l>> &c" + p.getName() + " is &lMUTATING! &cThey spawn in &c&l" + secondsLeft + "s..."));
                announcedTarget.add(secondsLeft );
            }
        }
        
        if (secondsLeft <= 0) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(p.getUniqueId());
            t.sendMessage(MCUtils.color("&6&l>> " + nexusPlayer.getColoredName().toUpperCase() + " &c&lIS AFTER YOU! RUN!"));
            
            game.addMutation(mutation);
            game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "mutation", nexusPlayer.getName() + " mutated agaisnt " + t.getName()));
            return false;
        }
    
        return true;
    }
}
