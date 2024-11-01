package net.shoreline.client.api.module;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;

import java.util.function.Predicate;

/**
 * api
 * @see net.shoreline.client.impl.module.combat.AutoWebModule
 */
public class BlockPlacerModule extends RotationModule {
    protected Config<Boolean> strictDirectionConfig = new BooleanConfig("StrictDirection", "Places on visible sides only", false);
    protected Config<Boolean> grimConfig = new BooleanConfig("Grim", "Places using grim instant rotations", false);

    public BlockPlacerModule(String name, String desc, ModuleCategory category) {
        super(name, desc, category);
        this.register(this.strictDirectionConfig, this.grimConfig);
    }

    public BlockPlacerModule(String name, String desc, ModuleCategory category, int rotationPriority) {
        super(name, desc, category, rotationPriority);
        this.register(this.strictDirectionConfig, this.grimConfig);
    }

    protected int getSlot(Predicate<ItemStack> filter) {
        if (mc.player == null) return -1;

        DefaultedList<ItemStack> inventory = mc.player.getInventory().main;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.get(i);
            if (itemStack.isEmpty() || !filter.test(itemStack)) continue;
            return i;
        }
        return -1;
    }

    protected int getBlockItemSlot(Block block) {
        if (mc.player == null) return -1;

        DefaultedList<ItemStack> inventory = mc.player.getInventory().main;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.get(i);
            if (stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() == block) {
                    return i;
                }
            }
        }
        return -1;
    }
}
