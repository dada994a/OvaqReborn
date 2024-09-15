package net.shoreline.client.impl.module.client;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public class FontModule extends ToggleModule {
    private Font customFont;
    private boolean fontLoaded = false;

    public FontModule() {
        super("Font", "Changes the client text to custom font rendering", ModuleCategory.CLIENT);
        loadCustomFont();
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/assets/ovaqreborn/fonts/JetBrains Mono.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f); // カスタムフォントのサイズを24に設定
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            fontLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
