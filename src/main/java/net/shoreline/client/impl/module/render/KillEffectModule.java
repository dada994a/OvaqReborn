package net.shoreline.client.impl.module.render;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.event.EventDispatcher;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.entity.EntityDeathEvent;

public class KillEffectModule extends ToggleModule {

    public KillEffectModule() {
        super("KillEffect", "Spawns lightning when you kill an entity", ModuleCategory.RENDER);
        EventDispatcher.register(EntityDeathEvent.class, this::onEntityDeath);
    }

    private void onEntityDeath(EntityDeathEvent event) {
        LivingEntity killedEntity = event.getEntity();
        if (isPlayerKiller(killedEntity)) {
            spawnLightningEffect(mc.world, killedEntity.getPos());
        }
    }

    private boolean isPlayerKiller(LivingEntity killedEntity) {
        return mc.player != null && killedEntity.getRecentDamageSource() != null && killedEntity.getRecentDamageSource().getAttacker() == mc.player;
    }

    private void spawnLightningEffect(ClientWorld world, Vec3d pos) {
        if (world != null) {
            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.setPos(pos.x, pos.y, pos.z);
            lightning.setSilent(true);
            world.spawnEntity(lightning);

            EntitySpawnS2CPacket packet = new EntitySpawnS2CPacket(lightning);
            mc.getNetworkHandler().sendPacket(packet);
        }
    }
}
