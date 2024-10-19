package net.shoreline.client.impl.module.client;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

/**
 * @author OvaqReborn
 */
public class ProtocolModule extends ToggleModule {
    Config<Boolean> crouchFixConfig = new BooleanConfig("CrouchFix", "title", true);
    Config<Boolean> sneakFixConfig = new BooleanConfig("SneakFix", "title", true);

    public ProtocolModule() {
        super("Protocol", "Tweaks like 1.12.2", ModuleCategory.CLIENT);
    }

    public boolean getCrouchFix() {
        return crouchFixConfig.getValue();
    }

    public boolean getSneakFix() {
        return sneakFixConfig.getValue();
    }
}
