package gg.moonflower.pinwheel.impl.geometry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class GeometryModelParserImpl {

    public static GeometryModelData[] parseModel(JsonElement json) throws JsonParseException {
        String formatVersion = PinwheelGsonHelper.getAsString(json.getAsJsonObject(), "format_version");
        if (formatVersion.equals("1.12.0")) {
            return Geometry1120Parser.parseModel(json);
        }
        if (formatVersion.equals("1.8.0")) {
            return Geometry180Parser.parseModel(json);
        }
        if (formatVersion.equals("1.1.0")) {
            return Geometry110Parser.parseModel(json);
        }
        throw new JsonSyntaxException("Unsupported geometry version: " + formatVersion);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static TextureTable parseTextures(JsonElement json) throws JsonParseException {
        DataResult<TextureTable> result = TextureTable.CODEC.parse(JsonOps.INSTANCE, json);
        if (result.error().isPresent()) {
            throw new JsonParseException(result.error().get().message());
        }
        return result.result().get();
    }
}
