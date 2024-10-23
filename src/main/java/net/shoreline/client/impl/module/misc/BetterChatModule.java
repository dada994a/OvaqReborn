package net.shoreline.client.impl.module.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.util.render.animation.Easing;
import net.shoreline.client.util.render.animation.TimeAnimation;

import java.util.HashMap;
import java.util.Map;

public class BetterChatModule extends ToggleModule {
    Config<Boolean> animation = new BooleanConfig("Animation", "Animates the chat", false);
    Config<Integer> time = new NumberConfig<>("Time", "Time for the animation", 0, 200, 1000);
    Config<Boolean> transparentBackground = new BooleanConfig("TransparentBackground", "Makes the chat background fully transparent", true);

    public final Map<ChatHudLine, TimeAnimation> animationMap = new HashMap<>();

    public BetterChatModule() {
        super("BetterChat", "Modifications for the chat", ModuleCategory.MISC);
    }

    public Config<Boolean> getAnimationConfig() {
        return animation;
    }

    public Config<Integer> getTimeConfig() {
        return time;
    }

    public Config<Boolean> getTransparentBackgroundConfig() {
        return transparentBackground;
    }

    public Easing getEasingConfig() {
        return Easing.LINEAR;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (transparentBackground.getValue()) {
            ChatHud chatHud = mc.inGameHud.getChatHud();
            chatHud.addMessage(Text.of(Formatting.BLACK + "Chat background is now transparent"), null, null);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ChatHud chatHud = mc.inGameHud.getChatHud();
        chatHud.addMessage(Text.of("Chat background restored"), null, null);
    }
}
