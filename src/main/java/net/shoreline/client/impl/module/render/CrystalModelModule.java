package net.shoreline.client.impl.module.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
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

    Config<Float> spinValue = new NumberConfig<>("SpinSpeed", "Speed of crystal spin", 1f, 0f, 3f);
    Config<Float> floatValue = new NumberConfig<>("FloatSpeed", "Speed of crystal float", 1f, 0f, 3f);
    Config<Float> floatOffset = new NumberConfig<>("FloatOffset", "Offset for floating", 0f, -1f, 1f);
    Config<Boolean> sync = new BooleanConfig("Sync", "Sync crystal effects", true);
    Config<Float> spinAdd = new NumberConfig<>("SpinNewAdd", "Additional spin value", 0f, 0f, 100f);

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

    public double getSpinAge(EndCrystalEntity crystal) {
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

    public double getFloatAge(EndCrystalEntity crystal) {
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

    public void renderCrystal(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, int i, ModelPart core, ModelPart frame) {
    }
}
