package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that specifies the initial speed of a particle.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleInitialSpeedComponent(MolangExpression[] speed) implements ParticleComponent {

    public static ParticleInitialSpeedComponent deserialize(JsonElement json) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            MolangExpression expression = JsonTupleParser.parseExpression(json, "speed");
            MolangExpression[] speed = new MolangExpression[]{expression, expression, expression};
            return new ParticleInitialSpeedComponent(speed);
        }

        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            if (jsonArray.size() != 3) {
                throw new JsonSyntaxException("Expected speed to be a JsonArray of size 3, was " + jsonArray.size());
            }
            MolangExpression dx = JsonTupleParser.parseExpression(jsonArray.get(0), "speed[0]");
            MolangExpression dy = JsonTupleParser.parseExpression(jsonArray.get(1), "speed[1]");
            MolangExpression dz = JsonTupleParser.parseExpression(jsonArray.get(2), "speed[2]");
            MolangExpression[] speed = new MolangExpression[]{dx, dy, dz};
            return new ParticleInitialSpeedComponent(speed);
        }

        throw new JsonSyntaxException("Expected speed to be a JsonArray or float, was " + PinwheelGsonHelper.getType(json));
    }
}
