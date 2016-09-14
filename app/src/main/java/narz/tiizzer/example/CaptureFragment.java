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

    private String currentFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
    private int mCameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public static CaptureFragment newInstance() {
        return new CaptureFragment();
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
    public boolean isCropSquareImage() {
        return false;
    }

    @Override
    public String getCurrentFlashMode() {
        return currentFlashMode;
    }

    @Override
    public void setCurrentFlashMode(String currentFlashMode) {
        this.currentFlashMode = currentFlashMode;
    }

    @Override
    public int getCurrentCameraPosition() {
        return mCameraPosition;
    }

    @Override
    public void setCameraPosition(int position) {
        mCameraPosition = position;
    }
}
