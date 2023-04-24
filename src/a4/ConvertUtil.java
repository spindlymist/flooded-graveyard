package a4;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ConvertUtil {

    public static float[] toFloatArray(Vector2f[] vectors) {
        float[] values = new float[vectors.length * 3];

        int i = 0;
        for(Vector2f vector : vectors) {
            values[i++] = vector.x;
            values[i++] = vector.y;
        }

        return values;
    }

    public static float[] toFloatArray(Vector3f[] vectors) {
        float[] values = new float[vectors.length * 3];

        int i = 0;
        for(Vector3f vector : vectors) {
            values[i++] = vector.x;
            values[i++] = vector.y;
            values[i++] = vector.z;
        }

        return values;
    }

    public static float[] toFloatArray(Vector4f[] vectors) {
        float[] values = new float[vectors.length * 4];

        int i = 0;
        for(Vector4f vector : vectors) {
            values[i++] = vector.x;
            values[i++] = vector.y;
            values[i++] = vector.z;
            values[i++] = vector.w;
        }

        return values;
    }

}
