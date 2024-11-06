package net.shoreline.client.impl.gui.click.impl.config;

import net.minecraft.client.gui.DrawContext;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.ConfigContainer;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.gui.click.component.Button;
import net.shoreline.client.impl.gui.click.impl.config.setting.ConfigButton;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ConfigList extends Button {
    private final ConfigContainer configContainer;
    private final List<ConfigButton<?>> configComponents = new CopyOnWriteArrayList<>();
    private boolean open;

    public ConfigList(ConfigContainer configContainer, CategoryFrame frame, float x, float y) {
        super(frame, x, y, 103.0f, 13.0f);
        this.configContainer = configContainer;

        for (Config<?> config : configContainer.getConfigs()) {
            if (!config.getName().equalsIgnoreCase("Enabled")) {
                configComponents.add(createConfigButton(config, x, y));
            }
        }
        open = false;
    }

    private ConfigButton<?> createConfigButton(Config<?> config, float x, float y) {
        if (config.getValue() instanceof Boolean) {
        }
        return null;
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        render(context, x, y, mouseX, mouseY, delta);
    }

    public void render(DrawContext context, float ix, float iy, float mouseX, float mouseY, float delta) {
        x = ix;
        y = iy;
        rectGradient(context, 0x555555, 0x555555);
        RenderManager.renderText(context, "Config List", ix + 2, iy + 3.5f, -1);

        if (open) {
            float off = iy + height + 1.0f;
            for (ConfigButton<?> configButton : configComponents) {
                if (configButton.getConfig().isVisible()) {
                    configButton.render(context, ix + 2.0f, off, mouseX, mouseY, delta);
                    off += configButton.getHeight();
                }
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isWithin(mouseX, mouseY)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                loadConfig();
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                open = !open;
            }
        }
    }

    private void loadConfig() {

        for (Config<?> config : configContainer.getConfigs()) {
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (open) {
            for (ConfigButton<?> component : configComponents) {
                component.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (open) {
            for (ConfigButton<?> component : configComponents) {
                component.mouseReleased(mouseX, mouseY, button);
            }
        }
    }
}
