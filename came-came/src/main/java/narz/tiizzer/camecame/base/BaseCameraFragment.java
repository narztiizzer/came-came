package narz.tiizzer.camecame.base;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import narz.tiizzer.camecame.InitialCamera;
import narz.tiizzer.camecame.R;
import narz.tiizzer.camecame.helper.ScreenHelper;
import narz.tiizzer.camecame.interfaces.BaseCaptureInterface;
import narz.tiizzer.camecame.interfaces.BaseControlViewInterface;

/**
 * Created by narztiizzer on 8/19/2016 AD.
 */
@SuppressWarnings("deprecation")
public abstract class BaseCameraFragment extends Fragment implements Camera.PictureCallback , BaseControlViewInterface {
    private FrameLayout rootFrame;
    private LinearLayout controlFrame;

    private BaseCaptureInterface mInterface;
    private BaseControlView mControlView;

    public abstract void openCamera();
    public abstract void closeCamera();
    public abstract void onPressCapture();
    public abstract void onPressFlash();

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camera_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controlFrame = (LinearLayout) view.findViewById(R.id.controlsFrame);
        rootFrame = (FrameLayout) view.findViewById(R.id.rootFrame);
        controlFrame.setBackgroundColor(Color.parseColor(mInterface.setFrameControlBackground()));

        mControlView = mInterface.setControlView();
        if(mControlView != null) {

            mControlView.setControlInterface(this);

            InitialCamera.getInstance().setCaptureIcon(mControlView.setCaptureIcon());
            InitialCamera.getInstance().setRetakeIcon(mControlView.setRetakeIcon());
            InitialCamera.getInstance().setFrontCameraIcon(mControlView.setFrontCameraIcon());
            InitialCamera.getInstance().setRearCameraIcon(mControlView.setRearCameraIcon());
            InitialCamera.getInstance().setFlashAutoIcon(mControlView.setFlashAutoIcon());
            InitialCamera.getInstance().setFlashOnIcon(mControlView.setFlashOnIcon());
            InitialCamera.getInstance().setFlashOffIcon(mControlView.setFlashOffIcon());

            getChildFragmentManager().beginTransaction().add(R.id.controlsFrame, mControlView , "CONTROL-VIEW").commit();
        }

        if(mInterface.isUseRectangularMode()) { setRectangularPreview(); }
    }

    protected void setRectangularPreview() {
        ViewTreeObserver observer = rootFrame.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                FrameLayout.LayoutParams params = getControlLayoutParams();
                setControlLayoutParams(params);
                rootFrame.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    protected FrameLayout.LayoutParams getControlLayoutParams() {

        int screenWidth = ScreenHelper.getScreenWidth();
        int screenHeight = ScreenHelper.getScreenHeight();
        int rootFrameWidth = rootFrame.getWidth();
        int rootFrameHeight = rootFrame.getHeight();

        FrameLayout.LayoutParams resultParams = null;
        if(ScreenHelper.isPortrait(getActivity()))
            resultParams = new FrameLayout.LayoutParams(screenWidth , rootFrameHeight - screenWidth);
        else
            resultParams = new FrameLayout.LayoutParams(rootFrameWidth - screenHeight , screenHeight);

        return resultParams;
    }

    protected void setControlLayoutParams(FrameLayout.LayoutParams params) {
        if(ScreenHelper.isPortrait(getActivity())) {
            params.gravity = Gravity.BOTTOM;
        } else {
            params.gravity = Gravity.RIGHT;
        }

        controlFrame.setLayoutParams(params);
        controlFrame.setOrientation(ScreenHelper.isPortrait(getActivity()) ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BaseCaptureInterface)
            mInterface = (BaseCaptureInterface) activity;
    }

    @Override
    public final void onDetach() {
        super.onDetach();
    }

    public final int getCurrentCameraPosition() {
        if (mInterface == null) return InitialCamera.CAMERA_POSITION_UNKNOWN;
        return mInterface.getCurrentCameraPosition();
    }

    public final int getCurrentCameraId() {
        if (mInterface.getCurrentCameraPosition() == InitialCamera.CAMERA_POSITION_BACK)
            return (Integer) mInterface.getBackCamera();
        else return (Integer) mInterface.getFrontCamera();
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onControlViewCreated() {
        if(mControlView.getSwitchCameraControlView() != null) {
            int switchDrawableId = mInterface.getCurrentCameraPosition() == InitialCamera.CAMERA_POSITION_BACK ? InitialCamera.getInstance().getFrontCameraIcon() : InitialCamera.getInstance().getRearCameraIcon();
            Drawable switchDrawable = InitialCamera.getInstance().getCameraContext().getResources().getDrawable(switchDrawableId);
            mControlView.getSwitchCameraControlView().setBackground(switchDrawable);
            mControlView.getSwitchCameraControlView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInterface.toggleCameraPosition();
                    closeCamera();
                    openCamera();
                }
            });
        }

        if(mControlView.getFlashControlView() != null) {
            int flashDrawableId = 0;
            switch(mInterface.getCurrentFlashMode()) {
                case Camera.Parameters.FLASH_MODE_AUTO : flashDrawableId = InitialCamera.getInstance().getFlashAutoIcon();
                    break;
                case Camera.Parameters.FLASH_MODE_ON : flashDrawableId = InitialCamera.getInstance().getFlashOnIcon();
                    break;
                case Camera.Parameters.FLASH_MODE_OFF : flashDrawableId = InitialCamera.getInstance().getFlashOffIcon();
                    break;
                default: Log.d("FLASH ICON" , "MODE NOT MATCH");
            }

            Drawable switchDrawable = InitialCamera.getInstance().getCameraContext().getResources().getDrawable(flashDrawableId);
            mControlView.getFlashControlView().setBackground(switchDrawable);
            mControlView.getFlashControlView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPressFlash();
                }
            });
        }

        if(mControlView.getCaptureControlView() != null) {
            mControlView.getCaptureControlView().setBackground(InitialCamera.getInstance().getCameraContext().getResources().getDrawable(R.drawable.ic_shutter));
            mControlView.getCaptureControlView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPressCapture();
                }
            });
        }

        Log.d("Control" , "Created");
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected final void throwError(Exception e) {
        Activity act = getActivity();
        if (act != null) {
            act.finish();
        }
    }

    protected BaseCaptureInterface getBaseCaptureInterface() {
        return this.mInterface;
    }

    public void setCaptureCallback(BaseCaptureInterface interfaceCallback) {
        mInterface = interfaceCallback;
    }

    protected BaseControlView getBaseControlView() { return this.mControlView; }
}
