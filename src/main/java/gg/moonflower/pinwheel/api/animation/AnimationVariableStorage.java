package gg.moonflower.pinwheel.api.animation;

import gg.moonflower.pinwheel.impl.animation.AnimationVariableStorageImpl;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariableProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Stores variables used by animation controllers.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AnimationVariableStorage extends MolangVariableProvider {

    /**
     * Retrieves a field by name.
     *
     * @param name The name of the field to retrieve
     * @return The field found or <code>null</code>
     */
    @Nullable Value getField(String name);

    /**
     * Optionally retrieves a field by name.
     *
     * @param name The name of the field to retrieve
     * @return The field found
     */
    default Optional<Value> getFieldOptional(String name) {
        return Optional.ofNullable(this.getField(name));
    }

    /**
     * Checks if a field exists with the specified name.
     *
     * @param name The name of the field to check for
     * @return Whether that field exists
     */
    boolean hasField(String name);

    static AnimationVariableStorage create(Collection<String> names) {
        return new AnimationVariableStorageImpl(names);
    }

    static AnimationVariableStorage create(Map<String, Value> values) {
        return new AnimationVariableStorageImpl(values);
    }

    interface Value {

        float getValue();

        void setValue(float value);
    }
}
