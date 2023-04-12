package gg.moonflower.pinwheel.api.geometry.bone;

import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.LocatorAccess;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pinwheel.impl.geometry.bone.AnimatedBoneImpl;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A model part that uses {@link GeometryModelData.Bone} as the source of geometry.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AnimatedBone extends ModelBone {

    /**
     * Resets the transformation of this part.
     */
    void resetTransform();

    /**
     * @return The bone definition this bone references while rendering
     */
    GeometryModelData.Bone getBone();

    /**
     * @return The pose of this bone for animation
     */
    AnimationPose getAnimationPose();

    /**
     * @return An unmodifiable view of the children in this bone
     */
    Collection<AnimatedBone> getChildren();

    /**
     * @return All locators in this bone
     */
    GeometryModelData.Locator[] getLocators();

    /**
     * Updates all locator positions in this bone and all children.
     *
     * @param matrixStack The stack of matrix transformations to use for calculating transforms
     * @param access      Access to locator transformation data
     */
    void updateLocators(MatrixStack matrixStack, LocatorAccess access);

    /**
     * Add all bones recursively to a collection.
     *
     * @param bones collection to be added to
     */
    void listBones(Collection<AnimatedBone> bones);

    /**
     * Sets whether this bone can be seen.
     *
     * @param visible Whether this bone can be seen
     */
    void setVisible(boolean visible);

    /**
     * @return Whether this bone can be seen
     */
    boolean isVisible();

    /**
     * Creates a new animated bone instance.
     *
     * @param bone          The bone itself
     * @param textureWidth  The width of the texture for dividing bone UV
     * @param textureHeight The height of the texture for dividing bone UV
     * @param children      The children
     * @return A new animated bone instance
     */
    static AnimatedBone create(GeometryModelData.Bone bone, float textureWidth, float textureHeight, List<AnimatedBone> children) {
        return new AnimatedBoneImpl(bone, textureWidth, textureHeight, children);
    }

    /**
     * Creates a new builder from the specified bone definition.
     * Children must be added with {@link Builder#addChild(GeometryModelData.Bone)} or {@link Builder#addChild(Builder)}.
     *
     * @param bone The bone this builder represents
     */
    static Builder bone(GeometryModelData.Bone bone) {
        return new Builder(bone);
    }

    /**
     * Creates a deep copy of the specified builder. All children are copied to allow this builder to add children independently.
     *
     * @param copy The builder to copy
     */
    static Builder bone(Builder copy) {
        return new Builder(copy);
    }

    /**
     * A position, rotation, and scale transformation applied on top of default positions for animations.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    record AnimationPose(Vector3f position, Vector3f rotation, Vector3f scale) {

        public AnimationPose() {
            this(new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));
        }

        /**
         * Resets the transformation for this pose.
         */
        public void identity() {
            this.position.set(0, 0, 0);
            this.rotation.set(0, 0, 0);
            this.scale.set(1, 1, 1);
        }

        /**
         * @return Whether this pose is set to the identity transform
         */
        public boolean isIdentity() {
            return this.position.lengthSquared() == 0 && this.rotation.lengthSquared() == 0 && this.scale.lengthSquared() == 1;
        }

        /**
         * Applies additional transformations that can be dynamically changed.
         *
         * @param x         The x offset
         * @param y         The y offset
         * @param z         The z offset
         * @param rotationX The x rotation offset in degrees
         * @param rotationY The y rotation offset in degrees
         * @param rotationZ The z rotation offset in degrees
         * @param scaleX    The x factor
         * @param scaleY    The y factor
         * @param scaleZ    The z factor
         */
        public void add(float x, float y, float z, float rotationX, float rotationY, float rotationZ, float scaleX, float scaleY, float scaleZ) {
            this.position.add(x, y, z);
            this.rotation.add(rotationX, rotationY, rotationZ);
            this.scale.add(scaleX, scaleY, scaleZ);
        }
    }

    /**
     * Creates animated bone trees from bone definitions easily. Also validates there are no circular references in bone trees.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    class Builder {

        private final GeometryModelData.Bone bone;
        private final List<Builder> children;

        private Builder(GeometryModelData.Bone bone) {
            this.bone = Objects.requireNonNull(bone, "bone");
            this.children = new ArrayList<>();
        }

        private Builder(Builder copy) {
            Objects.requireNonNull(copy, "copy");
            this.bone = copy.bone;
            this.children = copy.children.stream().map(Builder::new).collect(Collectors.toList());
        }

        /**
         * Adds the specified bone as a child of this bone. Creates a {@link Builder} automatically if no children are required.
         *
         * @param child The child to add
         */
        public Builder addChild(GeometryModelData.Bone child) {
            return this.addChild(AnimatedBone.bone(child));
        }

        /**
         * Adds the specified bone as a child of this bone.
         *
         * @param child The child to add
         */
        public Builder addChild(Builder child) {
            Objects.requireNonNull(child, "child");
            this.children.add(child);
            return this;
        }

        /**
         * @return The bone definition this builder represents
         */
        public GeometryModelData.Bone getBone() {
            return this.bone;
        }

        private void validate() throws GeometryCompileException {
            Set<GeometryModelData.Bone> checked = new HashSet<>();
            List<Builder> children = new ArrayList<>(this.children);
            while (!children.isEmpty()) {
                Builder child = children.remove(0);
                if (!checked.add(child.bone)) {
                    throw new GeometryCompileException("Circular reference in bone: " + child.bone);
                }

                children.addAll(child.children);
            }
        }

        private AnimatedBone createUnsafe(float textureWidth, float textureHeight) {
            List<AnimatedBone> children = new ArrayList<>(this.children.size());
            for (Builder child : this.children) {
                children.add(child.createUnsafe(textureWidth, textureHeight));
            }
            return AnimatedBone.create(this.bone, textureWidth, textureHeight, children);
        }

        /**
         * Validates all children are valid and have no circular references and creates this bone and all children.
         * This bone will be considered the root bone and all children will be descendants of this bone.
         *
         * @param textureWidth  The width of the texture. Used for calculating bone UV
         * @param textureHeight The height of the texture. Used for calculating bone UV
         * @return A new bone with all children assigned
         * @throws IllegalStateException If there is a circular reference in children
         */
        public AnimatedBone create(float textureWidth, float textureHeight) throws GeometryCompileException {
            this.validate();
            return this.createUnsafe(textureWidth, textureHeight);
        }
    }
}
