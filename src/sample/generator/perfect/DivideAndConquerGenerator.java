package sample.generator.perfect;

import sample.UtilsRandom;

import java.util.*;
import java.awt.Point;

public class DivideAndConquerGenerator extends PerfectWallGenerator {

    private static final Point NO_ENTER = new Point(-1, -1);

    @Override
    protected void generate() {
        cut(1, maze.width, 1, maze.height, Collections.singletonList(new Point(maze.startX, maze.startY)));
    }

    private void cut(int leftX, int rightX, int leftY, int rightY, List<Point> enters) {
        int xSize = (rightX - leftX);
        int ySize = (rightY - leftY);

        if (xSize <= 1 || ySize <= 1) {
            return;
        }

        if (xSize == 2 && ySize == 2) {
            List<Point> possibleCuts = new ArrayList<>();

            for (int x = leftX; x < rightX; ++x) {
                for (int y = leftY; y < rightY; ++y) {
                    possibleCuts.add(new Point(x, y));
                }
            }

            for (Point enter : enters) {
                possibleCuts.remove(enter);
            }

            if (possibleCuts.size() > 0) {
                Point cut = UtilsRandom.nextElement(possibleCuts);
                cutCell(cut);
            }

            return;
        }

        List<Integer> possibleXs = new ArrayList<>();
        List<Integer> possibleYs = new ArrayList<>();

        for (int x = leftX; x < rightX; ++x) {
            possibleXs.add(x);
        }

        for (int y = leftY; y < rightY; ++y) {
            possibleYs.add(y);
        }

        for (Point enter : enters) {
            possibleXs.remove(Integer.valueOf(enter.x));
            possibleYs.remove(Integer.valueOf(enter.y));
        }

        if (possibleXs.isEmpty() && possibleYs.isEmpty()) return;

        if ((xSize <= 2 || possibleXs.isEmpty()) && !possibleYs.isEmpty()) {
            // only y
            cutY(leftX, rightX, leftY, rightY, enters, possibleYs);
        } else if (ySize <= 2 || possibleYs.isEmpty()) {
            // only x
            cutX(leftX, rightX, leftY, rightY, enters, possibleXs);
        } else {
            // x and y
            int possibleSizeSum = possibleXs.size() + possibleYs.size();
            if (UtilsRandom.nextInt(0, possibleSizeSum) < possibleXs.size()) {
                cutX(leftX, rightX, leftY, rightY, enters, possibleXs);
            } else {
                cutY(leftX, rightX, leftY, rightY, enters, possibleYs);
            }
        }
    }

    private void cutY(int leftX, int rightX, int leftY, int rightY, List<Point> enters, List<Integer> possibleYs) {
        final int divideY = UtilsRandom.nextElement(possibleYs);
        final int divideEnterX = UtilsRandom.nextInt(leftX, rightX);

        for (int x = leftX; x < rightX; ++x) {
            if (divideEnterX == x) continue;

            boolean needCut = true;
            for (Point enter : enters) {
                if (enter.y == divideY && enter.x == x) {
                    needCut = false;
                    break;
                }
            }

            if (needCut) {
                cutCell(x, divideY);
            }

        }

        List<Point> leftEnters = new ArrayList<>();
        for (Point enter : enters) {
            if (enter.y < divideY) {
                leftEnters.add(enter);
            }
        }

        leftEnters.add(new Point(divideEnterX, divideY - 1));
        cut(leftX, rightX, leftY, divideY, leftEnters);

        List<Point> rightEnters = new ArrayList<>();
        for (Point enter : enters) {
            if (enter.y > divideY) {
                rightEnters.add(enter);
            }
        }

        rightEnters.add(new Point(divideEnterX, divideY + 1));
        cut(leftX, rightX, divideY + 1, rightY, rightEnters);
    }

    private void cutX(int leftX, int rightX, int leftY, int rightY, List<Point> enters, List<Integer> possibleXs) {
        final int divideX = UtilsRandom.nextElement(possibleXs);
        final int divideEnterY = UtilsRandom.nextInt(leftY, rightY);

        for (int y = leftY; y < rightY; ++y) {
            if (divideEnterY == y) continue;

            boolean needCut = true;
            for (Point enter : enters) {
                if (enter.x == divideX && enter.y == y) {
                    needCut = false;
                    break;
                }
            }

            if (needCut) {
                cutCell(divideX, y);
            }
        }

        List<Point> leftEnters = new ArrayList<>();
        for (Point enter : enters) {
            if (enter.x < divideX) {
                leftEnters.add(enter);
            }
        }

        leftEnters.add(new Point(divideX - 1, divideEnterY));
        cut(leftX, divideX, leftY, rightY, leftEnters);

        List<Point> rightEnters = new ArrayList<>();
        for (Point enter : enters) {
            if (enter.x > divideX) {
                rightEnters.add(enter);
            }
        }

        rightEnters.add(new Point(divideX + 1, divideEnterY));
        cut(divideX + 1, rightX, leftY, rightY, rightEnters);
    }
}
