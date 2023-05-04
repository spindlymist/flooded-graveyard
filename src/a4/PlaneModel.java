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

    private final float[] vertices = new float[6 * 3];
    private final float[] texCoords = new float[6 * 2];
    private final float[] normals = {
            0f, 1f, 0f, /**/ 0f, 1f, 0f,
            0f, 1f, 0f, /**/ 0f, 1f, 0f,
            0f, 1f, 0f, /**/ 0f, 1f, 0f,
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
        int i = 0;
        vertices[i++] = -xHalfLength;
        vertices[i++] = 0f;
        vertices[i++] = -zHalfLength;

        vertices[i++] = -xHalfLength;
        vertices[i++] = 0f;
        vertices[i++] = zHalfLength;

        vertices[i++] = xHalfLength;
        vertices[i++] = 0f;
        vertices[i++] = -zHalfLength;

        vertices[i++] = xHalfLength;
        vertices[i++] = 0f;
        vertices[i++] = zHalfLength;

        vertices[i++] = xHalfLength;
        vertices[i++] = 0f;
        vertices[i++] = -zHalfLength;

        vertices[i++] = -xHalfLength;
        vertices[i++] = 0f;
        vertices[i++] = zHalfLength;
    }

    private void createTexCoords(float xMaxTexCoord, float zMaxTexCoord) {
        int i = 0;
        texCoords[i++] = zMaxTexCoord;
        texCoords[i++] = 0f;

        texCoords[i++] = 0f;
        texCoords[i++] = 0f;

        texCoords[i++] = zMaxTexCoord;
        texCoords[i++] = xMaxTexCoord;

        texCoords[i++] = 0f;
        texCoords[i++] = xMaxTexCoord;

        texCoords[i++] = zMaxTexCoord;
        texCoords[i++] = xMaxTexCoord;

        texCoords[i++] = 0f;
        texCoords[i++] = 0f;
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

    @Override
    public float[] getTexCoords() {
        return texCoords;
    }

    @Override
    public float[] getNormals() {
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
