package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;

/**
 * Component that controls when a particle should be removed and how long it can live for.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleLifetimeExpressionComponent(MolangExpression expirationExpression,
                                                  MolangExpression maxLifetime) implements ParticleComponent {

    public static final MolangExpression DEFAULT_EXPIRATION = MolangExpression.ZERO;
    public static final MolangExpression DEFAULT_MAX_LIFETIME = MolangExpression.of(1);

    public static ParticleLifetimeExpressionComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        return new ParticleLifetimeExpressionComponent(
                JsonTupleParser.getExpression(object, "expiration_expression", () -> ParticleLifetimeExpressionComponent.DEFAULT_EXPIRATION),
                JsonTupleParser.getExpression(object, "max_lifetime", () -> ParticleLifetimeExpressionComponent.DEFAULT_MAX_LIFETIME));
    }
}
