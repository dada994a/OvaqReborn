package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.entity.player.PushEntityEvent;
import net.shoreline.client.impl.event.network.DisconnectEvent;
import net.shoreline.client.util.world.FakePlayerEntity;

import java.util.UUID;

/**
 * @author linus
 * @see FakePlayerEntity
 * @since 1.0
 */
public class FakePlayerModule extends ToggleModule {
    private FakePlayerEntity fakePlayer;

    public FakePlayerModule() {
        super("FakePlayer", "Spawns an indestructible client-side player",
                ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        if (mc.player != null && mc.world != null) {
            fakePlayer = new FakePlayerEntity(mc.player, "FakePlayer");
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
            fakePlayer.spawnPlayer();
        }
    }

    @Override
    public void onDisable() {
        if (fakePlayer != null) {
            fakePlayer.despawnPlayer();
            fakePlayer = null;
        }
    }

    @EventListener
    public void onDisconnect(DisconnectEvent event) {
        fakePlayer = null;
        disable();
    }

    @EventListener
    public void onPushEntity(PushEntityEvent event) {
        if (event.getPushed().equals(mc.player) && event.getPusher().equals(fakePlayer)) {
            event.setCanceled(true);
        }
    }
}
