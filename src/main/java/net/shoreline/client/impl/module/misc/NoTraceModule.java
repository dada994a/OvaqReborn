package net.shoreline.client.impl.module.misc;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

public class NoTraceModule extends ToggleModule {

    Config<Boolean> onlyPickaxeConfig = new BooleanConfig("OnlyPickaxe","sonomama", true);
    public NoTraceModule() {
        super("NoTrace","test", ModuleCategory.MISC);
    }

    public boolean onUpdate() {
        if (onlyPickaxeConfig.getValue()) {
            return mc.player.getMainHandStack().getItem() instanceof PickaxeItem || mc.player.isUsingItem() && !(mc.player.getMainHandStack().getItem() instanceof SwordItem);
        }
        return true;
    }
}
