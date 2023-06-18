package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;

/**
 * Component that summons particles once.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterRateInstantComponent(MolangExpression particleCount) implements ParticleEmitterComponent {

    public static final MolangExpression DEFAULT_PARTICLE_COUNT = MolangExpression.of(10);

    public static EmitterRateInstantComponent deserialize(JsonElement json) throws JsonParseException {
        return new EmitterRateInstantComponent(JsonTupleParser.getExpression(json.getAsJsonObject(), "num_particles", () -> EmitterRateInstantComponent.DEFAULT_PARTICLE_COUNT));
    }

    @Override
    public boolean canLoop() {
        return true;
    }
}
