package com.thenexusreborn.survivalgames.mutations;

import java.util.*;

public class PlayerMutations {
    private final Map<String, UnlockedMutation> mutations = new HashMap<>();
    private UUID uuid;

    public PlayerMutations(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UnlockedMutation get(String name) {
        return mutations.get(name);
    }

    public void add(UnlockedMutation mutation) {
        this.mutations.put(mutation.getType(), mutation);
    }

    public void remove(String mutation) {
        this.mutations.remove(mutation);
    }

    public boolean isUnlocked(String mutation) {
        return this.mutations.containsKey(mutation);
    }

    public Set<String> findAll() {
        return new HashSet<>(this.mutations.keySet());
    }
}