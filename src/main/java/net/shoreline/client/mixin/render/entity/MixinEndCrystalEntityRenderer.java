package net.shoreline.client.mixin.render.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.shoreline.client.OvaqReborn;
import net.shoreline.client.impl.event.render.entity.RenderCrystalEvent;
import net.shoreline.client.impl.module.render.CrystalModelModule;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalEntityRenderer.class)
public abstract class MixinEndCrystalEntityRenderer {

    @Final
    @Shadow
    private ModelPart core;

    @Final
    @Shadow
    private ModelPart frame;

    @Final
    @Shadow
    private ModelPart bottom;

    @Shadow @Final private static RenderLayer END_CRYSTAL;

    @Shadow @Final private static float SINE_45_DEGREES;

    private float yOffset(EndCrystalEntity crystal, float tickDelta) {
        float f = (float) (CrystalModelModule.getFloatAge(crystal) + tickDelta) * CrystalModelModule.floatValue.getValue();
        float g = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
        g = (g * g + g) * 0.4F;
        return g - 1.4F + CrystalModelModule.floatOffset.getValue();
    }

    @Inject(method = "render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    public void hookRender(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {

        if (!CrystalModelModule.INSTANCE.isEnabled()) {
            return;
        }

        if (!CrystalModelModule.sync.getValue()) {
            RenderCrystalEvent renderCrystalEvent = new RenderCrystalEvent(endCrystalEntity, f, g, matrixStack, i, core, frame);
            OvaqReborn.EVENT_HANDLER.dispatch(renderCrystalEvent);
            if (renderCrystalEvent.isCanceled()) {
                ci.cancel();
                return;
            }
        }
        matrixStack.push();
        float h = yOffset(endCrystalEntity, g);
        float j = (float) ((CrystalModelModule.getSpinAge(endCrystalEntity) + g) * 3.0F * CrystalModelModule.spinValue.getValue());
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
        matrixStack.push();
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        matrixStack.translate(0.0F, -0.5F, 0.0F);
        int k = OverlayTexture.DEFAULT_UV;
        if (endCrystalEntity.shouldShowBottom()) {
            this.bottom.render(matrixStack, vertexConsumer, i, k);
        }

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0F, 1.5F + h / 2.0F, 0.0F);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        this.frame.render(matrixStack, vertexConsumer, i, k);
        matrixStack.scale(0.875F, 0.875F, 0.875F);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        this.frame.render(matrixStack, vertexConsumer, i, k);
        matrixStack.scale(0.875F, 0.875F, 0.875F);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        this.core.render(matrixStack, vertexConsumer, i, k);
        matrixStack.pop();
        matrixStack.pop();

        BlockPos blockPos = endCrystalEntity.getBeamTarget();
        if (blockPos != null) {
            float m = (float) blockPos.getX() + 0.5F;
            float n = (float) blockPos.getY() + 0.5F;
            float o = (float) blockPos.getZ() + 0.5F;
            float p = (float) (m - endCrystalEntity.getX());
            float q = (float) (n - endCrystalEntity.getY());
            float r = (float) (o - endCrystalEntity.getZ());
            matrixStack.translate(p, q, r);
            EnderDragonEntityRenderer.renderCrystalBeam(-p, -q + h, -r, g, endCrystalEntity.endCrystalAge, matrixStack, vertexConsumerProvider, i);
        }
    }
}