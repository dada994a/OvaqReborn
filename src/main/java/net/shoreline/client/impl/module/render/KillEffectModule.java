package net.shoreline.client.impl.module.render;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class KillEffectModule extends ToggleModule {
    Config<Mode> modeConfig = new EnumConfig<>("Mode", "", Mode.Thunder, Mode.values());
    Config<Boolean> thunderSoundConfig = new BooleanConfig("Thunder Sound", "", true, () -> modeConfig.getValue() == Mode.Thunder);
    Config<Float> rangeConfig = new NumberConfig<>("Range", "", 1.f, 10.f, 20.f);
    Config<Boolean> avoidSelfConfig = new BooleanConfig("Avoid Self", "", true);
    Config<Boolean> avoidFriendConfig = new BooleanConfig("Avoid Friend", "", true);

    public KillEffectModule() {
        super("KillEffect", "Spawns lightning when you kill an entity", ModuleCategory.RENDER);
    }

    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof EntityStatusS2CPacket a) {
            if (a.getStatus() == 3) {
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (player == mc.player && avoidSelfConfig.getValue() || Managers.SOCIAL.isFriend(player.getGameProfile().getName()) && avoidFriendConfig.getValue()) continue;
                    if (mc.player.distanceTo(player) > rangeConfig.getValue()) continue;
                    switch (modeConfig.getValue()) {
                        case Thunder -> {
                            LightningEntity le = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
                            le.updatePosition(player.getX(), player.getY(), player.getZ());
                            le.refreshPositionAfterTeleport(player.getX(), player.getY(), player.getZ());
                            mc.world.addEntity(le);
                            if (thunderSoundConfig.getValue()) {
                                mc.world.playSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER,1f, 1.f,false);
                            }
                        }
                    }
                }
            }
        }
    }

    public enum Mode {
        Thunder
    }
}
