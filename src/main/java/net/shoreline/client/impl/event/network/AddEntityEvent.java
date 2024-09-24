package net.shoreline.client.impl.event.network;

import net.minecraft.entity.Entity;

public class AddEntityEvent {
    private final Entity entity;

    public AddEntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
