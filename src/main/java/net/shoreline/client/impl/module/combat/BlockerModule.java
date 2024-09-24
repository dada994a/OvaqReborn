package net.shoreline.client.impl.module.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ObsidianPlacerModule;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.network.AddEntityEvent;
import net.shoreline.client.util.player.PlayerUtil;
import net.shoreline.client.init.Managers;

import java.util.ArrayList;
import java.util.List;

public class BlockerModule extends ObsidianPlacerModule {

    private Config<Integer> shiftTicksConfig = new NumberConfig<>("ShiftTicks", "The number of blocks to place per tick", 1, 1, 5);
    private Config<Integer> shiftDelayConfig = new NumberConfig<>("ShiftDelay", "The delay between each block placement interval", 0, 0, 100);
    private int shiftDelay;

    public BlockerModule() {
        super("Blocker", "Places obsidian when crystals are placed", ModuleCategory.COMBAT, 900);
    }

    @EventListener
    public void onAddEntity(AddEntityEvent event) {
        if (event.getEntity() instanceof EndCrystalEntity crystalEntity) {
            BlockPos crystalPos = crystalEntity.getBlockPos();
            BlockPos blockToPlace = crystalPos.down(); // 黒曜石を置く位置

            // プレイヤーがその位置にブロックを置けるか確認
            if (canPlaceBlock(blockToPlace)) {
                placeObsidian(blockToPlace);
            }
        }
    }

    private boolean canPlaceBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).isReplaceable();
    }

    private void placeObsidian(BlockPos pos) {
        int obsidianSlot = getObsidianSlot(); // 黒曜石が入っているスロットを取得
        if (obsidianSlot != -1) {
            Managers.INTERACT.placeBlock(pos, obsidianSlot, false, false, (state, angles) -> {
                // 必要に応じて回転処理を追加
            });
        }
    }

    private int getObsidianSlot() {
        // インベントリから黒曜石のスロットを探すロジック
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.OBSIDIAN) {
                return i;
            }
        }
        return -1; // 見つからない場合
    }
}
