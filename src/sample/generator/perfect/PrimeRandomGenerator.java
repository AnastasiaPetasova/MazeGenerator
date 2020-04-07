package sample.generator.perfect;

import sample.UtilsRandom;

import java.awt.*;

public class PrimeRandomGenerator extends PrimeGenerator {

    Point calculateFrom() {
        return UtilsRandom.nextElement(activePoints);
    }
}
