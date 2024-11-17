package net.shoreline.client.impl.shaders;

import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.shoreline.client.util.render.shaders.GlProgram;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class RoundShader extends GlProgram {
    public GlUniform size;
    public GlUniform location;
    public GlUniform radius;

    public GlUniform color1;
    public GlUniform color2;
    public GlUniform color3;
    public GlUniform color4;

    public GlUniform outlineColor;

    public GlUniform softness;
    public GlUniform thickness;

    public RoundShader() {
        super(new Identifier("minecraft", "round"), VertexFormats.POSITION);
    }

    @Override
    public void setup() {
        this.size = this.findUniform("size");
        this.location = this.findUniform("location");
        this.radius = this.findUniform("radius");

        this.color1 = this.findUniform("color1");
        this.color2 = this.findUniform("color2");
        this.color3 = this.findUniform("color3");
        this.color4 = this.findUniform("color4");

        this.outlineColor = this.findUniform("outlineColor");
        this.softness = this.findUniform("softness");
        this.thickness = this.findUniform("thickness");
    }
}