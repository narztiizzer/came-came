package narz.tiizzer.camecame;

import android.content.Context;

/**
 * Created by narztiizzer on 8/25/2016 AD.
 */
public class InitialCamera {

    public static final int CAMERA_POSITION_UNKNOWN = 0;
    public static final int CAMERA_POSITION_FRONT = 1;
    public static final int CAMERA_POSITION_BACK = 2;

    public static final int TOUCH_FOCUS_STATE = 99;
    public static final int TOUCH_FOCUS_FAIL = 0;
    public static final int TOUCH_FOCUS_SUCCESS = 1;

    public int frontCameraIcon;
    public int rearCameraIcon;
    public int captureIcon;
    public int retakeIcon;
    public int flashOnIcon;
    public int flashOffIcon;
    public int flashAutoIcon;

    private Context cameraContext;

    private static InitialCamera ourInstance;

    public InitialCamera(Context cntx){
        cameraContext = cntx;
    }

    public static InitialCamera init(Context context) {
        if(ourInstance == null);
            ourInstance = new InitialCamera(context);
        return ourInstance;
    }

    public static InitialCamera getInstance(){
        return ourInstance;
    }

    public Context getCameraContext() { return this.cameraContext; }

    public int getFrontCameraIcon() {
        return frontCameraIcon;
    }

    public void setFrontCameraIcon(int frontCameraIcon) {
        this.frontCameraIcon = frontCameraIcon != 0 ? frontCameraIcon : R.drawable.ic_camera_front;
    }

    public int getRearCameraIcon() {
        return rearCameraIcon;
    }

    public void setRearCameraIcon(int rearCameraIcon) {
        this.rearCameraIcon = rearCameraIcon != 0 ? rearCameraIcon : R.drawable.ic_camera_rear;
    }

    public int getCaptureIcon() {
        return captureIcon;
    }

    public void setCaptureIcon(int captureIcon) {
        this.captureIcon = captureIcon != 0 ? captureIcon : R.drawable.ic_shutter;
    }

    public int getRetakeIcon() {
        return retakeIcon;
    }

    public void setRetakeIcon(int retakeIcon) {
        this.retakeIcon = retakeIcon != 0 ? retakeIcon : R.drawable.ic_retake;
    }

    public int getFlashOnIcon() {
        return flashOnIcon;
    }

    public void setFlashOnIcon(int flashOnIcon) {
        this.flashOnIcon = flashOnIcon != 0 ? flashOnIcon : R.drawable.ic_flash_on;
    }

    public int getFlashOffIcon() {
        return flashOffIcon;
    }

    public void setFlashOffIcon(int flashOffIcon) {
        this.flashOffIcon = flashOffIcon != 0 ? flashOffIcon : R.drawable.ic_flash_off;
    }

    public int getFlashAutoIcon() {
        return flashAutoIcon;
    }

    public void setFlashAutoIcon(int flashAutoIcon) {
        this.flashAutoIcon = flashAutoIcon != 0 ? flashAutoIcon : R.drawable.ic_flash_auto;
    }
}
