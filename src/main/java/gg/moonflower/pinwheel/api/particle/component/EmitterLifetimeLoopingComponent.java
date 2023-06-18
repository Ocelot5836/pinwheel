package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;

/**
 * Component that spawns particles during the active time.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterLifetimeLoopingComponent(MolangExpression activeTime,
                                              MolangExpression sleepTime) implements ParticleEmitterComponent {

    public static final MolangExpression DEFAULT_ACTIVE_TIME = MolangExpression.of(10);
    public static final MolangExpression DEFAULT_SLEEP_TIME = MolangExpression.ZERO;

    public static EmitterLifetimeLoopingComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        return new EmitterLifetimeLoopingComponent(
                JsonTupleParser.getExpression(object, "active_time", () -> EmitterLifetimeLoopingComponent.DEFAULT_ACTIVE_TIME),
                JsonTupleParser.getExpression(object, "sleep_time", () -> EmitterLifetimeLoopingComponent.DEFAULT_SLEEP_TIME));
    }
}
