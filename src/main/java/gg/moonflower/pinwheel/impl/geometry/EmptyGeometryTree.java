package gg.moonflower.pinwheel.impl.geometry;

import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.GeometryTree;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public enum EmptyGeometryTree implements GeometryTree {
    INSTANCE;

    @Override
    public @Nullable AnimatedBone getBone(String name) {
        return null;
    }

    @Override
    public Collection<AnimatedBone> getBones() {
        return List.of();
    }

    @Override
    public Collection<AnimatedBone> getRootBones() {
        return List.of();
    }

    @Override
    public @Nullable LocatorTransformation getLocatorTransformation(String name) {
        return null;
    }

    @Override
    public GeometryModelData.Locator[] getLocators() {
        return new GeometryModelData.Locator[0];
    }
}
