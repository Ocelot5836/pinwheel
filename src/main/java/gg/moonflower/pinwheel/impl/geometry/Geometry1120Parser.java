package gg.moonflower.pinwheel.impl.geometry;

import com.google.gson.*;
import gg.moonflower.pinwheel.api.JSONTupleParser;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class Geometry1120Parser {

    private Geometry1120Parser() {
    }

    public static GeometryModelData[] parseModel(JsonElement json) throws JsonParseException {
        JsonArray jsonArray = PinwheelGsonHelper.getAsJsonArray(json.getAsJsonObject(), "minecraft:geometry");
        GeometryModelData[] data = new GeometryModelData[jsonArray.size()];
        for (int i = 0; i < data.length; i++) {
            JsonObject object = PinwheelGsonHelper.convertToJsonObject(jsonArray.get(i), "minecraft:geometry[" + i + "]");

            // Description
            GeometryModelData.Description description = parseDescription(PinwheelGsonHelper.getAsJsonObject(object, "description"));

            // Bones
            GeometryModelData.Bone[] bones;
            if (object.has("bones")) {
                Set<String> usedNames = new HashSet<>();
                JsonArray bonesJson = PinwheelGsonHelper.getAsJsonArray(object, "bones");
                bones = new GeometryModelData.Bone[bonesJson.size()];
                for (int j = 0; j < bones.length; j++) {
                    bones[j] = parseBone(PinwheelGsonHelper.convertToJsonObject(bonesJson.get(j), "bones[" + j + "]"));
                    if (!usedNames.add(bones[j].name())) {
                        throw new JsonSyntaxException("Duplicate bone: " + bones[j].name());
                    }
                }
            } else {
                bones = new GeometryModelData.Bone[0];
            }

            data[i] = new GeometryModelData(description, bones);
        }
        return data;
    }

    private static GeometryModelData.Description parseDescription(JsonObject json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String identifier = PinwheelGsonHelper.getAsString(jsonObject, "identifier");
        float visibleBoundsWidth = PinwheelGsonHelper.getAsFloat(jsonObject, "visible_bounds_width", 0);
        float visibleBoundsHeight = PinwheelGsonHelper.getAsFloat(jsonObject, "visible_bounds_height", 0);
        float[] visibleBoundsOffset = JSONTupleParser.getFloat(jsonObject, "visible_bounds_offset", 3, () -> new float[3]);
        int textureWidth = PinwheelGsonHelper.getAsInt(jsonObject, "texture_width", 256);
        int textureHeight = PinwheelGsonHelper.getAsInt(jsonObject, "texture_height", 256);
        boolean preserveModelPose2588 = PinwheelGsonHelper.getAsBoolean(jsonObject, "preserve_model_pose2588", false);
        if (textureWidth == 0) {
            throw new JsonSyntaxException("Texture width must not be zero");
        }
        if (textureHeight == 0) {
            throw new JsonSyntaxException("Texture height must not be zero");
        }
        return new GeometryModelData.Description(identifier, visibleBoundsWidth, visibleBoundsHeight, new Vector3f(visibleBoundsOffset[0], visibleBoundsOffset[1], visibleBoundsOffset[2]), textureWidth, textureHeight, preserveModelPose2588);
    }

    private static GeometryModelData.Bone parseBone(JsonObject json) throws JsonParseException {
        JsonObject boneJson = json.getAsJsonObject();
        String name = PinwheelGsonHelper.getAsString(boneJson, "name");
        boolean reset2588 = PinwheelGsonHelper.getAsBoolean(boneJson, "reset2588", false);
        boolean neverRender2588 = PinwheelGsonHelper.getAsBoolean(boneJson, "neverrender2588", false);
        String parent = PinwheelGsonHelper.getAsString(boneJson, "parent", null);
        float[] pivot = JSONTupleParser.getFloat(boneJson, "pivot", 3, () -> new float[3]);
        float[] rotation = JSONTupleParser.getFloat(boneJson, "rotation", 3, () -> new float[3]);
        float[] bindPoseRotation2588 = JSONTupleParser.getFloat(boneJson, "bind_pose_rotation2588", 3, () -> new float[3]);
        boolean mirror = PinwheelGsonHelper.getAsBoolean(boneJson, "mirror", false);
        float inflate = PinwheelGsonHelper.getAsFloat(boneJson, "inflate", 0);
        boolean debug = PinwheelGsonHelper.getAsBoolean(boneJson, "debug", false);

        GeometryModelData.Cube[] cubes = json.has("cubes") ? Geometry180Parser.parseCubes(json) : new GeometryModelData.Cube[0];
        GeometryModelData.Locator[] locators = json.has("locators") ? Geometry110Parser.parseLocators(json) : new GeometryModelData.Locator[0];

        GeometryModelData.PolyMesh polyMesh = boneJson.has("poly_mesh") ? Geometry180Parser.GSON.fromJson(boneJson.get("poly_mesh"), GeometryModelData.PolyMesh.class) : null;

        // TODO texture_mesh

        return new GeometryModelData.Bone(name, reset2588, neverRender2588, parent, new Vector3f(pivot[0], pivot[1], pivot[2]), new Vector3f(rotation[0], rotation[1], rotation[2]), new Vector3f(bindPoseRotation2588[0], bindPoseRotation2588[1], bindPoseRotation2588[2]), mirror, inflate, debug, cubes, locators, polyMesh);
    }
}
