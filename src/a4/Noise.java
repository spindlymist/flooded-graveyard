package a4;

import java.awt.*;
import java.util.Random;

public class Noise {

    protected final int width, height, depth;
    private final double[][][] noise;
    private final static Random random = new Random();

    public Noise(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        noise = new double[width][height][depth];
        generateNoise();
    }

    public byte[] generateData(int octaves) {
        byte[] data = new byte[width * height * depth * 4];
        int i = 0;

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                for(int z = 0; z < depth; z++) {
                    Color cellColor = generateCell(x, y, z, octaves);
                    data[i++] = (byte) cellColor.getRed();
                    data[i++] = (byte) cellColor.getGreen();
                    data[i++] = (byte) cellColor.getBlue();
                    data[i++] = (byte) cellColor.getAlpha();
                }
            }
        }

        return data;
    }

    protected Color generateCell(double x, double y, double z, int octaves) {
        int sample = (int) turbulentSample(x, y, z, octaves);
        return new Color(sample, sample, sample, 255);
    }

    private void generateNoise() {
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                for(int z = 0; z < depth; z++) {
                    noise[x][y][z] = random.nextDouble();
                }
            }
        }
    }

    protected double smoothSample(double x, double y, double z, double zoom) {
        int x1 = (int) x;
        int y1 = (int) y;
        int z1 = (int) z;

        double tx = x - x1;
        double ty = y - y1;
        double tz = z - z1;

        int x2 = x1 - 1;
        if (x2 < 0) x2 = (int) (Math.round(width / zoom) - 1);
        int y2 = y1 - 1;
        if (y2 < 0) y2 = (int) (Math.round(height / zoom) - 1);
        int z2 = z1 - 1;
        if (z2 < 0) z2 = (int) (Math.round(depth / zoom) - 1);

        double sample = 0.0;

        sample += tx         * ty         * tz         * noise[x1][y1][z1];
        sample += (1.0 - tx) * ty         * tz         * noise[x2][y1][z1];
        sample += tx         * (1.0 - ty) * tz         * noise[x1][y2][z1];
        sample += (1.0 - tx) * (1.0 - ty) * tz         * noise[x2][y2][z1];

        sample += tx         * ty         * (1.0 - tz) * noise[x1][y1][z2];
        sample += (1.0 - tx) * ty         * (1.0 - tz) * noise[x2][y1][z2];
        sample += tx         * (1.0 - ty) * (1.0 - tz) * noise[x1][y2][z2];
        sample += (1.0 - tx) * (1.0 - ty) * (1.0 - tz) * noise[x2][y2][z2];

        return sample;
    }

    protected double turbulentSample(double x, double y, double z, int octaves) {
        double sample = 0.0;
        double maxZoom = Math.pow(2, octaves);
        double zoom = maxZoom;
        while(zoom >= .9) {
            sample += smoothSample(x / zoom, y / zoom, z / zoom, zoom) * zoom;
            zoom /= 2.0;
        }
        sample = 128.0 * sample / maxZoom; // should be adjusted based on octaves

        return sample;
    }

}
