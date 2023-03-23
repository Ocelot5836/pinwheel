package gg.moonflower.pinwheel.impl.animation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class AnimationParserImpl {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AnimationData[].class, new AnimationData.Deserializer()).create();
    private static final String VERSION = "1.8.0";

    public static AnimationData[] parse(JsonElement json) throws JsonSyntaxException {
        String formatVersion = PinwheelGsonHelper.getAsString(json.getAsJsonObject(), "format_version");
        if (!formatVersion.equals(VERSION))
            throw new JsonSyntaxException("Unsupported animation version: " + formatVersion);
        return GSON.fromJson(PinwheelGsonHelper.getAsJsonObject(json.getAsJsonObject(), "animations"), AnimationData[].class);
    }
}
