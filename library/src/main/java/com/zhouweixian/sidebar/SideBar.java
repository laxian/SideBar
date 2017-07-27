package com.zhouweixian.sidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 方微信联系人索引，通过触摸以及滑动选择索引，当前索引位置(choice)在屏幕中间显示
 * Created by zhouweixian on 2017/7/26
 */
public class SideBar extends View {

    private char[] arr;
    private static String DEFAULT_INDEX_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 触摸模式，触摸事件未结束
     */
    private boolean touchMode;

    private Paint paint;

    /**
     * 每个index占用的平均高度
     */
    private float avgIndexH;

    /**
     * index 字体大小
     */
    private float indexFontSize = 30;

    /**
     * index字体颜色
     */
    private int indexFontColor = Color.GRAY;

    /**
     * 触摸时index字体颜色
     */
    private int indexFontColorTouch = Color.WHITE;

    /**
     * index背景色
     */
    private int indexBgColor = Color.TRANSPARENT;

    /**
     * 触摸时index背景色
     */
    private int indexBgColorTouch = Color.parseColor("#ccb2b2b2");

    /**
     * 默认可触摸宽度
     */
    private float indexTouchWidth = (int) (indexFontSize * 1.5);

    /**
     * 选中的index 字体大小
     */
    private float choiceFontSize = 100;

    /**
     * 当前choice字体色
     */
    private int choiceFontColor = Color.WHITE;

    /**
     * choice背景色
     */
    private int choiceBgColor = Color.parseColor("#66000000");

    /**
     * choice背景宽度
     */
    private float choiceBgWidth = 200;

    /**
     * choice背景高度
     */
    private float choiceBgHeight = 200;

    /**
     * choice背景圆角半径
     */
    private float choiceBgCorner = 10;

    /**
     * 回调通知当前触摸到的index位置，提示UI做出响应，例如，列表滚动到指定位置
     */
    private OnCurrentIndexCallback callback;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SideBar);
        indexFontSize = a.getFloat(R.styleable.SideBar_index_font_size, indexFontSize);
        indexFontColor = a.getInt(R.styleable.SideBar_index_font_color, indexFontColor);
        indexFontColorTouch = a.getInt(R.styleable.SideBar_index_font_color_touch, indexFontColorTouch);
        indexBgColor = a.getInt(R.styleable.SideBar_index_background_color, indexBgColor);
        indexBgColorTouch = a.getInt(R.styleable.SideBar_index_background_color_touch, indexBgColorTouch);
        indexTouchWidth = a.getFloat(R.styleable.SideBar_index_touch_width, indexTouchWidth);
        choiceFontSize = a.getFloat(R.styleable.SideBar_choice_font_size, choiceFontSize);
        choiceFontColor = a.getInt(R.styleable.SideBar_choice_font_color, choiceFontColor);
        choiceBgColor = a.getInt(R.styleable.SideBar_choice_background_color, choiceBgColor);
        choiceBgWidth = a.getFloat(R.styleable.SideBar_choice_background_width, choiceBgWidth);
        choiceBgHeight = a.getFloat(R.styleable.SideBar_choice_background_height, choiceBgHeight);
        choiceBgCorner = a.getFloat(R.styleable.SideBar_index_font_size, choiceBgCorner);
        String str = a.getString(R.styleable.SideBar_index_set);

        if (TextUtils.isEmpty(str)) {
            str = DEFAULT_INDEX_SET;
        }
        arr = str.toCharArray();

        a.recycle();
        init();
    }

    private void init() {
        setClickable(true);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        avgIndexH = getIndexAvgHeight();

        Log.d("sidebar", "------>onLayout");
    }

    private float getIndexAvgHeight() {
        // 有效内容区高度
        int contentH = getHeight() - getPaddingTop() - getPaddingBottom();
        // 计算出每个index的高度
        return contentH * 1.0f / arr.length;
    }

    /**
     * 触摸点的X坐标
     */
    float touchX;

    /**
     * 触摸点的Y坐标
     */
    float touchY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("sidebar", "ACTION_DOWN");
                touchX = event.getX();
                if (touchX < getWidth() - indexFontSize) {
                    return false;
                }
                touchMode = true;
                touchY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("sidebar", "ACTION_MOVE");
                touchX = event.getX();
                touchY = event.getY();
                // 处理touchY 上下越界
                if (touchY < 0 || touchY >= getHeight()) {
                    touchY = Math.min(Math.max(0, touchY), getHeight() - 1);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.d("sidebar", "ACTION_UP");
                touchMode = false;
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("sidebar", "ACTION_POINTER_UP");
                if (event.getPointerCount() == 0) {
                    touchMode = false;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("sidebar", "ACTION_CANCEL");
                touchMode = false;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画右侧索引
        drawIndex(canvas);
        // 画选中项
        drawChoice(canvas, touchY);
    }

    private void drawIndex(Canvas canvas) {
        int from = getPaddingTop();

        paint.setColor(indexFontColor);
        paint.setTextSize(indexFontSize);
        paint.setTextAlign(Paint.Align.CENTER);

        // 触摸的时候显示灰色背景，松手时取消
        if (touchMode) {
            paint.setColor(indexBgColorTouch);
            canvas.drawRect(getWidth() - indexTouchWidth, 0, getWidth(), getHeight(), paint);
            paint.setColor(indexFontColorTouch);
        } else {
            if (indexBgColor != Color.TRANSPARENT) {
                paint.setColor(indexBgColor);
                canvas.drawRect(getWidth() - indexTouchWidth, 0, getWidth(), getHeight(), paint);
            }
        }

        // 绘制索引，文字居中对齐
        for (int i = 0; i < arr.length; i++) {
            canvas.drawText(arr[i] + "", getWidth() - indexTouchWidth / 2,
                    (float) (from + (i + 0.75) * avgIndexH), paint);
        }
    }

    private void drawChoice(Canvas canvas, float touchY) {

        if (touchMode) {
            // 绘制背景，200x200圆角正方形
            paint.setColor(choiceBgColor);
            canvas.drawRoundRect(new RectF(getWidth() / 2 - choiceBgWidth / 2,
                            getHeight() / 2 - choiceBgWidth / 2,
                            getWidth() / 2 + choiceBgHeight / 2,
                            getHeight() / 2 + choiceBgHeight / 2),
                    choiceBgCorner, choiceBgCorner, paint);

            // 计算出当前索引位置
            int index = (int) ((touchY - getPaddingTop()) / avgIndexH);
            paint.setTextSize(choiceFontSize);
            paint.setColor(choiceFontColor);
            paint.setTextAlign(Paint.Align.CENTER);

            /*
             绘制文字，水平居中，垂直居中，至于（getHeight() / 2 + choiceFontSize / 3）是什么道理，不知道，试验出来的。
             文字绘制默认是以左下角为原点。水平可以通过Align.CENTER居中，
             但垂直居中似乎不好控制，getHeight() / 2 + choiceFontSize / 3，最为接近
              */
            canvas.drawText(arr[index % arr.length] + "", getWidth() / 2,
                    getHeight() / 2 + choiceFontSize / 3, paint);
            if (callback != null) {
                callback.onCurrentIndex(index, arr[index]);
            }
        }
    }

    public void setCurrentIndexCallback(OnCurrentIndexCallback callback) {
        this.callback = callback;
    }
}