package sample;

import java.util.List;
import java.util.Random;

public class UtilsRandom {

    private final static Random random;

    static {
        random = new Random();
    }

    public static boolean nextBoolean() {
        return random.nextBoolean();
    }

    public static int nextIndex(List<?> values) {
        return random.nextInt(values.size());
    }

    public static <T> T nextElement(List<T> values) {
        return values.get(nextIndex(values));
    }

    public static int nextInt(int leftInclusive, int rightExclusive) {
        int size = rightExclusive - leftInclusive;
        return random.nextInt(size) + leftInclusive;
    }

    public static double nextProbability() {
        return random.nextDouble();
    }
}
