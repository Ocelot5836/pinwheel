package gg.moonflower.pinwheel.api.transform;

import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * Represents the transformation of a locator in space.
 *
 * @param locator The locator data
 * @param matrix  The matrix to store transformations in
 * @author Ocelot
 * @since 1.0.0
 */
public record LocatorTransformation(GeometryModelData.Locator locator, Matrix4f matrix) {

    /**
     * Creates a new locator transformation from a locator.
     *
     * @param locator The locator to make a transform for
     */
    public LocatorTransformation(@NotNull GeometryModelData.Locator locator) {
        this(locator, new Matrix4f());
    }
}
