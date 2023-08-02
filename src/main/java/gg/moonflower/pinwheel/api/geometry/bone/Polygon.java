package gg.moonflower.pinwheel.api.geometry.bone;

import gg.moonflower.pinwheel.api.FaceDirection;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A representation of a part of a geometry model as a set of vertices and normals.
 *
 * @param material The material this polygon should use or <code>null</code> for the default
 * @param vertices The vertices in this model
 * @param normals  The normal directions for each vertex
 * @author Ocelot
 * @since 1.0.0
 */
public record Polygon(@Nullable String material, Vertex[] vertices, Vector3fc[] normals) {

    public Polygon {
        Validate.isTrue(vertices.length == normals.length, "There must be an equal number of vertices and normals");
    }

    /**
     * @return Whether this polygon is a quad
     */
    public boolean isQuad() {
        return this.vertices.length == 4;
    }

    /**
     * @return Whether this polygon is a triangle
     */
    public boolean isTriangle() {
        return this.vertices.length == 3;
    }

    /**
     * Creates a new polygon that represents a quad.
     *
     * @param material     The material to use or <code>null</code> for the default
     * @param vertices     The vertices in the quad
     * @param normalMatrix The matrix to apply to the normals
     * @param mirror       Whether to mirror the quad
     * @param direction    The direction the quad is facing. Used for the normal
     * @return A new polygon that represents a quad
     */
    public static Polygon quad(@Nullable String material, Vertex[] vertices, Matrix3fc normalMatrix, boolean mirror, FaceDirection direction) {
        Validate.isTrue(vertices.length == 4, "Quads must have 4 vertices");
        if (mirror) {
            int i = vertices.length;

            for (int j = 0; j < i / 2; ++j) {
                Vertex vertex = vertices[j];
                vertices[j] = vertices[i - 1 - j];
                vertices[i - 1 - j] = vertex;
            }
        }

        Vector3f normal = new Vector3f(direction.normal());
        if (mirror) {
            normal.mul(-1.0F, 1.0F, 1.0F);
        }
        normal.mul(normalMatrix);

        return new Polygon(material, vertices, new Vector3fc[]{normal, normal, normal, normal});
    }
}
