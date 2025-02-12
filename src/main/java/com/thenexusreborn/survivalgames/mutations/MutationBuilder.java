package com.thenexusreborn.survivalgames.mutations;

import com.thenexusreborn.survivalgames.game.GamePlayer;

public class MutationBuilder {
    private final GamePlayer player;
    private MutationType type = MutationType.PIG_ZOMBIE;
    private GamePlayer target;
    private boolean bypassTimer = false;

    public MutationBuilder(GamePlayer player) {
        this.player = player;
        this.target = player.getGame().getPlayer(player.getKiller());
    }

    public MutationBuilder setType(MutationType type) {
        this.type = type;
        return this;
    }

    public MutationBuilder setTarget(GamePlayer target) {
        this.target = target;
        return this;
    }

    public MutationBuilder setBypassTimer(boolean bypassTimer) {
        this.bypassTimer = bypassTimer;
        return this;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public MutationType getType() {
        return type;
    }

    public GamePlayer getTarget() {
        return target;
    }

    public boolean isBypassTimer() {
        return bypassTimer;
    }
}
