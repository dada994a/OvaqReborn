package net.shoreline.client.impl.module.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.TotemEvent;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import org.joml.Vector3f;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PopChamsModule extends ToggleModule {
/**
 * @author Hypinohaizin
 * @since 10:31
 */
    public PopChamsModule() {
        super("PopChams", "test", ModuleCategory.RENDER);
    }
    Config<Color> colorConfig = new ColorConfig("Color","test",new Color(255,255,255,200));
    Config<Integer>  ySpeedConfig = new NumberConfig<>("Y Speed", "test" ,-10,10,1);
    Config<Integer> aSpeedConfig = new NumberConfig<>("Alpha Speed","test",5,100,1);
    private final CopyOnWriteArrayList<Person> popList = new CopyOnWriteArrayList<>();

    @EventListener
    public void onUpdate() {
        popList.forEach(person -> person.update(popList));
    }

    @EventListener
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);

        popList.forEach(person -> {
            person.modelPlayer.leftPants.visible = false;
            person.modelPlayer.rightPants.visible = false;
            person.modelPlayer.leftSleeve.visible = false;
            person.modelPlayer.rightSleeve.visible = false;
            person.modelPlayer.jacket.visible = false;
            person.modelPlayer.hat.visible = false;
            renderEntity(matrixStack, person.player, person.modelPlayer, person.getAlpha());
        });

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }
    @EventListener
    public void onTotem(TotemEvent event) {
        PlayerEntity e = event.getPlayer();
        if (e == mc.player || e.distanceTo(mc.player) > 20) return;
        PlayerEntity entity = new PlayerEntity(mc.world, BlockPos.ORIGIN, e.bodyYaw, new GameProfile(e.getUuid(), e.getName().getString())) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };
        entity.copyPositionAndRotation(e);
        entity.bodyYaw = e.bodyYaw;
        entity.headYaw = e.headYaw;
        entity.handSwingProgress = e.handSwingProgress;
        entity.handSwingTicks = e.handSwingTicks;
        entity.setSneaking(e.isSneaking());
        entity.limbAnimator.setSpeed(e.limbAnimator.getSpeed());
        //entity.limbAnimator.pos = e.limbAnimator.getPos();
        popList.add(new Person(entity));
    }

    public void renderEntity(MatrixStack matrices, LivingEntity entity, BipedEntityModel<PlayerEntity> modelBase, int alpha) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        Vec3d gpos=entity.getPos().add(0, ySpeedConfig.getValue() / 50, 0);
        entity.setPos(gpos.x,gpos.y,gpos.z);

        matrices.push();
        matrices.translate((float) x, (float) y, (float) z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rad(180 - entity.bodyYaw)));
        prepareScale(matrices);

        modelBase.animateModel((PlayerEntity) entity, entity.limbAnimator.getPos(), entity.limbAnimator.getSpeed(), mc.getTickDelta());
        modelBase.setAngles((PlayerEntity) entity, entity.limbAnimator.getPos(), entity.limbAnimator.getSpeed(), entity.age, entity.headYaw - entity.bodyYaw, entity.getPitch());

        RenderSystem.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        modelBase.render(matrices, buffer, 10, 0, colorConfig.getValue().getRed() / 255f, colorConfig.getValue().getGreen() / 255f, color.getValue().getBlue() / 255f, alpha / 255f);
        tessellator.draw();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private static void prepareScale(MatrixStack matrixStack) {
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.scale(1.6f, 1.8f, 1.6f);
        matrixStack.translate(0.0F, -1.501F, 0.0F);
    }

    public class Person {
        private final PlayerEntity player;
        private final PlayerEntityModel<PlayerEntity> modelPlayer;
        private int alpha;

        public Person(PlayerEntity player) {
            this.player = player;
            modelPlayer = new PlayerEntityModel<>(new EntityRendererFactory.Context(mc.getEntityRenderDispatcher(), mc.getItemRenderer(), mc.getBlockRenderManager(), mc.getEntityRenderDispatcher().getHeldItemRenderer(), mc.getResourceManager(), mc.getEntityModelLoader(), mc.textRenderer).getPart(EntityModelLayers.PLAYER), false);
            modelPlayer.getHead().scale(new Vector3f(-0.3f, -0.3f, -0.3f));
            alpha = colorConfig.getValue().getAlpha();
        }

        public void update(CopyOnWriteArrayList<Person> arrayList) {
            if (alpha <= 0) {
                arrayList.remove(this);
                player.kill();
                player.remove(Entity.RemovalReason.KILLED);
                player.onRemoved();
                return;
            }
            alpha -= aSpeedConfig.getValue();
        }

        public int getAlpha() {
            return clamp(alpha, 0, 255);
        }
    }
    public int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public float rad(float angle) {
        return (float) (angle * Math.PI / 180);
    }
}



