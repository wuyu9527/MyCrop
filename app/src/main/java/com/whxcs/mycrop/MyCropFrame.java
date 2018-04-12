package com.whxcs.mycrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static com.whxcs.mycrop.MainActivity.whx;

/**
 * Created by whx on 2018/4/12.
 */
public class MyCropFrame extends View {

    public MyCropFrame(Context context) {
        super(context);
        //初始化画笔
        initPaint();
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint); //4.0以上关闭硬件加速，否则虚线不显示
    }

    public MyCropFrame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint); //4.0以上关闭硬件加速，否则虚线不显示
    }
    
    private Paint mPaint; //定义画笔
    private Path path;// 创建Path

    private void initPaint() {
        path = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true); //设置抗锯齿的效果
        mPaint.setStyle(Paint.Style.STROKE); //设置画笔样式为描边
        mPaint.setStrokeWidth(3);  //设置笔刷的粗细度
        mPaint.setColor(Color.BLACK); //设置画笔的颜色
        mPaint.setStyle(Paint.Style.FILL);                   // 设置画布模式为填充
        //path.setFillType(Path.FillType.EVEN_ODD);                   // 设置Path填充模式为 奇偶规则
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);            // 反奇偶规则

        path.addRect(-200, -200, 200, 200, Path.Direction.CW);

    }

    float mViewWidth;
    float mViewHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(whx,mViewWidth+":"+mViewHeight);
        canvas.translate(mViewWidth / 2, mViewHeight / 2);          // 移动画布(坐标系)
        mPaint.setAlpha(150);
        canvas.drawPath(path, mPaint);
    }
}
