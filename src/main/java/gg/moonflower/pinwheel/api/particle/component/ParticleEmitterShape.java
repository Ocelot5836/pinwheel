package gg.moonflower.pinwheel.api.particle.component;

import gg.moonflower.pinwheel.api.particle.ParticleInstance;
import gg.moonflower.pinwheel.api.particle.ParticleSourceObject;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Components that can emit particles in different shapes.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ParticleEmitterShape extends ParticleEmitterComponent {

    /**
     * Default particle emitter shape that spawns no particles.
     */
    ParticleEmitterShape EMPTY = (spawner, count) -> {
    };

    /**
     * Emits the specified amount of particles.
     *
     * @param spawner The spawner to create particles
     * @param count   The number of particles to spawn
     */
    void emitParticles(ParticleEmitterShape.Spawner spawner, int count);

    /**
     * Spawns particles in the world and links them correctly.
     */
    interface Spawner {

        /**
         * @return A new particle entity
         */
        ParticleInstance createParticle();

        /**
         * Spawns the specified particle into the world.
         *
         * @param particle The particle to spawn
         */
        void spawnParticle(ParticleInstance particle);

        /**
         * @return The spawning entity instance or <code>null</code> if this spawner is no attached to anything
         */
        @Nullable ParticleSourceObject getEntity();

        /**
         * @return The environment for the spawning instance
         */
        MolangEnvironment getEnvironment();

        /**
         * @return The random instance for particles
         */
        Random getRandom();

        /**
         * Sets the relative position of the specified particle.
         *
         * @param x The new x position
         * @param y The new y position
         * @param z The new z position
         */
        void setPosition(ParticleInstance particle, double x, double y, double z);

        /**
         * Sets the velocity of the specified particle.
         *
         * @param dx The new x velocity
         * @param dy The new y velocity
         * @param dz The new z velocity
         */
        void setVelocity(ParticleInstance particle, double dx, double dy, double dz);

        /**
         * Sets the relative position and velocity of the specified particle.
         *
         * @param x  The new x position
         * @param y  The new y position
         * @param z  The new z position
         * @param dx The new x velocity
         * @param dy The new y velocity
         * @param dz The new z velocity
         */
        default void setPositionVelocity(ParticleInstance particle, double x, double y, double z, double dx, double dy, double dz) {
            this.setPosition(particle, x, y, z);
            this.setVelocity(particle, dx, dy, dz);
        }
    }
}
