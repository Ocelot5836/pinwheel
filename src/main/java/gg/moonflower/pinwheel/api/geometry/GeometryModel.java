package gg.moonflower.pinwheel.api.geometry;

import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * An abstract geometry model that can be rendered.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface GeometryModel extends GeometryTree {

    /**
     * A blank model that can be used for empty entries.
     */
    GeometryModel EMPTY = new GeometryModel() {

        @Override
        public @Nullable AnimatedBone getBone(String name) {
            return null;
        }

        @Override
        public Collection<AnimatedBone> getBones() {
            return Collections.emptySet();
        }

        @Override
        public Collection<AnimatedBone> getRootBones() {
            return Collections.emptySet();
        }

        @Override
        public void render(GeometryRenderer renderer, MatrixStack matrixStack) {
        }

        @Override
        public @Nullable LocatorTransformation getLocatorTransformation(String name) {
            return null;
        }

        @Override
        public GeometryModelData.Locator[] getLocators() {
            return new GeometryModelData.Locator[0];
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "EmptyGeometry";
        }
    };

    String POLY_MESH_TEXTURE = "poly_mesh.texture";

    // TODO animation playing

//    /**
//     * Calculates the length of an animation based on the current time and loop modes of all animations.
//     *
//     * @param animationTime The current time in seconds
//     * @param animations    The animations to get the length of
//     * @return The length of the set of animations
//     */
//    static float getAnimationLength(float animationTime, AnimationData... animations) {
//        boolean loop = false;
//        float length = 0;
//        for (AnimationData animation : animations) {
//            if (animation.loop() != AnimationData.Loop.NONE) {
//                loop = true;
//            }
//            if (animation.animationLength() > length) {
//                length = animation.animationLength();
//            }
//        }
//
//        if (loop && animationTime > length) {
//            return length;
//        }
//        return Integer.MAX_VALUE;
//    }
//
//    /**
//     * Applies the specified animation transformations at the specified time.
//     *
//     * @param ticks      The time of the entity
//     * @param runtime    The runtime to execute MoLang instructions in.
//     *                   This is generally going to be {@link MolangRuntime#runtime()}
//     * @param animations The animations to play
//     * @param delta      The percentage between ticks
//     */
//    void applyAnimations(float ticks,
//                         MolangRuntime.Builder runtime,
//                         Collection<AnimationData> animations,
//                         float delta);

    /**
     * Transforms and passes all polygons into the specified renderer.
     *
     * @param renderer    The renderer for mesh parts
     * @param matrixStack The matrix transformations to apply
     */
    void render(GeometryRenderer renderer, MatrixStack matrixStack);
}
