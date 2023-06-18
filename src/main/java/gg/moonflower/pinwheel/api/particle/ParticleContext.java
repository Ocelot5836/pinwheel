package gg.moonflower.pinwheel.api.particle;


import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.Random;

/**
 * Basic context from a particle for {@link ParticleEvent}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ParticleContext {

    /**
     * Spawns a particle effect.
     *
     * @param effect The effect to spawn
     * @param type   The way to spawn the particle
     */
    void particleEffect(String effect, ParticleEvent.ParticleSpawnType type);

    /**
     * Plays a sound.
     *
     * @param sound The id of the sound to play
     */
    void soundEffect(String sound);

    /**
     * Executes an expression.
     *
     * @param expression The expression to execute
     */
    void expression(MolangExpression expression);

    /**
     * Logs a message to chat.
     *
     * @param message The message to send
     */
    void log(String message);

    /**
     * @return The source of randomness for particle events
     */
    Random getRandom();
}
