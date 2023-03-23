package gg.moonflower.pinwheel.api.geometry;

import com.google.gson.*;
import gg.moonflower.pinwheel.api.FaceDirection;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Deserializes custom bedrock models from JSON.
 *
 * @param description Information about this model
 * @param bones       The individual parts in the model
 * @author Ocelot
 * @since 1.0.0
 */
public record GeometryModelData(Description description, Bone[] bones) {

    /**
     * A completely empty model definition.
     */
    public static final GeometryModelData EMPTY = new GeometryModelData(new Description("empty", 0, 0, new Vector3f(), 256, 256, false), new Bone[0]);

    @Override
    public String toString() {
        return "BedrockModel{" +
                "description=" + this.description +
                ", bones=" + Arrays.toString(this.bones) +
                '}';
    }

    /**
     * The different types of polygons that can be represented by a poly mesh.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public enum PolyType {

        TRIANGLES("tri_list", 3),
        QUADS("quad_list", 4);

        private final String name;
        private final int vertices;

        PolyType(String name, int vertices) {
            this.name = name;
            this.vertices = vertices;
        }

        /**
         * @return The JSON name of this poly type
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return The amount of vertices in this shape
         */
        public int getVertices() {
            return this.vertices;
        }
    }

    /**
     * Information about the model.
     *
     * @param identifier          The identifier of this model. Used to refer to this geometry definition
     * @param visibleBoundsWidth  The width of the visibility bounding box
     * @param visibleBoundsHeight The height of the visibility bounding box
     * @param visibleBoundsOffset The offset of the visibility bounding box from the origin
     * @param textureWidth        The width of the texture in pixels
     * @param textureHeight       The height of the texture in pixels
     * @author Ocelot
     * @since 1.0.0
     */
    public record Description(String identifier, float visibleBoundsWidth, float visibleBoundsHeight,
                              Vector3f visibleBoundsOffset, int textureWidth, int textureHeight,
                              boolean preserveModelPose2588) {

        @Override
        public String toString() {
            return "Description{" +
                    "identifier='" + this.identifier + '\'' +
                    ", visibleBoundsWidth=" + this.visibleBoundsWidth +
                    ", visibleBoundsHeight=" + this.visibleBoundsHeight +
                    ", visibleBoundsOffset=" + this.visibleBoundsOffset +
                    ", textureWidth=" + this.textureWidth +
                    ", textureHeight=" + this.textureHeight +
                    ", preserveModelPose2588=" + this.preserveModelPose2588 +
                    '}';
        }
    }

    /**
     * A single bone equivalent to vanilla model parts.
     *
     * @param name     The identifier used when fetching this bone
     * @param parent   The bone this bone is relative to
     * @param pivot    The position this bone pivots around
     * @param rotation The initial rotation of this bone in degrees
     * @param mirror   Whether the cube should be mirrored along the un-rotated x-axis
     * @param inflate  The amount to grow in all FaceDirections
     * @param cubes    The list of cubes associated with this bone
     * @param locators The list of positions attached to this bone
     * @param polyMesh The polygon mesh associated with the bone or <code>null</code> if there is no poly mesh for this type
     * @author Ocelot
     * @since 1.0.0
     */
    public record Bone(String name, boolean reset2588, boolean neverRender2588, @Nullable String parent, Vector3f pivot,
                       Vector3f rotation, Vector3f bindPoseRotation2588, boolean mirror, float inflate, boolean debug,
                       Cube[] cubes, Locator[] locators, @Nullable PolyMesh polyMesh) {

        @Override
        public String toString() {
            return "Bone{" +
                    "name='" + this.name + '\'' +
                    ", reset2588=" + this.reset2588 +
                    ", neverRender2588=" + this.neverRender2588 +
                    ", parent='" + this.parent + '\'' +
                    ", pivot=" + this.pivot +
                    ", rotation" + this.rotation +
                    ", bindPoseRotation2588" + this.bindPoseRotation2588 +
                    ", mirror=" + this.mirror +
                    ", inflate=" + this.inflate +
                    ", debug=" + this.debug +
                    ", cubes=" + Arrays.toString(this.cubes) +
                    ", locators=" + Arrays.toString(this.locators) +
                    '}';
        }
    }

    /**
     * A single box in the model.
     *
     * @param origin          The un-rotated lower corner of the cube
     * @param size            The amount to extend beyond the origin
     * @param rotation        The degrees to rotate around the pivot
     * @param pivot           The position to pivot rotation around
     * @param overrideInflate Whether this inflate value should be used instead of the bone value
     * @param inflate         The amount to grow in all FaceDirections
     * @param overrideMirror  Whether this mirror value should be used instead of the bone value
     * @param mirror          Whether the cube should be mirrored along the un-rotated x-axis
     * @author Ocelot
     * @since 1.0.0
     */
    public record Cube(Vector3f origin, Vector3f size, Vector3f rotation, Vector3f pivot, boolean overrideInflate,
                       float inflate, boolean overrideMirror, boolean mirror, CubeUV[] uv) {

        /**
         * Fetches the uv for the specified face.
         *
         * @param FaceDirection The FaceDirection of the face to fetch
         * @return The uv for that face or <code>null</code> to skip that face
         */
        @Nullable
        public CubeUV uv(FaceDirection FaceDirection) {
            return this.uv[FaceDirection.get3DDataValue()];
        }

        @Override
        public String toString() {
            return "Cube{" +
                    "origin=" + this.origin +
                    ", size=" + this.size +
                    ", rotation=" + this.rotation +
                    ", pivot=" + this.pivot +
                    ", overrideInflate=" + this.overrideInflate +
                    ", inflate=" + this.inflate +
                    ", overrideMirror=" + this.overrideMirror +
                    ", mirror=" + this.mirror +
                    ", northUV=" + this.uv(FaceDirection.NORTH) +
                    ", eastUV=" + this.uv(FaceDirection.EAST) +
                    ", southUV=" + this.uv(FaceDirection.SOUTH) +
                    ", westUV=" + this.uv(FaceDirection.WEST) +
                    ", upUV=" + this.uv(FaceDirection.UP) +
                    ", downUV=" + this.uv(FaceDirection.DOWN) +
                    '}';
        }
    }

    /**
     * A single UV for a face on a cube.
     *
     * @param materialInstance The material texture to use for this face
     * @author Ocelot
     * @since 1.0.0
     */
    public record CubeUV(float u, float v, float uSize, float vSize, String materialInstance) {

        @Override
        public String toString() {
            return "CubeUV{" +
                    "uv=(" + this.u + "," + this.v + ")" +
                    ", uvSize=(" + this.uSize + "," + this.vSize + ")" +
                    ", materialInstance='" + this.materialInstance + '\'' +
                    '}';
        }
    }

    /**
     * Polygon mesh information for a single bone.
     *
     * @param normalizedUvs Whether UVs should be read from <code>0-1</code> or <code>0-textureWidth/textureHeight</code>
     * @author Ocelot
     * @since 1.0.0
     */
    public record PolyMesh(boolean normalizedUvs, Vector3f[] positions, Vector3f[] normals, Vector2f[] uvs,
                           Polygon[] polys, PolyType polyType) {

        @Override
        public String toString() {
            return "PolyMesh{" +
                    "normalizedUvs=" + this.normalizedUvs +
                    ", positions=" + Arrays.toString(this.positions) +
                    ", normals=" + Arrays.toString(this.normals) +
                    ", uvs=" + Arrays.toString(this.uvs) +
                    ", polys=" + Arrays.toString(this.polys) +
                    ", polyType=" + this.polyType +
                    '}';
        }

        public static class Deserializer implements JsonDeserializer<PolyMesh> {

            private static PolyType parseType(JsonElement json) throws JsonParseException {
                if (!json.isJsonPrimitive())
                    throw new JsonSyntaxException("Expected String, was " + PinwheelGsonHelper.getType(json));
                for (PolyType polyType : PolyType.values())
                    if (polyType.name.equalsIgnoreCase(json.getAsString()))
                        return polyType;
                throw new JsonSyntaxException("Unsupported poly type: " + json.getAsString() + ". Supported poly types: " + Arrays.toString(Arrays.stream(PolyType.values()).map(PolyType::getName).toArray(String[]::new)));
            }

            // Figure out what kinds of polys are being used based on the length of the polys array
            private static PolyType parseType(Polygon[] polys) {
                if (polys.length == 0) {
                    return PolyType.TRIANGLES;
                }
                return polys[0].positions().length == 3 ? PolyType.TRIANGLES : PolyType.QUADS;
            }

            private static <T> T[] parsePositions(JsonObject json, String name, int size, Function<Integer, T[]> arrayGenerator, Function<JsonArray, T> generator) throws JsonParseException {
                JsonArray positionsJson = PinwheelGsonHelper.getAsJsonArray(json, name, null);
                if (positionsJson == null)
                    return arrayGenerator.apply(0);

                T[] positions = arrayGenerator.apply(positionsJson.size());
                for (int i = 0; i < positionsJson.size(); i++) {
                    JsonElement element = positionsJson.get(i);
                    if (!element.isJsonArray())
                        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray, was " + PinwheelGsonHelper.getType(element));

                    JsonArray array = element.getAsJsonArray();
                    if (array.size() != size)
                        throw new JsonParseException("Expected " + size + " " + name + " values, was " + array.size());

                    positions[i] = generator.apply(array);
                }

                return positions;
            }

            @Override
            public PolyMesh deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                boolean normalizedUvs = PinwheelGsonHelper.getAsBoolean(jsonObject, "normalized_uvs", false);
                Vector3f[] positions = parsePositions(jsonObject, "positions", 3, Vector3f[]::new, j -> new Vector3f(j.get(0).getAsFloat(), j.get(1).getAsFloat(), j.get(2).getAsFloat()));
                Vector3f[] normals = parsePositions(jsonObject, "normals", 3, Vector3f[]::new, j -> new Vector3f(j.get(0).getAsFloat(), j.get(1).getAsFloat(), j.get(2).getAsFloat()));
                Vector2f[] uvs = parsePositions(jsonObject, "uvs", 2, Vector2f[]::new, j -> new Vector2f(j.get(0).getAsFloat(), j.get(1).getAsFloat()));

                if (!jsonObject.has("polys"))
                    throw new JsonSyntaxException("Missing polys, expected to find a JsonArray or String");

                JsonElement polysJson = jsonObject.get("polys");
                if (!polysJson.isJsonArray() && !(polysJson.isJsonPrimitive() && polysJson.getAsJsonPrimitive().isString()))
                    throw new JsonSyntaxException("Expected polys to be a JsonArray or String, was " + PinwheelGsonHelper.getType(polysJson));

                Polygon[] polys = polysJson.isJsonArray() ? context.deserialize(polysJson, Polygon[].class) : new Polygon[0];
                PolyType polyType = polysJson.isJsonPrimitive() ? parseType(polysJson) : parseType(polys);

                for (Polygon poly : polys) {
                    if (poly.positions().length != polyType.getVertices())
                        throw new JsonSyntaxException("Expected positions to be of length " + polyType.getVertices() + ". Was " + poly.positions().length);
                    if (poly.normals().length != polyType.getVertices())
                        throw new JsonSyntaxException("Expected normals to be of length " + polyType.getVertices() + ". Was " + poly.normals().length);
                    if (poly.uvs().length != polyType.getVertices())
                        throw new JsonSyntaxException("Expected uvs to be of length " + polyType.getVertices() + ". Was " + poly.uvs().length);
                }

                return new PolyMesh(normalizedUvs, positions, normals, uvs, polys, polyType);
            }
        }
    }

    /**
     * An indexed polygon in a {@link PolyMesh}.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public record Polygon(int[] positions, int[] normals, int[] uvs) {

        @Override
        public String toString() {
            return "Poly{" +
                    "positions=" + Arrays.toString(positions) +
                    ", normals=" + Arrays.toString(normals) +
                    ", uvs=" + Arrays.toString(uvs) +
                    '}';
        }

        public static class Deserializer implements JsonDeserializer<Polygon> {

            private static int[] parseVertex(JsonElement element) throws JsonParseException {
                if (!element.isJsonArray())
                    throw new JsonSyntaxException("Expected vertex to be a JsonArray, was " + PinwheelGsonHelper.getType(element));
                JsonArray array = element.getAsJsonArray();
                if (array.size() != 3)
                    throw new JsonParseException("Expected 3 vertex values, was " + array.size());
                return new int[]{array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt()};
            }

            @Override
            public Polygon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonArray jsonArray = json.getAsJsonArray();
                if (jsonArray.size() != 3 && jsonArray.size() != 4)
                    throw new JsonSyntaxException("Expected 3 or 4 index values, was " + jsonArray.size());

                int[] array1 = parseVertex(jsonArray.get(0));
                int[] array2 = parseVertex(jsonArray.get(1));
                int[] array3 = parseVertex(jsonArray.get(2));
                if (jsonArray.size() == 4) {
                    int[] array4 = parseVertex(jsonArray.get(3));
                    return new Polygon(new int[]{array1[0], array2[0], array3[0], array4[0]}, new int[]{array1[1], array2[1], array3[1], array4[1]}, new int[]{array1[2], array2[2], array3[2], array4[2]});
                }
                return new Polygon(new int[]{array1[0], array2[0], array3[0]}, new int[]{array1[1], array2[1], array3[1]}, new int[]{array1[2], array2[2], array3[2]});
            }
        }
    }

    /**
     * A single marker position inside a bone.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public record Locator(String identifier, Vector3f position) {

        @Override
        public String toString() {
            return "Locator{" +
                    "identifier='" + this.identifier + '\'' +
                    ", position=" + this.position +
                    '}';
        }
    }
}
