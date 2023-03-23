package gg.moonflower.pinwheel.api.geometry;

import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import org.jetbrains.annotations.Nullable;

/**
 * Provides access to the transformations of locators.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface LocatorAccess {

    /**
     * Retrieves the transformations for the specified locator.
     *
     * @param name The name of the locator to get
     * @return All locators in the model
     */
    @Nullable LocatorTransformation getLocatorTransformation(String name);

    /**
     * @return All locators
     */
    GeometryModelData.Locator[] getLocators();
}
