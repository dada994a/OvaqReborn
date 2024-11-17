package net.shoreline.client.util.math;

import net.minecraft.client.render.Camera;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.mixin.accessor.AccessorGameRenderer;
import net.shoreline.client.util.Globals;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class ProjectionUtil implements Globals {
    public static Vector2f project(double x, double y, double z) {
        Camera camera = mc.getEntityRenderDispatcher().camera;
        Vec3d cameraPos = camera.getPos();

        Quaternionf cameraRotation = mc.getEntityRenderDispatcher().getRotation();
        cameraRotation.conjugate();

        Vector3f result3f = new Vector3f(
                (float) (cameraPos.x - x),
                (float) (cameraPos.y - y),
                (float) (cameraPos.z - z));

        result3f.rotate(cameraRotation);

        if (mc.options.getBobView().getValue()) {
            if (mc.getCameraEntity() instanceof PlayerEntity playerentity) {
                calculateViewBobbing(playerentity, result3f);
            }
        }

        double fov = ((AccessorGameRenderer) mc.gameRenderer)
                .invokeGetFov(camera, mc.getTickDelta(), true);

        return calculateScreenPosition(result3f, fov);
    }

    private static void calculateViewBobbing(PlayerEntity playerEntity, Vector3f result3f) {
        float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
        float f1 = -(playerEntity.horizontalSpeed + f * mc.getTickDelta());
        float f2 = MathHelper.lerp(mc.getTickDelta(), playerEntity.prevStrideDistance, playerEntity.strideDistance);

        float angle = Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F;
        angle *= ((float) Math.PI / 180F);

        Quaternionf quaternion = new Quaternionf().setAngleAxis(angle, 1.0F, 0.0F, 0.0F);
        quaternion.conjugate();
        result3f.rotate(quaternion);

        float angle1 = MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F;
        angle1 *= ((float) Math.PI / 180F);

        Quaternionf quaternion1 = new Quaternionf().setAngleAxis(angle1, 0.0F, 0.0F, 1.0F);
        quaternion1.conjugate();
        result3f.rotate(quaternion1);

        Vector3f bobTranslation = new Vector3f((MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F),
                (-Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2)), 0.0f);
        bobTranslation.y = -bobTranslation.y;
        result3f.add(bobTranslation);
    }

    private static Vector2f calculateScreenPosition(Vector3f result3f, double fov) {
        Window window = mc.getWindow();
        float width = window.getScaledWidth() / 2.0F;
        float height = window.getScaledHeight() / 2.0F;
        float x = result3f.x;
        float y = result3f.y;
        float z = result3f.z;

        float scaleFactor = height / (z * (float) Math.tan(Math.toRadians(fov / 2.0F)));
        if (z < 0.0F) {
            return new Vector2f(-x * scaleFactor + width, height - y * scaleFactor);
        }
        return new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    }
}