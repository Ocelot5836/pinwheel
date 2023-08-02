package gg.moonflower.pinwheel.impl.particle.event;

import gg.moonflower.pinwheel.api.particle.ParticleContext;
import gg.moonflower.pinwheel.api.particle.ParticleEvent;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Executes a random {@link ParticleEvent} based on weight.
 */
public class RandomParticleEvent implements ParticleEvent {

    private final List<WeightedEvent> events;
    private final int totalWeight;

    public RandomParticleEvent(ParticleEvent[] events, int[] weights) {
        if (events.length != weights.length) {
            throw new IllegalArgumentException("Expected " + events.length + " weights, got " + weights.length);
        }
        this.events = IntStream.range(0, events.length).mapToObj(i -> new WeightedEvent(events[i], weights[i])).toList();

        long weight = 0L;
        for (WeightedEvent weightedEntry : this.events) {
            weight += weightedEntry.weight;
        }

        if (weight > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        }
        this.totalWeight = (int) weight;
    }

    @Override
    public void execute(ParticleContext context) {
        if (this.events.isEmpty()) {
            return;
        }

        int roll = context.getRandom().nextInt(this.totalWeight);
        for (WeightedEvent weightedEntry : this.events) {
            roll -= weightedEntry.weight;
            if (roll < 0) {
                weightedEntry.event.execute(context);
                break;
            }
        }
    }

    private record WeightedEvent(ParticleEvent event, int weight) {
    }
}
