package net.shoreline.client.impl.module.misc;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.init.Managers;

public class AutoEatModule extends ToggleModule {
    boolean a;
    private int prevSlot;

    Config<Boolean> hungerEnableConfig = new BooleanConfig("Hunger", "d or e", true);
    Config<Float> hungerConfig = new NumberConfig<>("HungerValue", "The minimum hunger level before eating", 1.0f, 19.0f, 20.0f, () -> hungerEnableConfig.getValue());
    Config<Boolean> healthEnableConfig = new BooleanConfig("Health", "d or e", true);
    Config<Float> healthConfig = new NumberConfig<>("HealthValue", "", 5.f,15.f,36.f, () -> healthEnableConfig.getValue());

    public AutoEatModule() {
        super("AutoEat", "", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        prevSlot = -1;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isDead()) {
            return;
        }
        if (healthEnableConfig.getValue()) {
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
        // Hunger
        if (hungerEnableConfig.getValue()) {
            if (!mc.player.isUsingItem()) {
                if (this.prevSlot != -1) {
                    Managers.INVENTORY.setClientSlot(this.prevSlot);
                    this.prevSlot = -1;
                }
                KeyBinding.setKeyPressed(mc.options.useKey.getDefaultKey(), false);
                return;
            }
            HungerManager hungerManager = mc.player.getHungerManager();
            if (hungerManager.getFoodLevel() <= this.hungerConfig.getValue()) {
                int slot = this.getFoodSlot();
                if (slot == -1) {
                    return;
                }
                if (slot == 45) {
                    mc.player.setCurrentHand(Hand.OFF_HAND);
                } else {
                    Managers.INVENTORY.setClientSlot(slot);
                }
                KeyBinding.setKeyPressed(mc.options.useKey.getDefaultKey(), true);
            }
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

    private int getFoodSlot() {
        int foodLevel = -1;
        int slot = -1;
        if (mc.player == null) return -1;

        DefaultedList<ItemStack> inventory = mc.player.getInventory().main;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.get(i);
            if (!stack.isFood() || stack.getItem() == Items.POISONOUS_POTATO || stack.getItem() == Items.ROTTEN_FLESH) continue;
            int hunger = stack.getItem().getFoodComponent().getHunger();
            if (hunger <= foodLevel) continue;
            slot = i;
            foodLevel = hunger;
        }

        ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.isFood()) {
            if (offhand.getItem() == Items.POISONOUS_POTATO || offhand.getItem() == Items.ROTTEN_FLESH) {
                return slot;
            }
            int hunger = offhand.getItem().getFoodComponent().getHunger();
            if (hunger > foodLevel) {
                slot = 45;
            }
        }
        return slot;
    }
}
