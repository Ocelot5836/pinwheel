package gg.moonflower.pinwheel.impl.texture;

import gg.moonflower.pinwheel.api.texture.TextureLocation;

import java.util.Comparator;

/**
 * Basic implementation of {@link TextureLocation}.
 *
 * @param namespace The namespace of the texture
 * @param path      The path of the texture
 * @author Ocelot
 * @since 1.0.0
 */
public record LocalTextureLocationImpl(String namespace, String path) implements TextureLocation {

    static final Comparator<? super TextureLocation> COMPARATOR = Comparator.comparing(TextureLocation::path).thenComparing(TextureLocation::namespace);

    @Override
    public int compareTo(TextureLocation o) {
        return COMPARATOR.compare(this, o);
    }
}
