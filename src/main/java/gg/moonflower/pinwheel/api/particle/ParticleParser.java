package gg.moonflower.pinwheel.api.particle;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import gg.moonflower.pinwheel.impl.particle.event.SoundParticleEvent;
import gg.moonflower.pinwheel.impl.particle.event.SpawnParticleEvent;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;

import java.io.Reader;

/**
 * Helper to read {@link ParticleData} from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ParticleParser {

    Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ParticleData.class, new ParticleData.Deserializer())
            .registerTypeAdapter(ParticleData.Description.class, new ParticleData.Description.Deserializer())
            .registerTypeAdapter(ParticleData.Curve.class, new ParticleData.Curve.Deserializer())
            .registerTypeAdapter(ParticleEvent.class, new ParticleEvent.Deserializer())
            .registerTypeAdapter(SpawnParticleEvent.class, new SpawnParticleEvent.Deserializer())
            .registerTypeAdapter(SoundParticleEvent.class, new SoundParticleEvent.Deserializer())
            .registerTypeAdapter(Flipbook.class, new Flipbook.Deserializer())
            .create();

    /**
     * Creates a new particle from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new particle from the reader
     */
    static ParticleData parseParticle(Reader reader) throws JsonParseException {
        return parseParticle(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new particle from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new particle from the reader
     */
    static ParticleData parseParticle(JsonReader reader) throws JsonParseException {
        return parseParticle(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new particle from the specified JSON string.
     *
     * @param json The raw json string
     * @return A new particle from the json
     */
    static ParticleData parseParticle(String json) throws JsonParseException {
        return parseParticle(JsonParser.parseString(json));
    }

    /**
     * Creates a new particle from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new particle from the json
     */
    static ParticleData parseParticle(JsonElement json) throws JsonParseException {
        String formatVersion = PinwheelGsonHelper.getAsString(json.getAsJsonObject(), "format_version");
        if (formatVersion.equals("1.10.0")) {
            return GSON.fromJson(json.getAsJsonObject().getAsJsonObject("particle_effect"), ParticleData.class);
        }
        throw new JsonSyntaxException("Unsupported particle version: " + formatVersion);
    }

    /**
     * Creates a new particle event from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new particle event from the reader
     */
    static ParticleEvent parseParticleEvent(Reader reader) throws JsonParseException {
        return parseParticleEvent(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new particle event from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new particle event from the reader
     */
    static ParticleEvent parseParticleEvent(JsonReader reader) throws JsonParseException {
        return parseParticleEvent(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new particle event from the specified JSON string.
     *
     * @param json The raw json string
     * @return A new particle event from the json
     */
    static ParticleEvent parseParticleEvent(String json) throws JsonParseException {
        return parseParticleEvent(JsonParser.parseString(json));
    }

    /**
     * Creates a new particle event from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new particle event from the json
     */
    static ParticleEvent parseParticleEvent(JsonElement json) throws JsonParseException {
        return GSON.fromJson(json, ParticleEvent.class);
    }

    /**
     * Creates a new flipbook the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new flipbook from the reader
     */
    static Flipbook parseFlipbook(Reader reader) throws JsonParseException {
        return parseFlipbook(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new flipbook from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new flipbook from the reader
     */
    static Flipbook parseFlipbook(JsonReader reader) throws JsonParseException {
        return parseFlipbook(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new flipbook from the specified JSON string.
     *
     * @param json The raw json string
     * @return A new flipbook from the json
     */
    static Flipbook parseFlipbook(String json) throws JsonParseException {
        return parseFlipbook(JsonParser.parseString(json));
    }

    /**
     * Creates a new flipbook from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new flipbook from the json
     */
    static Flipbook parseFlipbook(JsonElement json) throws JsonParseException {
        return GSON.fromJson(json, Flipbook.class);
    }
}
