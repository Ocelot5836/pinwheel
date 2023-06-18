package gg.moonflower.pinwheel.api.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.impl.animation.PlayingAnimationImpl;

/**
 * Represents an animation with a current animation state.
 *
 * @since 1.0.0
 */
public interface PlayingAnimation {

    /**
     * @return The animation that is playing
     */
    AnimationData getAnimation();

    /**
     * @return The length of this animation
     */
    default float getLength() {
        return this.getAnimation().animationLength();
    }

    /**
     * @return The time since the beginning of the animation in seconds
     */
    float getAnimationTime();

    /**
     * @return The time used when calculating what key frames to reference
     */
    default float getRenderAnimationTime() {
        float animationTime = this.getAnimationTime();
        return switch (this.getAnimation().loop()) {
            case NONE -> animationTime;
            case LOOP -> animationTime % this.getLength();
            case HOLD_ON_LAST_FRAME -> Math.min(animationTime, this.getLength());
        };
    }

    /**
     * @return If this animation is done playing
     */
    default boolean isDone() {
        return this.getAnimation().loop() == AnimationData.Loop.NONE && this.getAnimationTime() >= this.getLength();
    }

    /**
     * @return The manual weight factor of this animation
     */
    float getWeightFactor();

    /**
     * Calculates how much influence this animation should have when added to a model.
     *
     * @param environment The environment
     * @return The amount of this animation that should apply. A value of <code>1</code> is the regular addition factor
     */
    float getWeight(MolangEnvironment environment);

    /**
     * Sets the animation time.
     *
     * @param time The new time in seconds
     */
    void setAnimationTime(float time);

    /**
     * Sets the weight factor for this animation.
     *
     * @param weight The new relative weight
     */
    void setWeight(float weight);

    /**
     * Creates a new playing animation for the specified animation data.
     *
     * @param animation The animation to create an instance for
     * @return A new playing animation
     */
    static PlayingAnimation of(AnimationData animation) {
        return new PlayingAnimationImpl(animation);
    }
}
