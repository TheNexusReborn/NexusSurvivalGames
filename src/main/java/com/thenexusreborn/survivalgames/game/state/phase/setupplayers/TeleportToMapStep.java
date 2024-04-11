package com.thenexusreborn.survivalgames.game.state.phase.setupplayers;

import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.GamePhaseStep;
import com.thenexusreborn.survivalgames.game.state.StepStatus;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportToMapStep extends GamePhaseStep {
    public TeleportToMapStep(GamePhase gamePhase, GamePhaseStep... prerequisiteSteps) {
        super(gamePhase, "teleport_to_map", prerequisiteSteps);
    }

    @Override
    public boolean run() {
        try {
            setStatus(StepStatus.STARTING);
            game.resetSpawns();
            List<UUID> tributes = new ArrayList<>(), spectators = new ArrayList<>();
            for (GamePlayer player : game.getPlayers().values()) {
                player.clearInventory();
                player.clearPotionEffects();
                player.setFood(20, game.getSettings().getStartingSaturation());
                if (player.getTeam() == GameTeam.TRIBUTES) {
                    tributes.add(player.getUniqueId());
                    player.setFlight(false, false);
                } else if (player.getTeam() == GameTeam.SPECTATORS) {
                    spectators.add(player.getUniqueId());
                    player.setFlight(true, true);
                    player.giveSpectatorItems(game);
                }
            }

            Location mapSpawn = game.getGameMap().getCenter().toLocation(game.getGameMap().getWorld());
            game.teleportTributes(tributes, mapSpawn);
            game.teleportSpectators(spectators, mapSpawn);
            for (Entity entity : game.getGameMap().getWorld().getEntities()) {
                if (entity instanceof Monster) {
                    entity.remove();
                }
            }
            setStatus(StepStatus.COMPLETE);
        } catch (Exception e) {
            setStatus(StepStatus.ERROR);
            e.printStackTrace();
        }
        
        setStatus(StepStatus.COMPLETE);
        return true;
    }
}
