package com.msile.view.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 简单触摸图片放大、位移、旋转
 * Created by msilemsile on 16/8/11.
 */
public class PictureDemoView extends View {

    private Bitmap mPicture;
    private Matrix matrix;
    private int mTouchX, mTouchY;
    private int mTouchMode;
    private double mLastDistance;
    private double mLastRotate;
    private static final int SINGLE_TOUCH_MODE = 1;
    private static final int MUL_TOUCH_MODE = 2;

    public PictureDemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PictureDemoView(Context context) {
        this(context, null);
    }

    private void init() {
        matrix = new Matrix();
    }

    public void setPicture(Bitmap mPicture) {
        this.mPicture = mPicture;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPicture == null) {
            return;
        }
        canvas.drawBitmap(mPicture, matrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                mTouchMode = SINGLE_TOUCH_MODE;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = (int) event.getX();
                float moveY = (int) event.getY();
                if (mTouchMode == SINGLE_TOUCH_MODE) {
                    //位移
                    int distanceX = (int) (moveX - mTouchX);
                    int distanceY = (int) (moveY - mTouchY);
                    matrix.postTranslate(distanceX, distanceY);
                    postInvalidate();
                    mTouchX = (int) moveX;
                    mTouchY = (int) moveY;
                } else if (mTouchMode == MUL_TOUCH_MODE) {
                    //中心点
                    float centerX = getPointCenterX(event);
                    float centerY = getPointCenterY(event);
                    //旋转
                    double currentRotate = getRotation(event);
                    matrix.postRotate((float) (currentRotate - mLastRotate), centerX, centerY);

                    double currentDistance = getDistance(event);
                    if (mLastDistance > 0) {
                        //缩放
                        float scale = (float) (currentDistance / mLastDistance);
                        if (Math.abs(scale) > 0) {
                            matrix.postScale(scale, scale, centerX, centerY);
                            postInvalidate();
                        }
                    }
                    mLastDistance = currentDistance;
                    mLastRotate = currentRotate;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getActionIndex() > 1) {
                    break;
                }
                mTouchMode = MUL_TOUCH_MODE;
                mLastDistance = getDistance(event);
                mLastRotate = getRotation(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = 0;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchX = 0;
                mTouchY = 0;
                break;
        }
        return true;
    }

    /**
     * 两点间距离
     */
    private double getDistance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 两点角度
     */
    private double getRotation(MotionEvent event) {
        float delta_x = (event.getX(0) - event.getX(1));
        float delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return Math.toDegrees(radians);
    }

    /**
     * 两点X中心点
     */
    private float getPointCenterX(MotionEvent event) {
        return Math.abs((event.getX(1) + event.getX(0)) / 2);
    }

    /**
     * 两点Y中心点
     */
    private float getPointCenterY(MotionEvent event) {
        return Math.abs((event.getY(1) + event.getY(0)) / 2);
    }

}
