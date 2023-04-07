package gg.moonflower.pinwheel.impl.geometry;

import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.GeometryTree;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class GeometryTreeImpl implements GeometryTree {

    private static final String PARENT = "parent.";

    private final Map<String, LocatorTransformation> locatorTransformations;
    private final GeometryModelData.Locator[] locators;
    private final Map<String, AnimatedBone> bones;
    private final Set<AnimatedBone> rootBones;

    public GeometryTreeImpl(int textureWidth, int textureHeight, GeometryModelData.Bone[] bones) throws GeometryCompileException {
        this.locatorTransformations = Arrays.stream(bones).flatMap(bone -> Arrays.stream(bone.locators()))
                .map(LocatorTransformation::new)
                .collect(Collectors.toMap(t -> t.locator().identifier(), t -> t));
        this.locators = Arrays.stream(bones).flatMap(bone -> Arrays.stream(bone.locators())).toArray(GeometryModelData.Locator[]::new);

        if (bones.length == 0) {
            this.bones = Collections.emptyMap();
            this.rootBones = Collections.emptySet();
            return;
        }

        Map<String, AnimatedBone.Builder> boneBuilders = Arrays.stream(bones).collect(Collectors.toMap(GeometryModelData.Bone::name, AnimatedBone::bone));
        Set<String> rootBones = new HashSet<>();

        // Figure out what bones are the children of what other bones
        List<GeometryModelData.Bone> unprocessedBones = new ArrayList<>(Arrays.asList(bones));
        while (!unprocessedBones.isEmpty()) {
            GeometryModelData.Bone bone = unprocessedBones.remove(0);
            String name = bone.name();
            String parent = bone.parent();

            if (parent == null || parent.startsWith(PARENT)) {
                // There is no parent, so it must be a root node
                rootBones.add(name);
                continue;
            }

            if (!boneBuilders.containsKey(parent)) {
                throw new GeometryCompileException("Unknown bone: " + parent);
            }

            boneBuilders.get(parent).addChild(boneBuilders.get(name));
        }

        // Compile all bones
        Map<String, AnimatedBone> compiledBones = new HashMap<>();
        for (Map.Entry<String, AnimatedBone.Builder> entry : boneBuilders.entrySet()) {
            compiledBones.put(entry.getKey(), entry.getValue().create(textureWidth, textureHeight));
        }
        this.bones = Collections.unmodifiableMap(compiledBones);
        this.rootBones = rootBones.stream().map(this.bones::get).collect(Collectors.toSet());
    }

    @Override
    public @Nullable AnimatedBone getBone(String name) {
        return this.bones.get(name);
    }

    @Override
    public Collection<AnimatedBone> getBones() {
        return this.bones.values();
    }

    @Override
    public Collection<AnimatedBone> getRootBones() {
        return this.rootBones;
    }

    @Override
    public @Nullable LocatorTransformation getLocatorTransformation(@NotNull String name) {
        return this.locatorTransformations.get(name);
    }

    @Override
    public GeometryModelData.Locator[] getLocators() {
        return this.locators;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeometryTreeImpl that = (GeometryTreeImpl) o;
        return this.bones.equals(that.bones);
    }

    @Override
    public int hashCode() {
        return this.bones.hashCode();
    }

    @Override
    public String toString() {
        return "GeometryTreeImpl{bones=" + this.bones + "}";
    }
}
