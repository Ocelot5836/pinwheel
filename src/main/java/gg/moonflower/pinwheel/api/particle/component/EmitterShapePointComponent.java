package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.api.particle.ParticleInstance;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that spawns particles in a disc.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterShapePointComponent(MolangExpression[] offset,
                                         MolangExpression[] direction) implements ParticleEmitterShape {

    public static EmitterShapePointComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return new EmitterShapePointComponent(
                JsonTupleParser.getExpression(jsonObject, "offset", 3, () -> new MolangExpression[]{
                        MolangExpression.ZERO,
                        MolangExpression.ZERO,
                        MolangExpression.ZERO
                }),
                JsonTupleParser.getExpression(jsonObject, "direction", 3, () -> new MolangExpression[]{
                        MolangExpression.ZERO,
                        MolangExpression.ZERO,
                        MolangExpression.ZERO
                })
        );
    }

    @Override
    public void emitParticles(ParticleEmitterShape.Spawner spawner, int count) {
        for (int i = 0; i < count; i++) {
            ParticleInstance particle = spawner.createParticle();
            MolangEnvironment runtime = particle.getEnvironment();
            float x = this.offset[0].safeResolve(runtime);
            float y = this.offset[1].safeResolve(runtime);
            float z = this.offset[2].safeResolve(runtime);
            float dx = this.direction[0].safeResolve(runtime);
            float dy = this.direction[1].safeResolve(runtime);
            float dz = this.direction[2].safeResolve(runtime);
            spawner.setPositionVelocity(particle, x, y, z, dx, dy, dz);
            spawner.spawnParticle(particle);
        }
    }
}
