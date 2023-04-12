package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import gg.moonflower.pinwheel.api.particle.ParticleSourceObject;
import gg.moonflower.pinwheel.api.particle.ParticleInstance;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

/**
 * Component that spawns particles in a box around an entity.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterShapeEntityBoxComponent(boolean surfaceOnly,
                                             @Nullable MolangExpression[] direction,
                                             boolean inwards) implements ParticleEmitterShape {

    public static EmitterShapeEntityBoxComponent deserialize(JsonElement json) throws JsonParseException {
        Objects.requireNonNull(json, "json");
        JsonObject jsonObject = json.getAsJsonObject();
        boolean surfaceOnly = PinwheelGsonHelper.getAsBoolean(jsonObject, "surface_only", false);
        Either<Boolean, MolangExpression[]> dir = ParticleComponent.parseDirection(jsonObject, "direction");
        MolangExpression[] direction = dir.right().orElse(null);
        boolean inwards = dir.left().orElse(false);
        return new EmitterShapeEntityBoxComponent(surfaceOnly, direction, inwards);
    }

    @Override
    public void emitParticles(ParticleEmitterShape.Spawner spawner, int count) {
        ParticleSourceObject entity = spawner.getEntity();
        if (entity == null) {
            for (int i = 0; i < count; i++) {
                ParticleInstance particle = spawner.createParticle();
                MolangEnvironment runtime = particle.getEnvironment();
                double dx = 0;
                double dy = 0;
                double dz = 0;
                if (this.direction != null) {
                    dx = Objects.requireNonNull(this.direction[0]).safeResolve(runtime);
                    dy = Objects.requireNonNull(this.direction[1]).safeResolve(runtime);
                    dz = Objects.requireNonNull(this.direction[2]).safeResolve(runtime);
                }
                spawner.setPositionVelocity(particle, 0, 0, 0, dx, dy, dz);
            }
            return;
        }

        Random random = spawner.getRandom();
        ParticleSourceObject.Bounds bounds = entity.getBounds();
        for (int i = 0; i < count; i++) {
            ParticleInstance particle = spawner.createParticle();
            MolangEnvironment runtime = particle.getEnvironment();

            double radiusX = bounds.getMaxX() / 2F;
            double radiusY = bounds.getMaxY() / 2F;
            double radiusZ = bounds.getMaxZ() / 2F;
            double offsetX = bounds.getMinX() + radiusX;
            double offsetY = bounds.getMinY() + radiusY;
            double offsetZ = bounds.getMinZ() + radiusZ;
            double rx = this.surfaceOnly ? radiusX : radiusX * random.nextFloat();
            double ry = this.surfaceOnly ? radiusY : radiusY * random.nextFloat();
            double rz = this.surfaceOnly ? radiusZ : radiusZ * random.nextFloat();

            double x = (random.nextFloat() * 2 - 1) * rx;
            double y = (random.nextFloat() * 2 - 1) * ry;
            double z = (random.nextFloat() * 2 - 1) * rz;

            double dx;
            double dy;
            double dz;
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
        }
    }
}
