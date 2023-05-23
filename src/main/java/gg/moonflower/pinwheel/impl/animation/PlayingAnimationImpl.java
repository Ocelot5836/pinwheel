package gg.moonflower.pinwheel.impl.animation;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PlayingAnimationImpl implements PlayingAnimation {

    private final AnimationData animation;
    private float time;
    private float weight;

    public PlayingAnimationImpl(AnimationData animation) {
        this.animation = animation;
        this.time = 0;
        this.weight = 1.0F;
    }

    @Override
    public AnimationData getAnimation() {
        return this.animation;
    }

    @Override
    public float getAnimationTime() {
        return this.time;
    }

    @Override
    public float getWeightFactor() {
        return this.weight;
    }

    @Override
    public float getWeight(MolangEnvironment environment) {
        if (this.weight == 0) {
            return 0;
        }
        environment.setThisValue(this.weight);
        return this.weight * this.animation.blendWeight().safeResolve(environment);
    }

    @Override
    public void setAnimationTime(float time) {
        this.time = time;
    }

    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }
}
