package gg.moonflower.pinwheel.api.particle;

/**
 * An object particles can spawn from.
 *
 * @since 1.0.0
 */
public interface ParticleSourceObject {

    /**
     * @return The bounds of this object
     */
    Bounds getBounds();

    /**
     * The bounds of a source object.
     *
     * @since 1.0.0
     */
    interface Bounds {

        /**
         * @return The minimum x position of the AABB
         */
        float getMinX();

        /**
         * @return The minimum y position of the AABB
         */
        float getMinY();

        /**
         * @return The minimum z position of the AABB
         */
        float getMinZ();

        /**
         * @return The maximum x position of the AABB
         */
        float getMaxX();

        /**
         * @return The maximum y position of the AABB
         */
        float getMaxY();

        /**
         * @return The maximum z position of the AABB
         */
        float getMaxZ();
    }
}
