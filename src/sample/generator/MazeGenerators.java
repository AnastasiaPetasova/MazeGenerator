package sample.generator;

import sample.MazeGenerator;
import sample.generator.perfect.*;
import sample.generator.random.RandomGenerator;
import sample.generator.random.RandomWalkGenerator;

import java.util.ArrayList;
import java.util.List;

public class MazeGenerators {

    private final static List<MazeGenerator> generators;

    public static List<MazeGenerator> all() {
        return generators;
    }

    static {
        generators = new ArrayList<>();

        generators.add(new RandomGenerator());
        generators.add(new RandomWalkGenerator());

        generators.add(new ReverseBacktrackingGenerator());
        generators.add(new PrimeRandomGenerator());
        generators.add(new PrimeWeightsGenerator());

        generators.add(new DivideAndConquerGenerator());
    }
}
