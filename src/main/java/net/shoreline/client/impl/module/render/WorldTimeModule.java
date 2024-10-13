package net.shoreline.client.impl.module.render;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;

/**
 * @author linus
 * @since 1.0
 */
public class WorldTimeModule extends ToggleModule {

    Config<Integer> worldTimeConfig = new NumberConfig<>("WorldTime", "The world time of day", 0, 0, 24000);
    private long prevTime;

    public WorldTimeModule() {
        super("WorldTime", "Changes the world time", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        prevTime = mc.world.getTimeOfDay();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.world.setTimeOfDay(prevTime);
        super.onDisable();
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() == EventStage.POST) {
            mc.world.setTimeOfDay(worldTimeConfig.getValue());
        }
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            event.cancel();
        }
    }
}
