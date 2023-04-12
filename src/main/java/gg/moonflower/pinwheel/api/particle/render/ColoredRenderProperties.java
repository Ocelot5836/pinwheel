package gg.moonflower.pinwheel.api.particle.render;

/**
 * Abstract implementation for colored render properties.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class ColoredRenderProperties implements ParticleRenderProperties {

    private float red;
    private float green;
    private float blue;
    private float alpha;

    public ColoredRenderProperties() {
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.alpha = 1.0F;
    }

    @Override
    public float getRed() {
        return red;
    }

    @Override
    public float getGreen() {
        return green;
    }

    @Override
    public float getBlue() {
        return blue;
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public void setRed(float red) {
        this.red = red;
    }

    @Override
    public void setGreen(float green) {
        this.green = green;
    }

    @Override
    public void setBlue(float blue) {
        this.blue = blue;
    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
