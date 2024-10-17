package net.shoreline.client.impl.manager;

import net.shoreline.client.OvaqReborn;
import net.shoreline.client.OvaqRebornMod;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.impl.module.client.*;
import net.shoreline.client.impl.module.combat.*;
import net.shoreline.client.impl.module.exploit.*;
import net.shoreline.client.impl.module.misc.*;
import net.shoreline.client.impl.module.movement.*;
import net.shoreline.client.impl.module.render.*;
import net.shoreline.client.impl.module.world.*;

import java.util.*;

/**
 * @author linus
 * @since 1.0
 */
public class ModuleManager {
    // The client module register. Keeps a list of modules and their ids for
    // easy retrieval by id.
    private final Map<String, Module> modules =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Initializes the module register.
     */
    public ModuleManager() {
        // MAINTAIN ALPHABETICAL ORDER
        register(
                // Client
                new ServerModule(),
                new CapesModule(),
                new ClickGuiModule(),
                new ColorsModule(),
                new HUDModule(),
                new RPCModule(),
                new IRCModule(),
                //new FontModule(),
                new RotationsModule(),
                // Combat
                new AuraModule(),
                new AutoArmorModule(),
                new AutoBowReleaseModule(),
                new AutoCrystalModule(),
                new AutoLogModule(),
                new AutoTotemModule(),
                new AutoTrapModule(),
                new AutoXPModule(),
                new BacktrackModule(),
               // new BedAuraModule(),
                new BlockLagModule(),
                new BowAimModule(),
                new CriticalsModule(),
                new HoleFillModule(),
                new HoleSnapModule(),
                new NoHitDelayModule(),
                new ReplenishModule(),
                new PistonPushModule(),
                new SelfBowModule(),
                new SelfTrapModule(),
                new SurroundModule(),
                new TriggerModule(),
                // Exploit
                new AntiHungerModule(),
                new AntiResourcePackModule(),
                new ChorusControlModule(),
                new ClientSpoofModule(),
                new CrasherModule(),
                //new DisablerModule(),
                new ExtendedFireworkModule(),
                new FakeLatencyModule(),
                new FastLatencyModule(),
                new FastProjectileModule(),
                new HitboxDesyncModule(),
                //new HeartfulExploitModule(),
                new PacketCancelerModule(),
                new PacketFlyModule(),
                new PhaseModule(),
                new PearlPhaseModule(),
                //new TrapPhaseModule(),
                new PortalGodModeModule(),
                new ReachModule(),
                // Misc
                //new AntiAFKModule(),
                new AntiAimModule(),
                new ChatSuffixModule(),
                // new AntiBookBanModule(),
                new AntiSpamModule(),
                new AutoAcceptModule(),
                //new AutoEzModule(),
                new AutoFishModule(),
                new AutoReconnectModule(),
                new AutoRespawnModule(),
                new BeaconSelectorModule(),
                new BetterChatModule(),
                new ChatNotifierModule(),
                new ChestSwapModule(),
                // new ChestStealerModule(),
                new FakePlayerModule(),
                new InvCleanerModule(),
                new MiddleClickModule(),
                new NoPacketKickModule(),
                new NoSoundLagModule(),
                new SpammerModule(),
                new TimerModule(),
                new TotemSpamModule(),
                new TrueDurabilityModule(),
                new UnfocusedFPSModule(),
                new XCarryModule(),
                // Movement
                new AntiLevitationModule(),
                new AutoWalkModule(),
                //new BlockHoleMoveModule(),
                new BlockMoveModule(),
                new BoatFlyModule(),
                new ElytraFlyModule(),
                new EntityControlModule(),
                new EntitySpeedModule(),
                new FakeLagModule(),
                new FastFallModule(),
                new FlightModule(),
                new IceSpeedModule(),
                new JesusModule(),
                new LongJumpModule(),
                new NoFallModule(),
                new NoJumpDelayModule(),
                new NoSlowModule(),
                new ParkourModule(),
                new ReverseStepModule(),
                new SpeedModule(),
                new SprintModule(),
                new StepModule(),
                new TickShiftModule(),
                new VelocityModule(),
                new YawModule(),
                // Render
                new BlockHighlightModule(),
                new BreakHighlightModule(),
                new ChamsModule(),
                //new CrystalAnimationModule(),
                new ESPModule(),
                new ExtraTabModule(),
                new FreecamModule(),
                new FullbrightModule(),
                new HoleESPModule(),
                new KillEffectModule(),
                new NameProtectModule(),
                new NametagsModule(),
                new NoRenderModule(),
                new NoRotateModule(),
                new NoWeatherModule(),
                new ParticlesModule(),
                new SkeletonModule(),
                new SkyboxModule(),
                //new ShadersModule(),
                new TooltipsModule(),
                new TracersModule(),
                new TrueSightModule(),
                new ViewClipModule(),
                new ViewModelModule(),
                //new WaypointsModule(),
                new WorldTimeModule(),
                // World
               // new AirPlaceModule(),
                new AntiInteractModule(),
                new AutoMineModule(),
                new AutoToolModule(),
                new AvoidModule(),
                new BlockInteractModule(),
                new FastDropModule(),
                //new FastEatModule(),
                new FastPlaceModule(),
                new MultitaskModule(),
                new NoGlitchBlocksModule(),
                new ScaffoldModule(),
                new SpeedmineModule()
                // new WallhackModule()
        );
        if (OvaqRebornMod.isBaritonePresent()) {
            register(new BaritoneModule());
        }
        OvaqReborn.info("Registered {} modules!", modules.size());
    }

    /**
     *
     */
    public void postInit() {
        // TODO
    }

    /**
     * @param modules
     * @see #register(Module)
     */
    private void register(Module... modules) {
        for (Module module : modules) {
            register(module);
        }
    }

    /**
     * @param module
     */
    private void register(Module module) {
        modules.put(module.getId(), module);
    }

    /**
     * @param id
     * @return
     */
    public Module getModule(String id) {
        return modules.get(id);
    }

    /**
     * @return
     */
    public List<Module> getModules() {
        return new ArrayList<>(modules.values());
    }
}
