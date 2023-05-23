package gg.moonflower.pinwheel.api.animation;

import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;

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
     * @return The MoLang environment this animation is running in
     */
    MolangEnvironment getEnvironment();

    /**
     * @return All custom variables for animation
     */
    AnimationVariableStorage getVariables();

    /**
     * @return All animations playing in this controller
     */
    Collection<PlayingAnimation> getPlayingAnimations();

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
