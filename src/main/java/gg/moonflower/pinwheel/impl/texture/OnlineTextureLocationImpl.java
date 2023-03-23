package gg.moonflower.pinwheel.impl.texture;

import gg.moonflower.pinwheel.api.texture.TextureLocation;
import org.jetbrains.annotations.NotNull;

/**
 * An online implementation of {@link TextureLocation}.
 *
 * @param url The URL to point to
 */
public record OnlineTextureLocationImpl(String url) implements TextureLocation {

    @Override
    public int compareTo(@NotNull TextureLocation o) {
        return LocalTextureLocationImpl.COMPARATOR.compare(this, o);
    }

    @Override
    public String namespace() {
        return "online";
    }

    @Override
    public String path() {
        return this.url;
    }
}
