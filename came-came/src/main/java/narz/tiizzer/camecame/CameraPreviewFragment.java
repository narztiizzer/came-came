package narz.tiizzer.camecame;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import narz.tiizzer.camecame.base.BaseCameraFragment;
import narz.tiizzer.camecame.camera.DrawingFocus;
import narz.tiizzer.camecame.helper.ScreenHelper;
import narz.tiizzer.camecame.interfaces.BaseCaptureInterface;
import narz.tiizzer.camecame.camera.CameraPreview;
import narz.tiizzer.camecame.util.CameraUtil;
import narz.tiizzer.camecame.util.CompareSizesByArea;

/**
 * Created by narztiizzer on 8/19/2016 AD.
 */

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CameraPreviewFragment extends BaseCameraFragment implements View.OnTouchListener , Camera.AutoFocusCallback {
    CameraPreview mPreviewView;
    View mPreviewFrame;

    private Camera.Size mVideoSize;
    private Camera mCamera;
    private Point mWindowSize;
    private int mDisplayOrientation;
    private boolean mIsAutoFocusing;
    private List<Camera.Size> collectionSize;
    private boolean isTouchFocusActivate = true;
    private DrawingFocus drawingFocusView;
    private Rect touchFocusPosition;

    public static CameraPreviewFragment newInstance() {
        CameraPreviewFragment fragment = new CameraPreviewFragment();
        fragment.setRetainInstance(true);
        fragment.setArguments(new Bundle());
        return fragment;
    }

    private static Camera.Size chooseVideoSize(BaseCaptureInterface ci, List<Camera.Size> choices) {
        Camera.Size backupSize = null;
        Camera.Size selectedSize = null;
        for (Camera.Size size : choices) {
            if (size.height <= ci.videoPreferredHeight()) {

                if (size.width == size.height * ci.videoPreferredAspect()) {
                    if(selectedSize == null || size.width > selectedSize.width)
                        selectedSize = size;
                }

                if (size.height >= ci.videoPreferredHeight())
                    backupSize = size;
            }
        }
        if (selectedSize == null) return backupSize;
        return selectedSize;
    }

    private Camera.Size chooseOptimalSize(List<Camera.Size> choices, Camera.Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        collectionSize = new ArrayList<>();
        int w = aspectRatio.width;
        int h = aspectRatio.height;

        double videoRatio = (double) h / w;

        for (Camera.Size option : choices) {
            double optionRatio = (double) option.height / option.width;
            if(optionRatio == videoRatio) {
                collectionSize.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (collectionSize.size() > 0) {
            return Collections.max(collectionSize, new CompareSizesByArea());
        } else {
            return aspectRatio;
        }
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreviewFrame = view.findViewById(R.id.rootFrame);
        mPreviewFrame.setOnTouchListener(this);
        setFocusView();
        super.getBaseCaptureInterface().setCameraPosition(InitialCamera.CAMERA_POSITION_BACK);
    }

    @Override
    public void openCamera() {
        final Activity activity = getActivity();
        if (null == activity || activity.isFinishing()) return;
        try {

            final int mBackCameraId = super.getBaseCaptureInterface().getBackCamera() != null ? (Integer) getBaseCaptureInterface().getBackCamera() : -1;
            final int mFrontCameraId = super.getBaseCaptureInterface().getFrontCamera() != null ? (Integer) getBaseCaptureInterface().getFrontCamera() : -1;
            if (mBackCameraId == -1 || mFrontCameraId == -1) {
                int numberOfCameras = Camera.getNumberOfCameras();
                if (numberOfCameras == 0) {
                    throwError(new Exception("No cameras are available on this device."));
                    return;
                }

                for (int i = 0; i < numberOfCameras; i++) {
                    //noinspection ConstantConditions
                    if (mFrontCameraId != -1 && mBackCameraId != -1) break;
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, info);
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && mFrontCameraId == -1) {
                        super.getBaseCaptureInterface().setFrontCamera(i);
                    } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK && mBackCameraId == -1) {
                        super.getBaseCaptureInterface().setBackCamera(i);
                    }
                }
            }

            if (mWindowSize == null)
                mWindowSize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(mWindowSize);
            final int toOpen = getCurrentCameraId();

            int switchDrawableId = super.getBaseCaptureInterface().getCurrentCameraPosition() == InitialCamera.CAMERA_POSITION_BACK ? InitialCamera.getInstance().getFrontCameraIcon() : InitialCamera.getInstance().getRearCameraIcon();
            @SuppressWarnings("ResourceType")
            Drawable switchDrawable = InitialCamera.getInstance().getCameraContext().getResources().getDrawable(switchDrawableId);
            super.getBaseControlView().getSwitchCameraControlView().setBackground(switchDrawable);

            mCamera = Camera.open(toOpen == -1 ? 0 : toOpen);
            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> videoSizes = parameters.getSupportedVideoSizes();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

            if (videoSizes == null || videoSizes.size() == 0)
                videoSizes = parameters.getSupportedPreviewSizes();
            mVideoSize = chooseVideoSize(super.getBaseCaptureInterface() , videoSizes);
            Camera.Size previewSize = chooseOptimalSize(previewSizes , mVideoSize);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                parameters.setRecordingHint(true);
            setCameraDisplayOrientation(parameters);

            if(parameters.getSupportedFlashModes() != null && parameters.getSupportedFlashModes().contains(super.getBaseCaptureInterface().getCurrentFlashMode()))
                parameters.setFlashMode(super.getBaseCaptureInterface().getCurrentFlashMode());

            drawingFocusView.setVisibility(getCurrentCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT || !super.getBaseCaptureInterface().isShowFocusPoint() ? View.GONE : View.VISIBLE);
            mCamera.setParameters(parameters);
            createPreview();
        } catch (IllegalStateException e) {
            throwError(new Exception("Cannot access the camera.", e));
        } catch (RuntimeException e2) {
            throwError(new Exception("Cannot access the camera, you may need to restart your device.", e2));
        }
    }

    @Override
    public void closeCamera() {
        if(mCamera != null) {
            this.mPreviewView.getHolder().getSurface().release();
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    @Override
    public void onPressCapture() {
        mCamera.takePicture(null , null , this);
    }

    @Override
    public void onPressFlash() {

        String flashMode = super.getBaseCaptureInterface().getCurrentFlashMode();
        Camera.Parameters parameters = mCamera.getParameters();
        if(parameters.getSupportedFlashModes() != null && parameters.getSupportedFlashModes().contains(super.getBaseCaptureInterface().getCurrentFlashMode())) {
            switch (flashMode) {
                case Camera.Parameters.FLASH_MODE_AUTO : super.getBaseCaptureInterface().setCurrentFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    super.getBaseControlView().getFlashControlView().setBackground(InitialCamera.getInstance().getCameraContext().getResources().getDrawable(R.drawable.ic_flash_off));
                    break;
                case Camera.Parameters.FLASH_MODE_ON  : super.getBaseCaptureInterface().setCurrentFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    super.getBaseControlView().getFlashControlView().setBackground(InitialCamera.getInstance().getCameraContext().getResources().getDrawable(R.drawable.ic_flash_auto));
                    break;
                case Camera.Parameters.FLASH_MODE_OFF : super.getBaseCaptureInterface().setCurrentFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    super.getBaseControlView().getFlashControlView().setBackground(InitialCamera.getInstance().getCameraContext().getResources().getDrawable(R.drawable.ic_flash_on));
                    break;
                default: Log.d("" , "");
            }
            parameters.setFlashMode(super.getBaseCaptureInterface().getCurrentFlashMode());
            mCamera.setParameters(parameters);
        }

    }

    @Override
    public void onPause() {
        if (mCamera != null)
            closeCamera();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mPreviewView.getHolder().getSurface().release();
        } catch (Throwable ignored) { }
        mPreviewFrame = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        openCamera();
    }


    @Override
    public void onStop() {
        super.onStop();
        closeCamera();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view.getId() == R.id.rootFrame) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = (event.getX());
                float y = (event.getY());
                final float touchMajor = event.getTouchMajor();
                float touchMinor = event.getTouchMinor();

                drawingFocusView.setVisibility(View.GONE);
                touchFocusPosition = new Rect(
                        (int) (x - touchMajor / 2),
                        (int) (y - touchMinor / 2),
                        (int) (x + touchMajor / 2),
                        (int) (y + touchMinor / 2));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        touchFocus(touchFocusPosition);
                    }
                });
            }
        }
        return true;
    }

    private void setFocusView() {
        drawingFocusView = new DrawingFocus(getActivity());
        ViewGroup.LayoutParams layoutParamsDrawing = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT , ViewGroup.LayoutParams.FILL_PARENT);
        getActivity().addContentView(drawingFocusView, layoutParamsDrawing);
    }

    public void touchFocus(final Rect tFocusRect){
        final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
        Camera.Area focusArea = new Camera.Area(tFocusRect, 1000);
        focusList.add(focusArea);

        try {
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusAreas(focusList);
            params.setMeteringAreas(focusList);
            mCamera.setParameters(params);
        } catch (Exception e) {
            Log.d("CameraFragment" , "Camera set params fail!");
        }

        mCamera.autoFocus(this);
    }

    @SuppressWarnings("WrongConstant")
    private void setCameraDisplayOrientation(Camera.Parameters parameters) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(getCurrentCameraId(), info);
        final int deviceOrientation = ScreenHelper.getDisplayRotationDegree(getActivity());
        mDisplayOrientation = ScreenHelper.getDisplayOrientation(
                info.orientation, deviceOrientation, info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        Log.d("CameraFragment", String.format("Orientations: Sensor = %d˚, Device = %d˚, Display = %d˚",
                info.orientation, deviceOrientation, mDisplayOrientation));

        int previewOrientation;
        if (CameraUtil.isArcWelder()) {
            previewOrientation = 0;
        } else {
            previewOrientation = mDisplayOrientation;
            if (ScreenHelper.isPortrait(deviceOrientation) && getCurrentCameraPosition() == InitialCamera.CAMERA_POSITION_FRONT)
                previewOrientation = ScreenHelper.mirror(mDisplayOrientation);
        }
        parameters.setRotation(previewOrientation);
        mCamera.setDisplayOrientation(previewOrientation);
    }

    private void createPreview() {
        Activity activity = getActivity();
        if (activity == null) return;
        if (mWindowSize == null)
            mWindowSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(mWindowSize);
        mPreviewView = new CameraPreview(getActivity(), mCamera);

        FrameLayout viewFrame = (FrameLayout) mPreviewFrame;

        if (viewFrame.getChildCount() > 0 && (viewFrame.getChildAt(0) instanceof CameraPreview))
            viewFrame.removeViewAt(0);
        viewFrame.addView(mPreviewView, 0);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (data.length > 0) {
            Bitmap srcBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap croppedBitmap = null;
            boolean isPortrait = ScreenHelper.isPortrait(getActivity());

            if(super.getBaseCaptureInterface().isUseRectangularMode()) {
                if(isPortrait)
                    croppedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth() , srcBitmap.getWidth());
                else
                    croppedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getHeight() , srcBitmap.getHeight());

                super.getBaseCaptureInterface().onCapture(croppedBitmap , camera);
            } else {
                super.getBaseCaptureInterface().onCapture(srcBitmap , camera);
            }

            Log.d("CREATE BItMAP" , "Complete");
        } else {
            Log.d("CREATE BITMAP" , "Fail");
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if(camera != null) {
            if (success) {
                this.mCamera.cancelAutoFocus();
                this.drawingFocusView.setHaveTouch(InitialCamera.TOUCH_FOCUS_SUCCESS, touchFocusPosition);
                this.drawingFocusView.invalidate();
            } else {
                this.drawingFocusView.setHaveTouch(InitialCamera.TOUCH_FOCUS_FAIL, touchFocusPosition);
                this.drawingFocusView.invalidate();
            }

            this.drawingFocusView.setVisibility(super.getCurrentCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT || !super.getBaseCaptureInterface().isShowFocusPoint() ? View.GONE : View.VISIBLE);
        }
    }
}
