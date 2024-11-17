package net.shoreline.client.api.shape;

import lombok.Builder;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector4i;

@Builder
@Getter
public class ShapeProperties {
    private Matrix4f matrix4f;
    private float x, y, width, height;
    private float softness, thickness;

    public static Vector4f round;

    @Builder.Default
    private int outlineColor = -1;
    private static Vector4i color;

    @Builder(toBuilder = true)
    private ShapeProperties(Matrix4f matrix4f, float x, float y, float width, float height, float softness,
                            float thickness, Vector4f round, int outlineColor, Vector4i color) {
        this.matrix4f = matrix4f;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.softness = softness;
        this.thickness = thickness;
        this.round = round != null ? round : new Vector4f(0);
        this.outlineColor = outlineColor;
        this.color = color != null ? color : new Vector4i(-1);
    }

    public static ShapeProperties.ShapePropertiesBuilder create(Matrix4f matrix4f, double x, double y, double width,
                                                                double height) {
        return ShapeProperties.create(matrix4f, x, y, width, height);
    }

    public static class ShapePropertiesBuilder {

        public ShapePropertiesBuilder color(int color) {
            setColor(new Vector4i(color));
            return this;
        }

        public ShapePropertiesBuilder color(Vector4i color) {
            setColor(color);
            return this;
        }

        public ShapePropertiesBuilder round(float round) {
            setRound(new Vector4f(round));
            return this;
        }

        public ShapePropertiesBuilder round(Vector4f round) {
            setRound(new Vector4f(round));
            return this;
        }

        public ShapePropertiesBuilder color(int... color) {
            setColor(new Vector4i(color));
            return this;
        }

        public ShapePropertiesBuilder round(float... round) {
            setRound(new Vector4f(round));
            return this;
        }
    }

    public static void setRound(Vector4f round) {
        ShapeProperties.round = round;
    }

    public Vector4f getRound() {
        return round;
    }

    public void setSoftness(float softness) {
        this.softness = softness;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public static void setColor(Vector4i color) {
        ShapeProperties.color = color;
    }

    public void setMatrix4f(Matrix4f matrix4f) {
        this.matrix4f = matrix4f;
    }

    public void setOutlineColor(int outlineColor) {
        this.outlineColor = outlineColor;
    }

    public float getHeight() {
        return height;
    }

    public float getSoftness() {
        return softness;
    }

    public float getThickness() {
        return thickness;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public Vector4i getColor() {
        return color;
    }

    public Matrix4f getMatrix4f() {
        return matrix4f;
    }

    public int getOutlineColor() {
        return outlineColor;
    }
}