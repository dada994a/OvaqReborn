package net.shoreline.client.impl.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

/**
 * @author Hypinohaizin
 * @since 2024/11/08 12:17
 */

public class TotemAnimationsModule extends ToggleModule {

 public TotemAnimationsModule() {
  super("TotemAnimations", "test", ModuleCategory.RENDER);
 }


 Config<TotemAnimationsModule.Mode> modeConfig = new EnumConfig<>("Mode", "Modes", TotemAnimationsModule.Mode.FadeOut, TotemAnimationsModule.Mode.values());

 private ItemStack floatingItem = null;
 private int floatingItemTimeLeft;

 public void showFloatingItem(ItemStack floatingItem) {
  this.floatingItem = floatingItem;
  floatingItemTimeLeft = getTime();
 }

 public void onUpdate() {
  if (floatingItemTimeLeft > 0) {
   --floatingItemTimeLeft;
   if (floatingItemTimeLeft == 0) {
    floatingItem = null;
   }
  }
 }

 public void renderFloatingItem(int scaledWidth, int scaledHeight, float tickDelta) {
  if (floatingItem != null && floatingItemTimeLeft > 0 && !modeConfig.getValue().equals(Mode.Off)) {
   int i = getTime() - floatingItemTimeLeft;
   float f = ((float) i + tickDelta) / (float) getTime();
   float g = f * f;
   float h = f * g;
   float j = 10.25F * h * g - 24.95F * g * g + 25.5F * h - 13.8F * g + 4.0F * f;
   float k = j * 3.1415927F;
   RenderSystem.enableDepthTest();
   RenderSystem.disableCull();
   RenderSystem.enableBlend();
   RenderSystem.defaultBlendFunc();
   MatrixStack matrixStack = new MatrixStack();
   matrixStack.push();
   float f2 = ((float) i + tickDelta);
   float n = 50.0F + 175.0F * MathHelper.sin(k);
   //switch to shit if
   if (modeConfig.getValue().equals(Mode.FadeOut)) {
    final float x2 = (float) (Math.sin(((f2 * 112) / 180f)) * 100);
    final float y2 = (float) (Math.cos(((f2 * 112) / 180f)) * 50);
    matrixStack.translate((float) (scaledWidth / 2) + x2, (float) (scaledHeight / 2) + y2, -50.0F);
    matrixStack.scale(n, -n, n);
   } else if (modeConfig.getValue().equals(Mode.Size)) {
    matrixStack.translate((float) (scaledWidth / 2), (float) (scaledHeight / 2), -50.0F);
    matrixStack.scale(n, -n, n);
   } else if (modeConfig.getValue().equals(Mode.Otkisuli)) {
    matrixStack.translate((float) (scaledWidth / 2), (float) (scaledHeight / 2), -50.0F);
    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f2 * 2));
    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f2 * 2));
    matrixStack.scale(200 - f2 * 1.5f, -200 + f2 * 1.5f, 200 - f2 * 1.5f);
   } else if (modeConfig.getValue().equals(Mode.Insert)) {
    matrixStack.translate((float) (scaledWidth / 2), (float) (scaledHeight / 2), -50.0F);
    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f2 * 3));
    matrixStack.scale(200 - f2 * 1.5f, -200 + f2 * 1.5f, 200 - f2 * 1.5f);
   } else if (modeConfig.getValue().equals(Mode.Fall)) {
    float downFactor = (float) (Math.pow(f2, 3) * 0.2f);
    matrixStack.translate((float) (scaledWidth / 2), (float) (scaledHeight / 2) + downFactor, -50.0F);
    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f2 * 5));
    matrixStack.scale(200 - f2 * 1.5f, -200 + f2 * 1.5f, 200 - f2 * 1.5f);
   } else if (modeConfig.getValue().equals(Mode.Rocket)) {
    float downFactor = (float) (Math.pow(f2, 3) * 0.2f) - 20;
    matrixStack.translate((float) (scaledWidth / 2), (float) (scaledHeight / 2) - downFactor, -50.0F);
    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f2 * floatingItemTimeLeft * 2));
    matrixStack.scale(200 - f2 * 1.5f, -200 + f2 * 1.5f, 200 - f2 * 1.5f);
   } else if (modeConfig.getValue().equals(Mode.Roll)) {
    float rightFactor = (float) (Math.pow(f2, 2) * 4.5f);
    matrixStack.translate((float) (scaledWidth / 2) + rightFactor, (float) (scaledHeight / 2), -50.0F);
    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f2 * 40));
    matrixStack.scale(200 - f2 * 1.5f, -200 + f2 * 1.5f, 200 - f2 * 1.5f);
   }

   VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
   RenderSystem.setShaderColor(1f, 1f, 1f, 1f - f);
   mc.getItemRenderer().renderItem(floatingItem, ModelTransformationMode.FIXED, 15728880, OverlayTexture.DEFAULT_UV, matrixStack, immediate, mc.world, 0);
   matrixStack.pop();
   immediate.draw();
   RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

   RenderSystem.disableBlend();
   RenderSystem.enableCull();
   RenderSystem.disableDepthTest();
  }
 }

 private int getTime() {
  if (modeConfig.getValue().equals(Mode.FadeOut))
   return 10;

  if (modeConfig.getValue().equals(Mode.Insert))
   return 20;

  return 40;
 }

 public enum Mode {
  FadeOut, Size, Otkisuli, Insert, Fall, Rocket, Roll, Off
 }
}