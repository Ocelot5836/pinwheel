package gg.moonflower.pinwheel.api.animation;

import gg.moonflower.molangcompiler.api.bridge.MolangVariableProvider;
import gg.moonflower.pinwheel.impl.animation.AnimationVariableStorageImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Stores queries used by animation controllers.
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

    /**
     * Creates a new variable storage with the specified keys.
     *
     * @param names The names of the variable values
     * @return A new storage for those values
     */
    static AnimationVariableStorage create(Collection<String> names) {
        return new AnimationVariableStorageImpl(names);
    }

    /**
     * Creates a new variable storage with the specified keys and values.
     *
     * @param values The names and values of the variables to add
     * @return A new storage for those values
     */
    static AnimationVariableStorage create(Map<String, Value> values) {
        return new AnimationVariableStorageImpl(values);
    }

    /**
     * @return A new builder for putting variable entries into storage
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Deep copies the values from the specified builder.
     *
     * @param copy The builder to copy from
     * @return A new builder for putting variable entries into storage
     */
    static Builder builder(Builder copy) {
        return new Builder(copy);
    }

    /**
     * Represents a value obtainable by variable storage.
     *
     * @since 1.0.0
     */
    interface Value {

        /**
         * @return The value stored
         */
        float getValue();

        /**
         * Sets the value to store.
         *
         * @param value The new value to store
         */
        void setValue(float value);
    }

    /**
     * Constructs a new variable storage.
     *
     * @since 1.0.0
     */
    class Builder {

        private final Map<String, Value> values;

        /**
         * Creates a new empty builder.
         */
        public Builder() {
            this.values = new HashMap<>();
        }

        /**
         * Creates a builder with new values copied from the specified builder.
         *
         * @param copy The builder to copy from
         */
        public Builder(Builder copy) {
            this.values = new HashMap<>(copy.values.size());
            copy.values.keySet().forEach(this::add);
        }

        /**
         * Adds a new value under the specified name.
         *
         * @param name The name of the value to add
         * @return The created value
         */
        public Value add(String name) {
            Value value = new AnimationVariableStorageImpl.ValueImpl();
            this.add(name, value);
            return value;
        }

        /**
         * Adds the specified value under the specified name.
         *
         * @param name  The name of the value to add
         * @param value The value to add
         */
        public Builder add(String name, Value value) {
            this.values.put(name, value);
            return this;
        }

        /**
         * Creates a new storage with the previously specified values.
         *
         * @return The storage containing the values
         */
        public AnimationVariableStorage create() {
            return AnimationVariableStorage.create(this.values);
        }
    }
}
