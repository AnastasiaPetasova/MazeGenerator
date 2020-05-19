package sample.generator.perfect;

import sample.Point3D;
import sample.UtilsRandom;

public class PrimeRandomGenerator extends PrimeGenerator {

    Point3D calculateFrom() {
        return UtilsRandom.nextElement(activePoints);
    }
}
