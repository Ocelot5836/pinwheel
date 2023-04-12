package gg.moonflower.pinwheel.api.particle;

import io.github.ocelot.molangcompiler.api.MolangEnvironment;

/**
 * An instance of a particle that contains the basic requirements of all particles.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ParticleInstance {

    /**
     * @return The time in seconds this particle has been alive for
     */
    float getAge();

    /**
     * @return The maximum amount of time in seconds this particle can live
     */
    float getLifetime();

    /**
     * @return This particle's MoLang execution environment
     */
    MolangEnvironment getEnvironment();
}
