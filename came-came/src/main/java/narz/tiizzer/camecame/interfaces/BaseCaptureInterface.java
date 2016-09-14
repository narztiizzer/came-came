package narz.tiizzer.camecame.interfaces;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import narz.tiizzer.camecame.base.BaseControlView;

/**
 * Created by narztiizzer on 8/19/2016 AD.
 */

public interface BaseCaptureInterface {

    void setCameraPosition(int position);

    boolean toggleCameraPosition();

    float videoPreferredAspect();

    int getCurrentCameraPosition();

    int videoPreferredHeight();

    String getCurrentFlashMode();

    void setCurrentFlashMode(String mode);

    @NonNull
    BaseControlView setControlView();

    boolean isCropSquareImage();

    boolean isShowFocusPoint();
}
