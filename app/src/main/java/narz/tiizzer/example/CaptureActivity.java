package narz.tiizzer.example;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import narz.tiizzer.camecame.base.BaseCaptureActivity;
import narz.tiizzer.camecame.base.BaseControlView;

/**
 * Created by narztiizzer on 8/25/2016 AD.
 */
public class CaptureActivity extends BaseCaptureActivity {

    private String currentFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
    private int mCameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;

    @Override
    public boolean isCropSquareImage() {
        return false;
    }

    @Override
    public boolean isShowFocusPoint() {
        return false;
    }

    @NonNull
    @Override
    public BaseControlView setControlView() {
        return new CaptureController();
    }

    @Override
    public void setCameraPosition(int position) {
        mCameraPosition = position;
    }

    @Override
    public int getCurrentCameraPosition() {
        return mCameraPosition;
    }

    @Override
    public String getCurrentFlashMode() {
        return currentFlashMode;
    }

    @Override
    public void setCurrentFlashMode(String currentFlashMode) {
        this.currentFlashMode = currentFlashMode;
    }

}
