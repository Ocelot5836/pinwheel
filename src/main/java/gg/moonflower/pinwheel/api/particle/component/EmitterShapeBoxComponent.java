package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.api.particle.ParticleInstance;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

/**
 * Component that spawns particles in a box.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterShapeBoxComponent(MolangExpression[] offset,
                                       MolangExpression[] halfDimensions,
                                       boolean surfaceOnly,
                                       @Nullable MolangExpression[] direction,
                                       boolean inwards) implements ParticleEmitterShape {

    public static EmitterShapeBoxComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        MolangExpression[] offset = JsonTupleParser.getExpression(jsonObject, "offset", 3, () -> new MolangExpression[]{
                MolangExpression.ZERO,
                MolangExpression.ZERO,
                MolangExpression.ZERO
        });
        MolangExpression[] halfDimensions = JsonTupleParser.getExpression(jsonObject, "half_dimensions", 3, null);
        boolean surfaceOnly = PinwheelGsonHelper.getAsBoolean(jsonObject, "surface_only", false);
        Either<Boolean, MolangExpression[]> dir = ParticleComponent.parseDirection(jsonObject, "direction");
        MolangExpression[] direction = dir.right().orElse(null);
        boolean inwards = dir.left().orElse(false);
        return new EmitterShapeBoxComponent(offset, halfDimensions, surfaceOnly, direction, inwards);
    }

    @Override
    public void emitParticles(ParticleEmitterShape.Spawner spawner, int count) {
        Random random = spawner.getRandom();
        for (int i = 0; i < count; i++) {
            ParticleInstance particle = spawner.createParticle();
            MolangEnvironment runtime = particle.getEnvironment();

            float offsetX = this.offset[0].safeResolve(runtime);
            float offsetY = this.offset[1].safeResolve(runtime);
            float offsetZ = this.offset[2].safeResolve(runtime);
            float radiusX = this.halfDimensions[0].safeResolve(runtime);
            float radiusY = this.halfDimensions[1].safeResolve(runtime);
            float radiusZ = this.halfDimensions[2].safeResolve(runtime);
            float rx = this.surfaceOnly ? radiusX : radiusX * random.nextFloat();
            float ry = this.surfaceOnly ? radiusY : radiusY * random.nextFloat();
            float rz = this.surfaceOnly ? radiusZ : radiusZ * random.nextFloat();

            float x = (random.nextFloat() * 2 - 1) * rx;
            float y = (random.nextFloat() * 2 - 1) * ry;
            float z = (random.nextFloat() * 2 - 1) * rz;

            float dx;
            float dy;
            float dz;
            if (this.direction != null) {
                dx = Objects.requireNonNull(this.direction[0]).safeResolve(runtime);
                dy = Objects.requireNonNull(this.direction[1]).safeResolve(runtime);
                dz = Objects.requireNonNull(this.direction[2]).safeResolve(runtime);
            } else {
                dx = x;
                dy = y;
                dz = z;
                if (this.inwards) {
                    dx = -dx;
                    dy = -dy;
                    dz = -dz;
                }
            }

            spawner.setPositionVelocity(particle, offsetX + x, offsetY + y, offsetZ + z, dx, dy, dz);
            spawner.spawnParticle(particle);
        }
    }
}
