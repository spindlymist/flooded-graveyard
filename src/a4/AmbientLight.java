package a4;

import com.jogamp.opengl.GL4;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class AmbientLight implements Light {

    private static final String UNIFORM_AMBIENT_NAME = "globalAmbient";

    private final Vector4f ambientColor = new Vector4f();

    public AmbientLight(Vector4fc color) {
        ambientColor.set(color);
    }

    public void setColor(Vector4fc color) {
        ambientColor.set(color);
    }

    public Vector4fc getColor() {
        return ambientColor;
    }

    @Override
    public void installLight(GL4 gl, Camera camera, int shaderProgram) {
        int ambientLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_AMBIENT_NAME);
        gl.glUniform4fv(ambientLocation, 1, ambientColor.get(ReusableBuffers.floatBuf4));
    }

}
