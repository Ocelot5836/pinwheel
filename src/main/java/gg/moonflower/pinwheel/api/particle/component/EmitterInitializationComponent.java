package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import org.jetbrains.annotations.Nullable;

/**
 * Component that initializes emitters.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterInitializationComponent(@Nullable MolangExpression creationExpression,
                                             @Nullable MolangExpression tickExpression) implements ParticleEmitterComponent {

    public static EmitterInitializationComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        return new EmitterInitializationComponent(
                JsonTupleParser.getExpression(object, "creation_expression", () -> null),
                JsonTupleParser.getExpression(object, "per_update_expression", () -> null));
    }

    @Override
    public boolean canLoop() {
        return true;
    }
}
