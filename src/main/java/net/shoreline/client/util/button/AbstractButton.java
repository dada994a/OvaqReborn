package net.shoreline.client.util.button;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public abstract class AbstractButton implements Button {
    public final String name;
    public final Runnable action;

    public float x, y, width, height;

    protected AbstractButton(String name, Runnable action) {
        this.name = name;
        this.action = action;
    }

    public AbstractButton size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public AbstractButton position(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
