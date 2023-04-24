package a4;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4fc;

import static com.jogamp.opengl.GL4.*;

/**
 * Renders a 3D model with a particular shader program and texture.
 */
public class ModelRenderer implements Renderer {

    ////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////

    private static final String UNIFORM_M_MATRIX_NAME = "m_matrix";
    private static final String UNIFORM_V_MATRIX_NAME = "v_matrix";
    private static final String UNIFORM_PROJ_MATRIX_NAME = "proj_matrix";
    private static final String UNIFORM_NORM_MATRIX_NAME = "norm_matrix";
    private static final String UNIFORM_SHADOW_MVP_MATRIX_NAME = "shadowMVP";
    private static final String UNIFORM_USE_COLOR_NAME = "use_color";
    private static final String UNIFORM_USE_TEXTURE_NAME = "use_texture";
    private static final String UNIFORM_USE_NORMAL_MAP_NAME = "use_norm_map";
    private static final String UNIFORM_USE_TEX_UNIT_INDICES_NAME = "use_tex_unit_idx";
    private static final String UNIFORM_ALPHA_CUTOUT_NAME = "alpha_cutout";
    private static final String UNIFORM_MATERIAL_AMBIENT_NAME = "material.ambient";
    private static final String UNIFORM_MATERIAL_DIFFUSE_NAME = "material.diffuse";
    private static final String UNIFORM_MATERIAL_SPECULAR_NAME = "material.specular";
    private static final String UNIFORM_MATERIAL_SHININESS_NAME = "material.shininess";
    private static final String UNIFORM_TIME_NAME = "time";
    private static final String UNIFORM_FAR_PLANE_NAME = "shadowFarPlane";

    private static final Matrix4f b = new Matrix4f(
            .5f, 0f, 0f, 0f,
            0f, .5f, 0f, 0f,
            0f, 0f, .5f, 0f,
            .5f, .5f, .5f, 1f
    );

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private Model model;
    private final int shaderProgram;
    private final Material material;
    private Matrix4fc projMatrix;
    private final Matrix4f modelViewMatrix = new Matrix4f();
    private final Matrix4f normalModelViewMatrix = new Matrix4f();
    private final Matrix4f shadowMVPMatrix = new Matrix4f();

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public ModelRenderer(int shaderProgram, Material material) {
        this.shaderProgram = shaderProgram;
        this.material = material;
    }

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    public void render(RenderPass renderPass, Camera camera, Camera shadowCamera, Matrix4fc modelMatrix, Entity entity) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(shaderProgram);

        model = entity.getModel();
        prepareMatrices(camera, shadowCamera, modelMatrix);
        prepareUniforms(gl, camera, shadowCamera, renderPass, modelMatrix);
        prepareVBOs(gl);
        prepareTextures(gl);

        gl.glFrontFace(model.props.windingOrder);

        if(renderPass == RenderPass.Alpha) {
            gl.glEnable(GL_CULL_FACE);
            gl.glCullFace(GL_FRONT);
            gl.glDrawArrays(model.props.primitive, 0, model.getNumVertices());
            gl.glCullFace(GL_BACK);
            gl.glDrawArrays(model.props.primitive, 0, model.getNumVertices());
        }
        else {
            if(model.props.cullFace != GL_NONE) {
                gl.glEnable(GL_CULL_FACE);
                gl.glCullFace(model.props.cullFace);
            }
            else {
                gl.glDisable(GL_CULL_FACE);
            }
            gl.glDrawArrays(model.props.primitive, 0, model.getNumVertices());
        }
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods
    ////////////////////////////////////////////////////////////

    private void prepareMatrices(Camera camera, Camera shadowCamera, Matrix4fc modelMatrix) {
        projMatrix = camera.getProjMatrix();
        Matrix4fc viewMatrix = camera.getViewMatrix();
        viewMatrix.mul(modelMatrix, modelViewMatrix);

        modelViewMatrix.invert(normalModelViewMatrix);
        normalModelViewMatrix.transpose(normalModelViewMatrix);

        shadowMVPMatrix.set(b);
        shadowMVPMatrix.mul(shadowCamera.getProjMatrix());
        shadowMVPMatrix.mul(shadowCamera.getViewMatrix());
        shadowMVPMatrix.mul(modelMatrix);
    }

    private void prepareUniforms(GL4 gl, Camera camera, Camera shadowCamera, RenderPass renderPass, Matrix4fc modelMatrix) {
        setUniformMat4(gl, UNIFORM_M_MATRIX_NAME, modelMatrix);
        setUniformMat4(gl, UNIFORM_V_MATRIX_NAME, camera.getViewMatrix());
        setUniformMat4(gl, UNIFORM_PROJ_MATRIX_NAME, camera.getProjMatrix());
        setUniformMat4(gl, UNIFORM_NORM_MATRIX_NAME, normalModelViewMatrix);
        setUniformMat4(gl, UNIFORM_SHADOW_MVP_MATRIX_NAME, shadowMVPMatrix);
        setUniform1i(gl, UNIFORM_USE_COLOR_NAME, model.hasColors() ? 1 : 0);
        setUniform1i(gl, UNIFORM_USE_TEXTURE_NAME, model.hasTexCoords() ? 1 : 0);
        setUniform1i(gl, UNIFORM_USE_NORMAL_MAP_NAME, material.normalMap >= 0 ? 1 : 0);
        setUniform1i(gl, UNIFORM_USE_TEX_UNIT_INDICES_NAME, model.hasTexUnitIndices() ? 1 : 0);
        setUniform1i(gl, UNIFORM_ALPHA_CUTOUT_NAME, renderPass == RenderPass.Alpha ? 0 : 1);
        setUniform4f(gl, UNIFORM_MATERIAL_AMBIENT_NAME, material.ambient);
        setUniform4f(gl, UNIFORM_MATERIAL_DIFFUSE_NAME, material.diffuse);
        setUniform4f(gl, UNIFORM_MATERIAL_SPECULAR_NAME, material.specular);
        setUniform1f(gl, UNIFORM_MATERIAL_SHININESS_NAME, material.shininess);
        setUniform1f(gl, "fogStart", camera.getFogParameters().fogStartDistance);
        setUniform1f(gl, "fogEnd", camera.getFogParameters().fogEndDistance);
        setUniform4f(gl, "fogColor", camera.getFogParameters().fogColor);
        setUniform1f(gl, UNIFORM_TIME_NAME, (float) Application.getGameTime());
        setUniform1f(gl, UNIFORM_FAR_PLANE_NAME, shadowCamera.getCameraParameters().farClippingPlane);
        setUniform1i(gl, "is_underwater", RenderSystem.getInstance().isUnderwater(camera.getPosition()) ? 1 : 0);
        setUniform1f(gl, "water_level", RenderSystem.getInstance().getWaterHeight());
        setUniform1i(gl, "discard_underwater", renderPass == RenderPass.WaterReflection ? 1 : 0);
    }

    private void setUniformMat4(GL4 gl, String name, Matrix4fc value) {
        int location = gl.glGetUniformLocation(shaderProgram, name);
        gl.glUniformMatrix4fv(location, 1, false, value.get(ReusableBuffers.floatBuf16));
    }

    private void setUniform4f(GL4 gl, String name, Vector4fc value) {
        int location = gl.glGetUniformLocation(shaderProgram, name);
        gl.glUniform4fv(location, 1, value.get(ReusableBuffers.floatBuf4));
    }

    private void setUniform1i(GL4 gl, String name, int value) {
        int location = gl.glGetUniformLocation(shaderProgram, name);
        gl.glUniform1i(location, value);
    }

    private void setUniform1f(GL4 gl, String name, float value) {
        int location = gl.glGetUniformLocation(shaderProgram, name);
        gl.glUniform1f(location, value);
    }

    private void prepareVBOs(GL4 gl) {
        useVBO(gl, Model.VBO_VERTICES, 3);
        if(model.hasTexCoords()) useVBO(gl, Model.VBO_TEX_COORDS, 2);
        if(model.hasNormals()) useVBO(gl, Model.VBO_NORMALS, 3);
        if(model.hasTangents()) useVBO(gl, Model.VBO_TANGENTS, 3);
        if(model.hasColors()) useVBO(gl, Model.VBO_COLORS, 4);
        if(model.hasTexUnitIndices()) useVBO(gl, Model.VBO_TEX_UNIT_INDICES, 1);
    }

    private void useVBO(GL4 gl, int vboIndex, int components) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, model.getVBO(vboIndex));
        gl.glVertexAttribPointer(vboIndex, components, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(vboIndex);
    }

    private void prepareTextures(GL4 gl) {
        if(model.hasTexCoords()) {
            if(model.hasTexUnitIndices()) {
                model.bindTextures(gl);
            }
            else {
                gl.glActiveTexture(GL_TEXTURE0);
                gl.glBindTexture(GL_TEXTURE_2D, material.diffuseTex);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, material.wrapModeS);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, material.wrapModeT);

                gl.glActiveTexture(GL_TEXTURE8);
                gl.glBindTexture(GL_TEXTURE_3D, material.tex3D);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, material.wrapModeS);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, material.wrapModeT);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, material.wrapModeR);
            }

            // Bind normal map if present
            if(material.normalMap >= 0) {
                gl.glActiveTexture(GL_TEXTURE5);
                gl.glBindTexture(GL_TEXTURE_2D, material.normalMap);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, material.wrapModeS);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, material.wrapModeT);
            }
        }
    }

}
