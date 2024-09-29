package net.shoreline.client.impl.shaders;

import net.minecraft.util.math.Vec2f;
import net.shoreline.client.api.render.shader.Program;
import net.shoreline.client.api.render.shader.Shader;
import net.shoreline.client.api.render.shader.Uniform;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform1f;

public class CircleProgram extends Program {
    Uniform<Float> radius = new Uniform<>("radius");
    Uniform<Vec2f> position = new Uniform<>("cpos");

    public CircleProgram() {
        super(new Shader("circle.frag", GL_FRAGMENT_SHADER));
    }

    @Override
    public void initUniforms() {
        radius.init(id);
        position.init(id);
    }

    @Override
    public void updateUniforms() {
        glUniform1f(radius.getId(), radius.get());
        glUniform2f(position.getId(), position.get().x, position.get().y);
    }

    public void setUniforms(float radius, Vec2f position) {
        this.radius.set(radius);
        this.position.set(position);
    }
}
