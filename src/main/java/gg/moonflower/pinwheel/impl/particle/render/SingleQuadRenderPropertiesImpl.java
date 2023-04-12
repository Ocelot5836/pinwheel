package gg.moonflower.pinwheel.impl.particle.render;

import gg.moonflower.pinwheel.api.particle.render.ColoredRenderProperties;
import gg.moonflower.pinwheel.api.particle.render.SingleQuadRenderProperties;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/**
 * Specifies render properties for a single quad.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class SingleQuadRenderPropertiesImpl extends ColoredRenderProperties implements SingleQuadRenderProperties {

    private final Quaternionf quaternion = new Quaternionf();
    private float width;
    private float height;
    private float uMin;
    private float vMin;
    private float uMax;
    private float vMax;

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getUMin() {
        return uMin;
    }

    @Override
    public float getVMin() {
        return vMin;
    }

    @Override
    public float getUMax() {
        return uMax;
    }

    @Override
    public float getVMax() {
        return vMax;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void setUV(float uMin, float vMin, float uMax, float vMax) {
        this.uMin = uMin;
        this.vMin = vMin;
        this.uMax = uMax;
        this.vMax = vMax;
    }

    @Override
    public Quaternionf getRotation() {
        return this.quaternion;
    }

    @Override
    public void setRotation(Quaternionfc rotation) {
        this.quaternion.set(rotation);
    }

    @Override
    public boolean canRender() {
        return this.width * this.height > 0;
    }
}
