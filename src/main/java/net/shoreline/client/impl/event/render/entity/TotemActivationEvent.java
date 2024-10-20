package net.shoreline.client.impl.event.render.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.shoreline.client.api.event.Event;

public class TotemActivationEvent extends Event {
    private final PlayerEntity player;

    public TotemActivationEvent(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return player;
    }
}

