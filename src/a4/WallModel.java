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

    private final Vector3f[] vertices = {
        // Front large
        new Vector3f(-1f, 0f, 0.5f), new Vector3f(1f, 2f, 0.5f), new Vector3f(-1f, 2f, 0.5f),
        new Vector3f(-1f, 0f, 0.5f), new Vector3f(1f, 0f, 0.5f), new Vector3f(1f, 2f, 0.5f),
        // Front small
        new Vector3f(-1f, 2f, 0.5f), new Vector3f(0f, 3f, 0.5f), new Vector3f(-1f, 3f, 0.5f),
        new Vector3f(-1f, 2f, 0.5f), new Vector3f(0f, 2f, 0.5f), new Vector3f(0f, 3f, 0.5f),
        // Back large
        new Vector3f(-1f, 0f, -0.5f), new Vector3f(-1f, 2f, -0.5f), new Vector3f(1f, 2f, -0.5f),
        new Vector3f(-1f, 0f, -0.5f), new Vector3f(1f, 2f, -0.5f), new Vector3f(1f, 0f, -0.5f),
        // Back small
        new Vector3f(-1f, 2f, -0.5f), new Vector3f(-1f, 3f, -0.5f), new Vector3f(0f, 3f, -0.5f),
        new Vector3f(-1f, 2f, -0.5f), new Vector3f(0f, 3f, -0.5f), new Vector3f(0f, 2f, -0.5f),
        // Right large
        new Vector3f(1f, 0f, 0.5f), new Vector3f(1f, 2f, -0.5f), new Vector3f(1f, 2f, 0.5f),
        new Vector3f(1f, 0f, 0.5f), new Vector3f(1f, 0f, -0.5f), new Vector3f(1f, 2f, -0.5f),
        // Right small
        new Vector3f(0f, 2f, 0.5f), new Vector3f(0f, 3f, -0.5f), new Vector3f(0f, 3f, 0.5f),
        new Vector3f(0f, 2f, 0.5f), new Vector3f(0f, 2f, -0.5f), new Vector3f(0f, 3f, -0.5f),
        // Left
        new Vector3f(-1f, 0f, -0.5f), new Vector3f(-1f, 3f, 0.5f), new Vector3f(-1f, 3f, -0.5f),
        new Vector3f(-1f, 0f, -0.5f), new Vector3f(-1f, 0f, 0.5f), new Vector3f(-1f, 3f, 0.5f),
        // Top left
        new Vector3f(-1f, 3f, 0.5f), new Vector3f(0f, 3f, -0.5f), new Vector3f(-1f, 3f, -0.5f),
        new Vector3f(-1f, 3f, 0.5f), new Vector3f(0f, 3f, 0.5f), new Vector3f(0f, 3f, -0.5f),
        // Top right
        new Vector3f(0f, 2f, 0.5f), new Vector3f(1f, 2f, -0.5f), new Vector3f(0f, 2f, -0.5f),
        new Vector3f(0f, 2f, 0.5f), new Vector3f(1f, 2f, 0.5f), new Vector3f(1f, 2f, -0.5f),
    };

    private final Vector2f[] texCoords = {
        // Front large
        new Vector2f(0f, 0f), new Vector2f(2f, 2f), new Vector2f(0f, 2f),
        new Vector2f(0f, 0f), new Vector2f(2f, 0f), new Vector2f(2f, 2f),
        // Front small
        new Vector2f(0f, 2f), new Vector2f(1f, 3f), new Vector2f(0f, 3f),
        new Vector2f(0f, 2f), new Vector2f(1f, 2f), new Vector2f(1f, 3f),
        // Back large
        new Vector2f(0f, 0f), new Vector2f(0f, 2f), new Vector2f(2f, 2f),
        new Vector2f(0f, 0f), new Vector2f(2f, 2f), new Vector2f(2f, 0f),
        // Back small
        new Vector2f(0f, 2f), new Vector2f(0f, 3f), new Vector2f(1f, 3f),
        new Vector2f(0f, 2f), new Vector2f(1f, 3f), new Vector2f(1f, 2f),
        // Right large
        new Vector2f(0f, 0f), new Vector2f(0.5f, 2f), new Vector2f(0f, 2f),
        new Vector2f(0f, 0f), new Vector2f(0.5f, 0f), new Vector2f(0.5f, 2f),
        // Right small
        new Vector2f(0f, 2f), new Vector2f(0.5f, 3f), new Vector2f(0f, 3f),
        new Vector2f(0f, 2f), new Vector2f(0.5f, 2f), new Vector2f(0.5f, 3f),
        // Left
        new Vector2f(0f, 0f), new Vector2f(0.5f, 3f), new Vector2f(0f, 3f),
        new Vector2f(0f, 0f), new Vector2f(0.5f, 0f), new Vector2f(0.5f, 3f),
        // Top left
        new Vector2f(0f, 0f), new Vector2f(1f, 0.5f), new Vector2f(0f, 0.5f),
        new Vector2f(0f, 0f), new Vector2f(1f, 0f), new Vector2f(1f, 0.5f),
        // Top right
        new Vector2f(0f, 0f), new Vector2f(1f, 0.5f), new Vector2f(0f, 0.5f),
        new Vector2f(0f, 0f), new Vector2f(1f, 0f), new Vector2f(1f, 0.5f)
    };

    private final Vector3f[] normals = {
            // Front large
            new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f),
            new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f),
            // Front small
            new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f),
            new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 1f),
            // Back large
            new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -1f),
            new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -1f),
            // Back small
            new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -1f),
            new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -1f),
            // Right large
            new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f),
            new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f),
            // Right small
            new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f),
            new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f), new Vector3f(1f, 0f, 0f),
            // Left
            new Vector3f(-1f, 0f, 0f), new Vector3f(-1f, 0f, 0f), new Vector3f(-1f, 0f, 0f),
            new Vector3f(-1f, 0f, 0f), new Vector3f(-1f, 0f, 0f), new Vector3f(-1f, 0f, 0f),
            // Top left
            new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f),
            new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f),
            // Top right
            new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f),
            new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f)
    };

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

    @Override
    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    @Override
    public Vector3f[] getNormals() {
        return normals;
    }

    @Override
    public Vector4f[] getColors() {
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
