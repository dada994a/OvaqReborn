package net.shoreline.client.impl.event.render.entity;

import net.shoreline.client.api.event.Event;
import net.minecraft.util.math.Vec3d;

public class TotemParticleEvent extends Event {
    private final Vec3d position;  // パーティクルの位置
    public double velocityX;        // X方向の速度
    public double velocityY;        // Y方向の速度
    public double velocityZ;        // Z方向の速度
    public int color;               // パーティクルの色（ARGB形式）

    public TotemParticleEvent(Vec3d position) {
        this.position = position;
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;
        this.color = 0xFFFFFFFF;  // デフォルトの色（白）
    }

    public Vec3d getPosition() {
        return position;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
