package gg.moonflower.pinwheel.api.geometry.bone;

import gg.moonflower.pinwheel.api.geometry.GeometryRenderer;
import gg.moonflower.pinwheel.api.transform.MatrixStack;

/**
 * A simple bone that exists as part of a mode.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ModelBone {

    /**
     * Copies the transformations from the specified bone into this bone.
     *
     * @param bone The bone to copy transformations from
     */
    void copyTransform(ModelBone bone);

    /**
     * Transforms the specified matrix stack to match this bone.
     *
     * @param matrixStack The matrix stack to transform
     */
    void translateAndRotate(MatrixStack matrixStack);

    /**
     * Renders this bone.
     *
     * @param renderer    The renderer to pass all polygons to
     * @param matrixStack The matrix transformation to draw the entire bone and all children in
     */
    void render(GeometryRenderer renderer, MatrixStack matrixStack);
}
