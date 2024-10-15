package net.shoreline.client.api.module;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.MacroConfig;
import net.shoreline.client.api.config.setting.ToggleConfig;
import net.shoreline.client.api.macro.Macro;
import net.shoreline.client.api.Hideable;
import net.shoreline.client.util.render.animation.Animation;
import net.shoreline.client.util.render.animation.Easing;
import org.lwjgl.glfw.GLFW;
import net.shoreline.client.util.chat.ChatUtil;

/**
 * {@link Module} implementation with enabled state and keybinding. The
 * enabled state dictates when the module is running and subscribed to the
 * EventBus. The keybinding is used to {@link #enable()} and
 * {@link #disable()} the module.
 *
 * <p>The user cannot directly interact with the {@link #enabledConfig}. This
 * is the only config which cannot be interacted with through the configuration
 * menu in the ClickGui. Instead, the user can {@link #toggle()} the module
 * to change the enabled state.</p>
 *
 * @author linus
 * @see Macro
 * @see ToggleConfig
 * @since 1.0
 */
public class ToggleModule extends Module implements Hideable {
    private final Animation animation = new Animation(false, 300, Easing.CUBIC_IN_OUT);


    Config<Boolean> enabledConfig = new ToggleConfig("Enabled", "The module enabled state. This state is true when the module is running.", false);
    Config<Macro> keybindingConfig = new MacroConfig("Keybind", "The module keybinding. Pressing this key will toggle the module enabled state. Press [BACKSPACE] to delete the keybind.", new Macro(getId(), GLFW.GLFW_KEY_UNKNOWN, () -> toggle()));
    Config<Boolean> hiddenConfig = new BooleanConfig("Hidden", "The hidden state of the module in the Arraylist", false);
    Config<Boolean> togglemessageConfig = new BooleanConfig("ToggleMessage", "Send messages when the module is toggled", true);

    public ToggleModule(String name, String desc, ModuleCategory category) {
        super(name, desc, category);
        register( keybindingConfig, enabledConfig, hiddenConfig, togglemessageConfig);
    }

    public ToggleModule(String name, String desc, ModuleCategory category, Integer keycode) {
        this(name, desc, category);
        keybind(keycode);
    }

    @Override
    public boolean isHidden() {
        return hiddenConfig.getValue();
    }

    @Override
    public void setHidden(boolean hidden) {
        hiddenConfig.setValue(hidden);
    }

    public void toggle() {
        if (isEnabled()) {
            disable();
        } else {
            enable();
        }
    }

    public void enable() {
        enabledConfig.setValue(true);
        onEnable();
        if (togglemessageConfig.getValue()) {
            ChatUtil.clientSendMessage("§2[+]§f %s", getName());
        }
    }

    public void disable() {
        enabledConfig.setValue(false);
        onDisable();
        if (togglemessageConfig.getValue()) {
            ChatUtil.clientSendMessage("§c[-]§f %s", getName());
        }
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void keybind(int keycode) {
        keybindingConfig.setContainer(this);
        ((MacroConfig) keybindingConfig).setValue(keycode);
    }

    public boolean isEnabled() {
        return enabledConfig.getValue();
    }

    public Macro getKeybinding() {
        return keybindingConfig.getValue();
    }

    public Animation getAnimation() {
        return animation;
    }
}
