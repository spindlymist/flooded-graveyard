package a4;

import java.util.List;
import java.util.Random;

/**
 * Utility class for generating random values. Note that all client code shares the same generator, and hence changing
 * the seed will impact all client code.
 */
public class RandUtil {

    private static final Random rng = new Random();

    public static void setSeed(long seed) {
        rng.setSeed(seed);
    }

    public static int nextRangedInt(int min, int max) {
        int range = max - min;

        return rng.nextInt(range + 1) + min;
    }

    public static float nextRangedFloat(float min, float max) {
        float range = max - min;

        return (float) (rng.nextDouble() * range + min);
    }

    public static double nextRangedDouble(double min, double max) {
        double range = max - min;

        return rng.nextDouble() * range + min;
    }

    public static boolean nextBoolean() {
        return rng.nextDouble() < 0.5;
    }

    /**
     * Generates a random double in the range [0.0, 360.0).
     * @return the random angle
     */
    public static double nextAngleInDegrees() {
        return nextRangedDouble(0.0, 360.0);
    }

    /**
     * Generates a random double in the range [0.0, 2*PI).
     * @return the random angle
     */
    public static double nextAngleInRadians() {
        return nextRangedDouble(0.0, 2.0 * Math.PI);
    }


    /**
     * Returns one element from a collection at random.
     * @param collection the collection from which to choose
     * @return the chosen element
     */
    public static <T> T chooseOne(List<T> collection) {
        int index = nextRangedInt(0, collection.size() - 1);
        return collection.get(index);
    }

    /**
     * Returns true with the specified probability.
     * @param probability the desired probability of true being returned in the range [0, 1]
     * @return the result of the probabilistic decision
     */
    public static boolean chance(double probability) {
        return Math.random() < probability;
    }

}
