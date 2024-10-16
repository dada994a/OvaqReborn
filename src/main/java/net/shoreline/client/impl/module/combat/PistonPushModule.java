package net.shoreline.client.impl.module.combat;

import com.google.common.collect.Lists;
import net.minecraft.block.*;
        import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.RotationModule;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.chat.ChatUtil;
import net.shoreline.client.util.player.InventoryUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PistonPushModule extends RotationModule {
    private List<Block> airBlocks = Arrays.asList(
            Blocks.AIR,
            Blocks.FIRE,
            Blocks.LAVA,
            Blocks.WATER
    );

    int timer = 0;
    PlayerEntity target = null;

    Config<Float> rangeConfig = new NumberConfig<>("Range", "number", 1.0f, 5.0f, 7.0f);
    Config<Integer> tickConfig = new NumberConfig<>("Tick", "eee", 0, 1, 5);

    public PistonPushModule() {
        super("PistonPush", "test", ModuleCategory.COMBAT);
    }

    @EventListener
    public void onPlayerTick(final PlayerTickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isDead()) {
            return;
        }
        try {
            int piston = getItemHotbar(Items.PISTON);
            int red = getItemHotbar(Items.REDSTONE_BLOCK);

            if (piston == -1) {
                piston = getItemHotbar(Items.STICKY_PISTON);
            }

            if (piston == -1 || red == -1) {
                ChatUtil.error("No Materials.");
                disable();
                return;
            }
            target = getTargetPlayer();

            if (target == null) {
                ChatUtil.error("No targets.");
                disable();
            } else {
                if (hole(true, target)) {
                    disable();
                    return;
                }
                BlockPos pos = new BlockPos(target.getBlockX(), Math.round(target.getBlockY()), target.getBlockZ());
                if (target.getEyeHeight(target.getPose()) < 1.4) {
                    disable();
                    return;
                }
                if (!state(pos.up(2)).isReplaceable()) {
                    disable();
                    return;
                }
                ArrayList<Piston> pushable = new ArrayList<>();
                for (int i = 0; i < 4; ++i) {
                    Piston p = new Piston(pos.up(), Direction.fromHorizontal(i));
                    if (p.isPistonAble()) {
                        pushable.add(p);
                    }
                }
                pushable.sort(Comparator.comparingDouble(this::sort));
                Piston a = pushable.get(0);
                pushable.clear();
                placePiston(a);
                placeRedstone(a.activatorPos);
            }
        } catch (Exception e) {
        }
    }

    private void placeRedstone(BlockPos activatorPos) {
        Place(activatorPos, Items.REDSTONE_BLOCK, 3);
    }

    private boolean placePiston(Piston a) {
        if (a.pistonEd) {
            return false;
        }
        float y = a.d.getOpposite().asRotation();
        y = (float) MathHelper.clamp(a.getDirectionAngle(), y - 40.0f, y + 40.0f);
        setRotation(y, 0.0f);
        ++this.timer;
        if (this.timer < tickConfig.getValue()) {
            return true;
        }
        this.timer = 0;
        Place(a.piston, Items.PISTON, 2);
        return true;
    }

    private void Place(BlockPos pos, Item e, int retrys) {
        if (retrys < 1) {
            return;
        }
        int freqs2 = 0;
        while (airBlocks.contains(mc.world.getBlockState(pos).getBlock())) {
            if (freqs2 >= retrys) {
                break;
            }
            silentPlace(pos, e);
            freqs2++;
        }
    }

    private double sort(Piston pres) {
        return distanceFromEye(pres.pos) * 1;
    }

    private void silentPlace(BlockPos placepos, Item block) {
        boolean NO = InventoryUtil.hasItemInInventory(block, true);
        if (!NO) {
            return;
        }
        Managers.INTERACT.placeBlock(placepos, getItemHotbar(block), false, false, null);
    }

    private boolean hole(boolean doubles, Entity entity) {
        BlockPos blockPos = entity.getBlockPos();
        int air = 0;
        for (Direction direction : Direction.values()) {
            BlockState state;
            if (direction == Direction.UP || (state = state(blockPos.offset(direction))).getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.CRYING_OBSIDIAN) continue;
            if (!doubles || direction == Direction.DOWN) {
                return true;
            }
            ++air;
            for (Direction dir : Direction.values()) {
                BlockState blockState1;
                if (dir == direction.getOpposite() || dir == Direction.UP || (blockState1 = state(blockPos.offset(direction).offset(dir))).getBlock() == Blocks.BEDROCK || blockState1.getBlock() == Blocks.OBSIDIAN) continue;
                return true;
            }
        }
        return air >= 2;
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

    private BlockState state(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    private boolean canPlace(BlockPos blockPos, boolean checkEntities) {
        if (blockPos == null) {
            return false;
        }
        if (blockPos.getY() > 319 || blockPos.getY() < -64) {
            return false;
        }
        if (!mc.world.getBlockState(blockPos).isReplaceable()) {
            return false;
        }
        return !checkEntities || mc.world.canPlace(Blocks.STONE.getDefaultState(), blockPos, ShapeContext.absent());
    }

    private static double distanceFromEye(double x, double y, double z) {
        return Math.sqrt(mc.player.getX() - x * mc.player.getX() - x + mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()) * mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()) - y + mc.player.getZ() - z * mc.player.getZ() - z);
    }

    private static double distanceFromEye(BlockPos pos) {
        return distanceFromEye(pos.getX(), pos.getY(), pos.getZ());
    }

    public class Piston {
        BlockPos pos;
        Direction d;
        BlockPos activatorPos = null;
        boolean activated = false;
        boolean pistonEd = false;
        BlockPos piston = null;

        public Piston(BlockPos target, Direction dir) {
            this.pos = target;
            this.d = dir;
        }

        public boolean isPistonAble() {
            BlockPos check = this.pos.offset(this.d);
            this.piston = this.pos.offset(this.d.getOpposite());
            if (distanceFromEye(this.piston) > rangeConfig.getValue()) {
                return false;
            }
            if (state(this.piston).getBlock() instanceof PistonBlock) {
                this.pistonEd = true;
            } else if (!canPlace(this.piston, true)) {
                return false;
            }
            this.getActivators();
            if (!this.activated && (this.activatorPos == null || distanceFromEye(this.activatorPos) > rangeConfig.getValue())) {
                return false;
            }
            if (!state(check).isReplaceable()) {
                return false;
            }
            if (!state(check.up()).isReplaceable()) {
                return false;
            }
            return true;
        }

        private void getActivators() {
            ArrayList<BlockPos> a = new ArrayList<>();
            for (Direction dir0 : Direction.values()) {
                if (dir0 == this.d) continue;
                BlockPos c = this.piston.offset(dir0);
                if (state(c).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
                    this.activated = true;
                    return;
                }
                if (canPlace(c, true)) {
                    a.add(c);
                }
            }
            if (a.isEmpty()) {
                this.activatorPos = null;
            } else {
                a.sort(Comparator.comparingDouble(PistonPushModule::distanceFromEye));
                this.activatorPos = a.get(0);
            }
        }

        public double getDirectionAngle() {
            return switch (this.d) {
                case DOWN -> 90;
                case UP -> -90;
                case NORTH -> 180.0;
                case SOUTH -> 0.0;
                case WEST -> 90.0;
                case EAST -> -90.0;
            };
        }
    }

    //taken from shoreline
    private PlayerEntity getTargetPlayer() {
        final List<Entity> entities = Lists.newArrayList(mc.world.getEntities());
        return (PlayerEntity) entities.stream()
                .filter((entity) -> entity instanceof PlayerEntity && entity.isAlive() && !mc.player.equals(entity))
                .filter((entity) -> mc.player.squaredDistanceTo(entity) <= ((NumberConfig<Float>) rangeConfig).getValueSq())
                .min(Comparator.comparingDouble((entity) -> mc.player.squaredDistanceTo(entity)))
                .orElse(null);
    }
}