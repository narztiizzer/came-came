package narz.tiizzer.camecame.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by NARZTIIZZER on 1/29/2016.
 */
public class ScreenHelper {
    public static DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    // Get display size

    public static int getOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static int getScreenWidth() {
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        return metrics.heightPixels;
    }

    public static boolean isTabletSize(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static int getDisplayRotationDegree(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return displayDegree.DEGREES_0;
            case Surface.ROTATION_90:
                return displayDegree.DEGREES_90;
            case Surface.ROTATION_180:
                return displayDegree.DEGREES_180;
            case Surface.ROTATION_270:
                return displayDegree.DEGREES_270;
        }
        return displayDegree.DEGREES_0;
    }

    public static int getDisplayOrientation(int sensorOrientation , int displayOrientation, boolean front) {
        final boolean isLandscape = isLandscape(displayOrientation);
        if (displayOrientation == displayDegree.DEGREES_0)
            displayOrientation = displayDegree.DEGREES_360;
        int result = sensorOrientation - displayOrientation;
        result = naturalize(result);
        if (isLandscape && front)
            result = mirror(result);
        return result;
    }

    public static int mirror(int orientation) {
        switch (orientation) {
            case displayDegree.DEGREES_0:
                return displayDegree.DEGREES_180;
            case displayDegree.DEGREES_90:
                return displayDegree.DEGREES_270;
            case displayDegree.DEGREES_180:
                return displayDegree.DEGREES_0;
            case displayDegree.DEGREES_270:
                return displayDegree.DEGREES_90;
        }
        return displayDegree.DEGREES_0;
    }

    @SuppressWarnings("ResourceType")
    public static int naturalize(int orientation) {
        if (orientation == 360)
            orientation = 0;
        else if (orientation > 360) {
            do {
                orientation = orientation - 360;
            } while (orientation > 360);
        } else if (orientation < 0) {
            do {
                orientation = 360 + orientation;
            } while (orientation < 0);
        }
        return orientation;
    }

    public static boolean isPortrait(Context activity) {
        return isPortrait(getDisplayRotationDegree(activity));
    }

    public static boolean isLandscape(Context activity) {
        return isLandscape(getDisplayRotationDegree(activity));
    }

    public static boolean isPortrait(int degrees) {
        return degrees == displayDegree.DEGREES_0 || degrees == displayDegree.DEGREES_180 || degrees == displayDegree.DEGREES_360;
    }

    public static boolean isLandscape(int degrees) {
        return degrees == displayDegree.DEGREES_90 || degrees == displayDegree.DEGREES_270;
    }

    public static class displayDegree {
        public static final int DEGREES_0 = 0;
        public static final int DEGREES_90 = 90;
        public static final int DEGREES_180 = 180;
        public static final int DEGREES_270 = 270;
        public static final int DEGREES_360 = 360;
    }
}
