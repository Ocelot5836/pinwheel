package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.api.PinwheelMolangCompiler;
import gg.moonflower.pinwheel.api.particle.ParticleInstance;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Component that specifies the color of a particle.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleAppearanceTintingComponent(ColorSupplier red,
                                                 ColorSupplier green,
                                                 ColorSupplier blue,
                                                 ColorSupplier alpha) implements ParticleComponent {

    public static ParticleAppearanceTintingComponent deserialize(JsonElement json) throws JsonParseException {
        Objects.requireNonNull(json, "json");
        JsonObject jsonObject = json.getAsJsonObject();
        if (!jsonObject.has("color")) {
            throw new JsonSyntaxException("Missing color, expected to find a JsonObject, JsonArray, string, or float");
        }

        ColorSupplier red;
        ColorSupplier green;
        ColorSupplier blue;
        ColorSupplier alpha;

        JsonElement colorElement = jsonObject.get("color");
        if (colorElement.isJsonObject()) {
            JsonObject colorObject = colorElement.getAsJsonObject();
            if (!colorObject.has("gradient")) {
                throw new JsonSyntaxException("Missing gradient, expected to find a JsonObject or JsonArray");
            }

            MolangExpression interpolant = JsonTupleParser.getExpression(colorObject, "interpolant", null);

            JsonElement gradientElement = colorObject.get("gradient");
            if (gradientElement.isJsonObject()) {
                JsonObject gradientObject = gradientElement.getAsJsonObject();

                List<Pair<Float, ColorSupplier[]>> colors = new ArrayList<>(gradientObject.size());
                float[] times = new float[gradientObject.size()];
                int i = 0;
                for (Map.Entry<String, JsonElement> entry : gradientObject.entrySet()) {
                    try {
                        float time = Float.parseFloat(entry.getKey());
                        times[i++] = time;
                        colors.add(Pair.of(time, parseColor(entry.getValue(), entry.getKey())));
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException("Invalid time: " + entry.getKey(), e);
                    }
                }
                colors.sort((a, b) -> Float.compare(a.getFirst(), b.getFirst()));

                red = ColorSupplier.gradient(interpolant, colors.stream().map(pair -> pair.getSecond()[0]).toArray(ColorSupplier[]::new), times);
                green = ColorSupplier.gradient(interpolant, colors.stream().map(pair -> pair.getSecond()[1]).toArray(ColorSupplier[]::new), times);
                blue = ColorSupplier.gradient(interpolant, colors.stream().map(pair -> pair.getSecond()[2]).toArray(ColorSupplier[]::new), times);
                alpha = ColorSupplier.gradient(interpolant, colors.stream().map(pair -> pair.getSecond()[3]).toArray(ColorSupplier[]::new), times);
            } else if (gradientElement.isJsonArray()) {
                JsonArray gradientArray = gradientElement.getAsJsonArray();

                int count = gradientArray.size();
                ColorSupplier[] colors = new ColorSupplier[count * 4];
                float[] times = new float[count];
                for (int i = 0; i < count; i++) {
                    JsonElement element = gradientArray.get(i);
                    times[i] = (float) i / (float) count;
                    ColorSupplier[] elementColors = parseColor(element, "gradient[" + i + "]");
                    colors[i] = elementColors[0];
                    colors[count + i] = elementColors[1];
                    colors[count * 2 + i] = elementColors[2];
                    colors[count * 3 + i] = elementColors[3];
                }

                red = ColorSupplier.gradient(interpolant, Arrays.copyOfRange(colors, 0, count), times);
                green = ColorSupplier.gradient(interpolant, Arrays.copyOfRange(colors, count, count * 2), times);
                blue = ColorSupplier.gradient(interpolant, Arrays.copyOfRange(colors, count * 2, count * 3), times);
                alpha = ColorSupplier.gradient(interpolant, Arrays.copyOfRange(colors, count * 3, count * 4), times);
            } else {
                throw new JsonSyntaxException("Expected gradient to be a JsonObject or JsonArray, was " + PinwheelGsonHelper.getType(gradientElement));
            }
        } else if (colorElement.isJsonArray() || colorElement.isJsonPrimitive()) {
            ColorSupplier[] colors = parseColor(colorElement, "color");
            red = colors[0];
            green = colors[1];
            blue = colors[2];
            alpha = colors[3];
        } else {
            throw new JsonSyntaxException("Expected color to be a JsonObject, JsonArray, string, or float, was " + PinwheelGsonHelper.getType(colorElement));
        }

        return new ParticleAppearanceTintingComponent(red, green, blue, alpha);
    }

    private static ColorSupplier[] parseColor(@Nullable JsonElement json, String name) throws JsonParseException {
        if (json == null) {
            throw new JsonSyntaxException("Missing " + name + ", expected to find a JsonArray, string, or float");
        }
        if (json.isJsonArray()) {
            JsonArray colorJson = json.getAsJsonArray();
            if (colorJson.size() != 3 && colorJson.size() != 4) {
                throw new JsonSyntaxException("Expected 3 or 4 elements in " + name + ", got " + colorJson.size());
            }

            ColorSupplier[] colors = new ColorSupplier[4];
            if (colorJson.size() == 3) {
                colors[3] = ColorSupplier.constant(1.0F);
            }

            for (int i = 0; i < colorJson.size(); i++) {
                JsonElement colorElement = colorJson.get(i);
                if (!colorElement.isJsonPrimitive()) {
                    throw new JsonSyntaxException("Expected " + name + "[" + i + "] to be a string or float, was " + PinwheelGsonHelper.getType(colorElement));
                }
                if (colorElement.getAsJsonPrimitive().isNumber()) {
                    colors[i] = ColorSupplier.constant(PinwheelGsonHelper.convertToFloat(colorElement, name + "[" + i + "]"));
                } else {
                    try {
                        MolangExpression expression = PinwheelMolangCompiler.get().compile(PinwheelGsonHelper.convertToString(colorElement, name + "[" + i + "]"));
                        colors[i] = ColorSupplier.molang(expression);
                    } catch (Exception e) {
                        throw new JsonSyntaxException("Failed to parse " + name + "[" + i + "]", e);
                    }
                }
            }

            return colors;
        } else if (json.isJsonPrimitive()) {
            String value = PinwheelGsonHelper.convertToString(json, name);
            if (!value.startsWith("#") || value.length() > 9) {
                throw new JsonSyntaxException("Invalid hex color: " + value);
            }

            try {
                int color = Integer.parseUnsignedInt(value.substring(1), 16);
                float red = (float) (color >> 16 & 0xFF) / 255F;
                float green = (float) (color >> 8 & 0xFF) / 255F;
                float blue = (float) (color & 0xFF) / 255F;
                float alpha = value.length() > 7 ? (float) (color >> 24 & 0xFF) / 255F : 1.0F;
                return new ColorSupplier[]{ColorSupplier.constant(red), ColorSupplier.constant(green), ColorSupplier.constant(blue), ColorSupplier.constant(alpha)};
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException("Invalid hex color: " + value, e);
            }
        }

        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray or string, was " + PinwheelGsonHelper.getType(json));
    }

    /**
     * Returns colors for a particle.
     */
    @FunctionalInterface
    public interface ColorSupplier {

        /**
         * Calculates a single color component for the specified particle.
         *
         * @param particle    The particle to calculate color for
         * @param environment The environment to evaluate colors in
         * @return The value of the color component
         */
        float get(ParticleInstance particle, MolangEnvironment environment);

        /**
         * Creates a new color supplier for a single constant.
         *
         * @param value The value of the color
         * @return A new constant color supplier
         */
        static ColorSupplier constant(float value) {
            return new Constant(value);
        }

        /**
         * Creates a new color supplier for a single MoLang expression.
         *
         * @param component The expression to evaluate each time {@link #get(ParticleInstance, MolangEnvironment)} is called
         * @return A new MoLang color supplier
         */
        static ColorSupplier molang(MolangExpression component) {
            return new Molang(component);
        }

        /**
         * Creates a new color supplier that blends between multiple color suppliers at different times.
         *
         * @param interpolant The expression to use as the time input
         * @param colors      The colors to use at each time
         * @param times       The times to use
         * @return A new gradient color supplier
         */
        static ColorSupplier gradient(MolangExpression interpolant,
                                      ColorSupplier[] colors,
                                      float[] times) {
            if (colors.length < 2 || colors.length != times.length) {
                throw new IllegalArgumentException("Colors must equal times and have at least 2 for a gradient");
            }
            return new Gradient(interpolant, colors, times);
        }
    }

    private record Constant(float value) implements ColorSupplier {

        @Override
        public float get(ParticleInstance particle, MolangEnvironment environment) {
            return this.value;
        }
    }

    private record Molang(MolangExpression component) implements ColorSupplier {

        @Override
        public float get(ParticleInstance particle, MolangEnvironment environment) {
            return environment.safeResolve(this.component);
        }
    }

    private record Gradient(MolangExpression interpolant,
                            ColorSupplier[] colors,
                            float[] times) implements ColorSupplier {

        @Override
        public float get(ParticleInstance particle, MolangEnvironment environment) {
            float input = environment.safeResolve(this.interpolant);
            ColorSupplier start = this.colors[0];
            ColorSupplier end = this.colors[1];
            float startTime = this.times[0];
            float endTime = this.times[1];
            for (int i = 0; i < this.colors.length; i++) {
                if (this.times[i] <= input) {
                    start = this.colors[i];
                    end = start;
                    startTime = this.times[i];
                    endTime = 1.0F;
                } else {
                    end = this.colors[i];
                    endTime = this.times[i];
                    break;
                }
            }

            float a = start.get(particle, environment);
            if (startTime == endTime) {
                return a;
            }

            float b = end.get(particle, environment);
            float pct = (input - startTime) / (endTime - startTime);
            return a + (b - a) * pct;
        }
    }
}
