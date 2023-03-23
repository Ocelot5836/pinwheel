package gg.moonflower.pinwheel.api.texture;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A table of textures to be used for geometry and particle rendering.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class TextureTable {

    public static final Codec<TextureTable> CODEC =
            Codec.unboundedMap(Codec.STRING,
                            Codec.either(
                                    GeometryModelTexture.CODEC.listOf().xmap(
                                            list -> list.toArray(new GeometryModelTexture[0]),
                                            Arrays::asList),
                                    GeometryModelTexture.CODEC.xmap(
                                            texture -> new GeometryModelTexture[]{texture},
                                            array -> array.length > 0 ? array[0] : GeometryModelTexture.MISSING)
                            ).xmap(either -> either.left().orElseGet(() -> either.right().orElseThrow()),
                                    array -> array.length > 1 ?
                                            Either.left(array) :
                                            Either.right(array))) // Left is multiple layers, right is one layer
                    .xmap(TextureTable::new, table -> table.textures);
    public static TextureTable EMPTY = new TextureTable(new HashMap<>());
    private static final GeometryModelTexture[] MISSING = new GeometryModelTexture[]{GeometryModelTexture.MISSING};

    private final Map<String, GeometryModelTexture[]> textures;
    private final Set<ModelTextureKey> textureKeys;

    /**
     * Creates a new texture table with the specified keys and layers.
     *
     * @param textures The map of texture to layers.
     *                 Layers draw the same mesh data multiple times with different textures
     */
    public TextureTable(Map<String, GeometryModelTexture[]> textures) {
        this.textures = new HashMap<>(textures);
        this.textures.values().removeIf(layers -> layers.length == 0);
        this.textureKeys = this.textures.values().stream()
                .flatMap(Arrays::stream)
                .map(GeometryModelTexture::key)
                .collect(Collectors.toSet());
    }

    /**
     * Checks if the specified texture key exists.
     *
     * @param key The key to check
     * @return Whether that texture exists
     */
    public boolean hasTexture(String key) {
        return this.textures.containsKey(key);
    }

    /**
     * Fetches a geometry model texture by the specified key.
     *
     * @param key The key of the textures to get
     * @return The texture with that key or {@link GeometryModelTexture#MISSING} if there is no texture bound to that key
     */
    public GeometryModelTexture[] getLayerTextures(String key) {
        return this.textures.getOrDefault(key, MISSING);
    }

    /**
     * @return All definitions for textures
     */
    public Map<String, GeometryModelTexture[]> getTextureDefinitions() {
        return this.textures;
    }

    /**
     * @return All texture keys used in this table
     */
    public Set<ModelTextureKey> getTextureKeys() {
        return this.textureKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TextureTable that = (TextureTable) o;
        return this.textures.equals(that.textures);
    }

    @Override
    public int hashCode() {
        return this.textures.hashCode();
    }

    @Override
    public String toString() {
        return "TextureTable{" +
                "textures=" +
                this.textures.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                        .collect(Collectors.joining(", ")) +
                '}';
    }
}
