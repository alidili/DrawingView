package tech.yangle.drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
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

    private static final String TAG = "DrawingView";
    private BasePen mPaint;
    private BasePen mTriPaint;
    private Path mPath;
    private Path mTriPath;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;
    // 是否为三方绘制
    private boolean mIsTriDraw;
    // 是否可以绘制
    private boolean mIsCanDraw;
    // 屏幕触摸监听
    private OnTouchListener mTouchListener;
    // 初始时的缩放比例
    private float mScale = 1;
    private final PointF mOffset = new PointF(0, 0);
    private PointF mCurrentPoint;
    private PointPath mCurrentPath;
    // 画笔当前宽度
    private float mCurrentWidth;
    // 要涂鸦的图片
    private Bitmap mBgBitmap;
    private float dsPicWidth, dsPicHeight, bitmapFactor, dy, dx;

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*
     * 初始化
     *
     * @param width  画布宽度
     * @param height 画布高度
     */
    public void init(int width, int height) {
        // 不使用硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 双缓存机制
        mBufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
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
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        Log.i(TAG, "[onSizeChanged] w: " + w + " h: " + h);
        init(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // ACTION_MOVE时，将笔迹临时绘制在画布上
        if (mCurrentPath != null) {
            mCurrentPath.disPlayPath(getContext(), mBufferCanvas);
        }

        if (mBgBitmap != null) {
            contralPicBorder();
            @SuppressLint("DrawAllocation")
            Matrix matrix = new Matrix();
            // 将图片设置到 DrawingView 中
            matrix.postScale(bitmapFactor, bitmapFactor);
            // 将图片平移到屏幕中心
            matrix.postTranslate(dx, dy);
            // 使用矩阵绘制位图
            canvas.drawBitmap(mBgBitmap, matrix, null);
        }

        // 将前面画的位图显示出来
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
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
        mCurrentPoint = new PointF((x - mOffset.x) / mScale, (y - mOffset.y) / mScale);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "[onTouchEvent]: finger down");
                if (mIsTriDraw) {
                    mCurrentPath = PointPath.getInstance(mCurrentPoint, mTriPaint);
                    mCurrentPath.setCurrentPathType(mTriPaint.mPenType);
                    mCurrentPath.setCurrentWidth(mCurrentWidth);
                    mCurrentPath.setCurrentColor(mTriPaint.getColor());
                } else {
                    mCurrentPath = PointPath.getInstance(mCurrentPoint, mPaint);
                    mCurrentPath.setCurrentPathType(mPaint.mPenType);
                    mCurrentPath.setCurrentWidth(mCurrentWidth);
                    mCurrentPath.setCurrentColor(mPaint.getColor());
                }
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "[onTouchEvent]: finger move");
                if (mCurrentPath == null) break;
                mCurrentPath.savePointToPath(mCurrentPoint);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "[onTouchEvent]: finger up");
                // ACTION_UP时，将当前一笔的笔迹，绘制到缓存画布上
                if (mIsTriDraw) {
                    mBufferCanvas.drawPath(mTriPath, mTriPaint);
                    mTriPath.reset();
                } else {
                    mBufferCanvas.drawPath(mPath, mPaint);
                    mPath.reset();
                }
                if (mCurrentPath != null) {
                    // 将一条完整的一条路径保存下来
                    mCurrentPath.savePointToPath(mCurrentPoint);
                }
                // 重新置空
                mCurrentPath = null;
                invalidate();
                break;

        }
        return true;
    }

    /**
     * 在画布上的控制位图边界
     */
    private void contralPicBorder() {
        // 拿到图片的宽和高
        float width = (float) mBgBitmap.getWidth();
        float height = (float) mBgBitmap.getHeight();
        if (width > height) {
            dsPicWidth = getWidth() - 2;
            dsPicHeight = height / width * dsPicWidth;
            bitmapFactor = dsPicHeight / dsPicWidth;
        } else {
            dsPicHeight = getHeight() - 2;
            dsPicWidth = width / height * dsPicHeight;
            bitmapFactor = dsPicWidth / dsPicHeight;
        }
        dy = (getHeight() - (height * bitmapFactor)) / 2;
        dx = (getWidth() - (width * bitmapFactor)) / 2;
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
        mCurrentWidth = penWidth;
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
        mCurrentWidth = penWidth;
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

    /**
     * 放大画板后，对pointPath坐标进行缩放平移坐标点 绘画
     *
     * @param scaleX        缩放值
     * @param mMatrixValue1 x轴 坐标点偏移量
     * @param mMatrixValue2 y轴 坐标点偏移量
     */
    public void setScaleAndOffset(float scaleX, float mMatrixValue1, float mMatrixValue2) {
        mScale = scaleX;
        mCurrentWidth = PointPath.NORMAL_LINE_WIDTH / mScale;
        mOffset.x = mMatrixValue1;
        mOffset.y = mMatrixValue2;
    }

    /**
     * 设置图片涂鸦
     *
     * @param bitmap 位图对象
     */
    public void setBackgroundPic(Bitmap bitmap) {
        mBgBitmap = bitmap;
        invalidate();
    }

}
