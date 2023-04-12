package gg.moonflower.pinwheel.api.particle.component;

/**
 * Helper for emitters to define default properties.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ParticleEmitterComponent extends ParticleComponent {

    @Override
    default boolean isEmitterComponent() {
        return true;
    }
}
