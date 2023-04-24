package a4;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Utility class for common math operations.
 */
public class MathUtil {

    public static final Vector3fc VEC3_ZERO = new Vector3f(0f, 0f, 0f);
    public static final Vector3fc VEC3_ONE = new Vector3f(1f, 1f, 1f);
    public static final Vector3fc VEC3_RIGHT = new Vector3f(1f, 0f, 0f);
    public static final Vector3fc VEC3_LEFT = new Vector3f(-1f, 0f, 0f);
    public static final Vector3fc VEC3_UP = new Vector3f(0f, 1f, 0f);
    public static final Vector3fc VEC3_DOWN = new Vector3f(0f, -1f, 0f);
    public static final Vector3fc VEC3_FORWARD = new Vector3f(0f, 0f, 1f);
    public static final Vector3fc VEC3_BACKWARD = new Vector3f(0f, 0f, -1f);

    /**
     * Constrains an integer value to the range [<code>minimum</code>, <code>maximum</code>].
     * @param value the value to be constrained
     * @param minimum the minimum permissible value
     * @param maximum the maximum permissible value
     * @return the closest integer within the range [<code>minimum</code>, <code>maximum</code>]
     */
    public static int clamp(int value, int minimum, int maximum) {
        return Math.max(Math.min(value, maximum), minimum);
    }

    /**
     * Constrains an double value to the range [<code>minimum</code>, <code>maximum</code>].
     * @param value the value to be constrained
     * @param minimum the minimum permissible value
     * @param maximum the maximum permissible value
     * @return the closest double within the range [<code>minimum</code>, <code>maximum</code>]
     */
    public static double clamp(double value, double minimum, double maximum) {
        return Math.max(Math.min(value, maximum), minimum);
    }

    /**
     * Converts an angle in degrees to the equivalent angle in the range [0.0, 360.0).
     * @param angle the angle to convert
     * @return the converted angle
     */
    public static double clampAngle(double angle) {
        angle %= 360.0;
        if(angle < 0.0) {
            angle += 360.0;
        }

        return angle;
    }

    /**
     * Interpolates linearly ("lerps") between a start and end value based on the specified weighting. Note that this
     * method does not enforce any constraints on <code>weight</code>.
     * @param start the initial value
     * @param end the final value
     * @param weight the desired weighting of the two values, where 0.0 = start, 1.0 = end, and 0.5 = their mean
     * @return the linear interpolation of the two values
     */
    public static double lerp(double start, double end, double weight) {
        return start * (1.0 - weight) + end * weight;
    }

    /**
     * Interpolates linearly ("lerps") between a start and end value based on the specified weighting. Note that this
     * method does not enforce any constraints on <code>weight</code>.
     * @param start the initial value
     * @param end the final value
     * @param weight the desired weighting of the two values, where 0.0 = start, 1.0 = end, and 0.5 = their mean
     * @return the linear interpolation of the two values
     */
    public static Vector3f lerp3(Vector3fc start, Vector3fc end, float weight) {
        return new Vector3f(
            start.x() * (1.0f - weight) + end.x() * weight,
            start.y() * (1.0f - weight) + end.y() * weight,
            start.z() * (1.0f - weight) + end.z() * weight
        );
    }

    /**
     * Finds the smallest angle in degrees between the two given angles, constrained the the range [-180, 180].
     * @param startAngle the starting angle
     * @param endAngle the ending angle
     * @return the angle between
     */
    public static double angleBetween(double startAngle, double endAngle) {
        double delta = clampAngle(endAngle) - clampAngle(startAngle);
        if(Math.abs(delta) > 180.0) {
            delta -= Math.copySign(360.0, delta);
        }

        return delta;
    }

    /**
     * Determines the maximum value amongst the arguments.
     * @param first the first value to compare
     * @param rest the remaining values to compare
     * @return the maximum value
     */
    public static int max(int first, int...rest) {
        int max = first;

        for(int num : rest) {
            if(num > max) max = num;
        }

        return max;
    }

    /**
     * Determines the maximum value amongst the arguments.
     * @param first the first value to compare
     * @param rest the remaining values to compare
     * @return the maximum value
     */
    public static double max(double first, double...rest) {
        double max = first;

        for(double num : rest) {
            if(num > max) max = num;
        }

        return max;
    }

}
