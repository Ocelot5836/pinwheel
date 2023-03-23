package gg.moonflower.pinwheel.api.transform;

import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.impl.transform.JomlMatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

/**
 * A set of matrix transformations that can be used while rendering {@link GeometryModel}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface MatrixStack {

    /**
     * Translates the position by the specified amount.
     *
     * @param x The x amount
     * @param y The y amount
     * @param z The z amount
     */
    default void translate(double x, double y, double z) {
        this.translate((float) x, (float) y, (float) z);
    }

    /**
     * Translates the position by the specified amount.
     *
     * @param x The x amount
     * @param y The y amount
     * @param z The z amount
     */
    void translate(float x, float y, float z);

    /**
     * Rotates the position and normal by the specified quaternion rotation.
     *
     * @param rotation The rotation to use
     */
    void rotate(Quaternionfc rotation);

    /**
     * <p>Rotates the position and normal by the specified angle about the line specified by x, y, z.</p>
     * <p>For rotating along all 3 axes, use {@link #rotateXYZ(float, float, float)} or {@link #rotateZYX(float, float, float)}.</p>
     *
     * @param amount The amount to rotate in radians
     * @param x      The x normal
     * @param y      The y normal
     * @param z      The z normal
     */
    void rotate(float amount, float x, float y, float z);

    /**
     * Rotates about the x, y, then z planes the specified angles.
     *
     * @param x The amount to rotate in the x in radians
     * @param y The amount to rotate in the y in radians
     * @param z The amount to rotate in the z in radians
     */
    void rotateXYZ(float x, float y, float z);

    /**
     * Rotates about the z, y, then x planes the specified angles.
     *
     * @param z The amount to rotate in the z in radians
     * @param y The amount to rotate in the y in radians
     * @param x The amount to rotate in the x in radians
     */
    void rotateZYX(float z, float y, float x);

    /**
     * Scales the position and normal by the specified amount in the x, y, and z.
     *
     * @param x The x scale factor
     * @param y The y scale factor
     * @param z The z scale factor
     */
    void scale(float x, float y, float z);

    /**
     * Saves the current position and normal transformation for restoring later wit {@link #popMatrix()}.
     */
    void pushMatrix();

    /**
     * Restores a previous position and normal set with {@link #pushMatrix()}.
     *
     * @throws IllegalStateException If there are no more matrix transformations to pop
     */
    void popMatrix();

    /**
     * @return The current position matrix
     */
    Matrix4f position();

    /**
     * @return The computed normal matrix from the position
     */
    Matrix3f normal();

    /**
     * @return A new matrix stack for managing transformations
     */
    static MatrixStack create() {
        return new JomlMatrixStack();
    }
}
