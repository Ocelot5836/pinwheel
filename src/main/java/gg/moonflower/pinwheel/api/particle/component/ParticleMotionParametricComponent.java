package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import org.jetbrains.annotations.Nullable;

/**
 * Component that specifies how a particle moves over time directly.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleMotionParametricComponent(@Nullable MolangExpression[] relativePosition,
                                                @Nullable MolangExpression[] direction,
                                                MolangExpression rotation) implements ParticleComponent {

    public static ParticleMotionParametricComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        MolangExpression[] relativePosition = JsonTupleParser.getExpression(object, "relative_position", 3, () -> null);
        MolangExpression[] direction = JsonTupleParser.getExpression(object, "direction", 3, () -> null);
        MolangExpression rotation = JsonTupleParser.getExpression(object, "rotation", () -> MolangExpression.ZERO);
        return new ParticleMotionParametricComponent(relativePosition, direction, rotation);
    }
}
