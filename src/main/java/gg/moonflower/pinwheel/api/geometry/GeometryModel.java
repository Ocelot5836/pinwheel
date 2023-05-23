package gg.moonflower.pinwheel.api.geometry;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pinwheel.impl.animation.AnimationKeyframeResolverImpl;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
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
        public void applyAnimations(MolangEnvironment environment, Collection<PlayingAnimation> animations) {
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

    /**
     * Calculates the length of an animation based on the current time and loop modes of all animations.
     *
     * @param animationTime The current time in seconds
     * @param animations    The animations to get the length of
     * @return The length of the set of animations
     */
    @Deprecated
    static float getAnimationLength(float animationTime, Collection<PlayingAnimation> animations) {
        boolean loop = false;
        float length = 0;
        for (PlayingAnimation playingAnimation : animations) {
            if (playingAnimation.getAnimation().loop() != AnimationData.Loop.NONE) {
                loop = true;
            }
            if (playingAnimation.getLength() > length) {
                length = playingAnimation.getLength();
            }
        }

        if (loop && animationTime > length) {
            return length;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Applies the specified bone animation to the specified pose.
     *
     * @param animationTime The current animation time in seconds
     * @param weight        The amount this animation should affect the pose
     * @param environment   The environment to resolve animations in
     * @param pose          The pose to modify
     * @param boneAnimation The animation to apply
     */
    static void applyKeyframeAnimation(float animationTime, float weight, MolangEnvironment environment, AnimatedBone.AnimationPose pose, AnimationData.BoneAnimation boneAnimation) {
        AnimationKeyframeResolverImpl.apply(animationTime, weight, environment, pose, boneAnimation);
    }

    /**
     * Applies the specified animation transformations at the specified time.
     *
     * @param controller The controller to apply animations from
     */
    default void applyAnimations(AnimationController controller) {
        this.applyAnimations(controller.getEnvironment(), controller.getPlayingAnimations());
    }

    /**
     * Applies the specified animation transformations at the specified time.
     *
     * @param environment The runtime to execute MoLang instructions in.
     *                    This is generally going to be {@link MolangRuntime#runtime()}
     * @param animations  The animations to play
     */
    default void applyAnimations(MolangEnvironment environment, Collection<PlayingAnimation> animations) {
        this.resetTransformation();

        for (PlayingAnimation animation : animations) {
            float blendWeight = animation.getWeight(environment);
            if (Math.abs(blendWeight) <= 1E-6) {
                continue;
            }

            AnimationData data = animation.getAnimation();
            float localAnimationTime = animation.getRenderAnimationTime();
            for (AnimationData.BoneAnimation boneAnimation : data.boneAnimations()) {
                AnimatedBone bone = this.getBone(boneAnimation.name());
                if (bone == null) {
                    continue;
                }

                GeometryModel.applyKeyframeAnimation(localAnimationTime, blendWeight, environment, bone.getAnimationPose(), boneAnimation);
            }
        }
    }

    /**
     * Transforms and passes all polygons into the specified renderer.
     *
     * @param renderer    The renderer for mesh parts
     * @param matrixStack The matrix transformations to apply
     */
    void render(GeometryRenderer renderer, MatrixStack matrixStack);
}
