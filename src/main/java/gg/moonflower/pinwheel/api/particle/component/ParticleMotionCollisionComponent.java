package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;

/**
 * Component that specifies how a particle moves after colliding.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleMotionCollisionComponent(MolangExpression enabled,
                                               float collisionDrag,
                                               float coefficientOfRestitution,
                                               float collisionRadius,
                                               boolean expireOnContact,
                                               String[] events) implements ParticleComponent {

    public static final MolangExpression DEFAULT_ENABLED = MolangExpression.of(true);

    public static ParticleMotionCollisionComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        MolangExpression enabled = JsonTupleParser.getExpression(jsonObject, "enabled", () -> DEFAULT_ENABLED);
        float collisionDrag = PinwheelGsonHelper.getAsFloat(jsonObject, "collision_drag", 0) / 20F;
        float coefficientOfRestitution = PinwheelGsonHelper.getAsFloat(jsonObject, "coefficient_of_restitution", 0);
        float collisionRadius = PinwheelGsonHelper.getAsFloat(jsonObject, "collision_radius", 0.1F);
        boolean expireOnContact = PinwheelGsonHelper.getAsBoolean(jsonObject, "expire_on_contact", false);
        String[] events = ParticleComponent.getEvents(jsonObject, "events");
        return new ParticleMotionCollisionComponent(enabled, collisionDrag, coefficientOfRestitution, collisionRadius, expireOnContact, events);
    }
}
