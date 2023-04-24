package a4;

import org.joml.*;

import java.lang.Math;

public class Camera {

    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projMatrix = new Matrix4f();
    private CameraParameters cameraParameters;
    private FogParameters fogParameters = new FogParameters(new Vector4f(.12f, .12f, .25f, 1f), 5f, 25f);
    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f n = new Vector3f(0, 0, -1);
    private final Vector3f v = new Vector3f(0, 1, 0);
    private final Vector3f u = new Vector3f(1, 0, 0);
    private Skybox skybox = null;

    public Camera(float fieldOfView, float aspectRatio, float nearClippingPlane, float farClippingPlane) {
        cameraParameters = new CameraParameters(fieldOfView, aspectRatio, nearClippingPlane, farClippingPlane);
        generateProjMatrix();
    }

    public Camera(CameraParameters cameraParameters) {
        this.cameraParameters = cameraParameters;
        generateProjMatrix();
    }

    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4fc getProjMatrix() {
        return projMatrix;
    }

    protected void generateViewMatrix() {
        viewMatrix.set(
                u.x, v.x, -n.x, 0,
                u.y, v.y, -n.y, 0,
                u.z, v.z, -n.z, 0,
                0, 0, 0, 1
        );
        viewMatrix.mul(new Matrix4f().setTranslation(-position.x, -position.y, -position.z));
    }

    protected void generateProjMatrix() {
        projMatrix.setPerspective((float) Math.toRadians(cameraParameters.fieldOfView), cameraParameters.aspectRatio, cameraParameters.nearClippingPlane, cameraParameters.farClippingPlane);
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getForward() {
        return n;
    }

    public Vector3fc getUp() {
        return v;
    }

    public Vector3fc getRight() {
        return u;
    }

    public CameraParameters getCameraParameters() {
        return cameraParameters;
    }

    public void setPosition(Vector3fc position) {
        this.position.set(position);
        generateViewMatrix();
    }

    public void setPosition(Vector4fc position) {
        this.position.set(position.x(), position.y(), position.z());
        generateViewMatrix();
    }

    public void moveForward(float distance) {
        Vector3f moveVec = new Vector3f(n).mul(distance);
        position.add(moveVec);
        generateViewMatrix();
    }

    public void moveRight(float distance) {
        Vector3f moveVec = new Vector3f(u).mul(distance);
        position.add(moveVec);
        generateViewMatrix();
    }

    public void moveUp(float distance) {
        Vector3f moveVec = new Vector3f(v).mul(distance);
        position.add(moveVec);
        generateViewMatrix();
    }

    public void pan(float degrees) {
        float radians = (float) Math.toRadians(degrees);
        u.rotateAxis(radians, v.x, v.y, v.z).normalize();
        n.rotateAxis(radians, v.x, v.y, v.z).normalize();
        generateViewMatrix();
    }

    public void pitch(float degrees) {
        float radians = (float) Math.toRadians(degrees);
        n.rotateAxis(radians, u.x, u.y, u.z).normalize();
        v.rotateAxis(radians, u.x, u.y, u.z).normalize();
        generateViewMatrix();
    }

    public void roll(float degrees) {
        float radians = (float) Math.toRadians(degrees);
        u.rotateAxis(radians, n.x, n.y, n.z).normalize();
        v.rotateAxis(radians, n.x, n.y, n.z).normalize();
        generateViewMatrix();
    }

    public void rotateY(float degrees) {
        float radians = (float) Math.toRadians(degrees);
        n.rotateAxis(radians, 0f, 1f, 0f).normalize();
        u.rotateAxis(radians, 0f, 1f, 0f).normalize();
        v.rotateAxis(radians, 0f, 1f, 0f).normalize();
        generateViewMatrix();
    }

    public void lookAt(Vector3fc target) {
        lookAt(target, MathUtil.VEC3_UP);
    }

    public void lookAt(Vector3fc target, Vector3fc worldUpVector) {
        target.sub(position, n);
        n.normalize();

        n.cross(worldUpVector, u);
        u.normalize();

        u.cross(n, v);
        v.normalize();

        generateViewMatrix();
    }

    public FogParameters getFogParameters() {
        return fogParameters;
    }

    public void setFogParameters(FogParameters fogParameters) {
        this.fogParameters = fogParameters;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public void setCameraParameters(CameraParameters cameraParameters) {
        this.cameraParameters = cameraParameters;
        generateViewMatrix();
        generateProjMatrix();
    }

    public static class CameraParameters {
        public float fieldOfView;
        public float aspectRatio;
        public float nearClippingPlane;
        public float farClippingPlane;

        public CameraParameters(float fieldOfView, float aspectRatio, float nearClippingPlane, float farClippingPlane) {
            this.fieldOfView = fieldOfView;
            this.aspectRatio = aspectRatio;
            this.nearClippingPlane = nearClippingPlane;
            this.farClippingPlane = farClippingPlane;
        }
    }

    public static class FogParameters {
        public Vector4f fogColor = new Vector4f();
        public float fogStartDistance;
        public float fogEndDistance;

        public FogParameters(Vector4f fogColor, float fogStartDistance, float fogEndDistance) {
            this.fogColor.set(fogColor);
            this.fogStartDistance = fogStartDistance;
            this.fogEndDistance = fogEndDistance;
        }
    }

}
