package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;

/**
 * Component that applies lighting to a particle.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public enum ParticleAppearanceLightingComponent implements ParticleComponent {

    INSTANCE;

    public static ParticleAppearanceLightingComponent deserialize(JsonElement json) {
        return INSTANCE;
    }
}
