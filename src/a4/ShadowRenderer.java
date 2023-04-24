package a4;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import org.joml.Matrix4fc;
import org.joml.Vector4fc;

import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL4.*;

public class ShadowRenderer implements Renderer {

    private static final String UNIFORM_M_MATRIX_NAME = "m_matrix";
    private static final String UNIFORM_PROJ_MATRIX_NAME = "proj_matrix";
    private static final String UNIFORM_V_MATRICES_NAME = "v_matrices";
    private static final String UNIFORM_LIGHT_POSITION_NAME = "lightPosition";
    private static final String UNIFORM_FAR_PLANE_NAME = "farPlane";
    private static final FloatBuffer floatBuf96 = Buffers.newDirectFloatBuffer(96);

    private final int shaderProgram;

    public ShadowRenderer(int shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    @Override
    public void render(RenderPass renderPass, Camera camera, Camera shadowCamera, Matrix4fc modelMatrix, Entity entity) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        Model model = entity.getModel();
        if(model == null) return;

        gl.glUseProgram(shaderProgram);

        prepareUniforms(gl, shadowCamera, modelMatrix);
        prepareVBOs(gl, model);

        gl.glDisable(GL_CULL_FACE);
        gl.glFrontFace(model.props.windingOrder);
        gl.glDrawArrays(model.props.primitive, 0, model.getNumVertices());
    }

    private void prepareUniforms(GL4 gl, Camera camera, Matrix4fc modelMatrix) {
        setUniformMat4(gl, UNIFORM_M_MATRIX_NAME, modelMatrix);
        setUniformMat4(gl, UNIFORM_PROJ_MATRIX_NAME, camera.getProjMatrix());
        setUniform3f(gl, UNIFORM_LIGHT_POSITION_NAME, RenderSystem.getInstance().getLightPosition());
        setUniform1f(gl, UNIFORM_FAR_PLANE_NAME, camera.getCameraParameters().farClippingPlane);

        // Fill view matrix array
        ShadowCamera shadowCam = (ShadowCamera) camera;
        for(int i = 0; i < 6; i++) {
            floatBuf96.position(i * 16);
            shadowCam.getViewMatrix(i).get(floatBuf96);
        }
        floatBuf96.position(0);
        int vMatricesLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_V_MATRICES_NAME);
        gl.glUniformMatrix4fv(vMatricesLocation, 6, false, floatBuf96);
    }

    private void prepareVBOs(GL4 gl, Model model) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, model.getVBO(Model.VBO_VERTICES));
        gl.glVertexAttribPointer(Model.VBO_VERTICES, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(Model.VBO_VERTICES);
    }

    private void setUniformMat4(GL4 gl, String name, Matrix4fc value) {
        int location = gl.glGetUniformLocation(shaderProgram, name);
        gl.glUniformMatrix4fv(location, 1, false, value.get(ReusableBuffers.floatBuf16));
    }

    private void setUniform3f(GL4 gl, String name, Vector4fc value) {
        int location = gl.glGetUniformLocation(shaderProgram, name);
        gl.glUniform3fv(location, 1, value.get(ReusableBuffers.floatBuf3));
    }

    private void setUniform1f(GL4 gl, String name, float value) {
        int location = gl.glGetUniformLocation(shaderProgram, name);
        gl.glUniform1f(location, value);
    }

}
