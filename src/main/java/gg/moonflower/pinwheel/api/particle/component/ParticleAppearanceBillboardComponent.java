package gg.moonflower.pinwheel.api.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pinwheel.api.JsonTupleParser;
import gg.moonflower.pinwheel.api.particle.Flipbook;
import gg.moonflower.pinwheel.api.particle.ParticleInstance;
import gg.moonflower.pinwheel.api.particle.ParticleParser;
import gg.moonflower.pinwheel.api.particle.render.SingleQuadRenderProperties;
import gg.moonflower.pinwheel.impl.PinwheelGsonHelper;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Component that specifies the billboard properties of a particle.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public record ParticleAppearanceBillboardComponent(MolangExpression[] size,
                                                   FaceCameraMode cameraMode,
                                                   float minSpeedThreshold,
                                                   @Nullable MolangExpression[] customDirection,
                                                   ParticleAppearanceBillboardComponent.TextureSetter textureSetter) implements ParticleComponent {

    public static final TextureSetter DEFAULT_UV = (particle, environment, properties) -> properties.setUV(0, 0, 1, 1);

    public static ParticleAppearanceBillboardComponent deserialize(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        MolangExpression[] size = JsonTupleParser.getExpression(jsonObject, "size", 2, null);
        FaceCameraMode cameraMode = FaceCameraMode.parseCameraMode(PinwheelGsonHelper.getAsString(jsonObject, "facing_camera_mode"));

        float minSpeedThreshold = 0.01F;
        MolangExpression[] customDirection = null;
        if (jsonObject.has("direction")) {
            JsonObject directionJson = PinwheelGsonHelper.getAsJsonObject(jsonObject, "direction");
            if ("custom_direction".equals(PinwheelGsonHelper.getAsString(directionJson, "mode"))) {
                customDirection = JsonTupleParser.getExpression(directionJson, "direction", 3, () -> new MolangExpression[]{
                        MolangExpression.ZERO,
                        MolangExpression.ZERO,
                        MolangExpression.ZERO
                });
            } else {
                minSpeedThreshold = PinwheelGsonHelper.getAsFloat(directionJson, "min_speed_threshold", minSpeedThreshold);
            }
        }

        TextureSetter textureSetter = ParticleAppearanceBillboardComponent.DEFAULT_UV;
        if (jsonObject.has("uv")) {
            JsonObject uvJson = PinwheelGsonHelper.getAsJsonObject(jsonObject, "uv");
            int textureWidth = PinwheelGsonHelper.getAsInt(uvJson, "texture_width", 1);
            int textureHeight = PinwheelGsonHelper.getAsInt(uvJson, "texture_height", 1);

            if (uvJson.has("flipbook")) {
                Flipbook flipbook = ParticleParser.parseFlipbook(uvJson.get("flipbook"));
                textureSetter = TextureSetter.flipbook(textureWidth, textureHeight, flipbook);
            } else {
                MolangExpression[] uv = JsonTupleParser.getExpression(uvJson, "uv", 2, null);
                MolangExpression[] uvSize = JsonTupleParser.getExpression(uvJson, "uv_size", 2, null);
                textureSetter = TextureSetter.constant(textureWidth, textureHeight, uv, uvSize);
            }
        }

        return new ParticleAppearanceBillboardComponent(size, cameraMode, minSpeedThreshold, customDirection, textureSetter);
    }


    /**
     * The different types of camera transforms for particles.
     *
     * @author Ocelot
     * @since 1.6.0
     */
    public enum FaceCameraMode {
        ROTATE_XYZ("rotate_xyz"),
        ROTATE_Y("rotate_y"),
        LOOK_AT_XYZ("lookat_xyz"),
        LOOK_AT_Y("lookat_y"),
        DIRECTION_X("direction_x"),
        DIRECTION_Y("direction_y"),
        DIRECTION_Z("direction_z"),
        EMITTER_TRANSFORM_XY("emitter_transform_xy"),
        EMITTER_TRANSFORM_XZ("emitter_transform_xz"),
        EMITTER_TRANSFORM_YZ("emitter_transform_yz");

        private final String name;

        FaceCameraMode(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        /**
         * @return The JSON name of this camera mode
         */
        public String getName() {
            return this.name;
        }

        /**
         * Parses a camera mode from the specified name.
         *
         * @param type The type of mode to parse
         * @return The mode found
         * @throws JsonParseException If the mode does not exist
         */
        public static FaceCameraMode parseCameraMode(String type) throws JsonParseException {
            Objects.requireNonNull(type, "type");
            for (FaceCameraMode cameraMode : FaceCameraMode.values()) {
                if (cameraMode.name.equalsIgnoreCase(type)) {
                    return cameraMode;
                }
            }
            throw new JsonSyntaxException("Unsupported camera mode: " + type + ". Supported camera modes: " +
                    Arrays.stream(FaceCameraMode.values())
                            .map(FaceCameraMode::getName)
                            .collect(Collectors.joining(", ")));
        }
    }

    /**
     * Setter for particle UV.
     */
    @FunctionalInterface
    public interface TextureSetter {

        /**
         * Sets the UV coordinate for the specified particle.
         *
         * @param particle    The particle to set the UV of
         * @param environment The environment to evaluate uvs in
         * @param properties  The render properties to set
         */
        void setUV(ParticleInstance particle, MolangEnvironment environment, SingleQuadRenderProperties properties);

        /**
         * Creates a constant UV coordinate.
         *
         * @param textureWidth  The width of the texture
         * @param textureHeight The height of the texture
         * @param uv            The uv
         * @param uvSize        The size of the uv
         * @return A new constant uv setter
         */
        static TextureSetter constant(int textureWidth, int textureHeight, MolangExpression[] uv, MolangExpression[] uvSize) {
            return new ConstantTextureSetter(textureWidth, textureHeight, uv, uvSize);
        }

        /**
         * Creates a texture setter from a flipbook.
         *
         * @param textureWidth  The width of the texture
         * @param textureHeight The height of the texture
         * @param flipbook      The flipbook to animate through
         * @return A new flipbook uv setter
         */
        static TextureSetter flipbook(int textureWidth, int textureHeight, Flipbook flipbook) {
            return new FlipbookTextureSetter(textureWidth, textureHeight, flipbook);
        }
    }

    private record ConstantTextureSetter(int textureWidth,
                                         int textureHeight,
                                         MolangExpression[] uv,
                                         MolangExpression[] uvSize) implements TextureSetter {

        @Override
        public void setUV(ParticleInstance particle, MolangEnvironment environment, SingleQuadRenderProperties properties) {
            float u0 = this.uv[0].safeResolve(environment);
            float v0 = this.uv[1].safeResolve(environment);
            float u1 = u0 + this.uvSize[0].safeResolve(environment);
            float v1 = v0 + this.uvSize[1].safeResolve(environment);
            properties.setUV(u0 / (float) this.textureWidth, v0 / (float) this.textureHeight, u1 / (float) this.textureWidth, v1 / (float) this.textureHeight);
        }
    }

    private record FlipbookTextureSetter(int textureWidth,
                                         int textureHeight,
                                         Flipbook flipbook) implements TextureSetter {

        @Override
        public void setUV(ParticleInstance particle, MolangEnvironment environment, SingleQuadRenderProperties properties) {
            float age = particle.getParticleAge();
            float life = particle.getParticleLifetime();
            properties.setUV(environment, this.textureWidth, this.textureHeight, this.flipbook, age, life);
        }
    }
}
