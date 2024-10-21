package net.shoreline.client.impl.event.entity;

import net.minecraft.entity.Entity;
import net.shoreline.client.api.event.Event;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class SwingAnimationEvent extends Event {
    private final Entity entity;
    private int speed;

    public SwingAnimationEvent(Entity entity, Integer speed) {
        this.entity = entity;
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
