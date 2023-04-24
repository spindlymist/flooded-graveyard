package a4;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ShadowCamera extends Camera {

    private final Matrix4f[] viewMatrices = new Matrix4f[] {
            new Matrix4f(), new Matrix4f(), new Matrix4f(),
            new Matrix4f(), new Matrix4f(), new Matrix4f()
    };
    private boolean generatingMatrices = false;

    public ShadowCamera(float fieldOfView, float aspectRatio, float nearClippingPlane, float farClippingPlane) {
        super(fieldOfView, aspectRatio, nearClippingPlane, farClippingPlane);
    }

    public ShadowCamera(CameraParameters cameraParameters) {
        super(cameraParameters);
    }

    @Override
    protected void generateViewMatrix() {
        if(generatingMatrices) {
            super.generateViewMatrix();
        }
        else {
            generatingMatrices = true;

            Vector3fc position = getPosition();
            Vector3f target = new Vector3f();

            target.set(position).add(1f, 0f, 0f);
            lookAt(target, MathUtil.VEC3_DOWN);
            viewMatrices[0].set(getViewMatrix());

            target.set(position).add(-1f, 0f, 0f);
            lookAt(target, MathUtil.VEC3_DOWN);
            viewMatrices[1].set(getViewMatrix());

            target.set(position).add(0f, 1f, 0f);
            lookAt(target, MathUtil.VEC3_FORWARD);
            viewMatrices[2].set(getViewMatrix());

            target.set(position).add(0f, -1f, 0f);
            lookAt(target, MathUtil.VEC3_BACKWARD);
            viewMatrices[3].set(getViewMatrix());

            target.set(position).add(0f, 0f, 1f);
            lookAt(target, MathUtil.VEC3_DOWN);
            viewMatrices[4].set(getViewMatrix());

            target.set(position).add(0f, 0f, -1f);
            lookAt(target, MathUtil.VEC3_DOWN);
            viewMatrices[5].set(getViewMatrix());

            generatingMatrices = false;
        }
    }

    public Matrix4fc getViewMatrix(int index) {
        return viewMatrices[index];
    }

}
