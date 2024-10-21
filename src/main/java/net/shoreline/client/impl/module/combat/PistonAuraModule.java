package net.shoreline.client.impl.module.combat;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.entity.EntityTrackingSection;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.world.entity.SimpleEntityLookup;
import net.shoreline.client.OvaqReborn;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.RotationModule;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.mixin.accessor.AccessorSectionedEntityCache;
import net.shoreline.client.mixin.accessor.AccessorSimpleEntityLookup;
import net.shoreline.client.mixin.accessor.AccessorWorld;
import net.shoreline.client.util.chat.ChatUtil;
import net.shoreline.client.util.world.EndCrystalUtil;
import net.shoreline.client.util.world.SneakBlocks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * @author OvaqReborn
 * Beta
 */
public class PistonAuraModule extends RotationModule {
    PlayerEntity target = null;
    private static Entity cEntity;
    private static PosDirPair leverPPos;
    private static int leverItem;
    private static BlockPos interferePos;
    private static BlockPos supportPos;
    private static BlockPos cSupportPos;
    private static BlockPos activePos;
    private static BlockPos leverPos;
    private static BlockPos breakPos;
    private static BlockPos cPos;
    private static Piston pistonPos;

    Config<Float> rangeConfig = new NumberConfig<>("Range", "number", 1.0f, 5.0f, 7.0f);
    Config<Double> radiusConfig = new NumberConfig<>("Radius", "", 4.4, 0.0, 6.0);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "", true);
    Config<Boolean> breakCrystalsConfig = new BooleanConfig("BreakCrystal", "", true);
    Config<Boolean> ignoreTerrainConfig = new BooleanConfig("IgnoreTerrain", "", true);
    Config<Boolean> airCalcConfig = new BooleanConfig("Aircalc", "", true);
    Config<Boolean> leverConfig = new BooleanConfig("LeverTest", "use lever to activate piston.", false);
    Config<Boolean> pushCrystalConfig = new BooleanConfig("PushCrystal", "push blocks", true);
    Config<Boolean> dnpConfig = new BooleanConfig("DNP", "dev", true);
    Config<Boolean> mineConfig = new BooleanConfig("Mine", "test", true);
    Config<Boolean> antiSurroundConfig = new BooleanConfig("AntiSurround", "test", true);
    Config<Boolean> facePlaceConfig = new BooleanConfig("FacePlace", "test", true);
    Config<Boolean> multiConfig = new BooleanConfig("Multi", "test", false);
    Config<Boolean> supportConfig = new BooleanConfig("Support", "a", true);
    Config<Boolean> csupportConfig = new BooleanConfig("CrystalSupport", "b", true);
    Config<Boolean> pauseEatConfig = new BooleanConfig("Pause Eat", "jp", true);
    Config<Float> minHealthConfig = new NumberConfig<>("MinHealth", "minimum hp", 0.0f, 6.0f, 36.0f);
    Config<Float> minDamageConfig = new NumberConfig<>("MinDamage", "Minimum Damage", 0.0f, 6.0f, 36.0f);
    Config<Boolean> zeroTickDelay = new BooleanConfig("WithoutDelay", "", true);
    Config<Integer> pistonDelayConfig = new NumberConfig<>("Piston Delay", "eee", 0, 1, 5);
    Config<Integer> supportDelayConfig = new NumberConfig<>("Support Delay", "eee", 0, 1, 5);
    Config<Integer> crystalDelayConfig = new NumberConfig<>("Crystal Delay", "eee", 0, 1, 5);
    Config<Integer> breakDelayConfig = new NumberConfig<>("Break Delay", "eee", 0, 1, 5);
    Config<Integer> cBreakDelayConfig = new NumberConfig<>("CrystalBreak Delay", "eee", 0, 1, 5);
    Config<Integer> activateDelayConfig = new NumberConfig<>("Activate Delay", "eee", 0, 1, 5);
    Config<Integer> deactivateDelayConfig = new NumberConfig<>("Deactivate Delay", "eee", 0, 1, 5);

    static Action action = Action.None;

    static int timer = 0;
    Runnable task; //todo: later

    public PistonAuraModule() {
        super("PistonAura", "beta", ModuleCategory.COMBAT);
    }

    @EventListener
    public void onPlayerTick(PlayerTickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isDead()) {
            return;
        }
        try {
            target = getTargetPlayer();
            if (target == null) {
                ChatUtil.error("No Target");
                disable();
                return;
            } else {
                int crystal = getItemHotbar(Items.END_CRYSTAL);
                int piston = getItemHotbar(Items.PISTON);
                int redstone = getItemHotbar(Items.REDSTONE_BLOCK);
                if (crystal == -1 || piston == -1 || redstone == -1) {
                    ChatUtil.error("No Materials");
                    disable();
                    return;
                }
                if (mc.player.getHealth() < minHealthConfig.getValue()) {
                    return;
                }
                if (pauseEatConfig.getValue() && isEating()) {
                    return;
                }
                if (Managers.SOCIAL.isFriend(target.getName().getString())) {
                    target = null;
                    return;
                }
                ++timer;
                int supportItem = getItemHotbar2(itemStack -> itemStack.getItem() instanceof BlockItem &&
                        itemStack.getItem() != Items.REDSTONE_BLOCK &&
                        itemStack.getItem() != Items.PISTON &&
                        itemStack.getItem() != Items.OBSIDIAN);
                int obsidian = getItemHotbar(Items.OBSIDIAN);
                this.calculateStage(redstone, supportItem, obsidian);
                if (action != Action.BreakInterfering) {
                    interferePos = null;
                } else {
                    OvaqReborn.LOGGER.info("Breaking interfering block at position: {}", interferePos.toString(), new Object[0]);
                    mine(interferePos);
                }
                if (action == Action.BreakActivator) {
                    if (timer > deactivateDelayConfig.getValue()) {
                        mine(breakPos);
                    }
                    return;
                }
                breakPos = null;
                if (timer <= getDelay()) {
                    if (action == Action.Rotate) {
                        test();
                    }
                    return;
                }
                timer = 0;

                if (action != Action.BreakingCrystal) {
                    cEntity = null;
                } else {
                    if (!cEntity.isAlive()) {
                        return;
                    }
                    if (rotateConfig.getValue()) {
                        setRotation((float) getYaw(cEntity), mc.player.getPitch());
                    }
                    mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(cEntity, mc.player.isSneaking()));
                }
                if (action != Action.PlaceCrystal) {
                    cPos = null;
                } else {
                    placeCrystal(crystal, cPos, true);
                }
                if (action != Action.PlaceRedstone) {
                    activePos = null;
                } else {
                    Managers.INTERACT.placeBlock(activePos, redstone, false, false, null);
                }
                if (action != Action.ToggleLever) {
                    leverPos = null;
                } else {
                    this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofBottomCenter(leverPos), Direction.UP, leverPos, false));
                }
                if (action != Action.PlaceLever) {
                    leverPPos = null;
                } else {
                    float pitch = leverPPos.dir().equals(Direction.DOWN) ? 90.0f : 0.0f;
                    //float f = leverPPos.dir().equals(Direction.UP) ? -90.0f : pitch;
                    if (rotateConfig.getValue()) {
                        setRotation(leverPPos.dir().asRotation(), pitch);
                    }
                    Managers.INTERACT.placeBlock(leverPPos.pos(), leverItem, false, false, null);
                }
                if (action != Action.Support) {
                    supportPos = null;
                } else {
                    Managers.INTERACT.placeBlock(supportPos, supportItem, false, false, null);
                }
                if (action != Action.SupportCrystal) {
                    cSupportPos = null;
                } else {
                    Managers.INTERACT.placeBlock(cSupportPos, obsidian, false, false, null);
                }
                if (action == Action.Rotate) {
                    test();
                } else if (action == Action.PlacePiston) {
                    Managers.INTERACT.placeBlock(pistonPos.getPos(), piston, false, false, null);
                } else {
                    pistonPos = null;
                }
            }
        } catch (Exception e) {
        }
    }

    private void mine(BlockPos p) {
        //todo make this work
        if (isAir(p)) {
            return;
        }
        int pick = getItemHotbar(Items.DIAMOND_PICKAXE);
        if (pick == -1) {
            pick = getItemHotbar(Items.NETHERITE_PICKAXE);
        }
        if (pick == -1) {
            return;
        }
        Managers.INVENTORY.setSlot(pick);
        Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, p, Direction.UP, id));
        Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, p, Direction.UP, id));
        Managers.INVENTORY.syncToClient();
    }

    private void test() {
        if (rotateConfig.getValue()) {
            OvaqReborn.LOGGER.info("Rotating to direction: {}", pistonPos.getRotateDir().toString(), new Object[0]);
            setRotation(pistonPos.getRotateDir().asRotation(), 0.0f);
        }
    }

    private void placeCrystal(int item, BlockPos pos, boolean swing) {
        Hand hand = Hand.MAIN_HAND;
        if (item == -1) {
            return;
        }
        Managers.INVENTORY.setSlot(item);
        mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(Vec3d.of(pos), Direction.UP, pos.down(), false), 0));
        if (swing) {
            mc.player.swingHand(hand);
        }
        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
        Managers.INVENTORY.syncToClient();
    }

    private int getDelay() {
        if (this.zeroTickDelay.getValue()) {
            return 0;
        }
        switch (action) {
            case PlacePiston: {
                return 0;
            }
            case Rotate: {
                return pistonDelayConfig.getValue();
            }
            case Support:
            case SupportCrystal: {
                return supportDelayConfig.getValue();
            }
            case PlaceCrystal: {
                return crystalDelayConfig.getValue();
            }
            case PlaceRedstone:
            case PlaceLever: {
                return activateDelayConfig.getValue();
            }
            case ToggleLever: {
                return deactivateDelayConfig.getValue();
            }
            case BreakingCrystal: {
                return cBreakDelayConfig.getValue();
            }
            case BreakInterfering: {
                return breakDelayConfig.getValue();
            }
        }
        return 0;
    }

    private double getYaw(Entity entity) {
        return mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(entity.getZ() - mc.player.getZ(), entity.getX() - mc.player.getX())) - 90f - mc.player.getYaw());
    }
    private void calculateStage(int red, int sup, int obi) {
        BlockPos pPos;
        if (action == Action.Rotate && timer >= pistonDelayConfig.getValue()) {
            action = Action.PlacePiston;
            return;
        }
        action = Action.None;
        Entity entity = target;
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            pPos = new BlockPos(player.getBlockX(), Math.round(player.getBlockY()), player.getBlockZ());
            for (Entity entity2 : mc.world.getEntities()) {
                if (!(entity2 instanceof EndCrystalEntity) || !(EndCrystalUtil.getDamageTo(player, entity2.getPos(), ignoreTerrainConfig.getValue()) >= minDamageConfig.getValue()) || wtf(entity2.getBlockPos(), target) && !(facePlaceConfig.getValue())) continue;
                action = Action.BreakingCrystal;
                cEntity = entity2;
                return;
            }
            if (facePlaceConfig.getValue()) {
                for (int i = 0; i <= 4; ++i) {
                    for (int j = 0; j < 4; ++j) {
                        BlockPos test = pPos.add(Direction.fromHorizontal(j).getVector()).up(i);
                        if (!(EndCrystalUtil.getDamageTo(player, Vec3d.ofBottomCenter(test), ignoreTerrainConfig.getValue()) >= minDamageConfig.getValue()) || !canPlace(test, radiusConfig.getValue(), true)) continue;
                        action = Action.PlaceCrystal;
                        cPos = test;
                        return;
                    }
                }
            }
        } else {
            pPos = target.getBlockPos();
        }
        for (int i = 1; i <= 5; ++i) {
            if (isBlastResist(pPos.up(i - 1))) {
                return;
            }
            List<Piston> pistons = getPresumablePistons(pPos.up(i), true, true, true, true, true);
            for (Piston piston : pistons) {
                Entity e;
                BlockPos lever = piston.getLeverPos();
                if (dnpConfig.getValue()) {
                    BlockPos act;
                    if (isPiston(piston.getPos()) && (act = piston.getRedstoneBlock()) != null) {
                        action = Action.BreakActivator;
                        breakPos = act;
                        return;
                    }
                    if (lever != null && piston.isActivated()) {
                        action = Action.ToggleLever;
                        leverPos = lever;
                        return;
                    }
                }
                if (piston.shouldSupportCrystal(pPos) && csupportConfig.getValue()) {
                    BlockPos a;
                    if (airCalcConfig.getValue()) {
                        action = Action.SupportCrystal;
                        cSupportPos = piston.getCrystalPos().down();
                        return;
                    }
                    if (target.isOnGround() && (a = getRecursiveSupportBlock(piston.getCrystalPos(), radiusConfig.getValue())) != null) {
                        if (a.equals(piston.getCrystalPos().down()) && obi != -1) {
                            action = Action.SupportCrystal;
                            cSupportPos = a;
                            return;
                        }
                        if (sup != -1) {
                            action = Action.Support;
                            supportPos = a;
                            return;
                        }
                        if (obi != -1) {
                            action = Action.SupportCrystal;
                            cSupportPos = a;
                            return;
                        }
                    }
                }
                if (piston.isReadyCrystal(radiusConfig.getValue()) && !piston.isActivated()) {
                    if (leverConfig.getValue()) {
                        if (lever != null) {
                            action = Action.ToggleLever;
                            leverPos = lever;
                            return;
                        }
                        if (piston.shouldUseLever() || red == -1 && leverItem != -1) {
                            List<PosDirPair> pos;
                            if (!(pos = piston.getLeverPlacePair()).isEmpty() && !(distanceFromEye(pos.get(0).pos()) > radiusConfig.getValue())) {
                                action = Action.PlaceLever;
                                leverPPos = pos.get(0);
                                return;
                            }
                        }
                    }
                    action = Action.PlaceRedstone;
                    activePos = piston.getRedstonePos(radiusConfig.getValue()).get(0);
                    return;
                }
                if (piston.isReadyPiston(radiusConfig.getValue(), pushCrystalConfig.getValue())) {
                    if (piston.getRedstoneBlock() != null) continue;
                    action = Action.PlaceCrystal;
                    cPos = piston.getCrystalPos();
                    return;
                }
                if (!multiConfig.getValue() && piston.isReady(radiusConfig.getValue())) {
                    return;
                }
                if (piston.isPlaceable(radiusConfig.getValue(), pushCrystalConfig.getValue(), antiSurroundConfig.getValue(), leverConfig.getValue())) {
                    if (piston.isSupported() || !piston.isSupportable || !supportConfig.getValue()) {
                        action = Action.Rotate;
                        pistonPos = piston;
                        return;
                    }
                    if (sup == -1 || (supportPos = getRecursiveSupportBlock(piston.getPos().down(), radiusConfig.getValue())) == null) continue;
                    action = Action.Support;
                    return;
                }
                if (!breakCrystalsConfig.getValue() || (e = piston.getInterferingCrystal(radiusConfig.getValue())) == null) continue;
                action = Action.BreakingCrystal;
                cEntity = e;
            }
            OvaqReborn.LOGGER.warn("Couldn't find a piston pos on level {}", i, new Object[0]);
            if (i != 3) continue;
            if (mineConfig.getValue()) {
                for (int j = 1; j <= 3; ++j) {
                    BlockPos interfere = getPresumablePistons(pPos.up(j), true, true, true, true, true).get(0).getInterferingBlock(radiusConfig.getValue());
                    if (interfere == null) continue;
                    if (interferePos == null || state(interferePos).isReplaceable()) {
                        interferePos = interfere;
                    }
                    action = Action.BreakInterfering;
                    break;
                }
            }
            return;
        }
    }

    private boolean isEating() {
        if (mc.world == null || mc.player == null || mc.player.age <= 20) {
            return false;
        }
        RaycastContext context = new RaycastContext(mc.player.getEyePos(), mc.player.getEyePos().add(mc.player.getRotationVector().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player);
        BlockHitResult result = mc.world.raycast(context);
        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            if (SneakBlocks.isSneakBlock(mc.world.getBlockState(pos).getBlock()) && !mc.player.isSneaking()) {
                return false;
            }
        }
        ItemStack mainHandStack = mc.player.getMainHandStack();
        ItemStack offHandStack = mc.player.getOffHandStack();
        return mc.player.isUsingItem() && (mainHandStack.isFood() || offHandStack.isFood());
    }

    private boolean wtf(BlockPos pos, Entity pe) {
        BlockPos pePos = new BlockPos(mc.player.getBlockX(), Math.round(mc.player.getBlockY()), mc.player.getBlockZ()).up(2);
        if (pe != null) {
            pePos = new BlockPos(pe.getBlockX(), Math.round(pe.getBlockY()), pe.getBlockZ());
        }
        return pos.equals(pePos.south()) || pos.equals(pePos.west()) || pos.equals(pePos.east()) || pos.equals(pePos.north());
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

    private boolean canPlace(BlockPos crystalPos, double distance, boolean checkEntities) {
        if (distanceFromEye(crystalPos) > distance) {
            return false;
        }
        if (iwe(Box.from(Vec3d.of(crystalPos)), entity -> !entity.isSpectator()) && checkEntities) {
            return false;
        }
        Block block = getBlock(crystalPos.down());
        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            return isAir(crystalPos);
        }
        return false;
    }

    private boolean dos(BlockPos crystalPos, BlockPos pos) {
        return Box.from(Vec3d.ofBottomCenter(crystalPos).add(0.0, 0.5, 0.0)).expand(0.5).intersects(Box.from(Vec3d.of(pos)));
    }

    private boolean dos(Entity entity, BlockPos pos) {
        return entity instanceof EndCrystalEntity && entity.getBoundingBox().intersects(Box.from(Vec3d.of(pos)));
    }

    public boolean isPiston(BlockPos pos) {
        Block block = getBlock(pos);
        return block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.MOVING_PISTON;
    }

    private boolean isPistonHead(BlockPos pos) {
        Block a = getBlock(pos);
        return a == Blocks.PISTON_HEAD || a == Blocks.MOVING_PISTON;
    }

    private boolean isGoodPiston(BlockPos pos, Direction dir) {
        if (!isPiston(pos)) {
            return false;
        }
        for (Direction d : Direction.values()) {
            if (!isPistonHead(pos.offset(d))) continue;
            return false;
        }
        return state(pos.offset(dir.getOpposite())).isReplaceable();
    }

    private BlockPos getRecursiveSupportBlock(BlockPos pos, double maxDistance) {
        if (distanceFromEye(pos) > maxDistance) {
            return null;
        }
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP || isAir(pos.offset(dir))) continue;
            return pos;
        }
        return canPlace(pos, true) ? getRecursiveSupportBlock(pos.down(), maxDistance) : null;
    }

    private BlockState state(BlockPos p) {
        return mc.world.getBlockState(p);
    }

    private Block getBlock(BlockPos pos) {
        return state(pos).getBlock();
    }

    private boolean isAir(BlockPos p) {
        return state(p).isAir();
    }

    private int getItemHotbar2(Predicate<ItemStack> predicate) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (predicate.test(itemStack)) {
                return i;
            }
        }
        return -1;
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

    private static double distanceFromEye(double x, double y, double z) {
        double f = mc.player.getX() - x;
        double g = mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()) - y;
        double h = mc.player.getZ() - z;
        return Math.sqrt(f * f + g * g + h * h);
    }

    private static double distanceFromEye(BlockPos pos) {
        return distanceFromEye(pos.getX(), pos.getY(), pos.getZ());
    }

    private static double distanceFromEye(Entity entity) {
        double feet = distanceFromEye(entity.getX(), entity.getY(), entity.getZ());
        double head = distanceFromEye(entity.getX(), entity.getY() + (double)entity.getHeight(), entity.getZ());
        return Math.min(head, feet);
    }

    private static double distanceFromEye(PosDirPair pos) {
        return distanceFromEye(pos.pos());
    }

    private List<Piston> getPresumablePistons(BlockPos checkPos, boolean farawayPos, boolean sidePos, boolean upperPos, boolean straight, boolean upPos) {
        ArrayList<Piston> a = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            Direction dire = Direction.fromHorizontal(i);
            if (upPos) {
                a.add(new Piston(checkPos, dire));
            }
            if (straight) {
                a.add(new Piston(checkPos, dire, 2));
            }
            if (farawayPos) {
                a.add(new Piston(checkPos, dire, 3));
                if (upperPos) {
                    a.add(new Piston(checkPos, dire, 3, Direction.UP));
                }
            }
            if (sidePos) {
                a.add(new Piston(checkPos, dire, 2, Direction.fromHorizontal((int)(i + 1))));
                a.add(new Piston(checkPos, dire, 2, Direction.fromHorizontal((int)(i == 0 ? 3 : i - 1))));
                if (upperPos) {
                    a.add(new Piston(checkPos, dire, 2, Direction.fromHorizontal((int)(i + 1)), true));
                    a.add(new Piston(checkPos, dire, 2, Direction.fromHorizontal((int)(i == 0 ? 3 : i - 1)), true));
                }
                if (farawayPos) {
                    if (upperPos) {
                        a.add(new Piston(checkPos, dire, 3, Direction.fromHorizontal((int)(i + 1)), true));
                        a.add(new Piston(checkPos, dire, 3, Direction.fromHorizontal((int)(i == 0 ? 3 : i - 1)), true));
                    }
                    a.add(new Piston(checkPos, dire, 3, Direction.fromHorizontal((int)(i + 1))));
                    a.add(new Piston(checkPos, dire, 3, Direction.fromHorizontal((int)(i == 0 ? 3 : i - 1))));
                }
            }
            if (!upperPos) continue;
            a.add(new Piston(checkPos, dire, 2, Direction.UP));
        }
        a.sort(Comparator.comparingDouble(this::distanceToPiston));
        return a;
    }

    private boolean isLever(BlockPos pos) {
        return getBlock(pos).equals(Blocks.LEVER);
    }

    private boolean isFullBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).isFullCube(mc.world, pos);
    }

    private double distanceToPiston(Piston pistonSequence) {
        return distanceFromEye(pistonSequence.getPos());
    }

    private boolean isCrystalOnPos(BlockPos pos) {
        //ここ下手に変えたら死ぬ
        Box test = new Box(pos.getX() - 0.5, pos.getY() - 0.5, pos.getZ() - 0.5,
                pos.getX() + 1.5, pos.getY() + 1.5, pos.getZ() + 1.5);

        List<EndCrystalEntity> entities = mc.world.getEntitiesByClass(EndCrystalEntity.class, test, entity -> true);

        return !entities.isEmpty();
    }

    private float getBlastResistance(BlockPos block) {
        return mc.world.getBlockState(block).getBlock().getBlastResistance();
    }

    private boolean isBlastResist(BlockPos block) {
        return getBlastResistance(block) >= 600.0f;
    }

    private float getHardness(BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).getHardness(mc.world, blockPos);
    }

    private boolean isBreakable(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        return getHardness(pos) > 0.0f && !mc.world.getBlockState(pos).isAir();
    }

    private boolean iwe(Box box, Predicate<Entity> predicate) {
        EntityLookup<Entity> entityLookup = ((AccessorWorld) mc.world).getEntityLookup();
        if (entityLookup instanceof SimpleEntityLookup<Entity> simpleEntityLookup) {
            SectionedEntityCache<Entity> cache = ((AccessorSimpleEntityLookup) simpleEntityLookup).getCache();
            LongSortedSet trackedPositions = ((AccessorSectionedEntityCache) cache).getTrackedPositions();
            Long2ObjectMap<EntityTrackingSection<Entity>> trackingSections = ((AccessorSectionedEntityCache) cache).getTrackingSections();
            for (int x = ChunkSectionPos.getSectionCoord(box.minX - 2); x <= ChunkSectionPos.getSectionCoord(box.maxX + 2); x++) {
                trackedPositions.subSet(ChunkSectionPos.asLong(x, 0, 0), ChunkSectionPos.asLong(x, -1, -1) + 1).forEach(pos -> {
                    if (ChunkSectionPos.unpackY(pos) >= ChunkSectionPos.getSectionCoord(box.minY - 2) && ChunkSectionPos.unpackZ(pos) <= ChunkSectionPos.getSectionCoord(box.maxY + 2) && ChunkSectionPos.unpackZ(pos) >= ChunkSectionPos.getSectionCoord(box.minZ - 2) && ChunkSectionPos.unpackZ(pos) <= ChunkSectionPos.getSectionCoord(box.maxZ + 2)) {
                        EntityTrackingSection<Entity> section = trackingSections.get(pos);
                        if (section != null && section.getStatus().shouldTrack()) {
                            section.stream().filter(entity -> entity.getBoundingBox().intersects(box) && predicate.test(entity)).findFirst().ifPresent(entity -> { throw new RuntimeException(); });
                        }
                    }
                });
            }
            return false;
        }
        AtomicBoolean found = new AtomicBoolean(false);
        entityLookup.forEachIntersects(box, entity -> { if (!found.get() && predicate.test(entity)) found.set(true); });
        return found.get();
    }

    public class Piston {
        private final BlockPos pos;
        private final BlockPos crystalPos;
        private final Direction dir;
        private final Direction adir;
        public final boolean isSupportable;

        public Piston(BlockPos center, Direction placeRotateDir, int offset, Direction side) {
            this.pos = center.offset(placeRotateDir, offset).offset(side);
            this.crystalPos = center.offset(placeRotateDir);
            this.dir = placeRotateDir;
            this.adir = placeRotateDir.getOpposite();
            this.isSupportable = true;
        }

        public Piston(BlockPos center, Direction placeRotateDir, int offset, Direction side, boolean up) {
            this.pos = up ? center.offset(placeRotateDir, offset).offset(side).up() : center.offset(placeRotateDir, offset).offset(side);
            this.crystalPos = center.offset(placeRotateDir);
            this.dir = placeRotateDir;
            this.adir = placeRotateDir.getOpposite();
            this.isSupportable = true;
        }

        public Piston(BlockPos center, Direction placeRotateDir, int offset) {
            this.pos = center.offset(placeRotateDir, offset);
            this.crystalPos = center.offset(placeRotateDir);
            this.dir = placeRotateDir;
            this.adir = placeRotateDir.getOpposite();
            this.isSupportable = true;
        }

        public Piston(BlockPos center, Direction placeRotateDir) {
            this.pos = center.offset(placeRotateDir).up();
            this.crystalPos = center.offset(placeRotateDir);
            this.dir = placeRotateDir;
            this.adir = placeRotateDir.getOpposite();
            this.isSupportable = false;
        }

        public Direction getRotateDir() {
            return this.dir;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public BlockPos getCrystalPos() {
            return this.crystalPos;
        }

        public List<BlockPos> getRedstonePos(double distance) {
            ArrayList<BlockPos> a = new ArrayList<>();
            if (this.isNull()) {
                return a;
            }
            for (BlockPos pos : this.getPlaceRedstonePos(distance)) {
                if (!canPlace(pos, true)) continue;
                a.add(pos);
            }
            return a;
        }

        public List<BlockPos> getPlaceRedstonePos(double distance) {
            ArrayList<BlockPos> a = new ArrayList<>();
            if (this.isNull()) {
                return a;
            }
            for (Direction d : Direction.values()) {
                BlockPos p;
                if (d.equals(this.adir) || distanceFromEye(p = this.pos.offset(d)) > distance || dos(this.crystalPos, p)) continue;
                a.add(p);
            }
            a.sort(Comparator.comparingDouble(PistonAuraModule::distanceFromEye));
            return a;
        }

        private boolean isNull() {
            if (this.pos == null || this.crystalPos == null || this.pos.equals(this.crystalPos)) {
                return true;
            }
            return !dos(this.crystalPos.offset(this.dir), this.pos);
        }

        public boolean shouldUseLever() {
            return isBlastResist(this.crystalPos.offset(this.adir)) && !this.getLeverPlacePair().isEmpty();
        }

        public boolean isActivated() {
            return isPistonHead(this.pos.offset(this.adir)) && isPiston(this.pos);
        }

        public BlockPos getRedstoneBlock() {
            for (Direction d : Direction.values()) {
                BlockPos p = this.pos.offset(d);
                if (!getBlock(p).equals(Blocks.REDSTONE_BLOCK)) continue;
                if (state(this.pos.offset(this.adir)).isReplaceable() && isPiston(this.pos)) {
                    return this.pos;
                }
                return p;
            }
            return null;
        }

        public List<PosDirPair> getLeverPlacePair() {
            ArrayList<PosDirPair> ac = new ArrayList<>();
            if (state(this.pos.down()).isReplaceable()) {
                for (int i = 0; i < 4; ++i) {
                    Direction a = Direction.fromHorizontal(i);
                    if (!isFullBlock(this.pos.down().offset(a))) continue;
                    ac.add(new PosDirPair(this.pos.down(), a));
                }
            }
            if (state(this.pos.offset(this.dir)).isReplaceable()) {
                ac.add(new PosDirPair(this.pos.offset(this.dir), this.dir));
            }
            if (isFullBlock(this.pos.down())) {
                Direction[] directionArray = Direction.values();
                int n = directionArray.length;
                for (int i = 0; i < n; ++i) {
                    Direction dire = directionArray[i];
                    if (!state(this.pos.down().offset(dire)).isReplaceable()) continue;
                    ac.add(new PosDirPair(this.pos.down().offset(dire), dire.getOpposite()));
                }
            }
            if (isFullBlock(this.pos.offset(this.dir))) {
                for (Direction dire : Direction.values()) {
                    if (dire == this.adir || !state(this.pos.offset(this.dir).offset(dire)).isReplaceable()) continue;
                    ac.add(new PosDirPair(this.pos.offset(this.dir).offset(dire), dire.getOpposite()));
                }
            }
            ac.sort(Comparator.comparingDouble(PistonAuraModule::distanceFromEye));
            return ac;
        }

        public BlockPos getLeverPos() {
            if (isLever(this.pos.down())) {
                return this.pos.down();
            }
            if (isLever(this.pos.offset(this.dir))) {
                return this.pos.offset(this.dir);
            }
            for (Direction dire : Direction.values()) {
                if (dire == this.adir) continue;
                if (isLever(this.pos.down().offset(dire))) {
                    return this.pos.down().offset(dire);
                }
                if (!isLever(this.pos.offset(this.dir).offset(dire))) continue;
                return this.pos.offset(this.dir).offset(dire);
            }
            return null;
        }

        public boolean isPlaceable(double distance, boolean pushSelf, boolean surBreak, boolean useLever) {
            if (this.isNull()) {
                return false;
            }
            BlockPos targetPos = new BlockPos(
                    mc.player.getBlockX(), Math.round(mc.player.getBlockY()), mc.player.getBlockZ());
            if (!pushSelf && this.pos.offset(this.adir).equals(targetPos.up())) {
                return false;
            }
            if (!surBreak) {
                for (int i = 0; i < 4; ++i) {
                    if (!dos(this.crystalPos, targetPos.offset(Direction.fromHorizontal(i)))) continue;
                    return false;
                }
            }
            if (!state(this.pos.offset(this.adir)).isReplaceable()) {
                return false;
            }
            if (!canPlace(this.crystalPos, distance, true)) {
                return false;
            }
            if (!(!this.getRedstonePos(distance).isEmpty() || useLever && this.shouldUseLever())) {
                return false;
            }
            return canPlace(this.pos, true);
        }

        public boolean isReadyPiston(double distance, boolean pushSelf) {
            if (this.isNull()) {
                return false;
            }
            BlockPos targetPos = new BlockPos(
                    mc.player.getBlockX(), Math.round(mc.player.getBlockY()), mc.player.getBlockZ());
            if (!pushSelf && this.pos.offset(this.adir).equals(targetPos.up())) {
                return false;
            }
            if (!canPlace(this.crystalPos, distance, true)) {
                return false;
            }
            if (this.getRedstonePos(distance).isEmpty()) {
                return false;
            }
            return isGoodPiston(this.pos, this.dir);
        }

        public boolean isReady(double distance) {
            if (this.isNull() || distanceFromEye(this.pos) > distance) {
                return false;
            }
            if (this.isReadyPiston(distance, true)) {
                return false;
            }
            return !isGoodPiston(this.pos, this.dir) && isPiston(this.pos) && state(this.pos.offset(this.dir)).isReplaceable();
        }

        public boolean isReadyCrystal(double distance) {
            if (this.isNull()) {
                return false;
            }
            if (!isCrystalOnPos(this.crystalPos)) {
                return false;
            }
            if (this.getRedstonePos(distance).isEmpty()) {
                return false;
            }
            return(isGoodPiston(this.pos, this.dir));
        }

        public boolean isSupported() {
            return !isAir(this.pos.down());
        }

        public boolean shouldSupportCrystal(BlockPos target) {
            if (!isAir(this.crystalPos.down())) {
                return false;
            }
            if (!isAir(this.crystalPos)) {
                return false;
            }
            int blast = 0;
            for (int i = 0; i < 4; ++i) {
                if (!isBlastResist(target.offset(Direction.fromHorizontal(i)))) continue;
                ++blast;
            }
            return blast < 3;
        }

        public Entity getInterferingCrystal(double distance) {
            if (!state(this.pos).isReplaceable()) {
                return null;
            }
            for (Entity entity : mc.world.getEntities()) {
                if (!dos(entity, this.pos) || !(distanceFromEye(entity) <= distance) || entity.getBlockPos() == this.crystalPos) continue;
                return entity;
            }
            return null;
        }

        public BlockPos getInterferingBlock(double distance) {
            BlockPos pon = null;
            if (!this.isNull() || this.isReadyCrystal(distance) || this.isReadyCrystal(distance) || this.isPlaceable(distance, false, false, false)) {
                if (isBreakable(this.crystalPos) && distanceFromEye(this.crystalPos) <= distance && (!(Modules.SURROUND.isEnabled() || !isSurroundBlock(this.crystalPos)))) {
                    pon = this.crystalPos;
                }
                if (isBreakable(this.pos) && distanceFromEye(this.pos) <= distance && (!Modules.SURROUND.isEnabled() || !isSurroundBlock(this.pos))) {
                    pon = this.pos;
                }
                if (this.getRedstonePos(distance).isEmpty()) {
                    for (BlockPos p : this.getPlaceRedstonePos(distance)) {
                        if (!isBreakable(p) || (Modules.SURROUND.isEnabled() && isSurroundBlock(p))) continue;
                        pon = p;
                        break;
                    }
                }
            }
            return pon;
        }

        private ArrayList<BlockPos> getSurroundBlocks(PlayerEntity player) {
            ArrayList<BlockPos> poses = new ArrayList<>();
            BlockPos pos = new BlockPos(player.getBlockX(), Math.round(player.getBlockY()), player.getBlockZ());
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                poses.add(pos.offset(direction));
            }
            return poses;
        }

        private boolean isSurroundBlock(BlockPos p) {
            for (BlockPos pos : getSurroundBlocks(mc.player)) {
                if (!p.equals(pos)) continue;
                return true;
            }
            return false;
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

    public record PosDirPair(BlockPos pos, Direction dir) {}

    public enum Action {
        Rotate,
        Support,
        PlaceLever,
        PlacePiston,
        ToggleLever,
        PlaceCrystal,
        PlaceRedstone,
        SupportCrystal,
        BreakActivator,
        BreakingCrystal,
        BreakInterfering,
        None
    }
}
