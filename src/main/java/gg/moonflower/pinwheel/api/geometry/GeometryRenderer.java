package gg.moonflower.pinwheel.api.geometry;

import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import org.jetbrains.annotations.NotNull;

/**
 * Renders geometry polygons.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface GeometryRenderer {

    /**
     * Renders the specified geometry polygon with the specified material.
     *
     * @param matrixStack The current position and normal the specified polygon should be rendered with
     * @param polygon     The polygon to render. This may or may not be a quad
     */
    void render(@NotNull MatrixStack matrixStack, @NotNull Polygon polygon);
}
