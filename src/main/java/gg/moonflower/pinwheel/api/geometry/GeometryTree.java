package gg.moonflower.pinwheel.api.geometry;

import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.geometry.bone.ModelBone;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pinwheel.impl.geometry.EmptyGeometryTree;
import gg.moonflower.pinwheel.impl.geometry.GeometryTreeImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Creates a tree of {@link AnimatedBone} that can be used to access all bones in a model.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface GeometryTree extends LocatorAccess {

    /**
     * Resets all model angles to the default transformation.
     */
    default void resetTransformation() {
        this.getBones().forEach(AnimatedBone::resetTransform);
    }

    /**
     * Copies the model angles of the specified bone to all bones that reference the specified parent.
     *
     * @param parent The name of the parent child bones must reference to be copied
     * @param copy   The bone to copy the angles of
     */
    default void copyAngles(@Nullable String parent, ModelBone copy) {
        for (AnimatedBone bone : this.getBones()) {
            if (Objects.equals(bone.getBone().parent(), parent)) {
                bone.copyTransform(copy);
            }
        }
    }

    /**
     * Updates the locations of all locators in the model.
     */
    default void updateLocators() {
        MatrixStack matrixStack = MatrixStack.create();
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        for (AnimatedBone part : this.getRootBones()) {
            part.updateLocators(matrixStack, this);
        }
    }

    /**
     * Retrieves a bone by name.
     *
     * @param name The name of the bone
     * @return The bone found or <code>null</code> if there is no bone
     */
    @Nullable AnimatedBone getBone(String name);

    /**
     * Retrieves a bone by name.
     *
     * @param name The name of the bone
     * @return The bone found
     */
    default Optional<AnimatedBone> getBoneOptional(String name) {
        return Optional.ofNullable(this.getBone(name));
    }

    /**
     * @return All bones in the tree
     */
    Collection<AnimatedBone> getBones();

    /**
     * @return All bones that all other bones are children of
     */
    Collection<AnimatedBone> getRootBones();

    /**
     * Creates a new geometry tree for the specified model.
     *
     * @param model The model to create a tree for
     * @return A new tree to access all bones in the model
     * @throws GeometryCompileException If there is an issue linking bones to parents or compiling bones
     */
    static GeometryTree create(GeometryModelData model) throws GeometryCompileException {
        GeometryModelData.Description desc = model.description();
        return create(desc.textureWidth(), desc.textureHeight(), model.bones());
    }

    /**
     * Creates a new geometry tree for the specified bones.
     *
     * @param textureWidth  The width of the texture. Used for calculating bone UV
     * @param textureHeight The height of the texture. Used for calculating bone UV
     * @param bones         The bones to create a tree for
     * @return A new tree to access all bones
     * @throws GeometryCompileException If there is an issue linking bones to parents or compiling bones
     */
    static GeometryTree create(int textureWidth, int textureHeight, GeometryModelData.Bone[] bones) throws GeometryCompileException {
        return new GeometryTreeImpl(textureWidth, textureHeight, bones);
    }

    /**
     * @return A geometry tree with no elements
     */
    static GeometryTree empty() {
        return EmptyGeometryTree.INSTANCE;
    }
}
