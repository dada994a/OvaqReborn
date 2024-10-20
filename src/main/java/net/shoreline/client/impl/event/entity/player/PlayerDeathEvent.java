package net.shoreline.client.impl.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.shoreline.client.api.event.Event;

public class PlayerDeathEvent extends Event {

    private final PlayerEntity victim;
    private final PlayerEntity killer;

    public PlayerDeathEvent(PlayerEntity victim, PlayerEntity killer) {
        this.victim = victim;
        this.killer = killer;
    }

    public PlayerEntity getVictim() {
        return victim;
    }

    public PlayerEntity getKiller() {
        return killer;
    }
}

