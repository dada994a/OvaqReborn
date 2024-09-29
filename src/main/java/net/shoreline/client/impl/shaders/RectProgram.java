package net.shoreline.client.impl.shaders;

import net.minecraft.util.math.Vec2f;
import net.shoreline.client.api.render.shader.Program;
import net.shoreline.client.api.render.shader.Shader;
import net.shoreline.client.api.render.shader.Uniform;

import static org.lwjgl.opengl.GL20.*;

public class RectProgram extends Program {
    Uniform<Vec2f> size = new Uniform<>("size");
    Uniform<float[]> color = new Uniform<>("color"); // Vec4fの代わりにfloat配列を使用

    public RectProgram() {
        super(new Shader("rect.vert", GL_FRAGMENT_SHADER));
    }

    @Override
    public void initUniforms() {
        size.init(id);
        color.init(id);
    }

    @Override
    public void updateUniforms() {
        glUniform2f(size.getId(), size.get().x, size.get().y);
        float[] col = color.get();
        glUniform4f(color.getId(), col[0], col[1], col[2], col[3]); // 配列から色を取得
    }

    public void setUniforms(Vec2f size, float[] color) {
        this.size.set(size);
        this.color.set(color);
    }
}
