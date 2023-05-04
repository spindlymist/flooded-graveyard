package a4;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Simple model of a castle-like wall segment. Shaped like this:
 *       __
 *      |  |__
 *      |     |
 *      |_____|
 */
public class WallModel extends Model {

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private final float[] vertices = {
        // Front large
        -1f, 0f, 0.5f, /**/ 1f, 2f, 0.5f, /**/ -1f, 2f, 0.5f,
        -1f, 0f, 0.5f, /**/ 1f, 0f, 0.5f, /**/ 1f, 2f, 0.5f,
        // Front small
        -1f, 2f, 0.5f, /**/ 0f, 3f, 0.5f, /**/ -1f, 3f, 0.5f,
        -1f, 2f, 0.5f, /**/ 0f, 2f, 0.5f, /**/ 0f, 3f, 0.5f,
        // Back large
        -1f, 0f, -0.5f, /**/ -1f, 2f, -0.5f, /**/ 1f, 2f, -0.5f,
        -1f, 0f, -0.5f, /**/ 1f, 2f, -0.5f, /**/ 1f, 0f, -0.5f,
        // Back small
        -1f, 2f, -0.5f, /**/ -1f, 3f, -0.5f, /**/ 0f, 3f, -0.5f,
        -1f, 2f, -0.5f, /**/ 0f, 3f, -0.5f, /**/ 0f, 2f, -0.5f,
        // Right large
        1f, 0f, 0.5f, /**/ 1f, 2f, -0.5f, /**/ 1f, 2f, 0.5f,
        1f, 0f, 0.5f, /**/ 1f, 0f, -0.5f, /**/ 1f, 2f, -0.5f,
        // Right small
        0f, 2f, 0.5f, /**/ 0f, 3f, -0.5f, /**/ 0f, 3f, 0.5f,
        0f, 2f, 0.5f, /**/ 0f, 2f, -0.5f, /**/ 0f, 3f, -0.5f,
        // Left
        -1f, 0f, -0.5f, /**/ -1f, 3f, 0.5f, /**/ -1f, 3f, -0.5f,
        -1f, 0f, -0.5f, /**/ -1f, 0f, 0.5f, /**/ -1f, 3f, 0.5f,
        // Top left
        -1f, 3f, 0.5f, /**/ 0f, 3f, -0.5f, /**/ -1f, 3f, -0.5f,
        -1f, 3f, 0.5f, /**/ 0f, 3f, 0.5f, /**/ 0f, 3f, -0.5f,
        // Top right
        0f, 2f, 0.5f, /**/ 1f, 2f, -0.5f, /**/ 0f, 2f, -0.5f,
        0f, 2f, 0.5f, /**/ 1f, 2f, 0.5f, /**/ 1f, 2f, -0.5f,
    };

    private final float[] texCoords = {
        // Front large
        0f, 0f, /**/ 2f, 2f, /**/ 0f, 2f,
        0f, 0f, /**/ 2f, 0f, /**/ 2f, 2f,
        // Front small
        0f, 2f, /**/ 1f, 3f, /**/ 0f, 3f,
        0f, 2f, /**/ 1f, 2f, /**/ 1f, 3f,
        // Back large
        0f, 0f, /**/ 0f, 2f, /**/ 2f, 2f,
        0f, 0f, /**/ 2f, 2f, /**/ 2f, 0f,
        // Back small
        0f, 2f, /**/ 0f, 3f, /**/ 1f, 3f,
        0f, 2f, /**/ 1f, 3f, /**/ 1f, 2f,
        // Right large
        0f, 0f, /**/ 0.5f, 2f, /**/ 0f, 2f,
        0f, 0f, /**/ 0.5f, 0f, /**/ 0.5f, 2f,
        // Right small
        0f, 2f, /**/ 0.5f, 3f, /**/ 0f, 3f,
        0f, 2f, /**/ 0.5f, 2f, /**/ 0.5f, 3f,
        // Left
        0f, 0f, /**/ 0.5f, 3f, /**/ 0f, 3f,
        0f, 0f, /**/ 0.5f, 0f, /**/ 0.5f, 3f,
        // Top left
        0f, 0f, /**/ 1f, 0.5f, /**/ 0f, 0.5f,
        0f, 0f, /**/ 1f, 0f, /**/ 1f, 0.5f,
        // Top right
        0f, 0f, /**/ 1f, 0.5f, /**/ 0f, 0.5f,
        0f, 0f, /**/ 1f, 0f, /**/ 1f, 0.5f,
    };

    private final float[] normals = {
            // Front large
            0f, 0f, 1f, /**/ 0f, 0f, 1f, /**/ 0f, 0f, 1f,
            0f, 0f, 1f, /**/ 0f, 0f, 1f, /**/ 0f, 0f, 1f,
            // Front small
            0f, 0f, 1f, /**/ 0f, 0f, 1f, /**/ 0f, 0f, 1f,
            0f, 0f, 1f, /**/ 0f, 0f, 1f, /**/ 0f, 0f, 1f,
            // Back large
            0f, 0f, -1f, /**/ 0f, 0f, -1f, /**/ 0f, 0f, -1f,
            0f, 0f, -1f, /**/ 0f, 0f, -1f, /**/ 0f, 0f, -1f,
            // Back small
            0f, 0f, -1f, /**/ 0f, 0f, -1f, /**/ 0f, 0f, -1f,
            0f, 0f, -1f, /**/ 0f, 0f, -1f, /**/ 0f, 0f, -1f,
            // Right large
            1f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 0f,
            1f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 0f,
            // Right small
            1f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 0f,
            1f, 0f, 0f, /**/ 1f, 0f, 0f, /**/ 1f, 0f, 0f,
            // Left
            -1f, 0f, 0f, /**/ -1f, 0f, 0f, /**/ -1f, 0f, 0f,
            -1f, 0f, 0f, /**/ -1f, 0f, 0f, /**/ -1f, 0f, 0f,
            // Top left
            0f, 1f, 0f, /**/ 0f, 1f, 0f, /**/ 0f, 1f, 0f,
            0f, 1f, 0f, /**/ 0f, 1f, 0f, /**/ 0f, 1f, 0f,
            // Top right
            0f, 1f, 0f, /**/ 0f, 1f, 0f, /**/ 0f, 1f, 0f,
            0f, 1f, 0f, /**/ 0f, 1f, 0f, /**/ 0f, 1f, 0f,
    };

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

    @Override
    public float[] getTexCoords() {
        return texCoords;
    }

    @Override
    public float[] getNormals() {
        return normals;
    }

    @Override
    public float[] getColors() {
        return null;
    }

    @Override
    public int[] getTexUnitIndices() {
        return null;
    }

    @Override
    public boolean hasTexCoords() {
        return true;
    }

    @Override
    public boolean hasNormals() {
        return true;
    }

    @Override
    public boolean hasColors() {
        return false;
    }

    @Override
    public boolean hasTexUnitIndices() {
        return false;
    }
}
