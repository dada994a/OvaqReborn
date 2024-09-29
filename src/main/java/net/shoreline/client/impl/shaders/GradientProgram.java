package net.shoreline.client.impl.shaders;

import net.minecraft.util.math.Vec2f;
import net.shoreline.client.api.render.shader.Program;
import net.shoreline.client.api.render.shader.Shader;
import net.shoreline.client.api.render.shader.Uniform;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glUniform2f;

public class GradientProgram extends Program {
    // 解像度を受け取るUniform
    private final Uniform<Vec2f> resolution = new Uniform<>("resolution");

    public GradientProgram() {
        // シェーダーの初期化
        super(new Shader("gradient.frag", GL_FRAGMENT_SHADER));
    }

    @Override
    public void initUniforms() {
        // Uniformを初期化
        resolution.init(id);
    }

    @Override
    public void updateUniforms() {
        // Uniformの値を更新
        Vec2f res = resolution.get();
        glUniform2f(resolution.getId(), res.x, res.y);
    }

    // 解像度を設定するメソッド
    public void setUniforms(Vec2f resolution) {
        this.resolution.set(resolution);
    }
}
