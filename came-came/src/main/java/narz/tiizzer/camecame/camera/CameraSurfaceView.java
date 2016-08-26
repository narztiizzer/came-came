package narz.tiizzer.camecame.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView {

	private static final double ASPECT_RATIO = 0.75;

	public CameraSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub

		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);

//		final boolean isPortrait =
//				getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
//
//		if (isPortrait) {
//			if (width > height * ASPECT_RATIO) {
//				width = (int) (height * ASPECT_RATIO + 0.5);
//			} else {
//				height = (int) (width / ASPECT_RATIO + 0.5);
//			}
//		} else {
//			if (height > width * ASPECT_RATIO) {
//				height = (int) (width * ASPECT_RATIO + 0.5);
//			} else {
//				width = (int) (height / ASPECT_RATIO + 0.5);
//			}
//		}

		setMeasuredDimension(width , height);
	}
}
