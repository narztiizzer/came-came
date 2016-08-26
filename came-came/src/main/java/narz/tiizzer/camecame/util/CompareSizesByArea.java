package narz.tiizzer.camecame.util;

import android.hardware.Camera;

import java.util.Comparator;

/**
 * Created by narztiizzer on 8/19/2016 AD.
 */
public class CompareSizesByArea implements Comparator<Camera.Size> {
    @Override
    public int compare(Camera.Size lhs, Camera.Size rhs) {
        // We cast here to ensure the multiplications won't overflow
        return Long.signum((long) lhs.width * lhs.height -
                (long) rhs.width * rhs.height);
    }
}
