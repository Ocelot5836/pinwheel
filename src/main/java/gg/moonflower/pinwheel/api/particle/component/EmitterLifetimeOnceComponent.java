package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;

/**
 * Component that spawns particles during the active time.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterLifetimeOnceComponent(MolangExpression activeTime) implements ParticleEmitterComponent {

    public static final MolangExpression DEFAULT_ACTIVE_TIME = MolangExpression.of(10);

    public static EmitterLifetimeOnceComponent deserialize(JsonElement json) throws JsonParseException {
        return new EmitterLifetimeOnceComponent(JsonTupleParser.getExpression(json.getAsJsonObject(), "active_time", () -> EmitterLifetimeOnceComponent.DEFAULT_ACTIVE_TIME));
    }
}
