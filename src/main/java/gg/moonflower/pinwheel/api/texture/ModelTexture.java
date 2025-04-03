package gg.moonflower.pinwheel.api.texture;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

/**
 * A single texture layer that is part of a {@link TextureTable}.
 *
 * @param type     The type of texture this is. This allows for local and online texture sources
 * @param layer    The layer this texture should use. This is for using built-in render types
 * @param data     The additional data of this texture. Can be a URL string depending on {@link #type()}
 * @param color    The color to tint multiply the texture by when rendering
 * @param glowing  Whether to ignore lighting, e.g. whether this texture should be "fullbright"
 * @param location The name of the sprite in the atlas
 * @author Ocelot
 * @see Builder
 * @since 1.0.0
 */
public record ModelTexture(Type type,
                           TextureLayer layer,
                           String data,
                           int color,
                           boolean glowing,
                           TextureLocation location,
                           ModelTextureKey key) {

    public static final ModelTexture MISSING = ModelTexture.texture().build();

    private static final Codec<ModelTexture> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Type::byName, type -> type.name().toLowerCase(Locale.ROOT))
                    .optionalFieldOf("type", Type.LOCATION).forGetter(ModelTexture::type),
            Codec.STRING.xmap(TextureLayer::byName, type -> type.name().toLowerCase(Locale.ROOT))
                    .optionalFieldOf("layer", TextureLayer.CUTOUT_CULL).forGetter(ModelTexture::layer),
            Codec.STRING.fieldOf("texture").forGetter(ModelTexture::data),
            Codec.STRING.optionalFieldOf("color", "0xFFFFFF")
                    .xmap(color -> Integer.parseUnsignedInt(color.substring(2), 16), color -> "0x" + Integer.toHexString(color & 0xFFFFFF)
                            .toUpperCase(Locale.ROOT))
                    .forGetter(ModelTexture::color),
            Codec.BOOL.optionalFieldOf("glowing", false).forGetter(ModelTexture::glowing)
    ).apply(instance, ModelTexture::new));
    private static final Codec<ModelTexture> LOCAL_CODEC = Codec.STRING.xmap(
            location -> new ModelTexture(Type.LOCATION, TextureLayer.CUTOUT_CULL, location, 0xFFFFFF, false),
            ModelTexture::data);

    public static final Codec<ModelTexture> CODEC = Codec.either(LOCAL_CODEC, FULL_CODEC)
            .xmap(either -> either.map(left -> left, right -> right), Either::left);

    @ApiStatus.Internal
    public ModelTexture(Type type,
                        TextureLayer layer,
                        String data,
                        int color,
                        boolean glowing) {
        this(type,
                layer,
                data,
                color,
                glowing,
                type.createLocation(data),
                new ModelTextureKey(layer, color, glowing));
    }

    @ApiStatus.Internal
    public ModelTexture {
    }

    /**
     * @return The red color factor of this texture
     */
    public float red() {
        return ((this.color >> 16) & 0xff) / 255f;
    }

    /**
     * @return The green color factor of this texture
     */
    public float green() {
        return ((this.color >> 8) & 0xff) / 255f;
    }

    /**
     * @return The blue color factor of this texture
     */
    public float blue() {
        return (this.color & 0xff) / 255f;
    }

    /**
     * @return A new builder for constructing a texture
     */
    public static Builder texture() {
        return new Builder();
    }

    /**
     * @param texture The texture to start with
     * @return A new builder for constructing a texture
     */
    public static Builder texture(ModelTexture texture) {
        return new Builder(texture);
    }

    /**
     * A type of {@link ModelTexture}.
     *
     * @author Ocelot
     */
    public enum Type {

        UNKNOWN(location -> TextureLocation.local("missingno")),
        LOCATION(TextureLocation::tryParseLocal),
        ONLINE(TextureLocation::online);

        private final Function<String, TextureLocation> locationGenerator;

        Type(Function<String, TextureLocation> locationGenerator) {
            this.locationGenerator = locationGenerator;
        }

        /**
         * Fetches a type of texture by the specified name.
         *
         * @param name The name of the type of texture
         * @return The type by that name or {@link #UNKNOWN} if there is no type by that name
         */
        public static Type byName(String name) {
            for (Type type : Type.values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return Type.UNKNOWN;
        }

        /**
         * Creates a new key based on the specified data string.
         *
         * @param data The data to convert
         * @return The new location for that data
         */
        public @Nullable TextureLocation createLocation(String data) {
            return this.locationGenerator.apply(data);
        }
    }

    /**
     * Supported render types for textures.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public enum TextureLayer {

        SOLID, SOLID_CULL, CUTOUT, CUTOUT_CULL, TRANSLUCENT, TRANSLUCENT_CULL;

        /**
         * Fetches a texture layer by the specified name.
         *
         * @param name The name of the texture layer
         * @return The texture layer by that name or {@link #CUTOUT} if there is no layer by that name
         */
        public static TextureLayer byName(String name) {
            for (TextureLayer layer : TextureLayer.values()) {
                if (layer.name().equalsIgnoreCase(name)) {
                    return layer;
                }
            }
            return TextureLayer.CUTOUT;
        }
    }

    /**
     * Constructs new geometry model textures.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Builder {

        private Type type;
        private TextureLayer layer;
        private String data;
        private int color;
        private boolean glowing;

        private Builder() {
            this.type = Type.UNKNOWN;
            this.layer = TextureLayer.SOLID;
            this.data = "";
            this.color = -1;
            this.glowing = false;
        }

        private Builder(ModelTexture texture) {
            this.type = texture.type();
            this.layer = texture.layer();
            this.data = texture.data();
            this.color = texture.color();
            this.glowing = texture.glowing();
        }

        /**
         * Sets the texture to a local location to a file.
         *
         * @param texture The location of the texture
         */
        public Builder setTextureLocation(TextureLocation texture) {
            this.data = texture.toString();
            this.type = Type.LOCATION;
            return this;
        }

        /**
         * Sets the texture to be an online URL resource.
         *
         * @param url The URL to the texture
         */
        public Builder setTextureOnline(String url) {
            this.data = url;
            this.type = Type.ONLINE;
            return this;
        }

        /**
         * Sets the layer for the texture to render in
         *
         * @param layer The layer to draw in
         */
        public Builder setTextureLayer(TextureLayer layer) {
            this.layer = layer;
            return this;
        }

        /**
         * Sets the color to tint this texture to.
         *
         * @param color The new color
         */
        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        /**
         * Sets whether this texture should render "fullbright".
         *
         * @param glowing Whether this should ignore lighting
         */
        public Builder setGlowing(boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        /**
         * @return A new texture with all the properties defined
         */
        public ModelTexture build() {
            return new ModelTexture(this.type, this.layer, this.data, this.color, this.glowing);
        }
    }
}
