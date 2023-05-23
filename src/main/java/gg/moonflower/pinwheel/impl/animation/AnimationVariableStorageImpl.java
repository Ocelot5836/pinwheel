package gg.moonflower.pinwheel.impl.animation;

import gg.moonflower.pinwheel.api.animation.AnimationVariableStorage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class AnimationVariableStorageImpl implements AnimationVariableStorage {

    private final Map<String, Value> fields;

    public AnimationVariableStorageImpl(Collection<String> names) {
        this(names.stream().collect(Collectors.toMap(name -> name, name -> new ValueImpl())));
    }

    public AnimationVariableStorageImpl(Map<String, Value> values) {
        this.fields = Collections.unmodifiableMap(values);
    }

    @Override
    public @Nullable Value getField(String name) {
        return this.fields.get(name);
    }

    @Override
    public boolean hasField(String name) {
        return this.fields.containsKey(name);
    }

    @Override
    public void addMolangVariables(Context context) {
        this.fields.forEach((name, field) -> context.addQuery(name, field::getValue));
    }

    private static class ValueImpl implements Value {

        private float value;

        @Override
        public float getValue() {
            return this.value;
        }

        @Override
        public void setValue(float value) {
            this.value = value;
        }
    }
}
