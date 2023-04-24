package a4;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static com.jogamp.opengl.GL.GL_LINES;

public class AxisModel extends Model {

    private Vector3f[] axisVertices;
    private Vector4f[] vertexColors;

    public AxisModel(float axisLength, Vector4fc xColor, Vector4fc yColor, Vector4fc zColor) {
        props.primitive = GL_LINES;
        createVertices(axisLength);
        createColors(xColor, yColor, zColor);
    }

    private void createVertices(float axisLength) {
        axisVertices = new Vector3f[6];

        // Position start points of each axis at origin
        for(int axis = 0; axis < 3; axis++) {
            axisVertices[axis * 2] = new Vector3f(0f, 0f, 0f);
        }

        // Position endpoints of each axis
        axisVertices[1] = new Vector3f(axisLength, 0f, 0f);
        axisVertices[3] = new Vector3f(0f, axisLength, 0f);
        axisVertices[5] = new Vector3f(0f, 0f, axisLength);

    }

    private void createColors(Vector4fc xColor, Vector4fc yColor, Vector4fc zColor) {
        vertexColors = new Vector4f[6];
        vertexColors[0] = new Vector4f(xColor);
        vertexColors[1] = new Vector4f(xColor);
        vertexColors[2] = new Vector4f(yColor);
        vertexColors[3] = new Vector4f(yColor);
        vertexColors[4] = new Vector4f(zColor);
        vertexColors[5] = new Vector4f(zColor);
    }

    @Override
    public int getNumVertices() {
        return axisVertices.length;
    }

    @Override
    public Vector3f[] getVertices() {
        return axisVertices;
    }

    @Override
    public Vector2f[] getTexCoords() {
        return null;
    }

    @Override
    public Vector3f[] getNormals() {
        return null;
    }

    @Override
    public Vector4f[] getColors() {
        return vertexColors;
    }

    @Override
    public int[] getTexUnitIndices() {
        return null;
    }

    @Override
    public boolean hasTexCoords() {
        return false;
    }

    @Override
    public boolean hasNormals() {
        return false;
    }

    @Override
    public boolean hasColors() {
        return true;
    }

    @Override
    public boolean hasTexUnitIndices() {
        return false;
    }

}
