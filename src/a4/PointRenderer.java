package a4;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static com.jogamp.opengl.GL4.GL_POINTS;

public class PointRenderer implements Renderer {

    private static final String UNIFORM_COLOR_NAME = "pointColor";
    private static final String UNIFORM_MVP_MATRIX_NAME = "mvp_matrix";

    private final Vector4f color = new Vector4f();
    private final Matrix4f mvpMatrix = new Matrix4f();
    private final float size;
    private final int shaderProgram;

    public PointRenderer(int shaderProgram, float size, Vector4fc color) {
        this.shaderProgram = shaderProgram;
        this.size = size;
        this.color.set(color);
    }

    @Override
    public void render(RenderPass renderPass, Camera camera, Camera shadowCamera, Matrix4fc modelMatrix, Entity entity) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(shaderProgram);

        mvpMatrix.set(camera.getProjMatrix());
        mvpMatrix.mul(camera.getViewMatrix());
        mvpMatrix.mul(modelMatrix);

        int colorLoc = gl.glGetUniformLocation(shaderProgram, UNIFORM_COLOR_NAME);
        gl.glUniform4fv(colorLoc, 1, color.get(ReusableBuffers.floatBuf4));

        int mvpMatLoc = gl.glGetUniformLocation(shaderProgram, UNIFORM_MVP_MATRIX_NAME);
        gl.glUniformMatrix4fv(mvpMatLoc, 1, false, mvpMatrix.get(ReusableBuffers.floatBuf16));

        gl.glPointSize(size);
        gl.glDrawArrays(GL_POINTS, 0, 1);
        gl.glPointSize(1f);
    }

}
