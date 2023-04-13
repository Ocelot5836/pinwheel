package gg.moonflower.pinwheel.api.geometry.bone;

import org.joml.Matrix4fc;
import org.joml.Vector4f;

import java.util.Objects;

/**
 * A single vertex in a definition of geometry.
 *
 * @param x The x position
 * @param y The y position
 * @param z The z position
 * @param u The u texture coordinate
 * @param v The v texture coordinate
 * @author Ocelot
 * @since 1.0.0
 */
public record Vertex(float x, float y, float z, float u, float v) {

    private static final Vector4f TRANSFORM_VECTOR = new Vector4f();

    /**
     * Creates a new vertex and transforms the position by the specified matrix.
     *
     * @param transform The matrix to transform the position by
     * @param x         The x position
     * @param y         The y position
     * @param z         The z position
     * @param u         The u texture coordinate
     * @param v         The v texture coordinate
     * @return A new transformed vertex
     */
    public static Vertex create(Matrix4fc transform, float x, float y, float z, float u, float v) {
        Objects.requireNonNull(transform, "transform");
        TRANSFORM_VECTOR.set(x, y, z, 1.0F);
        TRANSFORM_VECTOR.mul(transform);
        return new Vertex(TRANSFORM_VECTOR.x(), TRANSFORM_VECTOR.y(), TRANSFORM_VECTOR.z(), u, v);
    }
}
