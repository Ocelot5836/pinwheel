package gg.moonflower.pinwheel.api.texture;

/**
 * A key for a {@link ModelTexture} render type.
 *
 * @param layer   The layer to render on
 * @param color   The color to apply
 * @param glowing Whether the texture is "fullbright"
 * @author Ocelot
 * @see ModelTexture
 * @since 1.0.0
 */
public record ModelTextureKey(ModelTexture.TextureLayer layer, int color, boolean glowing) {
}
