package gg.moonflower.pinwheel.impl.transform;

import gg.moonflower.pinwheel.api.transform.MatrixStack;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class JomlMatrixStack implements MatrixStack {

    private final Matrix4fStack positionStack;
    private final Matrix3f normal;
    private boolean dirtyNormal;

    public JomlMatrixStack() {
        this.positionStack = new Matrix4fStack(64);
        this.normal = new Matrix3f();
    }

    @Override
    public void translate(float x, float y, float z) {
        this.positionStack.translate(x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        this.positionStack.scale(x, y, z);
        this.dirtyNormal = true;
    }

    @Override
    public void rotate(Quaternionfc rotation) {
        this.positionStack.rotate(rotation);
        this.dirtyNormal = true;
    }

    @Override
    public void rotate(float amount, float x, float y, float z) {
        this.positionStack.rotate(amount, x, y, z);
        this.dirtyNormal = true;
    }

    @Override
    public void rotateXYZ(float x, float y, float z) {
        this.positionStack.rotateXYZ(x, y, z);
        this.dirtyNormal = true;
    }

    @Override
    public void rotateZYX(float z, float y, float x) {
        this.positionStack.rotateZYX(z, y, x);
        this.dirtyNormal = true;
    }

    @Override
    public void pushMatrix() {
        this.positionStack.pushMatrix();
        this.dirtyNormal = true;
    }

    @Override
    public void popMatrix() {
        this.positionStack.popMatrix();
        this.dirtyNormal = true;
    }

    @Override
    public Matrix4f position() {
        return this.positionStack;
    }

    @Override
    public Matrix3f normal() {
        if (this.dirtyNormal) {
            this.dirtyNormal = false;
            return this.positionStack.normal(this.normal);
        }
        return this.normal;
    }
}
