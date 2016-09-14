package narz.tiizzer.camecame.base;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import narz.tiizzer.camecame.InitialCamera;
import narz.tiizzer.camecame.interfaces.BaseControlViewInterface;

/**
 * Created by narztiizzer on 8/25/2016 AD.
 */
public abstract class BaseControlView extends Fragment  {
    public View switchCameraButton;
    public View flashButton;
    public View captureButton;
    private BaseControlViewInterface controlInterface;

    @SuppressWarnings("deprecation")
    public abstract void onCapture(Bitmap bmp , Camera camera);
    public abstract void onFlashStateChanged(String flashState , String message);
    public abstract void onSwitchedCamera(int cameraId , String message);

    @LayoutRes
    public abstract int setControlLayout();
    public abstract void onControlViewCreated(View controlView , Bundle savedInstanceState);

    public abstract int setCaptureControlView();
    public abstract int setSwitchCameraControlView();
    public abstract int setFlashControlView();

    public View getCaptureControlView() { return captureButton; }
    public View getSwitchCameraControlView() { return switchCameraButton; }
    public View getFlashControlView() { return flashButton; }

    public void setControlInterface(BaseControlViewInterface controlInterface) {
        this.controlInterface = controlInterface;
    }
    public void invalidateControlView() {
        if(controlInterface != null)
            controlInterface.onControlViewCreated();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(setControlLayout() , container , false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.switchCameraButton = view.findViewById(setSwitchCameraControlView());
        this.captureButton = view.findViewById(setCaptureControlView());
        this.flashButton = view.findViewById(setFlashControlView());

        onControlViewCreated(view , savedInstanceState);
        invalidateControlView();
    }


}
