package narz.tiizzer.camecame;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import narz.tiizzer.camecame.base.BaseCameraFragment;
import narz.tiizzer.camecame.base.BaseControlView;
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
public class CameraPreviewFragment extends BaseCameraFragment implements View.OnTouchListener , Camera.AutoFocusCallback , SurfaceHolder.Callback{
    private CameraPreview mPreviewView;
    private View mPreviewFrame;
    private BaseControlView mBaseControlView;
    private BaseCaptureInterface mBaseCaptureInterface;

    private Camera.Size mVideoSize;
    private Camera mCamera;
    private int mDisplayOrientation;
    private List<Camera.Size> collectionSize;
    private DrawingFocus drawingFocusView;
    private Rect touchFocusPosition;
    private CameraPreview mCameraPreview;
    private SurfaceHolder mCameraSurfaceHolder;
    private boolean hasAutoFocus;


    public static CameraPreviewFragment newInstance() {
        CameraPreviewFragment fragment = new CameraPreviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBaseCaptureInterface = super.getBaseCaptureInterface();
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
        mCameraPreview = (CameraPreview) view.findViewById(R.id.camera_preview);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraSurfaceHolder = mCameraPreview.getHolder();
                mCameraSurfaceHolder.addCallback(CameraPreviewFragment.this);
                mCameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

                mPreviewFrame = view.findViewById(R.id.rootFrame);
                mPreviewFrame.setOnTouchListener(CameraPreviewFragment.this);
                setFocusView();
            }
        });

    }

    @Override
    public void openCamera() {
        final int toOpen = getCurrentCameraPosition();
        openCameraPosition(toOpen);
    }

    @Override
    public void closeCamera() {
        if(this.mCamera != null) {
            this.mCameraSurfaceHolder.getSurface().release();
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    @Override
    public void onPressCapture() {
        this.enableClickControlButton(false);
        this.mCamera.takePicture(null , null , this);
    }

    @Override
    public void onPressFlash() {

        String flashMode = super.getBaseCaptureInterface().getCurrentFlashMode();
        String newFlashState = null;
        Camera.Parameters parameters = this.mCamera.getParameters();
        if(parameters.getSupportedFlashModes() != null && parameters.getSupportedFlashModes().contains(super.getBaseCaptureInterface().getCurrentFlashMode())) {
            switch (flashMode) {
                case Camera.Parameters.FLASH_MODE_AUTO : super.getBaseCaptureInterface().setCurrentFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    newFlashState = Camera.Parameters.FLASH_MODE_OFF;
                    break;
                case Camera.Parameters.FLASH_MODE_ON  : super.getBaseCaptureInterface().setCurrentFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    newFlashState = Camera.Parameters.FLASH_MODE_AUTO;
                    break;
                case Camera.Parameters.FLASH_MODE_OFF : super.getBaseCaptureInterface().setCurrentFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    newFlashState = Camera.Parameters.FLASH_MODE_ON;
                    break;
                default: Log.d("Flash state" , "State not found");
            }
            parameters.setFlashMode(this.mBaseCaptureInterface.getCurrentFlashMode());
            this.mCamera.setParameters(parameters);
        }
        this.mBaseControlView.onFlashStateChanged(newFlashState , (newFlashState != null ? "Flash state changed success" : "Flash state changed fail"));
    }

    @Override
    public void onPressSwitch() {
        this.enableClickControlButton(false);

        this.mCamera.stopPreview();
        //NB: if you don't release the current camera before switching, you app will crash
        this.mCamera.release();

        int toOpen = getCurrentCameraPosition();
        openCameraPosition(toOpen);
    }

    @Override
    public void onPause() {
        if (this.mCamera != null)
            closeCamera();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCameraSurfaceHolder.removeCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if(this.hasAutoFocus)
            this.mCamera.autoFocus(this);
    }

    @SuppressWarnings("WrongConstant")
    private void setCameraDisplayOrientation(Camera.Parameters parameters) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(getCurrentCameraPosition(), info);
        final int deviceOrientation = ScreenHelper.getDisplayRotationDegree(getActivity());
        this.mDisplayOrientation = ScreenHelper.getDisplayOrientation(
                info.orientation, deviceOrientation, info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        Log.d("CameraFragment", String.format("Orientations: Sensor = %d˚, Device = %d˚, Display = %d˚",
                info.orientation, deviceOrientation, this.mDisplayOrientation));

        int previewOrientation;
        if (CameraUtil.isArcWelder()) {
            previewOrientation = 0;
        } else {
            previewOrientation = this.mDisplayOrientation;
            if (ScreenHelper.isPortrait(deviceOrientation) && getCurrentCameraPosition() == Camera.CameraInfo.CAMERA_FACING_FRONT)
                previewOrientation = ScreenHelper.mirror(this.mDisplayOrientation);
        }
        parameters.setRotation(previewOrientation);
        this.mCamera.setDisplayOrientation(previewOrientation);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (data.length > 0) {
            Bitmap srcBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            if(mBaseCaptureInterface.getCurrentCameraPosition() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Matrix matrix = new Matrix();
                matrix.preScale(1.0f, -1.0f);
                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
            }

            Bitmap croppedBitmap = null;
            boolean isPortrait = ScreenHelper.isPortrait(getActivity());

            if(super.getBaseCaptureInterface().isCropSquareImage()) {
                if(isPortrait)
                    croppedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth() , srcBitmap.getWidth());
                else
                    croppedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getHeight() , srcBitmap.getHeight());

                super.getBaseControlView().onCapture(croppedBitmap , camera);
            } else {
                super.getBaseControlView().onCapture(srcBitmap , camera);
            }

            this.enableClickControlButton(true);
            Log.d("CREATE BITMAP" , "Complete");
        } else {
            Log.d("CREATE BITMAP" , "Fail");
        }
    }

    @Override
    public void initControlView() {
        this.mBaseControlView = super.getBaseControlView();
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

            this.drawingFocusView.setVisibility(super.getCurrentCameraPosition() == Camera.CameraInfo.CAMERA_FACING_FRONT || !super.getBaseCaptureInterface().isShowFocusPoint() ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (surfaceHolder.getSurface() == null)
            return;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceHolder.removeCallback(this);
    }

    private void openCameraPosition(final int toOpen) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCamera = Camera.open(toOpen);

                Camera.Parameters parameters = mCamera.getParameters();

                List<Camera.Size> videoSizes = parameters.getSupportedVideoSizes();
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

                if (videoSizes == null || videoSizes.size() == 0)
                    videoSizes = parameters.getSupportedPreviewSizes();
                mVideoSize = chooseVideoSize(mBaseCaptureInterface, videoSizes);
                Camera.Size previewSize = chooseOptimalSize(previewSizes, mVideoSize);
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    parameters.setRecordingHint(true);
                setCameraDisplayOrientation(parameters);

                if (parameters.getSupportedFlashModes() != null && parameters.getSupportedFlashModes().contains(mBaseCaptureInterface.getCurrentFlashMode()))
                    parameters.setFlashMode(mBaseCaptureInterface.getCurrentFlashMode());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawingFocusView.setVisibility(getCurrentCameraPosition() == Camera.CameraInfo.CAMERA_FACING_FRONT || !mBaseCaptureInterface.isShowFocusPoint() ? View.GONE : View.VISIBLE);
                    }
                });

                List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
                hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(parameters);

                try {
                    mCamera.setPreviewDisplay(mCameraSurfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();

                mBaseControlView.onSwitchedCamera(toOpen , "Open " + (toOpen == Camera.CameraInfo.CAMERA_FACING_FRONT ? "front" : "rear") + " camera success");
                enableClickControlButton(true);
            }
        });

    }

    private void enableClickControlButton(boolean isEnable) {
        this.mBaseControlView.getCaptureControlView().setClickable(isEnable);
        this.mBaseControlView.getFlashControlView().setClickable(isEnable);
        this.mBaseControlView.getSwitchCameraControlView().setClickable(isEnable);
    }
}
