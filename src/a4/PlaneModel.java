package a4;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * A model of a plane. Vertices and texture coordinates only.
 */
public class PlaneModel extends Model {

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private final Vector3f[] vertices = new Vector3f[6];
    private final Vector2f[] texCoords = new Vector2f[6];
    private final Vector3f[] normals = {
            new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f),
            new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f),
            new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f)
    };

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public PlaneModel(float xLength, float zLength, float xMaxTexCoord, float zMaxTexCoord) {
        createVertices(xLength / 2f, zLength / 2f);
        createTexCoords(xMaxTexCoord, zMaxTexCoord);
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods
    ////////////////////////////////////////////////////////////

    private void createVertices(float xHalfLength, float zHalfLength) {
        vertices[0] = new Vector3f(-xHalfLength, 0f, -zHalfLength);
        vertices[1] = new Vector3f(-xHalfLength, 0f, zHalfLength);
        vertices[2] = new Vector3f(xHalfLength, 0f, -zHalfLength);

        vertices[3] = new Vector3f(xHalfLength, 0f, zHalfLength);
        vertices[4] = new Vector3f(xHalfLength, 0f, -zHalfLength);
        vertices[5] = new Vector3f(-xHalfLength, 0f, zHalfLength);
    }

    private void createTexCoords(float xMaxTexCoord, float zMaxTexCoord) {
        texCoords[0] = new Vector2f(zMaxTexCoord, 0f);
        texCoords[1] = new Vector2f(0f, 0f);
        texCoords[2] = new Vector2f(zMaxTexCoord, xMaxTexCoord);

        texCoords[3] = new Vector2f(0f, xMaxTexCoord);
        texCoords[4] = new Vector2f(zMaxTexCoord, xMaxTexCoord);
        texCoords[5] = new Vector2f(0f, 0f);
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

    @Override
    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    @Override
    public Vector3f[] getNormals() {
        return normals;
    }

    @Override
    public boolean hasTexCoords() {
        return true;
    }

    @Override
    public boolean hasNormals() {
        return true;
    }

}
