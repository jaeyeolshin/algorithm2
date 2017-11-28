import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private int[][] colors;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        height = picture.height();
        width = picture.width();
        colors = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                colors[y][x] = picture.get(x, y).getRGB();
            }
        }
    }

    private double square(double d) {
        return d * d;
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = colors[y][x];
                picture.set(x, y, new Color(rgb));
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    private int red(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    private int blue(int rgb) {
        return (rgb >> 0) & 0xFF;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return 1000;
        }

        int rgbLeft = colors[y][x - 1];
        int rgbRight = colors[y][x + 1];
        int rgbUp = colors[y - 1][x];
        int rgbDown = colors[y + 1][x];
        double dx = square(red(rgbRight) - red(rgbLeft)) +
                square(green(rgbRight) - green(rgbLeft)) +
                square(blue(rgbRight) - blue(rgbLeft));
        double dy = square(red(rgbDown) - red(rgbUp)) +
                square(green(rgbDown) - green(rgbUp)) +
                square(blue(rgbDown) - blue(rgbUp));
        return Math.sqrt(dx + dy);
    }

    private void transpose() {
        int[][] transposed = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                transposed[x][y] = colors[y][x];
            }
        }
        colors = transposed;
        int temp = width;
        width = height;
        height = temp;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] seam =  findVerticalSeam();
        transpose();
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int size = width * height + 2;
        int[] pathTo = new int[size];
        double[] distTo = new double[size];

        int start = 0, end = size - 1;
        for (int i = 0; i < size; i++) {
            pathTo[i] = -1;
            distTo[i] = Double.POSITIVE_INFINITY;
        }
        distTo[start] = 0.0;

        for (int x = 0; x < width; x++) {
            int v = vertex(x, 0);
            pathTo[v] = 0;
            distTo[v] = energy(x, 0);
        }

        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double energy = energy(x, y);
                int v = vertex(x, y);

                if (x > 0) {
                    int lu = vertex(x - 1, y - 1);
                    if (distTo[v] > distTo[lu] + energy) {
                        distTo[v] = distTo[lu] + energy;
                        pathTo[v] = lu;
                    }
                }
                int up = vertex(x, y - 1);
                if (distTo[v] > distTo[up] + energy) {
                    distTo[v] = distTo[up] + energy;
                    pathTo[v] = up;
                }

                if (x < width - 1) {
                    int ru = vertex(x + 1, y - 1);
                    if (distTo[v] > distTo[ru] + energy) {
                        distTo[v] = distTo[ru] + energy;
                        pathTo[v] = ru;
                    }
                }
            }
        }

        for (int x = 0; x < width; x++) {
            int v = vertex(x, height - 1);
            if (distTo[end] > distTo[v]) {
                distTo[end] = distTo[v];
                pathTo[end] = v;
            }
        }

        int[] path = new int[height];
        int fv = end;
        for (int i = height - 1; i >= 0; i--) {
            fv = pathTo[fv];
            path[i] = x(fv);
        }

        return path;
    }

    private int vertex(int x, int y) {
        return y * width() + x + 1;
    }

    private int x(int v) {
        return (v - 1) % width();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam);
        for (int y = 0; y < seam.length; y++) {
            int x = seam[y];
            int len = width - x - 1;
            System.arraycopy(colors[y], x + 1, colors[y], x, len);
        }
        width--;
    }

    private void validateSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam is null");
        }

        int length = height;
        int max = width - 1;

        if (max <= 0) {
            throw new IllegalArgumentException("carving is not available");
        }

        if (seam.length != length) {
            throw new IllegalArgumentException("wrong seam length");
        }

        int prev = seam[0];
        if (prev < 0 || prev > max) {
            throw new IllegalArgumentException("wrong seam path");
        }

        for (int i = 1; i < length; i++) {
            int current = seam[i];
            if (current < 0 || current > max || current < prev - 1 || current > prev + 1) {
                throw new IllegalArgumentException("wrong seam path");
            }
            prev = current;
        }
    }
}