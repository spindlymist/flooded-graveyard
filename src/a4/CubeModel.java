package a4;

import org.joml.Vector3f;

import static com.jogamp.opengl.GL.GL_CW;

/**
 * A model of a cube. Vertices only.
 */
public class CubeModel extends Model {

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private final float[] vertices = {
        -1.0f,  1.0f, -1.0f, /**/ -1.0f, -1.0f, -1.0f, /**/  1.0f, -1.0f, -1.0f,
         1.0f, -1.0f, -1.0f, /**/  1.0f,  1.0f, -1.0f, /**/ -1.0f,  1.0f, -1.0f,
         1.0f, -1.0f, -1.0f, /**/  1.0f, -1.0f,  1.0f, /**/  1.0f,  1.0f, -1.0f,
         1.0f, -1.0f,  1.0f, /**/  1.0f,  1.0f,  1.0f, /**/  1.0f,  1.0f, -1.0f,
         1.0f, -1.0f,  1.0f, /**/ -1.0f, -1.0f,  1.0f, /**/  1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f, /**/ -1.0f,  1.0f,  1.0f, /**/  1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f, /**/ -1.0f, -1.0f, -1.0f, /**/ -1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f, -1.0f, /**/ -1.0f,  1.0f, -1.0f, /**/ -1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f, /**/  1.0f, -1.0f,  1.0f, /**/  1.0f, -1.0f, -1.0f,
         1.0f, -1.0f, -1.0f, /**/ -1.0f, -1.0f, -1.0f, /**/ -1.0f, -1.0f,  1.0f,
        -1.0f,  1.0f, -1.0f, /**/  1.0f,  1.0f, -1.0f, /**/  1.0f,  1.0f,  1.0f,
         1.0f,  1.0f,  1.0f, /**/ -1.0f,  1.0f,  1.0f, /**/ -1.0f,  1.0f, -1.0f,
    };

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public CubeModel() {
        props.windingOrder = GL_CW;
    }

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    @Override
    public int getNumVertices() {
        return vertices.length / 3;
    }

    @Override
    public float[] getVertices() {
        return vertices;
    }

}
