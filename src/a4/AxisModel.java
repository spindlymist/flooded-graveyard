package a4;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static com.jogamp.opengl.GL.GL_LINES;

public class AxisModel extends Model {

    private final float[] axisVertices = new float[6 * 3];
    private final float[] vertexColors = new float[6 * 4];

    public AxisModel(float axisLength, Vector4fc xColor, Vector4fc yColor, Vector4fc zColor) {
        props.primitive = GL_LINES;
        createVertices(axisLength);
        createColors(xColor, yColor, zColor);
    }

    private void createVertices(float axisLength) {
        // Position endpoints of each axis
        axisVertices[1 * 2 + 0] = axisLength; // x
        axisVertices[3 * 2 + 1] = axisLength; // y
        axisVertices[5 * 2 + 2] = axisLength; // z
    }

    private void createColors(Vector4fc xColor, Vector4fc yColor, Vector4fc zColor) {
        int i = 0;
        vertexColors[i++] = xColor.x();
        vertexColors[i++] = xColor.y();
        vertexColors[i++] = xColor.z();
        vertexColors[i++] = xColor.w();
        vertexColors[i++] = yColor.x();
        vertexColors[i++] = yColor.y();
        vertexColors[i++] = yColor.z();
        vertexColors[i++] = yColor.w();
        vertexColors[i++] = zColor.x();
        vertexColors[i++] = zColor.y();
        vertexColors[i++] = zColor.z();
        vertexColors[i++] = zColor.w();
    }

    @Override
    public int getNumVertices() {
        return axisVertices.length / 3;
    }

    @Override
    public float[] getVertices() {
        return axisVertices;
    }

    @Override
    public float[] getTexCoords() {
        return null;
    }

    @Override
    public float[] getNormals() {
        return null;
    }

    @Override
    public float[] getColors() {
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
