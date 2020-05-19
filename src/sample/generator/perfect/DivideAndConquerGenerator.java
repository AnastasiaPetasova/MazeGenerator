package sample.generator.perfect;

import sample.Point3D;
import sample.UtilsRandom;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DivideAndConquerGenerator extends PerfectWallGenerator {

    private static final Point NO_ENTER = new Point(-1, -1);

    @Override
    protected void generate() {
        cut(1, maze.width, 1, maze.height, 1, maze.layersCount,
                Collections.singletonList(new Point3D(maze.start))
        );
    }

    private void cut(int leftX, int rightX, int leftY, int rightY, int leftZ, int rightZ,
                     List<Point3D> enters) {
        int xSize = (rightX - leftX);
        int ySize = (rightY - leftY);
        int zSize = (rightZ - leftZ);

        if (xSize < 1 || ySize < 1 || zSize < 1) return;

        int largesCount = 0;
        if (1 < xSize) ++largesCount;
        if (1 < ySize) ++largesCount;
        if (1 < zSize) ++largesCount;

        if (largesCount < 2) return;

        if (xSize == 2 && ySize == 2 && zSize == 2) {
            List<Point3D> possibleCuts = new ArrayList<>();

            for (int z = leftZ; z < rightZ; ++z) {
                for (int x = leftX; x < rightX; ++x) {
                    for (int y = leftY; y < rightY; ++y) {
                        possibleCuts.add(new Point3D(x, y, z));
                    }
                }
            }

            for (Point3D enter : enters) {
                possibleCuts.remove(enter);
            }

            if (possibleCuts.size() > 0) {
                Point3D cut = UtilsRandom.nextElement(possibleCuts);
                cutCell(cut);
            }

            return;
        }

        List<Integer> possibleXs = new ArrayList<>();
        List<Integer> possibleYs = new ArrayList<>();
        List<Integer> possibleZs = new ArrayList<>();

        for (int x = leftX; x < rightX; ++x) {
            possibleXs.add(x);
        }

        for (int y = leftY; y < rightY; ++y) {
            possibleYs.add(y);
        }

        for (int z = leftZ; z < rightZ; ++z) {
            possibleZs.add(z);
        }

        for (Point3D enter : enters) {
            possibleXs.remove(Integer.valueOf(enter.x));
            possibleYs.remove(Integer.valueOf(enter.y));
            possibleZs.remove(Integer.valueOf(enter.z));
        }

        if (possibleXs.isEmpty() && possibleYs.isEmpty() && possibleZs.isEmpty()) return;

        int possibleXSize = (xSize <= 2 || possibleXs.isEmpty()) ? 0 : possibleXs.size();
        int possibleYSize = (ySize <= 2 || possibleYs.isEmpty()) ? 0 : possibleYs.size();
        int possibleZSize = (zSize <= 2 || possibleZs.isEmpty()) ? 0 : possibleZs.size();

        int totalSize = possibleXSize + possibleYSize + possibleZSize;
        if (0 == totalSize) return;

        int totalIndex = UtilsRandom.nextInt(0, totalSize);
        if (totalIndex < possibleXSize) {
            cutX(leftX, rightX, leftY, rightY, leftZ, rightZ, enters, possibleXs);
        } else if (totalIndex < possibleXSize + possibleYSize) {
            cutY(leftX, rightX, leftY, rightY, leftZ, rightZ, enters, possibleYs);
        } else {
            cutZ(leftX, rightX, leftY, rightY, leftZ, rightZ, enters, possibleZs);
        }
    }

    private void cutZ(int leftX, int rightX, int leftY, int rightY, int leftZ, int rightZ,
                      List<Point3D> enters, List<Integer> possibleZs) {
        final int divideZ = UtilsRandom.nextElement(possibleZs);
        final int divideEnterX = UtilsRandom.nextInt(leftX, rightX);
        final int divideEnterY = UtilsRandom.nextInt(leftY, rightY);

        for (int x = leftX; x < rightX; ++x) {
            for (int y = leftY; y < rightY; ++y) {
                if (divideEnterX == x && divideEnterY == y) continue;

                boolean needCut = true;
                for (Point3D enter : enters) {
                    if (enter.equalsTo(x, y, divideZ)) {
                        needCut = false;
                        break;
                    }
                }

                if (needCut) {
                    cutCell(x, y, divideZ);
                }

            }
        }

        List<Point3D> leftEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.z < divideZ) {
                leftEnters.add(enter);
            }
        }

        leftEnters.add(new Point3D(divideEnterX, divideEnterY, divideZ - 1));
        cut(leftX, rightX, leftY, rightY, leftZ, divideZ, leftEnters);

        List<Point3D> rightEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.z > divideZ) {
                rightEnters.add(enter);
            }
        }

        rightEnters.add(new Point3D(divideEnterX, divideEnterY, divideZ + 1));
        cut(leftX, rightX, leftY, rightY, divideZ + 1, rightZ, rightEnters);
    }

    private void cutY(int leftX, int rightX, int leftY, int rightY, int leftZ, int rightZ,
                      List<Point3D> enters, List<Integer> possibleYs) {
        final int divideY = UtilsRandom.nextElement(possibleYs);
        final int divideEnterX = UtilsRandom.nextInt(leftX, rightX);
        final int divideEnterZ = UtilsRandom.nextInt(leftZ, rightZ);

        for (int z = leftZ; z < rightZ; ++z) {
            for (int x = leftX; x < rightX; ++x) {
                if (divideEnterX == x && divideEnterZ == z) continue;

                boolean needCut = true;
                for (Point3D enter : enters) {
                    if (enter.equalsTo(x, divideY, z)) {
                        needCut = false;
                        break;
                    }
                }

                if (needCut) {
                    cutCell(x, divideY, z);
                }

            }
        }

        List<Point3D> leftEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.y < divideY) {
                leftEnters.add(enter);
            }
        }

        leftEnters.add(new Point3D(divideEnterX, divideY - 1, divideEnterZ));
        cut(leftX, rightX, leftY, divideY, leftZ, rightZ, leftEnters);

        List<Point3D> rightEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.y > divideY) {
                rightEnters.add(enter);
            }
        }

        rightEnters.add(new Point3D(divideEnterX, divideY + 1, divideEnterZ));
        cut(leftX, rightX, divideY + 1, rightY, leftZ, rightZ, rightEnters);
    }

    private void cutX(int leftX, int rightX, int leftY, int rightY, int leftZ, int rightZ,
                      List<Point3D> enters, List<Integer> possibleXs) {
        final int divideX = UtilsRandom.nextElement(possibleXs);
        final int divideEnterY = UtilsRandom.nextInt(leftY, rightY);
        final int divideEnterZ = UtilsRandom.nextInt(leftZ, rightZ);

        for (int z = leftZ; z < rightZ; ++z) {
            for (int y = leftY; y < rightY; ++y) {
                if (divideEnterY == y || divideEnterZ == z) continue;

                boolean needCut = true;
                for (Point3D enter : enters) {
                    if (enter.equalsTo(divideX, y, z)) {
                        needCut = false;
                        break;
                    }
                }

                if (needCut) {
                    cutCell(divideX, y, z);
                }
            }
        }

        List<Point3D> leftEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.x < divideX) {
                leftEnters.add(enter);
            }
        }

        leftEnters.add(new Point3D(divideX - 1, divideEnterY, divideEnterZ));
        cut(leftX, divideX, leftY, rightY, leftZ, rightZ, leftEnters);

        List<Point3D> rightEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.x > divideX) {
                rightEnters.add(enter);
            }
        }

        rightEnters.add(new Point3D(divideX + 1, divideEnterY, divideEnterZ));
        cut(divideX + 1, rightX, leftY, rightY, leftZ, rightZ, rightEnters);
    }
}
