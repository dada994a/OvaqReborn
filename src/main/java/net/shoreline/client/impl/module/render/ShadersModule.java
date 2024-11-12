package net.shoreline.client.impl.module.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.impl.manager.render.ShaderManager;
import net.shoreline.client.init.Managers;

import java.awt.*;

public class ShadersModule extends ToggleModule {
    public static ShadersModule INSTANCE;
    public ShadersModule() {
        super("Shaders", "Apply solid shader effects", ModuleCategory.RENDER);
        INSTANCE = this;
    }


    Config<Boolean> hands = new BooleanConfig("Hands", "Render hands with shader effects", true);
    Config<Boolean> players = new BooleanConfig("Players", "Render players with shader effects", true);
    Config<Boolean> self = new BooleanConfig("Self", "Render self with shader effects", true, () -> this.players.getValue());
    Config<Boolean> friends = new BooleanConfig("Friends", "Render friends with shader effects", true);
    Config<Boolean> crystals = new BooleanConfig("Crystals", "Render crystals with shader effects", true);
    Config<Boolean> creatures = new BooleanConfig("Creatures", "Render creatures with shader effects", false);
    Config<Boolean> monsters = new BooleanConfig("Monsters", "Render monsters with shader effects", false);
    Config<Boolean> ambients = new BooleanConfig("Ambients", "Render ambient entities with shader effects", false);
    Config<Boolean> others = new BooleanConfig("Others", "Render other entities with shader effects", false);


    public Config<ShaderManager.Shader> mode = new EnumConfig<>("Mode","a", ShaderManager.Shader.Default, ShaderManager.Shader.values());
    public Config<ShaderManager.Shader> handsMode = new EnumConfig<>("HandsMode","a", ShaderManager.Shader.Default, ShaderManager.Shader.values());

    Config<Integer> maxRange = new NumberConfig<>("MaxRange", "Maximum range for shader rendering", 64, 16, 256);
    public Config<Float> factor = new NumberConfig<>("GradientFactor", "Factor for gradient effect", 2f, 0f, 20f);
    public Config<Float> gradient = new NumberConfig<>("Gradient", "Gradient effect value", 2f, 0f, 20f);
    public Config<Integer> alpha2 = new NumberConfig<>("GradientAlpha", "Alpha value for gradient", 170, 0, 255);
    public Config<Integer> lineWidth = new NumberConfig<>("LineWidth", "Line width for shader rendering", 2, 0, 20);
    public Config<Integer> quality = new NumberConfig<>("Quality", "Quality for shader rendering", 3, 0, 20);
    public Config<Integer> octaves = new NumberConfig<>("SmokeOctaves", "Octaves for smoke shader effect", 10, 5, 30);
    public Config<Integer> fillAlpha = new NumberConfig<>("FillAlpha", "Alpha for fill color", 170, 0, 255);
    public Config<Boolean> glow = new BooleanConfig("SmokeGlow", "Enable glow effect for smoke", true);

    public Config<Color> outlineColor = new ColorConfig("Outline", "Outline color for shader rendering", new Color(0x8800FF00));
    public Config<Color> outlineColor1 = new ColorConfig("SmokeOutline", "Outline color for smoke", new Color(0x8800FF00));
    public Config<Color> outlineColor2 = new ColorConfig("SmokeOutline2", "Second outline color for smoke", new Color(0x8800FF00));
    public Config<Color> fillColor1 = new ColorConfig("Fill", "Fill color for shader", new Color(0x8800FF00));
    public Config<Color> fillColor2 = new ColorConfig("SmokeFill", "Fill color for smoke", new Color(0x8800FF00));
    public Config<Color> fillColor3 = new ColorConfig("SmokeFil2", "Second fill color for smoke", new Color(0x8800FF00));

    public boolean shouldRender(Entity entity) {
        if (entity == null)
            return false;

        if (mc.player == null)
            return false;

        if (mc.player.squaredDistanceTo(entity.getPos()) > maxRange.getValue())
            return false;

        if (entity instanceof PlayerEntity) {
            if (entity == mc.player && !self.getValue())
                return false;
            if (Managers.SOCIAL.isFriend((Text) entity))
                return friends.getValue();
            return players.getValue();
        }

        if (entity instanceof EndCrystalEntity)
            return crystals.getValue();

        return switch (entity.getType().getSpawnGroup()) {
            case CREATURE, WATER_CREATURE -> creatures.getValue();
            case MONSTER -> monsters.getValue();
            case AMBIENT, WATER_AMBIENT -> ambients.getValue();
            default -> others.getValue();
        };
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        if (hands.getValue())
            Managers.SHADER.renderShader(()-> mc.gameRenderer.renderHand(event.getMatrices(), mc.gameRenderer.getCamera(), mc.getTickDelta()), handsMode.getValue());
    }

    @Override
    public void onDisable() {
        Managers.SHADER.reloadShaders();
    }
}
