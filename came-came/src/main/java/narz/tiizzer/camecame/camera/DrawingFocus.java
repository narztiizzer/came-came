package narz.tiizzer.camecame.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import narz.tiizzer.camecame.InitialCamera;
import narz.tiizzer.camecame.helper.ScreenHelper;

/**
 * Created by narztiizzer on 12/28/15 AD.
 */
public class DrawingFocus extends View {
    private Paint drawingPaint;

    private boolean isCenter;
    private int focusState;
    private Rect touchArea;

    public DrawingFocus(Context context) {
        super(context);

        drawingPaint = new Paint();
        drawingPaint.setColor(Color.GREEN);
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(2);
    }

    public void setHaveTouch(int focusState, Rect touchArea){
        setHaveTouch(focusState , touchArea , true);
    }

    public void setHaveTouch(int focusState, Rect touchArea , boolean isCenter){
        this.focusState = focusState;
        this.touchArea = touchArea;
        this.isCenter = isCenter;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub

        if(touchArea != null) {
            if(!isCenter) {
                switch (focusState) {
                    case InitialCamera.TOUCH_FOCUS_FAIL:
                        drawingPaint.setColor(Color.RED);
                        canvas.drawLine((ScreenHelper.getScreenWidth() / 2) - 40, (ScreenHelper.getScreenHeight() / 2), (ScreenHelper.getScreenWidth() / 2) + 40, (ScreenHelper.getScreenHeight() / 2), drawingPaint);
                        canvas.drawLine((ScreenHelper.getScreenWidth() / 2), (ScreenHelper.getScreenHeight() / 2) - 40, (ScreenHelper.getScreenWidth() / 2), (ScreenHelper.getScreenHeight() / 2) + 40, drawingPaint);
                        break;
                    case InitialCamera.TOUCH_FOCUS_SUCCESS:
                        drawingPaint.setColor(Color.GREEN);
                        canvas.drawLine((ScreenHelper.getScreenWidth() / 2) - 40, (ScreenHelper.getScreenHeight() / 2), (ScreenHelper.getScreenWidth() / 2) + 40, (ScreenHelper.getScreenHeight() / 2), drawingPaint);
                        canvas.drawLine((ScreenHelper.getScreenWidth() / 2), (ScreenHelper.getScreenHeight() / 2) - 40, (ScreenHelper.getScreenWidth() / 2), (ScreenHelper.getScreenHeight() / 2) + 40, drawingPaint);
                        break;
                    case InitialCamera.TOUCH_FOCUS_STATE:
                        drawingPaint.setColor(Color.BLUE);
                        break;
                    default:
                        canvas.drawLine((ScreenHelper.getScreenWidth() / 2) - 40, (ScreenHelper.getScreenHeight() / 2), (ScreenHelper.getScreenWidth() / 2) + 40, (ScreenHelper.getScreenHeight() / 2), drawingPaint);
                        canvas.drawLine((ScreenHelper.getScreenWidth() / 2), (ScreenHelper.getScreenHeight() / 2) - 40, (ScreenHelper.getScreenWidth() / 2), (ScreenHelper.getScreenHeight() / 2) + 40, drawingPaint);
                        Log.d("TAG", "Create focus");
                }
            } else {
                switch (this.focusState) {
                    case InitialCamera.TOUCH_FOCUS_FAIL:
                        this.drawingPaint.setColor(Color.RED);
                        canvas.drawLine(
                                            this.touchArea.centerX() - 40,
                                            this.touchArea.centerY(),
                                            this.touchArea.centerX()  + 40,
                                            this.touchArea.centerY() ,
                                            this.drawingPaint
                                       );
                        canvas.drawLine(
                                            this.touchArea.centerX(),
                                            this.touchArea.centerY() - 40,
                                            this.touchArea.centerX(),
                                            this.touchArea.centerY() + 40,
                                            this.drawingPaint
                                       );
                        break;
                    case InitialCamera.TOUCH_FOCUS_SUCCESS:
                        this.drawingPaint.setColor(Color.GREEN);
                        canvas.drawLine(
                                this.touchArea.centerX() - 40,
                                this.touchArea.centerY(),
                                this.touchArea.centerX()  + 40,
                                this.touchArea.centerY() ,
                                this.drawingPaint
                        );
                        canvas.drawLine(
                                this.touchArea.centerX(),
                                this.touchArea.centerY() - 40,
                                this.touchArea.centerX(),
                                this.touchArea.centerY() + 40,
                                this.drawingPaint
                        );
                        break;
                    case InitialCamera.TOUCH_FOCUS_STATE:
                        drawingPaint.setColor(Color.BLUE);
                        break;
                    default:
                        canvas.drawLine(
                                this.touchArea.centerX() - 40,
                                this.touchArea.centerY(),
                                this.touchArea.centerX()  + 40,
                                this.touchArea.centerY() ,
                                this.drawingPaint
                        );
                        canvas.drawLine(
                                this.touchArea.centerX(),
                                this.touchArea.centerY() - 40,
                                this.touchArea.centerX(),
                                this.touchArea.centerY() + 40,
                                this.drawingPaint
                        );
                }

                Log.d("TAG", "Create focus");
            }
        }
    }}
