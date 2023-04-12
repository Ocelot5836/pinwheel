package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Component that listens for lifecycle events.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleLifetimeEventComponent(String[] creationEvent,
                                             String[] expirationEvent,
                                             TimelineEvent[] timelineEvents) implements ParticleComponent {

    public static ParticleLifetimeEventComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String[] creationEvent = ParticleComponent.getEvents(jsonObject, "creation_event");
        String[] expirationEvent = ParticleComponent.getEvents(jsonObject, "expiration_event");

        TimelineEvent[] timelineEvents;
        if (jsonObject.has("timeline")) {
            JsonObject timelineJson = PinwheelGsonHelper.getAsJsonObject(jsonObject, "timeline");
            List<TimelineEvent> events = new ArrayList<>(timelineJson.entrySet().size());
            for (Map.Entry<String, JsonElement> entry : timelineJson.entrySet()) {
                try {
                    events.add(new TimelineEvent(Float.parseFloat(entry.getKey()), ParticleComponent.parseEvents(entry.getValue(), entry.getKey())));
                } catch (Exception e) {
                    throw new JsonSyntaxException("Failed to parse " + entry.getKey() + " in timeline", e);
                }
            }
            events.sort((a, b) -> Float.compare(a.time(), b.time()));
            timelineEvents = events.toArray(TimelineEvent[]::new);
        } else {
            timelineEvents = new TimelineEvent[0];
        }

        return new ParticleLifetimeEventComponent(creationEvent, expirationEvent, timelineEvents);
    }

    @Override
    public boolean isEmitterComponent() {
        return true;
    }

    @Override
    public boolean isParticleComponent() {
        return true;
    }

    /**
     * An event that occurs on the timeline.
     *
     * @param time   The time the event happens at
     * @param events The events to fire
     */
    public record TimelineEvent(float time, String[] events) {
    }
}
