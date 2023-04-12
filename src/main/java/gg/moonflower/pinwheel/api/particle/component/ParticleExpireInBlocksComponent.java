package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;

/**
 * Component that specifies what blocks particles will immediately expire in.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleExpireInBlocksComponent(String[] blocks) implements ParticleComponent {

    public static ParticleExpireInBlocksComponent deserialize(JsonElement json) throws JsonParseException {
        JsonArray jsonObject = json.getAsJsonArray();
        String[] blocks = new String[jsonObject.size()];
        try {
            for (int i = 0; i < jsonObject.size(); i++) {
                blocks[i] = PinwheelGsonHelper.convertToString(jsonObject.get(i), "minecraft:particle_expire_if_in_blocks[" + i + "]");
            }
        } catch (Exception e) {
            throw new JsonSyntaxException(e);
        }
        return new ParticleExpireInBlocksComponent(blocks);
    }
}
