package gg.moonflower.pinwheel.impl.animation;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@ApiStatus.Internal
public class AnimationKeyframeResolverImpl {

    private static final Vector3f POSITION = new Vector3f();
    private static final Vector3f ROTATION = new Vector3f();
    private static final Vector3f SCALE = new Vector3f();

    public static void apply(float animationTime, float weight, MolangEnvironment environment, AnimatedBone.AnimationPose pose, AnimationData.BoneAnimation boneAnimation) {
        POSITION.set(0);
        ROTATION.set(0);
        SCALE.set(1);
        get(animationTime, environment, 0, boneAnimation.positionFrames(), POSITION);
        get(animationTime, environment, 0, boneAnimation.rotationFrames(), ROTATION);
        get(animationTime, environment, 1, boneAnimation.scaleFrames(), SCALE);
        POSITION.mul(weight);
        ROTATION.mul(weight);
        SCALE.sub(1, 1, 1);
        SCALE.mul(weight);
        pose.add(POSITION.x(), POSITION.y(), POSITION.z(), ROTATION.x(), ROTATION.y(), ROTATION.z(), SCALE.x(), SCALE.y(), SCALE.z());
    }

    private static void get(float animationTime, MolangEnvironment environment, float startValue, AnimationData.KeyFrame[] frames, Vector3f result) {
        environment.setThisValue(startValue);
        if (frames.length == 1) {
            AnimationData.KeyFrame keyFrame = frames[0];
            float x = environment.safeResolve(keyFrame.transformPostX());
            float y = environment.safeResolve(keyFrame.transformPostY());
            float z = environment.safeResolve(keyFrame.transformPostZ());
            result.set(x, y, z);
            return;
        }

        for (int i = 0; i < frames.length; i++) {
            AnimationData.KeyFrame to = frames[i];
            if ((to.time() < animationTime && i < frames.length - 1) || to.time() == 0) {
                continue;
            }

            AnimationData.KeyFrame from = i == 0 ? null : frames[i - 1];
            float timeOffset = from != null ? from.time() : 0;
            float progress = Math.min(1.0F, (animationTime - timeOffset) / (to.time() - timeOffset));

            if (to.lerpMode() == AnimationData.LerpMode.CATMULLROM) {
                AnimationData.KeyFrame before = i >= 2 ? frames[i - 2] : null;
                AnimationData.KeyFrame after = i < frames.length - 1 ? frames[i + 1] : null;
                catmullRom(progress, environment, startValue, before, from, to, after, result);
            } else {
                lerp(to.lerpMode().apply(progress), environment, startValue, from, to, result);
            }
            break;
        }
    }

    private static void lerp(float progress, MolangEnvironment environment, float startValue, @Nullable AnimationData.KeyFrame from, AnimationData.KeyFrame to, Vector3f result) {
        float fromX = from == null ? startValue : environment.safeResolve(from.transformPostX());
        float fromY = from == null ? startValue : environment.safeResolve(from.transformPostY());
        float fromZ = from == null ? startValue : environment.safeResolve(from.transformPostZ());

        float x = lerp(progress, fromX, environment.safeResolve(to.transformPreX()));
        float y = lerp(progress, fromY, environment.safeResolve(to.transformPreY()));
        float z = lerp(progress, fromZ, environment.safeResolve(to.transformPreZ()));
        result.set(x, y, z);
    }

    private static void catmullRom(float progress, MolangEnvironment environment, float startValue, @Nullable AnimationData.KeyFrame before, @Nullable AnimationData.KeyFrame from, AnimationData.KeyFrame to, @Nullable AnimationData.KeyFrame after, Vector3f result) {
        float fromX = from == null ? startValue : environment.safeResolve(from.transformPostX());
        float fromY = from == null ? startValue : environment.safeResolve(from.transformPostY());
        float fromZ = from == null ? startValue : environment.safeResolve(from.transformPostZ());

        float beforeX = before == null ? fromX : environment.safeResolve(before.transformPostX());
        float beforeY = before == null ? fromY : environment.safeResolve(before.transformPostY());
        float beforeZ = before == null ? fromZ : environment.safeResolve(before.transformPostZ());

        float toX = environment.safeResolve(to.transformPreX());
        float toY = environment.safeResolve(to.transformPreY());
        float toZ = environment.safeResolve(to.transformPreZ());

        float afterX = after == null ? toX : environment.safeResolve(after.transformPreX());
        float afterY = after == null ? toY : environment.safeResolve(after.transformPreY());
        float afterZ = after == null ? toZ : environment.safeResolve(after.transformPreZ());

        result.set(catmullRom(beforeX, fromX, toX, afterX, progress), catmullRom(beforeY, fromY, toY, afterY, progress), catmullRom(beforeZ, fromZ, toZ, afterZ, progress));
    }

    private static float lerp(float pct, float pre, float post) {
        return pre + pct * (post - pre);
    }

    private static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        return 0.5F * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
    }
}
