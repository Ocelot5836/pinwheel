package gg.moonflower.pinwheel.api.particle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pinwheel.api.particle.component.ParticleComponent;
import gg.moonflower.pinwheel.impl.particle.ParticleComponentParserImpl;

/**
 * <p>Deserializes particle components from JSON.</p>
 * <p>The way components are parsed can be customized using the {@linkplain java.util.ServiceLoader Service Loader API},
 * otherwise a {@linkplain ParticleComponentParserImpl simple implementation} using case insensitive strings is used.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ParticleComponentParser {

    /**
     * @return The parser instance to use
     */
    static ParticleComponentParser getInstance() {
        return ParticleComponentParserImpl.getInstance();
    }

    /**
     * Deserializes all components in the specified json.
     *
     * @param json The json to deserialize components from
     * @return All components deserialized
     * @throws JsonParseException If any errors occurs deserializing components
     */
    ParticleComponent[] deserialize(JsonObject json) throws JsonParseException;
}
