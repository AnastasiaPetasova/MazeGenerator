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

        if (xSize <= 2 && ySize <= 2 && zSize <= 2) {
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

        List<Point3D> possibleXs = new ArrayList<>();
        List<Point3D> possibleYs = new ArrayList<>();
        List<Point3D> possibleZs = new ArrayList<>();

        for (int x = leftX; x < rightX; ++x) {
            for (int y = leftY; y < rightY; ++y) {
                for (int z = leftZ; z < rightZ; ++z) {
                    Point3D point = new Point3D(x, y, z);
                    if (enters.contains(point)) continue;

                    if (leftX < x && x < rightX - 1) possibleXs.add(point);
                    if (leftY < y && y < rightY - 1) possibleYs.add(point);
                    if (leftZ < z && z < rightZ - 1) possibleZs.add(point);
                }
            }
        }

        int possibleXSize = possibleXs.size();
        int possibleYSize = possibleYs.size();
        int possibleZSize = possibleZs.size();

        int totalSize = possibleXSize + possibleYSize + possibleZSize;
        if (0 == totalSize) return;

        int totalIndex = UtilsRandom.nextInt(0, totalSize);
        if (totalIndex < possibleXSize) {
            cutX(leftX, rightX, leftY, rightY, leftZ, rightZ, enters, possibleXs.get(totalIndex));
        } else if (totalIndex < possibleXSize + possibleYSize) {
            cutY(leftX, rightX, leftY, rightY, leftZ, rightZ, enters, possibleYs.get(totalIndex - possibleXSize));
        } else {
            cutZ(leftX, rightX, leftY, rightY, leftZ, rightZ, enters, possibleZs.get(totalIndex - possibleXSize - possibleYSize));
        }
    }

    private void cutZ(int leftX, int rightX, int leftY, int rightY, int leftZ, int rightZ,
                      List<Point3D> enters, Point3D dividePoint) {
        final int divideEnterX = dividePoint.x;
        final int divideEnterY = dividePoint.y;
        final int divideEnterZ = dividePoint.z;

        for (int x = leftX; x < rightX; ++x) {
            for (int y = leftY; y < rightY; ++y) {
                if (divideEnterX == x && divideEnterY == y) continue;

                boolean needCut = true;
                for (Point3D enter : enters) {
                    if (enter.equalsTo(x, y, divideEnterZ)) {
                        needCut = false;
                        break;
                    }
                }

                if (needCut) {
                    cutCell(x, y, divideEnterZ);
                }

            }
        }

        List<Point3D> leftEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.z < divideEnterZ) {
                leftEnters.add(enter);
            }
        }

        leftEnters.add(new Point3D(divideEnterX, divideEnterY, divideEnterZ - 1));
        cut(leftX, rightX, leftY, rightY, leftZ, divideEnterZ, leftEnters);

        List<Point3D> rightEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.z > divideEnterZ) {
                rightEnters.add(enter);
            }
        }

        rightEnters.add(new Point3D(divideEnterX, divideEnterY, divideEnterZ + 1));
        cut(leftX, rightX, leftY, rightY, divideEnterZ + 1, rightZ, rightEnters);
    }

    private void cutY(int leftX, int rightX, int leftY, int rightY, int leftZ, int rightZ,
                      List<Point3D> enters, Point3D dividePoint) {
        final int divideEnterX = dividePoint.x;
        final int divideEnterY = dividePoint.y;
        final int divideEnterZ = dividePoint.z;

        for (int z = leftZ; z < rightZ; ++z) {
            for (int x = leftX; x < rightX; ++x) {
                if (divideEnterX == x && divideEnterZ == z) continue;

                boolean needCut = true;
                for (Point3D enter : enters) {
                    if (enter.equalsTo(x, divideEnterY, z)) {
                        needCut = false;
                        break;
                    }
                }

                if (needCut) {
                    cutCell(x, divideEnterY, z);
                }

            }
        }

        List<Point3D> leftEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.y < divideEnterY) {
                leftEnters.add(enter);
            }
        }

        leftEnters.add(new Point3D(divideEnterX, divideEnterY - 1, divideEnterZ));
        cut(leftX, rightX, leftY, divideEnterY, leftZ, rightZ, leftEnters);

        List<Point3D> rightEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.y > divideEnterY) {
                rightEnters.add(enter);
            }
        }

        rightEnters.add(new Point3D(divideEnterX, divideEnterY + 1, divideEnterZ));
        cut(leftX, rightX, divideEnterY + 1, rightY, leftZ, rightZ, rightEnters);
    }

    private void cutX(int leftX, int rightX, int leftY, int rightY, int leftZ, int rightZ,
                      List<Point3D> enters, Point3D dividePoint) {
        final int divideEnterX = dividePoint.x;
        final int divideEnterY = dividePoint.y;
        final int divideEnterZ = dividePoint.z;

        for (int z = leftZ; z < rightZ; ++z) {
            for (int y = leftY; y < rightY; ++y) {
                if (divideEnterY == y && divideEnterZ == z) continue;

                boolean needCut = true;
                for (Point3D enter : enters) {
                    if (enter.equalsTo(divideEnterX, y, z)) {
                        needCut = false;
                        break;
                    }
                }

                if (needCut) {
                    cutCell(divideEnterX, y, z);
                }
            }
        }

        List<Point3D> leftEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.x < divideEnterX) {
                leftEnters.add(enter);
            }
        }

        leftEnters.add(new Point3D(divideEnterX - 1, divideEnterY, divideEnterZ));
        cut(leftX, divideEnterX, leftY, rightY, leftZ, rightZ, leftEnters);

        List<Point3D> rightEnters = new ArrayList<>();
        for (Point3D enter : enters) {
            if (enter.x > divideEnterX) {
                rightEnters.add(enter);
            }
        }

        rightEnters.add(new Point3D(divideEnterX + 1, divideEnterY, divideEnterZ));
        cut(divideEnterX + 1, rightX, leftY, rightY, leftZ, rightZ, rightEnters);
    }
}
