package gg.moonflower.pinwheel.api.particle.render;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/**
 * Generic interface for specifying render properties.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ParticleRenderProperties {

    ParticleRenderProperties NONE = new ParticleRenderProperties() {
        @Override
        public Quaternionf getRotation() {
            return new Quaternionf();
        }

        @Override
        public float getRed() {
            return 0;
        }

        @Override
        public float getGreen() {
            return 0;
        }

        @Override
        public float getBlue() {
            return 0;
        }

        @Override
        public float getAlpha() {
            return 0;
        }

        @Override
        public void setRotation(Quaternionfc rotation) {
        }

        @Override
        public void setRed(float red) {
        }

        @Override
        public void setGreen(float green) {
        }

        @Override
        public void setBlue(float blue) {
        }

        @Override
        public void setAlpha(float alpha) {
        }

        @Override
        public boolean canRender() {
            return false;
        }
    };

    /**
     * @return The rotation of the particle model
     */
    Quaternionf getRotation();

    /**
     * @return The red tint of the model
     */
    float getRed();

    /**
     * @return The green tint of the model
     */
    float getGreen();

    /**
     * @return The blue tint of the model
     */
    float getBlue();

    /**
     * @return The alpha tint of the model
     */
    float getAlpha();

    /**
     * Sets the rotation of the particle model.
     *
     * @param rotation The new rotation
     */
    void setRotation(Quaternionfc rotation);

    /**
     * Sets the red tint of the model.
     *
     * @param red The new red factor
     */
    void setRed(float red);

    /**
     * Sets the green tint of the model.
     *
     * @param green The new green factor
     */
    void setGreen(float green);

    /**
     * Sets the blue tint of the model.
     *
     * @param blue The new blue factor
     */
    void setBlue(float blue);

    /**
     * Sets the alpha tint of the model.
     *
     * @param alpha The new alpha factor
     */
    void setAlpha(float alpha);

    /**
     * Sets the color of the model.
     *
     * @param red   The new red factor
     * @param green The new green factor
     * @param blue  The new blue factor
     * @param alpha The new alpha factor
     */
    default void setColor(float red, float green, float blue, float alpha) {
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
        this.setAlpha(alpha);
    }

    /**
     * Sets the color of the model.
     *
     * @param color The color in <code>AARRGGB</code> format
     */
    default void setColor(int color) {
        this.setRed((float) (color >> 16 & 0xFF) / 255F);
        this.setGreen((float) (color >> 8 & 0xFF) / 255F);
        this.setBlue((float) (color & 0xFF) / 255F);
        this.setAlpha((float) (color >> 24 & 0xFF) / 255F);
    }

    /**
     * @return Whether the model should be rendered
     */
    boolean canRender();
}
