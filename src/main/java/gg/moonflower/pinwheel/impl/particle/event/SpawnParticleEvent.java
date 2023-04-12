package gg.moonflower.pinwheel.impl.particle.event;

import com.google.gson.*;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.api.particle.ParticleContext;
import gg.moonflower.pinwheel.api.particle.ParticleEvent;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Spawns a particle emitter.
 *
 * @param effect              The name of the particle to summon
 * @param type                The type of spawning to use
 * @param preEffectExpression The expression to evaluate before spawning the particle
 */
public record SpawnParticleEvent(String effect,
                                 ParticleEvent.ParticleSpawnType type,
                                 @Nullable MolangExpression preEffectExpression) implements ParticleEvent {

    @Override
    public void execute(ParticleContext context) {
        if (this.preEffectExpression != null) {
            context.expression(this.preEffectExpression);
        }
        context.particleEffect(this.effect, this.type);
    }

    public static class Deserializer implements JsonDeserializer<SpawnParticleEvent> {

        private static ParticleSpawnType parseType(String name) throws JsonParseException {
            for (ParticleSpawnType curveType : ParticleSpawnType.values()) {
                if (curveType.name().toLowerCase(Locale.ROOT).equalsIgnoreCase(name)) {
                    return curveType;
                }
            }
            String types = Arrays.stream(ParticleSpawnType.values())
                    .map(type -> type.name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(", "));
            throw new JsonSyntaxException("Unsupported particle type: " + name + ". Supported particle types: " + types);
        }

        @Override
        public SpawnParticleEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = PinwheelGsonHelper.convertToJsonObject(json, "particle_effect");
            String effect = PinwheelGsonHelper.getAsString(jsonObject, "effect");
            ParticleSpawnType type = parseType(PinwheelGsonHelper.getAsString(jsonObject, "type"));
            MolangExpression expression = JsonTupleParser.getExpression(jsonObject, "pre_effect_expression", () -> null);
            return new SpawnParticleEvent(effect, type, expression);
        }
    }
}
