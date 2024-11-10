package net.shoreline.client.util.interfaces;

import net.minecraft.client.gl.Framebuffer;

/**
 * @author Hypinohaizin
 * @since 2024/11/10 15:34
 */

public interface IShaderEffect {
 void addFakeTargetHook(String name, Framebuffer buffer);
}
