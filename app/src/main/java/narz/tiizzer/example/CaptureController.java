package narz.tiizzer.example;

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
    public int setFrontCameraIcon() {
        return 0;
    }

    @Override
    public int setRearCameraIcon() {
        return 0;
    }

    @Override
    public int setCaptureIcon() {
        return 0;
    }

    @Override
    public int setRetakeIcon() {
        return 0;
    }

    @Override
    public int setFlashOnIcon() {
        return 0;
    }

    @Override
    public int setFlashOffIcon() {
        return 0;
    }

    @Override
    public int setFlashAutoIcon() {
        return 0;
    }

    @Override
    public String setControlBackgroundColor() {
        return null;
    }

    @Override
    public int setControlLayout() {
        return R.layout.control_layout;
    }

    @Override
    public void onControlViewCreated(View controlView, Bundle savedInstanceState) {
        setSwitchCameraControlView(controlView.findViewById(R.id.switchCameraButton));
        setFlashControlView(controlView.findViewById(R.id.flashButton));
        setCaptureControlView(controlView.findViewById(R.id.captureButton));
        Log.d("Control view" , "Control view created");
        invalidateControlView();
    }

}
