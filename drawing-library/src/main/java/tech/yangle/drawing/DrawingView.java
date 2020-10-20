package tech.yangle.drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import tech.yangle.drawing.pen.BasePen;
import tech.yangle.drawing.pen.Eraser;
import tech.yangle.drawing.pen.StandardPen;
import tech.yangle.drawing.pen.TranslucentPen;

/**
 * 画板
 * <p>
 * Created by yangle on 2020/10/15.
 * Website：http://www.yangle.tech
 */
public class DrawingView extends View {

    private BasePen mPaint;
    private BasePen mTriPaint;
    private Path mPath;
    private Path mTriPath;
    private float mLastX;
    private float mTriLastX;
    private float mLastY;
    private float mTriLastY;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;
    // 是否为三方绘制
    private boolean mIsTriDraw;
    // 是否可以绘制
    private boolean mIsCanDraw;
    // 屏幕触摸监听
    private OnTouchListener mTouchListener;

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化
     *
     * @param width  画布宽度
     * @param height 画布高度
     */
    public void init(int width, int height) {
        // 不使用硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 双缓存机制
        mBufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        mBufferCanvas = new Canvas(mBufferBitmap);
        mBufferCanvas.drawColor(Color.TRANSPARENT);

        // 默认画笔
        mPaint = new StandardPen(getContext());
        // 当外部调用onTouchEvent方法，传入一些坐标信息，进行绘制时，使用此画笔
        mTriPaint = new StandardPen(getContext());
        // 笔迹路径
        mPath = new Path();
        // 外部调用，笔迹路径
        mTriPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
        // ACTION_MOVE时，将笔迹临时绘制在画布上
        if (mPath != null) {
            canvas.drawPath(mIsTriDraw ? mTriPath : mPath, mIsTriDraw ? mTriPaint : mPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPath == null) {
            return true;
        }
        mIsTriDraw = event.getMetaState() == 100;
        if (!mIsTriDraw) {
            if (!mIsCanDraw) {
                return true;
            }
            if (mTouchListener != null) {
                mTouchListener.onTouch(event);
            }
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsTriDraw) {
                    mTriLastX = x;
                    mTriLastY = y;
                    mTriPath.moveTo(x, y);
                } else {
                    mLastX = x;
                    mLastY = y;
                    mPath.moveTo(x, y);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsTriDraw) {
                    mTriPath.quadTo(mTriLastX, mTriLastY, (x + mTriLastX) / 2, (y + mTriLastY) / 2);
                    mTriLastX = x;
                    mTriLastY = y;
                } else {
                    mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                    mLastX = x;
                    mLastY = y;
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // ACTION_UP时，将当前一笔的笔迹，绘制到缓存画布上
                if (mIsTriDraw) {
                    mBufferCanvas.drawPath(mTriPath, mTriPaint);
                    mTriPath.reset();
                } else {
                    mBufferCanvas.drawPath(mPath, mPaint);
                    mPath.reset();
                }
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 设置画笔类型
     *
     * @param penType {@link PenType}
     */
    public void setPenType(int penType) {
        switch (penType) {
            case PenType.ERASER: // 橡皮擦
                mPaint = new Eraser(getContext());
                break;

            case PenType.STANDARD_PEN: // 标准笔
            default:
                mPaint = new StandardPen(getContext());
                break;

            case PenType.TRANSLUCENT_PEN: // 透明笔
                mPaint = new TranslucentPen(getContext());
                break;
        }
    }

    /**
     * 设置三方画笔类型
     *
     * @param penType {@link PenType}
     */
    public void setTriPenType(int penType) {
        switch (penType) {
            case PenType.ERASER: // 橡皮擦
                mTriPaint = new Eraser(getContext());
                break;

            case PenType.STANDARD_PEN: // 标准笔
            default:
                mTriPaint = new StandardPen(getContext());
                break;

            case PenType.TRANSLUCENT_PEN: // 透明笔
                mTriPaint = new TranslucentPen(getContext());
                break;
        }
    }

    /**
     * 设置画笔宽度
     *
     * @param penWidth 画笔宽度|px
     */
    public void setPenWidth(int penWidth) {
        if (mPaint != null) {
            mPaint.setStrokeWidth(penWidth);
        }
    }

    /**
     * 设置三方画笔宽度
     *
     * @param penWidth 画笔宽度|px
     */
    public void setTriPenWidth(int penWidth) {
        if (mTriPaint != null) {
            mTriPaint.setStrokeWidth(penWidth);
        }
    }

    /**
     * 设置画笔颜色
     *
     * @param penColor 画笔颜色
     */
    public void setPenColor(int penColor) {
        if (mPaint != null) {
            mPaint.setColor(penColor);
        }
    }

    /**
     * 设置三方画笔颜色
     *
     * @param penColor 画笔颜色
     */
    public void setTriPenColor(int penColor) {
        if (mTriPaint != null) {
            mTriPaint.setColor(penColor);
        }
    }

    /**
     * 设置画笔透明度
     *
     * @param penAlpha 画笔透明度，0..255
     */
    public void setPenAlpha(int penAlpha) {
        if (mPaint != null) {
            mPaint.setAlpha(penAlpha);
        }
    }

    /**
     * 设置三方画笔透明度
     *
     * @param penAlpha 画笔透明度，0..255
     */
    public void setTriPenAlpha(int penAlpha) {
        if (mTriPaint != null) {
            mTriPaint.setAlpha(penAlpha);
        }
    }

    /**
     * 设置绘制模式
     *
     * @param isCanDraw true: 绘制模式 false: 非绘制模式
     */
    public void setIsCanDraw(boolean isCanDraw) {
        this.mIsCanDraw = isCanDraw;
    }

    /**
     * 清除画布
     */
    public void clear() {
        if (mBufferBitmap == null) {
            return;
        }
        mBufferBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    /**
     * 获取画布Bitmap数据
     *
     * @return 画布Bitmap数据
     */
    public Bitmap getBitmap() {
        return mBufferBitmap;
    }

    /**
     * 释放资源
     */
    public void release() {
        destroyDrawingCache();
        if (mBufferBitmap != null) {
            mBufferBitmap.recycle();
            mBufferBitmap = null;
        }
    }

    /**
     * 设置屏幕触摸监听
     *
     * @param onTouchListener OnTouchListener
     */
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.mTouchListener = onTouchListener;
    }

    /**
     * 屏幕触摸监听
     */
    public interface OnTouchListener {
        /**
         * 回调方法
         *
         * @param motionEvent MotionEvent
         */
        void onTouch(MotionEvent motionEvent);
    }
}
