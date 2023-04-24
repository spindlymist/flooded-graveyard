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

    private final Vector3f[] vertices = {
            new Vector3f(-1.0f,  1.0f, -1.0f), new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(1.0f, -1.0f, -1.0f),
            new Vector3f( 1.0f, -1.0f, -1.0f), new Vector3f( 1.0f,  1.0f, -1.0f), new Vector3f(-1.0f,  1.0f, -1.0f),
            new Vector3f( 1.0f, -1.0f, -1.0f), new Vector3f( 1.0f, -1.0f,  1.0f), new Vector3f( 1.0f,  1.0f, -1.0f),
            new Vector3f( 1.0f, -1.0f,  1.0f), new Vector3f( 1.0f,  1.0f,  1.0f), new Vector3f( 1.0f,  1.0f, -1.0f),
            new Vector3f( 1.0f, -1.0f,  1.0f), new Vector3f(-1.0f, -1.0f,  1.0f), new Vector3f( 1.0f,  1.0f,  1.0f),
            new Vector3f(-1.0f, -1.0f,  1.0f), new Vector3f(-1.0f,  1.0f,  1.0f), new Vector3f( 1.0f,  1.0f,  1.0f),
            new Vector3f(-1.0f, -1.0f,  1.0f), new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(-1.0f,  1.0f,  1.0f),
            new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(-1.0f,  1.0f, -1.0f), new Vector3f(-1.0f,  1.0f,  1.0f),
            new Vector3f(-1.0f, -1.0f,  1.0f), new Vector3f( 1.0f, -1.0f,  1.0f), new Vector3f( 1.0f, -1.0f, -1.0f),
            new Vector3f( 1.0f, -1.0f, -1.0f), new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(-1.0f, -1.0f,  1.0f),
            new Vector3f(-1.0f,  1.0f, -1.0f), new Vector3f( 1.0f,  1.0f, -1.0f), new Vector3f( 1.0f,  1.0f,  1.0f),
            new Vector3f( 1.0f,  1.0f,  1.0f), new Vector3f(-1.0f,  1.0f,  1.0f), new Vector3f(-1.0f,  1.0f, -1.0f)
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
        return vertices.length;
    }

    @Override
    public Vector3f[] getVertices() {
        return vertices;
    }

}
