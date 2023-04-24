package a4;

import java.awt.*;

public class MarbleNoise extends Noise {

    public MarbleNoise(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    protected Color generateCell(double x, double y, double z, int octaves) {
        double value = x / width + y / height + z / depth;
        value += 2.0 * turbulentSample(x, y, z, octaves) / 256.0;
        float sineVal = (float) Math.abs(Math.sin(value * Math.PI * 1f));

        return new Color(sineVal, sineVal, sineVal, 1f);
    }
}
