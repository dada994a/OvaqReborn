package net.shoreline.client.impl.module.movement;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class BoatFlyModule extends ToggleModule {

    Config<Float> forwardSpeed = new NumberConfig<>("ForwardSpeed", "Speed while moving forward", 0.1f, 1.0f, 20.0f);
    Config<Float> backwardSpeed = new NumberConfig<>("BackwardSpeed", "Speed while moving backward", 0.1f, 1.0f, 20.0f);
    Config<Float> upwardSpeed = new NumberConfig<>("UpwardSpeed", "Speed while moving upward", 0.1f, 1.0f, 10.0f);
    Config<Boolean> changeForwardSpeed = new BooleanConfig("ChangeForwardSpeed", "Enable changing forward/backward speed", true);

    public BoatFlyModule() {
        super("BoatFly", "Allows you to fly while in a boat by CzechDonkey", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (!mc.player.hasVehicle()) {
            return;
        }

        Entity vehicle = mc.player.getVehicle();
        Vec3d velocity = vehicle.getVelocity();

        double motionX = velocity.x;
        double motionY = 0;
        double motionZ = velocity.z;

        if (mc.options.jumpKey.isPressed()) {
            motionY = upwardSpeed.getValue();
        } else if (mc.options.sprintKey.isPressed()) {
            motionY = velocity.y;
        }

        if (changeForwardSpeed.getValue()) {
            double speed;
            if (mc.options.forwardKey.isPressed()) {
                speed = forwardSpeed.getValue();
            } else if (mc.options.backKey.isPressed()) {
                speed = -backwardSpeed.getValue();
            } else {
                speed = 0;
            }

            float yawRad = vehicle.getYaw() * MathHelper.RADIANS_PER_DEGREE;
            motionX = MathHelper.sin(-yawRad) * speed;
            motionZ = MathHelper.cos(yawRad) * speed;
        }

        vehicle.setVelocity(motionX, motionY, motionZ);
    }
}