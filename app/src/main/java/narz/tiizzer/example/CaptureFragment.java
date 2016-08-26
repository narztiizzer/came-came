package narz.tiizzer.example;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;

import narz.tiizzer.camecame.base.BaseCaptureFragment;
import narz.tiizzer.camecame.base.BaseControlView;

/**
 * Created by narztiizzer on 8/25/2016 AD.
 */
public class CaptureFragment extends BaseCaptureFragment {

    public static CaptureFragment newInstance() {
        return new CaptureFragment();
    }

    @NonNull
    @Override
    public String setFrameControlBackground() {
        return "#FFFFFF";
    }

    @NonNull
    @Override
    public int setStatusBarColor() {
        return 0;
    }

    @NonNull
    @Override
    public int setNavigationBarColor() {
        return 0;
    }

    @Override
    public boolean isRectangularMode() {
        return false;
    }

    @Override
    public boolean isShowFocusPoint() {
        return false;
    }

    @NonNull
    @Override
    public BaseControlView setControlView() {
        CaptureController captureController = new CaptureController();
        return captureController;
    }

    @Override
    public void onCapture(Bitmap bmp, Camera camera) {
        Log.d("CAPTURE" , "Capture result");
        camera.startPreview();
    }

    @Override
    public boolean isUseRectangularMode() {
        return isRectangularMode();
    }
}
