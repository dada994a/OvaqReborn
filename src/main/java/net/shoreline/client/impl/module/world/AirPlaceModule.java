package net.shoreline.client.impl.module.world;

import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.init.Managers;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.render.RenderWorldEvent;

import java.awt.*;

public class AirPlaceModule extends ToggleModule {
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range for placing blocks in the air", 5f, 0f, 6f);
    Config<Boolean> swingConfig = new BooleanConfig("Swing", "Whether to swing the hand on placement", true);
    Config<Color> fillColorConfig = new ColorConfig("Fill Color", "The color for the fill effect", new Color(100, 50, 255, 50));
    Config<Color> lineColorConfig = new ColorConfig("Line Color", "The color for the outline effect", new Color(100, 50, 255, 150));
    Config<Integer> lineWidthConfig = new NumberConfig<>("Line Width", "The width of the outline", 2, 1, 5);

    private BlockHitResult hit;
    private int cooldown;

    public AirPlaceModule() {
        super("AirPlace", "Allows placing blocks in the air", ModuleCategory.WORLD);
    }

    @EventListener
    public void onUpdate() {
        if (cooldown > 0) {
            cooldown--;
        }

        HitResult hitResult = mc.getCameraEntity().raycast(rangeConfig.getValue(), 0, false);

        if (hitResult instanceof BlockHitResult bhr) {
            hit = bhr;
        } else {
            return;
        }

        boolean main = mc.player.getMainHandStack().getItem() instanceof BlockItem;
        boolean off = mc.player.getOffHandStack().getItem() instanceof BlockItem;
        if (mc.options.useKey.isPressed() && (main || off) && cooldown <= 0) {
            mc.interactionManager.interactBlock(mc.player, main ? Hand.MAIN_HAND : Hand.OFF_HAND, hit);
            if (swingConfig.getValue()) {
                mc.player.swingHand(main ? Hand.MAIN_HAND : Hand.OFF_HAND);
            } else {
                Managers.NETWORK.sendPacket(new HandSwingC2SPacket(main ? Hand.MAIN_HAND : Hand.OFF_HAND));
            }
        }
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        if (hit == null || !mc.world.getBlockState(hit.getBlockPos()).getBlock().equals(Blocks.AIR) || (!(mc.player.getMainHandStack().getItem() instanceof BlockItem) && !(mc.player.getOffHandStack().getItem() instanceof BlockItem))) {
            return;
        }
        RenderManager.renderBox(event.getMatrices(), new Box(hit.getBlockPos()), fillColorConfig.getValue().getRGB());
        RenderManager.renderBoundingBox(event.getMatrices(), new Box(hit.getBlockPos()), lineWidthConfig.getValue(), lineColorConfig.getValue().getRGB());
    }
}
