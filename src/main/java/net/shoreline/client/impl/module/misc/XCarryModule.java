package net.shoreline.client.impl.module.misc;

import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PacketEvent;

/**
 * @author linus
 * @since 1.0
 */
public class XCarryModule extends ToggleModule {
    //
  //  Config<Boolean> inventoryConfig = new BooleanConfig("Inventory", "Prevents server from recieving packets regarding inventory items", true);
  //  Config<Boolean> armorConfig = new BooleanConfig("Armor", "Prevents server from recieving packets regarding armor items", false);
    Config<Boolean> forceCancelConfig = new BooleanConfig("ForceCancel", "Cancels all close window packets", false);

    /**
     *
     */
    public XCarryModule() {
        super("XCarry", "Allow player to carry items in the crafting slots",
                ModuleCategory.MISC);
    }

    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event) {
        if (mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof CloseHandledScreenC2SPacket packet
                && (packet.getSyncId() == mc.player.playerScreenHandler.syncId
                || forceCancelConfig.getValue())) {
            event.cancel();
        }
    }
}
