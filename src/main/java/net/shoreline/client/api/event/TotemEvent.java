package net.shoreline.client.api.event;

import net.minecraft.entity.player.PlayerEntity;

public class TotemEvent extends Event {
    private final PlayerEntity player;

    public TotemEvent(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}
