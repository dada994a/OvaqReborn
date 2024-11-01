package net.shoreline.client.impl.module.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.BlockPlacerModule;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.impl.event.network.DisconnectEvent;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.init.Managers;

import java.util.ArrayList;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class AutoWebModule extends BlockPlacerModule {
    Config<Float> rangeConfig = new NumberConfig<>("PlaceRange", "The range to fill nearby holes", 0.1f, 4.0f, 6.0f);
    Config<Float> enemyRangeConfig = new NumberConfig<>("EnemyRange", "The maximum range of targets", 0.1f, 10.0f, 15.0f);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotates to block before placing", false);
    Config<Boolean> coverHeadConfig = new BooleanConfig("CoverHead", "Places webs on the targets head", false);
    Config<Integer> shiftTicksConfig = new NumberConfig<>("ShiftTicks", "The number of blocks to place per tick", 1, 2, 5);
    Config<Integer> shiftDelayConfig = new NumberConfig<>("ShiftDelay", "The delay between each block placement interval", 0, 1, 5);
    private int shiftDelay;

    public AutoWebModule() {
        super("AutoWeb", "Automatically traps nearby entities in webs", ModuleCategory.COMBAT);
    }

    @EventListener
    public void onDisconnect(DisconnectEvent event) {
        this.disable();
    }

    @EventListener
    public void onPlayerTick(PlayerTickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isDead()) {
            return;
        }
        int blocksPlaced = 0;
        if (this.shiftDelay < this.shiftDelayConfig.getValue()) {
            ++this.shiftDelay;
            return;
        }
        ArrayList<BlockPos> webs = new ArrayList<>();
        for (PlayerEntity player : mc.world.getPlayers()) {
            double d;
            if (player == mc.player || Managers.SOCIAL.isFriend(player.getGameProfile().getName()) || (d = mc.player.distanceTo(player)) > enemyRangeConfig.getValue()) continue;
            //feet
            BlockPos feetPos = player.getBlockPos();
            double dist = mc.player.getPos().distanceTo(new Vec3d(feetPos.getX() + 0.5, feetPos.getY() + 0.5, feetPos.getZ() + 0.5));
            if (mc.world.getBlockState(feetPos).getBlock() == Blocks.AIR && dist <= ((NumberConfig<Float>) rangeConfig).getValueSq()) {
                webs.add(feetPos);
            }

            //head
            if (!coverHeadConfig.getValue()) continue;
            BlockPos headPos = feetPos.up();
            double dist2 = mc.player.getPos().distanceTo(new Vec3d(headPos.getX() + 0.5, headPos.getY() + 0.5, headPos.getZ() + 0.5));
            if (mc.world.getBlockState(headPos).getBlock() != Blocks.AIR || !(dist2 <= ((NumberConfig<Float>) rangeConfig).getValueSq())) continue;
            webs.add(headPos);
        }
        while (blocksPlaced < shiftTicksConfig.getValue() && blocksPlaced < webs.size()) {
            BlockPos targetPos = webs.get(blocksPlaced);
            ++blocksPlaced;
            shiftDelay = 0;
            placeWeb(targetPos);
        }
    }

    void placeWeb(BlockPos pos) {
        int slot = this.getBlockItemSlot(Blocks.COBWEB);
        if (slot == -1) {
            return;
        }
        Managers.INTERACT.placeBlock(pos, slot, strictDirectionConfig.getValue(), false, (state, angles) -> {
            if (this.rotateConfig.getValue()) {
                if (state) {
                    Managers.ROTATION.setRotationSilent(angles[0], angles[1], this.grimConfig.getValue());
                } else {
                    Managers.ROTATION.setRotationSilentSync(this.grimConfig.getValue());
                }
            }
        });
    }
}
