package net.shoreline.client.util.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class EntityUtil {

    /**
     * @param entity
     * @return
     */
    public static float getHealth(Entity entity) {
        if (entity instanceof LivingEntity e) {
            return e.getHealth() + e.getAbsorptionAmount();
        }
        return 0.0f;
    }

    /**
     * @param e
     * @return
     */
    public static boolean isMonster(Entity e) {
        return e instanceof Monster;
    }

    /**
     * @param e
     * @return
     */
    public static boolean isNeutral(Entity e) {
        return e instanceof Angerable && !((Angerable) e).hasAngerTime();
    }

    /**
     * @param e
     * @return
     */
    public static boolean isPassive(Entity e) {
        return e instanceof PassiveEntity || e instanceof AmbientEntity
                || e instanceof SquidEntity;
    }

    public static boolean isVehicle(Entity e) {
        return e instanceof BoatEntity || e instanceof MinecartEntity
                || e instanceof FurnaceMinecartEntity
                || e instanceof ChestMinecartEntity;
    }

    public static Vec3d getEntityVec3dPosition(Entity entityIn) {
        return new Vec3d(entityIn.getX(), entityIn.getY(), entityIn.getZ());
    }

    public static Box getEntityBox(Entity entity) {
        Vec3d pos = getEntityVec3dPosition(entity);
        double width = entity.getWidth();
        double height = entity.getHeight();
        Box box = entity.getBoundingBox();
        if (box != null) {
            width = box.maxX - box.minX;
            height = box.maxY - box.minY;
        }
        Vec3d first = new Vec3d(pos.getX() - width / 2.0, pos.getY(), pos.getZ() - width / 2.0);
        Vec3d second = new Vec3d(pos.getX() + width / 2.0, pos.getY() + height, pos.getZ() + width / 2.0);
        return new Box(first, second);
    }
}
