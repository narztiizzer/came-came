package narz.tiizzer.camecame.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public abstract class BaseCaptureFragment extends Fragment implements BaseCaptureInterface {

    public static final int PERMISSION_RC = 69;
    private boolean mRequestingPermission;
    private boolean isHasFrontCamera , isHasRearCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(!checkCameraFromInstanceState(savedInstanceState)) {
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    this.isHasFrontCamera = true;
                } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    this.isHasRearCamera = true;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(InitialCamera.FRONT_CAMERA , isHasFrontCamera);
        outState.putBoolean(InitialCamera.REAR_CAMERA , isHasRearCamera);
    }

    @SuppressWarnings("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View layoutView = inflater.inflate(R.layout.camera_activity, null);

        if (!CameraUtil.hasCamera(getActivity())) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Camera error")
                    .setMessage("Device not support")
                    .setPositiveButton(android.R.string.ok, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            getActivity().finish();
                        }
                    }).show();
            getActivity().finish();
        }

        checkPermissions();

        return layoutView;
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            showInitialRecorder();
            return;
        }

        final boolean videoGranted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        final boolean audioGranted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (videoGranted) {
            showInitialRecorder();
        } else {
            String[] perms;
            if (audioGranted) perms = new String[]{Manifest.permission.CAMERA};
            else perms = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
            ActivityCompat.requestPermissions(getActivity(), perms, PERMISSION_RC);
            mRequestingPermission = true;
        }
    }

    @Override
    public boolean toggleCameraPosition() {
        if (getCurrentCameraPosition() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // Front, go to back if possible
            if (this.isHasRearCamera) {
                setCameraPosition(Camera.CameraInfo.CAMERA_FACING_BACK);
                return true;
            } else return false;
        } else {
            // Back, go to front if possible
            if (this.isHasFrontCamera) {
                setCameraPosition(Camera.CameraInfo.CAMERA_FACING_FRONT);
                return true;
            } else return false;
        }
    }

    private void showInitialRecorder() {
        BaseCameraFragment cameraFragment = CameraPreviewFragment.newInstance();
        cameraFragment.setCaptureCallback(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, cameraFragment)
                .commit();
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_RC) showInitialRecorder();
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestingPermission = false;
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission denied")
                    .setMessage("Not has permission to use camera")
                    .setPositiveButton(android.R.string.ok , null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            getActivity().finish();
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


    private boolean checkCameraFromInstanceState(Bundle bundle) {
        if(bundle == null) return false;
        this.isHasFrontCamera = bundle.getBoolean(InitialCamera.FRONT_CAMERA , false);
        this.isHasRearCamera = bundle.getBoolean(InitialCamera.REAR_CAMERA , false);
        return this.isHasFrontCamera || this.isHasRearCamera;
    }
}
