package gg.moonflower.pinwheel.api.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.geometry.GeometryModel;

import java.util.Collection;

/**
 * Manages the state of animation for a geometry model.
 *
 * @author Ocelot
 * @see GeometryModel
 * @since 1.0.0
 */
public interface AnimationController {

    /**
     * Clears all playing animations.
     */
    void clearAnimations();

    /**
     * @return The MoLang environment this animation is running in
     */
    MolangEnvironment getEnvironment();

    /**
     * @return All animations playing in this controller
     */
    Collection<? extends PlayingAnimation> getPlayingAnimations();

    /**
     * @return Whether no animations are currently playing
     */
    default boolean isNoAnimationPlaying() {
        return this.getPlayingAnimations().isEmpty();
    }

    /**
     * Sets the animation time for all animations.
     *
     * @param time The new time in seconds
     */
    default void setAnimationTime(float time) {
        this.getPlayingAnimations().forEach(animation -> animation.setAnimationTime(time));
    }

    /**
     * Sets the weight factor for all animations.
     *
     * @param weight The new relative weight
     */
    default void setWeight(float weight) {
        this.getPlayingAnimations().forEach(animation -> animation.setWeight(weight));
    }
}
