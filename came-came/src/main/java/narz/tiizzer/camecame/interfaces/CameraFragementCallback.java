package narz.tiizzer.camecame.interfaces;

import android.graphics.Bitmap;
import android.hardware.Camera;

/**
 * Created by narztiizzer on 8/9/2016 AD.
 */
public interface CameraFragementCallback {
    void onCaptureImage(Bitmap bitmap, String tag, Camera camera);
    void onSwitchedCamera();
}
