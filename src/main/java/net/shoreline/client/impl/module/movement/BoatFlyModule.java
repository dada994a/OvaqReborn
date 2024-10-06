package net.shoreline.client.impl.module.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;

/**
 * @author linus
 * @since 1.0
 * @Credit Shlok2000
 */
public class BoatFlyModule extends ToggleModule {

    Config<Float> speedConfig = new NumberConfig<>("Speed", "The speed of the boat while flying", 0.1f, 1.0f, 10.0f);
    Config<Float> hoverHeight = new NumberConfig<>("HoverHeight", "Height to maintain while hovering", 0.4f, 0.1f, 10.0f);

    public BoatFlyModule() {
        super("BoatFly", "Allows you to fly while in a boat by CzechDonkey", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.player.isRiding() && mc.player.getVehicle() instanceof BoatEntity boat) {
            float forward = mc.player.input.movementForward;
            float strafe = mc.player.input.movementSideways;

            double d = Math.cos(Math.toRadians(mc.player.getYaw() + 90.0f));
            double d2 = Math.sin(Math.toRadians(mc.player.getYaw() + 90.0f));

            if (forward != 0 || strafe != 0) {
                boat.setVelocity(
                        (forward * speedConfig.getValue() * d) + (strafe * speedConfig.getValue() * d2),
                        boat.getVelocity().y,
                        (forward * speedConfig.getValue() * d2) - (strafe * speedConfig.getValue() * d)
                );
            } else {
                double currentY = boat.getY();
                boat.setVelocity(boat.getVelocity().x, currentY + (hoverHeight.getValue() - currentY), boat.getVelocity().z);
            }

            if (mc.player.input.jumping) {
                boat.setVelocity(boat.getVelocity().x, 0.4, boat.getVelocity().z);
            }
            if (mc.player.input.sneaking) {
                boat.setVelocity(boat.getVelocity().x, -0.4, boat.getVelocity().z);
            }
        }
    }
}
