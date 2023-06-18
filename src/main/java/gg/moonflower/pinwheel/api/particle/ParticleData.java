package gg.moonflower.pinwheel.api.particle;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.api.particle.component.ParticleComponent;
import gg.moonflower.pinwheel.api.texture.ModelTexture;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Data about a particle from the Bedrock specs.
 *
 * @param description Basic info about the particle
 * @param curves      Custom curves to evaluate
 * @param events      Custom defined event listeners for when events are fired
 * @param components  All components in the particle
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleData(Description description,
                           Map<String, Curve> curves,
                           Map<String, ParticleEvent> events,
                           Map<String, ParticleComponent> components) {

    public static final ParticleData EMPTY = new ParticleData(new Description("empty", ModelTexture.MISSING, null), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());

    /**
     * The different types of curves for calculating particle variables.
     *
     * @author Ocelot
     */
    public enum CurveType {

        LINEAR, BEZIER, BEZIER_CHAIN, CATMULL_ROM

    }

    /**
     * Information about the particle.
     *
     * @param identifier The identifier of this model. Used to refer to this particle definition
     * @param texture    The texture to use if material is null
     * @param material   The material to use or <code>null</code>
     * @author Ocelot
     */
    public record Description(String identifier, ModelTexture texture, @Nullable String material) {

        public static class Deserializer implements JsonDeserializer<Description> {

            @Override
            public Description deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = PinwheelGsonHelper.convertToJsonObject(json, "description");
                String identifier = PinwheelGsonHelper.getAsString(jsonObject, "identifier");
                JsonObject basicRenderParams = PinwheelGsonHelper.getAsJsonObject(jsonObject, "basic_render_parameters");

                DataResult<ModelTexture> texture;
                if (basicRenderParams.has("texture")) {
                    texture = ModelTexture.CODEC.parse(JsonOps.INSTANCE, basicRenderParams.get("texture"));
                    if (texture.error().isPresent()) {
                        throw new JsonSyntaxException(texture.error().get().message());
                    }
                } else {
                    texture = DataResult.success(ModelTexture.MISSING);
                }
                String material = PinwheelGsonHelper.getAsString(basicRenderParams, "material", null);

                return new Description(identifier, texture.result().orElseThrow(), material);
            }
        }
    }

    /**
     * @param type            The type of curve to use
     * @param nodes           The node inputs
     * @param input           The value to use as input into nodes. For example, <code>v.particle_age/v.particle_lifetime</code> would result in an input from <code>0</code> to <code>1</code> over the particle's lifetime
     * @param horizontalRange The range input is mapped to. From <code>0</code> to this value. <b><i>Note: This field is considered deprecated and optional</i></b>
     */
    public record Curve(CurveType type,
                        CurveNode[] nodes,
                        MolangExpression input,
                        MolangExpression horizontalRange) {

        @Override
        public String toString() {
            return "Curve[type=" + this.type +
                    ", nodes=" + Arrays.toString(this.nodes) +
                    ", input=" + this.input +
                    ", horizontalRange=" + this.horizontalRange + "]";
        }

        public static class Deserializer implements JsonDeserializer<Curve> {

            private static CurveType parseType(JsonElement json) throws JsonParseException {
                if (!json.isJsonPrimitive()) {
                    throw new JsonSyntaxException("Expected String, was " + PinwheelGsonHelper.getType(json));
                }
                for (CurveType curveType : CurveType.values()) {
                    if (curveType.name().equalsIgnoreCase(json.getAsString())) {
                        return curveType;
                    }
                }
                throw new JsonSyntaxException("Unsupported curve type: " + json.getAsString() + ". Supported curve types: " +
                        Arrays.stream(CurveType.values())
                                .map(type -> type.name().toLowerCase(Locale.ROOT))
                                .collect(Collectors.joining(", ")));
            }

            private static CurveNode[] parseNodes(JsonElement json, CurveType type) {
                if (json.isJsonArray()) {
                    if (type == CurveType.BEZIER_CHAIN) {
                        throw new JsonSyntaxException("Bezier Chain expected JsonObject, was " + PinwheelGsonHelper.getType(json));
                    }

                    JsonArray array = PinwheelGsonHelper.convertToJsonArray(json, "nodes");
                    CurveNode[] nodes = new CurveNode[array.size()];
                    int offset = type == CurveType.CATMULL_ROM ? 1 : 0;
                    for (int i = 0; i < nodes.length; i++) {
                        float time = (float) Math.max(i - offset, 0) / (float) (nodes.length - offset * 2 - 1);
                        MolangExpression value = JsonTupleParser.parseExpression(array.get(i), "nodes[" + i + "]");
                        nodes[i] = new CurveNode(time, value);
                    }
                    return nodes;
                } else if (json.isJsonObject()) {
                    JsonObject jsonObject = PinwheelGsonHelper.convertToJsonObject(json, "nodes");
                    List<CurveNode> curveNodes = new ArrayList<>(jsonObject.entrySet().size());
                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        try {
                            float time = Float.parseFloat(entry.getKey());
                            JsonObject nodeJson = PinwheelGsonHelper.convertToJsonObject(entry.getValue(), entry.getKey());

                            if (type == CurveType.BEZIER_CHAIN) {
                                boolean singleValue = nodeJson.has("value");
                                boolean singleSlope = nodeJson.has("slope");
                                if (singleValue && (nodeJson.has("left_value") || nodeJson.has("right_value"))) {
                                    throw new JsonSyntaxException("left_value and right_value must not be present with value");
                                }
                                if (singleSlope && (nodeJson.has("left_slope") || nodeJson.has("right_slope"))) {
                                    throw new JsonSyntaxException("left_slope and right_slope must not be present with slope");
                                }

                                MolangExpression leftValue = singleValue ? JsonTupleParser.parseExpression(nodeJson.get("value"), "value") : JsonTupleParser.parseExpression(nodeJson.get("left_value"), "left_value");
                                MolangExpression rightValue = singleValue ? leftValue : JsonTupleParser.parseExpression(nodeJson.get("right_value"), "right_value");
                                MolangExpression leftSlope = singleSlope ? JsonTupleParser.parseExpression(nodeJson.get("slope"), "slope") : JsonTupleParser.parseExpression(nodeJson.get("left_slope"), "left_slope");
                                MolangExpression rightSlope = singleSlope ? leftSlope : JsonTupleParser.parseExpression(nodeJson.get("right_slope"), "right_slope");
                                curveNodes.add(new BezierChainCurveNode(time, leftValue, rightValue, leftSlope, rightSlope));
                            } else {
                                MolangExpression value = JsonTupleParser.parseExpression(nodeJson.get("value"), "value");
                                curveNodes.add(new CurveNode(time, value));
                            }
                        } catch (NumberFormatException e) {
                            throw new JsonParseException("Failed to parse nodes at time '" + entry.getKey() + "'", e);
                        }
                    }
                    if (type == CurveType.BEZIER && curveNodes.size() != 4) {
                        throw new JsonSyntaxException("Bezier expected 4 nodes, had " + curveNodes.size());
                    }
                    curveNodes.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
                    return curveNodes.toArray(CurveNode[]::new);
                }
                throw new JsonSyntaxException("Expected nodes to be a JsonArray or JsonObject, was " + PinwheelGsonHelper.getType(json));
            }

            @Override
            public Curve deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = PinwheelGsonHelper.convertToJsonObject(json, "curve");
                CurveType type = parseType(jsonObject.get("type"));
                CurveNode[] curves = jsonObject.has("nodes") ? parseNodes(jsonObject.get("nodes"), type) : new CurveNode[0];
                MolangExpression input = JsonTupleParser.getExpression(jsonObject, "input", null);
                MolangExpression horizontalRange = JsonTupleParser.getExpression(jsonObject, "horizontal_range", () -> MolangExpression.of(1.0F));
                return new Curve(type, curves, input, horizontalRange);
            }
        }
    }

    /**
     * A node in a {@link Curve}. Used to define most types of curves.
     */
    public static class CurveNode {

        private final float time;
        private final MolangExpression value;

        public CurveNode(float time, MolangExpression value) {
            this.time = time;
            this.value = value;
        }

        public float getTime() {
            return this.time;
        }

        public MolangExpression getValue() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CurveNode curveNode = (CurveNode) o;
            return Float.compare(curveNode.time, this.time) == 0 &&
                    this.value.equals(curveNode.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.time, this.value);
        }

        @Override
        public String toString() {
            return "CurveNode[" +
                    "time=" + this.time +
                    ", value=" + this.value + ']';
        }
    }

    /**
     * A node in a {@link Curve}. Used to define bezier chains specifically.
     */
    public static class BezierChainCurveNode extends CurveNode {

        private final MolangExpression leftValue;
        private final MolangExpression rightValue;
        private final MolangExpression leftSlope;
        private final MolangExpression rightSlope;

        public BezierChainCurveNode(float time, MolangExpression leftValue, MolangExpression rightValue, MolangExpression leftSlope, MolangExpression rightSlope) {
            super(time, leftValue);
            this.leftValue = leftValue;
            this.rightValue = rightValue;
            this.leftSlope = leftSlope;
            this.rightSlope = rightSlope;
        }

        public MolangExpression getLeftValue() {
            return this.leftValue;
        }

        public MolangExpression getRightValue() {
            return this.rightValue;
        }

        public MolangExpression getLeftSlope() {
            return this.leftSlope;
        }

        public MolangExpression getRightSlope() {
            return this.rightSlope;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            BezierChainCurveNode that = (BezierChainCurveNode) o;
            return this.leftValue.equals(that.leftValue) &&
                    this.rightValue.equals(that.rightValue) &&
                    this.leftSlope.equals(that.leftSlope) &&
                    this.rightSlope.equals(that.rightSlope);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.leftValue, this.rightValue, this.leftSlope, this.rightSlope);
        }

        @Override
        public String toString() {
            return "BezierChainCurveNode{" +
                    "time=" + this.getTime() +
                    ", leftValue=" + this.leftValue +
                    ", rightValue=" + this.rightValue +
                    ", leftSlope=" + this.leftSlope +
                    ", rightSlope=" + this.rightSlope +
                    '}';
        }
    }

    public static class Deserializer implements JsonDeserializer<ParticleData> {

        @Override
        public ParticleData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final Description description = context.deserialize(jsonObject.get("description"), Description.class);

            ImmutableMap.Builder<String, Curve> curves = ImmutableMap.builder();
            if (jsonObject.has("curves")) {
                JsonObject curvesJson = PinwheelGsonHelper.getAsJsonObject(jsonObject, "curves");
                for (Map.Entry<String, JsonElement> entry : curvesJson.entrySet()) {
                    String key = entry.getKey();
                    if (!key.startsWith("variable.") && !key.startsWith("v.")) {
                        throw new JsonSyntaxException(key + " is not a valid MoLang variable name");
                    }
                    curves.put(key.split("\\.", 2)[1], context.deserialize(entry.getValue(), Curve.class));
                }
            }

            ImmutableMap.Builder<String, ParticleEvent> events = ImmutableMap.builder();
            if (jsonObject.has("events")) {
                JsonObject eventsJson = PinwheelGsonHelper.getAsJsonObject(jsonObject, "events");
                for (Map.Entry<String, JsonElement> entry : eventsJson.entrySet()) {
                    events.put(entry.getKey(), context.deserialize(entry.getValue(), ParticleEvent.class));
                }
            }

            Map<String, ParticleComponent> components;
            if (jsonObject.has("components")) {
                JsonObject eventsJson = PinwheelGsonHelper.getAsJsonObject(jsonObject, "components");
                components = ParticleComponentParser.getInstance().deserialize(eventsJson);
            } else {
                components = Collections.emptyMap();
            }

            return new ParticleData(description, curves.build(), events.build(), components);
        }
    }
}
