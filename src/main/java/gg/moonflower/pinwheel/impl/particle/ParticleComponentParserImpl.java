package gg.moonflower.pinwheel.impl.particle;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pinwheel.api.particle.ParticleComponentParser;
import gg.moonflower.pinwheel.api.particle.component.*;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@ApiStatus.Internal
public final class ParticleComponentParserImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticleComponentParser.class);
    private static final ParticleComponentParser INSTANCE = ServiceLoader.load(ParticleComponentParser.class).findFirst().orElseGet(() -> ParticleComponentParserImpl::deserialize);
    private static final Map<String, ParticleComponent.Factory> FACTORIES;

    static {
        ImmutableMap.Builder<String, ParticleComponent.Factory> builder = ImmutableMap.builder();
        put(builder, "emitter_lifetime_events", ParticleLifetimeEventComponent::deserialize);
        put(builder, "emitter_lifetime_expression", EmitterLifetimeExpressionComponent::deserialize);
        put(builder, "emitter_lifetime_looping", EmitterLifetimeLoopingComponent::deserialize);
        put(builder, "emitter_lifetime_once", EmitterLifetimeOnceComponent::deserialize);

        put(builder, "emitter_rate_instant", EmitterRateInstantComponent::deserialize);
        // Omit emitter_rate_manual
        put(builder, "emitter_rate_steady", EmitterRateSteadyComponent::deserialize);

        put(builder, "emitter_shape_disc", EmitterShapeDiscComponent::deserialize);
        put(builder, "emitter_shape_box", EmitterShapeBoxComponent::deserialize);
        put(builder, "emitter_shape_custom", EmitterShapePointComponent::deserialize);
        put(builder, "emitter_shape_entity_aabb", EmitterShapeEntityBoxComponent::deserialize);
        put(builder, "emitter_shape_point", EmitterShapePointComponent::deserialize);
        put(builder, "emitter_shape_sphere", EmitterShapeSphereComponent::deserialize);

        put(builder, "emitter_initialization", EmitterInitializationComponent::deserialize);
        put(builder, "emitter_local_space", EmitterLocalSpaceComponent::deserialize);

        put(builder, "particle_appearance_billboard", ParticleAppearanceBillboardComponent::deserialize);
        put(builder, "particle_appearance_lighting", ParticleAppearanceLightingComponent::deserialize);
        put(builder, "particle_appearance_tinting", ParticleAppearanceTintingComponent::deserialize);

        put(builder, "particle_initial_speed", ParticleInitialSpeedComponent::deserialize);
        put(builder, "particle_initial_spin", ParticleInitialSpinComponent::deserialize);

        put(builder, "particle_expire_if_in_blocks", ParticleExpireInBlocksComponent::deserialize);
        put(builder, "particle_expire_if_not_in_blocks", ParticleExpireNotInBlocksComponent::deserialize);
        put(builder, "particle_lifetime_events", ParticleLifetimeEventComponent::deserialize);
        put(builder, "particle_lifetime_expression", ParticleLifetimeExpressionComponent::deserialize);
        put(builder, "particle_kill_plane", ParticleKillPlaneComponent::deserialize);

        put(builder, "particle_motion_collision", ParticleMotionCollisionComponent::deserialize);
        put(builder, "particle_motion_dynamic", ParticleMotionDynamicComponent::deserialize);
        put(builder, "particle_motion_parametric", ParticleMotionParametricComponent::deserialize);
        FACTORIES = builder.build();
    }

    private ParticleComponentParserImpl() {
    }

    private static void put(ImmutableMap.Builder<String, ParticleComponent.Factory> builder, String name, ParticleComponent.Factory factory) {
        builder.put("minecraft:" + name, factory);
    }

    private static Map<String, ParticleComponent> deserialize(JsonObject json) throws JsonParseException {
        Map<String, ParticleComponent> components = new HashMap<>(json.size());
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            try {
                String id = entry.getKey();
                if (!FACTORIES.containsKey(id)) {
                    LOGGER.error("Unknown particle component: " + entry.getKey());
                    continue;
                }

                components.put(id, FACTORIES.get(id).create(entry.getValue()));
            } catch (Exception e) {
                throw new JsonSyntaxException("Invalid particle component: " + entry.getKey(), e);
            }
        }
        return Collections.unmodifiableMap(components);
    }

    public static ParticleComponentParser getInstance() {
        return INSTANCE;
    }
}
