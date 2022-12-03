package com.thenexusreborn.survivalgames.game.tasks;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChickenMutationTask extends NexusThread<SurvivalGames> {
    
    private int eggGain = 19;
    
    public ChickenMutationTask(SurvivalGames plugin) {
        super(plugin, 1L, 0L, false);
    }
    
    @Override
    public void onRun() {
        Game game = plugin.getGame();
        if (game == null) {
            return;
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            if (gamePlayer == null) {
                continue;
            }
            if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
                continue;
            }
        
            if (gamePlayer.getMutation() instanceof ChickenMutation chickenMutation) {
    
                if (this.eggGain == 0) {
                    chickenMutation.incrementAmmunition();
                }
                
                ItemStack hand = player.getItemInHand();
                if (player.isBlocking() && hand != null && hand.getType() == Material.WOOD_SWORD && chickenMutation.getAmmunition() > 0) {
                    player.launchProjectile(Egg.class);
                    chickenMutation.decrementAmmunition();
                }
    
                player.setLevel(chickenMutation.getAmmunition());
                
                if (chickenMutation.isChuteActive()) {
                    Location location = player.getLocation();
                    location.setY(location.getBlockY() - 1);
                    if (location.getBlock().getType() == Material.AIR) {
                        player.setVelocity(player.getVelocity().setY(-0.2));
                    } else {
                        chickenMutation.deactivateChute();
                    }
                }
            }
        }
        
        if (eggGain <= 0) {
            eggGain = 19;
        } else {
            eggGain--;
        }
    }
}
