package gg.moonflower.pinwheel.api;

import gg.moonflower.molangcompiler.api.GlobalMolangCompiler;
import gg.moonflower.molangcompiler.api.MolangCompiler;

/**
 * Retrieves the compiler instance pinwheel should use.
 *
 * @author Ocelot
 * @since 1.1.0
 */
public final class PinwheelMolangCompiler {

    private static MolangCompiler compiler = input -> GlobalMolangCompiler.get().compile(input);

    /**
     * @return The current molang compiler instance
     */
    public static MolangCompiler get() {
        return compiler;
    }

    /**
     * Sets the current molang compiler instance.
     *
     * @param compiler The new compiler to use
     */
    public static void set(MolangCompiler compiler) {
        PinwheelMolangCompiler.compiler = compiler;
    }
}
