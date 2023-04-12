package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that specifies how a particle accelerates over time.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleMotionDynamicComponent(MolangExpression[] linearAcceleration,
                                             MolangExpression linearDragCoefficient,
                                             MolangExpression rotationAcceleration,
                                             MolangExpression rotationDragCoefficient) implements ParticleComponent {

    public static final MolangExpression DEFAULT_LINEAR_ACCELERATION_X = MolangExpression.ZERO;
    public static final MolangExpression DEFAULT_LINEAR_ACCELERATION_Y = MolangExpression.ZERO;
    public static final MolangExpression DEFAULT_LINEAR_ACCELERATION_Z = MolangExpression.ZERO;
    public static final MolangExpression DEFAULT_LINEAR_DRAG_COEFFICIENT = MolangExpression.ZERO;
    public static final MolangExpression DEFAULT_ROTATION_ACCELERATION = MolangExpression.ZERO;
    public static final MolangExpression DEFAULT_ROTATION_DRAG_COEFFICIENT = MolangExpression.ZERO;

    public static ParticleMotionDynamicComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        MolangExpression[] linearAcceleration = JsonTupleParser.getExpression(object, "linear_acceleration", 3, () -> new MolangExpression[]{
                ParticleMotionDynamicComponent.DEFAULT_LINEAR_ACCELERATION_X,
                ParticleMotionDynamicComponent.DEFAULT_LINEAR_ACCELERATION_Y,
                ParticleMotionDynamicComponent.DEFAULT_LINEAR_ACCELERATION_Z
        });
        MolangExpression linearDragCoefficient = JsonTupleParser.getExpression(object, "linear_drag_coefficient", () -> ParticleMotionDynamicComponent.DEFAULT_LINEAR_DRAG_COEFFICIENT);
        MolangExpression rotationAcceleration = JsonTupleParser.getExpression(object, "rotation_acceleration", () -> ParticleMotionDynamicComponent.DEFAULT_ROTATION_ACCELERATION);
        MolangExpression rotationDragCoefficient = JsonTupleParser.getExpression(object, "rotation_drag_coefficient", () -> ParticleMotionDynamicComponent.DEFAULT_ROTATION_DRAG_COEFFICIENT);
        return new ParticleMotionDynamicComponent(linearAcceleration, linearDragCoefficient, rotationAcceleration, rotationDragCoefficient);
    }
}
