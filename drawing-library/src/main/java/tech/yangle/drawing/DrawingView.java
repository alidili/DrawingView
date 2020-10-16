package tech.yangle.drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    private Path mPath;
    private float mLastX;
    private float mLastY;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int width, int height) {
        // 不使用硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 双缓存机制
        mBufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        mBufferCanvas = new Canvas(mBufferBitmap);

        // 默认画笔
        mPaint = new StandardPen(getContext());
        // 笔迹路径
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
        // ACTION_MOVE时，将笔迹临时绘制在画布上
        canvas.drawPath(mPath, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                mLastX = x;
                mLastY = y;
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // ACTION_UP时，将当前一笔的笔迹，绘制到缓存画布上
                mBufferCanvas.drawPath(mPath, mPaint);
                mPath.reset();
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
     * 设置画笔颜色
     *
     * @param penColor 画笔颜色
     */
    public void setPenColor(int penColor) {
        if (mPaint != null) {
            mPaint.setColor(penColor);
        }
    }
}
