package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Defines a component in a particle definition that can be used to create actual Artemis entity components.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ParticleComponent {

    /**
     * @return Whether this component can only be added to emitters
     */
    default boolean isEmitterComponent() {
        return false;
    }

    /**
     * @return Whether this component can only be added to particles
     */
    default boolean isParticleComponent() {
        return !this.isEmitterComponent();
    }

    /**
     * @return Whether this component should be re-added when an emitter loops
     */
    default boolean canLoop() {
        return false;
    }

    /**
     * Reads all event references in the specified json.
     *
     * @param json The json to read references from
     * @param name The name of the element to get
     * @return The events parsed
     * @throws JsonSyntaxException If the file is malformed
     */
    static String[] getEvents(JsonObject json, String name) throws JsonSyntaxException {
        if (!json.has(name)) {
            return new String[0];
        }
        return ParticleComponent.parseEvents(json.get(name), name);
    }

    /**
     * Reads all event references in the specified json.
     *
     * @param element The element to get as events
     * @param name    The name of the element
     * @return The events parsed
     * @throws JsonSyntaxException If the file is malformed
     */
    static String[] parseEvents(@Nullable JsonElement element, String name) throws JsonSyntaxException {
        if (element == null) {
            throw new JsonSyntaxException("Missing " + name + ", expected to find a JsonArray or string");
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            String[] events = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                events[i] = PinwheelGsonHelper.convertToString(array.get(i), name + "[" + i + "]");
            }
            return events;
        } else if (element.isJsonPrimitive()) {
            return new String[]{PinwheelGsonHelper.convertToString(element, name)};
        }
        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray or string, was " + PinwheelGsonHelper.getType(element));
    }

    /**
     * Parses a direction as either inwards or a custom MoLang expression.
     *
     * @param json The json to get the direction from
     * @param name The name of the element
     * @return The parsed direction
     */
    static Either<Boolean, MolangExpression[]> parseDirection(JsonObject json, String name) {
        if (!json.has(name)) {
            return Either.left(false);
        }

        if (json.get(name).isJsonPrimitive()) {
            String directionString = PinwheelGsonHelper.getAsString(json, name);
            if ("inwards".equalsIgnoreCase(directionString)) {
                return Either.left(true);
            } else if ("outwards".equalsIgnoreCase(directionString)) {
                return Either.left(false);
            } else {
                throw new JsonSyntaxException("Expected direction to be inwards or outwards, was " + directionString);
            }
        }

        return Either.right(JsonTupleParser.getExpression(json, name, 3, null));
    }

    /**
     * Context for adding components to entities.
     */
    interface Context {

        /**
         * @return The environment to run events in
         */
        MolangEnvironment getEnvironment();
    }

    /**
     * Deserializes components from JSON.
     */
    @FunctionalInterface
    interface Factory {

        /**
         * Creates a new particle component from JSON.
         *
         * @param json The json to deserialize data from
         * @return A new particle component
         * @throws JsonParseException If any error occurs while deserializing the component
         */
        ParticleComponent create(JsonElement json) throws JsonParseException;
    }
}
