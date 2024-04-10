package com.thenexusreborn.survivalgames.game.state.phase;

import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.game.state.GamePhase;
import com.thenexusreborn.survivalgames.game.state.PhaseStatus;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.thenexusreborn.survivalgames.game.OldGameState.TELEPORT_START_DONE;

public class TeleportToMapPhase extends GamePhase {
    public TeleportToMapPhase(Game game) {
        super(game, "teleport_to_map");
    }

    @Override
    public void beginphase() {
        try {
            setStatus(Status.RESETTING_SPAWNS);
            game.resetSpawns();
            setStatus(Status.RESETTING_PLAYERS);
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
            setStatus(Status.TELEPORTING_TRIBUTES);
            game.teleportTributes(tributes, mapSpawn);
            setStatus(Status.TELEPORTING_SPECTATORS);
            game.teleportSpectators(spectators, mapSpawn);
            setStatus(Status.REMOVING_MONSTERS);
            for (Entity entity : game.getGameMap().getWorld().getEntities()) {
                if (entity instanceof Monster) {
                    entity.remove();
                }
            }

            setStatus(Status.RECALCULATING_VISIBILITY);
            setStatus(PhaseStatus.COMPLETE);
            game.setState(TELEPORT_START_DONE);
        } catch (Exception e) {
            e.printStackTrace();
            game.handleError("There was an error teleporting players to their starting positions.");
        }
    }
    
    public enum Status implements PhaseStatus {
        RESETTING_SPAWNS, TELEPORTING_TRIBUTES, TELEPORTING_SPECTATORS, REMOVING_MONSTERS, RECALCULATING_VISIBILITY, RESETTING_PLAYERS

    }
}
