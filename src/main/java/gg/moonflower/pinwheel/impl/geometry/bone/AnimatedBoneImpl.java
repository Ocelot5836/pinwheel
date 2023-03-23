package gg.moonflower.pinwheel.impl.geometry.bone;

import gg.moonflower.pinwheel.api.FaceDirection;
import gg.moonflower.pinwheel.api.geometry.*;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.geometry.bone.ModelBone;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.jetbrains.annotations.ApiStatus;
import org.joml.*;

import java.lang.Math;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class AnimatedBoneImpl implements AnimatedBone {

    private static final MatrixStack TRANSFORM_MATRIX = MatrixStack.create();

    private final GeometryModelData.Bone bone;
    private final float textureWidth;
    private final float textureHeight;

    private final Collection<AnimatedBone> children;
    private final ObjectList<Polygon> polygons;

    private final Vector3f pivot;
    private final Vector3f rotation;
    private final Matrix4f copyPosition;
    private final Matrix3f copyNormal;
    private final AnimatedBone.AnimationPose animationPose;
    private boolean copyVanilla;

    public AnimatedBoneImpl(GeometryModelData.Bone bone, float textureWidth, float textureHeight, List<AnimatedBone> children) {
        this.bone = bone;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.children = Collections.unmodifiableList(children);
        this.polygons = new ObjectArrayList<>();
        this.pivot = new Vector3f();
        this.rotation = new Vector3f();
        this.copyPosition = new Matrix4f();
        this.copyNormal = new Matrix3f();
        this.animationPose = new AnimatedBone.AnimationPose();
        this.resetTransform();
        Arrays.stream(bone.cubes()).forEach(this::addCube);
        GeometryModelData.PolyMesh polyMesh = bone.polyMesh();
        if (polyMesh != null) {
            this.addPolyMesh(polyMesh);
        }
    }

    private void addCube(GeometryModelData.Cube cube) {
        boolean empty = true;
        for (FaceDirection direction : FaceDirection.values()) {
            if (cube.uv(direction) != null) {
                empty = false;
                break;
            }
        }

        if (empty) {
            return;
        }

        Vector3f origin = cube.origin();
        Vector3f size = cube.size();
        float x = origin.x() / 16f;
        float y = origin.y() / 16f;
        float z = origin.z() / 16f;
        float sizeX = size.x() / 16f;
        float sizeY = size.y() / 16f;
        float sizeZ = size.z() / 16f;
        float inflate = (cube.overrideInflate() ? cube.inflate() : this.bone.inflate()) / 16f;

        float x1 = x + sizeX;
        float y1 = y + sizeY;
        float z1 = z + sizeZ;
        x = x - inflate;
        y = y - inflate;
        z = z - inflate;
        x1 = x1 + inflate;
        y1 = y1 + inflate;
        z1 = z1 + inflate;

        if (x == x1 && y == y1 && z == z1)
            return;

        boolean mirror = cube.overrideMirror() ? cube.mirror() : this.bone.mirror();
        if (mirror) {
            float f3 = x1;
            x1 = x;
            x = f3;
        }

        Vector3f rotation = cube.rotation();
        Vector3f pivot = cube.pivot();
        float rotationX = rotation.x();
        float rotationY = rotation.y();
        float rotationZ = rotation.z();
        float pivotX = pivot.x() / 16f;
        float pivotY = -pivot.y() / 16f;
        float pivotZ = pivot.z() / 16f;

        MatrixStack matrixStack = MatrixStack.create();
        matrixStack.translate(pivotX, pivotY, pivotZ);
        matrixStack.rotateZYX(rotationZ, rotationY, rotationX);
        matrixStack.translate(-pivotX, -pivotY, -pivotZ);
        Matrix4fc matrix4f = matrixStack.position();
        Matrix3fc matrix3f = matrixStack.normal();

        if (y != y1) {
            if (x != x1) {
                this.addFace(cube, matrix4f, matrix3f, x1, y1, z, x, y1, z, x, y, z, x1, y, z, FaceDirection.NORTH);
                this.addFace(cube, matrix4f, matrix3f, x, y1, z1, x1, y1, z1, x1, y, z1, x, y, z1, FaceDirection.SOUTH);
            }
            if (z != z1) {
                this.addFace(cube, matrix4f, matrix3f, x, y1, z, x, y1, z1, x, y, z1, x, y, z, FaceDirection.EAST);
                this.addFace(cube, matrix4f, matrix3f, x1, y1, z1, x1, y1, z, x1, y, z, x1, y, z1, FaceDirection.WEST);
            }
        }

        if (x != x1 && z != z1) {
            this.addFace(cube, matrix4f, matrix3f, x, y, z1, x1, y, z1, x1, y, z, x, y, z, FaceDirection.DOWN);
            this.addFace(cube, matrix4f, matrix3f, x1, y1, z1, x, y1, z1, x, y1, z, x1, y1, z, FaceDirection.UP);
        }
    }

    private void addPolyMesh(GeometryModelData.PolyMesh polyMesh) {
        Matrix4f matrix4f = new Matrix4f();
        for (GeometryModelData.Polygon poly : polyMesh.polys()) {
            Vertex[] vertices = new Vertex[polyMesh.polyType().getVertices()];
            Vector3f[] normals = new Vector3f[polyMesh.polyType().getVertices()];
            for (int i = 0; i < vertices.length; i++) {
                vertices[i] = this.getVertex(polyMesh, poly, matrix4f, i);
                normals[i] = polyMesh.normals()[poly.normals()[i]].get(new Vector3f());
                normals[i].mul(1, -1, 1);
            }
            this.polygons.add(new Polygon("poly_mesh.texture", vertices, normals));
        }
    }

    private Vertex getVertex(GeometryModelData.PolyMesh polyMesh, GeometryModelData.Polygon poly, Matrix4fc matrix4f, int index) {
        Vector3fc position = polyMesh.positions()[poly.positions()[index]];
        Vector2fc uv = polyMesh.uvs()[poly.uvs()[index]];
        return Vertex.create(matrix4f, position.x(), -position.y(), position.z(), polyMesh.normalizedUvs() ? uv.x() : uv.x() / this.textureWidth, 1 - (polyMesh.normalizedUvs() ? uv.y() : uv.y() / this.textureHeight));
    }

    private void addFace(GeometryModelData.Cube cube, Matrix4fc matrix4f, Matrix3fc matrix3f, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, FaceDirection face) {
        GeometryModelData.CubeUV uv = cube.uv(face);
        if (uv != null) {
            this.polygons.add(Polygon.quad(uv.materialInstance(), new Vertex[]{
                    Vertex.create(matrix4f, x0, -y0, z0, (uv.u() + uv.uSize()) / this.textureWidth, uv.v() / this.textureHeight),
                    Vertex.create(matrix4f, x1, -y1, z1, uv.u() / this.textureWidth, uv.v() / this.textureHeight),
                    Vertex.create(matrix4f, x2, -y2, z2, uv.u() / this.textureWidth, (uv.v() + uv.vSize()) / this.textureHeight),
                    Vertex.create(matrix4f, x3, -y3, z3, (uv.u() + uv.uSize()) / this.textureWidth, (uv.v() + uv.vSize()) / this.textureHeight)
            }, matrix3f, cube.overrideMirror() ? cube.mirror() : this.bone.mirror(), face.opposite()));
        }
    }

    @Override
    public void resetTransform() {
        Vector3f rotation = this.bone.rotation();
        Vector3f pivot = this.bone.pivot();
        this.rotation.set(rotation).mul((float) (Math.PI / 180.0));
        this.pivot.set(pivot.x(), -pivot.y(), pivot.z());
        this.copyPosition.identity();
        this.copyNormal.identity();
        this.animationPose.identity();
        this.copyVanilla = false;
    }

    @Override
    public void render(GeometryRenderer renderer, MatrixStack matrixStack) {
        if (!this.polygons.isEmpty() || !this.children.isEmpty()) {
            matrixStack.pushMatrix();
            this.translateAndRotate(matrixStack);

            if (this.copyVanilla) {
                matrixStack.translate(-this.pivot.x() / 16.0F, -this.pivot.x() / 16.0F, -this.pivot.x() / 16.0F);
            }

            for (Polygon polygon : this.polygons) {
                renderer.render(matrixStack, polygon);
            }

            for (AnimatedBone bone : this.children) {
                bone.render(renderer, matrixStack);
            }

            matrixStack.popMatrix();
        }
    }

    @Override
    public void copyTransform(ModelBone bone) {
        this.copyPosition.identity();
        this.copyNormal.identity();
        TRANSFORM_MATRIX.position().identity();
        TRANSFORM_MATRIX.normal().identity();
        bone.translateAndRotate(TRANSFORM_MATRIX);
        this.copyPosition.mul(TRANSFORM_MATRIX.position());
        this.copyNormal.mul(TRANSFORM_MATRIX.normal());
        this.copyVanilla = !AnimatedBoneImpl.class.isAssignableFrom(bone.getClass());
    }

    @Override
    public void translateAndRotate(MatrixStack matrixStack) {
        Vector3fc pos = this.animationPose.position();
        Vector3fc rot = this.animationPose.rotation();
        Vector3fc scale = this.animationPose.scale();

        matrixStack.position().mul(this.copyPosition);
        matrixStack.normal().mul(this.copyNormal);
        matrixStack.translate((pos.x() + this.pivot.x()) / 16.0F, (-pos.y() + this.pivot.z()) / 16.0F, (pos.z() + this.pivot.z()) / 16.0F);
        matrixStack.scale(scale.x(), scale.y(), scale.z());
        matrixStack.rotateZYX(this.rotation.z() + (float) (rot.z() * Math.PI / 180.0F), this.rotation.y() + (float) (rot.y() * Math.PI / 180.0F), this.rotation.x() + (float) (rot.x() * Math.PI / 180.0F));
        matrixStack.translate(-this.pivot.x() / 16.0F, -this.pivot.y() / 16.0F, -this.pivot.z() / 16.0F);
    }

    @Override
    public GeometryModelData.Bone getBone() {
        return this.bone;
    }

    @Override
    public AnimationPose getAnimationPose() {
        return this.animationPose;
    }

    @Override
    public Collection<AnimatedBone> getChildren() {
        return this.children;
    }

    @Override
    public GeometryModelData.Locator[] getLocators() {
        return this.bone.locators();
    }

    @Override
    public void updateLocators(MatrixStack matrixStack, LocatorAccess access) {
        matrixStack.pushMatrix();
        this.translateAndRotate(matrixStack);

        for (GeometryModelData.Locator locator : this.bone.locators()) {
            String name = locator.identifier();
            LocatorTransformation transformation = access.getLocatorTransformation(name);
            if (transformation == null) {
                continue;
            }

            matrixStack.pushMatrix();
            Vector3fc pos = locator.position();
            matrixStack.translate(pos.x() / 16.0F, -pos.y() / 16.0F, pos.z() / 16.0F);
            transformation.matrix().set(matrixStack.position());
            matrixStack.popMatrix();
        }

        for (AnimatedBone part : this.children) {
            part.updateLocators(matrixStack, access);
        }

        matrixStack.popMatrix();
    }

    @Override
    public String toString() {
        return "AnimatedBoneImpl{" +
                "name=" + this.bone.name() +
                ", children=" + this.children +
                '}';
    }
}
