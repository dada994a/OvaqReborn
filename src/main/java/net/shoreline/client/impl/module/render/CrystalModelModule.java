package net.shoreline.client.impl.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.minecraft.client.render.*;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 * @author OvaqReborn
 * @since 1.0
 */
public class CrystalModelModule extends ToggleModule {
    public static HashMap<EndCrystalEntity, Double> spinMap = new HashMap<>();
    public static HashMap<Vec3d, Double> posSpinMap = new HashMap<>();

    public static HashMap<EndCrystalEntity, Double> floatMap = new HashMap<>();
    public static HashMap<Vec3d, Double> posFloatMap = new HashMap<>();
    public static Random random = new Random();

    public static Config<Float> spinValue = new NumberConfig<>("SpinSpeed", "Speed of crystal spin", 1f, 0f, 3f);
    public static Config<Float> floatValue = new NumberConfig<>("FloatSpeed", "Speed of crystal float", 1f, 0f, 3f);
    public static Config<Float> floatOffset = new NumberConfig<>("FloatOffset", "Offset for floating", 0f, -1f, 1f);
    public static Config<Boolean> sync = new BooleanConfig("Sync", "Sync crystal effects", true);
    public static Config<Float> spinAdd = new NumberConfig<>("SpinNewAdd", "Additional spin value", 0f, 0f, 100f);

    public CrystalModelModule() {
        super("CrystalModel", "Render and manage crystals", ModuleCategory.RENDER);
    }

    @EventListener
    public void onUpdate() {
        if (!sync.getValue()) {
            return;
        }
        List<EndCrystalEntity> noSpinAge = new ArrayList<>();
        List<EndCrystalEntity> noFloatAge = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity crystal) {
                if (spinMap.getOrDefault(crystal, -1D) != -1) {
                    spinMap.put(crystal, spinMap.get(crystal) + spinValue.getValue());
                    posSpinMap.put(crystal.getPos(), spinMap.get(crystal));
                } else {
                    noSpinAge.add(crystal);
                }
                if (floatMap.getOrDefault(crystal, -1D) != -1) {
                    floatMap.put(crystal, floatMap.get(crystal) + floatValue.getValue());
                    posFloatMap.put(crystal.getPos(), floatMap.get(crystal));
                } else {
                    noFloatAge.add(crystal);
                }
            }
        }

        for (EndCrystalEntity crystal : noSpinAge) {
            if (spinMap.getOrDefault(crystal, -1D) == -1) {
                spinMap.put(crystal, posSpinMap.getOrDefault(crystal.getPos(), (double) random.nextInt(10000)) + spinAdd.getValue());
            }
        }

        for (EndCrystalEntity crystal : noFloatAge) {
            if (floatMap.getOrDefault(crystal, -1D) == -1) {
                floatMap.put(crystal, posFloatMap.getOrDefault(crystal.getPos(), (double) random.nextInt(10000)));
            }
        }
    }

    public static double getSpinAge(EndCrystalEntity crystal) {
        if (!sync.getValue()) {
            return crystal.endCrystalAge;
        }
        if (spinMap.getOrDefault(crystal, -1D) == -1) {
            spinMap.put(crystal, posSpinMap.getOrDefault(crystal.getPos(), (double) random.nextInt(10000)) + spinAdd.getValue());
        }
        double age = spinMap.getOrDefault(crystal, posSpinMap.getOrDefault(crystal.getPos(), -1d));
        if (age != -1d) {
            return age;
        }
        age = random.nextInt(10000);
        posSpinMap.put(crystal.getPos(), age);
        return age;
    }

    public static double getFloatAge(EndCrystalEntity crystal) {
        if (!sync.getValue()) {
            return crystal.endCrystalAge;
        }
        if (floatMap.getOrDefault(crystal, -1D) == -1) {
            floatMap.put(crystal, posFloatMap.getOrDefault(crystal.getPos(), (double) random.nextInt(10000)));
        }
        double age = floatMap.getOrDefault(crystal, posFloatMap.getOrDefault(crystal.getPos(), -1d));
        if (age != -1d) {
            return age + floatOffset.getValue();
        }
        age = random.nextInt(10000);
        posFloatMap.put(crystal.getPos(), age);
        return age + floatOffset.getValue();
    }
    @EventListener
    public void RenderCrystal(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, int i, ModelPart core, ModelPart frame) {
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        matrixStack.push();
        float h =  EndCrystalEntityRenderer.getYOffset(endCrystalEntity, g);
        float j = ((float) endCrystalEntity.endCrystalAge + g) * 3.0f;
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0f, -0.5f, 0.0f);
        int k = OverlayTexture.DEFAULT_UV;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, (float) Math.sin(0.7853981633974483), (float) Math.sin(0.7853981633974483), (float) Math.sin(0.7853981633974483)));
        frame.render(matrixStack, buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, (float) Math.sin(0.7853981633974483), 0.0f, (float) Math.sin(0.7853981633974483)));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        frame.render(matrixStack, buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, (float) Math.sin(0.7853981633974483), 0.0f, (float) Math.sin(0.7853981633974483)));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        core.render(matrixStack, buffer, i, k);
        matrixStack.pop();
        matrixStack.pop();
        tessellator.draw();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }
}
