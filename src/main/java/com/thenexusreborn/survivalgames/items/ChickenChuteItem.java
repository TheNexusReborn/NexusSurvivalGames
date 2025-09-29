package com.thenexusreborn.survivalgames.items;

import com.stardevllc.starcore.api.itembuilder.ItemBuilders;
import com.stardevllc.staritems.model.CustomItem;
import com.stardevllc.staritems.model.types.PlayerEvent;
import com.stardevllc.starmclib.XMaterial;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.mutations.impl.ChickenMutation;
import org.bukkit.event.block.Action;

public class ChickenChuteItem extends CustomItem {
    public ChickenChuteItem(SurvivalGames plugin) {
        super(plugin, "chicken_chute_item", ItemBuilders.of(XMaterial.FEATHER).displayName("&bChicken Chute")
                .addLoreLine("&7Create a parachute of chickens (5s cooldown)"));
        
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
            
            if (gamePlayer.getTeam() != GameTeam.MUTATIONS) {
                return;
            }
            
            if (gamePlayer.getMutation() == null) {
                return;
            }
            
            if (!(gamePlayer.getMutation() instanceof ChickenMutation chickenMutation)) {
                return;
            }
            
            if (!chickenMutation.isParachuteOnCooldown()) {
                if (chickenMutation.isChuteActive()) {
                    chickenMutation.deactivateChute();
                } else {
                    chickenMutation.activateChute();
                }
            } else {
                gamePlayer.sendMessage(MsgType.WARN + "Chicken Chute is still on cooldown: &e" + chickenMutation.getParachuteCooldownRemainingSeconds() + "s&c!");
            }
        });
    }
}
