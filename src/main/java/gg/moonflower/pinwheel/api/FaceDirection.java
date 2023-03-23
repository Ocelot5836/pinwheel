package gg.moonflower.pinwheel.api;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Locale;

/**
 * The six directions a face can be textured in for cubes.
 *
 * @since 1.0.0
 */
public enum FaceDirection {

    DOWN(0, -1, 0),
    UP(0, 1, 0),
    NORTH(0, 0, -1),
    SOUTH(0, 0, 1),
    WEST(-1, 0, 0),
    EAST(1, 0, 0);

    private final int data3d;
    private final String name;
    private final Vector3fc normal;

    FaceDirection(float x, float y, float z) {
        this.data3d = this.ordinal();
        this.name = this.name().toLowerCase(Locale.ROOT);
        this.normal = new Vector3f(x, y, z);
    }

    /**
     * @return The 3D data of this direction
     */
    public int get3DDataValue() {
        return this.data3d;
    }

    /**
     * @return The name of this direction
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The normal vector for this face
     */
    public Vector3fc normal() {
        return this.normal;
    }

    /**
     * @return The direction opposite to this direction
     */
    public FaceDirection opposite() {
        return switch (this) {
            case DOWN -> UP;
            case UP -> DOWN;
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
        };
    }
}
