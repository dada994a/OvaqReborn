package net.shoreline.client.mixin.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import net.shoreline.client.impl.module.client.CapesModule;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.Globals;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry implements Globals {

    @Shadow @Final private GameProfile profile;
    @Unique
    private Identifier capeTexture;
    @Unique
    private boolean capeTextureLoaded;

    /**
     *
     * @param profile
     * @param secureChatEnforced
     * @param ci
     */
    @Inject(method = "<init>(Lcom/mojang/authlib/GameProfile;Z)V", at = @At("TAIL"))
    private void hookInit(GameProfile profile, boolean secureChatEnforced, CallbackInfo ci) {
        if (capeTextureLoaded) {
            return;
        }
        Managers.CAPES.loadPlayerCape(profile, identifier -> {
            capeTexture = identifier;
        });
        capeTextureLoaded = true;
    }

    /**
     *
     * @param cir
     */
    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void hookGetSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (Modules.CAPES.isEnabled() && Modules.CAPES.getUserConfig().getValue()) {
            String u = this.profile.getId().toString();
            for (String uuid : CapesModule.whitelist) {
                if (u.equals(uuid)) {
                    SkinTextures t = cir.getReturnValue();
                    SkinTextures customCapeTexture = new SkinTextures(t.texture(), t.textureUrl(), CapesModule.TEXTURE, CapesModule.TEXTURE, t.model(), t.secure());
                    cir.setReturnValue(customCapeTexture);
                    return;
                }
            }
        } else if (Modules.CAPES.isEnabled() && Modules.CAPES.getOptifineConfig().getValue()) {
            SkinTextures t = cir.getReturnValue();
            SkinTextures customCapeTexture = new SkinTextures(t.texture(), t.textureUrl(), capeTexture, capeTexture, t.model(), t.secure());
            cir.setReturnValue(customCapeTexture);
        }
    }
}
