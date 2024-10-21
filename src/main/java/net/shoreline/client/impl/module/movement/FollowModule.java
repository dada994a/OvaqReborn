package net.shoreline.client.impl.module.movement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.util.string.EnumFormatter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
//SKID BY SHIMEJI
/**
 * @author OvaqReborn
 * @since 1.0
 */
public class FollowModule extends ToggleModule {

    Config<FollowMode> modeConfig = new EnumConfig<>("Mode", "Mode to follow the target", FollowMode.MOTION, FollowMode.values());
    Config<Float> speedConfig = new NumberConfig<>("Speed", "Speed of following", 1.0f, 0.1f, 10.0f);

    public FollowModule() {
        super("Follow", "Automatically follow a target player.", ModuleCategory.MOVEMENT);
    }

    private PlayerEntity getTarget() {
        List<PlayerEntity> players = mc.world.getPlayers().stream()
                .filter(player -> !player.equals(mc.player))
                .collect(Collectors.toList());

        return players.isEmpty() ? null : players.get(0);
    }

    @Override
    public String getModuleData() {
        DecimalFormat decimal = new DecimalFormat("0.0");
        return String.format("Mode: %s, Speed: %sx", EnumFormatter.formatEnum(modeConfig.getValue()), decimal.format(speedConfig.getValue()));
    }

    @EventListener
    public void onTick(TickEvent event) {
        PlayerEntity target = getTarget();
        if (target != null) {
            switch (modeConfig.getValue()) {
                case MOTION:
                    followUsingMotion(target);
                    break;
                case TELEPORT:
                    mc.player.teleport(target.getX(), target.getY(), target.getZ());
                    break;
                case SET_POSITION:
                    mc.player.setPos(target.getX(), target.getY(), target.getZ());
                    break;
            }
        }
    }

    private void followUsingMotion(PlayerEntity target) {
        Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
        Vec3d currentPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        Vec3d direction = targetPos.subtract(currentPos).normalize();

        mc.player.setVelocity(direction.x * speedConfig.getValue(), direction.y * speedConfig.getValue(), direction.z * speedConfig.getValue());
    }
    private enum FollowMode {
        MOTION,
        TELEPORT,
        SET_POSITION
    }
}
