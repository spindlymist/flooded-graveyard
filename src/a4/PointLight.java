package a4;

import com.jogamp.opengl.GL4;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class PointLight implements Light {

    private static final String UNIFORM_AMBIENT_NAME = "pointLight.ambient";
    private static final String UNIFORM_DIFFUSE_NAME = "pointLight.diffuse";
    private static final String UNIFORM_SPECULAR_NAME = "pointLight.specular";
    private static final String UNIFORM_POSITION_NAME = "pointLight.position";
    private static final String UNIFORM_WORLD_POSITION_NAME = "pointLight.worldPosition";

    private final Vector4f black = new Vector4f(0f, 0f, 0f, 1f);
    private final Vector4f ambientColor = new Vector4f();
    private final Vector4f diffuseColor = new Vector4f();
    private final Vector4f specularColor = new Vector4f();
    private final Vector4f worldPosition = new Vector4f();
    private final Vector4f viewSpacePosition = new Vector4f();
    private boolean isEnabled = true;

    public PointLight(Vector4fc position, Vector4fc ambientColor, Vector4fc diffuseColor, Vector4fc specularColor) {
        this.worldPosition.set(position);
        this.ambientColor.set(ambientColor);
        this.diffuseColor.set(diffuseColor);
        this.specularColor.set(specularColor);
    }

    public Vector4fc getAmbientColor() {
        return ambientColor;
    }

    public Vector4fc getDiffuseColor() {
        return diffuseColor;
    }

    public Vector4fc getSpecularColor() {
        return specularColor;
    }

    public Vector4fc getPosition() {
        return worldPosition;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setAmbientColor(Vector4fc color) {
        ambientColor.set(color);
    }

    public void setDiffuseColor(Vector4fc color) {
        ambientColor.set(color);
    }

    public void setSpecularColor(Vector4fc color) {
        ambientColor.set(color);
    }

    public void setPosition(Vector4fc position) {
        worldPosition.set(position);
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public void installLight(GL4 gl, Camera camera, int shaderProgram) {
        calculateViewSpacePosition(camera);

        int ambientLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_AMBIENT_NAME);
        int diffuseLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_DIFFUSE_NAME);
        int specularLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_SPECULAR_NAME);
        int positionLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_POSITION_NAME);
        int worldPositionLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_WORLD_POSITION_NAME);

        if(isEnabled) {
            gl.glUniform4fv(ambientLocation, 1, ambientColor.get(ReusableBuffers.floatBuf4));
            gl.glUniform4fv(diffuseLocation, 1, diffuseColor.get(ReusableBuffers.floatBuf4));
            gl.glUniform4fv(specularLocation, 1, specularColor.get(ReusableBuffers.floatBuf4));
            gl.glUniform3fv(positionLocation, 1, viewSpacePosition.get(ReusableBuffers.floatBuf3));
            gl.glUniform3fv(worldPositionLocation, 1, worldPosition.get(ReusableBuffers.floatBuf3));
        }
        else {
            gl.glUniform4fv(ambientLocation, 1, black.get(ReusableBuffers.floatBuf4));
            gl.glUniform4fv(diffuseLocation, 1, black.get(ReusableBuffers.floatBuf4));
            gl.glUniform4fv(specularLocation, 1, black.get(ReusableBuffers.floatBuf4));
            gl.glUniform3fv(positionLocation, 1, viewSpacePosition.get(ReusableBuffers.floatBuf3));
        }
    }

    private void calculateViewSpacePosition(Camera camera) {
        viewSpacePosition.set(worldPosition);
        viewSpacePosition.mul(camera.getViewMatrix());
    }

}
