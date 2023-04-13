package gg.moonflower.pinwheel.impl.texture;

import gg.moonflower.pinwheel.api.texture.TextureLocation;

/**
 * An online implementation of {@link TextureLocation}.
 *
 * @param url The URL to point to
 */
public record OnlineTextureLocationImpl(String url) implements TextureLocation {

    @Override
    public int compareTo(TextureLocation o) {
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
