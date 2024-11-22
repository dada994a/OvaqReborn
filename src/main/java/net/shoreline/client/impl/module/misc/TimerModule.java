package net.shoreline.client.impl.module.misc;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.impl.event.render.TickCounterEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class TimerModule extends ToggleModule {
    //
    Config<Bypass> bypassConfig = new EnumConfig<>("Bypass", "", Bypass.Vanilla, Bypass.values());
    Config<Float> ticksConfig = new NumberConfig<>("Ticks", "The game tick speed", 0.1f, 2.0f, 50.0f);
    Config<Boolean> tpsSyncConfig = new BooleanConfig("TPSSync", "Syncs game tick speed to server tick speed", false);
    //
    private float prevTimer = -1.0f;
    private float timer = 1.0f;
    boolean bool1;
    List<Packet<?>> packets = new ArrayList<>();

    /**
     *
     */
    public TimerModule() {
        super("Timer", "Changes the client tick speed", ModuleCategory.MISC);
    }

    @Override
    public String getModuleData() {
        DecimalFormat decimal = new DecimalFormat("0.0#");
        return decimal.format(timer);
    }

    @Override
    public void onDisable() {
        if (this.bypassConfig.getValue() == Bypass.Shotbow) {
            this.h();
        }
    }

    @Override
    public void toggle() {
        Modules.SPEED.setPrevTimer();
        if (Modules.SPEED.isUsingTimer()) {
            return;
        }
        super.toggle();
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() == EventStage.PRE) {
            switch (bypassConfig.getValue()) {
                case Vanilla -> {
                    if (Modules.SPEED.isUsingTimer()) {
                        return;
                    }
                    if (tpsSyncConfig.getValue()) {
                        timer = Math.max(Managers.TICK.getTpsCurrent() / 20.0f, 0.1f);
                        return;
                    }
                    timer = ticksConfig.getValue();
                }
                case Shotbow -> {
                    if (mc.interactionManager.isBreakingBlock()) {
                        timer = 1.0f;
                        return;
                    }
                    timer = ticksConfig.getValue();
                    if (mc.player.isSprinting()) {
                        mc.player.setSprinting(true);
                        mc.options.sprintKey.setPressed(true);
                        mc.player.setSprinting(true);
                    }
                    else {
                        mc.player.setSprinting(false);
                        mc.options.sprintKey.setPressed(false);
                        mc.player.setSprinting(false);
                    }
                    if (mc.player.isSubmergedInWater()) {
                        return;
                    }
                    if (mc.player.isInLava()) {
                        mc.player.setVelocity(mc.player.getVelocity().multiply(0.6, 1.0, 0.6));
                    }
                    else {
                        float scala = 0.7f;
                        if (mc.player.getStatusEffects().stream().anyMatch(p -> p.getEffectType() == StatusEffects.SPEED)) {
                            int amp = mc.player.getStatusEffects().stream().filter(p -> p.getEffectType() == StatusEffects.SPEED).findAny().get().getAmplifier();
                            switch (amp) {
                                case 2: {
                                    scala = 0.5f;
                                    break;
                                }
                            }
                        }
                        mc.player.setVelocity(mc.player.getVelocity().multiply(scala, 1.0, scala));
                    }
                    this.h();
                }
            }
        }
    }

    private void h() {
        this.bool1 = true;
        int index = 0;
        for (Packet<?> p : this.packets) {
            if (p instanceof PlayerActionC2SPacket) {
                mc.getNetworkHandler().sendPacket(p);
            } else {
                ++index;
                double x = mc.player.getX();
                double y = mc.player.getY();
                double z = mc.player.getZ();
                if (index % 2 != 0) {
                    x = mc.player.prevX;
                    y = mc.player.prevY;
                    z = mc.player.prevZ;
                }

                if (p instanceof PlayerMoveC2SPacket.PositionAndOnGround o) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(o.getX(x), o.getY(y), o.getZ(z), o.isOnGround()));
                }
                else if (p instanceof PlayerMoveC2SPacket.LookAndOnGround o2) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(o2.getYaw(mc.player.getYaw()), o2.getPitch(mc.player.getPitch()), o2.isOnGround()));
                }
                else if (p instanceof PlayerMoveC2SPacket.Full o3) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(o3.getX(x), o3.getY(y), o3.getZ(z), o3.getYaw(mc.player.getYaw()), o3.getPitch(mc.player.getPitch()), o3.isOnGround()));
                }
                else {
                    mc.getNetworkHandler().sendPacket(p);
                }
            }
        }
        this.bool1 = false;
        this.packets.clear();
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (this.bypassConfig.getValue() == Bypass.Shotbow) {
            if (this.bool1) {
                return;
            }
            Packet<?> a = event.getPacket();
            if (a instanceof PlayerActionC2SPacket packet) {}
            else {
                Packet<?> a2 = event.getPacket();
                if (a2 instanceof PlayerMoveC2SPacket p) {
                    this.bool1 = false;
                }
                else if (event.getPacket() instanceof PlayerMoveC2SPacket) {}
            }
        }
    }

    @EventListener
    public void onTickCounter(TickCounterEvent event) {
        if (timer != 1.0f) {
            event.cancel();
            event.setTicks(timer);
        }
    }

    /**
     * @return
     */
    public float getTimer() {
        return timer;
    }

    /**
     * @param timer
     */
    public void setTimer(float timer) {
        prevTimer = this.timer;
        this.timer = timer;
    }

    public void resetTimer() {
        if (prevTimer > 0.0f) {
            this.timer = prevTimer;
            prevTimer = -1.0f;
        }
    }

    public enum Bypass {
        Vanilla,
        Shotbow
    }
}
