package com.thenexusreborn.survivalgames.items;

import com.stardevllc.starcore.base.XMaterial;
import com.stardevllc.starcore.base.itembuilder.ItemBuilder;
import com.stardevllc.staritems.model.CustomItem;
import com.stardevllc.staritems.model.types.PlayerEvent;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.game.death.DeathType;
import com.thenexusreborn.survivalgames.util.SGUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.block.Action;

import java.util.List;

public class CreeperSuicideBomb extends CustomItem {
    public CreeperSuicideBomb(SurvivalGames plugin) {
        super(plugin, "creeper_suicide_bomb", ItemBuilder.of(XMaterial.GUNPOWDER).displayName("&cSuicide Bomb")
                .setLore(List.of(
                        "&7Explode yourself for higher damage",
                        "&7If you kill your target, you take revenge",
                        "&7If you don't, you become a spectator again"
                )));
        
        addEventHandler(PlayerEvent.INTERACT, e -> {
            Action action = e.getAction();
            if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
            if (sgPlayer == null) {
                return;
            }
            
            if (sgPlayer.getGame() == null) {
                return;
            }
            
            GamePlayer gamePlayer = sgPlayer.getGamePlayer();
            if (gamePlayer == null)  {
                return;
            }
            
            Location loc = gamePlayer.getLocation();
            SGUtils.spawnTNTWithSource(loc, e.getPlayer(), 1, 4F);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (gamePlayer.getTeam() == GameTeam.MUTATIONS) {
                    Game game = gamePlayer.getGame();
                    game.killPlayer(gamePlayer, new DeathInfo(game, System.currentTimeMillis(), gamePlayer, DeathType.SUICIDE, e.getPlayer().getLocation()));
                }
            }, 10L);
        });
    }
}
