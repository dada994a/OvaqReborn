package net.shoreline.client.api.event;

import net.minecraft.entity.player.PlayerEntity;

/**
 * @author OvaqReborn
 * @since 1.0
 */

public class DeathEvent extends Event{
        private final PlayerEntity player;

        public DeathEvent(PlayerEntity player) {
            this.player = player;
        }
        public PlayerEntity getPlayer() {
            return this.player;
        }
    }
