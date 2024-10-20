package net.shoreline.client.impl.event;

import net.minecraft.entity.Entity;
import net.shoreline.client.api.event.Cancelable;
import net.shoreline.client.api.event.Event;

@Cancelable
public class EntityOutlineEvent extends Event {
    private final Entity entity;
    private int outlineColor;
    private float outlineWidth;

    public EntityOutlineEvent(Entity entity) {
        this.entity = entity;
        this.outlineColor = 0xFFFFFFFF;
        this.outlineWidth = 1.0f;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(int color) {
        this.outlineColor = color;
    }

    public float getOutlineWidth() {
        return outlineWidth;
    }

    public void setOutlineWidth(float width) {
        this.outlineWidth = width;
    }
}
