package com.thenexusreborn.survivalgames.mutations;

import com.starmediadev.starsql.annotations.table.TableName;

import java.util.UUID;

@TableName("unlockedmutations")
public class UnlockedMutation {
    private long id;
    private UUID uuid;
    private String type;
    private long timestamp;

    private UnlockedMutation() {}

    public UnlockedMutation(UUID uuid, String type, long timestamp) {
        this.uuid = uuid;
        this.type = type;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}