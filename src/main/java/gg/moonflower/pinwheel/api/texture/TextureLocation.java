package gg.moonflower.pinwheel.api.texture;

import gg.moonflower.pinwheel.impl.texture.LocalTextureLocationImpl;
import gg.moonflower.pinwheel.impl.texture.OnlineTextureLocationImpl;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract version of a location pointing to a texture somewhere. The rendering implementation should use these to figure out where to load and render textures from.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface TextureLocation extends Comparable<TextureLocation> {

    /**
     * The namespace to use if one is not provided.
     */
    String DEFAULT_NAMESPACE = "minecraft";
    /**
     * The default namespace and value separator.
     */
    char DEFAULT_SEPARATOR = ':';

    /**
     * @return The namespace of the texture
     */
    String namespace();

    /**
     * @return The path the texture point to
     */
    String path();

    /**
     * @return Whether the result of {@link #path()} should be treated as a URL to an online resource
     */
    default boolean isOnline() {
        return this instanceof OnlineTextureLocationImpl;
    }

    /**
     * Attempts to create a new texture location by splitting the location by {@link #DEFAULT_SEPARATOR}.
     *
     * @param location The location to split
     * @return A new texture location created or <code>null</code> if creation failed
     */
    static @Nullable TextureLocation tryParseLocal(String location) {
        return local(location);
    }

    /**
     * Creates a new texture location by splitting the location by {@link #DEFAULT_SEPARATOR}.
     *
     * @param location The location to split
     * @return A new texture location created
     */
    static TextureLocation local(String location) {
        return local(location, DEFAULT_SEPARATOR);
    }

    /**
     * Creates a new texture location by splitting the location by the specified separator.
     *
     * @param location  The location to split
     * @param separator The char to split the location on
     * @return A new texture location created
     */
    static TextureLocation local(String location, char separator) {
        int index = location.indexOf(separator);
        String namespace = index >= 1 ? location.substring(0, index) : DEFAULT_NAMESPACE;
        String path = index >= 0 ? location.substring(index + 1) : location;
        return local(namespace, path);
    }

    /**
     * Creates a new texture location with the specified namespace and path.
     *
     * @param namespace The namespace of the location
     * @param path      The path of the location
     * @return A new texture location created
     */
    static TextureLocation local(String namespace, String path) {
        return new LocalTextureLocationImpl(namespace, path);
    }

    /**
     * Creates a new texture location for an online resource.
     *
     * @param url The url to point to
     * @return A new texture location created
     */
    static TextureLocation online(String url) {
        return new OnlineTextureLocationImpl(url);
    }
}
