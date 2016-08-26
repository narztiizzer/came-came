package narz.tiizzer.camecame.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import narz.tiizzer.camecame.InitialCamera;
import narz.tiizzer.camecame.interfaces.BaseControlViewInterface;

/**
 * Created by narztiizzer on 8/25/2016 AD.
 */
public abstract class BaseControlView extends Fragment {
    public View switchCameraButton;
    public View flashButton;
    public View captureButton;
    private BaseControlViewInterface controlInterface;

    public abstract int setFrontCameraIcon();
    public abstract int setRearCameraIcon();
    public abstract int setCaptureIcon();
    public abstract int setRetakeIcon();
    public abstract int setFlashOnIcon();
    public abstract int setFlashOffIcon();
    public abstract int setFlashAutoIcon();
    public abstract String setControlBackgroundColor();

    @LayoutRes
    public abstract int setControlLayout();
    public abstract void onControlViewCreated(View controlView , Bundle savedInstanceState);

    public void setCaptureControlView(View captureButton) {  this.captureButton = captureButton; }
    public void setSwitchCameraControlView(View switchCameraButton) { this.switchCameraButton = switchCameraButton; }
    public void setFlashControlView(View flashButton) { this.flashButton = flashButton; }

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
        onControlViewCreated(view , savedInstanceState);
    }


}
