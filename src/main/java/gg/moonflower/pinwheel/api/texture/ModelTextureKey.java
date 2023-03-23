package gg.moonflower.pinwheel.api.texture;

/**
 * A key for a {@link GeometryModelTexture} render type.
 *
 * @param layer   The layer to render on
 * @param color   The color to apply
 * @param glowing Whether the texture is "fullbright"
 * @author Ocelot
 * @see GeometryModelTexture
 * @since 1.0.0
 */
public record ModelTextureKey(GeometryModelTexture.TextureLayer layer, int color, boolean glowing) {
}
