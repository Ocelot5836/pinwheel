package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;

import java.util.Objects;

/**
 * Component that kills all particles that pass over a plane. Uses the standard <code>ax + by + cz + d = 0</code> form.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleKillPlaneComponent(float a, float b, float c, float d) implements ParticleComponent {

    public ParticleKillPlaneComponent(float[] coefficients) {
        this(coefficients[0], coefficients[1], coefficients[2], coefficients[3]);
    }

    public static ParticleKillPlaneComponent deserialize(JsonElement json) throws JsonParseException {
        Objects.requireNonNull(json, "json");
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            throw new JsonSyntaxException("Molang expressions are not supported");
        }
        if (!json.isJsonArray()) {
            throw new JsonSyntaxException("Expected minecraft:particle_kill_plane to be a JsonArray, was " + PinwheelGsonHelper.getType(json));
        }

        JsonArray vectorJson = json.getAsJsonArray();
        if (vectorJson.size() != 4) {
            throw new JsonParseException("Expected 4 minecraft:particle_kill_plane values, was " + vectorJson.size());
        }

        float[] coefficients = new float[4];
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = PinwheelGsonHelper.convertToFloat(vectorJson.get(i), "minecraft:particle_kill_plane[" + i + "]");
        }
        return new ParticleKillPlaneComponent(coefficients);
    }

    private double solve(double x, double y, double z) {
        return this.a * x + this.b * y + this.c * z + this.d;
    }

    /**
     * Solves the plane equation for the old and current position.
     *
     * @param oldX The old x to solve for
     * @param oldY The old y to solve for
     * @param oldZ The old z to solve for
     * @param x    The new x to solve for
     * @param y    The new y to solve for
     * @param z    The new z to solve for
     * @return Whether the change from the old position to the new position crossed the plane
     */
    public boolean solve(double oldX, double oldY, double oldZ, double x, double y, double z) {
        double old = this.solve(oldX, oldY, oldZ);
        double current = this.solve(x, y, z);
        return Math.signum(old) != Math.signum(current);
    }
}
