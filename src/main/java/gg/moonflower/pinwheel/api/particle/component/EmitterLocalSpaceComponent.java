package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;

/**
 * Component that determines if position, rotation, and velocity are relative to the emitter reference.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record EmitterLocalSpaceComponent(boolean position,
                                         boolean rotation,
                                         boolean velocity) implements ParticleComponent {

    public static EmitterLocalSpaceComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        return new EmitterLocalSpaceComponent(
                PinwheelGsonHelper.getAsBoolean(object, "position", false),
                PinwheelGsonHelper.getAsBoolean(object, "rotation", false),
                PinwheelGsonHelper.getAsBoolean(object, "velocity", false));
    }
}
