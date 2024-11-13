package net.shoreline.client.impl.module.combat;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.RotationModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;

import java.awt.*;

/**
 * @author Hypinohaizin
 * @since 2024/11/13 12:18
 * これはBEta番です
 */
public class AntiCrawlModule extends RotationModule {

 Config<Float> rangeConfig = new NumberConfig<>("Range", "Block clearance range", 1.0f, 3.0f, 5.0f);
 Config<Float> speedConfig = new NumberConfig<>("Speed", "Block breaking speed", 0.1f, 1.0f, 1.0f);
 //Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotate when clearing blocks", true);
 Config<Color> clearanceColorConfig = new ColorConfig("ClearanceColor", "Outline color for blocks to clear", new Color(150, 150, 255, 100));

 private ClearanceData clearanceData;

 public AntiCrawlModule() {
  super("AntiCrawl", "Prevents crawling by clearing obstructive blocks", ModuleCategory.COMBAT, 800);
 }

 @Override
 protected void onDisable() {
  clearanceData = null;
  Managers.INVENTORY.syncToClient();
 }

 @EventListener
 public void onPlayerTick(final PlayerTickEvent event) {
  if (clearanceData != null) {
   double distance = clearanceData.getPos().getSquaredDistance(mc.player.getX(), mc.player.getY(), mc.player.getZ());
   if (distance > ((NumberConfig<Float>) rangeConfig).getValueSq()) {
    clearanceData = null;
    return;
   }

   if (clearanceData.getState().isAir()) {
    clearanceData.resetDamage();
    return;
   }

   float damageDelta = Modules.SPEEDMINE.calcBlockBreakingDelta(clearanceData.getState(), mc.world, clearanceData.getPos());
   if (clearanceData.damage(damageDelta) >= clearanceData.getSpeed()) {
    if (mc.player.isUsingItem()) {
     return;
    }
    stopClearing(clearanceData);
   }
  } else {
   BlockPos headBlock = new BlockPos((int) mc.player.getX(), (int) (mc.player.getY() + 1), (int) mc.player.getZ());
   BlockState blockAbove = mc.world.getBlockState(headBlock);
   if (!blockAbove.isAir()) {
    clearanceData = new ClearanceData(headBlock, speedConfig.getValue());
    startClearing(clearanceData);
   }
  }
 }

 @EventListener
 public void onRenderWorld(final RenderWorldEvent event) {
  renderClearanceData(event.getMatrices(), clearanceData);
 }

 private void renderClearanceData(MatrixStack matrixStack, ClearanceData data) {
  if (data != null) {
   BlockPos pos = data.getPos();
   VoxelShape shape = VoxelShapes.fullCube();
   Box renderBox = shape.getBoundingBox();
   Box expandedBox = new Box(pos.getX() + renderBox.minX, pos.getY() + renderBox.minY, pos.getZ() + renderBox.minZ, pos.getX() + renderBox.maxX, pos.getY() + renderBox.maxY, pos.getZ() + renderBox.maxZ);
   int color = clearanceColorConfig.getValue().getRGB();
   RenderManager.renderBox(matrixStack, expandedBox, color);
   RenderManager.renderBoundingBox(matrixStack, expandedBox, 2.5f, color);
  }
 }

 private void startClearing(ClearanceData data) {
  if (data.getState().isAir()) {
   return;
  }
  Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(
          PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, data.getPos(), Direction.UP, id));
  data.setStarted();
 }

 private void stopClearing(ClearanceData data) {
  if (!data.isStarted() || data.getState().isAir()) {
   return;
  }
  Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(
          PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), Direction.UP, id));
  data.setCleared();
 }

 public static class ClearanceData {
  private final BlockPos pos;
  private final float speed;
  private float blockDamage;
  private boolean started;
  private boolean cleared;

  public ClearanceData(BlockPos pos, float speed) {
   this.pos = pos;
   this.speed = speed;
  }

  public float damage(float dmg) {
   blockDamage += dmg;
   return blockDamage;
  }

  public void resetDamage() {
   blockDamage = 0.0f;
  }

  public BlockPos getPos() {
   return pos;
  }

  public BlockState getState() {
   return mc.world.getBlockState(pos);
  }

  public boolean isStarted() {
   return started;
  }

  public void setStarted() {
   this.started = true;
  }

  public void setCleared() {
   this.cleared = true;
   resetDamage();
  }

  public boolean isCleared() {
   return cleared;
  }

  public float getSpeed() {
   return speed;
  }
 }
}
