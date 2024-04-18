package com.xxc.drawview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xxc.drawview.bean.LinePath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @ClassName DrawView
 * @Description
 * @Author xxc
 * @Date 2024/4/18 23:06
 * @Version 1.0
 */
public class DrawView extends View {

    // 创建画笔
    private Paint mPaint = new Paint();
    // 画笔默认值
    private int mDefaultColor = Color.BLACK;
    private float mDefaultSize = 10;
    // 创建路径 Path
    private Path mPath;
    // 创建一个列表保存绘制的路径信息
    private List<LinePath> mPathList = new ArrayList<>();

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        setBackgroundColor(Color.WHITE);
        mPaint.setColor(mDefaultColor);
        mPaint.setStrokeWidth(mDefaultSize);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // 绘制保存的内容
        if (!mPathList.isEmpty()) {
            mPathList.forEach(new Consumer<LinePath>() {
                @Override
                public void accept(LinePath linePath) {
                    mPaint.setColor(linePath.getColor());
                    mPaint.setStrokeWidth(linePath.getSize());
                    canvas.drawPath(linePath.getPath(), mPaint);
                }
            });
        }
        // 绘制内容
        if (mPath != null) {
            mPaint.setColor(mDefaultColor);
            mPaint.setStrokeWidth(mDefaultSize);
            canvas.drawPath(mPath, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath = new Path();
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // 保存绘制的路径
                mPathList.add(new LinePath(mPath, mDefaultColor, mDefaultSize));
                mPath = null;
                break;
        }
        // 消费了事件
        return true;
    }

    /***************************************外部交互，设置属性***********************************************/

    /**
     * 设置画笔的粗细
     * @param size
     */
    public void setPaintSize(float size) {
        mDefaultSize = size;
    }

    /**
     * 设置画笔颜色
     * @param color
     */
    public void setPaintColor(String color) {
        mDefaultColor = Color.parseColor(color);
    }

    /**
     * 设置橡皮擦
     */
    public void setEar() {
        mDefaultColor = Color.WHITE;
    }

    public void undo() {
        if (!mPathList.isEmpty()) {
            mPathList.remove(mPathList.size() - 1);
            invalidate();
        }
    }
}
