package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that summons particles at a steady rate until too many particles are spawned.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterRateSteadyComponent(MolangExpression spawnRate,
                                         MolangExpression maxParticles) implements ParticleEmitterComponent {

    public static final MolangExpression DEFAULT_SPAWN_RATE = MolangExpression.of(1);
    public static final MolangExpression DEFAULT_MAX_PARTICLES = MolangExpression.of(50);

    public static EmitterRateSteadyComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        return new EmitterRateSteadyComponent(
                JsonTupleParser.getExpression(object, "spawn_rate", () -> EmitterRateSteadyComponent.DEFAULT_SPAWN_RATE),
                JsonTupleParser.getExpression(object, "max_particles", () -> EmitterRateSteadyComponent.DEFAULT_MAX_PARTICLES));
    }
}
