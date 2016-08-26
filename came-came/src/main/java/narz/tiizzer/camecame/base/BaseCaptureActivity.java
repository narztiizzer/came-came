package narz.tiizzer.camecame.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import narz.tiizzer.camecame.CameraPreviewFragment;
import narz.tiizzer.camecame.InitialCamera;
import narz.tiizzer.camecame.R;
import narz.tiizzer.camecame.interfaces.BaseCaptureInterface;
import narz.tiizzer.camecame.util.CameraUtil;

/**
 * Created by narztiizzer on 8/19/2016 AD.
 */
public abstract class BaseCaptureActivity extends AppCompatActivity implements BaseCaptureInterface {
    private int mCameraPosition = InitialCamera.CAMERA_POSITION_UNKNOWN;
    private boolean mRequestingPermission;
    private Object mFrontCameraId;
    private Object mBackCameraId;

    public static final int PERMISSION_RC = 69;

    private String currentFlashMode = Camera.Parameters.FLASH_MODE_AUTO;

    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("camera_position", mCameraPosition);
        outState.putBoolean("requesting_permission", mRequestingPermission);
        if (mFrontCameraId instanceof String) {
            outState.putString("front_camera_id_str", (String) mFrontCameraId);
            outState.putString("back_camera_id_str", (String) mBackCameraId);
        } else {
            if (mFrontCameraId != null)
                outState.putInt("front_camera_id_int", (Integer) mFrontCameraId);
            if (mBackCameraId != null)
                outState.putInt("back_camera_id_int", (Integer) mBackCameraId);
        }
    }

    @SuppressWarnings("ResourceAsColor")
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CameraUtil.hasCamera(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Camera error")
                    .setMessage("Device not support")
                    .setPositiveButton(android.R.string.ok , null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    }).show();
            return;
        }
        setContentView(R.layout.camera_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.setStatusBarColor(setStatusBarColor() != 0 ? setStatusBarColor() : R.color.colorPrimaryDark);
            window.setNavigationBarColor(setNavigationBarColor()!= 0 ? setNavigationBarColor() : R.color.colorPrimaryDark);
        }

        if (null == savedInstanceState) {
            checkPermissions();
        } else {
            mCameraPosition = savedInstanceState.getInt("camera_position", -1);
            mRequestingPermission = savedInstanceState.getBoolean("requesting_permission", false);
            if (savedInstanceState.containsKey("front_camera_id_str")) {
                mFrontCameraId = savedInstanceState.getString("front_camera_id_str");
                mBackCameraId = savedInstanceState.getString("back_camera_id_str");
            } else {
                mFrontCameraId = savedInstanceState.getInt("front_camera_id_int");
                mBackCameraId = savedInstanceState.getInt("back_camera_id_int");
            }
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            showInitialRecorder();
            return;
        }

        final boolean videoGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        final boolean audioGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (videoGranted) {
            showInitialRecorder();
        } else {
            String[] perms;
            if (audioGranted) perms = new String[]{Manifest.permission.CAMERA};
            else perms = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
            ActivityCompat.requestPermissions(this, perms, PERMISSION_RC);
            mRequestingPermission = true;
        }
    }

    @Override
    protected final void onPause() {
        super.onPause();
        if (!isFinishing() && !isChangingConfigurations() && !mRequestingPermission)
            finish();
    }

    @Override
    public final void onBackPressed() {
        finish();
    }

    @Override
    public void setCameraPosition(int position) {
        mCameraPosition = position;
    }

    @Override
    public void toggleCameraPosition() {
        if (getCurrentCameraPosition() == InitialCamera.CAMERA_POSITION_FRONT) {
            // Front, go to back if possible
            if (getBackCamera() != null)
                setCameraPosition(InitialCamera.CAMERA_POSITION_BACK);
        } else {
            // Back, go to front if possible
            if (getFrontCamera() != null)
                setCameraPosition(InitialCamera.CAMERA_POSITION_FRONT);
        }
    }

    @Override
    public int getCurrentCameraPosition() {
        return mCameraPosition;
    }

    @Override
    public Object getCurrentCameraId() {
        if (getCurrentCameraPosition() == InitialCamera.CAMERA_POSITION_FRONT)
            return getFrontCamera();
        else return getBackCamera();
    }

    @Override
    public void setFrontCamera(Object id) {
        mFrontCameraId = id;
    }

    @Override
    public Object getFrontCamera() {
        return mFrontCameraId;
    }

    @Override
    public void setBackCamera(Object id) {
        mBackCameraId = id;
    }

    @Override
    public Object getBackCamera() {
        return mBackCameraId;
    }

    private void showInitialRecorder() {
        BaseCameraFragment cameraFragment = CameraPreviewFragment.newInstance();
        cameraFragment.setCaptureCallback(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, cameraFragment)
                .commit();
    }

    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_RC) showInitialRecorder();
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestingPermission = false;
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission denied")
                    .setMessage("Not has permission to use camera")
                    .setPositiveButton(android.R.string.ok , null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    }).show();
        } else {
            showInitialRecorder();
        }
    }

    @Override
    public float videoPreferredAspect() {
        return 4f / 3f;
    }

    @Override
    public int videoPreferredHeight() {
        return 1080;
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
