package gg.moonflower.pinwheel.api.geometry;

/**
 * An exception indicating a {@link GeometryTree} failed to compile.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class GeometryCompileException extends Exception {

    public GeometryCompileException(String message) {
        super(message, null, true, true);
    }

    public GeometryCompileException(Throwable cause) {
        super(null, cause, true, true);
    }
}
