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

    @SuppressWarnings("deprecation")
    void onCapture(Bitmap bmp , Camera camera);

    void setCameraPosition(int position);

    void toggleCameraPosition();

    void setFrontCamera(Object id);

    void setBackCamera(Object id);

    Object getCurrentCameraId();

    Object getFrontCamera();

    Object getBackCamera();

    float videoPreferredAspect();

    boolean isUseRectangularMode();

    int getCurrentCameraPosition();

    int videoPreferredHeight();

    String getCurrentFlashMode();

    void setCurrentFlashMode(String mode);

    @NonNull
    BaseControlView setControlView();

    boolean isRectangularMode();

    boolean isShowFocusPoint();

    @NonNull
    String setFrameControlBackground();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    int setStatusBarColor();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    int setNavigationBarColor();
}
