package narz.tiizzer.example;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import narz.tiizzer.camecame.base.BaseControlView;

/**
 * Created by narztiizzer on 8/25/2016 AD.
 */
public class CaptureController extends BaseControlView {

    @Override
    public void onCapture(Bitmap bmp, Camera camera) {
        Log.d("TAG" , "Capture");
    }

    @Override
    public void onFlashStateChanged(String flashState, String message) {
        Log.d("TAG" , message);
    }

    @Override
    public void onSwitchedCamera(int cameraId, String message) {
        Log.d("TAG" , message);
    }

    @Override
    public int setControlLayout() {
        return R.layout.control_layout;
    }

    @Override
    public void onControlViewCreated(View controlView, Bundle savedInstanceState) {
        Log.d("Control view" , "Control view created");
    }

    @Override
    public int setCaptureControlView() {
        return R.id.captureButton;
    }

    @Override
    public int setSwitchCameraControlView() {
        return R.id.switchCameraButton;
    }

    @Override
    public int setFlashControlView() {
        return R.id.flashButton;
    }

}
