package gg.moonflower.pinwheel.impl.particle.event;

import com.google.gson.*;
import gg.moonflower.pinwheel.api.particle.ParticleContext;
import gg.moonflower.pinwheel.api.particle.ParticleEvent;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;

import java.lang.reflect.Type;

/**
 * Creates a sound effect.
 *
 * @param effect The sound id to play
 */
public record SoundParticleEvent(String effect) implements ParticleEvent {

    @Override
    public void execute(ParticleContext context) {
        context.soundEffect(this.effect);
    }

    public static class Deserializer implements JsonDeserializer<SoundParticleEvent> {

        @Override
        public SoundParticleEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = PinwheelGsonHelper.convertToJsonObject(json, "sound_effect");
            String effect = PinwheelGsonHelper.getAsString(jsonObject, "event_name");
            return new SoundParticleEvent(effect);
        }
    }
}
