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
                new ProtocolModule(),
                // Combat
                new AuraModule(),
                new AutoArmorModule(),
                new AutoBowReleaseModule(),
                new AutoObsidianModule(),
                new AutoCrystalModule(),
               new OldAutoCrystalModule(),
                new AutoLogModule(),
                new OffHandModule(),
                new AutoTrapModule(),
                new AutoWebModule(),
                new AutoXPModule(),
                new BacktrackModule(),
                new BlockLagModule(),
                new BowAimModule(),
                new CriticalsModule(),
                new HoleFillModule(),
                new HoleSnapModule(),
                new NoHitDelayModule(),
                new ReplenishModule(),
                new PistonAuraModule(),
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
                new ExtendedFireworkModule(),
                new FakeLatencyModule(),
                new FastLatencyModule(),
                new FastProjectileModule(),
                new HitboxDesyncModule(),
                //new HeartfulExploitModule(),
                new PacketCancelerModule(),
                new PacketControlModule(),
                new PacketFlyModule(),
                new PhaseModule(),
                new PearlPhaseModule(),
                new PortalGodModeModule(),
                new RaytraceBypassModule(),
                new ReachModule(),
                new RegenModule(),
                // Misc
                //new AntiAFKModule(),
                new AntiAimModule(),
                new ChatSuffixModule(),
                new AntiSpamModule(),
                new AutoAcceptModule(),
                new AutoEzModule(),
                new AutoFishModule(),
                new AutoGappleModule(),
                new AutoEatModule(),
                new AutoReconnectModule(),
                new AutoRespawnModule(),
                new AutoSignModule(),
                new BeaconSelectorModule(),
                new BetterChatModule(),
                new ChatNotifierModule(),
                new ChestSwapModule(),
                new DeathCoordModule(),
                new FakePlayerModule(),
                new InvCleanerModule(),
                new MiddleClickModule(),
                new NoPacketKickModule(),
                new NoSoundLagModule(),
                new SpammerModule(),
                new TimerModule(),
                new TrueDurabilityModule(),
                new UnfocusedFPSModule(),
                new XCarryModule(),
                // Movement
                new AntiLevitationModule(),
                new AutoWalkModule(),
                new BlockMoveModule(),
                new BoatFlyModule(),
                new ElytraFlyModule(),
                new EntityControlModule(),
                new EntitySpeedModule(),
                new FakeLagModule(),
                new FastFallModule(),
                new FlightModule(),
                new FollowModule(),
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
                //new BreadcrumbsModule(),
                new BreakHighlightModule(),
                new ChamsModule(),
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
                new PhaseESPModule(),
                new SkeletonModule(),
                new SkyboxModule(),
                new SmartF3Module(),
                //new ShadersModule(),
                new TooltipsModule(),
                new TracersModule(),
                new TrueSightModule(),
                new ViewClipModule(),
                new ViewModelModule(),
                //new WaypointsModule(),
                new WorldTimeModule(),
                // World
                new AirInteractModule(),
                new AntiInteractModule(),
                new AutoMineModule(),
                new AutoToolModule(),
                new AvoidModule(),
                new FastDropModule(),
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
