package net.shoreline.client.impl.module.client;

import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.font.TTFFontRenderer;

/**
 * @author OvaqReborn
 * @since 1.0
 * 取り合図ベースを作成
 */
public class FontModule extends ToggleModule {

    private final NumberConfig fontSizeConfig = new NumberConfig<>("Font Size", "Font size in pixels", 7, 1, 50);

    public FontModule() {
        super("Font", "Changes the client text to custom font rendering", ModuleCategory.CLIENT);
    }

    private void updateFont() {
        int fontSize = (int) fontSizeConfig.getValue();
        RenderManager.tf = TTFFontRenderer.of("NotoSansJP-ExtraBold", fontSize);
    }

    public NumberConfig getFontSizeConfig() {
        return fontSizeConfig;
    }

    @Override
    public void onEnable() {
        updateFont();
    }

    @Override
    public void onDisable() {
       // RenderManager.disableFont();
    }
}
