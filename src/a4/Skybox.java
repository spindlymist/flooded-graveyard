package a4;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import static com.jogamp.opengl.GL4.*;

public class Skybox {

    private static final String UNIFORM_VIEW_MATRIX_NAME = "v_matrix";
    private static final String UNIFORM_PROJ_MATRIX_NAME = "proj_matrix";

    private final int shaderProgram;
    private final int cubeMapID;
    private final Model cubeModel;

    public Skybox(int shaderProgram, String directory) throws Utils.OpenGLException {
        this.shaderProgram = shaderProgram;
        cubeMapID = Utils.loadCubeMap(directory, 1024, 1024);
        cubeModel = new CubeModel();
        cubeModel.initVBOs();
    }

    public void render(Camera camera) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(shaderProgram);

        useVBO(gl, Model.VBO_VERTICES, 3);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapID);

        int viewMatLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_VIEW_MATRIX_NAME);
        int projMatLocation = gl.glGetUniformLocation(shaderProgram, UNIFORM_PROJ_MATRIX_NAME);
        gl.glUniformMatrix4fv(viewMatLocation, 1, false, camera.getViewMatrix().get(ReusableBuffers.floatBuf16));
        gl.glUniformMatrix4fv(projMatLocation, 1, false, camera.getProjMatrix().get(ReusableBuffers.floatBuf16));

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, cubeModel.getNumVertices());
    }

    private void useVBO(GL4 gl, int vboIndex, int components) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, cubeModel.getVBO(vboIndex));
        gl.glVertexAttribPointer(vboIndex, components, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(vboIndex);
    }

}
