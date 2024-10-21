package net.shoreline.client.impl.event.entity;
import net.minecraft.entity.Entity;
import net.shoreline.client.api.event.Event;

/**
 * @author OvaqReborn
 * @since 1.0
 */

public class FlagGetEvent extends Event {
    private final Entity entity;
    private final int flag;
    private boolean returnValue;

    public FlagGetEvent(Entity entity, int flag, boolean returnValue) {
        this.entity = entity;
        this.flag = flag;
        this.returnValue = returnValue;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public int getFlag() {
        return this.flag;
    }

    public boolean getReturnValue() {
        return this.returnValue;
    }

    public void setReturnValue(boolean returnValue) {
        this.returnValue = returnValue;
    }
}
