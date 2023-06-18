package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;

/**
 * Component that controls when an emitter can produce particles and if it should be removed.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterLifetimeExpressionComponent(MolangExpression activation,
                                                 MolangExpression expiration) implements ParticleEmitterComponent {

    public static final MolangExpression DEFAULT_ACTIVATION = MolangExpression.of(1);
    public static final MolangExpression DEFAULT_EXPIRATION = MolangExpression.ZERO;

    public static EmitterLifetimeExpressionComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        return new EmitterLifetimeExpressionComponent(
                JsonTupleParser.getExpression(object, "activation_expression", () -> EmitterLifetimeExpressionComponent.DEFAULT_ACTIVATION),
                JsonTupleParser.getExpression(object, "expiration_expression", () -> EmitterLifetimeExpressionComponent.DEFAULT_EXPIRATION));
    }
}
