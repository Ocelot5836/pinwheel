package gg.moonflower.pinwheel.api.particle.render;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.particle.Flipbook;
import gg.moonflower.pinwheel.impl.particle.render.SingleQuadRenderPropertiesImpl;

/**
 * Defines the render properties for a single quad.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface SingleQuadRenderProperties extends ParticleRenderProperties {

    /**
     * @return The width of the quad
     */
    float getWidth();

    /**
     * @return The height of the quad
     */
    float getHeight();

    /**
     * @return The minimum x coordinate to use on the texture
     */
    float getUMin();

    /**
     * @return The minimum y coordinate to use on the texture
     */
    float getVMin();

    /**
     * @return The maximum x coordinate to use on the texture
     */
    float getUMax();

    /**
     * @return The maximum y coordinate to use on the texture
     */
    float getVMax();

    /**
     * Sets the width of the quad.
     *
     * @param width The new x size
     */
    void setWidth(float width);

    /**
     * Sets the height of the quad.
     *
     * @param height The new y size
     */
    void setHeight(float height);

    /**
     * Sets the UV coordinates of the quad.
     *
     * @param uMin The minimum x coordinate to use on the texture
     * @param vMin The minimum y coordinate to use on the texture
     * @param uMax The maximum x coordinate to use on the texture
     * @param vMax The maximum y coordinate to use on the texture
     */
    void setUV(float uMin, float vMin, float uMax, float vMax);

    /**
     * Sets the UV coordinates based on the provided flipbook.
     *
     * @param environment   The environment to resolve the flipbook in
     * @param textureWidth  The width of the texture
     * @param textureHeight The height of the texture
     * @param flipbook      The flipbook to use
     * @param time          The time in seconds since the beginning of the animation
     * @param maxLife       The maximum life of the particle
     */
    default void setUV(MolangEnvironment environment, int textureWidth, int textureHeight, Flipbook flipbook, float time, float maxLife) {
        int maxFrame = (int) flipbook.maxFrame().safeResolve(environment);
        int frame;
        if (flipbook.stretchToLifetime()) {
            frame = Math.min((int) (time / maxLife * (maxFrame + 1)), maxFrame);
        } else {
            frame = (int) (time * flipbook.fps());
            if (flipbook.loop()) {
                frame %= maxFrame;
            } else {
                frame = Math.min(frame, maxFrame);
            }
        }

        float u = flipbook.baseU().safeResolve(environment);
        float v = flipbook.baseV().safeResolve(environment);
        float uSize = flipbook.sizeU();
        float vSize = flipbook.sizeV();
        float uo = flipbook.stepU() * frame;
        float vo = flipbook.stepV() * frame;

        float uMin = (u + uo) / (float) textureWidth;
        float vMin = (v + vo) / (float) textureHeight;
        float uMax = (u + uo + uSize) / (float) textureWidth;
        float vMax = (v + vo + vSize) / (float) textureHeight;
        this.setUV(uMin, vMin, uMax, vMax);
    }

    /**
     * @return A new instance of single quad render properties
     */
    static SingleQuadRenderProperties quad() {
        return new SingleQuadRenderPropertiesImpl();
    }
}
