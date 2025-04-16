package gg.moonflower.pinwheel.impl.geometry;

import com.google.gson.*;
import gg.moonflower.pinwheel.api.FaceDirection;
import gg.moonflower.pinwheel.api.JsonTupleParser;
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
public final class Geometry1210Parser {

    private Geometry1210Parser() {
    }

    public static GeometryModelData[] parseModel(JsonElement json) throws JsonParseException {
        JsonArray jsonArray = PinwheelGsonHelper.getAsJsonArray(json.getAsJsonObject(), "minecraft:geometry");
        GeometryModelData[] data = new GeometryModelData[jsonArray.size()];
        for (int i = 0; i < data.length; i++) {
            JsonObject object = PinwheelGsonHelper.convertToJsonObject(jsonArray.get(i), "minecraft:geometry[" + i + "]");

            // Description
            GeometryModelData.Description description = Geometry1120Parser.parseDescription(PinwheelGsonHelper.getAsJsonObject(object, "description"));

            // Cape
            String cape = PinwheelGsonHelper.getAsString(object, "cape", null);

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

            data[i] = new GeometryModelData(description, cape, bones);
        }
        return data;
    }

    static GeometryModelData.Bone parseBone(JsonObject json) throws JsonParseException {
        JsonObject boneJson = json.getAsJsonObject();
        String name = PinwheelGsonHelper.getAsString(boneJson, "name");
        boolean reset2588 = PinwheelGsonHelper.getAsBoolean(boneJson, "reset2588", false);
        boolean neverRender2588 = PinwheelGsonHelper.getAsBoolean(boneJson, "neverrender2588", false);
        String parent = PinwheelGsonHelper.getAsString(boneJson, "parent", null);
        float[] pivot = JsonTupleParser.getFloat(boneJson, "pivot", 3, () -> new float[3]);
        float[] rotation = JsonTupleParser.getFloat(boneJson, "rotation", 3, () -> new float[3]);
        float[] bindPoseRotation2588 = JsonTupleParser.getFloat(boneJson, "bind_pose_rotation2588", 3, () -> new float[3]);
        boolean mirror = PinwheelGsonHelper.getAsBoolean(boneJson, "mirror", false);
        float inflate = PinwheelGsonHelper.getAsFloat(boneJson, "inflate", 0);
        boolean debug = PinwheelGsonHelper.getAsBoolean(boneJson, "debug", false);

        GeometryModelData.Cube[] cubes = json.has("cubes") ? parseCubes(json) : new GeometryModelData.Cube[0];
        GeometryModelData.Locator[] locators = json.has("locators") ? Geometry110Parser.parseLocators(json) : new GeometryModelData.Locator[0];

        GeometryModelData.PolyMesh polyMesh = boneJson.has("poly_mesh") ? Geometry180Parser.GSON.fromJson(boneJson.get("poly_mesh"), GeometryModelData.PolyMesh.class) : null;

        // TODO texture_mesh

        return new GeometryModelData.Bone(name, reset2588, neverRender2588, parent, new Vector3f(pivot[0], pivot[1], pivot[2]), new Vector3f(rotation[0], rotation[1], rotation[2]), new Vector3f(bindPoseRotation2588[0], bindPoseRotation2588[1], bindPoseRotation2588[2]), mirror, inflate, debug, cubes, locators, null, polyMesh);
    }

    static GeometryModelData.Cube[] parseCubes(JsonObject json) {
        JsonArray cubesJson = PinwheelGsonHelper.getAsJsonArray(json, "cubes");
        GeometryModelData.Cube[] cubes = new GeometryModelData.Cube[cubesJson.size()];
        for (int i = 0; i < cubesJson.size(); i++) {
            cubes[i] = parseCube(PinwheelGsonHelper.convertToJsonObject(cubesJson.get(i), "cubes[" + i + "]"));
        }
        return cubes;
    }

    static GeometryModelData.Cube parseCube(JsonObject json) throws JsonParseException {
        JsonObject cubeJson = json.getAsJsonObject();
        float[] origin = JsonTupleParser.getFloat(cubeJson, "origin", 3, () -> new float[3]);
        float[] size = JsonTupleParser.getFloat(cubeJson, "size", 3, () -> new float[3]);
        float[] rotation = JsonTupleParser.getFloat(cubeJson, "rotation", 3, () -> new float[3]);
        float[] pivot = JsonTupleParser.getFloat(cubeJson, "pivot", 3, () -> new float[]{origin[0] + size[0] / 2F, origin[1] + size[1] / 2F, origin[2] + size[2] / 2F});
        boolean overrideInflate = cubeJson.has("inflate");
        float inflate = PinwheelGsonHelper.getAsFloat(cubeJson, "inflate", 0);
        boolean overrideMirror = cubeJson.has("mirror");
        boolean mirror = PinwheelGsonHelper.getAsBoolean(cubeJson, "mirror", false);
        GeometryModelData.CubeUV[] uv = parseUV(cubeJson, size);
        if (uv.length != FaceDirection.values().length) {
            throw new JsonParseException("Expected uv to be of size " + FaceDirection.values().length + ", was " + uv.length);
        }
        return new GeometryModelData.Cube(new Vector3f(origin[0], origin[1], origin[2]), new Vector3f(size[0], size[1], size[2]), new Vector3f(rotation[0], rotation[1], rotation[2]), new Vector3f(pivot[0], pivot[1], pivot[2]), overrideInflate, inflate, overrideMirror, mirror, uv);
    }

    static GeometryModelData.CubeUV[] parseUV(JsonObject cubeJson, float[] size) {
        if (!cubeJson.has("uv")) {
            return new GeometryModelData.CubeUV[6];
        }

        if (cubeJson.get("uv").isJsonArray()) {
            return Geometry110Parser.parseUV(cubeJson, size);
        }
        if (cubeJson.get("uv").isJsonObject()) {
            JsonObject uvJson = cubeJson.getAsJsonObject("uv");
            float[] cubeUV = JsonTupleParser.getFloat(uvJson, "uv", 2, () -> new float[2]);

            GeometryModelData.CubeUV[] uvs = new GeometryModelData.CubeUV[6];
            for (FaceDirection direction : FaceDirection.values()) {
                if (!uvJson.has(direction.getName())) {
                    continue;
                }

                JsonObject faceJson = PinwheelGsonHelper.getAsJsonObject(uvJson, direction.getName());
                float[] uv = JsonTupleParser.getFloat(faceJson, "uv", 2, null);
                float[] uvSize = JsonTupleParser.getFloat(faceJson, "uv_size", 2, () -> new float[2]);
                String material = PinwheelGsonHelper.getAsString(faceJson, "material_instance", "texture");
                GeometryModelData.CubeUVRotation rotation = parseUVRotation(faceJson);
                uvs[direction.get3DDataValue()] = new GeometryModelData.CubeUV(cubeUV[0] / size[0] + uv[0], cubeUV[1] / size[1] + uv[1], uvSize[0], uvSize[1], rotation, material);
            }
            return uvs;
        }
        throw new JsonSyntaxException("Expected uv to be a JsonObject, was " + PinwheelGsonHelper.getType(cubeJson.get("uv")));
    }

    static GeometryModelData.CubeUVRotation parseUVRotation(JsonObject uvJson) {
        int rotation = PinwheelGsonHelper.getAsInt(uvJson, "uv_rotation", 0);
        return switch (rotation) {
            case 0 -> GeometryModelData.CubeUVRotation.ROT_0;
            case 90 -> GeometryModelData.CubeUVRotation.ROT_90;
            case 180 -> GeometryModelData.CubeUVRotation.ROT_180;
            case 270 -> GeometryModelData.CubeUVRotation.ROT_270;
            default -> throw new JsonSyntaxException("UV rotation can be 0, 90, 180, or 270");
        };
    }
}
