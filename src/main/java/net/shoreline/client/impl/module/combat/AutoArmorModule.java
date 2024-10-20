package net.shoreline.client.impl.module.combat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.NumberDisplay;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.player.EnchantmentUtil;

import java.util.PriorityQueue;
import java.util.Queue;


public class AutoArmorModule extends ToggleModule {
    Config<Priority> priorityConfig = new EnumConfig<>("Priority", "Armor enchantment priority", Priority.BLAST_PROTECTION, Priority.values());
    Config<Float> minDurabilityConfig = new NumberConfig<>("MinDurability", "Durability percent to replace armor", 0.0f, 0.0f, 20.0f, NumberDisplay.PERCENT);
    Config<Boolean> elytraPriorityConfig = new BooleanConfig("ElytraPriority", "Prioritizes existing elytras in the chestplate armor slot", true);
    Config<Boolean> blastLeggingsConfig = new BooleanConfig("Leggings-BlastPriority", "Prioritizes Blast Protection leggings", true);
    Config<Boolean> noBindingConfig = new BooleanConfig("NoBinding", "Avoids armor with the Curse of Binding enchantment", true);
    Config<Boolean> inventoryConfig = new BooleanConfig("AllowInventory", "Allows armor to be swapped while in the inventory menu", false);

    private final Queue<ArmorSlot> helmet = new PriorityQueue<>();
    private final Queue<ArmorSlot> chestplate = new PriorityQueue<>();
    private final Queue<ArmorSlot> leggings = new PriorityQueue<>();
    private final Queue<ArmorSlot> boots = new PriorityQueue<>();

    public AutoArmorModule() {
        super("AutoArmor", "Automatically replaces armor pieces", ModuleCategory.COMBAT);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() != EventStage.PRE) {
            return;
        }
        if (mc.currentScreen != null && !(mc.currentScreen instanceof InventoryScreen && inventoryConfig.getValue())) {
            return;
        }

        helmet.clear();
        chestplate.clear();
        leggings.clear();
        boots.clear();

        for (int j = 9; j < 45; j++) {
            ItemStack stack = mc.player.getInventory().getStack(j);
            if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armor)) {
                continue;
            }
            if (noBindingConfig.getValue() && EnchantmentHelper.hasBindingCurse(stack)) {
                continue;
            }
            int index = armor.getSlotType().getEntitySlotId();
            float dura = (stack.getMaxDamage() - stack.getDamage()) / (float) stack.getMaxDamage();
            if (dura < minDurabilityConfig.getValue()) {
                continue;
            }
            ArmorSlot data = new ArmorSlot(index, j, stack);
            if (index == EquipmentSlot.HEAD.getEntitySlotId()) {
                helmet.add(data);
            } else if (index == EquipmentSlot.CHEST.getEntitySlotId()) {
                chestplate.add(data);
            } else if (index == EquipmentSlot.LEGS.getEntitySlotId()) {
                leggings.add(data);
            } else if (index == EquipmentSlot.FEET.getEntitySlotId()) {
                boots.add(data);
            }
        }

        for (int i = 0; i < 4; i++) {
            ItemStack armorStack = mc.player.getInventory().getArmorStack(i);
            if (elytraPriorityConfig.getValue() && armorStack.getItem() == Items.ELYTRA) {
                continue;
            }
            float armorDura = (armorStack.getMaxDamage() - armorStack.getDamage()) / (float) armorStack.getMaxDamage();
            if (!armorStack.isEmpty() && armorDura >= minDurabilityConfig.getValue()) {
                continue;
            }
            if (i == EquipmentSlot.HEAD.getEntitySlotId() && !helmet.isEmpty()) {
                ArmorSlot helmetSlot = helmet.poll();
                swapArmor(helmetSlot.getType(), helmetSlot.getSlot());
            } else if (i == EquipmentSlot.CHEST.getEntitySlotId() && !chestplate.isEmpty()) {
                ArmorSlot chestSlot = chestplate.poll();
                swapArmor(chestSlot.getType(), chestSlot.getSlot());
            } else if (i == EquipmentSlot.LEGS.getEntitySlotId() && !leggings.isEmpty()) {
                ArmorSlot leggingsSlot = leggings.poll();
                swapArmor(leggingsSlot.getType(), leggingsSlot.getSlot());
            } else if (i == EquipmentSlot.FEET.getEntitySlotId() && !boots.isEmpty()) {
                ArmorSlot bootsSlot = boots.poll();
                swapArmor(bootsSlot.getType(), bootsSlot.getSlot());
            }
        }
    }

    public void swapArmor(int armorSlot, int slot) {
        ItemStack stack = mc.player.getInventory().getArmorStack(armorSlot);
        Managers.INVENTORY.pickupSlot(slot);
        boolean rt = !stack.isEmpty();
        Managers.INVENTORY.pickupSlot(8 + armorSlot); // Adjusted to correct armor slot index
        if (rt) {
            Managers.INVENTORY.pickupSlot(slot);
        }
    }

    public float getPriority(int i, ItemStack armorStack) {
        return 1.0f;
    }

    public enum Priority {
        BLAST_PROTECTION(Enchantments.BLAST_PROTECTION),
        PROTECTION(Enchantments.PROTECTION),
        PROJECTILE_PROTECTION(Enchantments.PROJECTILE_PROTECTION);

        private final Enchantment enchant;

        Priority(Enchantment enchant) {
            this.enchant = enchant;
        }

        public Enchantment getEnchantment() {
            return enchant;
        }
    }

    public class ArmorSlot implements Comparable<ArmorSlot> {
        private final int armorType;
        private final int slot;
        private final ItemStack armorStack;

        public ArmorSlot(int armorType, int slot, ItemStack armorStack) {
            this.armorType = armorType;
            this.slot = slot;
            this.armorStack = armorStack;
        }

        @Override
        public int compareTo(ArmorSlot other) {
            if (armorType != other.armorType) {
                return 0;
            }
            final ItemStack otherStack = other.getArmorStack();
            ArmorItem armorItem = (ArmorItem) armorStack.getItem();
            ArmorItem otherItem = (ArmorItem) otherStack.getItem();
            int durabilityDiff = armorItem.getMaterial().getProtection(armorItem.getType()) - otherItem.getMaterial().getProtection(otherItem.getType());
            if (durabilityDiff != 0) {
                return durabilityDiff;
            }
            Enchantment enchantment = priorityConfig.getValue().getEnchantment();
            if (blastLeggingsConfig.getValue() && armorType == EquipmentSlot.LEGS.getEntitySlotId() && hasEnchantment(Enchantments.BLAST_PROTECTION)) {
                return -1;
            }
            if (hasEnchantment(enchantment)) {
                return other.hasEnchantment(enchantment) ? 0 : -1;
            } else {
                return other.hasEnchantment(enchantment) ? 1 : 0;
            }
        }

        public boolean hasEnchantment(Enchantment enchantment) {
            Object2IntMap<Enchantment> enchants = EnchantmentUtil.getEnchantments(armorStack);
            return enchants.containsKey(enchantment);
        }

        public ItemStack getArmorStack() {
            return armorStack;
        }

        public int getType() {
            return armorType;
        }

        public int getSlot() {
            return slot;
        }
    }
}
