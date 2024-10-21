package net.shoreline.client.impl.module.combat;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.shoreline.client.OvaqReborn;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ObsidianPlacerModule;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.math.timer.CacheTimer;
import net.shoreline.client.util.math.timer.Timer;

/**
 * @author OvaqReborn
 */
public class AutoObsidianModule extends ObsidianPlacerModule {
    PlayerEntity target = null;

    Timer timer = new CacheTimer();

    Config<Integer> rangeConfig = new NumberConfig<>("Range", "number", 1, 5, 7);
    Config<Integer> minYConfig =
            new NumberConfig<>("MinY", "minimum Y Offset", 1, 3, 5);
    Config<Integer> delayConfig = new NumberConfig<>("Delay", "delay", 1, 1, 5);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "yaw", false);

    public AutoObsidianModule() {
        super("AutoObsidian", "Place Pos Base", ModuleCategory.COMBAT);
    }

    @Override
    public void onEnable() {
        timer.reset();
    }

    @EventListener
    public void onPlayerTick(PlayerTickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isDead()) {
            return;
        }
        try {
            target = getTargetPlayer();

            if (target != null) {
                int obsidian = getItemHotbar(Items.OBSIDIAN);

                if (obsidian != -1) {
                    if (timer.passed(delayConfig.getValue())) {
                        for (BlockPos pos :
                                this.posArrayList(target, rangeConfig.getValue())) {
                            if (pos != null) {
                                Managers.INTERACT.placeBlock(pos, obsidian, strictDirectionConfig.getValue(), false, (state, angles) ->
                                {
                                    if (rotateConfig.getValue()) {
                                        if (state) {
                                            Managers.ROTATION.setRotationSilent(angles[0], angles[1], grimConfig.getValue());
                                        } else {
                                            Managers.ROTATION.setRotationSilentSync(grimConfig.getValue());
                                        }
                                    }
                                });
                            }
                        }
                    }
                    timer.reset();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private int getItemHotbar(Item item) {
        for (int i = 0; i < 9; ++i) {
            Item item2 = mc.player.getInventory().getStack(i).getItem();
            if (Item.getRawId(item2) != Item.getRawId(item)) {
                continue;
            }
            return i;
        }
        return -1;
    }

    protected int height(BlockPos start) {
        int playerHeight;
        for (playerHeight = 0; isAir(start.down(playerHeight)); ++playerHeight) {
        }
        return start.getY() - (start.getY() - playerHeight);
    }

    protected ArrayList<BlockPos> posArrayList(
            PlayerEntity target, int maxRange) {
        Vec3i[] offsets = {
            new Vec3i(1, 0, 0), // east
            new Vec3i(-1, 0, 0), // west
            new Vec3i(0, 0, 1), // south
            new Vec3i(0, 0, -1) // north
    };
        Vec3i[] offsetsdig = {
                new Vec3i(1, 0, 1), // southeast
                new Vec3i(-1, 0, -1), // northwest
                new Vec3i(-1, 0, 1), // southwest
                new Vec3i(1, 0, -1) // northeast
        };
        BlockPos targetPos = new BlockPos(
                target.getBlockX(), Math.round(target.getBlockY()), target.getBlockZ());
        ArrayList<BlockPos> positions = new ArrayList<>();
        boolean hasPlaced = false;
        if (!target.isOnGround()) {
            if (this.height(targetPos) > minYConfig.getValue()) {
                BlockPos downPos = targetPos.down(2);
                OvaqReborn.LOGGER.info(this.height(targetPos));
                BlockPos nearestBlock =
                        this.findNearestBlockInRange(downPos, maxRange);
                if (nearestBlock != null && !hasPlaced) {
                    this.createVerticalAndHorizontalPath(
                            downPos, nearestBlock, positions);
                    if (isAir(downPos)) {
                        positions.add(downPos);
                    }
                    if (isAir(downPos.north())) {
                        positions.add(downPos.north());
                    }
                    OvaqReborn.LOGGER.info(downPos);
                    hasPlaced = true;
                    return positions;
                }
            }
        } else if (!isAir(targetPos.down()) && !hasPlaced) {
            ArrayList<BlockPos> posArray = new ArrayList<>();
            Vec3i[] array = offsets;
            int length = array.length;
            int i = 0;
            while (i < length) {
                Vec3i pos = array[i];
                BlockPos newPos = targetPos.down().add(pos);
                posArray.add(newPos.up());
                if (!isAir(newPos) && isAir(newPos.up())) {
                    hasPlaced = true;
                    Block targetPosBlock = state(targetPos).getBlock();
                    if ((targetPosBlock == Blocks.REDSTONE_TORCH
                            || targetPosBlock == Blocks.WITHER_SKELETON_SKULL)
                            && isAir(targetPos.down().add(
                            new Vec3i(pos.getX() * 2, pos.getY(), pos.getZ() * 2)))
                            && isAir(targetPos.down()
                            .add(new Vec3i(
                                    pos.getX() * 2, pos.getY(), pos.getZ() * 2))
                            .up())) {
                        positions.add(targetPos.down().add(
                                new Vec3i(pos.getX() * 2, pos.getY(), pos.getZ() * 2)));
                        break;
                    }
                    break;
                } else {
                    BlockPos doubled = targetPos.down().add(
                            new Vec3i(pos.getX() * 2, pos.getY(), pos.getZ() * 2));
                    if (this.validBlock(doubled) && this.validAir(newPos)) {
                        for (Vec3i checkpos : offsets) {
                            if (this.validBlock(doubled.add(checkpos))) {
                                hasPlaced = true;
                                break;
                            }
                        }
                    }
                    ++i;
                }
            }
            if (!hasPlaced) {
                for (Vec3i pos : offsetsdig) {
                    BlockPos newPos = targetPos.down().add(pos);
                    if (this.validBlock(newPos)) {
                        for (Vec3i pos2 : offsets) {
                            BlockPos newPos2 = newPos.add(pos2);
                            if (this.validBlock(newPos2) && !hasPlaced
                                    && this.surroundedCheck(targetPos)) {
                                hasPlaced = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (!hasPlaced) {
                int currentScore = 0;
                BlockPos bestPos = null;
                boolean add = false;
                boolean add_two = false;
                int bestScore = 0;
                for (Vec3i pos3 : offsets) {
                    BlockPos newPos3 = targetPos.down().add(pos3);
                    if (this.validAir(newPos3)) {
                        ++currentScore;
                        if (!isAir(newPos3.add(pos3))) {
                            ++currentScore;
                            if (isAir(newPos3.add(pos3).up())) {
                                ++currentScore;
                            }
                        }
                        BlockPos xed = targetPos.down().add(
                                new Vec3i(pos3.getX() * 2, pos3.getY(), pos3.getZ() * 2));
                        Block targetPosBlock2 = state(targetPos).getBlock();
                        if ((targetPosBlock2 == Blocks.REDSTONE_TORCH
                                || targetPosBlock2 == Blocks.WITHER_SKELETON_SKULL)
                                && isAir(xed.up())) {
                            positions.add(newPos3);
                            positions.add(xed);
                            return positions;
                        }
                    }
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestPos = newPos3;
                        currentScore = 0;
                        if (add) {
                            add_two = true;
                            add = false;
                        } else {
                            add_two = false;
                        }
                    }
                }
                if (bestPos != null) {
                    positions.add(bestPos);
                    if (add_two) {
                        for (Vec3i pos2 : offsets) {
                            if (targetPos.add(pos2) == bestPos) {
                                positions.add(targetPos.down().add(
                                        new Vec3i(pos2.getX() * 2, pos2.getY(), pos2.getZ() * 2)));
                            }
                        }
                    }
                }
            }
        }
        if (positions.isEmpty() && !hasPlaced) {
            boolean continuee = true;
            if (isAir(targetPos.down())) {
                for (Vec3i pos : offsets) {
                    BlockPos newPos = targetPos.down().add(pos);
                    if (!isAir(newPos)) {
                        for (Vec3i pos2 : offsets) {
                            if (!isAir(newPos.add(pos2)) && isAir(newPos.add(pos2).up())) {
                                continuee = false;
                            }
                        }
                        if (continuee) {
                            if (this.canAndShouldPlace(targetPos.down())) {
                                positions.add(targetPos.down());
                                break;
                            }
                            break;
                        }
                    }
                }
                if (positions.isEmpty()) {
                    for (Vec3i pos : offsetsdig) {
                        BlockPos newPos = targetPos.down().add(pos);
                        if (!isAir(newPos)) {
                            for (Vec3i pos2 : offsets) {
                                if (!isAir(newPos.add(pos2)) && isAir(newPos.add(pos2).up())) {
                                    continuee = false;
                                }
                            }
                            if (continuee) {
                                positions.add(targetPos.down());
                                if (pos.getX() > 0 && isAir(newPos.add(new Vec3i(-1, 0, 0)))) {
                                    positions.add(newPos.add(new Vec3i(-1, 0, 0)));
                                }
                                if (pos.getX() < 0 && isAir(newPos.add(new Vec3i(1, 0, 0)))) {
                                    positions.add(newPos.add(new Vec3i(1, 0, 0)));
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (!positions.isEmpty()) {
            OvaqReborn.LOGGER.info(positions);
            return positions;
        }
        return positions;
    }

    private boolean surroundedCheck(BlockPos targetPos) {
        Vec3i[] safePositions = {new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0),
                new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)};
        int air = 0;
        for (Vec3i vec3i : safePositions) {
            BlockPos pos = targetPos.add(vec3i);
            BlockState blockState = state(pos);
            if (Block.getRawIdFromState(blockState) == 49
                    || Block.getRawIdFromState(blockState) == 7) {
                ++air;
            }
        }
        return air == 0;
    }

    private void createVerticalAndHorizontalPath(
            BlockPos startPos, BlockPos targetPos, ArrayList<BlockPos> positions) {
        int yDifference = targetPos.getY() - startPos.getY();
        BlockPos currentPos = startPos;
        for (int y = 0; y < Math.abs(yDifference); ++y) {
            currentPos = currentPos.up((yDifference > 0) ? 1 : -1);
            if (isAir(currentPos)) {
                positions.add(currentPos);
            }
        }
        int xDifference = targetPos.getX() - currentPos.getX();
        int zDifference = targetPos.getZ() - currentPos.getZ();
        for (int x = 0; x < Math.abs(xDifference); ++x) {
            currentPos = currentPos.add((xDifference > 0) ? 1 : -1, 0, 0);
            if (isAir(currentPos)) {
                positions.add(currentPos);
            }
        }
        for (int z = 0; z < Math.abs(zDifference); ++z) {
            currentPos = currentPos.add(0, 0, (zDifference > 0) ? 1 : -1);
            if (isAir(currentPos)) {
                positions.add(currentPos);
            }
        }
    }

    private BlockPos findNearestBlockInRange(
            BlockPos downPos, int maxRange) {
        BlockPos nearestBlock = null;
        double nearestDistance = Double.MAX_VALUE;
        for (int x = -maxRange; x <= maxRange; ++x) {
            for (int y = -maxRange; y <= maxRange; ++y) {
                for (int z = -maxRange; z <= maxRange; ++z) {
                    BlockPos currentPos = downPos.add(x, y, z);
                    if (!isAir(currentPos)) {
                        double distance = downPos.getSquaredDistance(currentPos);
                        if (distance <= maxRange * maxRange && distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestBlock = currentPos;
                        }
                    }
                }
            }
        }
        return nearestBlock;
    }

    private BlockState state(BlockPos p) {
        return mc.world.getBlockState(p);
    }

    private boolean isAir(BlockPos p) {
        return state(p).isAir();
    }

    private boolean canAndShouldPlace(BlockPos p) {
        return isAir(p) && isAir(p.up());
    }

    private boolean validAir(BlockPos p) {
        return isAir(p) && isAir(p.up());
    }

    private boolean validBlock(BlockPos p) {
        return !isAir(p) && isAir(p.up());
    }

    // taken from shoreline
    private PlayerEntity getTargetPlayer() {
        List<Entity> entities = Lists.newArrayList(mc.world.getEntities());
        return (PlayerEntity) entities.stream()
                .filter((entity)
                        -> entity instanceof PlayerEntity && entity.isAlive()
                        && !mc.player.equals(entity))
                .filter((entity)
                        -> mc.player.squaredDistanceTo(entity)
                        <= ((NumberConfig<Integer>) rangeConfig).getValueSq())
                .min(Comparator.comparingDouble(
                        (entity) -> mc.player.squaredDistanceTo(entity)))
                .orElse(null);
    }
}