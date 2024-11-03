package net.shoreline.client.impl.module.misc;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
/**
 * @author OvaqReborn
 * @since 1.0
 */
public class AutoGappleModule extends ToggleModule {
    boolean a;

    Config<Float> healthConfig = new NumberConfig<>("Health", "", 5.f,15.f,36.f);

    public AutoGappleModule() {
        super("AutoGapple", "", ModuleCategory.MISC);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isDead()) {
            return;
        }
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= healthConfig.getValue()) {
            if (!mc.player.isUsingItem()) {
                int gapple = getItemHotbar(Items.ENCHANTED_GOLDEN_APPLE);
                if (gapple != -1) {
                    a = true;
                    mc.player.getInventory().selectedSlot = gapple;
                    mc.options.useKey.setPressed(true);
                }
            }
        } else if (a) {
            mc.options.useKey.setPressed(false);
            a = false;
        }
    }

    private int getItemHotbar(Item item) {
        for (int i = 0; i < 9; ++i) {
            Item item2 = mc.player.getInventory().getStack(i).getItem();
            if (Item.getRawId(item2) != Item.getRawId(item)) {
                continue;
            }
            return i;
        }
        return -1;
    }
}