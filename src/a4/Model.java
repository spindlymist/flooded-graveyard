package a4;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL4.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL4.GL_STATIC_DRAW;

/**
 * Contains 3D model data including vertices, texture coordinates, normal vectors, and vertex colors. Always call
 * initVBOs() before using.
 */
public abstract class Model {

    ////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////

    public static final int VBO_COUNT = 6;
    public static final int VBO_VERTICES = 0;
    public static final int VBO_TEX_COORDS = 1;
    public static final int VBO_NORMALS = 2;
    public static final int VBO_COLORS = 3;
    public static final int VBO_TEX_UNIT_INDICES = 4;
    public static final int VBO_TANGENTS = 5;

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private final Matrix4f modelViewMatrix = new Matrix4f();
    private int[] vbos;

    /**
     * Stores properties of the model such as the primitive type and winding order.
     */
    public ModelProps props = new ModelProps();

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    public void initVBOs() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        vbos = new int[VBO_COUNT];
        gl.glGenBuffers(vbos.length, vbos, 0);

        genVBOf(gl, VBO_VERTICES, getVertices());
        if(hasTexCoords()) genVBOf(gl, VBO_TEX_COORDS, getTexCoords());
        if(hasNormals()) genVBOf(gl, VBO_NORMALS, getNormals());
        if(hasTangents()) genVBOf(gl, VBO_TANGENTS, getTangents());
        if(hasColors()) genVBOf(gl, VBO_COLORS, getColors());
        if(hasTexUnitIndices()) genVBOi(gl, VBO_TEX_UNIT_INDICES, getTexUnitIndices());
    }

    public int getVBO(int index) {
        return vbos[index];
    }

    public abstract int getNumVertices();
    public abstract float[] getVertices();
    public float[] getTexCoords() { return null; }
    public float[] getNormals() { return null; }
    public float[] getTangents() { return null; }
    public float[] getColors() { return null; }
    public int[] getTexUnitIndices() { return null; }
    public boolean hasTexCoords() { return false; }
    public boolean hasNormals() { return false; }
    public boolean hasTangents() { return false; }
    public boolean hasColors() { return false; }
    public boolean hasTexUnitIndices() { return false; }

    ////////////////////////////////////////////////////////////
    // Internal Methods
    ////////////////////////////////////////////////////////////

    private void genVBOf(GL4 gl, int vboIndex, float[] values) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbos[vboIndex]);
        FloatBuffer buffer = Buffers.newDirectFloatBuffer(values);
        gl.glBufferData(GL_ARRAY_BUFFER, buffer.limit() * 4, buffer, GL_STATIC_DRAW);
    }

    private void genVBOi(GL4 gl, int vboIndex, int[] values) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbos[vboIndex]);
        IntBuffer buffer = Buffers.newDirectIntBuffer(values);
        gl.glBufferData(GL_ARRAY_BUFFER, buffer.limit() * 4, buffer, GL_STATIC_DRAW);
    }

    public void bindTextures(GL4 gl) { }

}
