package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;

/**
 * Component that specifies the initial rotation and rotation rate of a particle.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleInitialSpinComponent(MolangExpression rotation,
                                           MolangExpression rotationRate) implements ParticleComponent {

    public static ParticleInitialSpinComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        MolangExpression rotation = JsonTupleParser.getExpression(object, "rotation", () -> MolangExpression.ZERO);
        MolangExpression rotationRate = JsonTupleParser.getExpression(object, "rotation_rate", () -> MolangExpression.ZERO);
        return new ParticleInitialSpinComponent(rotation, rotationRate);
    }
}
