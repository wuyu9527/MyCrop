package com.whxcs.mycrop;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;


import com.facebook.drawee.view.SimpleDraweeView;

import static com.whxcs.mycrop.MainActivity.whx;


/**
 * Created by whx on 2017/2/22.
 */

public class ZoomableDraweeView extends SimpleDraweeView {
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;

    private float mCurrentScale = 1f;
    private Matrix mCurrentMatrix;
    private float mMidX;
    private float mMidY;
    private OnClickListener mClickListener;


    public ZoomableDraweeView(Context context) {
        super(context);
        init();
    }

    public ZoomableDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomableDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCurrentMatrix = new Matrix();

        ScaleGestureDetector.OnScaleGestureListener scaleListener = new ScaleGestureDetector
                .SimpleOnScaleGestureListener() {

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.i(whx, "onScaleBegin");
                return super.onScaleBegin(detector);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                mCurrentScale *= scaleFactor;
                if (mMidX == 0f) {
                    mMidX = getWidth() / 2f;
                }
                if (mMidY == 0f) {
                    mMidY = getHeight() / 2f;
                }

                if (mode != DRAG) {
                    if (oldNewRotation != newRotation) {
                        float numRotate = rotation - postRotation;
                        mCurrentMatrix.postRotate(numRotate, mMidX, mMidY);
                        postRotation = rotation;
                        oldNewRotation = newRotation;
                    }


                    mCurrentMatrix.postScale(scaleFactor, scaleFactor, mMidX, mMidY);

                    invalidate();
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);

                if (mCurrentScale < 1f) {
                    reset();
                }
                //checkBorder();
            }
        };
        mScaleDetector = new ScaleGestureDetector(getContext(), scaleListener);

        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mClickListener != null) {
                    mClickListener.onClick();
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mCurrentScale > 1f) {

                    mCurrentMatrix.postTranslate(-distanceX, -distanceY);
                    invalidate();
                    //checkBorder();
                }
                return true;
            }
        };
        mGestureDetector = new GestureDetector(getContext(), gestureListener);
    }

    /**
     * 检查图片边界是否移到view以内
     * 目的是让图片边缘不要移动到view里面
     */
    private void checkBorder() {
        RectF rectF = getDisplayRect(mCurrentMatrix);
        boolean reset = false;
        float dx = 0;
        float dy = 0;

        if (rectF.left > 0) {
            dx = getLeft() - rectF.left;
            reset = true;
        }
        if (rectF.top > 0) {
            dy = getTop() - rectF.top;
            reset = true;
        }
        if (rectF.right < getRight()) {
            dx = getRight() - rectF.right;
            reset = true;
        }
        if (rectF.bottom < getHeight()) {
            dy = getHeight() - rectF.bottom;
            reset = true;
        }
        if (reset) {

            mCurrentMatrix.postTranslate(dx, dy);
            invalidate();
        }
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        RectF rectF = new RectF(getLeft(), getTop(), getRight(), getBottom());
        matrix.mapRect(rectF);
        return rectF;
    }

    @Override
    public void setImageURI(Uri uri) {
        reset();
        super.setImageURI(uri);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        reset();
        super.setImageBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.concat(mCurrentMatrix);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode = NONE;
    float x_down = 0;
    float y_down = 0;
    float oldDist = 1f;
    float oldRotation = 0;
    float postRotation = 0;
    float newRotation = 0;
    float oldNewRotation = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX(0);
                y_down = event.getY(0);
                //savedMatrix.set(matrix); //保存
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldRotation = rotation(event);
                // savedMatrix.set(matrix);//保存
                //midPoint(event);//取中心点
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    rotation = rotation(event) - oldRotation;
                    if (-1 < rotation && rotation < 0) {
                        rotation = 0;
                    } else if (0 < rotation && rotation < 1) {
                        rotation = 0;
                    }
                    newRotation += rotation;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                postRotation = 0;
                mode = NONE;
                break;
        }
        mScaleDetector.onTouchEvent(event);
        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float CenterX = 0;
    private float CenterY = 0;
    private float rotation = 0;

    // 取手势中心点
    private void midPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        CenterX = x / 2;
        CenterY = y / 2;
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x;
        double delta_y;
        if (event.getX(0) > event.getX(1)) {
            delta_x = (event.getX(0) - event.getX(1));
            delta_y = (event.getY(0) - event.getY(1));
        } else {
            delta_x = (event.getX(1) - event.getX(0));
            delta_y = (event.getY(1) - event.getY(0));
        }

        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 初始大小
     */
    private void reset() {
        mCurrentMatrix.reset();
        mCurrentScale = 1f;
        newRotation = 0;
        postRotation = 0;
        invalidate();
    }

    public interface OnClickListener {
        void onClick();
    }

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }


    public void setImageRotation(float rotation) {
        if (mMidX == 0f) {
            mMidX = getWidth() / 2f;
        }
        if (mMidY == 0f) {
            mMidY = getHeight() / 2f;
        }
        newRotation += rotation;
        mCurrentMatrix.preRotate(rotation, mMidX, mMidY);
        invalidate();
    }

}
