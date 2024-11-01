package net.shoreline.client.mixin.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.string.StringUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

/**
 * @author OvaqReborn
 * @see DebugHud
 * @since 1.0
 */
@Mixin(DebugHud.class)
public class MixinDebugHud {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "drawText", at = @At("HEAD"))
    private void modifyDebugText(DrawContext context, List<String> text, boolean left, CallbackInfo ci) {
        if (Modules.SMARTF3.isEnabled()) {
            text.removeIf(Objects::isNull);
            if (Modules.SMARTF3.getActiveRenderer()) {
                text.removeIf(s -> s.startsWith("[Fabric] Active renderer:"));
            }

            if (Modules.SMARTF3.getIris()) {
                text.removeIf(s -> s.startsWith("[Iris]"));
                text.removeIf(s -> s.startsWith("[Entity Batching]"));
            }

            if (Modules.SMARTF3.getSodium()) {
                var sodiumIndex = StringUtil.indexOfStartingWith(text, "§aSodium Renderer");
                if (sodiumIndex != -1) {
                    text.subList(sodiumIndex, Math.min(sodiumIndex + 7, text.size())).clear();

                    if (sodiumIndex > 0 && text.get(sodiumIndex - 1).isEmpty()) {
                        text.remove(sodiumIndex - 1);
                    }
                }
            }
            while (!text.isEmpty() && text.get(0).isEmpty()) {
                text.remove(0);
            }
        }
    }

    @Redirect(method = "getRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;", ordinal = 1))
    protected final HitResult.Type changeFluidHitType(HitResult result) {
        if (Modules.SMARTF3.isEnabled()) {
            if (Modules.SMARTF3.getShyFluids() && result instanceof BlockHitResult blockHitResult && client.world.getFluidState(blockHitResult.getBlockPos()).isEmpty()) {
                return HitResult.Type.MISS;
            }
        }
        return result.getType();
    }
}
