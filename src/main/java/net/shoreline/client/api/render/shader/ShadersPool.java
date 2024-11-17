package net.shoreline.client.api.render.shader;

import net.shoreline.client.impl.shaders.RoundShader;

public class ShadersPool {
    public static RoundShader ROUNDED_SHADER;

    public static void initShaders() {
        ROUNDED_SHADER = new RoundShader();
    }
}
