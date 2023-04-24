package a4;

import java.awt.*;

public class WaterNoise extends Noise {

    public WaterNoise(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    protected Color generateCell(double x, double y, double z, int octaves) {
        double wave = 8.0 * (1.0 + Math.sin((1.0 / (width * 2.0)) * (8.0 * Math.PI) * (x + z - 4*y)));
        int sample = (int) MathUtil.clamp(turbulentSample(x, y, z, octaves) + wave, 0.0, 255.0);

        return new Color(sample, sample, sample, 255);
    }

}
